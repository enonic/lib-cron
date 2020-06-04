var service = __.newBean('com.enonic.lib.cron.handler.LibCronHandler');

__.disposer(function () {
    service.deactivate();
});

function required(params, name) {
    var value = params[name];
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

    var serviceParams = service.newParams();
    var contextParams = serviceParams.context;

    serviceParams.name = required(params, 'name');
    serviceParams.script = required(params, 'callback');
    serviceParams.cron = nullOrValue(params.cron);
    serviceParams.delay = nullOrValue(params.delay);
    serviceParams.fixedDelay = nullOrValue(params.fixedDelay);
    serviceParams.times = nullOrValue(params.times);

    serviceParams.applicationKey = nullOrValue(app.name);

    var context = params.context;

    if (context) {
        if (context.repository) {
            contextParams.repository = context.repository;
        }

        if (context.branch) {
            contextParams.branch = context.branch;
        }

        if (context.user) {
            if (context.user.login) {
                contextParams.username = context.user.login;
            }
            if (context.user.userStore) {
                contextParams.userStore = context.user.userStore;
            }
        }

        if (context.principals) {
            contextParams.principals = context.principals;
        }
        if (context.attributes) {
            contextParams.attributes = __.toScriptValue(context.attributes);
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
    var result = service.get(params.name);
    return __.toNativeObject(result);
}

function list(params) {
    var listParams = service.listParams();

    if(params) {
        if (params.pattern) {
            listParams.pattern = params.pattern;
        }
    }

    var result = service.list(listParams);

    return __.toNativeObject(result);
}

exports.reschedule = reschedule;
exports.schedule = schedule;
exports.unschedule = unschedule;
exports.get = get;
exports.list = list;
