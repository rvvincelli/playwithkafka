package io.github.rvvincelli.blogpost.playwithkafka.models

import scala.concurrent.duration._
import akka.actor.Actor
import scala.concurrent.ExecutionContext

trait KafkaActor extends Actor {

  protected final val trigger = "start"
  
  protected implicit def ec: ExecutionContext
  
  protected def topics: Set[String]
  
  override def preStart() {
    context.system.scheduler.scheduleOnce (0.milliseconds) { self ! trigger }
  }

  
}
