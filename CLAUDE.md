## Project

**lib-cron** (`com.enonic.lib.cron`) is an Enonic XP library for scheduling tasks and running them
on background threads. Targets XP 8+. Most of it is Java: a `LibCronHandler` script bean over a
bundle-scoped `JobScheduler`, with `cron-utils` unpacked into the jar. The JavaScript surface is a
single TypeScript file that marshals params into that bean.

The emitted JS runs under the consuming app's script engine, which defaults to Nashorn in XP 8, so
the output must stay ES5. `tsc` gates the built-ins through `lib: ES5`, esbuild gates the syntax
through `target: es5`, and the Java tests run the bundle on both engines.

Do not widen `lib` past `ES5` — it is the only one of those gates that fails silently, since
esbuild passes built-ins through untouched and the break lands on the consumer's engine. `target`
is `ES2015` only because TypeScript 7 removed `ES5`; `noEmit` makes it irrelevant to the output.

The bundle ships as `lib/cron.js` and that path is fixed — 2.x consumers call `require('/lib/cron')`.

## Commands

```bash
./gradlew build             # full build (production by default): esbuild -> build/esbuild, jar, Java tests on both engines
./gradlew build -Penv=dev   # dev build (source maps)
pnpm build                  # dev esbuild bundle only -> build/esbuild
pnpm check                  # type-check (tsc) + lint/format (biome)
pnpm fix                    # auto-fix lint + formatting
```

`xp.scriptEngines` gives `check` a `test` task on Nashorn and a `testGraalJS` task on GraalJS.
`LibCronHandlerTest` drives the emitted bundle through `ScriptTestSupport`, so both engines execute
the library's own JavaScript.

## Git & GitHub

**`gh` CLI:** Do not assume `gh` is available. Use raw `git` commands if missing.

### Commits

- **With issue**: `<Issue Title> #<number>` — e.g. `Modernize the Gradle and TypeScript toolchain #291`
- **Without issue**: plain text description — e.g. `Fix pattern matching in list`

### Pull Requests

- No emojis. Be concise — list only the changes.
