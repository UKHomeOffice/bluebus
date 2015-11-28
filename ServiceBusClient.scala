import dispatch.{Req, Http, as, url}
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Duration

class ServiceBusClient(config: SBusConfig) {

  def readHeaders = Map(
    "Authorization" -> config.sasToken,
    "Accept" -> config.contentType)

  def peek = {
    val req = (url(config.endpoint) / "head").timeout.secure
    Http(req.POST <:< readHeaders << "" OK as.String)
  }

  def receive = {
    val req = (url(config.endpoint) / "head").timeout.secure
    Http(req.DELETE <:< readHeaders << "" OK as.String)
  }

  def send(message: String, messageId: String) = {
    val headers = Map(
      "MessageId" -> messageId,
      "Authorization" -> config.sasToken,
      "Content-Type" -> config.contentType)
    Http(url(config.endpoint).secure.POST <:< headers << message OK as.String)
  }

  def shutdown() = Http.shutdown()

  private implicit class Request(req: Req) {
    def timeout = req.setParameters(Map(
      "timeout" -> Seq(config.timeout.getSeconds.toString)))
  }
}
