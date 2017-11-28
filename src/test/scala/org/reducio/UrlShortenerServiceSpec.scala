package org.reducio

import org.reducio.models._
import org.reducio.services.DefaultUrlShortenerService
import org.reducio.util._
import org.reducio.util.KeyUtils._
import org.scalatest.BeforeAndAfterEach
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class UrlShortenerServiceSpec extends SpecBase with BeforeAndAfterEach {
  "Url Shortener Service" should {
    "shorten valid url if not exist" in {
      val expectedCode = "6a6q6"
      val urlToShorten = "http://www.dice.se/games/star-wars-battlefront/"
      val request = UrlShortenRequest(url = urlToShorten)

      dataStoreMock.get[String](urlAsKey(urlsafeEncode64(urlToShorten))) returns Future(None)

      shortCodeServiceMock.crateFor(urlToShorten) returns Future(expectedCode)
      dataStoreMock.save[String](codeAsKey(expectedCode), urlToShorten) returns Future(true)
      dataStoreMock.save[String](urlAsKey(urlsafeEncode64(urlToShorten)), expectedCode) returns Future(true)

      val urlShortener = new DefaultUrlShortenerService(dataStoreMock, shortCodeServiceMock, statsServiceMock)

      val resultFuture: Future[UrlShortenResult] = urlShortener.shorten(request)
      val result: UrlShortenResult = Await.result(resultFuture, 5.seconds)

      result.code shouldEqual expectedCode
      result.opStatus shouldEqual EntityOp.Created
    }

    "return existing short code if the URL exist" in {
      val expectedCode = "6a6q6"
      val urlToShorten = "http://www.dice.se/games/star-wars-battlefront/"
      val request = UrlShortenRequest(url = urlToShorten)

      dataStoreMock.get[String](urlAsKey(urlsafeEncode64(urlToShorten))) returns Future(Some(expectedCode))

      val urlShortener = new DefaultUrlShortenerService(dataStoreMock, shortCodeServiceMock, statsServiceMock)

      val resultFuture: Future[UrlShortenResult] = urlShortener.shorten(request)
      val result: UrlShortenResult = Await.result(resultFuture, 5.seconds)

      result.code shouldEqual expectedCode
      result.opStatus shouldEqual EntityOp.Found
    }

    "return short code for URL if exists" in {
      val code = "6a6q6"
      val expectedUrl = "http://www.dice.se/games/star-wars-battlefront/"

      dataStoreMock.exists(codeAsKey(code)) returns Future(true)
      dataStoreMock.get[String](codeAsKey(code)) returns Future(Some(expectedUrl))
      statsServiceMock.hit(urlAsStatsKey(urlsafeEncode64(expectedUrl))) returns Future(anyLong)

      val urlShortener = new DefaultUrlShortenerService(dataStoreMock, shortCodeServiceMock, statsServiceMock)

      val resultFuture: Future[Option[String]] = urlShortener.get(code)
      val resultOpt: Option[String] = Await.result(resultFuture, 5.seconds)

      val actualUrl = resultOpt.get
      actualUrl shouldEqual expectedUrl
    }

    "return `None` for URL doesn't exists" in {
      val code = "6a6q6"

      dataStoreMock.exists(codeAsKey(code)) returns Future(false)

      val urlShortener = new DefaultUrlShortenerService(dataStoreMock, shortCodeServiceMock, statsServiceMock)

      val resultFuture: Future[Option[String]] = urlShortener.get(code)
      val result: Option[String] = Await.result(resultFuture, 5.seconds)

      result.isEmpty shouldEqual true
    }
  }
}
