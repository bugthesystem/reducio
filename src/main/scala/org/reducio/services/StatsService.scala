package org.reducio.services

import scala.concurrent.Future
import com.typesafe.scalalogging.LazyLogging
import org.reducio.persistence.DataStore
import scala.concurrent.Future

trait StatsService {
  def hit(key: String): Future[Long]
  def getStats(key: String): Future[Option[Long]]
}

class DefaultStatsService(dataStore: DataStore) extends StatsService with LazyLogging {
  override def hit(key: String): Future[Long] = {
    dataStore.incr(key)
  }

  override def getStats(key: String): Future[Option[Long]] = {
    dataStore.get[Long](key)
  }
}