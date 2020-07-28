package examplePackageName.topicStore

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.collection.mutable
import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._
import examplePackageName.exampleUtilities.TopicTempData

class ExampleThemeCreate extends Simulation {
  val httpProtocol =  httpBase(http, themeDBApi)

  val randomUUIDs = Iterator.continually(Map("UUID" -> java.util.UUID.randomUUID.toString))

  val name = niceName(this)

  object ExampleThemeDB {
    val createTheme = exec(
      http(name)
        .post("/theme")
        .body(StringBody(
          """{
            |"exampleJSONfield1": "8881a2a7-8102-4edc-861f-f8bbf5dXXXXX",
            |"exampleJSONfield2": true
            |}""".stripMargin)).asJSON
        .check(status.is(201))
        .check(jsonPath("$.themeId").saveAs("themeId"))
    )

    val saveTheme = exec({ session =>
      val topicMatterId = session("UUID").as[String]
      val themeId = session("topicId").as[String]
      ThemeTempData.addData(themeId, topicMatterId)
      session
    })

    val printSession = exec({session =>
      println(">>> session: "+session)
      session
    })
  }

  val scnCreate = scenario(name)
    .feed(randomUUIDs)
    .exec(ExampleThemeDB.createTheme, ExampleThemeDB.saveTheme)

  setUp(
    scnCreate
      .inject(
        constantUsersPerSec(usersPerSec) during(period seconds)
      ).protocols(httpProtocol)
   )
}
