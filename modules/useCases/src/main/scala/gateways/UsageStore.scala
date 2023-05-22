package gateways

import entities.UsageEvent

import scala.util.Try
import java.time._

abstract class UsageStore {
  def add(usageEvent: UsageEvent): Try[Unit]
  def delete(usageEvent: UsageEvent): Try[Unit]
  def usage(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[UsageEvent]]
  def olderThan(date: LocalDateTime): Try[List[UsageEvent]]
}
