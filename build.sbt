organization := "me.jeffshaw.scalaz.stream"

name := "iterator"

version := "1.0a"

libraryDependencies ++= Seq(
  "org.scalaz.stream" %% "scalaz-stream" % "0.7.2a",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"
)

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5")

licenses in ThisBuild := Seq("The MIT License (MIT)" -> url("http://opensource.org/licenses/MIT"))

homepage in ThisBuild := Some(url("https://github.com/shawjef3/scalaz-stream-iterator"))

pomExtra in ThisBuild :=
  <developers>
    <developer>
      <name>Jeff Shaw</name>
      <id>shawjef3</id>
      <url>https://github.com/shawjef3/</url>
    </developer>
  </developers>
    <scm>
      <url>git@github.com:shawjef3/scalaz-stream-iterator.git</url>
      <connection>git@github.com:shawjef3/scalaz-stream-iterator.git</connection>
    </scm>
