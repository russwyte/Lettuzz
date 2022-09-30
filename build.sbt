ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

val zioVersion = "2.0.2"

lazy val root = (project in file("."))
  .settings(
    name                                          := "Lettuzz",
    scalacOptions                                 := Seq("-feature"),
    idePackagePrefix.withRank(KeyRanks.Invisible) := Some("rocks.effect.early.lettuzz"),
    libraryDependencies ++= Seq(
      "io.lettuce"         % "lettuce-core"   % "6.2.0.RELEASE",
      "dev.zio"           %% "zio"            % zioVersion,
      "dev.zio"           %% "zio-test"       % zioVersion % Test,
      "dev.zio"           %% "zio-test-sbt"   % zioVersion % Test,
      "org.testcontainers" % "testcontainers" % "1.17.3"   % Test,
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    Test / fork := true,
  )
