package org.reducio

import java.nio.charset.StandardCharsets
import java.util.Base64

package object util {
  def urlsafeEncode64(input: String): String =
    Base64.getUrlEncoder.encodeToString(input.getBytes(StandardCharsets.UTF_8))

  object KeyUtils {
    val UrlKeyTemplate = "reducio:url:%s"
    val CodeKeyTemplate = "reducio:code:%s"
    val StatsKeyTemplate = "reducio:stats:call:%s"

    def codeAsKey(code: String): String = CodeKeyTemplate.format(code)

    def urlAsKey(url: String): String = UrlKeyTemplate.format(url)

    def urlAsStatsKey(url: String): String = StatsKeyTemplate.format(url)
  }
}
