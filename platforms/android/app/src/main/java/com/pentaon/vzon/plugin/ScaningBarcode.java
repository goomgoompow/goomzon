package com.pentaon.vzon.plugin;

import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

class ScaningBarcode extends BaseExecutor {

  public ScaningBarcode(CordovaPlugin plugin, JSONArray jsonArray, CallbackContext callbackContext) {
    super(plugin);
    mJSONArray = jsonArray;
    mCallbackContext = callbackContext;
  }

  @Override
  protected void executeAction() {
    ((MainActivity)mActivity).doScanAction(mJSONArray, mCallbackContext);
  }
}
