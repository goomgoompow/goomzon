package com.pentaon.vzon.plugin;

import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

class UpdatingLogin extends BaseExecutor {

  public UpdatingLogin(CordovaPlugin plugin, CallbackContext callbackContext) {
    super(plugin);
    mCallbackContext =callbackContext;
  }

  @Override
  protected void executeAction() {
    ((MainActivity)mActivity).updateLogin(mCallbackContext);
  }
}
