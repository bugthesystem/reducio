package org.reducio.services

import com.typesafe.scalalogging.LazyLogging
import org.reducio.models.{ EntityOperations, Stats, UrlShortenRequest, UrlShortenResult }
import org.reducio.persistence.DataStore
import org.reducio.util.KeyUtils.{ codeAsKey, urlAsKey, urlAsStatsKey }
import org.reducio.util.urlSafeEncode64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

trait UrlShortenerService {
  def shorten(request: UrlShortenRequest): Future[UrlShortenResult]

  def get(code: String): Future[Option[String]]

  def stats(url: String): Future[Option[Stats]]

  def clean(url: String): Future[Boolean]
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
          Future.successful(UrlShortenResult(code, status = EntityOperations.EntityFound))
        case None => save(request.url)
      }
    } yield result
  }

  override def get(code: String): Future[Option[String]] = {

    logger.debug(s"Access requested for shortened URL using code: $code")

    (for {
      exists <- dataStore.exists(codeAsKey(code))
      _ <- predicate(exists)(new Exception("Key not found in data store"))
      result <- dataStore.get[String](codeAsKey(code))
      _ <- result match {
        case Some(url) => statsService.hit(urlAsStatsKey(urlSafeEncode64(url)))
        case None => Future(())
      }
    } yield result).recover({
      case ex: Exception =>
        logger.error(s"An error occurred while getting URL by given code:$code", ex)
        None
    })
  }

  override def stats(url: String): Future[Option[Stats]] = {
    statsService.getStats(urlAsStatsKey(urlSafeEncode64(url))) map {
      statsOpt: Option[Long] =>
        statsOpt match {
          case Some(callCount) => Some(Stats(callCount))
          case None => None
        }
    }
  }

  override def clean(url: String): Future[Boolean] = {
    for {
      urlOpt: Option[String] <- dataStore.get[String](urlAsKey(urlSafeEncode64(url)))
      result <- urlOpt match {
        case Some(code) => purge(code, url)
        case _ =>
          logger.debug(s"There is no record to clean for requested URL($url)")
          Future(false)
      }
    } yield result
  }

  private def save(url: String): Future[UrlShortenResult] = (
    // TODO: Collision handling
    // Since I perform a CPU intensive io-free
    // (distributed cache, snowflake service etc) algorithm to calculate
    // short-code we -might- create the same code for different URLs
    // So it requires to follow another strategy (distributed counter, snowflake etc)
    // when we hit to this case. This will add more complexity by mixing approaches
    // but it will keep performing well for the collision-free case.

    for {
      code <- shortCodeService.create(url)
      _ <- dataStore.save[String](codeAsKey(code), url)
      _ <- dataStore.save[String](urlAsKey(urlSafeEncode64(url)), code)
    } yield UrlShortenResult(code, status = EntityOperations.EntityCreated)).recover({
      case ex: Throwable =>
        logger.error("An error occurred while saving shortened url record.", ex)
        UrlShortenResult("", status = EntityOperations.OperationFailed)
    })

  private def purge(code: String, url: String): Future[Boolean] = {
    logger.debug(s"All records will be purged for URL($url)")
    (for {
      _ <- dataStore.delete(codeAsKey(code))
      _ <- dataStore.delete(urlAsKey(urlSafeEncode64(url)))
      _ <- dataStore.delete(urlAsStatsKey(urlSafeEncode64(url)))
    } yield true).recover({
      case _: Throwable => false
    })
  }

  private def predicate(condition: Boolean)(fail: Exception): Future[Unit] =
    if (condition) Future(()) else Future.failed(fail)

  private def getByUrl(url: String): Future[Option[String]] = dataStore.get[String](urlAsKey(urlSafeEncode64(url)))
}
