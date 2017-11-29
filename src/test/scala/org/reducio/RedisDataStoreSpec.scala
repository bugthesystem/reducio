package org.reducio

import akka.actor.ActorSystem
import com.github.sebruck.EmbeddedRedis
import org.reducio.persistence.RedisDataStore
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class RedisDataStoreSpec extends WordSpec with Matchers with EmbeddedRedis with BeforeAndAfterAll {

  //This actor system will be used by `rediscala` Redis Client
  private implicit val actorSystem: ActorSystem = ActorSystem()

  override def afterAll(): Unit = actorSystem.terminate()

  "Service" should {
    "save key/value" in {
      withRedis() { port =>
        val key = "key"
        val value = "value"

        val redisDataStore = new RedisDataStore(host = "localhost", port = port)

        val resultFuture: Future[Boolean] = redisDataStore.save[String](key, value)
        val result: Boolean = Await.result(resultFuture, 5.seconds)

        result shouldEqual true
      }
    }

    "get value for key" in {
      withRedis() { port =>
        val key = "key"
        val value = "value"

        val redisDataStore = new RedisDataStore(host = "localhost", port = port)

        val saveFuture: Future[Boolean] = redisDataStore.save[String](key, value)
        val saveResult: Boolean = Await.result(saveFuture, 5.seconds)

        val getFuture: Future[Option[String]] = redisDataStore.get[String](key)
        val getResult: Option[String] = Await.result(getFuture, 5.seconds)

        saveResult shouldEqual true
        getResult.get shouldEqual value
      }
    }

    "check existence for key" in {
      withRedis() { port =>
        val key = "key"
        val value = "value"

        val redisDataStore = new RedisDataStore(host = "localhost", port = port)

        val saveFuture: Future[Boolean] = redisDataStore.save[String](key, value)
        val saveResult: Boolean = Await.result(saveFuture, 5.seconds)

        val existsFuture: Future[Boolean] = redisDataStore.exists(key)
        val existsResult: Boolean = Await.result(existsFuture, 5.seconds)

        saveResult shouldEqual true
        existsResult shouldEqual true
      }
    }
    "delete key" in {
      withRedis() { port =>
        val key = "key"
        val value = "value"

        val redisDataStore = new RedisDataStore(host = "localhost", port = port)

        val saveFuture: Future[Boolean] = redisDataStore.save[String](key, value)
        val saveResult: Boolean = Await.result(saveFuture, 5.seconds)

        val deleteFuture: Future[Long] = redisDataStore.delete(key)
        val _ = Await.result(deleteFuture, 5.seconds)

        val getFuture: Future[Option[String]] = redisDataStore.get[String](key)
        val getResult: Option[String] = Await.result(getFuture, 5.seconds)

        saveResult shouldEqual true
        getResult.isEmpty shouldEqual true
      }
    }

    "incr key" in {
      withRedis() { port =>
        val key = "key"
        val value = 1L

        val redisDataStore = new RedisDataStore(host = "localhost", port = port)

        val saveFuture: Future[Boolean] = redisDataStore.save[Long](key, value)
        val saveResult: Boolean = Await.result(saveFuture, 5.seconds)

        val incrFuture: Future[Long] = redisDataStore.incr(key)
        val incrResult = Await.result(incrFuture, 5.seconds)

        saveResult shouldEqual true
        incrResult shouldEqual 2L
      }
    }
  }
}
