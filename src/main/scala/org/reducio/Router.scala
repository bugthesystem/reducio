package org.reducio

import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.reducio.Routes.{ GetShortenedRoute, ShortenRoute, StatsRoute }
import org.reducio.services.UrlShortenerService

class Router(urlService: UrlShortenerService)
  extends Directives
  with FailFastCirceSupport
  with HttpConfig {
  val routes: Route =
    ShortenRoute(urlService).routes ~
      GetShortenedRoute(urlService).routes ~
      StatsRoute(urlService).routes
}
