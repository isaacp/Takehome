package data

import data.ConcreteUsageStore.h2Database
import entities.{Metric, UsageEvent}
import gateways.UsageStore

import java.sql.{ResultSet, Timestamp}
import java.time.*
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import data.H2Database
import entities.UsageType

class ConcreteUsageStore extends UsageStore {
  override def add(usageEvent: UsageEvent): Try[Unit] = {
    h2Database.command(
      s"INSERT INTO usage VALUES(" +
        s"'${usageEvent.metricId}', " +
        s"'${usageEvent.customer}', " +
        s"'${usageEvent.usageType.toString}', " +
        s"${usageEvent.units}, " +
        s"'${usageEvent.timestamp.format(DateTimeFormatter.ISO_DATE_TIME)}', " +
        s"'${usageEvent.createdAt.format(DateTimeFormatter.ISO_DATE_TIME)}'" +
        s")"
    )
  }

  override def usage(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[UsageEvent]] = {
    val result = h2Database.query(s"SELECT * FROM usage Where (TIME_OF>'${start.format(DateTimeFormatter.ISO_DATE_TIME)}' And '${end.format(DateTimeFormatter.ISO_DATE_TIME)}'>=TIME_OF And '$account'=CUSTOMER)")
    result.map(convertResultsToUsage) match
      case success @ Success(_) => success
      case failure @ Failure(_) => failure
  }

  override def olderThan(date: LocalDateTime): Try[List[UsageEvent]] = {
    val result = h2Database.query(s"SELECT * FROM usage Where TIME_OF<'$date'")
    result.map(convertResultsToUsage) match
      case success@Success(_) => success
      case failure@Failure(_) => failure
  }

  override def delete(usageEvent: UsageEvent): Try[Unit] = {
    h2Database.command(s"DELETE FROM usage WHERE ID=${usageEvent.metricId}")
  }

  private def convertResultsToUsage(result: ResultSet): List[UsageEvent] = {
    var list = List[UsageEvent]()
    while (result.next)
      val usage = UsageEvent(
        result.getString("ID"),
        result.getString("CUSTOMER"),
        UsageType.valueOf(result.getString("USAGE_TYPE")),
        result.getDouble("UNITS"),
        result.getTimestamp("TIME_OF").toLocalDateTime,
        result.getTimestamp("CREATED").toLocalDateTime
      )
      list = usage :: list
    list
  }
}

object ConcreteUsageStore {
  private var h2Database = H2Database()
  def apply(database: H2Database): ConcreteUsageStore = {
    h2Database = database
    new ConcreteUsageStore
  }
}
