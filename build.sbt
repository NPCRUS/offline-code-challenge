name := "offline-code-challenge"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.31",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.5",

  // for testing
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.31"  % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.11"  % "test"
)

