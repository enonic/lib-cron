// require()-only consumer: nothing here imports the module, so typing comes solely from the XpLibraries
// hook in the built package, which tsconfig.require.json pulls in the way a consumer's `types` entry does.
// XpRequire falls back to `unknown`, not `any`, so without the hook every line below fails, not passes.

const cronLib = require('/lib/cron');

cronLib.schedule({
    name: 'myTask',
    cron: '0 * * * *',
    callback: () => {
        log.info('Task is called');
    },
});

cronLib.reschedule({ name: 'myTask', delay: 1000, fixedDelay: 5000, callback: () => {} });
cronLib.unschedule({ name: 'myTask' });

export const job = cronLib.get({ name: 'myTask' });
export const names: string[] = cronLib.list({ pattern: 'my.*' }).jobs.map((entry) => entry.name);

if (job != null) {
    const applicationKey: string = job.applicationKey;
    void applicationKey;
}

// @ts-expect-error get returns null when no job is registered under that name
export const jobAlways: { name: string } = cronLib.get({ name: 'myTask' });

// @ts-expect-error name is required
cronLib.schedule({ callback: () => {}, fixedDelay: 5000 });
