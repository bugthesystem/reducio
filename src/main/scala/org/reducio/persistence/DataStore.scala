package org.reducio.persistence

import io.circe._
import scala.concurrent.Future

trait DataStore {

  def save[T](key: String, obj: T)(implicit encoder: Encoder[T]): Future[Boolean]

  def get[T](key: String)(implicit decoder: Decoder[T]): Future[Option[T]]

  def delete(key: String): Future[Long]

  def exists(key: String): Future[Boolean]
  def incr(key: String): Future[Long]
}