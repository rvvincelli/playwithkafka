package io.github.rvvincelli.blogpost.playwithkafka
package controllers

import java.util.{ Properties, UUID }
import scala.util.{ Failure, Success }
import akka.actor.Props
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.Play.current
import kafka.config.KafkaConsumerParams
import io.github.rvvincelli.blogpost.playwithkafka.services.PlayWithKafkaConsumer

object PlayWithKafkaConsumerApp extends Controller with KafkaConsumerParams {

  private def withKafkaConf[A](groupId: String, clientId: String, fromFirstMessage: Boolean)(f: Properties => A): A = {
    val offsetPolicy = if (fromFirstMessage) "smallest" else "largest"
    f { consumerProperties(groupId = groupId, clientId = clientId, offsetPolicy = offsetPolicy) }
  }

  def streamKafkaTopic(topics: String, batchSize: Int, fromFirstMessage: Boolean) = WebSocket.acceptWithActor[String, JsValue] { _ => out =>
    Props {
      val id = UUID.randomUUID().toString
      withKafkaConf(s"wsgroup-$id", s"wsclient-$id", fromFirstMessage) {
        new PlayWithKafkaConsumer(_, topics.split(',').toSet, out, batchSize)
      }
    }
  }
  
}
