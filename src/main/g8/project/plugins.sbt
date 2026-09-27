addSbtPlugin("org.scalameta"  % "sbt-scalafmt"        % "2.6.2")
// `app/stage` writes a directory of jars and a start script; a container image copies that.
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.7")
// `version` comes from `git describe`, so a release build is a tag and nothing else can be.
addSbtPlugin("com.github.sbt" % "sbt-dynver"          % "5.1.0")
