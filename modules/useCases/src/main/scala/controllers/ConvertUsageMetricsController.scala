package controllers

import entities.{Converter, Metric, UsageEvent}
import gateways.MetricStore

import java.util.Date
import scala.util.Try

case class ConvertUsageMetricsController(converter: Converter) {
  def execute(usageMetrics: List[Metric]): Try[List[UsageEvent]] = Try {
    usageMetrics.map{ metric =>
      converter.convert(metric)
    }
  }
}
