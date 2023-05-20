package data

import data.ConcreteCustomerAccounts.h2Database
import entities.CustomerAccount
import gateways.CustomerAccounts

import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import java.util.NoSuchElementException

class ConcreteCustomerAccounts extends CustomerAccounts {

  def customerAccount(id: String): Try[Option[CustomerAccount]] = {
    val triedResultSet = h2Database.query(s"SELECT * FROM customers Where ID = '$id'")
    triedResultSet.map {resultSet =>
      val list = convertResultToCustomerAccount(resultSet)
      if list.length > 0 then
        Some(list.head)
      else
        None
    }
  }

  override def add(customer: CustomerAccount): Try[Unit] = {
    h2Database.command(s"INSERT INTO customers VALUES('${customer.id}', '${customer.tier}', '${customer.currency}')")
    Try(println(h2Database.query(s"SELECT * FROM customers")))
  }

  def convertResultToCustomerAccount(result: ResultSet): List[CustomerAccount] = {
    var list = List[CustomerAccount]()
    while(result.next())
      val customer = CustomerAccount(
        result.getString("ID"),
        result.getString("TIER"),
        result.getString("CURRENCY")
      )
      list = customer :: list
    list
  }
}

object ConcreteCustomerAccounts {
  private var h2Database = H2Database()
  def apply(database: H2Database): ConcreteCustomerAccounts = {
    h2Database = database
    new ConcreteCustomerAccounts
  }
}
