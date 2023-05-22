package data

import data.ConcreteUsageArchive.h2Database
import entities.{UsageEvent, UsageType}
import gateways.UsageArchive

import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}

class ConcreteUsageArchive extends UsageArchive {

  def add(usageEvent: UsageEvent): Try[Unit] = Try {
    h2Database.command(
      s"INSERT INTO usageArchive VALUES(" +
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
    def convertResultsToUsage(result: ResultSet): List[UsageEvent] = {
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
    
    val result = h2Database.query(s"SELECT * FROM usageArchive Where (TIME_OF>'${start.format(DateTimeFormatter.ISO_DATE_TIME)}' And '${end.format(DateTimeFormatter.ISO_DATE_TIME)}'>=TIME_OF And '$account'=CUSTOMER)")
    result.map(convertResultsToUsage) match
      case success@Success(_) => success
      case failure@Failure(_) => failure
  }
}

object ConcreteUsageArchive {
  private var h2Database = H2Database()

  def apply(database: H2Database): ConcreteUsageArchive = {
    h2Database = database
    new ConcreteUsageArchive
  }
}