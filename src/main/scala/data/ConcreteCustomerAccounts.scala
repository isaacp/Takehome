package data

import entities.CustomerAccount
import gateways.CustomerAccounts

import scala.util.{Failure,Success, Try}
import java.util.NoSuchElementException

class ConcreteCustomerAccounts extends CustomerAccounts {
  private var collection = List[CustomerAccount]()

  def customerAccount(id: String): Option[CustomerAccount] = {
    collection.find(_.id == id)
  }

  override def add(customer: CustomerAccount): Try[Unit] = {
    if !collection.exists(p =>  p.id == customer.id) then
      collection = customer :: collection
      Success(())
    else
      Failure(UnsupportedOperationException("Customer already exists."))
  }
}

object ConcreteCustomerAccounts {
  def apply(): ConcreteCustomerAccounts = {
    new ConcreteCustomerAccounts
  }
}
