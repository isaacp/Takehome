package data

import data.H2Database.{DATABASE_URL, con}
import org.h2.*

import java.io.File
import java.sql.{Connection, DriverManager, ResultSet, Statement}
import scala.util.{Failure, Try}

class H2Database {
  def initialize: Try[Unit] = Try {
    val stm: Statement = con.createStatement
    val create: String =
      """
        |create table metrics(ID VARCHAR(500) PRIMARY KEY, USAGE_TYPE VARCHAR(500), VAL INT, CUSTOMER VARCHAR(500), CREATED TIMESTAMP);
        |create table customers(ID VARCHAR(500) PRIMARY KEY, TIER VARCHAR(500), CURRENCY VARCHAR(500));
        |create table usage(ID VARCHAR(500) PRIMARY KEY, CUSTOMER VARCHAR(500), USAGE_TYPE VARCHAR(500), UNITS DOUBLE, TIME_OF TIMESTAMP, CREATED TIMESTAMP);
        |create table usageArchive(ID VARCHAR(500) PRIMARY KEY, CUSTOMER VARCHAR(500), USAGE_TYPE VARCHAR(500), UNITS DOUBLE, TIME_OF TIMESTAMP, CREATED TIMESTAMP);
        |create table adjustments(AMOUNT DOUBLE, TIME_OF TIMESTAMP, CUSTOMER VARCHAR(500))""".stripMargin

    stm.execute(create)
    con.commit()
  }

  def command(sql: String): Try[Unit] = {
    try {
      Try {
        connection().createStatement.execute(sql)
        connection().commit()
      }
    } catch {
      case e: Exception => Failure(e)
    }
  }

  def query(sql: String): Try[ResultSet] = {
    try {
      Try {
        val resultSet = connection().createStatement.executeQuery(sql)
        connection().commit()
        resultSet
      }
    } catch {
      case e: Exception => Failure(e)
    }
  }

  private def connection(): Connection = {
    if con.isClosed then con = DriverManager.getConnection(DATABASE_URL)
    con
  }
}

object H2Database {
  final private val DATABASE_URL: String = s"jdbc:h2:mem:DATABASE"
  final private var con: Connection = DriverManager.getConnection(DATABASE_URL)

  def apply(): H2Database = {
    new H2Database
  }
}
