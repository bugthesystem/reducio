package org.reducio.services

import java.nio.charset.{ Charset, StandardCharsets }
import java.security.MessageDigest
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShortCodeService {
  def crateFor(url: String): Future[String]
}

class DefaultShortCodeService extends ShortCodeService {
  private val Regex = """/==\n?$/""".r

  override def crateFor(url: String): Future[String] = {

    //We can get short code for url applying at least 2 approaches
    // 1. Using counter (Long) in some distributed store, 
    //    convert to string with radix ie.32 and use it as short code
    //    but this will increase network calls to retrieve counter
    // 2. Calculating short code as following with cpu intensive approach whitout io call
    //    • get md5 hash of url as byte array
    //    • get last 4-bytes (32 bits)
    //    • pack it into a string using UTF-8 (in java default is UTF-16)
    //       ❯ try to read UTF-8 character otherwise format as constant-width hex representation
    //    • base64 encode the string and slice trailing junk from end
    //

    Future {
      val md5Bytes = md5(url)

      val byteArr = md5Bytes.slice(12, 16)

      val builder = new StringBuilder()
      byteArr.foreach { r =>
        val tmpString = new String(Array(r), Charset.forName("UTF-8"))
        val c = tmpString.toCharArray()(0)
        if (c == '�' || c == '')
          builder.append("\\x%02X ".format(r))
        else
          builder.append(c)
      }

      val utf8Str = builder.toString().replaceAll(" ", "")
      base64(utf8Str).dropRight(2)
    }
  }

  private def md5(str: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(str.getBytes)

  private def base64(str: String): String = Base64.getEncoder.encodeToString(str.getBytes(StandardCharsets.UTF_8))
}
