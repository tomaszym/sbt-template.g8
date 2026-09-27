---
name: code
description: Rules for writing or changing Scala code in this repository. Domain types that cannot hold invalid states, identifiers, codec derivation, reading dependency APIs with cellar, when to extract a helper, when to comment. Load before editing any source file.
---

# Code

## Invalid states

- A domain type must not be able to hold a value that is not a thing. This is the default, not an aspiration:
  reach for the shape that excludes the bad combinations first, then ask what it cost.
- Signs it went wrong: `Option` fields that always arrive together and are only real in some combinations. A
  `Boolean` beside the field it qualifies, meaning nothing when that field is absent. A doc comment explaining
  which combinations do not occur. A smart constructor checking a pairing a case class could have made
  unsayable. If "when this one is present, that one is too" can be written in a comment, it is a type waiting to
  be written instead.
- "It cannot happen in practice" is not an answer. A fixture says what one source sends today; a type says what
  this code can hold for ever. Measure the data to know what to model, never to excuse not modelling it.
- Price it before arguing about it. The tight version is usually one type and a constructor; the loose one
  usually costs a paragraph defending it. Write the tight one, run the tests, read the diff, let the number
  decide. Folding three optional fields into one `Option` of a small case class has cost three net lines, left
  the wire unchanged, and exposed a second impossible state nobody had noticed.
- The exception is real, and has to be named rather than gestured at: what exactly the tight shape costs, and why
  that is more than the bug it prevents. Churning every call site in a second language to prevent what a
  constructor already prevents is the honest form of that argument.
- The wire is a separate decision. A tight domain type does not oblige a nested JSON document: encode it flat if
  flat is what the reader wants. The type stops this process producing nonsense; the encoder decides what travels.
- This is for the types that hold meaning. A type that mirrors something else's shape, like an external payload,
  can pass fields it does not understand through raw: modelling every field of every version is a large guess.

## Identifiers

- Anything that names or identifies something is an opaque type in the `ids` package under the project's root
  package, never a bare `String`. Not a value class: `AnyVal` boxes inside a `Map` or `List`, where identifiers
  live. Opaque types are erased.
- The companion holds the codecs - with circe, the `Encoder`/`Decoder`, and `KeyEncoder`/`KeyDecoder` if it
  keys a map: one wire form, chosen once, no import needed. The smart constructor goes there too, if the value
  needs validating.
- Inside the companion the alias is transparent, so an instance for the underlying type already serves. Do not
  summon one there - the search finds the given being defined.

## Derivation

- Never automatic: no `io.circe.generic.auto.*`, no `sttp.tapir.generic.auto.*`, no `derives` on a case class.
  One instance per type, by hand or semi-automatically, next to the type.
- The wire form is a decision. Auto derivation hides it and changes it silently when a field is renamed, so the
  break shows up at run time rather than in the compiler. A `given` costs one line and puts the decision where a
  reader looks for it.
- pureconfig's Scala 3 derivation ignores default arguments, which makes optional keys mandatory. Use
  `forProductN` or a hand-written reader.

## Dependency APIs

- Never guess a signature or a behaviour. `cellar` (`~/.local/bin/cellar`) reads it from the artifact. Run it
  from the project root with a module: `cellar list --module core <package>` (core, app).
  The first call per module takes about 15 s, then caches in `.cellar/cache`.
- Worth most on the instances written by hand rather than derived. With tapir on the classpath,
  `cellar get --module app 'sttp.tapir.Schema$'` lists every `schemaFor...` that already exists.
- Off the classpath: `cellar get-external org.joda:joda-money:2.0.3 org.joda.money.Money` - a full
  coordinate, no `::`; `latest` resolves the newest release.
- Statics and companions sit behind `$`: `Money$` has `of`, `zero`, `ofMinor`.
- `get` says what exists, not what happens. For what throws, what rounds or what an empty input does, read the
  source with `cellar get-source <coordinate> <fqn>`; a signature alone is how a wrong guess gets made.
- `search` matches type names only. To find a method, get its type and read the members.

## Helpers

- A function with one caller usually costs more than it saves: the reader jumps away and loses the order of
  the steps. Write it where it is used.
- Extract when a second caller appears, when its comment would outgrow its code, or when a reader can skip it
  and lose nothing. A step that only wants a name can be a local `def` or `val` in the function it serves.
- A long line is not a reason. Split the expression.
- Five one-line methods look tidier than one ten-line function and read worse. The names promise the steps are
  separable, and hide whether they are even in the right order.

## Comments

- Only what the code cannot say. No doc comment that repeats the signature.
- Worth writing: why this over the obvious alternative, why something is absent, a constraint outside the file,
  a workaround and what would retire it.
- Never describe the project's current state ("nothing talks to a device yet"). It goes stale.
