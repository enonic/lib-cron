var service = __.newBean('com.enonic.lib.cron.service.CronService');

exports.schedule = function (name, cron, callback) {
    return service.schedule(name, cron, callback);
};

exports.unschedule = function (name) {

};
