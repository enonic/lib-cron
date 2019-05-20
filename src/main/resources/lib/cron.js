var service = __.newBean('com.enonic.lib.cron.service.CronService');

exports.schedule = function (params) {
    return service.schedule(params.name, params.cron, app.name, params.callback);
};

exports.unschedule = function (params) {
    return service.unschedule(params.name);
};

exports.reschedule = function (params) {
    exports.unschedule(params);
    return exports.schedule(params);
};
