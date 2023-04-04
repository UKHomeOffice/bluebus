
name := "bluebus"

ThisBuild / version := "v" + sys.env.getOrElse("DRONE_BUILD_NUMBER", sys.env.getOrElse("BUILD_ID", "DEV"))

<<<<<<< HEAD
lazy val scala = "2.13.10"

ThisBuild / scapegoatVersion := "2.1.1"
=======
val scala212 = "2.12.8"

ThisBuild / scapegoatVersion := "1.4.2"
scalaVersion := scala212
>>>>>>> c52a942 (DRTII-1155 using scala version and scapegoat available)

name := "bluebus"
organization := "uk.gov.homeoffice"
organizationName := "UK Home Office"
description := "Forked from https://github.com/sothach/bluebus"

<<<<<<< HEAD
=======
resolvers += "Artifactory Realm" at "https://artifactory.digital.homeoffice.gov.uk/"
resolvers += "Artifactory Realm sonatype cache" at "https://artifactory.digital.homeoffice.gov.uk/artifactory/sonatype-release-cache/"
resolvers += "Secured Central Repository" at "https://repo1.maven.org/maven2"
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
>>>>>>> c52a942 (DRTII-1155 using scala version and scapegoat available)

val artifactory = "https://artifactory.digital.homeoffice.gov.uk/"


libraryDependencies ++= Seq(
  "org.dispatchhttp" %% "dispatch-core" % "1.2.0",
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "net.jadler" % "jadler-all" % "1.3.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
)

publishTo := Some("release" at artifactory + "artifactory/libs-release")

trapExit := false
