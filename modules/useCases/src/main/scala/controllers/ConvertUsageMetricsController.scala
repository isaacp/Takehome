package controllers

import entities.{Converter, Metric, UsageEvent}
import gateways.MetricStore

import java.util.Date
import scala.util.Try

case class ConvertUsageMetricsController(converter: Converter) {
  def execute(metrics: List[Metric]): Try[List[UsageEvent]] = Try {
    metrics.map(converter.convert)
  }
}
