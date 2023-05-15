package controllers
import entities.BillingAdjustment
import gateways.BillingAdjustments

import scala.util.Try
import java.util.Date

case class AddBillingAdjustmentController(adjustmentsStore: BillingAdjustments) {
  def execute(adjustment: BillingAdjustment): Try[Unit] = Try {
    adjustmentsStore.add(adjustment.occurrence, adjustment.account, adjustment.amount)
  }
}
