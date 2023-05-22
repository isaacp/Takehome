package entities

import java.time._

final case class UsageEvent(
    metricId: String,
    customer: String,
    usageType: UsageType,
    units: Double,
    timestamp: LocalDateTime,
    createdAt: LocalDateTime
)
