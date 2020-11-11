cordova.define("com.pentaon.vzon.plugin.VzonPlugin", function(require, exports, module) {
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'coolMethod', [arg0]);
};

exports.showToast = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'showToast', [arg0]);
};

exports.openCameraForContract = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'openCameraForContract', [arg0]);
};

exports.scanBarcode = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'scanBarcode', [arg0]);
};

exports.takeCapture = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'takeCapture', [arg0]);
};

exports.setVzonToken = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'setVzonToken', [arg0]);
};

exports.testFunc = function(arg0, success, error){
    exec(success,error,'VzonPlugin','testFunc',[arg0]);
};

exports.updateLogin = function(arg0, success, error){
    exec(success,error,'VzonPlugin','updateLogin');
};
});
