package controllers

import entities.{CustomerAccount, Report, UsageEvent, UsageType, UsageTier}
import gateways.{BillingAdjustments, CustomerAccounts, UsageArchive, UsageStore}

import java.time.*
import java.util.NoSuchElementException
import scala.util.{Failure, Success, Try}

case class BuildUsageReportController(
  usageStore: UsageStore,
  usageArchive: UsageArchive,
  customerAccounts: CustomerAccounts,
  billingAdjustments: BillingAdjustments) {

  private val prices =Map(
    UsageTier.platinum -> Map(
      UsageType.compute -> 0.008, //dollars
      UsageType.storage -> 0.016, //dollars
      UsageType.bandwidth -> 0.021 //dollars
    ),
    UsageTier.gold -> Map(
      UsageType.compute -> 0.010,
      UsageType.storage -> 0.018,
      UsageType.bandwidth -> 0.023
    ),
    UsageTier.silver -> Map(
      UsageType.compute -> 0.012,
      UsageType.storage -> 0.020,
      UsageType.bandwidth -> 0.025
    ),
    UsageTier.basic -> Map(
      UsageType.compute -> 0.014,
      UsageType.storage -> 0.022,
      UsageType.bandwidth -> 0.027
    )
  )
  def execute(start: LocalDateTime, end: LocalDateTime, account: String): Try[Report] = {

    def calculate(units: Double, tier: UsageTier, unitType: UsageType): Double = {
      val tierPrices = prices(tier)
      unitType match
        case t @ UsageType.compute => tierPrices(t) * units
        case t @ UsageType.storage => tierPrices(t) * units
        case t @ UsageType.bandwidth => tierPrices(t) * units
    }

    def sumUnits(usage: List[UsageEvent]): (Double, Double, Double) = {
      (
        usage.filter(_.usageType == UsageType.compute).map(_.units).sum,
        usage.filter(_.usageType == UsageType.storage).map(_.units).sum,
        usage.filter(_.usageType == UsageType.bandwidth).map(_.units).sum
      )
    }

    for {
      maybeAcct <- customerAccounts.customerAccount(account)
      acct = maybeAcct.getOrElse(CustomerAccount("-1", UsageTier.basic, ""))
      usages <- usageStore.usage(start, end, acct.id)
      adjustments <- billingAdjustments.adjustments(start, end, acct.id)
      units = sumUnits(usages)
      adjustment = adjustments.map(_.amount).sum
    } yield Report(
      acct.id,
      start,
      end,
      acct.currency,
      acct.tier,
      calculate(units._1, acct.tier, UsageType.compute),
      units._1,
      calculate(units._2, acct.tier, UsageType.storage),
      units._2,
      calculate(units._3, acct.tier, UsageType.bandwidth),
      units._3,
      adjustment
    )
  }
}
