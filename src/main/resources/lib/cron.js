var service = __.newBean('com.enonic.lib.cron.service.CronService');

exports.schedule = function (name, cron, callback) {
    return service.schedule(name, cron, app.name, callback);
};

exports.unschedule = function (name) {
    return service.unschedule(name);
};
