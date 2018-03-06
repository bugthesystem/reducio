package org.reducio.persistence

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RedisDataStore(host: String, port: Int)(implicit val actorSystem: ActorSystem)
  extends DataStore with LazyLogging {

  val redis = RedisClient(host = host, port = port)

  override def save[T](key: String, obj: T)(implicit encoder: Encoder[T]): Future[Boolean] =
    redis.set(key, obj.asJson.noSpaces)

  override def get[T](key: String)(implicit decoder: Decoder[T]): Future[Option[T]] =
    redis.get(key).map(_.flatMap(v => decode[T](v.utf8String).toOption))

  override def delete(key: String): Future[Long] = redis.del(key)

  override def exists(key: String): Future[Boolean] = redis.exists(key)

  override def incr(key: String): Future[Long] = redis.incr(key)
}
