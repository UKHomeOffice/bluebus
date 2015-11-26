import java.time.Duration

case class SBusConfig(rootUri: String, namespace: String, tokenLease: Duration, timeout: Duration,
                       queueName: String, sasKeyName: String, sasKey: String) {
  val endpoint = s"https://$namespac$rootUri/$queueName/messages"
  def sasToken = SASAuthFactory.createToken(endpoint, sasKeyName, sasKey, tokenLease)
}
