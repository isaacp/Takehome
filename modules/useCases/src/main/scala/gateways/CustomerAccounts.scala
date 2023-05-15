package gateways

import entities.CustomerAccount

import scala.util.Try

trait CustomerAccounts {
  def customerAccount(id: String): Option[CustomerAccount]
  def add(customer: CustomerAccount): Try[Unit]
}
