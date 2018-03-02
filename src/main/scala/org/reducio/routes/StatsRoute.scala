package org.reducio.routes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._
import org.reducio.HttpConfig
import org.reducio.models.Stats
import org.reducio.services.UrlShortenerService
import org.reducio.util.HttpUtils._

case class StatsRoute(urlService: UrlShortenerService)
  extends Directives
  with FailFastCirceSupport
  with HttpConfig {

  def validateAndGetStats(url: String): Future[Option[Stats]] = for {
    uriOpt: Option[String] <- validateUri(url)
    statsResult <- uriOpt match {
      case Some(uri) => urlService.stats(uri.toString)
      case None => Future(None)
    }
  } yield statsResult

  val routes: Route =
    pathPrefix("stats"./) {
      parameter('url) { url =>
        onSuccess(validateAndGetStats(url)) {
          case Some(stats: Stats) => complete(OK, stats.asJson)
          case None => complete(NotFound)
        }
      }
    }
}

