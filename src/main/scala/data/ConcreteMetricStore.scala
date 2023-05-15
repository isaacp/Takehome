package data

import entities.{Metric, UsageEvent}
import gateways.MetricStore

import java.time.LocalDateTime
import java.util.NoSuchElementException
import scala.util.{Failure, Success, Try}

class ConcreteMetricStore extends MetricStore {
  private var collection = List[Metric]()

  override def metric(id: String): Try[Option[Metric]] = Try {
    collection.find(m => m.id == id)
  }

  override def metrics(start: LocalDateTime, end: LocalDateTime): Try[List[Metric]] = Try {
    collection.filter(p => p.time.isAfter(start) && (p.time.isBefore(end) || p.time.equals(end)))
  }

  override def audit(usageEvent: UsageEvent): Try[Unit] = Try {
    collection.find(_.id == usageEvent.metricId) match
      case Some(_) => Success(()) // Simple check for existence
      case None  => Failure(NoSuchElementException("metric does not exist"))
  }

  override def add(metric: Metric): Try[Unit] = {
    if !collection.exists(p => p.id == metric.id) then
      collection = metric :: collection
      Success(())
    else
      Failure(UnsupportedOperationException("Metric already exists"))
  }
}

object ConcreteMetricStore {
  def apply(): ConcreteMetricStore = {
    new ConcreteMetricStore
  }
}
