package examplePackageName.exampleProductTeamName

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random
import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._

class ExamplePresentationApiTests extends Simulation {
  val feeder = csv("assertion-folder-name.csv").random
  val httpProtocol = http.baseURL("https://example.api.com")
  val name = niceName(this)
  val component = scenario(name)
    .feed(feeder)
    .exec(http(name)
    .post("/present-something")
    .header("Content-Type", "application/json;charset=utf-8")
    .body(RawFileBody("assert-folder-name/${fileName}.json"))
    .check(status.is(200)))

  setUp(component.inject(
    nothingFor(5 seconds),
    atOnceUsers(10),
    rampUsersPerSec(10) to (usersPerSec) during(1 minutes),
    constantUsersPerSec(usersPerSec) during(period seconds)
  ).protocols(httpProtocol))
}
