/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.pentaon.vzon.activity;

import android.Manifest;
import android.Manifest.permission;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.gae.scaffolder.plugin.FCMPluginActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pentaon.vzon.R;
import com.pentaon.vzon.common.ApplicationContext;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.common.SharedInfo;
import com.pentaon.vzon.common.VzonPreference;
import com.pentaon.vzon.data.WidthHeight;
import com.pentaon.vzon.data.doc.BusinessLicense;
import com.pentaon.vzon.data.doc.CertificateSealImpression;
import com.pentaon.vzon.data.doc.CopyCorporateRegister;
import com.pentaon.vzon.data.doc.CopyOfBankbook;
import com.pentaon.vzon.data.doc.IDCard;
import com.pentaon.vzon.data.doc.IEvidentialDoc;
import com.pentaon.vzon.data.doc.IdOfProxy;
import com.pentaon.vzon.data.doc.PictureOfInstall;
import com.pentaon.vzon.data.doc.PictureOfStore;
import com.pentaon.vzon.data.doc.PowerOfAttorney;
import com.pentaon.vzon.manager.ScreenInfoManager;
import com.pentaon.vzon.manager.TokenManager;
import com.pentaon.vzon.service.CheckingRemoveTaskService;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.CryptoUtil;
import com.pentaon.vzon.utils.SystemUtil;
import com.pentaon.vzon.views.CustomAlertDialog;
import com.pentaon.vzon.views.CustomAlertDialog.CustomDialogOneButtonListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends CordovaActivity implements CustomDialogOneButtonListener {

  private static final String TAG = MainActivity.class.getSimpleName();

//    public static boolean IS_TAPPED = true;

  private static final int REQUEST_PERMISSION_CODE = 0X010;
  private String mImageName;
  private String mRootPath;
  private String mTempPath;
  private ArrayList<String> mPermissions = new ArrayList<>();
  private CallbackContext mCallbackContext;
  private VzonPreference mPref;
  private boolean mIsInit;
  private boolean mIsFromeSomeActivity; //intent 정보로 다른 activity로부터 호출됐는지 여부 판단.
  private boolean mUsableGallery;
  private int mIdOfRestartAlert;
  private int mIdOfPermissionAlert;
  private ScreenInfoManager mScreenInfoMgr;

  private CustomAlertDialog mPermissionAlertDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int currentVersion = Build.VERSION.SDK_INT;
    ApplicationContext.setSharedInfo(this.getApplicationContext());
    mPref = new VzonPreference(getApplicationContext());

    checkABI();
    initialize();

    //check하고 싶은 permission 추가
    String[] permissions = new String[]{
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.GET_TASKS,
        permission.ACCESS_NETWORK_STATE
    };
    if (currentVersion >= Build.VERSION_CODES.M) {
      addPermissions(permissions);
      requestPermission(mPermissions);
    }

  }

  @Override
  public void onBackPressed() {
//        super.onBackPressed();
    Log.d(TAG, "onBackPressed: ");
  }

  /**
   * abi(application binary interface) 종류 확인!
   */
  private void checkABI() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      Log.i(TAG, "[checkABI]: SUPPORTED_ABIS= " + Arrays.toString(Build.SUPPORTED_ABIS));
      Log.i(TAG,
          "[checkABI]: SUPPORTED_32_BIT_ABIS= " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
      Log.i(TAG,
          "[checkABI]: SUPPORTED_64_BIT_ABIS= " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
    } else {
      Log.i(TAG, "[checkABI]: CPU_ABI = " + Build.CPU_ABI);
      Log.i(TAG, "[checkABI]: CPU_ABI2 = " + Build.CPU_ABI2);
      Log.i(TAG, "[checkABI]: OS.ARCH = " + System.getProperty("os.arch"));
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = getIntent();
    String action = intent.getAction();
    Log.d(TAG, "onStart: intent = " + intent + " , action = " + action);
  }

  @Override
  protected void onResume() {
    super.onResume();
    FCMPluginActivity.IS_TAPPED = false;
    if (mIsInit) {
      mIsInit = false;
      mPref.put(AppConstants.IS_INIT, false);
      return;
    }
    /*if (!mIsFromeSomeActivity) {
      // todo: 2018-12-14 background 여부 판단해서 앱 초기화 기능 구현
      long resumeTime = System.currentTimeMillis();
      //---------------------------------------------------------
      // 토큰 만료 관련 소스 주석처리
      //---------------------------------------------------------

//            checkStartTime(resumeTime);
    } else {
      mIsFromeSomeActivity = false;
    }*/
  }

  private void initialize() {
    Log.d(TAG, "onCreate getToken(): " + FirebaseInstanceId.getInstance().getToken());
    Log.d(TAG, "initialize: launchUrl = " + launchUrl);
    // enable Cordova apps to be started in the background
    Bundle extras = getIntent().getExtras();
    if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
      moveTaskToBack(true);
    }
    loadUrl(launchUrl);
    startService(new Intent(this, CheckingRemoveTaskService.class));
    mIsInit = mPref.getValue(AppConstants.IS_INIT, true);

    Point point = new Point();
    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getRealSize(point);

    int widthPixels = getResources().getDisplayMetrics().widthPixels;
    int heightPixels = getResources().getDisplayMetrics().heightPixels;
    mScreenInfoMgr = ScreenInfoManager.getInstance();
    mScreenInfoMgr.setDisplayMetrics(new WidthHeight(widthPixels,heightPixels));
    mScreenInfoMgr.setRealDisplaySize(new WidthHeight(point.x, point.y));
  }


  private void checkStartTime(long time) {
    long savedTime = mPref.getValue(AppConstants.APP_START_TIME, 0L);
    long diff = (savedTime == 0) ? 0 : time - savedTime;
    Log.d(TAG, "checkStartTime: savedTime= " + savedTime + " ,diff: " + diff);
    if (Config.DEBUG) {
      Date date = new Date(diff);
      DateFormat format = new SimpleDateFormat("HH:mm:ss");
      format.setTimeZone(TimeZone.getTimeZone("UTC"));
      String dateFormatted = format.format(date);
      float elapsedTime = (float) diff / (60 * 1000);
      Toast.makeText(this, "[login 경과시간]: " + dateFormatted, Toast.LENGTH_SHORT).show();

    }
        /*
        if(savedTime!=0 && diff>=AppConstants.MILLISEC_A_DAY)
        {
            CustomAlertDialog restartAlertDialog = new CustomAlertDialog(MainActivity.this);
            restartAlertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            restartAlertDialog.show();
            restartAlertDialog.setDialogMessage(R.string.act_main_alert_message_restart_app)
                             .setOnButtonClickListener(this);
            restartAlertDialog.setRightButton(R.string.popup_btn_text_ok,true);


            restartAlertDialog.setCancelable(false);
            restartAlertDialog.setCanceledOnTouchOutside(false);
            mIdOfRestartAlert = restartAlertDialog.getId();

        }
        else
        {
            mPref.put(AppConstants.APP_START_TIME,time);
        }*/
  }

  private void restartApp() {
    Log.d(TAG, "restartApp: RESTART!!");
    ((ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE))
        .clearApplicationUserData();
  }

  @Override
  protected void onPause() {
    super.onPause();
    FCMPluginActivity.IS_TAPPED = true;
        /*if(mPermissionAlertDialog!=null&&mPermissionAlertDialog.isShowing())
        {
            mPermissionAlertDialog.dismiss();
        }*/
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    FCMPluginActivity.IS_TAPPED = false;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK || data == null) {
      return;
    }

    Bundle bundle = data.getExtras();
    mIsFromeSomeActivity = bundle.getBoolean(AppConstants.INTENT_EXTRA_FROM_SOME_ACTIVITY);
    Log.d(TAG, "onActivityResult: isFromSomeAcitivyt: " + mIsFromeSomeActivity);

    if (Config.DEBUG) {
      Log.d(TAG,
          "onActivityResult: requestCode[ " + requestCode + " ] , resultCode = [" + resultCode
              + "]");
    }

    switch (requestCode) {
      case AppConstants.REQUEST_SCAN_BARCODE:
        ArrayList<String> resultScanBarcodes = data
            .getStringArrayListExtra(AppConstants.INTENT_EXTRA_FROM_SCAN_BARCODE_ACT);
        if (resultScanBarcodes != null) {
          JSONArray jsonArray = new JSONArray(resultScanBarcodes);
          mCallbackContext.success(jsonArray);
        } else {
          mCallbackContext.error("REQUEST_SCAN_BARCODE::Error");
        }
        break;

      case AppConstants.REQUEST_EVIDENCE_ATTACH:

        ArrayList<String> iKeys = data
            .getStringArrayListExtra(AppConstants.INTENT_EXTRA_FROM_INSTALLATION_ACT);
        if (iKeys != null) {
          for (int i = 0; i < iKeys.size(); i++) {
            Log.d(TAG, "onActivityResult: iKeys.get(" + i + ")" + iKeys.get(i));
          }
          JSONArray installationFileKeys = new JSONArray(iKeys);
          mCallbackContext.success(installationFileKeys);
        }
        break;

      default:
        Log.d(TAG, "onActivityResult: switch DEFAULT");
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PERMISSION_CODE) {
      for (int result : grantResults) {
        if (result == PackageManager.PERMISSION_DENIED) {
          mPermissionAlertDialog = new CustomAlertDialog(this);
          mPermissionAlertDialog.show();
          mPermissionAlertDialog.setCancelable(false);
          mPermissionAlertDialog.setCanceledOnTouchOutside(false);
          mPermissionAlertDialog.setOneButtonClickListener(this)
              .setDialogMessage(R.string.act_main_permission_denied_alert_dialog_message);
          mPermissionAlertDialog.setButtons(CustomAlertDialog.ONE_BUTTON);
//                    mPermissionAlertDialog.setRightButton(R.string.popup_btn_text_ok,true);
//                    permissionAlertDialog.setLeftButton(R.string.popup_btn_text_cancel,true);
          mIdOfPermissionAlert = mPermissionAlertDialog.getId();
          break;
        }
      }
      SystemUtil.clearCache();
    }
  }

  /**
   * web으로 부터 전달받은 token값을 token manager에 등록
   */
  public void setVzonAccessToken(JSONArray args, CallbackContext callbackContext) {
    String token = "";
    try {
      token = args.get(0).toString();
      Log.d(TAG, "setVzonAccessToken: token = " + token);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    TokenManager.getInstance().setToken(token);
    callbackContext.success("Success to save the vzon access token.");
  }

  /**
   * 새롭게 로그인했을 경우 토큰 만료(로그인 후 24시간) 기준 시간을 변경함.
   */
  public void updateLogin(CallbackContext callbackContext) {
    Log.d(TAG, "updateLogin: ");
    long loginTime = System.currentTimeMillis();
    mPref.put(AppConstants.APP_START_TIME, loginTime);
    callbackContext.success();
    Date date = new Date();
//        Toast.makeText(this, "로그인 됨 ("+date+"+)", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onNeutralButtonClick(int id) {
    exitApp();
  }

  /**
   * catch native back button event
   */
  public void onCheckBackButton() {
    Log.d(TAG, "onCheckBackButton: ");
    exitApp();
  }

  //=================================================================
  // plugin method
  //=================================================================
  public void showToast(JSONArray args, CallbackContext callbackContext) {
    this.mCallbackContext = callbackContext;
    String arg0 = "";
    try {
      arg0 = args.getString(0);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Log.d(TAG, "showToast: arg0: " + arg0);
    Toast.makeText(getApplicationContext(),
        "[ " + arg0 + " ]" + getString(R.string.toast_server_connected), Toast.LENGTH_SHORT).show();
    mCallbackContext.success("showing toast message is completed.");
  }

  public void openCameraForContract(String type, boolean usableGallery,
      CallbackContext callbackContext) {
    this.mCallbackContext = callbackContext;
    this.mUsableGallery = usableGallery;
    Context context = this.getApplicationContext();

    SharedInfo si = ApplicationContext.getInstance().getSharedInfo();

    UUID uuid = UUID.randomUUID();

    mImageName = uuid.toString();
    mRootPath = si.getCachePath();
    mTempPath = si.getCachePath()+"temp/";

    SystemUtil.deleteAllFile(mRootPath);
    CryptoUtil.getInstance().clearEncryptStatuses();

    String[] arrTempFileName = mImageName.split("-");
    if (arrTempFileName.length > 2) {
      //arrayTempFileName[0] ->System.currentTimeMillis();
      String tempFileName =
          System.currentTimeMillis() + "_" + arrTempFileName[1] + "_" + arrTempFileName[2];
      tempFileName = SystemUtil.modifyFileName(0, tempFileName);
      mImageName = tempFileName;
    }

    //plugin으로 부터 전달받은 type(문서타입) 값에 따라 증빙 문서 관련 정보 가져옴.
    HashMap<String, Object> args = getEvidentialDocInfo(context, type).getEvidentialInfo();

    if (mUsableGallery) {
      //ImageLoadingFromGalleryActivity로 이동!
      boolean isEditable= (!type.equals(AppConstants.CONTRACT_PROOF_H));
      Intent intent1 = new Intent(MainActivity.this, ImageLoadingFromGalleryActivity.class);
      args.put(AppConstants.INTENT_EXTRA_ROOT_PATH, mRootPath);
      args.put(AppConstants.INTENT_EXTRA_TEMP_PATH, mTempPath);
      args.put(AppConstants.INTENT_EXTRA_FILE_NAME, mImageName);
      intent1.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, args);
      intent1.putExtra(AppConstants.INTENT_EXTRA_ALLOW_EDIT_MODE, isEditable);
      startActivityForResult(intent1, AppConstants.REQUEST_EVIDENCE_ATTACH);
    } else {
      Intent intent2 = new Intent(MainActivity.this, PictureListActivity.class);
      args.put(AppConstants.INTENT_EXTRA_ROOT_PATH, mRootPath);
      args.put(AppConstants.INTENT_EXTRA_TEMP_PATH, mTempPath);
      args.put(AppConstants.INTENT_EXTRA_FROM_MAIN, true);
      args.put(AppConstants.INTENT_EXTRA_FILE_NAME, mImageName);
      intent2.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, args);
      startActivityForResult(intent2, AppConstants.REQUEST_EVIDENCE_ATTACH);
    }

  }

  public void doScanAction(JSONArray args, CallbackContext callbackContext) {
    mCallbackContext = callbackContext;
    Log.d(TAG, "doScanAction: go to scanBarcode Activity");
    Intent intent = new Intent(MainActivity.this, ScanBarcodeActivity.class);
    intent.putExtra(AppConstants.INTENT_EXTRA_TO_SCAN_BARCODE_ACT, args.toString());
    intent.putExtra(AppConstants.INTENT_EXTRA_SCAN_BARCODE_ALLOW_REGISTER, true);
    startActivityForResult(intent, AppConstants.REQUEST_SCAN_BARCODE);
  }

  public void takePicture(CallbackContext callbackContext) {
    mCallbackContext = callbackContext;
    Intent intent = new Intent(MainActivity.this, InstallationCaptureActivity.class);
    startActivityForResult(intent, AppConstants.REQUEST_EVIDENCE_ATTACH);
  }

  public void gotoFcm(CallbackContext callbackContext) {
    mCallbackContext = callbackContext;
    Intent intent = new Intent(MainActivity.this, FCMPluginActivity.class);
    startActivityForResult(intent, 44);
  }

  public void setBaseUrl(JSONArray args, CallbackContext callbackContext) {
    this.mCallbackContext = callbackContext;
    try {
      String name = args.getString(0);
      Config.setBaseUrl(name);
      mCallbackContext.success();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * android 권한을 체크함.
   *
   * @param permission 권한 정보(Manifest.permission...)
   */
  private boolean checkPermission(String permission) {
    return (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
        == PackageManager.PERMISSION_GRANTED);
  }

  /**
   * android 권한을 체크하고 체크하고자 하는 권한을 배열에 넣음.(AndroidManifest.xml에 <uses-permission> tag로 등록되어 있어야함.)
   *
   * @param permissions 체크하고자 하는 권한 값
   */
  private void addPermissions(String[] permissions) {
    for (String permission : permissions) {
      if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
          == PackageManager.PERMISSION_DENIED) {
        mPermissions.add(permission);
      }
    }
  }

  private void requestPermission(String[] permissions) {
    ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSION_CODE);
  }

  private void requestPermission(ArrayList<String> permissions) {
    if (permissions.size() > 0) {
      String[] stringArr = permissions.toArray(new String[permissions.size()]);
      ActivityCompat.requestPermissions(MainActivity.this, stringArr, REQUEST_PERMISSION_CODE);
    }
        /*else
        {
            removeCacheData();
            startService(new Intent(this, CheckingRemoveTaskService.class));
        }*/
  }

  private void exitApp() {
    finishAffinity();
    Runtime.getRuntime().exit(0);
  }

  private IEvidentialDoc getEvidentialDocInfo(Context context, String type) {
    IEvidentialDoc iEvidentialDoc = null;
    switch (type) {
      case AppConstants.CONTRACT_PROOF_A: //사업자 등록증
        iEvidentialDoc = new BusinessLicense(context);
        break;
      case AppConstants.CONTRACT_PROOF_B: //대표자 신분증
        iEvidentialDoc = new IDCard(context);
        break;
      case AppConstants.CONTRACT_PROOF_C://통장 사본
        iEvidentialDoc = new CopyOfBankbook(context);
        break;
      case AppConstants.CONTRACT_PROOF_D://법인 인감 증명서
        iEvidentialDoc = new CertificateSealImpression(context);
        break;
      case AppConstants.CONTRACT_PROOF_E://법인 등기부 등본
        iEvidentialDoc = new CopyCorporateRegister(context);
        break;
      case AppConstants.CONTRACT_PROOF_F://위임장
        iEvidentialDoc = new PowerOfAttorney(context);
        break;
      case AppConstants.CONTRACT_PROOF_G://위임장 신분증
        iEvidentialDoc = new IdOfProxy(context);
        break;

      case AppConstants.CONTRACT_PROOF_H: //매장 사진
        iEvidentialDoc = new PictureOfStore(context);
        break;
      case AppConstants.CONTRACT_PROOF_I://설치 확인서
      default:
        iEvidentialDoc = new PictureOfInstall(context);
        break;
    }
    return iEvidentialDoc;
  }
}
