ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"
ThisBuild / name := "UsageBasedBilling"
ThisBuild / organization := "com.isaacp"

lazy val core = project in file("modules/core")
lazy val useCases = (project in file("modules/useCases")).dependsOn(core)

lazy val root = (project in file(".")).aggregate(core, useCases).dependsOn(useCases)
  .settings(
    name := "UsageBasedBilling",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fansi" % "0.4.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    )
  )
