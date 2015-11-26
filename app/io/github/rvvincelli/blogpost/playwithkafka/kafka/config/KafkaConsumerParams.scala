package io.github.rvvincelli.blogpost.playwithkafka.kafka.config

import java.util.Properties
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
import org.apache.kafka.clients.consumer.ConsumerConfig

trait KafkaConsumerParams {

  protected def consumerParams(groupId: String,
                               clientId: String,
                               autoCommit: Boolean,
                               autoCommitInterval: FiniteDuration,
                               offsetPolicy: String) = Map[String, String](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG       -> "kafkabroker:9092",
    ConsumerConfig.GROUP_ID_CONFIG                -> groupId,
    ConsumerConfig.CLIENT_ID_CONFIG               -> clientId,
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG      -> autoCommit.toString,
    ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> autoCommitInterval.toMillis.toString,
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG       -> offsetPolicy
  )

  protected def consumerProperties(groupId: String = "group-id",
                                   clientId: String = "client-id",
                                   autoCommit: Boolean = true,
                                   autoCommitInterval: FiniteDuration = new FiniteDuration(10, MILLISECONDS),
                                   offsetPolicy: String = "smallest") =
    consumerParams(groupId, clientId, autoCommit, autoCommitInterval, offsetPolicy).foldLeft(new Properties()) { (props, pair) =>
      props.setProperty(pair._1, pair._2)
      props
    }

}
