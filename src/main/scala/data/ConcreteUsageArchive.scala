package data

import entities.UsageEvent
import gateways.UsageArchive

import scala.util.Try

class ConcreteUsageArchive extends UsageArchive {
  private var collection = List[UsageEvent]()

  def add(uEvent: UsageEvent): Try[Unit] = Try {
    uEvent :: collection
  }

  def add(uEvents: List[UsageEvent]) : Try[Unit] = Try {
    collection = uEvents ::: collection
  }
}

object ConcreteUsageArchive {
  def apply(): ConcreteUsageArchive = {
    new ConcreteUsageArchive
  }
}