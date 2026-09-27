# CLAUDE.md

Rules for Scala code are in `.claude/skills/code/SKILL.md`: load it before editing any source file.

## Working together

- Build in small steps and stop after each one for review. The author edits the code by hand between steps, so
  read the file again before changing it.
- Domain types are their own step. Agree on the data before any code that uses it gets written.

## Commits

- [Conventional Commits](https://www.conventionalcommits.org/) for every message: `<type>[optional scope]: <description>`.
  Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`. A breaking
  change gets `!` after the type or scope (`feat(api)!: ...`) or a `BREAKING CHANGE:` footer.
- Never add a `Co-Authored-By: Claude` trailer, or any Claude/Anthropic attribution, to commits or PR bodies.
- Commit messages are in English, in plain short sentences: what changed and why. No filler, no marketing words,
  no long clauses stacked with commas.
