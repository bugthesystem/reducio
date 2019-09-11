name := "Reduc.io"
organization := "org.reducio"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.4"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= {
  val circeVersion = "0.12.1"
  val akkaVersion = "10.0.10"
  
  val gatlingVersion = "2.3.1"
  val specs2Version = "4.6.0"
  val scalaTestVersion = "3.0.7"
  
  val akkaHttpCirceVersion = "1.18.0"
  val logbackVersion = "1.2.3"
  val typesafeScalaLoggingVersion = "3.7.2"
  val rediscalaVersion = "1.8.0"
  val embeddedRedisVersion = "0.3.0"

  Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,

    "com.typesafe.akka" %% "akka-http" % akkaVersion,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion,

    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % typesafeScalaLoggingVersion,

    "com.github.etaty" %% "rediscala" % rediscalaVersion,

    "org.scalactic" %% "scalactic" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-mock" % specs2Version % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
    "com.github.sebruck" %% "scalatest-embedded-redis" % embeddedRedisVersion % "test",

    "io.gatling" % "gatling-test-framework" % gatlingVersion % "test,it",
    "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test,it"
  )
}

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "org.reducio",
  scalaVersion := "2.12.4",
  test in assembly := {}
)

Revolver.settings
enablePlugins(JavaAppPackaging)
enablePlugins(GatlingPlugin)

coverageExcludedFiles := ".*Main.*;.*Config.*"
javaOptions in Gatling := overrideDefaultJavaOptions("-Xms1024m", "-Xmx2048m")