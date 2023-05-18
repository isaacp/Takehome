package controllers

import entities.{Report, UsageEvent}
import gateways.{BillingAdjustments, CustomerAccounts, UsageArchive, UsageStore}

import java.time.*
import java.util.NoSuchElementException
import scala.util.{Failure, Success, Try}

case class BuildUsageReportController(
  usageStore: UsageStore,
  usageArchive: UsageArchive,
  customerAccounts: CustomerAccounts,
  billingAdjustments: BillingAdjustments) {

  private val priceChart = Map(
    ("compute" -> 0.008),
    ("storage" -> 0.016),
    ("bandwidth" -> 0.021)
  )
  def execute(start: LocalDateTime, end: LocalDateTime, account: String): Try[Report] = {

    def calculate(units: Double, unitType: String): Double = {
      unitType match
        case t @ "compute" => priceChart(t) * units
        case t @ "storage" => priceChart(t) * units
        case t @ "bandwidth" => priceChart(t) * units
    }
    customerAccounts.customerAccount(account) match
      case Some(acct) =>
        for{
          usage <- usageStore.usage(start, end, acct.id)
          adjustments <- billingAdjustments.adjustments(start, end, acct.id)
          units = computeUnits(usage)
          adjustment = adjustments.map(_.amount).sum
        } yield Report(acct.id,
          start,
          end,
          acct.currency,
          acct.tier,
          calculate(units._1, "compute"),
          calculate(units._2, "storage"),
          calculate(units._3, "bandwidth"),
          adjustment
        )
      case None => Failure(NoSuchElementException(s"Account $account does not exist"))
  }

  def computeUnits(usage: List[UsageEvent]): (Double, Double, Double) = {
    (
      usage.filter(_.usageType == "compute").map(_.units).sum,
      usage.filter(_.usageType == "storage").map(_.units).sum,
      usage.filter(_.usageType == "bandwidth").map(_.units).sum
    )
  }
}
