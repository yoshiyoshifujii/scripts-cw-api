ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val CirceVersion = "0.14.5"
val SttpVersion  = "4.0.0-M8"
val PekkoVersion = "1.0.2"

lazy val root = (project in file("."))
  .settings(
    name := "scripts-cw-api",
    libraryDependencies ++= Seq(
      "io.circe"                      %% "circe-generic"  % CirceVersion,
      "com.softwaremill.sttp.client4" %% "core"           % SttpVersion,
      "com.softwaremill.sttp.client4" %% "circe"          % SttpVersion,
      "com.softwaremill.sttp.client4" %% "okhttp-backend" % SttpVersion,
      "org.apache.pekko"              %% "pekko-stream"   % PekkoVersion
    )
  )
