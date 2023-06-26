package bluebus.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest}
import bluebus.configuration.SBusConfig

import scala.concurrent.duration.DurationInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceBusClient(config: SBusConfig)(implicit system: ActorSystem) {
  private val readHeadersRaw = Map(
    "Authorization" -> config.sasToken,
    "Accept" -> config.contentType
  )

  private def constructHeaders(readHeadersRaw: Map[String, String]): Seq[HttpHeader] =
    readHeadersRaw.map { case (k, v) => RawHeader(k, v) }.toSeq

  def peek: Future[String] = Http()
    .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.POST, headers = constructHeaders(readHeadersRaw)))
    .flatMap(_.entity.toStrict(10.seconds).map(_.data.utf8String))

  def receive: Future[String] = Http()
    .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.DELETE, headers = constructHeaders(readHeadersRaw)))
    .flatMap(_.entity.toStrict(10.seconds).map(_.data.utf8String))

  def send(message: String, messageId: String): Future[String] = {
    val headers = Map(
      "MessageId" -> messageId,
      "Authorization" -> config.sasToken,
      "Content-Type" -> config.contentType)

    Http()
      .singleRequest(HttpRequest(uri = config.endpoint, headers = constructHeaders(headers)))
      .flatMap(_.entity.toStrict(10.seconds).map(_.data.utf8String))
  }
}
