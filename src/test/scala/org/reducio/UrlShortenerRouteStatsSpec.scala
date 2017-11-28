package org.reducio

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import org.reducio.models.Stats
import org.scalatest.BeforeAndAfterEach
import scala.concurrent.Future

class UrlShortenerRouteStatsSpec extends SpecBase with BeforeAndAfterEach {

  def actorRefFactory: ActorSystem = system

  val baseAddress = "http://localhost:9001/v1"

  "Shortener Api" should {

    "returns stats if url is exists" in {
      val url = "http://www.dice.se/games/star-wars-battlefront/"
      val callCount = 1L

      urlShortenerServiceMock.stats(url) returns Future(Some(Stats(callCount)))

      Get(s"/v1/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK

        responseAs[Stats] should equal(Stats(callCount = callCount))
      }
    }

    "reply with `NotFound` if url does not exist" in {
      val url = "http://www.dice.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.stats(url) returns Future(None)

      Get(s"/v1/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }
  }
}
