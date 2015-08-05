organization := "me.jeffshaw.scalaz.stream"

name := "iterator"

version := "0.0a"

libraryDependencies ++= Seq(
  "org.scalaz.stream" %% "scalaz-stream" % "0.7.2a",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"
)

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5")
