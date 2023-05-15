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
        usageStore.usage(start, end, account).map{usage =>
          val compute = usage.filter(_.usageType == "compute").map(_.units).sum
          val storage = usage.filter(_.usageType == "storage").map(_.units).sum
          val bandwidth = usage.filter(_.usageType == "bandwidth").map(_.units).sum
          Report(acct.id, start, end, acct.currency, acct.tier, compute * 0.008, storage * 0.016, bandwidth * 0.021)
        }
      case None => Failure(NoSuchElementException(s"Account $account does not exist"))
  }
}
