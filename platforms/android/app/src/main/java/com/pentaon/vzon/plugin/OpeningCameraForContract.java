package com.pentaon.vzon.plugin;

import android.app.Activity;
import com.pentaon.vzon.activity.MainActivity;
import javax.security.auth.callback.Callback;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class OpeningCameraForContract extends BaseExecutor{

  public OpeningCameraForContract(CordovaPlugin plugin, JSONArray args,
      CallbackContext callbackContext) {
    super(plugin);
    mJSONArray = args;
    mCallbackContext = callbackContext;
  }

  @Override
  protected void executeAction() {
    String type = null;
    boolean usableGallery = false;

    try {
      type = mJSONArray.getString(0);
      if(mJSONArray.length()>1)usableGallery = mJSONArray.getBoolean(1);
      ((MainActivity) mActivity).openCameraForContract(type, usableGallery, mCallbackContext);
      mIsValidAction = true;
    } catch (JSONException e) {
      e.printStackTrace();
      mIsValidAction = false;
    }
  }
}
