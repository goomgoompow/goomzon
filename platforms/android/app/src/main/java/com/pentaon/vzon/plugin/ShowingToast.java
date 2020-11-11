package com.pentaon.vzon.plugin;

import android.os.Looper;
import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class ShowingToast extends BaseExecutor{

  public ShowingToast(CordovaPlugin plugin,JSONArray jsonArray, CallbackContext callbackContext) {
    super(plugin);
    mJSONArray = jsonArray;
    mCallbackContext = callbackContext;
  }

  @Override
  protected void executeAction() {
//    mCordovaInterface.getThreadPool().execute(new Runnable() {
    mActivity.runOnUiThread(new Runnable(){
      @Override
      public void run() {
        if(Looper.myLooper()==null) Looper.prepare();
        ((MainActivity)mActivity).showToast(mJSONArray, mCallbackContext);
      }
    });
  }
}
