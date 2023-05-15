import java.util.Date

trait MetricsStoreComponent {
  val metricsStore: MetricsStore

  trait MetricsStore {
    def metrics(start: Date, end: Date): Option[List[Metric]]
    def metric(id: String): Option[Metric]
  }
}
