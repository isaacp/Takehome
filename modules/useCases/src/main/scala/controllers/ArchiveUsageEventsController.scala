package controllers

import entities.UsageEvent
import gateways.{UsageArchive, UsageStore}

import java.time._
import scala.util.Try

case class ArchiveUsageEventsController(usageStore: UsageStore, usageArchive: UsageArchive) {
  def execute(usageEvents: List[UsageEvent]): Try[Unit] = Try {
    usageEvents.foreach(usageEvent => usageArchive.add(usageEvent))
  }
}
