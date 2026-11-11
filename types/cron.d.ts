/**
 * Cron library for Enonic XP.
 *
 * Schedules named jobs and runs them on background threads, either on a cron pattern or on a
 * fixed delay. Targets XP 8+.
 *
 * ```js
 * var cronLib = require('/lib/cron');
 *
 * cronLib.schedule({
 *     name: 'myTask',
 *     cron: '0 * * * *',
 *     times: 5,
 *     callback: function () {
 *         log.info('Task is called');
 *     },
 *     context: {
 *         repository: 'my-repo',
 *         branch: 'master',
 *         principals: ['role:system.admin'],
 *         user: { login: 'su', idProvider: 'system' },
 *     },
 * });
 * ```
 *
 * @module /lib/cron
 */
declare global {
    interface XpLibraries {
        '/lib/cron': typeof import('./cron');
    }
}
export type ScheduleContextUser = {
    /** User login, without the id provider prefix. */
    login: string;
    /**
     * Id provider containing the user. Either this or the deprecated `userStore` has to be set
     * whenever `user` is given — XP's authentication token rejects a missing id provider.
     */
    idProvider?: string;
    /** @deprecated Renamed to `idProvider` in XP 7. */
    userStore?: string;
};
/**
 * Context the job callback runs in. Anything left out falls back to the context bound when the
 * library was first required, not the context of the `schedule()` call.
 */
export type ScheduleContext = {
    /** Repository to execute the callback in. */
    repository?: string;
    /** Branch to execute the callback in. */
    branch?: string;
    /** User to execute the callback as. */
    user?: ScheduleContextUser;
    /** Additional principals to execute the callback with, e.g. `['role:system.admin']`. */
    principals?: string[];
    /** Additional context attributes. */
    attributes?: Record<string, unknown>;
};
/** Timing-independent half of {@link ScheduleParams}; TypeScript names it when a call is rejected. */
export type ScheduleParamsBase = {
    /** Unique job name. Scheduling an existing name replaces that job. */
    name: string;
    /** Code to run on every execution. */
    callback: () => void;
    /** Number of executions. Omit for an unbounded job. */
    times?: number;
    /** Context of the job run. */
    context?: ScheduleContext;
};
/**
 * Parameters for {@link schedule} and {@link reschedule}. Timing is either a cron pattern or a
 * fixed delay, never both, and one of them is required — the arms below mirror what
 * `JobDescriptorImpl` accepts, so a combination that would throw does not compile.
 */
export type ScheduleParams = (ScheduleParamsBase & {
    /** Cron pattern, e.g. `'0 * * * *'`. */
    cron: string;
    delay?: never;
    fixedDelay?: never;
}) | (ScheduleParamsBase & {
    /** Milliseconds between the end of one execution and the start of the next. Must be above `0`. */
    fixedDelay: number;
    /** Milliseconds to delay the first execution. */
    delay?: number;
    cron?: never;
});
/** Parameters for {@link unschedule}. */
export type UnscheduleParams = {
    /** Name of a scheduled job. Unknown names are ignored. */
    name: string;
};
/** Parameters for {@link get}. */
export type GetJobParams = {
    /** Name of a scheduled job. */
    name: string;
};
/** Parameters for {@link list}. */
export type ListJobsParams = {
    /** Java regular expression the job name must match in full. Omit to list every job. */
    pattern?: string;
};
/** User the job runs as, as resolved at scheduling time. */
export type JobUser = {
    type: string;
    key: string;
    displayName: string;
    modifiedTime?: string;
    disabled: boolean;
    email?: string;
    login: string;
    idProvider?: string;
    hasPassword: boolean;
};
/**
 * Context a scheduled job runs in. `authInfo` and `user` are genuinely optional — `ContextMapper`
 * omits each when absent — but `principals` is always serialized alongside an `authInfo`.
 */
export type JobContext = {
    branch: string;
    repository: string;
    authInfo?: {
        user?: JobUser;
        principals: string[];
    };
};
/**
 * A scheduled job. `cron` and `cronDescription` are present on cron jobs, `delay` and `fixedDelay`
 * on fixed-delay jobs — never both pairs.
 */
export type JobDescriptor = {
    name: string;
    cron?: string;
    /** Human-readable rendering of `cron`, e.g. `'every hour'`. */
    cronDescription?: string;
    delay?: number;
    fixedDelay?: number;
    /** Key of the application that scheduled the job. */
    applicationKey: string;
    /** Next execution in ISO 8601 format. Present on cron jobs only. */
    nextExecTime?: string;
    context: JobContext;
};
/** Result of {@link list}. */
export type ListJobsResult = {
    jobs: JobDescriptor[];
};
/**
 * Schedules a job. A job already registered under `name` is replaced.
 *
 * @param params - Job name, callback, timing and context.
 */
export declare function schedule(params: ScheduleParams): void;
/**
 * Stops and removes a scheduled job. Does nothing when no job is registered under that name.
 *
 * @param params - Name of the job to stop.
 */
export declare function unschedule(params: UnscheduleParams): void;
/**
 * Unschedules a job and schedules it again with the given parameters.
 *
 * @param params - Same shape as {@link schedule}.
 */
export declare function reschedule(params: ScheduleParams): void;
/**
 * Fetches a scheduled job by name.
 *
 * @param params - Name of the job to fetch.
 * @returns The job, or `null` when no job is registered under that name.
 */
export declare function get(params: GetJobParams): JobDescriptor | null;
/**
 * Lists scheduled jobs in registration order. Replacing a job through {@link schedule} keeps its
 * original position; {@link reschedule} moves it to the end.
 *
 * @param params - Optional name pattern to filter by.
 * @returns Matching jobs.
 */
export declare function list(params?: ListJobsParams): ListJobsResult;
