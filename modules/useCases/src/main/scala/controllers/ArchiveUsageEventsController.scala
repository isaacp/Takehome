package controllers

import entities.UsageEvent
import gateways.{UsageArchive, UsageStore}

import java.time._
import scala.util.Try

case class ArchiveUsageEventsController(usageStore: UsageStore, usageArchive: UsageArchive) {
  def execute(usageEvents: UsageEvent): Try[Unit] = {
    usageStore.greaterThanThreeMonthsOld().map {ev =>
      ev.foreach(usageArchive.add(_))
    }
  }
}
