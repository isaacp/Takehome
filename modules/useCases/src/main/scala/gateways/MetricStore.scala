package gateways

import entities.{Metric, UsageEvent}

import java.time._
import scala.util.Try

trait MetricStore {
  def metrics(start: LocalDateTime, end: LocalDateTime): Try[List[Metric]]

  def metric(id: String): Try[Option[Metric]]
  
  def audit(usageEvent: UsageEvent): Try[Unit]

  def add(metric: Metric): Try[Unit]
}
