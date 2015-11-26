package io.github.rvvincelli.blogpost.playwithkafka.domain

import java.text.ParseException
import argonaut._
import argonaut.Argonaut._
import org.apache.kafka.common.serialization.Serializer
import kafka.serializer.{Decoder, StringDecoder}
import java.util.{Map => JMap}

trait ArgonautSerializer[A] extends Serializer[A] {

  def encoder: EncodeJson[A]

  def serialize(topic: String, data: A) = encoder.encode(data).toString().getBytes

  def configure(configs: JMap[String, _], isKey: Boolean) = () 
  def close() = () 

}

trait ArgonautDecoder[A] extends Decoder[A] {

  def decoder: DecodeJson[A]
  
  def fromBytes(data: Array[Byte]) = Parse.decode(new String(data))(decoder).getOrElse { throw new ParseException(s"Invalid JSON: ${new String(data)}", 0) }

}

trait Codecs {

  protected val richEventCodec = casecodec1(RichEvent.apply, RichEvent.unapply)("content")

}

class RichEventSerializer extends ArgonautSerializer[RichEvent] with Codecs {
  def encoder = richEventCodec.Encoder
}

class RichEventDeserializer extends ArgonautDecoder[RichEvent] with Codecs {
  def decoder = richEventCodec.Decoder
}
