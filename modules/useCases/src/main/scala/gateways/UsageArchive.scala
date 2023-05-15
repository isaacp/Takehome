package gateways

import entities.UsageEvent

import scala.util.Try

trait UsageArchive {
  def add(uEvent: UsageEvent): Try[Unit]
  def add(uEvents: List[UsageEvent]): Try[Unit]
}
