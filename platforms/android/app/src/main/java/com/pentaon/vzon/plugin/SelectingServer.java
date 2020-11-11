package com.pentaon.vzon.plugin;

import com.pentaon.vzon.activity.MainActivity;
import com.pentaon.vzon.common.Config;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

class SelectingServer extends BaseExecutor {

  public SelectingServer(CordovaPlugin plugin, JSONArray args,
      CallbackContext callbackContext) {
    super(plugin);
    mJSONArray = args;
    mCallbackContext = callbackContext;
  }

  @Override
  protected void executeAction() {
    if( Config.DEBUG)((MainActivity)mActivity).showToast(mJSONArray, mCallbackContext);
    ((MainActivity)mActivity).setBaseUrl(mJSONArray,mCallbackContext);
  }
}
