package entities

import java.time._

case class Metric(id:String, metricType: String, value: Int, customer:String, time: LocalDateTime)
