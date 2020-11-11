package com.pentaon.vzon.activity;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.AmbientLightManager;
import com.google.zxing.client.android.DecodeFormatManager;
import com.google.zxing.client.android.DecodeHintManager;
import com.google.zxing.client.android.FinishListener;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.IntentSource;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.pentaon.vzon.R;
import com.pentaon.vzon.data.barcode.BarcodeVerification;
import com.pentaon.vzon.data.barcode.VerificationAssorter;
import com.pentaon.vzon.handler.CaptureHandler;
import com.pentaon.vzon.network.ApiClient;
import com.pentaon.vzon.network.ApiInterface;
import com.pentaon.vzon.pojo.BarcodeInfo;
import com.pentaon.vzon.pojo.BarcodeResult;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.views.CustomAlertDialog;
import com.pentaon.vzon.views.CustomAlertDialog.CustomDialogTwoButtonListener;
import com.pentaon.vzon.views.CustomToast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanBarcodeActivity extends BaseActivity implements SurfaceHolder.Callback,
    OnClickListener,
    CustomDialogTwoButtonListener {

  private static final String TAG = "ScanBarcodeActivity";

  private static final long DELAY_FOR_START_PREVIEW = 1000L;
  private static final int MSG_START_SCAN = 0x0001;

  private ImageButton mBtnBack;
  private TextView mTextViewProductName;
  private TextView mTextViewConfirmedProductNum;
  private TextView mTextViewTotalProductNum;
  private CustomToast mCustomToast;
  private ToggleButton mToggleBtnFlash;
  private CustomAlertDialog mCustomDialog;
  private ViewfinderView mViewfinderView;

  private ArrayList<BarcodeInfo> mBarcodeInfos = new ArrayList<>();
  private ArrayList<String> mArrConfirmed = new ArrayList<>(); //확인된 바코드 정보
  //  private ArrayList<String> mArrUnknownBarcode = new ArrayList<>();
  private Map<DecodeHintType, ?> mDecodeHints;

  private ApiInterface mApiInterface;
  private JSONArray mSerialNr;

  private int mTotalNum;
  private int mTotalNumberMustCheck = 0;
  private boolean mIsAllowRegister; //불일치한 시리얼 번호 등록 허용 여부
  private boolean mHasSurface;

  private InactivityTimer mInactivityTimer;
  private AmbientLightManager mAmbientLightManager;
  private CaptureHandler mCaptureHandler;
  private IntentSource mSource;
  private Collection<BarcodeFormat> mDecodeFormats;
  private Result mLastResult;

  private String mType;
  private String mCharacterSet;
  private String mSourceUrl;
  private String mBarcode;
  private Intent mIntent;
  private Result mSavedResultToShow;
  private Vibrator mVibrator;

  private CameraManager mCameraManager;
  private Point mBestPreviewSize;
  private BarcodeVerification mVerifier;
  private OrientationEventListener mOrientationEventListener;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan_barcode);

    mIntent = getIntent();
    mIsAllowRegister = mIntent
        .getBooleanExtra(AppConstants.INTENT_EXTRA_SCAN_BARCODE_ALLOW_REGISTER, false);
    String jsonArray = mIntent.getStringExtra(AppConstants.INTENT_EXTRA_TO_SCAN_BARCODE_ACT);

    try {
      JSONArray array = new JSONArray(jsonArray);
      Log.d(TAG, "onCreate: " + array.toString(2));
      parseJsonArray(array);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume: ");
    mCameraManager = new CameraManager(getApplication());
    mBestPreviewSize = mCameraManager.getBestPreviewSize();

    mViewfinderView = findViewById(R.id.act_scanbarcode_viewfinder_view);
    mViewfinderView.setCameraManager(mCameraManager);

    mCaptureHandler = null;
    mLastResult = null;

    resetStatusView();
    mAmbientLightManager.start(mCameraManager);
    mInactivityTimer.onResume();

    Intent intent = getIntent();
    mSource = IntentSource.NONE;
    mDecodeFormats = null;
    mCharacterSet = null;

    if (intent != null) {
      String action = intent.getAction();
      String dataString = intent.getDataString();

      if (Intents.Scan.ACTION.equals(action)) {
        // Scan the formats the intent requested, and return the result to the calling activity.
        mSource = IntentSource.NATIVE_APP_INTENT;
        mDecodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
        mDecodeHints = DecodeHintManager.parseDecodeHints(intent);

        if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
          int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
          int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
          if (width > 0 && height > 0) {
            mCameraManager.setManualFramingRect(width, height);
          }
        }

        if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
          int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
          if (cameraId > 0) {
            mCameraManager.setManualCameraId(cameraId);
          }
        }

                /*String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if(customPromptMessage!=null)
                {
                    mStatusView.setText(customPromptMessage);
                }*/
      } else if (dataString != null &&
          dataString.contains("http://www.google") &&
          dataString.contains("/m/products/scan")) {
        mSource = IntentSource.PRODUCT_SEARCH_LINK;
        mSourceUrl = dataString;
        mDecodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
      }

      mCharacterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
    }
    SurfaceView surfaceView = findViewById(R.id.act_scanbarcode_preview);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (mHasSurface) {
      //The activity was paused but not stopped, so the surfasce stil exists
      //surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
      //Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
    }
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause: ");
    if (mOrientationEventListener != null) {
      mOrientationEventListener.disable();
    }
    if (mCaptureHandler != null) {
      mCaptureHandler.quitSynchronously();
      mCaptureHandler = null;
    }
    mInactivityTimer.onPause();
    mAmbientLightManager.stop();
    mCameraManager.closeDriver();
    if (!mHasSurface) {
      SurfaceView surfaceView = (SurfaceView) findViewById(R.id.act_scanbarcode_preview);
      SurfaceHolder surfaceHolder = surfaceView.getHolder();
      surfaceHolder.removeCallback(this);
    }
    super.onPause();

  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop:");
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy: ");
    mInactivityTimer.shutdown();
    mOrientationEventListener.disable();
    mOrientationEventListener = null;
    mArrConfirmed = null;
//    mArrUnknownBarcode = null;
    super.onDestroy();
  }

  @Override
  protected void initLayout() {
    mTextViewProductName = findViewById(R.id.act_scanbarcode_product_name);
    mTextViewConfirmedProductNum = findViewById(R.id.act_scanbarcode_num_confirmed_barcode);
    mTextViewTotalProductNum = findViewById(R.id.act_scanbarcode_num_total_barcode);

    mBtnBack = findViewById(R.id.act_scanbarcode_arrow_close);
    mToggleBtnFlash = findViewById(R.id.act_scanbarcode_btn_flash);

    mToggleBtnFlash.setChecked(false);

    mBtnBack.setOnClickListener(this);
    mToggleBtnFlash.setOnClickListener(this);
  }

  @Override
  protected void initialize() {
    Retrofit retrofit = ApiClient.getClient();
    mCustomToast = new CustomToast(ScanBarcodeActivity.this);
    mApiInterface = retrofit.create(ApiInterface.class);
    mHasSurface = false;
    mInactivityTimer = new InactivityTimer(this);
    mAmbientLightManager = new AmbientLightManager(this);
    mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    mOrientationEventListener = new OrientationEventListener(this,
        SensorManager.SENSOR_DELAY_NORMAL) {
      @Override
      public void onOrientationChanged(int i) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
//                mCameraManager.setCameraOrientation(rotation);
        Log.d(TAG, "onOrientationChanged: rotation = " + rotation);
      }
    };
    if (mOrientationEventListener.canDetectOrientation()) {
      Log.d(TAG, "onCreate: Possible to dectect change a orientation");
//            mOrientationEventListener.enable();
    } else {
      Log.d(TAG, "onCreate: Impossible to detect change or orientation");
//            mOrientationEventListener.disable();
    }

    mCustomDialog = new CustomAlertDialog(this);
    mCustomDialog.setCancelable(false);
    mCustomDialog.setCanceledOnTouchOutside(false);
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra(AppConstants.INTENT_EXTRA_FROM_SCAN_BARCODE_ACT, mArrConfirmed);
    addInfoAndFinish(RESULT_OK, intent);
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    if (surfaceHolder == null) {
      Log.e(TAG, "surfaceCreated: *** WARNING *** surfaceCreated() gave us a null surface.");
    }

    if (!mHasSurface) {
      mHasSurface = true;
      initCamera(surfaceHolder);

    }
    showProgress(getString(R.string.common_progress_loading));
    CountDownTimer countDownTimer = new CountDownTimer(DELAY_FOR_START_PREVIEW,
        DELAY_FOR_START_PREVIEW) {
      @Override
      public void onTick(long l) {
        Log.d(TAG, "onTick: " + l);
      }

      @Override
      public void onFinish() {
        Log.d(TAG, "onFinish: ");
        hideProgress();
      }
    };
    countDownTimer.start();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    mHasSurface = false;
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

  }


  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   */
  public void handleDecode(Result rawResult) {
    mInactivityTimer.onActivity();
    mLastResult = rawResult;

    final String result = rawResult.getText();
    Log.d(TAG, "handleResult: result = " + result + " ,mVerifier: " + mVerifier);

    showProgress(getString(R.string.common_progress_loading));

    BarcodeInfo info = mVerifier.pickBarcodeInfoOut(result);
    Call<BarcodeResult> call = mApiInterface.doVerifySerialNumber(info);
    call.enqueue(new Callback<BarcodeResult>() {
      @Override
      public void onResponse(Call<BarcodeResult> call, Response<BarcodeResult> response) {
        if (response.isSuccessful()) {
          mVibrator.vibrate(200);
          boolean isConfirmed = response.body().result;
          boolean isDuplicated = checkDuplicatedConfirmedSerialNumber(result);
          String message = "";
          // TODO: 2018-11-19 mArrConfirmed ArrayList에 이미 저장된 barcode 값이 넘어왔을 떄!
          if (isConfirmed) {
            message = (isDuplicated) ?
                getString(R.string.act_scanbarcode_msg_duplicated_serial_number) :
                getString(R.string.act_scanbarcode_serial_number_ok);
          } else {
            message = (isDuplicated) ?
                getString(R.string.act_scanbarcode_msg_duplicated_serial_number) :
                getString(R.string.act_scanbarcode_serial_number_no);
            isConfirmed = isDuplicated;
          }
          mCustomToast.showToast(message, result, isConfirmed);
          checkBarcodeResult(result, isConfirmed);

        } else {
          showResponseFailToast(response.code());
          restartPreviewAfterDelay(DELAY_FOR_START_PREVIEW);
        }
        hideProgress();
      }

      @Override
      public void onFailure(Call<BarcodeResult> call, Throwable t) {
        hideProgress();
        Toast.makeText(ScanBarcodeActivity.this,
            getText(R.string.toast_picture_list_message_server_error).toString(),
            Toast.LENGTH_SHORT).show();
        restartPreviewAfterDelay(DELAY_FOR_START_PREVIEW);
      }
    });
  }

  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.act_scanbarcode_arrow_close:
        onBackPressed();
        break;
      case R.id.act_scanbarcode_btn_flash:
        //mScannerView.setFlash(mToggleBtnFlash.isChecked());
        mCameraManager.setTorch(mToggleBtnFlash.isChecked());
        break;
      default:
    }
  }

  public ViewfinderView getViewfinderView() {
    return mViewfinderView;
  }

  public CameraManager getCameraManager() {
    return mCameraManager;
  }

  public Handler getHandler() {
    return mCaptureHandler;
  }

  public void drawViewfinder() {
    mViewfinderView.drawViewfinder();
  }


  /**
   * intent로 전달된 jsonArray를 parsing해서 data 추출함
   */
  private void parseJsonArray(JSONArray jsonArray) {
    String productName = "";
    boolean bErrorOccur = false;
    try {
      /*
      public static final String PARAM_ID = "paramId"; // 출하, 오더 아이디 통합
      public static final String PARAM_ITEM_ID = "paramItemId"; // 출하 항목, 오더 항목 아이디 통합
      public static final String HOLD_PARTY_ID  ="holdPartyId";//보유 파티 아이디
      public static final String PROD_ID  = "prodId";// 상품 아이디 [필수]
      public static final String TYPE  = "type"; //바코드 유형[필수]
      public static final String SERIAL_NR  ="serialNr";//시리얼 넘버[필수]
      public static final String TOTAL_NUM  ="totalNum";
      */

      JSONObject obj = jsonArray.getJSONObject(0);
      productName = obj.getString(AppConstants.PROD_NAME);
      mTotalNum = obj.getInt(AppConstants.TOTAL_NUM);
      mSerialNr = obj.getJSONArray(AppConstants.SERIAL_NR);

      int size = mTotalNumberMustCheck = mTotalNum;
      mTextViewTotalProductNum.setText("" + size);

      mType = obj.getString(AppConstants.TYPE);

      for (int i = 0; i < size; i++) {
        int length = mSerialNr.length();
        if (length >= 0 && i < length) {
          if (!mSerialNr.getString(i).equals("")) {
            mArrConfirmed.add(mSerialNr.getString(i));
          }
        }
      }
      VerificationAssorter assortor = new VerificationAssorter(obj);
      mVerifier = assortor.getBarcodeVerifier();

    } catch (JSONException e) {
      e.printStackTrace();
      bErrorOccur = true;
    } finally {
      if (mVerifier == null || bErrorOccur) {
        Toast
            .makeText(this, getResources().getString(R.string.act_scanbarcode_error_barcode_verify),
                Toast.LENGTH_SHORT).show();
        addInfoAndFinish(RESULT_OK, mIntent);
        return;
      }
    }
    mTextViewProductName.setText(productName);
    mTextViewConfirmedProductNum.setText("" + mArrConfirmed.size());
  }

  /**
   * 서버로부터 전달된 값(isConfirmed)여부에 따라 #(true) 인식된 serial 번호를  배열(mArrConfirmed)에 넣거나 #(false) 등록여부를 묻는
   * alert dialog를 보여줌
   *
   * @param barcode 인식된 serialNumber
   * @param isConfirmed 서버에서 받은 결과값(true or false)
   */
  private void checkBarcodeResult(String barcode, boolean isConfirmed) {
    mBarcode = barcode;

    if (mArrConfirmed.contains(barcode)) {
      restartPreviewAfterDelay(DELAY_FOR_START_PREVIEW);
      return;
    }

    if (isConfirmed) {
      addConfirmedSerialNumber(barcode);
            /*stopScanPreview();
            startScanPreview();*/
      restartPreviewAfterDelay(DELAY_FOR_START_PREVIEW);

    } else {
      if (mIsAllowRegister) {
        mCustomDialog.show();
        mCustomDialog.setButtons(CustomAlertDialog.TWO_BUTTON);
        mCustomDialog.setDialogMessage(R.string.act_scanbarcode_alert_dialog_message)
            .setTwoButtonClickListener(this);
      } else {
//        mArrUnknownBarcode.add(barcode);
        restartPreviewAfterDelay(DELAY_FOR_START_PREVIEW);
      }

    }
  }

  /**
   * Web에 전달될 시리얼 번호 배열(mArrBarcodes)에 인식된 시리얼 번호를 추가
   *
   * @param barcode : 인식된 serial number.
   */
  private void addConfirmedSerialNumber(String barcode) {
    if (checkDuplicatedConfirmedSerialNumber(barcode)) {
      return;
    }
    mArrConfirmed.add(barcode);
    int size = mArrConfirmed.size();
    if (size >= mTotalNumberMustCheck) {
      if (size > mTotalNumberMustCheck) {
        int index = size-2;
        if(index<0)index=0;
        mArrConfirmed.remove(index);
      }
      Intent intent = new Intent();
      intent.putExtra(AppConstants.INTENT_EXTRA_FROM_SCAN_BARCODE_ACT, mArrConfirmed);
      addInfoAndFinish(RESULT_OK, intent);
    }
    mTextViewConfirmedProductNum.setText("" + mArrConfirmed.size());
  }

  /**
   * 전달된 시리얼 번호가 이미 확인되어 있는지 체크(mArrConfirmed 배열의 값과 비교)
   *
   * @return true: 중복됨, false : 중복 안됨
   */
  private boolean checkDuplicatedConfirmedSerialNumber(String barcode) {
    for (String sn : mArrConfirmed) {
      if (sn.equals(barcode)) {
        return true;
      }
    }
    return false;
  }

  /**
   * message를 전달 받아 간단한 Toast를 띄워줌
   *
   * @param message : 토스트 메시지
   */
  private void showToast(String message) {
    Toast.makeText(ScanBarcodeActivity.this
        , message
        , Toast.LENGTH_SHORT
    ).show();
  }


  private void restartPreviewAfterDelay(long delayMS) {
    if (mCaptureHandler != null) {
      mCaptureHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
    }
    resetStatusView();
  }

  private void resetStatusView() {
    mViewfinderView.setVisibility(View.VISIBLE);
    mLastResult = null;
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    if (surfaceHolder == null) {
      throw new IllegalStateException("No SurfaceHolder provided");
    }
    if (mCameraManager.isOpen()) {
      Log.w(TAG, "initCamera() while already open-- late SurfaceView callback?");
      return;
    }
    try {
      mCameraManager.openDriver(surfaceHolder);
      if (mCaptureHandler == null) {
        mCaptureHandler = new CaptureHandler(this, mDecodeFormats, mDecodeHints, mCharacterSet,
            mCameraManager);
      }
      decodeResult(null);
    } catch (IOException e) {
      e.printStackTrace();
      displayFrameworkBugMessageAndExit();
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializing camera", e);
      displayFrameworkBugMessageAndExit();
    }
  }

  private void displayFrameworkBugMessageAndExit() {
    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
        this);
    builder.setTitle(getString(R.string.app_name));
    builder.setMessage(getString(R.string.msg_camera_framework_bug));
    builder.setPositiveButton(R.string.popup_btn_text_ok, new FinishListener(this));
    builder.show();
  }

  private void decodeResult(Result result) {
    if (mCaptureHandler == null) {
      mSavedResultToShow = result;
    } else {
      if (result != null) {
        mSavedResultToShow = result;
      }
      if (mSavedResultToShow != null) {
        Message message = Message
            .obtain(mCaptureHandler, R.id.decode_succeeded, mSavedResultToShow);
        mCaptureHandler.handleMessage(message);
      }
      mSavedResultToShow = null;
    }
  }

  @Override
  public void onLeftButtonClick(int id) {
    mCustomDialog.dismiss();
    restartPreviewAfterDelay(0L);
  }

  @Override
  public void onRightButtonClick(int id) {
    for (String b : mArrConfirmed) {
      if (mBarcode.equals(b)) {
        showToast(getString(R.string.act_scanbarcode_msg_duplicated_serial_number));
        mCustomDialog.dismiss();
        restartPreviewAfterDelay(0L);
        return;
      }
    }
    addConfirmedSerialNumber(mBarcode);
//    mTotalNumberMustCheck++;
    mTextViewTotalProductNum.setText("" + mTotalNumberMustCheck);
    showToast(getString(R.string.act_scanbarcode_msg_register_serial_number_complete));
    mCustomDialog.dismiss();
    restartPreviewAfterDelay(0L);
  }
}
