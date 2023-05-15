package data

import entities.UsageEvent
import gateways.UsageStore

import java.time._
import scala.util.Try

class ConcreteUsageStore extends UsageStore {
  private var collection = List[UsageEvent]()

  override def add(usageEvent: UsageEvent): Try[Unit] = Try {
    if !contains(usageEvent.metricId) then
      collection = usageEvent :: collection
  }

  override def usage(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[UsageEvent]] = Try {
    collection.filter(p =>(p.timestamp.isAfter(start) || p.timestamp.equals(start)) && (p.timestamp.isBefore(end) || p.timestamp.equals(end)))
  }

  override def contains(eventId: String): Boolean = {
    collection.exists(p => p.metricId == eventId)
  }

  override def greaterThanThreeMonthsOld(): Try[List[UsageEvent]] = Try {
    collection.filter(p => p.timestamp.isBefore(LocalDateTime.now().minusDays(90)))
  }
}

object ConcreteUsageStore {
  def apply(): ConcreteUsageStore = {
    new ConcreteUsageStore
  }
}
