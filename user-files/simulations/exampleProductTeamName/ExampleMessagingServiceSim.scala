package examplePackageName.exampleProductTeamName

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random
import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._

class ExampleMessagingServiceSim extends Simulation {
  val httpProtocol = http
    .baseURL("https://example.api.com")
    .shareConnections

  val service = csv("example-messaging-service-pending.csv").circular

  val headers = Map("Accept" -> "application/json")

  val name = niceName(this)

  val scn = scenario(name)
    .feed(service)
    .repeat(1) {
      exec(http(name).get("${content}").headers(headers).check(status.in(Seq(200, 201, 202))))
    }

  setUp(scn.inject(
    constantUsersPerSec(usersPerSec) during(period seconds)
  ).protocols(httpProtocol))

}
