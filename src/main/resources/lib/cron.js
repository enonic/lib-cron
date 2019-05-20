var service = __.newBean('com.enonic.lib.cron.service.CronService');

function schedule(params) {
    return service.schedule(params.name, params.cron, app.name, params.callback);
}

function unschedule(params) {
    return service.unschedule(params.name);
}

function reschedule(params) {
    unschedule(params);
    return schedule(params);
}

exports.reschedule = reschedule;
exports.schedule = schedule;
exports.unschedule = unschedule;
