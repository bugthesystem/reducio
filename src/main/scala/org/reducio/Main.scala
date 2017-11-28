package org.reducio

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.reducio.persistence._
import org.reducio.services._
import scala.concurrent.ExecutionContext

object Main extends App with Config {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val dataStore = new RedisDataStore(redisHost, redisPort)
  val codeService = new DefaultShortCodeService()
  val statService = new DefaultStatsService(dataStore)
  val urlShortenerService = new DefaultUrlShortenerService(dataStore, codeService, statService)

  val router = new Router(urlShortenerService)

  Http().bindAndHandle(router.routes, httpHost, httpPort)
}
