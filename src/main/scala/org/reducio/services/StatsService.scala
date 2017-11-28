package org.reducio.services

import org.reducio.persistence.DataStore
import scala.concurrent.Future

trait StatsService {
  def hit(key: String): Future[Long]
  def getStats(key: String): Future[Option[Long]]
}

class DefaultStatsService(dataStore: DataStore) extends StatsService {
  override def hit(key: String): Future[Long] = {
    dataStore.incr(key)
  }

  override def getStats(key: String): Future[Option[Long]] = {
    dataStore.get[Long](key)
  }
}