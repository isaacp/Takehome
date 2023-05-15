package controllers

import entities.UsageEvent
import gateways.MetricStore

import scala.util.Try

case class AuditUsageEventsController(metricStore: MetricStore) {
  def execute(usageEvent: UsageEvent): Try[Unit] = Try {
    metricStore.audit(usageEvent)
  }
}
