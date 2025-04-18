import org.apache.pekko.actor.ActorSystem
import bluebus.client.ServiceBusClient
import bluebus.configuration.SBusConfig
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.net.URL

class SecureClientSpec extends AnyWordSpec with Matchers {

  "when the client is configured with a secure endpoint, it" ignore {
    "return the expected endpoint" in {
      implicit val system = ActorSystem("test")
      val config = SBusConfig(
        rootUri = new URL(s"https://localhost:443"),
        queueName = "queueName",
        sasKeyName = "RootManageSharedAccessKey",
        sasKey = "sasKey")
      val subject = new ServiceBusClient(config)

      subject.peek
    }
  }
}
