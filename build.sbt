
name := "bluebus"

ThisBuild / version := "v" + sys.env.getOrElse("DRONE_BUILD_NUMBER", sys.env.getOrElse("BUILD_ID", "DEV"))

scalaVersion := "2.13.16"

name := "bluebus"
organization := "uk.gov.homeoffice"
organizationName := "UK Home Office"
description := "Forked from https://github.com/sothach/bluebus"

val artifactory = "https://artifactory.digital.homeoffice.gov.uk/"

val pekkoVersion = "1.1.3"
val pekkoHttpVersion = "1.1.0"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
  "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "net.jadler" % "jadler-all" % "1.3.1" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
)

publishTo := Some("release" at artifactory + "artifactory/libs-release")

trapExit := false
