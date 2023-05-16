package controllers

import entities.Report
import gateways.{BillingAdjustments, CustomerAccounts, UsageArchive, UsageStore}

import java.time._
import java.util.NoSuchElementException
import scala.util.{Failure, Success, Try}

case class BuildUsageReportController(
  usageStore: UsageStore,
  usageArchive: UsageArchive,
  customerAccounts: CustomerAccounts,
  billingAdjustments: BillingAdjustments) {
  def execute(start: LocalDateTime, end: LocalDateTime, account: String): Try[Report] = {
    customerAccounts.customerAccount(account) match
      case Some(acct) =>
        for{
          usage <- usageStore.usage(start, end, acct.id)
          adjustments <- billingAdjustments.adjustments(start, end, acct.id)
          compute = usage.filter(_.usageType == "compute").map(_.units).sum
          storage = usage.filter(_.usageType == "storage").map(_.units).sum
          bandwidth = usage.filter(_.usageType == "bandwidth").map(_.units).sum
          adjustment = adjustments.map(_.amount).sum
        } yield Report(acct.id, start, end, acct.currency, acct.tier, compute * 0.008, storage * 0.016, bandwidth * 0.021, adjustment)
      case None => Failure(NoSuchElementException(s"Account $account does not exist"))
  }
}
