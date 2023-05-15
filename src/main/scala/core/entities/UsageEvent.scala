import java.util.Date

final case class UsageEvent(
    customer: String,
    usageType: String,
    units: Double,
    timestamp: Date,
    createdAt: Date
)
