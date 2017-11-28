package org.reducio

import org.reducio.services.DefaultShortCodeService
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

class ShortCodeServiceSpec extends SpecBase {
  "Service" should {
    "return short code for url" in {
      val key = "B1hceEY1XHhCNA"
      val url = "http://www.dice.se/games/star-wars-battlefront/"

      val codeService = new DefaultShortCodeService()

      val resultFuture: Future[String] = codeService.create(url)
      val result: String = Await.result(resultFuture, 5.seconds)

      result shouldEqual key
    }
  }
}
