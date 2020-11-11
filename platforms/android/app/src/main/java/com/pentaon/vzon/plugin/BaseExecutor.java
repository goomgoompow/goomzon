package com.pentaon.vzon.plugin;

import android.app.Activity;
import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public abstract class BaseExecutor implements VzonExecutable {

  protected CordovaPlugin mPlugin;
  protected CordovaInterface mCordovaInterface;
  protected JSONArray mJSONArray;
  protected CallbackContext mCallbackContext;
  protected boolean mIsValidAction;
  protected Activity mActivity;

  public BaseExecutor(CordovaPlugin cordovaPlugin) {
    mPlugin = cordovaPlugin;
    mCordovaInterface = cordovaPlugin.cordova;
  }

  @Override
  public boolean execute() {
    mActivity = mCordovaInterface.getActivity();
    mIsValidAction =mActivity!=null && mActivity instanceof MainActivity;
    if(mIsValidAction)
    {
      executeAction();
    }
    return mIsValidAction;
  }

  /** 실제 기능 구현 **/
  abstract protected void executeAction();
}
