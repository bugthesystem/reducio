package org.reducio

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.StatusCodes._
import scala.concurrent.Future

class UrlShortenerRouteGetSpec extends SpecBase {

  def actorRefFactory: ActorSystem = system

  "Shortener Api" should {

    "reply with long url as `Location` header when short url sent" in {
      val code: String = "6a6q6"
      val expectedLongUrl = "http://www.dice.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.get(code) returns Future(Some(expectedLongUrl))

      Get(s"/$code") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual Found

        val location: Option[HttpHeader] = header("Location")
        location.get.value() shouldEqual expectedLongUrl
      }
    }

    "reply with `NotFound` when non-existing short url sent" in {
      val code: String = "6a6q6"

      urlShortenerServiceMock.get(code) returns Future(None)

      Get(s"/$code") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }
  }
}
