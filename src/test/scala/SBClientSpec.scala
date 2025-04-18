import org.apache.pekko.actor.ActorSystem

import java.net.URL
import java.util.concurrent.Executors
import bluebus.client.ServiceBusClient
import bluebus.configuration.SBusConfig
import net.jadler.Jadler._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.Waiters._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._

class SBClientSpec extends AnyWordSpec with Matchers with ScalaFutures with BeforeAndAfterAll {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  var testSubject: Option[ServiceBusClient] = None
  var actorSystem: ActorSystem = null

  /*
      200	Message successfully deleted.
      401	Authorization failure.
      404	No message was found with the specified MessageId or LockToken.
      410	Specified queue or subscription does not exist.
      500	Internal error.
   */

  "when queue is read, the client" should {
    "receive the next message" in {
      testSubject.isDefined shouldBe true
      val subject = testSubject.get
      val waiter = new Waiter
      val payload = """{"key":"value}"""
      resetJadler()

      onRequest()
        .havingMethodEqualTo("DELETE")
        .havingPathEqualTo("/queueName/messages/head")
        .respond().withBody(payload).withStatus(200)
        .thenRespond().withStatus(204)

      val handler = (m: String) => {
        println(s"message=$m")
        m shouldBe payload
        waiter.dismiss
      }

      def receiveMessages(f: String => Unit): Unit =
        subject.receive map { message =>
          f(message)
          receiveMessages(f)
        }

      receiveMessages(handler)

      waiter.await(timeout(2.seconds), dismissals(1))
    }
  }

  "when queue is peeked, the client" should {
    "receive the next message" in {
      testSubject.isDefined shouldBe true
      val subject = testSubject.get
      val waiter = new Waiter
      val payload = """{"key":"value}"""
      resetJadler()

      onRequest()
        .havingMethodEqualTo("POST")
        .havingPathEqualTo("/queueName/messages/head")
        .respond().withBody(payload).withStatus(200)
        .thenRespond().withStatus(204)

      subject.peek map { message =>
        println(s"message=$message")
        message shouldBe payload
        waiter.dismiss
      }

      waiter.await(timeout(2.seconds), dismissals(1))
    }
  }

  def brokerProperties = """{
          "DeliveryCount" : 1,
          "EnqueuedSequenceNumber" : 0,
          "EnqueuedTimeUtc" : "Wed, 29 Nov 2017 13:37:27 GMT",
          "MessageId" : "cc046373b5c845639490018c995bf6fd",
          "PartitionKey" : "169",
          "SequenceNumber" : 61643019979786635,
          "State" : "Active",
          "TimeToLive" : 1209600
        }"""

  def initializeClient(actorSystem: ActorSystem): Option[ServiceBusClient] = {
    val config = SBusConfig(
      rootUri=new URL(s"http://localhost:$port"),
      queueName="queueName",
      sasKeyName="RootManageSharedAccessKey",
      sasKey="sasKey")
    Some(new ServiceBusClient(config)(actorSystem))
  }

  override def beforeAll: Unit = {
    initJadler()
    actorSystem = ActorSystem("test")
    testSubject = initializeClient(actorSystem)
  }

  override def afterAll: Unit = {
    testSubject = None
    closeJadler()
    actorSystem.terminate()
  }
}
