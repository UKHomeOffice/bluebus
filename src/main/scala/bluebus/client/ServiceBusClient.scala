package bluebus.client

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.headers.RawHeader
import org.apache.pekko.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest, HttpResponse}
import bluebus.configuration.SBusConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ServiceBusClient(config: SBusConfig)(implicit system: ActorSystem) {
  private def readHeadersRaw: Seq[HttpHeader] = constructHeaders(Map(
    "Authorization" -> config.sasToken,
    "Accept" -> config.contentType
  ))

  private def constructHeaders(readHeadersRaw: Map[String, String]): Seq[HttpHeader] =
    readHeadersRaw.map { case (k, v) => RawHeader(k, v) }.toSeq

  def peek: Future[String] = filter200(
    Http().singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.POST, headers = readHeadersRaw))
  )

  private def filter200(eventualResponse: Future[HttpResponse]): Future[String] = {
    eventualResponse
      .flatMap {
        case r if r.status.isSuccess() =>
          r.entity
            .toStrict(1.seconds)
            .map(_.data.utf8String)
            .recover { e =>
              r.entity.discardBytes()
              throw new Exception(s"Failed to read response body: ${e.getMessage}")
            }
        case r =>
          r.entity.discardBytes()
          Future.failed(new Exception(s"Non-200 response: ${r.status}"))
      }
  }

  def receive: Future[String] = filter200(
    Http()
      .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.DELETE, headers = readHeadersRaw))
  )
}
