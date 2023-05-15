package gateways

import entities.BillingAdjustment

import java.time._
import scala.util.Try

trait BillingAdjustments {
  def adjustments(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[BillingAdjustment]]
  def add(when: LocalDateTime, account: String, amount: Double): Try[Unit]
}
