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
pnpm build:types            # @enonic-types/lib-cron package -> build/types (version from gradle.properties)
pnpm test:types             # build:types + verify:types, the one-command developer entry point
pnpm verify:types           # packlist check + type-check types/test against an existing build/types
```

`./gradlew build` runs `buildTypes` (into `assemble`) and `testTypes` (into `check`); CI publishes
`build/types` to npm right after the Maven publish on release versions only.

`testTypes` runs `verify:types`, not `test:types`, because `types/build.mjs` opens with
`rm -rf build/types` — running the full `test:types` there would have a verification task delete
and rewrite `buildTypes`' declared output. lib-cors still has that wiring; this repo deliberately
diverges.

The types package is generated, never hand-written. `tsconfig.types.json` emits declarations from
`cron.ts`, and `types/build.mjs` assembles `build/types` with the version from `gradle.properties`.
A type reaches the package only if it is `export`ed, and the `declare global { interface
XpLibraries }` block in `cron.ts` is what types `require('/lib/cron')` for consumers — esbuild
erases it from the bundle. `types/verify.mjs` checks the built package rather than the emitted
file: the npm packlist, an import-style consumer (`types/test/consumer.ts`) and a `require()`-only
one (`types/test/require-only.ts`), both with `skipLibCheck: false`.

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
