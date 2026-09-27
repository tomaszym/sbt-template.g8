# sbt-template

A [giter8](https://www.foundweekends.org/giter8/) template for a Scala 3 project on the JVM. It generates two
modules, `core` (a library with circe codecs) and `app` (a `Main` that depends on core and is staged as a start
script). Java and sbt are pinned in `.sdkmanrc`.

| | Version | Set in |
|---|---|---|
| Java (Temurin, via SDKMAN) | 25.0.4-tem | `src/main/g8/default.properties` → `.sdkmanrc` |
| sbt | 1.12.15 | `src/main/g8/default.properties` → `project/build.properties`, `.sdkmanrc` |
| Scala | 3.8.4 | `src/main/g8/default.properties` |
| circe (core, parser; no generic) | 0.14.15 | `src/main/g8/build.sbt` |
| cats-core | 2.13.0 | `src/main/g8/build.sbt` |
| munit | 1.3.5 | `src/main/g8/build.sbt` |
| scalafmt / sbt-scalafmt | 3.11.5 / 2.6.2 | `src/main/g8/.scalafmt.conf`, `src/main/g8/project/plugins.sbt` |
| sbt-native-packager | 1.11.7 | `src/main/g8/project/plugins.sbt` |
| sbt-dynver | 5.1.0 | `src/main/g8/project/plugins.sbt` |

---

## 1. New machine, once

### System packages

macOS (curl, zip and unzip ship with it; this installs git):

```bash
xcode-select --install
```

Fedora:

```bash
sudo dnf install -y git curl zip unzip
```

Debian / Ubuntu:

```bash
sudo apt-get update && sudo apt-get install -y git curl zip unzip
```

### SDKMAN, Java, sbt

Same on macOS and Linux. The installer adds SDKMAN to `~/.zshrc` and `~/.bashrc`, so new terminals have `sdk`.

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25.0.4-tem        # answer Y to "set as default"
sdk install sbt 1.12.15
# cd into a directory with a .sdkmanrc switches to the versions it names
echo "sdkman_auto_env=true" >> "$HOME/.sdkman/etc/config"
```

Check:

```bash
java -version          # Temurin 25.0.4
sbt --script-version   # 1.12.15
```

SDKMAN drops old Java builds after a while. If `25.0.4-tem` is gone, run `sdk list java | grep -- -tem`,
install the newest 25.x, and put that identifier in `java_version` in `src/main/g8/default.properties`.

### Optional: cellar

The generated `.claude/skills/code/SKILL.md` tells Claude to read dependency APIs with
[cellar](https://github.com/VirtusLab/cellar) from `~/.local/bin/cellar`. Same on macOS and Linux:

```bash
CELLAR=0.1.0-M13
case "$(uname -s)-$(uname -m)" in
  Darwin-arm64)  P=macos-arm64 ;;
  Darwin-x86_64) P=macos-x86_64 ;;
  Linux-aarch64) P=linux-aarch64 ;;
  Linux-x86_64)  P=linux-x86_64 ;;
esac
mkdir -p ~/.local/bin
tmp="$(mktemp -d)"
curl -fL "https://github.com/VirtusLab/cellar/releases/download/v$CELLAR/cellar-$CELLAR-$P.tar.gz" | tar xz -C "$tmp"
mv "$tmp/cellar" ~/.local/bin/ && rm -rf "$tmp"
~/.local/bin/cellar --version
```

It is about 240 MB. Newer releases are listed at https://github.com/VirtusLab/cellar/releases.

### Optional: VS Code with Metals

```bash
code --install-extension scalameta.metals
```

---

## 2. New project

### From a local copy of this directory

The template has to be on the machine, here at `~/git/tomaszym/sbt-template` (clone it or copy it there).
`sbt new` creates the project in a subdirectory of the current directory, named after `--name`:

```bash
cd ~/git
sbt new file://$HOME/git/tomaszym/sbt-template --name=my-app
```

It asks for each parameter you did not pass; Enter keeps the default in brackets. Then:

```bash
cd my-app
git init && git add -A && git commit -m "build: new project from sbt-template"
sdk env install        # installs anything .sdkmanrc names that is missing
sbt test
```

### From GitHub, without a clone

Push this directory to GitHub as a repository whose name ends in `.g8`, for example `tomaszym/sbt-template.g8`.
From then on, on any machine:

```bash
sbt new tomaszym/sbt-template.g8 --name=my-app
```

### Parameters

Pass any of them as `--key=value` to skip its prompt.

| Parameter | Default | What it becomes |
|---|---|---|
| `name` | `my-project` | directory, sbt project names, start script name |
| `organization` | `link.azeno` | `ThisBuild / organization` |
| `package` | `<organization>.<name>` (e.g. `link.azeno.myproject`) | root package of the sources |
| `scala_version` | `3.8.4` | `ThisBuild / scalaVersion` |
| `sbt_version` | `1.12.15` | `project/build.properties`, `.sdkmanrc` |
| `java_version` | `25.0.4-tem` | `.sdkmanrc` |
| `slow_env` | `<NAME>_SLOW` (e.g. `MY_APP_SLOW`) | env var that turns on the suites tagged slow |

---

## 3. In the generated project

```bash
sbt test                          # the suites, without the ones tagged slow
MY_APP_SLOW=1 sbt test            # all of them; what CI should run (the variable is slow_env)
sbt "app/run 1 2 3"               # {"count":3,"mean":2.0}
sbt app/stage                     # -> app/target/universal/stage/bin/my-app
sbt scalafmtAll scalafmtSbt       # format
sbt scalafmtCheckAll scalafmtSbtCheck   # check formatting, for CI
```

What it contains:

```
my-app/
├── .sdkmanrc                 java and sbt versions
├── .scalafmt.conf            braces only, 150 columns
├── CLAUDE.md                 working in small steps; Conventional Commits in English, no Claude trailer
├── .claude/skills/code/      rules for Scala code: types without invalid states, opaque ids, codecs by hand, cellar
├── build.sbt
├── project/plugins.sbt       scalafmt, native-packager, dynver
├── core/src/{main,test}/scala/<package>/core/   Mean, Summary (circe codecs written by hand)
└── app/src/{main,test}/scala/<package>/         Main
```

The choices behind it:

- **Braces, not indentation.** `-no-indent` makes the compiler reject indentation syntax, and scalafmt is set to
  the same dialect.
- **A non-exhaustive match is a compile error** (`-Wconf:msg=match may not be exhaustive:e`), and so is
  nothing else by default; `-Wunused:all` stays a warning.
- **circe without circe-generic.** Every `Encoder`/`Decoder` is written next to its type with `forProductN` or
  by hand, never `derives` or `generic.auto`. The field names on the wire are a decision.
- **munit**, with a `slow` tag for suites that take minutes, excluded unless the `slow_env` variable is set.
- **`app/stage`** (sbt-native-packager) is the deliverable: `lib/` with every jar and `bin/<name>`.
- **`version` from git** (sbt-dynver): tag `v1.2.3` and the build is `1.2.3`. There is no `version :=`.
- **`app/run` is forked** in the repo root with stdin connected, so relative paths and interactive input work.

---

## 4. Changing the template

```bash
cd ~/git/tomaszym/sbt-template
sbt test
```

That renders `src/main/g8` with its defaults into `target/sbt-test/`, then runs the steps in
`src/test/g8/test` there: `test`, both scalafmt checks, `app/run 1 2 3`, `app/stage`, and a check that the
start script exists.

giter8 syntax in `src/main/g8`:

- `$name$`, `$package$` and so on are replaced; `$name;format="norm"$` is the name lower-cased and hyphenated.
- A literal `$` in a templated file is written `\$` (see the `s"...\$a"` in `Main.scala`).
- Files matching `verbatim` in `default.properties` are copied as they are: `.claude/**` and `CLAUDE.md`.
- A directory named `$package__packaged$` becomes the package path, e.g. `link/azeno/myapp`.
