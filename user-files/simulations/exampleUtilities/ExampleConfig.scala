package examplePackageName.exampleUtilities

import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

object ExampleConfig {
  val themeDBApi = "https://example.api.com"
  val themeConnectorApi = "https://example.api.com"
  val themeApi = "https://example.api.com"
  val localhost = "http://localhost:8080"

  val cacheControl: String = sys.env.get("CACHE_CONTROL").getOrElse("no-cache")
  val usersPerSec: Int = sys.env.get("USERS_PER_SEC").getOrElse("1").toInt
  val period = 60
  def niceName(obj: Object) = s"${obj.getClass.getSimpleName} with $usersPerSec for $period seconds"

  val useProxy = sys.env.get("SERVER_ENV").getOrElse("non-dev") == "dev"

  def httpBase(http: HttpProtocolBuilder, baseUrl: String) = http.baseURL(baseUrl)
}
