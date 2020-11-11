package com.pentaon.vzon.plugin;

import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class UpdatingVzonToken extends BaseExecutor{

  public UpdatingVzonToken(CordovaPlugin plugin, JSONArray args,
      CallbackContext callbackContext) {
    super(plugin);
    mJSONArray = args;
    mCallbackContext = callbackContext;
  }

  @Override
  protected void executeAction() {
    ((MainActivity)mActivity).setVzonAccessToken(mJSONArray , mCallbackContext);
  }
}
