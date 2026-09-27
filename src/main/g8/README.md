# $name$

Scala $scala_version$ on the JVM, Java and sbt pinned in `.sdkmanrc`.

| Module | What it is | Depends on |
|---|---|---|
| `core` | the library: types and their circe codecs, written by hand | - |
| `app` | the program: `Main`, staged as a start script by sbt-native-packager | core |

```bash
sdk env install                   # the Java and sbt named in .sdkmanrc
sbt test                          # the suites, without the ones tagged slow
$slow_env$=1 sbt test             # all of them; what CI should run
sbt "app/run 1 2 3"               # {"count":3,"mean":2.0}
sbt app/stage                     # -> app/target/universal/stage/bin/$name;format="norm"$
sbt scalafmtAll scalafmtSbt       # format
```

`version` comes from `git describe` (sbt-dynver): tag `v1.2.3` and the build is `1.2.3`.
