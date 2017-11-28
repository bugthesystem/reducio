package org.reducio

import java.net.URL

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directives, Route }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._
import org.reducio.models.{ EntityOp, Stats, UrlShortenRequest }
import org.reducio.services.UrlShortenerService

class Router(urlService: UrlShortenerService) extends Directives with FailFastCirceSupport with HttpConfig {

  private def https(url: String): String = if (useHttps) s"https://$url" else s"http://$url"

  val routes: Route =
    pathEndOrSingleSlash {
      post {
        formFieldMap { fields: Map[String, String] =>
          extractRequestContext { ctx =>
            try {
              val uri = new URL(fields("url"))
              onSuccess(urlService.shorten(UrlShortenRequest(url = uri.toString))) {
                result =>
                  {
                    result.opStatus match {
                      case EntityOp.Created =>
                        val url = s"${https(ctx.request.uri.authority.toString())}/${result.code}"
                        respondWithHeaders(List(RawHeader("Location", url))) {
                          complete(Created)
                        }
                      case EntityOp.Found =>
                        val url = s"${https(ctx.request.uri.authority.toString())}/${result.code}"
                        respondWithHeaders(List(RawHeader("Location", url))) {
                          complete(Found)
                        }
                      case EntityOp.Failed =>
                        complete(BadRequest)
                    }
                  }
              }
            } catch {
              case _: Throwable =>
                complete(BadRequest)
            }
          }
        }
      }
    } ~ path(Segment) { code =>
      get {
        onSuccess(urlService.get(code)) {
          case Some(url) =>
            respondWithHeaders(List(RawHeader("Location", url))) {
              complete(Found)
            }
          case None => complete(NotFound)
        }
      }
    } ~
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