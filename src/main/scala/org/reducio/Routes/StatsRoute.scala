package org.reducio.Routes

import java.net.URL

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._
import org.reducio.HttpConfig
import org.reducio.models.Stats
import org.reducio.services.UrlShortenerService

case class StatsRoute(urlService: UrlShortenerService)
  extends Directives
  with FailFastCirceSupport
  with HttpConfig {

  val routes: Route =
    pathPrefix("stats"./) {
      parameter('url) { url =>
        try {
          val uri = new URL(url)
          onSuccess(urlService.stats(uri.toString)) {
            case Some(stats: Stats) =>
              complete(OK, stats.asJson)
            case None => complete(NotFound)
          }
        } catch {
          case _: Throwable =>
            complete(BadRequest)
        }
      }
    }
}

