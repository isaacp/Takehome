import controllers.{AddBillingAdjustmentController, AuditUsageEventsController, BuildUsageReportController, ConvertUsageMetricsController, GetUsageMetricsController, PersistUsageEventsController}
import data.{ConcreteBillingAdjustments, ConcreteCustomerAccounts, ConcreteMetricStore, ConcreteUsageArchive, ConcreteUsageStore}
import entities.{BillingAdjustment, Converter, CustomerAccount, Metric}
import gateways.{MetricStore, UsageStore}

import scala.io.StdIn.readLine
import java.time.*
import java.util.{NoSuchElementException, UUID}
import scala.util.{Failure, Success, Try}

object Application extends App {
  val start = LocalDateTime.now()
  private val metricStore = ConcreteMetricStore()
  private val usageStore = ConcreteUsageStore()
  private val usageArchive = ConcreteUsageArchive()
  private val customerAccounts = ConcreteCustomerAccounts()
  private val billingAdjustments = ConcreteBillingAdjustments()
  private val converter = Converter()

  LoadData()

  private val reportController = BuildUsageReportController(usageStore, usageArchive, customerAccounts, billingAdjustments)
  private val adjustmentController = AddBillingAdjustmentController(billingAdjustments)

  println(customerAccounts)
  eventLoop

  private def eventLoop: Try[Unit] = Try {
    RunMetricsRetrievalStorageProcess(start, LocalDateTime.now)
    val input = readLine("$> ")
    val now = LocalDateTime.now()
    if input.matches("report [0-9]+") then
      val command = input.split(" ")(0)
      val account = input.split(" ")(1)
      reportController.execute(start, now, account).map{ report =>
        println(s"${command.capitalize} for account $account ")
        println(report)
      }
    else if input.matches("adjust [0-9]+ [0-9]+(.[0-9]+)?") then
      val command = input.split(" ")(0)
      val account = input.split(" ")(1)
      val amount = input.split(" ")(2)
      adjustmentController.execute(BillingAdjustment(amount.toDouble, now, account))
      println(s"account $account ${command}ed by $amount dollars.")

    if input.matches("q|quit") then
      Success(())
    else
      eventLoop
  }

  private def RunMetricsRetrievalStorageProcess(start: LocalDateTime, end: LocalDateTime): Try[Unit] = {
    val auditController = AuditUsageEventsController(metricStore)

    GetUsageMetricsController(metricStore).execute(start, end).map { metrics =>
      ConvertUsageMetricsController(converter).execute(metrics).map { usageEvents =>
        if usageEvents.exists(usageEvent => auditController.execute(usageEvent).isFailure) then
          Failure(NoSuchElementException("Error auditing usage event."))
        else
          PersistUsageEventsController(usageStore).execute(usageEvents)
      }
    }
  }

  private def LoadData() = {
    customerAccounts.add(CustomerAccount("1", "platinum", "usd"))
    customerAccounts.add(CustomerAccount("2", "gold", "usd"))
    customerAccounts.add(CustomerAccount("3", "silver", "usd"))
    val accountIds = List[String]("1", "2", "3")
    val metricType = List[String]("compute", "storage", "bandwidth")
    val rand = new scala.util.Random

    for {
      _ <- 0 to 100
      _ = metricStore.add(
        Metric(
          UUID.randomUUID().toString,
          metricType(rand.between(0, 3)),
          rand.between(0, 250),
          accountIds(rand.between(0, 3)),
          LocalDateTime.now().plusSeconds(rand.between(1, 300 ))
        )
      )
    } yield 0
  }
}