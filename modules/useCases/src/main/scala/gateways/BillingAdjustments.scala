package gateways

import entities.BillingAdjustment

import java.time._
import scala.util.Try

abstract class BillingAdjustments {
  def adjustments(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[BillingAdjustment]]
  def add(when: LocalDateTime, account: String, amount: Double): Try[Unit]
}
