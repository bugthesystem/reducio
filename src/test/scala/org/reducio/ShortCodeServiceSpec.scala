package org.reducio

import org.reducio.services.DefaultShortCodeService
import org.scalatest.BeforeAndAfterEach
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class ShortCodeServiceSpec extends SpecBase with BeforeAndAfterEach {
  "Short Code Service" should {
    "return short code for url" in {
      val key = "B1hceEY1XHhCNA"
      val url = "http://www.dice.se/games/star-wars-battlefront/"

      val codeService = new DefaultShortCodeService()

      val resultFuture: Future[String] = codeService.crateFor(url)
      val result: String = Await.result(resultFuture, 5.seconds)

      result shouldEqual key
    }
  }
}
