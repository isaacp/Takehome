package gateways

import entities.UsageEvent

import java.time.LocalDateTime
import scala.util.Try

abstract class UsageArchive {
  def add(uEvent: UsageEvent): Try[Unit]

  def usage(start: LocalDateTime, end: LocalDateTime, account: String): Try[List[UsageEvent]]
}
