package data

import data.ConcreteCustomerAccounts.h2Database
import entities.{CustomerAccount, UsageTier}
import gateways.CustomerAccounts

import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import java.util.NoSuchElementException

class ConcreteCustomerAccounts extends CustomerAccounts {

  def customerAccount(id: String): Try[Option[CustomerAccount]] = {
    def convertResultToCustomerAccount(result: ResultSet): List[CustomerAccount] = {
      var list = List[CustomerAccount]()
      while (result.next())
        val customer = CustomerAccount(
          result.getString("ID"),
          UsageTier.valueOf(result.getString("TIER")),
          result.getString("CURRENCY")
        )
        list = customer :: list
      list
    }

    val triedResultSet = h2Database.query(s"SELECT * FROM customers Where ID = '$id'")
    triedResultSet.map {resultSet =>
      convertResultToCustomerAccount(resultSet).headOption
    }
  }

  override def add(customer: CustomerAccount): Try[Unit] = {
    h2Database.command(s"INSERT INTO customers VALUES('${customer.id}', '${customer.tier.toString}', '${customer.currency}')")
  }
}

object ConcreteCustomerAccounts {
  private var h2Database = H2Database()
  def apply(database: H2Database): ConcreteCustomerAccounts = {
    h2Database = database
    new ConcreteCustomerAccounts
  }
}
