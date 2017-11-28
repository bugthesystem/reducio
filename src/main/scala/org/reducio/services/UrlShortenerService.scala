package org.reducio.services

import org.reducio.models.{ Stats, UrlShortenRequest, UrlShortenResult }
import scala.concurrent.Future
import scala.language.postfixOps
import com.typesafe.scalalogging.LazyLogging
import org.reducio.common._
import org.reducio.models.{ EntityOp, Stats, UrlShortenRequest, UrlShortenResult }
import org.reducio.persistence.DataStore
import org.reducio.util._
import org.reducio.util.KeyUtils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

trait UrlShortenerService {
  def shorten(request: UrlShortenRequest): Future[UrlShortenResult]

  def get(code: String): Future[Option[String]]

  def stats(url: String): Future[Option[Stats]]

  def clean(url: String): Future[Unit]
}

class DefaultUrlShortenerService(
  val dataStore: DataStore,
  val shortCodeService: ShortCodeService,
  val statsService: StatsService)
  extends UrlShortenerService with LazyLogging {

  override def shorten(request: UrlShortenRequest): Future[UrlShortenResult] = {
    logger.debug(s"Creating shortened URL for: ${request.url}")

    for {
      codeOpt <- getByUrl(request.url)
      result <- codeOpt match {
        case Some(code) =>
          logger.debug(s"The requested URL(${request.url}) is already shortened, so will return found one.")
          Future.successful(UrlShortenResult(code, opStatus = EntityOp.Found))
        case None => save(request.url)
      }
    } yield result
  }

  override def get(code: String): Future[Option[String]] = {

    logger.debug(s"Access requested for shortened URL using code: $code")

    (for {
      exists <- dataStore.exists(codeAsKey(code))
      _ <- predicate(exists)(new Exception("Key no found in data store"))
      result <- dataStore.get[String](codeAsKey(code))
      _ <- result match {
        case Some(url) => statsService.hit(urlAsStatsKey(urlsafeEncode64(url)))
        case None => Future(())
      }
    } yield result).recover({
      case ex: Exception =>
        logger.error(s"An error occurred while getting URL by given code:$code", ex)
        None
    })
  }

  override def stats(url: String): Future[Option[Stats]] = {
    statsService.getStats(urlAsStatsKey(urlsafeEncode64(url))) map {
      statsOpt: Option[Long] =>
        statsOpt match {
          case Some(callCount) => Some(Stats(callCount))
          case None => None
        }
    }
  }

  override def clean(url: String): Future[Unit] = {
    val urlOpt = dataStore.get[String](urlAsKey(urlsafeEncode64(url)))
    urlOpt.map {
      case Some(code) => purge(code, url)
      case _ =>
        logger.debug(s"There is no record to clean for requested URL($url)")
        Future(())
    }
  }

  private def save(url: String): Future[UrlShortenResult] = (
    for {
      code <- shortCodeService.crateFor(url)
      _ <- dataStore.save[String](codeAsKey(code), url)
      _ <- dataStore.save[String](urlAsKey(urlsafeEncode64(url)), code)
    } yield UrlShortenResult(code, opStatus = EntityOp.Created)).recover({
      case ex: Throwable =>
        logger.error("An error occurred while saving shortened url record.", ex)
        UrlShortenResult("", opStatus = EntityOp.Failed)
    })

  private def purge(code: String, url: String): Unit = {
    val (_, _, _) = parallel(
      dataStore.delete(codeAsKey(code)),
      dataStore.delete(urlAsKey(urlsafeEncode64(url))),
      dataStore.delete(urlAsStatsKey(urlsafeEncode64(url))))
    logger.debug(s"All records are purged for URL($url)")
  }

  private def predicate(condition: Boolean)(fail: Exception): Future[Unit] =
    if (condition) Future(()) else Future.failed(fail)

  private def getByUrl(url: String): Future[Option[String]] = dataStore.get[String](urlAsKey(urlsafeEncode64(url)))
}
