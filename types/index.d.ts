import type { PrincipalKey, User } from "@enonic-types/core";

declare module "/lib/cron" {
    export interface ScheduleContextUser {
        /**
         * User login.
         */
        login: string;

        /**
         * ID provider containing the user.
         */
        idProvider?: string;

        /**
         * @deprecated Use `idProvider` instead.
         */
        userStore?: string;
    }

    export interface ScheduleContext {
        /**
         * Repository to execute the callback in.
         */
        repository?: string;

        /**
         * Name of the branch to execute the callback in.
         */
        branch?: string;

        /**
         * Additional principals to execute the callback with.
         */
        principals?: PrincipalKey[];

        /**
         * Additional context attributes.
         */
        attributes?: Record<string, unknown>;

        /**
         * User credentials to execute the callback with.
         */
        user?: ScheduleContextUser;
    }

    export interface ScheduleParams {
        /**
         * Unique task name.
         */
        name: string;

        /**
         * Code of the task which should be called.
         */
        callback: () => void;

        /**
         * Cron pattern (see https://en.wikipedia.org/wiki/Cron). Cannot be combined with `fixedDelay` / `delay`.
         */
        cron?: string;

        /**
         * The delay between the termination of one execution and the commencement of the next (in ms). Cannot be combined with `cron`.
         */
        fixedDelay?: number;

        /**
         * The time to delay first execution (in ms). Cannot be combined with `cron`.
         */
        delay?: number;

        /**
         * The number of times the task will be executed. Leave empty for infinite calls.
         */
        times?: number;

        /**
         * Context of the task run.
         */
        context?: ScheduleContext;
    }

    export interface UnscheduleParams {
        /**
         * Name of the scheduled task to remove.
         */
        name: string;
    }

    export interface GetParams {
        /**
         * Name of the scheduled task to fetch.
         */
        name: string;
    }

    export interface ListParams {
        /**
         * Optional glob-style pattern to filter jobs by name.
         */
        pattern?: string;
    }

    export interface JobDescriptorContextAuthInfo {
        user?: User;
        principals?: PrincipalKey[];
    }

    export interface JobDescriptorContext {
        repository?: string;
        branch?: string;
        authInfo?: JobDescriptorContextAuthInfo;
    }

    export interface JobDescriptor {
        /**
         * Name of the scheduled task.
         */
        name: string;

        /**
         * Cron pattern the task was scheduled with, when scheduled by cron.
         */
        cron?: string;

        /**
         * Human-readable description of the cron pattern.
         */
        cronDescription?: string;

        /**
         * The delay between the termination of one execution and the commencement of the next (in ms), when scheduled by delay.
         */
        fixedDelay?: number;

        /**
         * The time to delay first execution (in ms), when scheduled by delay.
         */
        delay?: number;

        /**
         * Key of the application that scheduled the task.
         */
        applicationKey: string;

        /**
         * Context in which the task callback runs.
         */
        context?: JobDescriptorContext;

        /**
         * Time for next execution in ISO 8601 format.
         */
        nextExecTime?: string;
    }

    export interface JobList {
        jobs: JobDescriptor[];
    }

    /**
     * Schedules a task.
     */
    export function schedule(params: ScheduleParams): void;

    /**
     * Unschedules a scheduled task by name and schedules it again with the given parameters.
     */
    export function reschedule(params: ScheduleParams): void;

    /**
     * Unschedules a scheduled task.
     */
    export function unschedule(params: UnscheduleParams): void;

    /**
     * Fetches a scheduled task by name, or `null` if no such task exists.
     */
    export function get(params: GetParams): JobDescriptor | null;

    /**
     * Lists scheduled tasks, optionally filtered by a name pattern.
     */
    export function list(params?: ListParams): JobList;
}

export {};
