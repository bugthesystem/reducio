package org.reducio.services

import java.nio.{ ByteBuffer, CharBuffer }
import java.nio.charset.{ CharacterCodingException, Charset, StandardCharsets }
import java.security.MessageDigest
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShortCodeService {
  def create(url: String): Future[String]
}

class DefaultShortCodeService extends ShortCodeService {

  override def create(url: String): Future[String] = {

    //We can get short code for url applying following approaches
    // 1. Using counter (Long) in some distributed store,
    //    convert to string with radix ie.32 and use it as short code
    //    However this requires locking the counter which originally stored in a
    //    distributed env: distributed locking is very costly. Also It causes a call on network for each request
    // 2. Calculating short code as following with cpu intensive approach without io call
    //    • get md5 hash of url as byte array
    //    • get last 4-bytes (32 bits)
    //    • pack it into a string using UTF-8 (in java default is UTF-16)
    //       ❯ try to read UTF-8 character otherwise format as constant-width hex representation
    //    • base64 encode the string and slice trailing junk from end

    Future {
      val md5Bytes = md5(url)

      val byteArr = md5Bytes.slice(12, 16)

      val builder = new StringBuilder()
      byteArr.foreach { r =>
        val bytes = Array(r)
        isValidUTF8(bytes) match {
          case Some(charBuffer) => builder.append(charBuffer)
          case None => builder.append("\\x%02X ".format(r))
        }
      }

      val utf8Str = builder.toString().replaceAll(" ", "")
      base64(utf8Str).dropRight(2)
    }
  }

  def isValidUTF8(input: Array[Byte]): Option[CharBuffer] = {
    val cs = Charset.forName("UTF-8").newDecoder
    try {
      Some(cs.decode(ByteBuffer.wrap(input)))
    } catch {
      case _: CharacterCodingException => None
    }
  }

  private def md5(str: String) = MessageDigest.getInstance("MD5").digest(str.getBytes)

  private def base64(str: String): String = Base64.getEncoder.encodeToString(str.getBytes(StandardCharsets.UTF_8))
}
