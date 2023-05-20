package data

import data.ConcreteBillingAdjustments.h2Database
import entities.{BillingAdjustment, Metric}
import gateways.BillingAdjustments

import java.sql.{ResultSet, Timestamp}
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.{Failure, Success, Try}

class ConcreteBillingAdjustments extends BillingAdjustments {

  override def add(when: LocalDateTime, account: String, amount: Double): Try[Unit] = Try {
    h2Database.command(s"INSERT INTO adjustments VALUES($amount, '${when.format(DateTimeFormatter.ISO_DATE_TIME)}', '$account')")
  }
  override def adjustments(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[BillingAdjustment]] = {
    val result = h2Database.query(s"SELECT * FROM adjustments Where (TIME_OF>'${start.format(DateTimeFormatter.ISO_DATE_TIME)}' And TIME_OF<='${end.format(DateTimeFormatter.ISO_DATE_TIME)}' And CUSTOMER='$account')")
    result.map(convertResultsToAdjustments) match
      case success @ Success(_) => success
      case failure @ Failure(_) => failure
  }

  private def convertResultsToAdjustments(result: ResultSet): List[BillingAdjustment] = {
    var list = List[BillingAdjustment]()
    while (result.next)
      val adjustment = BillingAdjustment(
        result.getDouble("AMOUNT"),
        result.getTimestamp("TIME_OF").toLocalDateTime,
        result.getString("CUSTOMER")
      )
      list = adjustment :: list
    list
  }
}

object ConcreteBillingAdjustments {
  private var h2Database = H2Database()
  def apply(database: H2Database): BillingAdjustments = {
    h2Database = database
    new ConcreteBillingAdjustments
  }
}
