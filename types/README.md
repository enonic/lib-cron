# @enonic-types/lib-cron

TypeScript declarations for [lib-cron](https://github.com/enonic/lib-cron), the Enonic XP cron
library. They are generated from the library source on every release, so the version matches the
jar.

## Setup

```bash
npm install --save-dev @enonic-types/lib-cron
```

The package is types-only, so it belongs in `devDependencies`. Add it to both `types` and `paths` in
`tsconfig.json`:

```json
{
    "compilerOptions": {
        "types": ["@enonic-types/global", "@enonic-types/lib-cron"],
        "paths": {
            "/lib/cron": ["./node_modules/@enonic-types/lib-cron"]
        }
    }
}
```

Both entries are needed, one per module style. The `types` entry — next to
[`@enonic-types/global`](https://www.npmjs.com/package/@enonic-types/global) — loads the package's
`XpLibraries` augmentation, which is what types `require('/lib/cron')`; `paths` alone does not type
`require()` in a file that never imports the module. The `paths` entry is what resolves
`import ... from '/lib/cron'`, and needs no `baseUrl` on modern TypeScript.

## Usage

```js
const cronLib = require('/lib/cron');

cronLib.schedule({
    name: 'myTask',
    cron: '0 * * * *',
    callback: () => log.info('Task is called'),
});
```

```ts
import { get, list, schedule, unschedule } from '/lib/cron';
import type { JobDescriptor, ScheduleParams } from '@enonic-types/lib-cron';

export function start(params: ScheduleParams) {
    schedule(params);
}

export function status(name: string): JobDescriptor | null {
    return get({ name });
}

export function stopAll() {
    const result = list();
    for (let i = 0; i < result.jobs.length; i++) {
        unschedule({ name: result.jobs[i].name });
    }
}
```

Scheduling rules and runtime behavior are documented in the
[lib-cron documentation](https://github.com/enonic/lib-cron/tree/master/docs/index.adoc).
