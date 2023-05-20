package controllers

import entities.UsageEvent
import gateways.UsageStore

import scala.util.Try

case class PersistUsageEventsController(usageStore: UsageStore) {
  def execute(usageEvents: List[UsageEvent]): Try[Unit] = Try {
    usageEvents.foreach(usageStore.add)
  }
}
