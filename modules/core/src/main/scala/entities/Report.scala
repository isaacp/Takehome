package entities

import java.time.*
import java.time.format.DateTimeFormatter

final case class Report(
    customer: String,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    currency: String,
    tier: String,
    compute: Double,
    computeUnits: Double,
    storage: Double,
    storageUnits: Double,
    bandwidth: Double,
    bandwidthUnits: Double,
    adjustment: Double
) {
  override def toString: String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance
    s"""
      ||------------------------------------------------------------
      || Canopy, Inc            Period Ending: ${endDate.format(DateTimeFormatter.ofPattern("MMM d uuuu HH:MM"))}
      || Customer: $customer
      ||
      ||   Compute(${computeUnits.toInt} cu):
      ||   ${formatter.format(compute)}
      ||
      ||   Storage(${storageUnits.toInt} su):
      ||   ${formatter.format(storage)}
      ||
      ||   Bandwidth(${bandwidthUnits.toInt} bu):
      ||   ${formatter.format(bandwidth)}
      ||
      ||  Adjustment: ${formatter.format(adjustment)}
      ||
      ||  Total Amount: ${formatter.format(compute + storage + bandwidth + adjustment)}
      |-------------------------------------------------------------""".stripMargin
  }
}
