package bluebus.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest, HttpResponse}
import bluebus.configuration.SBusConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ServiceBusClient(config: SBusConfig)(implicit system: ActorSystem) {
  private val readHeadersRaw = Map(
    "Authorization" -> config.sasToken,
    "Accept" -> config.contentType
  )

  private def constructHeaders(readHeadersRaw: Map[String, String]): Seq[HttpHeader] =
    readHeadersRaw.map { case (k, v) => RawHeader(k, v) }.toSeq

  def peek: Future[String] = filter200(
    Http().singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.POST, headers = constructHeaders(readHeadersRaw)))
  )

  private def filter200(eventualResponse: Future[HttpResponse]) = {
    eventualResponse
      .flatMap {
        case r if r.status.isSuccess() =>
          r.entity.toStrict(10.seconds).map(_.data.utf8String)
        case r =>
          Future.failed(new Exception(s"Non-200 response: ${r.status}"))
      }
  }

  def receive: Future[String] = filter200(
    Http()
      .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.DELETE, headers = constructHeaders(readHeadersRaw)))
  )

  def send(message: String, messageId: String): Future[String] = {
    val headers = Map(
      "MessageId" -> messageId,
      "Authorization" -> config.sasToken,
      "Content-Type" -> config.contentType)

    filter200(
      Http().singleRequest(HttpRequest(uri = config.endpoint, headers = constructHeaders(headers)))
    )
  }
}
