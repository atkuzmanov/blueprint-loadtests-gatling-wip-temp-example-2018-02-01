package examplePackageName.exampleProductTeamName.exampleLive

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._
import examplePackageName.exampleUtilities.TopicTempDataConfig

class ExampleThemeApi extends Simulation {

  val httpProtocol =  httpBase(http, themeApi)

  val name = niceName(this)

  object ExampleThemeDBReader {

    val printSession = exec({session =>
      println(">>> session: "+session)
      session
    })

    val readSubject = exec(http(s"$name by Id")
      .get("/theme")
      .queryParam("id", "${id}")
      .check(status.is(200))
      .check(jsonPath("$.results[0].themeId").saveAs("foundTheme"))
    )

  }
  val scn = scenario(name)
    .feed(csv(ExampleThemeTempDataConfig.feedname).circular)
    .exec(ExampleThemeDBReader.readSubject)

  setUp(scn.inject(
    constantUsersPerSec(usersPerSec) during(period seconds)
  ).protocols(httpProtocol)).assertions(
    global.responseTime.mean.lessThan(1000),
    global.successfulRequests.percent.greaterThan(95)
  )
}
