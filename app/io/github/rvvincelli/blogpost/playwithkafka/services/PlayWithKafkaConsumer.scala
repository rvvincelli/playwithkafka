package io.github.rvvincelli.blogpost.playwithkafka.services

import java.util.Properties

import scala.concurrent.Future
import scala.util.Try

import akka.actor.{ ActorLogging, ActorRef }

import play.api.libs.json.{ Json, JsArray }

import kafka.consumer.{ Consumer, ConsumerConfig, KafkaStream }
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.{ConsumerConfig => ConsumerConf}

import io.github.rvvincelli.blogpost.playwithkafka.domain.RichEvent
import io.github.rvvincelli.blogpost.playwithkafka.domain.{RichEventDeserializer, RichEventSerializer}
import io.github.rvvincelli.blogpost.playwithkafka.models.KafkaActor

class PlayWithKafkaConsumer(val consumerSettings: Properties, val topics: Set[String], client: ActorRef, batchSize: Int) extends KafkaActor with ActorLogging {
  
  log.info("Batch size is: "+ batchSize)
  log.info(if (consumerSettings.getProperty(ConsumerConf.AUTO_OFFSET_RESET_CONFIG) == "smallest") "Fetching all the messages on the topic" else "Fetching messages posted on the topic only from now on")
  
  private val reDe = new RichEventDeserializer()
  private val reSer = new RichEventSerializer()
  
  protected lazy val consumer = Consumer.create(new ConsumerConfig(consumerSettings))

  private lazy val topicThreads = topics.map { _ -> 1 }.toMap[String, Int]
  private lazy val streams = consumer.createMessageStreams[String, RichEvent](topicThreads, new StringDecoder(), reDe).toList
  
  protected implicit val ec = context.dispatcher
  
  protected def streamTopic(topic: String, stream: KafkaStream[String, RichEvent]) = Future {
    log.info(s"Opening partition listener on topic [$topic]")
    stream.iterator().grouped(batchSize).foreach { mnms =>
      lazy val events = mnms.map { _.message() }
      log.info("Sending a messages batch...")
      client ! JsArray { events.map { event => Json.parse(reSer.encoder.encode(event).toString) } }
    }
  }
  
  override def receive = {
    case `trigger` => streams.foreach{case (topic, messages) => messages.foreach(streamTopic(topic, _))}
  }

}
