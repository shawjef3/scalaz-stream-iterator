organization := "me.jeffshaw.scalaz.stream"

name := "iterator"

version := "0.0a"

libraryDependencies ++= Seq(
  "org.scalaz.stream" %% "scalaz-stream" % "0.7.2a"
)

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5")
