package examplePackageName.curationApi

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class ExampleOrchestrationApi extends Simulation {

  val httpProtocol = http.baseURL("https://example.api.com")

  val scn = scenario("Orchestration Api /orchestrations")
    .exec(session =>
      session.set("id", randomAlphaNumericString(24))) // https://groups.google.com/forum/#!topic/gatling/ggR6L4ukmqU
    .exec(http("Orchestration Api /orchestrations")
    .put("/orchestrations?about=BD8B96DA7858A8FC0D7XXXX")
    .body(ElFileBody("orchestration-api/orchestration-api-orchestrations.json")).asJSON
    .header("Content-Type", "application/json")
    .check(status.in(Seq(204)))
  )

  setUp(scn.inject(
    rampUsersPerSec(1) to 50 during (2 minutes),
    constantUsersPerSec(5) during (5 minutes)
  ).protocols(httpProtocol))

  // http://alvinalexander.com/scala/creating-random-strings-in-scala
  def randomAlphaNumericString(length: Int): String = {
    val chars = ('a' to 'f') ++ ('A' to 'F') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }

  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }
}
