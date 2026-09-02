import type { ScriptValue } from '@enonic-types/core';

type ScheduleContextUser = {
    login: string;
    idProvider?: string;
    /** @deprecated Renamed to `idProvider` in XP 7. */
    userStore?: string;
};

export type ScheduleContext = {
    repository?: string;
    branch?: string;
    user?: ScheduleContextUser;
    principals?: string[];
    attributes?: Record<string, unknown>;
};

export type ScheduleParams = {
    name: string;
    callback: () => void;
    cron?: string;
    delay?: number;
    fixedDelay?: number;
    times?: number;
    context?: ScheduleContext;
};

export type UnscheduleParams = {
    name: string;
};

export type GetJobParams = {
    name: string;
};

export type ListJobsParams = {
    pattern?: string;
};

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

export type JobContext = {
    branch: string;
    repository: string;
    authInfo?: {
        user?: JobUser;
        principals?: string[];
    };
};

export type JobDescriptor = {
    name: string;
    cron?: string;
    cronDescription?: string;
    delay?: number;
    fixedDelay?: number;
    applicationKey: string;
    nextExecTime?: string;
    context?: JobContext;
};

export type ListJobsResult = {
    jobs: JobDescriptor[];
};

type ContextParamsBean = {
    setRepository(repository: string): void;
    setBranch(branch: string): void;
    setUsername(username: string): void;
    setIdProvider(idProvider: string): void;
    setPrincipals(principals: string[]): void;
    setAttributes(attributes: ScriptValue): void;
};

type ScheduleParamsBean = {
    setName(name: string): void;
    setScript(script: () => void): void;
    setCron(cron: string | null): void;
    setDelay(delay: number | null): void;
    setFixedDelay(fixedDelay: number | null): void;
    setTimes(times: number | null): void;
    setApplicationKey(applicationKey: string | null): void;
    getContext(): ContextParamsBean;
};

type ListJobsParamsBean = {
    setPattern(pattern: string): void;
};

type LibCronHandler = {
    newParams(): ScheduleParamsBean;
    listParams(): ListJobsParamsBean;
    schedule(params: ScheduleParamsBean): void;
    unschedule(name: string): void;
    get(name: string): JobDescriptor | null;
    list(params: ListJobsParamsBean): ListJobsResult;
};

const service: LibCronHandler = __.newBean<LibCronHandler>('com.enonic.lib.cron.handler.LibCronHandler');

function required<T, K extends keyof T>(params: T, name: K): NonNullable<T[K]> {
    const value = params[name];
    if (value === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }

    return value as NonNullable<T[K]>;
}

function applyContext(contextParams: ContextParamsBean, context: ScheduleContext): void {
    if (context.repository) {
        contextParams.setRepository(context.repository);
    }

    if (context.branch) {
        contextParams.setBranch(context.branch);
    }

    if (context.user) {
        if (context.user.login) {
            contextParams.setUsername(context.user.login);
        }
        if (context.user.idProvider) {
            contextParams.setIdProvider(context.user.idProvider);
        } else if (context.user.userStore) {
            contextParams.setIdProvider(context.user.userStore);
        }
    }

    if (context.principals) {
        contextParams.setPrincipals(context.principals);
    }
    if (context.attributes) {
        contextParams.setAttributes(__.toScriptValue(context.attributes));
    }
}

export function schedule(params: ScheduleParams): void {
    const serviceParams = service.newParams();

    serviceParams.setName(required(params, 'name'));
    serviceParams.setScript(required(params, 'callback'));
    serviceParams.setCron(__.nullOrValue(params.cron));
    serviceParams.setDelay(__.nullOrValue(params.delay));
    serviceParams.setFixedDelay(__.nullOrValue(params.fixedDelay));
    serviceParams.setTimes(__.nullOrValue(params.times));

    serviceParams.setApplicationKey(__.nullOrValue(app.name));

    if (params.context) {
        applyContext(serviceParams.getContext(), params.context);
    }

    service.schedule(serviceParams);
}

export function unschedule(params: UnscheduleParams): void {
    service.unschedule(params.name);
}

export function reschedule(params: ScheduleParams): void {
    unschedule(params);
    schedule(params);
}

export function get(params: GetJobParams): JobDescriptor | null {
    return __.toNativeObject(service.get(params.name));
}

export function list(params?: ListJobsParams): ListJobsResult {
    const listParams = service.listParams();

    if (params) {
        if (params.pattern) {
            listParams.setPattern(params.pattern);
        }
    }

    return __.toNativeObject(service.list(listParams));
}
