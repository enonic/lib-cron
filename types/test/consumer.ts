// Import-style consumer of the built package: tsconfig.json here resolves '/lib/cron' through
// build/types/package.json#types and checks the shipped .d.ts itself (skipLibCheck: false).
// require()-only consumption is a separate program, require-only.ts.

import {
    type GetJobParams,
    get,
    type JobContext,
    type JobDescriptor,
    type JobUser,
    type ListJobsParams,
    type ListJobsResult,
    list,
    reschedule,
    type ScheduleContext,
    type ScheduleContextUser,
    type ScheduleParams,
    type ScheduleParamsBase,
    schedule,
    type UnscheduleParams,
    unschedule,
} from '/lib/cron';

export const cronJob: ScheduleParams = {
    name: 'myTask',
    cron: '0 * * * *',
    times: 5,
    callback: () => {
        log.info('Task is called');
    },
};

export const delayedJob: ScheduleParams = {
    name: 'myTask',
    delay: 1000,
    fixedDelay: 5000,
    callback: () => {},
};

export const context: ScheduleContext = {
    repository: 'my-repo',
    branch: 'master',
    principals: ['role:system.admin'],
    attributes: { tenant: 'acme', retries: 3 },
    user: { login: 'su', idProvider: 'system' },
};

// The XP 6 spelling still type-checks, so a consumer upgrading the package does not have to fix it first
export const legacyContext: ScheduleContext = { user: { login: 'su', userStore: 'system' } };
export const user: ScheduleContextUser = { login: 'su', idProvider: 'system' };
export const base: ScheduleParamsBase = { name: 'myTask', callback: () => {} };

schedule({ name: 'myTask', callback: () => {}, fixedDelay: 5000, context });
reschedule(cronJob);

export const unscheduleParams: UnscheduleParams = { name: 'myTask' };
unschedule(unscheduleParams);

export const getParams: GetJobParams = { name: 'myTask' };
export const job: JobDescriptor | null = get(getParams);

export const listParams: ListJobsParams = { pattern: 'my.*' };
export const listed: ListJobsResult = list(listParams);
export const listedAll: ListJobsResult = list();
export const jobs: JobDescriptor[] = listed.jobs;

export const applicationKey: string = jobs[0].applicationKey;
export const nextExecTime: string | undefined = jobs[0].nextExecTime;
export const jobContext: JobContext = jobs[0].context;
export const jobUser: JobUser | undefined = jobContext.authInfo?.user;

if (jobContext.authInfo) {
    const principals: string[] = jobContext.authInfo.principals;
    void principals;
}

if (job != null) {
    const name: string = job.name;
    void name;
}

// Each rejection can fail for one reason only, so its directive cannot be satisfied by an unrelated error

// @ts-expect-error get returns null when no job is registered under that name
export const jobAlways: JobDescriptor = get(getParams);

// @ts-expect-error name is required
schedule({ callback: () => {}, fixedDelay: 5000 });

// @ts-expect-error callback is required
schedule({ name: 'myTask', fixedDelay: 5000 });

// @ts-expect-error one of cron or fixedDelay is required
schedule({ name: 'myTask', callback: () => {} });

// @ts-expect-error delay only modifies fixedDelay, which is still required
schedule({ name: 'myTask', callback: () => {}, delay: 1000 });

// @ts-expect-error cron and fixedDelay cannot be combined
schedule({ name: 'myTask', callback: () => {}, cron: '0 * * * *', fixedDelay: 5000 });

// @ts-expect-error cron is a string
schedule({ name: 'myTask', callback: () => {}, cron: 3600 });

// @ts-expect-error fixedDelay is a number of milliseconds
schedule({ name: 'myTask', callback: () => {}, fixedDelay: '5000' });

// @ts-expect-error a misspelt parameter is rejected
schedule({ name: 'myTask', callback: () => {}, fixedDelay: 5000, crons: '0 * * * *' });

// @ts-expect-error principals is a string array
schedule({ name: 'myTask', callback: () => {}, fixedDelay: 5000, context: { principals: 'role:system.admin' } });

// @ts-expect-error the user is identified by login, not key
schedule({ name: 'myTask', callback: () => {}, fixedDelay: 5000, context: { user: { login: 'su', key: 'x' } } });

// @ts-expect-error unschedule takes a params object, not a name
unschedule('myTask');

// @ts-expect-error pattern is a string
list({ pattern: /my.*/ });

// @ts-expect-error jobs is an array, not a single job
export const singleJob: JobDescriptor = listed.jobs;
