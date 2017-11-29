package org.reducio

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.{ ChainBuilder, ScenarioBuilder }
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._
import scala.util.Random

class ReducioSimulation extends Simulation {
  private val config = ConfigFactory.load()

  private val gatlingConfig = config.getConfig("gatling")

  private val followRedirectDisabledUsers = gatlingConfig.getInt("shorten-and-follow-redirect-disabled-scenario-users")
  private val followRedirectUsers = gatlingConfig.getInt("shorten-and-follow-redirect-scenario-users")

  val httpConf: HttpProtocolBuilder = http.baseURL("http://localhost:9001")
  val shortenAndFollowRedirectDisabledScenario: ScenarioBuilder =
    scenario("shorten -> FollowRedirect is Disabled")
      .exec(Shorten.shortenAndFollowRedirectDisabled)

  val shortenAndFollowRedirectScenario: ScenarioBuilder =
    scenario("shorten -> FollowRedirect is Enabled")
      .exec(Shorten.shortenAndFollowRedirect)

  setUp(
    shortenAndFollowRedirectDisabledScenario.inject(atOnceUsers(followRedirectDisabledUsers)),
    shortenAndFollowRedirectScenario.inject(atOnceUsers(followRedirectUsers))).protocols(httpConf)
}

object Shorten {

  val orderRefs: Iterator[Map[String, Int]] = Iterator.continually(
    Map("n" -> Random.nextInt(Integer.MAX_VALUE)))

  val shortenAndFollowRedirectDisabled: ChainBuilder = during(60.seconds) {
    val urlToShorten = s"https://gatling.io/docs/2.3/http/http_request-@{n}/".replaceAllLiterally("@", "$")

    feed(orderRefs)
      .exec(http("Shorten")
        .post("/")
        .formParam("url", urlToShorten)
        .disableFollowRedirect
        .check(status.in(201, 302))
        .check(header(HttpHeaderNames.Location)))
  }

  // This will call get endpoint so we will also be testing our get endpoint
  val shortenAndFollowRedirect: ChainBuilder = during(60.seconds) {
    val urlToShorten = s"https://gatling.io/docs/2.3/http/http_request-@{n}/".replaceAllLiterally("@", "$")

    feed(orderRefs)
      .exec(http("Shorten")
        .post("/")
        .formParam("url", urlToShorten)
        .check(status.in(200, 201)))
  }
}

