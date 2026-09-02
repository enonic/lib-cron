var assert = require('/lib/xp/testing');

var service = __.newBean('com.enonic.lib.cron.handler.LibCronHandler');

function fingerprint(value) {
    if (value === null) {
        return 'null';
    }
    if (value === undefined) {
        return 'undefined';
    }
    return typeof value + ':' + String(value);
}

function cronAfter(value) {
    var params = service.newParams();
    params.setCron(__.nullOrValue(value));
    return fingerprint(params.getCron());
}

function timesAfter(value) {
    var params = service.newParams();
    params.setTimes(__.nullOrValue(value));
    return fingerprint(params.getTimes());
}

// Handed a raw `undefined`, a String setter stores the literal "undefined" under Nashorn while
// GraalJS stores null, so every optional param crosses through __.nullOrValue first.
exports.undefinedBecomesNull = function () {
    assert.assertEquals('null', cronAfter(undefined), 'cron');
    assert.assertEquals('null', timesAfter(undefined), 'times');
};

exports.nullStaysNull = function () {
    assert.assertEquals('null', cronAfter(null), 'cron');
    assert.assertEquals('null', timesAfter(null), 'times');
};

exports.valuesPassThrough = function () {
    assert.assertEquals('string:* * * * *', cronAfter('* * * * *'), 'cron');
    assert.assertEquals('number:5', timesAfter(5), 'times');
    assert.assertEquals('number:0', timesAfter(0), 'times zero');
};
