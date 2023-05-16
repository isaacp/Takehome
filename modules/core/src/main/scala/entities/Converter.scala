package entities

import java.time._
case class Converter() {
  def convert(metric: Metric): UsageEvent = {
     UsageEvent(metric.id, metric.customer, metric.metricType, metric.value, metric.time, LocalDateTime.now)
  }
}
