// The build of the template itself. `sbt test` (or `sbt g8Test`) renders src/main/g8 with its defaults into
// target/sbt-test and runs the commands in src/test/g8/test there.
lazy val root = (project in file("."))
  .settings(
    name := "sbt-template",
    Test / Keys.test := {
      val _ = (Test / g8Test).toTask("").value
    },
    // The generated build compiles Scala 3; sbt's default heap is tight for that.
    scriptedLaunchOpts ++= List("-Xms1024m", "-Xmx2048m", "-Xss2m", "-Dfile.encoding=UTF-8"),
  )
