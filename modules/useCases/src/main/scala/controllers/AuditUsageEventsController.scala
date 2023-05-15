package controllers

import entities.UsageEvent
import gateways.MetricStore

import scala.util.Try

case class AuditUsageEventsController(metricStore: MetricStore) {
  private var auditCount = 0
  def execute(usageEvent: UsageEvent): Try[Unit] = Try {
    if auditCount % 10 == 0 then
      metricStore.audit(usageEvent)
    auditCount = auditCount + 1
  }
}
