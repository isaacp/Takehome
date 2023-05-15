package core.entities

import java.util.Date

final case class Report(
    customer: String,
    startDate: Date,
    endDate: Date,
    currency: String,
    tier: String,
    compute: UsageCost,
    storage: UsageCost,
    bandwidth: UsageCost
)
