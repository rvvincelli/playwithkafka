package io.github.rvvincelli.blogpost

import sbt._
import Keys._
import play.Play.autoImport._
import play.PlayScala

object PlayWithKafkaBuild extends Build {

  lazy val app: Project = Project(
    id = "play-with-kafka",
    base = file(".")
  ).enablePlugins(PlayScala).settings(
    version      := "1.0",
    scalaVersion := "2.10.5",
    resolvers += "cloudera" at "https://repository.cloudera.com/artifactory/repo/",
    libraryDependencies ++= Seq(
       "org.apache.kafka" %% "kafka"                 % Versions.kafkaVersion        % "compile" withSources() ,
       "io.argonaut"      %% "argonaut"              % Versions.argonautVersion     % "compile" withSources()
     )
  )

}