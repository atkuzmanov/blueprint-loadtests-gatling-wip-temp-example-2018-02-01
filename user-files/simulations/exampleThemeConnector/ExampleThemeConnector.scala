package examplePackageName.topicBridge

import java.util

import io.advantageous.boon.core.value.{ValueContainer, LazyValueMap, ValueList}
import io.advantageous.boon.json.JsonFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.collection.JavaConverters._
import scala.collection.mutable

import scala.concurrent.duration._
import examplePackageName.exampleUtilities.ExampleConfig._

import examplePackageName.exampleUtilities.SubjectTempData

class ExampleThemeConnector extends Simulation {

  val httpProtocol =  httpBase(http, themeConnectorApi)

  val searchTerms = csv("example-theme-connector-search-terms.csv").circular

  val name = niceName(this)

  def convertMapToTopicMatterId(value: AnyRef): String = {
    value.asInstanceOf[LazyValueMap].get("id").toString
  }
  def getTopicMatterIds(valList: ValueList): List[String] = {
    if (valList.isEmpty) {
      return List.empty
    }

    var idList = new scala.collection.mutable.MutableList[String]

    for (i <- 0 to valList.size()-1) {
      val topicMatter = valList.get(i)
      val id = convertMapToTopicMatterId(topicMatter)
      idList += id
    }
    idList.toList
  }

  object ThemeConnectorSearcher {

    val printSession = exec({session =>
      println(">>> session: "+session)
      session
    })

    val searchByTxt = exec(http(s"$name by text")
      .get("/search?txt=${text}")
      .check(status.is(200))
      .check(jsonPath("$.results").saveAs("results"))
    )

    case class TopicMatter(topicMatterId: String)

    val saveTopicMatters = exec({ session =>
      val results = session("results").as[String]
      val mapper = JsonFactory.create();
      val valueList = mapper.fromJson(results.toString()).asInstanceOf[ValueList]
      val ids = getTopicMatterIds(valueList)
      TopicMatterTempData.addData(ids)
      session
    })


  }
  val scn = scenario(name)
    .feed(searchTerms)
    .exec(ThemeConnectorSearcher.searchByTxt, ThemeConnectorSearcher.saveTopicMatters)


  setUp(scn.inject(
    constantUsersPerSec(usersPerSec) during(period seconds)
  ).protocols(httpProtocol)).assertions(
    global.responseTime.mean.lessThan(1000),
    global.successfulRequests.percent.greaterThan(95)
  )

  after {
    val topicMatterLists = TopicMatterTempData.dedupe()
    println("Simulation finished!" + topicMatterLists.length)
  }
}
