package controllers

import entities.UsageEvent
import gateways.UsageStore

import scala.util.Try

case class PersistUsageEventsController(usageStore: UsageStore) {
  def execute(usageEvent: UsageEvent): Try[Unit] = Try {
    usageStore add usageEvent
  }
}
