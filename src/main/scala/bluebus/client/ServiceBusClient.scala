package bluebus.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest}
import bluebus.configuration.SBusConfig

import scala.collection.immutable
//import dispatch.{Req, as, url, Http => HttpOld}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceBusClient(config: SBusConfig)(implicit system: ActorSystem) {
  //  val http = HttpOld.default
  val readHeadersRaw = Map(
    "Authorization" -> config.sasToken,
    "Accept" -> config.contentType
  )

  //  val request: String => Req = (endpoint: String) => {
  //    val req: Req = url(endpoint).timeout
  //    config.isSecure match {
  //      case true =>
  //        req.secure
  //      case false =>
  //        req
  //    }
  //  }

  private def constructHeaders(readHeadersRaw: Map[String, String]): Seq[HttpHeader] =
    readHeadersRaw.map { case (k, v) => RawHeader(k, v) }.toSeq

  //  def peek_old: Future[String] = http(request(s"${config.endpoint}/head").POST <:< readHeaders << "" OK as.String)
  def peek: Future[String] = Http()
    //    .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.POST, headers = ))
    .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.POST, headers = constructHeaders(readHeadersRaw)))
    .map(_.entity.toString)

  //  def receive: Future[String] = http(request(s"${config.endpoint}/head").DELETE <:< readHeaders << "" OK as.String)

  def receive: Future[String] = Http()
    .singleRequest(HttpRequest(uri = s"${config.endpoint}/head", method = HttpMethods.DELETE, headers = constructHeaders(readHeadersRaw)))
    .map(_.entity.toString)

  //  def send(message: String, messageId: String) = {
  //    val headers = Map(
  //      "MessageId" -> messageId,
  //      "Authorization" -> config.sasToken,
  //      "Content-Type" -> config.contentType)
  //    http(request(config.endpoint) <:< headers << message OK as.String)
  //  }

  def send(message: String, messageId: String): Future[String] = {
    val headers = Map(
      "MessageId" -> messageId,
      "Authorization" -> config.sasToken,
      "Content-Type" -> config.contentType)

    Http()
      .singleRequest(HttpRequest(uri = config.endpoint, headers = constructHeaders(headers)))
      .map(_.entity.toString)
  }

  //  def shutdown() = http.shutdown()

  //  private implicit class Request(req: Req) {
  //    def timeout = req.setParameters(Map(
  //      "timeout" -> Seq(config.timeout.getSeconds.toString)))
  //  }
}
