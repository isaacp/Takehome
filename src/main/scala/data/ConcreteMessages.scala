package data

import akka.actor.{Actor, ActorSystem, Props}
import controllers.PersistUsageEventsController
import entities.UsageEvent
import gateways.{Messages, UsageStore}

import scala.util.{Try, Failure}

class UsageWriterAkka(controller: PersistUsageEventsController) extends Actor{
  def receive = {
    case msg: UsageEvent => controller.execute(msg)
    case _ => Failure(IllegalArgumentException("Unknown message"))
  }
}

class ConcreteMessages(private val usageStore: UsageStore) extends Messages {
  private val actorSystem = ActorSystem("ActorSystem")
  private val actor = actorSystem.actorOf(Props(classOf[UsageWriterAkka], PersistUsageEventsController(usageStore)), "usageWriter")

  override def push(usageEvent: UsageEvent): Try[Unit] = Try {
    actor ! usageEvent
  }
}
