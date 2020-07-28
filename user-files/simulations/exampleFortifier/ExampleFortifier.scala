package examplePackageName.exampleLiveEnricher

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random
import scala.concurrent.duration._

import examplePackageName.exampleUtilities.ExampleConfig._

class ExampleFortifier extends Simulation {
  val httpProtocol = http
      .baseURL("https://example.api.com")
      .shareConnections

  val exampleFortifierBackground = csv("example-fortifier/fortifier-background.csv").circular
  val enricherSlow = csv("example-fortifier/fortifier-slow.csv").circular

  val nameBackground = niceName(this) + " example-fortifier-background"
  val scnBackground = scenario(nameBackground)
      .feed(exampleFortifierBackground)
      .exec(http(nameBackground).get("${content}").check(status.in(Seq(200, 202))))

  val nameSlow = niceName(this) + " example-fortifier-slow"
  val scnSlow = scenario(nameSlow)
      .feed(enricherSlow)
      .exec(http(nameSlow).get("${content}"))

  setUp(
    scnBackground.inject(constantUsersPerSec(50) during(60 seconds)).protocols(httpProtocol),
    scnSlow.inject(
      nothingFor(30 seconds),
      atOnceUsers(usersPerSec)
    ).protocols(httpProtocol)
  )
}
