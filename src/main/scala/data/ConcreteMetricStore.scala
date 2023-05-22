package data

import data.ConcreteMetricStore.h2Database
import entities.{Metric, UsageEvent, UsageType}
import gateways.MetricStore

import java.sql.{ResultSet, Timestamp}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.NoSuchElementException
import scala.util.{Failure, Success, Try}

class ConcreteMetricStore extends MetricStore {
  override def metric(id: String): Try[Option[Metric]] = {
    val result = h2Database.query(s"SELECT * FROM metrics Where ID = '$id'")
    result.map(convertResultsToMetrics) match
      case Success(metrics) =>
        if metrics.nonEmpty then
          Success(Some(metrics.head))
        else
          Try(None)
      case Failure(_) => Try(None)
  }

  override def metrics(start: LocalDateTime, end: LocalDateTime): Try[List[Metric]] = {
    val result = h2Database.query(s"SELECT * FROM metrics Where (CREATED > '${start.format(DateTimeFormatter.ISO_DATE_TIME)}' And '${end.format(DateTimeFormatter.ISO_DATE_TIME)}' >= CREATED)")
    result.map(convertResultsToMetrics) match
      case success @ Success(_) => success
      case failure @ Failure(_) => failure
  }

  override def audit(usageEvent: UsageEvent): Try[Unit] = {
    metric(usageEvent.metricId) match
      case Success(_) => Success(()) // Simple check for existence
      case Failure(e)  => Failure(e)
  }

  override def add(metric: Metric): Try[Unit] = {
    h2Database.command(s"INSERT INTO metrics VALUES('${metric.id}', '${metric.metricType.toString}', ${metric.value}, '${metric.customer}', '${metric.time.format(DateTimeFormatter.ISO_DATE_TIME)}')")
  }

  private def convertResultsToMetrics(result: ResultSet): List[Metric] = {
    var list = List[Metric]()
    while (result.next)
      val metric = Metric(
        result.getString("ID"),
        UsageType.valueOf(result.getString("USAGE_TYPE")),
        result.getInt("VAL"),
        result.getString("CUSTOMER"),
        result.getTimestamp("CREATED").toLocalDateTime
      )
      list = metric :: list
    list
  }
}

object ConcreteMetricStore {
  private var h2Database: H2Database = H2Database()
  def apply(database: H2Database): ConcreteMetricStore = {
    h2Database = database
    new ConcreteMetricStore
  }
}
