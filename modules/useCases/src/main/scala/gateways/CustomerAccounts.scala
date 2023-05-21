package gateways

import entities.CustomerAccount

import scala.util.Try

abstract class CustomerAccounts {
  def customerAccount(id: String): Try[Option[CustomerAccount]]
  def add(customer: CustomerAccount): Try[Unit]
}
