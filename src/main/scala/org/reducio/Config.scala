package org.reducio

import com.typesafe.config
import com.typesafe.config.ConfigFactory

trait BaseConfig {
  protected[this] val conf: config.Config = ConfigFactory.load()
}

trait HttpConfig extends BaseConfig {
  private val httpConfig = conf.getConfig("http")
  val httpHost: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")

  val useHttps: Boolean = httpConfig.getBoolean("use-https")
}

trait RedisConfig extends BaseConfig {
  private val redisConfig = conf.getConfig("redis")

  val redisHost: String = redisConfig.getString("host")
  val redisPort: Int = redisConfig.getInt("port")
}
