const service = __.newBean('com.enonic.lib.cron.handler.LibCronHandler');

function required(params, name) {
    const value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

function schedule(params) {

    const serviceParams = service.newParams();
    const contextParams = serviceParams.getContext();

    serviceParams.setName(required(params, 'name'));
    serviceParams.setScript(required(params, 'callback'));
    serviceParams.setCron(nullOrValue(params.cron));
    serviceParams.setDelay(nullOrValue(params.delay));
    serviceParams.setFixedDelay(nullOrValue(params.fixedDelay));
    serviceParams.setTimes(nullOrValue(params.times));

    serviceParams.setApplicationKey(nullOrValue(app.name));

    const context = params.context;

    if (context) {
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
                contextParams.setIdProvider(context.user.userStore);
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

    return service.schedule(serviceParams);

}

function unschedule(params) {
    return service.unschedule(params.name);
}

function reschedule(params) {
    unschedule(params);
    return schedule(params);
}

function get(params) {
    const result = service.get(params.name);
    return __.toNativeObject(result);
}

function list(params) {
    const listParams = service.listParams();

    if(params) {
        if (params.pattern) {
            listParams.setPattern(params.pattern);
        }
    }

    const result = service.list(listParams);

    return __.toNativeObject(result);
}

exports.reschedule = reschedule;
exports.schedule = schedule;
exports.unschedule = unschedule;
exports.get = get;
exports.list = list;
