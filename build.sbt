ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name                               := "Lettuzz",
    scalacOptions                      := Seq("-feature"),
    idePackagePrefix                   := Some("rocks.effect.early.lettuzz"),
    libraryDependencies += "dev.zio"   %% "zio"          % "2.0.2",
    libraryDependencies += "dev.zio"   %% "zio-test"     % "2.0.2" % "test",
    libraryDependencies += "io.lettuce" % "lettuce-core" % "6.2.0.RELEASE",
  )
