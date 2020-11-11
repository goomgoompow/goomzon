package com.pentaon.vzon.plugin;

import android.app.Activity;

import com.pentaon.vzon.activity.MainActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class VzonPlugin extends CordovaPlugin {

    private static final String TAG = "VzonPlugin";
    private static final String ACTION_SHOW_TOAST = "showToast";
    private static final String ACTION_OPEN_CAMERA_CONSTRACT = "openCameraForContract";
    private static final String ACTION_SCAN_BARCODE= "scanBarcode";
    private static final String ACTION_TAKE_PICTURE= "takePicture";
    private static final String ACTION_GO_TO_FCM= "gotoFCM";
    private static final String ACTION_SET_VZON_TOKEN = "setVzonToken";
    private static final String ACTION_CHECK_VZON_TOKEN = "checkVzonToken";
    private static final String ACTION_SELECT_SERVER = "selectServer";
    private static final String ACTION_TEST_FUNC= "clickTest";
    private static final String ACTION_LOGIN ="updateLogin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        BaseExecutor vzonExecutable = null;

        switch (action) {
            case ACTION_SHOW_TOAST:
                vzonExecutable = new ShowingToast(this, args, callbackContext);
                break;
            case ACTION_OPEN_CAMERA_CONSTRACT:
                vzonExecutable = new OpeningCameraForContract(this, args, callbackContext);
                break;
            case ACTION_SCAN_BARCODE:
                vzonExecutable = new ScaningBarcode(this, args, callbackContext);
                break;
            case ACTION_TAKE_PICTURE:
                vzonExecutable = new TakingPicture(this, args, callbackContext);
                break;
            case ACTION_SET_VZON_TOKEN:
                vzonExecutable = new UpdatingVzonToken(this, args, callbackContext);
                break;
            case ACTION_SELECT_SERVER:
                vzonExecutable = new SelectingServer(this, args, callbackContext);
                break;
            case ACTION_LOGIN:
                vzonExecutable = new UpdatingLogin(this, callbackContext);
                break;
            default:

        }

        return vzonExecutable.execute();
    }
}
