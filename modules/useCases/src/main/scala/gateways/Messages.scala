package gateways

import entities.UsageEvent

import scala.util.Try

abstract class Messages {
  
  def push(usageEvent: UsageEvent): Try[Unit]
}
