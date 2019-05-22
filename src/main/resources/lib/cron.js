var service = __.newBean('com.enonic.lib.cron.service.CronService');

function schedule(params) {

    var serviceParams = service.newParams();
    var contextParams = serviceParams.context;

    serviceParams.name = params.name;
    serviceParams.cron = params.cron;
    serviceParams.script = params.callback;

    serviceParams.applicationKey = app.name;

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

exports.reschedule = reschedule;
exports.schedule = schedule;
exports.unschedule = unschedule;
exports.get = get;
