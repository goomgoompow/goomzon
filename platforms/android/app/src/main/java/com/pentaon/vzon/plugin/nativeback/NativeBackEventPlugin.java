package com.pentaon.vzon.plugin.nativeback;

import com.pentaon.vzon.activity.MainActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeBackEventPlugin extends CordovaPlugin {

    private static final String ACTION_NATIVE_BACK = "nativeBack";
    private CordovaInterface mInterface;
    private MainActivity mActivity;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_NATIVE_BACK)) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if(mInterface==null) mInterface = cordova;
                    if(mActivity==null) mActivity = (MainActivity) mInterface.getActivity();
                    onCheckNativeBackEvent();
                    callbackContext.success();
                }
            });
            return true;
        }
        return false;
    }

    private void onCheckNativeBackEvent(){
        if(mActivity!=null){
            mActivity.onCheckBackButton();
        }
    }
}
