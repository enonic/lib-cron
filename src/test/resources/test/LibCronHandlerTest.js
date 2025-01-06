var assert = require('/lib/xp/testing.js');
var cronLib = require('/lib/cron.js');

var expectedWithDelayJson = {
    'name': 'myTask',
    'fixedDelay': 5000,
    'delay': 1000,
    'applicationKey': 'myapplication',
    'context': {
        'branch': 'draft',
        'repository': 'com.enonic.cms.default',
        'authInfo': {
            'principals': [
                'user:system:anonymous',
                'role:system.everyone'
            ]
        }
    }
};

exports.scheduleWithDelay = function () {
    cronLib.schedule({
        name: 'myTask',
        delay: 1000,
        fixedDelay: 5000,
        times: 5,
        callback: function () {
            log.info('Task is called');
        }
    });

    var result = cronLib.get({name: 'myTask'});

    assert.assertJsonEquals(expectedWithDelayJson, result);
};

var expectedWithCronJson = {
    'name': 'myTask',
    'cron': '* * * * *',
    'cronDescription': 'every minute',
    'applicationKey': 'myapplication',
    'context': {
        'branch': 'draft',
        'repository': 'com.enonic.cms.default',
        'authInfo': {
            'principals': [
                'user:system:anonymous',
                'role:system.everyone'
            ]
        }
    }
};

exports.scheduleWithCron = function () {
    cronLib.schedule({
        name: 'myTask',
        cron: '* * * * *',
        callback: function () {
            log.info('Task is called');
        }
    });

    var result = cronLib.get({name: 'myTask'});
    delete result['nextExecTime'];

    assert.assertJsonEquals(expectedWithCronJson, result);
};

var expectedWithContextJson = {
    name: 'myTask',
    cron: '* * * * *',
    cronDescription: 'every minute',
    applicationKey: 'myapplication',
    context: {
        branch: 'master',
        repository: 'my-repo',
        authInfo: {
            user: {
                type: 'user',
                key: 'user:system:test-user',
                disabled: false,
                login: 'test-user',
                idProvider: 'system'
            },
            principals: [
                'user:system:test-user',
                'role:system.test-role'
            ]
        }
    }
};

exports.scheduleWithContext = function () {
    cronLib.schedule({
        name: 'myTask',
        cron: '* * * * *',
        callback: function () {
            log.info('Task is called');
        }, context: {
            branch: 'master',
            repository: 'my-repo',
            user: {
                login: 'test-user',
                idProvider: 'system'
            },
            principals: [
                'user:system:test-user',
                'role:system.test-role'
            ]
        }
    });

    var result = cronLib.get({name: 'myTask'});
    delete result['nextExecTime'];

    assert.assertJsonEquals(expectedWithContextJson, result);
};

exports.unschedule = function () {
    cronLib.schedule({
        name: 'myTask',
        cron: '* * * * *',
        callback: function () {
        }
    });

    cronLib.schedule({
        name: 'myTask',
        cron: '1 * * * *',
        callback: function () {
        }
    });

    cronLib.unschedule({name: 'myTask'});

    var result = cronLib.list({name: 'myTask'});

    var expected = {
        'jobs': []
    };
    assert.assertJsonEquals(expected, result);
};

var listExpected = {
    'jobs': [
        {
            'name': 'myTask1',
            'cron': '* * * * *',
            'cronDescription': 'every minute',
            'applicationKey': 'myapplication',
            'context': {
                'branch': 'draft',
                'repository': 'com.enonic.cms.default',
                'authInfo': {
                    'principals': [
                        'user:system:anonymous',
                        'role:system.everyone'
                    ]
                }
            }
        },
        {
            'name': 'myTask2',
            'cron': '1 * * * *',
            'cronDescription': 'every hour at minute 1',
            'applicationKey': 'myapplication',
            'context': {
                'branch': 'draft',
                'repository': 'com.enonic.cms.default',
                'authInfo': {
                    'principals': [
                        'user:system:anonymous',
                        'role:system.everyone'
                    ]
                }
            }
        }
    ]
};

exports.list = function () {
    cronLib.schedule({
        name: 'myTask1',
        cron: '* * * * *',
        callback: function () {
        }
    });

    cronLib.schedule({
        name: 'myTask2',
        cron: '1 * * * *',
        callback: function () {
        }
    });

    var result = cronLib.list({name: 'myTask'});
    result.jobs.forEach(function (job) {
        delete job['nextExecTime'];
    });

    assert.assertJsonEquals(listExpected, result);
};
