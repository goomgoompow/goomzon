cordova.define("com.pentaon.vzon.plugin.nativeback.NativeBackEventPlugin", function(require, exports, module) {
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'NativeBackEventPlugin', 'coolMethod', [arg0]);
};

});
