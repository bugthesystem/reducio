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

  val routes: Route =
    pathPrefix("stats"./) {
      parameter('url) { url =>
        validateUri(url) match {
          case Some(uri) => onSuccess(urlService.stats(uri)) {
            case Some(stats: Stats) => complete(OK, stats.asJson)
            case None => complete(NotFound)
          }
          case None => complete(BadRequest)
        }
      }
    }
)
