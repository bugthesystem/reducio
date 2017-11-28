package org.reducio

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.StatusCodes._
import org.scalatest.BeforeAndAfterEach
import scala.concurrent.Future

class UrlShortenerRouteGetSpec extends SpecBase with BeforeAndAfterEach {

  def actorRefFactory: ActorSystem = system

  val baseAddress = "http://localhost:9001/v1"

  "Shortener Api" should {

    "reply with long url as `Location` header when short url send" in {
      val code: String = "6a6q6"
      val expectedLongUrl = "http://www.dice.se/games/star-wars-battlefront/"

      urlShortenerServiceMock.get(code) returns Future(Some(expectedLongUrl))

      Get(s"/v1/$code") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual Found

        val location: Option[HttpHeader] = header("Location")
        location.get.value() shouldEqual expectedLongUrl
      }
    }

    "reply with `NotFound` when non-existing short url send" in {
      val code: String = "6a6q6"

      urlShortenerServiceMock.get(code) returns Future(None)

      Get(s"/v1/$code") ~> router.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }
  }
}
