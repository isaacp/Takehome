package controllers

import entities.UsageEvent
import gateways.UsageStore

import scala.util.Try

case class PersistUsageEventsController(usageStore: UsageStore) {
  def execute(usageEvents: List[UsageEvent]): Try[Unit] = Try {
    usageEvents.map{ev =>
      if !usageStore.contains(ev.metricId) then
        usageStore.add(ev)

    }
  }
}
