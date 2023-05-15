package entities

import java.time._

case class BillingAdjustment(amount: Double, occurrence: LocalDateTime, account: String)