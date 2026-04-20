name := "scala-at-light-speed"

version := "0.1"

scalaVersion := "2.13.12"

// used dotenv-java for hiding API key in .env
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.13.12",
  "io.github.cdimascio" % "dotenv-java" % "2.2.4",
  "com.softwaremill.sttp.client3" %% "core" % "3.9.0", // HTTP client
  "com.lihaoyi" %% "upickle" % "3.1.3" // used for JSON parsing
)
