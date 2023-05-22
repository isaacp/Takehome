package entities

import java.time._
case class Metric(id:String, metricType: UsageType, value: Int, customer:String, time: LocalDateTime)
