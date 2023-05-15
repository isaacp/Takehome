package data

import entities.{BillingAdjustment, Metric}
import gateways.BillingAdjustments

import java.time._
import scala.util.Try

class ConcreteBillingAdjustments extends BillingAdjustments {
  private var collection = List[BillingAdjustment]()

  override def add(when: LocalDateTime, account: String, amount: Double): Try[Unit] = Try {
    collection = BillingAdjustment(amount, when, account) :: collection
  }
  override def adjustments(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[BillingAdjustment]] = Try {
    collection.filter(p => p.occurrence.isAfter(start) && (p.occurrence.isBefore(end) || p.occurrence.equals(end)))
  }
}

object ConcreteBillingAdjustments {
  def apply(): BillingAdjustments = {
    new ConcreteBillingAdjustments
  }
}
