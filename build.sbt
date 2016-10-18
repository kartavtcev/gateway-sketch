name := "gateway-sketch"
organization := "Sergei Kartavtcev"
version := "1.0.0"
scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.10"
  val scalaTestV = "3.0.0"
  val circeV = "0.5.1"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "de.heikoseeberger" %% "akka-http-circe" % "1.6.0",


    "org.slf4j" % "slf4j-nop" % "1.6.4",

    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV,

    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test"
  )
}