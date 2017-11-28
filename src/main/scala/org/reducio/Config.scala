package org.reducio

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val redisConfig = config.getConfig("redis")

  val httpHost: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")

  val redisHost: String = redisConfig.getString("host")
  val redisPort: Int = redisConfig.getInt("port")
}
