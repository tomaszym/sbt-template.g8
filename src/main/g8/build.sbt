val catsVersion  = "2.13.0"
val circeVersion = "0.14.15"
val munitVersion = "1.3.5"

ThisBuild / scalaVersion := "$scala_version$"
ThisBuild / organization := "$organization$"
// No `version :=` here: sbt-dynver reads it from `git describe`, so a release build is a tag and nothing else can be.

// A suite that costs minutes is tagged `slow` and left out of the development run. `$slow_env$=1 sbt test`
// runs everything, and CI should set it.
ThisBuild / Test / testOptions ++=
  (if (sys.env.contains("$slow_env$")) Nil else Seq(Tests.Argument("--exclude-tags=slow")))

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wunused:all",
  // A non-exhaustive match over a sum is a correctness bug rather than a style note. As a warning it scrolls
  // past and turns up later as a MatchError at run time.
  "-Wconf:msg=match may not be exhaustive:e",
  // The compiler rejects indentation syntax, so braces hold even where the formatter never runs.
  "-no-indent",
)

lazy val common = Seq(
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  // Nothing here is published, and without this `app/stage` builds scaladoc for every module on its way to a
  // directory that will never hold it.
  Compile / packageDoc / publishArtifact := false,
)

lazy val root = project
  .in(file("."))
  .aggregate(core, app)
  .settings(
    name           := "$name;format="norm"$",
    publish / skip := true,
  )

// The library: types and their circe codecs. Knows nothing about how it is run.
lazy val core = project
  .in(file("core"))
  .settings(common)
  .settings(
    name := "$name;format="norm"$-core",
    // No circe-generic: every codec is written next to its type (.claude/skills/code).
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core"   % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
    ),
  )

// The program. `app/stage` writes app/target/universal/stage: lib/ with every jar and bin/ with a start script.
lazy val app = project
  .in(file("app"))
  .enablePlugins(JavaAppPackaging)
  .dependsOn(core % "compile->compile;test->test")
  .settings(common)
  .settings(
    name                          := "$name;format="norm"$-app",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion,
    Compile / mainClass           := Some("$package$.Main"),
    executableScriptName          := "$name;format="norm"$",
    Compile / run / fork          := true,
    // Forked runs default to the module directory; relative paths given to `run` are the repo's.
    Compile / run / baseDirectory := (ThisBuild / baseDirectory).value,
    // Without this a forked run cannot read what you type.
    Compile / run / connectInput  := true,
  )
