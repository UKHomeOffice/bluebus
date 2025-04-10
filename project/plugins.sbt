ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.12")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.2.10")
addDependencyTreePlugin
