cordova.define("com.pentaon.vzon.plugin.VzonPlugin", function(require, exports, module) {
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'VzonPlugin', 'coolMethod', [arg0]);
};

});
