package org.reducio

import io.circe._

package object models {

  implicit val shortenRequestDecoder: Decoder[UrlShortenRequest] = Decoder.forProduct1("url")(UrlShortenRequest.apply)

  implicit val statsEncoder: Encoder[Stats] = Encoder.forProduct1("callCount")(s => s.callCount)
  implicit val statsDecoder: Decoder[Stats] = Decoder.forProduct1("callCount")(Stats.apply)

  case class UrlShortenRequest(url: String)

  case class Stats(callCount: Long)

  object EntityOp extends Enumeration {
    type OpStatus = Value
    val Created, Found, Failed = Value
  }

  case class UrlShortenResult(code: String, opStatus: EntityOp.OpStatus)

}
