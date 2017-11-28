name := "Reduc.io"
organization := "org.reducio"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.4"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= {
  val circeVersion = "0.8.0"
  val akkaVersion = "10.0.10"
  val gatlingVersion = "2.3.0"
  val specs2Version = "4.0.0"
  val scalaTestVersion = "3.0.4"

  Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,

    "com.typesafe.akka" %% "akka-http" % akkaVersion,
    "de.heikoseeberger" %% "akka-http-circe" % "1.18.0",

    //Logging
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

    //    "org.slf4j" % "slf4j-nop" % "1.7.22",
    "com.github.etaty" %% "rediscala" % "1.8.0",

    "org.scalactic" %% "scalactic" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-mock" % specs2Version % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",

    "io.gatling" % "gatling-test-framework" % gatlingVersion % "test,it",
    "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
  )
}

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.dice",
  scalaVersion := "2.12.4",
  test in assembly := {}
)

Revolver.settings
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

enablePlugins(GatlingPlugin)
javaOptions in Gatling := overrideDefaultJavaOptions("-Xms1024m", "-Xmx2048m")

fork in run := true