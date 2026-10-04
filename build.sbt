ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.8"

lazy val root = (project in file("."))
  .settings(
    name := "learnScala",
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature"),
    // sbt run 默认进课程入口；图书馆用 runMain showcase.bookShelf
    Compile / mainClass := Some("main")
  )
