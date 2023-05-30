import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.util.Try

class ApiPerform extends Simulation {
  def readInt(prompt: String): Int = {
    val input = System.console().readLine(prompt)
    Try(input.toInt).getOrElse(0)
  }

  val atOnceUserCount: Int = readInt("Enter value for at one user count: ")
  val constantUserCount: Int = readInt("Enter value for constant user count: ")
  val rampUserCount: Int = readInt("Enter value for ramp user count: ")
  val commonHeaders: Map[String, String] = Map("Content-Type" -> "application/json")
  val httpProtocol: HttpProtocolBuilder = http.baseUrl("https://reqres.in")
  val data_input: BatchableFeederBuilder[String] = csv("src/test/Data_File/data.csv").circular

  val commonRequest: ChainBuilder = exec(session => {
    val randomValue = scala.util.Random.nextInt(1000).toString
    session.set("randomValue", randomValue)
  })

  val chainedScenario: ScenarioBuilder = scenario("Chained Scenario")
    .exec(commonRequest)
    .pause(1.second)
    .exec(
      http("POST Request")
        .post("/api/users")
        .headers(commonHeaders)
        .body(StringBody(session => s"""{ "EmpID": "${session("randomValue").as[String]}", "Name": "Sparsh" }""".stripMargin))
        .check(status.is(201))
    )
    .pause(1.second)
    .exec(
      http("GET Request")
        .get("/api/users?page=2")
        .check(status.is(200))
    )
    .pause(1.second)
    .feed(data_input)
    .exec(
      http("PUT Request")
        .put("/api/users/2")
        .headers(commonHeaders)
        .body(StringBody(session => s"""{"name": "${session("name").as[String]}"}""".stripMargin))
        .check(status.is(200))
    )

  setUp(
    chainedScenario.inject(
      nothingFor(5.seconds),
      atOnceUsers(atOnceUserCount),
      constantUsersPerSec(constantUserCount).during(15.seconds),
      rampUsersPerSec(1).to(rampUserCount).during(30.seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(1.minute)
    .assertions(
      global.successfulRequests.percent.gte(95),
      forAll.responseTime.max.lt(2000)
    )
}
