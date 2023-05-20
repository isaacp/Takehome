package gateways

import entities.UsageEvent

import scala.util.Try
import java.time._

trait UsageStore {
  def add(usageEvent: UsageEvent): Try[Unit]
  def usage(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[UsageEvent]]
  def olderThan(date: LocalDateTime): Try[List[UsageEvent]]
}
