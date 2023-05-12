import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class ApiPerform extends Simulation {
  val atOnceUserCount: Int = 50
  val constantUserCount: Int = 20
  val rampUserCount: Int = 100
  val commonHeaders: Map[String, String] = Map("Content-Type" -> "application/json")
  val httpProtocol: HttpProtocolBuilder = http.baseUrl("https://reqres.in")

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
        .body(StringBody(session => s"""{ "EmpID": "${session("randomValue").as[String]}", "Name": "Sparsh" }"""))
        .check(status.is(201))
    )
    .pause(1.second)
    .exec(
      http("GET Request")
        .get("/api/users?page=2")
        .check(status.is(200))
    )
    .pause(1.second)
    .exec(
      http("PUT Request")
        .put("/api/users/2")
        .headers(commonHeaders)
        .body(StringBody("""{ "EmpID": "123", "Name": "Elon Musk" }"""))
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
      global.successfulRequests.percent.gt(95),
      forAll.responseTime.max.lt(2000)
    )
}