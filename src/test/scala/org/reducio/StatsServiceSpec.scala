package org.reducio

import org.reducio.services.DefaultStatsService
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class StatsServiceSpec extends SpecBase {

  "Service" should {
    "return stats if url exists" in {

      val key = "key"
      val callCount = 1L

      dataStoreMock.get[Long](key) returns Future(Some(callCount))
      val statsService = new DefaultStatsService(dataStoreMock)

      val resultFuture: Future[Option[Long]] = statsService.getStats(key)
      val result: Option[Long] = Await.result(resultFuture, 5.seconds)

      result.get shouldEqual callCount
    }

    "return `None  if url does not exist" in {
      val key = "key"
      dataStoreMock.get[Long](key) returns Future(None)
      val statsService = new DefaultStatsService(dataStoreMock)

      val resultFuture: Future[Option[Long]] = statsService.getStats(key)
      val result: Option[Long] = Await.result(resultFuture, 5.seconds)

      result.isEmpty shouldEqual true
    }

    "incr stats for URL" in {
      val key = "key"
      dataStoreMock.incr(key) returns Future(anyLong)
      val statsService = new DefaultStatsService(dataStoreMock)

      val resultFuture: Future[Long] = statsService.hit(key)
      val result: Long = Await.result(resultFuture, 5.seconds)

      result shouldEqual anyLong
    }
  }

}
