package examplePackageName.exampleProductTeamName

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

import examplePackageName.exampleUtilities.ExampleConfig._

class ExampleMatterApiAsset extends Simulation {

  val httpProtocol = http
    .baseURL("https://api.example.com/examplePath")

  val data = csv("exampleProductTeamName/example-matter-api-asset.csv").circular

  val defaultHeaders = Map("Accept" -> "application/json")

  val headers =
    if (cacheControl.nonEmpty) {
      defaultHeaders + ("Cache-Control" -> cacheControl)
    } else {
      defaultHeaders
    }

  println(s">>> headers are: $headers")

  val scn = scenario("Example Matter API Asset")
    .feed(data)
    .repeat(5) {
      exec(http("Example Matter API").get("${content}").headers(headers).check(status is 200))
    }

  setUp(
    scn.inject(constantUsersPerSec(usersPerSec) during (2 minutes)).protocols(httpProtocol)
  )
    .assertions(global.responseTime.mean.lessThan(500), global.successfulRequests.percent.greaterThan(100)
  )

}
