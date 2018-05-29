var exec = require('cordova/exec');

exports.fetchTemplate = function (success, error , arg0) {
    exec(success, error, 'JxLifeRemoteSignature', 'fetchTemplate', [arg0]);
};

exports.onlySign = function (arg0, success, error) {
    exec(success, error, 'JxLifeRemoteSignature', 'onlySign', [arg0]);
};

exports.submitSig = function (arg0, success, error) {
    exec(success, error, 'JxLifeRemoteSignature', 'submitSig', [arg0]);
};
