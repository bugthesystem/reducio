package org.reducio

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import org.reducio.models.Stats
import scala.concurrent.Future

class UrlShortenerRouteStatsSpec extends SpecBase {

  def actorRefFactory: ActorSystem = system

  "Url Shortener Api" should {

    "returns stats if url exists" in {

      val url = "http://www.dice.se/games/star-wars-battlefront/"

      val expectedStats = Stats(callCount = 10L)

      urlShortenerServiceMock.stats(url) returns Future(Some(expectedStats))

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK

        responseAs[Stats] shouldEqual expectedStats
      }
    }

    "reply with `NotFound` if url does not exist" in {
      val url = "http://www.dice.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.stats(url) returns Future(None)

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }
    "reply with `BadRequest` if url is malformed" in {
      val url = "httX://w#$.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.stats(url) returns Future(None)

      Get(s"/stats/?url=$url") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual BadRequest
      }
    }
  }
}
