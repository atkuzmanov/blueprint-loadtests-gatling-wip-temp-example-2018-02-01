package examplePackageName.exampleLiveApi

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random
import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._

class ExampleLiveApi extends Simulation {
   val httpProtocol = http
       .baseURL("https://example.api.com")
       .shareConnections

   val curationApi = csv("example-live-api/live-api.csv").circular

   val headers = Map("Accept" -> "text/event-stream", "Cache-Control" -> "no-cache","Connection" -> "keep-alive")
  val name = niceName(this)

   val scn = scenario(name)
       .feed(curationApi)
       .repeat(5) {
           exec(http(name).get("${content}").headers(headers).check(status.in(Seq(200, 202))))
       }

   setUp(scn.inject(
       rampUsersPerSec(1) to (usersPerSec) during (60 seconds),
       constantUsersPerSec(usersPerSec) during(period seconds)
   ).protocols(httpProtocol))

}
