import controllers.{AddBillingAdjustmentController, AuditUsageEventsController, BuildUsageReportController, ConvertUsageMetricsController, GetUsageMetricsController, PersistUsageEventsController}
import data.{ConcreteBillingAdjustments, ConcreteCustomerAccounts, ConcreteMetricStore, ConcreteUsageArchive, ConcreteUsageStore}
import entities.{BillingAdjustment, Converter, CustomerAccount, Metric}
import gateways.{MetricStore, UsageStore}

import scala.io.StdIn.readLine
import java.time.*
import java.util.{NoSuchElementException, UUID}
import scala.util.{Failure, Success, Try}
import data.H2Database

object Application extends App {
  val start = LocalDateTime.now()
  private val database = H2Database()
  database.initialize

  private val metricStore = ConcreteMetricStore(database)
  private val usageStore = ConcreteUsageStore(database)
  private val customerAccounts = ConcreteCustomerAccounts(database)
  private val billingAdjustments = ConcreteBillingAdjustments(database)
  private val usageArchive = ConcreteUsageArchive()
  private val converter = Converter()
  private val reportController = BuildUsageReportController(usageStore, usageArchive, customerAccounts, billingAdjustments)
  private val adjustmentController = AddBillingAdjustmentController(billingAdjustments)

  LoadData()
  eventLoop

  private def eventLoop: Try[Unit] = Try {
    RunMetricsRetrievalStorageProcess(start, LocalDateTime.now)
    val input = readLine("$> ")
    val now = LocalDateTime.now()
    val parts = input.split(" ")
    if input.matches("report [0-9]+") then
      val command = parts(0)
      val account = parts(1)
      println(s"$command $account")
      reportController.execute(start, now, account).foreach{ report =>
        println(s"${command.capitalize} for account $account ")
        println(report)
      }
    else if input.matches("adjust [0-9]+ (-)?[0-9]+(.[0-9]+)?") then
      val command = parts(0)
      val account = parts(1)
      val amount = parts(2)
      adjustmentController.execute(BillingAdjustment(amount.toDouble, now, account)) match
        case Success(_) => println(s"account $account ${command}ed by $amount dollars.")
        case Failure(exception) => println(exception)

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

  private def LoadData(): Unit = {
    customerAccounts.add(CustomerAccount("1", "platinum", "usd"))
    customerAccounts.add(CustomerAccount("2", "gold", "usd"))
    customerAccounts.add(CustomerAccount("3", "silver", "usd"))
    val accountIds = List[String]("1", "2", "3")
    val metricType = List[String]("compute", "storage", "bandwidth")
    val rand = new scala.util.Random

    for (_ <- 0 to 10000)
      metricStore.add(
        Metric(
          UUID.randomUUID().toString,
          metricType(rand.between(0, 3)),
          rand.between(0, 250),
          accountIds(rand.between(0, 3)),
          LocalDateTime.now().plusSeconds(rand.between(1, 300))
        )
      )
  }
}