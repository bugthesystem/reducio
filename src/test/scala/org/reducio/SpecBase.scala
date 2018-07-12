package org.reducio

import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.reducio.persistence.DataStore
import org.reducio.services.{ ShortCodeService, StatsService, UrlShortenerService }
import org.scalatest.{ Matchers, WordSpec }
import org.specs2.mock.Mockito
import scala.language.postfixOps

trait SpecBase extends WordSpec
  with Matchers
  with ScalatestRouteTest
  with Mockito
  with FailFastCirceSupport {

  val dataStoreMock: DataStore = mock[DataStore]

  val urlShortenerServiceMock: UrlShortenerService = mock[UrlShortenerService]
  val shortCodeServiceMock: ShortCodeService = mock[ShortCodeService]
  val statsServiceMock: StatsService = mock[StatsService]

  val router = new Router(urlShortenerServiceMock)
}
