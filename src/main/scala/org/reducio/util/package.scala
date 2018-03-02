package org.reducio

import java.net.URL
import java.nio.{ ByteBuffer, CharBuffer }
import java.nio.charset.{ CharacterCodingException, Charset, StandardCharsets }
import java.security.MessageDigest
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object util {
  def urlSafeEncode64(input: String): String =
    Base64.getUrlEncoder.encodeToString(input.getBytes(StandardCharsets.UTF_8))

  def isValidUTF8(input: Array[Byte]): Option[CharBuffer] = {
    val cs = Charset.forName("UTF-8").newDecoder
    try {
      Some(cs.decode(ByteBuffer.wrap(input)))
    } catch {
      case _: CharacterCodingException => None
    }
  }

  def md5(str: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(str.getBytes)

  def base64(str: String): String = Base64.getEncoder.encodeToString(str.getBytes(StandardCharsets.UTF_8))

  object KeyUtils {
    val UrlKeyTemplate = "reducio:url:%s"
    val CodeKeyTemplate = "reducio:code:%s"
    val StatsKeyTemplate = "reducio:stats:call:%s"

    def codeAsKey(code: String): String = CodeKeyTemplate.format(code)

    def urlAsKey(url: String): String = UrlKeyTemplate.format(url)

    def urlAsStatsKey(url: String): String = StatsKeyTemplate.format(url)
  }

  object HttpUtils {
    def https(url: String, useHttps: Boolean): String = if (useHttps) s"https://$url" else s"http://$url"

    def validateUri(urlField: String): Future[Option[String]] = {
      try {
        val _ = new URL(urlField)
        Future(Some(urlField))
      } catch {
        case _: Throwable =>
          Future(None)
      }
    }
  }

}
