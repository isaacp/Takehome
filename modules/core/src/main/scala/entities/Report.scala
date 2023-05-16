package entities

import java.time._

final case class Report(
    customer: String,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    currency: String,
    tier: String,
    compute: Double,
    storage: Double,
    bandwidth: Double,
    adjustment: Double
)
