package controllers

import entities.Metric
import gateways.MetricStore

import java.time._
import scala.util.Try

case class GetUsageMetricsController(metricStore: MetricStore) {
  def execute(start: LocalDateTime, end: LocalDateTime): Try[List[Metric]] = Try {
    metricStore.metrics(start, end).getOrElse(List[Metric]())
  }
}
