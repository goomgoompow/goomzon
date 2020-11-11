package com.pentaon.vzon.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.TouchEn.mVaccine.b2b2c.activity.BackgroundScanActivity;
import com.TouchEn.mVaccine.b2b2c.activity.ScanActivity;
import com.TouchEn.mVaccine.b2b2c.util.CommonUtil;
import com.TouchEn.mVaccine.b2b2c.util.Global;
import com.pentaon.vzon.R;
import com.pentaon.vzon.common.VzonPreference;
import com.secureland.smartmedic.SmartMedic;

public class SplashActivity extends BaseActivity {

  // mVaccine 제품 RequestCode
  public final static int REQUEST_CODE = 777;
  // UI 간소화 모드 검사진행 Notification
  private final static int MESSAGE_ID = 12345;
  // UI 간소화 모드 검사결과 Notification
  private final static int MESSAGE_ID1 = 123456;
  //private static final Context MainAcitivity_sample = null;

  private final static String MODE_MINI = "MINI";
  private final static String MODE_FULL = "FULL";
  private static String VACCINE_MODE = MODE_MINI;

  private int mCount = 3;
  private ImageView mImageView;

  @Override
  protected void initLayout() {
    mImageView = findViewById(R.id.act_splash_img);
  }

  @Override
  protected void initialize() {
    Drawable resources = mImageView.getDrawable();
    Bitmap bitmap = ((BitmapDrawable) resources).getBitmap();
    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
        .create(this.getResources(), bitmap);
    roundedBitmapDrawable.setCornerRadius(25f);
    mImageView.setImageDrawable(roundedBitmapDrawable);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //=========================================================
    // imageView 추가 및 rounded corner 적용
    // 난독화 테스트
    //=========================================================
    setContentView(R.layout.act_splash);

    com.secureland.smartmedic.core.Constants.site_id = "pentaon_vzon";
    com.secureland.smartmedic.core.Constants.license_key = "157427487f9238d78d0eee701294ea7fd460e6f2";

    com.secureland.smartmedic.core.Constants.debug = false; // 디버깅 필요 시 true 설정
    Global.debug = false; // 디버깅 필요 시 true 설정

/*    new CountDownTimer(3000,1000)
    {
      @Override
      public void onTick(long millisUntilFinished) {
        Toast.makeText(SplashActivity.this, ""+--mCount, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFinish() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
      }
    }.start();*/

    try {
      SmartMedic.init(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    checkVaccine(VACCINE_MODE);

  }

  private void checkVaccine(String vaccineMode) {
    switch (vaccineMode) {
      case MODE_MINI:
        mini();
        break;
      case MODE_FULL:
        full();
        break;
      default:
        mini();
    }
  }

/*------------------ 권장 옵션 (mini 모드) --------------------
	검사진행 중 UI가 없는 간소화 모드입니다.
	옵션에 따라 백신의 액티비티에서 루팅검사, 악성코드 검사를 실행합니다.
	--------------------------------------------------------*/

  public void mini() {
    Intent i = new Intent(this,
        BackgroundScanActivity.class); // BackgroundScanActivity와 통신할 Intent생성

    //BackgroundScanActivity로 넘길 옵션값 설정
    i.putExtra("useBlackAppCheck", true);  // 루팅 검사를 실시하면 루팅 우회 앱 설치 여부까지 검사
    i.putExtra("scan_rooting", true);     // 루팅 검사
    i.putExtra("scan_package", true);
    i.putExtra("useDualEngine", true);
    i.putExtra("backgroundScan", true);  // mini 전용
    i.putExtra("rootingexitapp", true);
    i.putExtra("rootingyesorno", true);
    i.putExtra("rootingyes", true);
    i.putExtra("show_update", true);
    i.putExtra("show_license", true);
    i.putExtra("show_notify", true);    // mini 전용
    i.putExtra("notifyClearable", false);    // mini 전용
    i.putExtra("notifyAutoClear", false);    // mini 전용
    i.putExtra("show_toast", true);
    i.putExtra("show_warning", false);
    i.putExtra("show_scan_ui", true);    // mini 전용
    i.putExtra("showBlackAppName", true);

    this.startActivityForResult(i, REQUEST_CODE); //Intent를 보내고 결과값을 얻어옴

  }

  /*----------------- 권장 옵션 (full 모드)--------------------
	검사진행 UI를 제공하는 모드입니다.
	옵션에 따라 백신의 액티비티에서 루팅검사, 악성코드 검사를 실행합니다.
	-------------------------------------------------------*/

  public void full() {
    Intent i = new Intent(this, ScanActivity.class);
    i.putExtra("useBlackAppCheck", true);
    i.putExtra("scan_rooting", true);
    i.putExtra("scan_package", true);
    i.putExtra("useDualEngine", true);
    i.putExtra("dualEngineBackground", true);     // full 전용
    i.putExtra("backgroundJobForLongTime", true); // full 전용
    i.putExtra("useStopDialog", false); // full 전용
    i.putExtra("rootingexitapp", true);
    i.putExtra("rootingyesorno", true);
    i.putExtra("rootingyes", true);
    i.putExtra("show_update", true);
    i.putExtra("show_license", false);
    i.putExtra("show_toast", true);
    i.putExtra("show_warning", false);

    this.startActivityForResult(i, REQUEST_CODE);
  }

  @Override
  public void onBackPressed() {
    finishAffinity();
  }
/*--------------루팅 검사 (별도 API)-------------------
	루팅 여부만 따로 검사해야할 때 사용할 수 있는 루팅체크 별로 API입니다.
	blackAppCheck 값 true/false 로 루팅 우회 앱 설치 여부 검사를 설정 할 수 있습니다.
	아래 샘플 코드를 활용하여 케이스별 분기 처리합니다.
  	--------------------------------------------------------*/

  public void rootingCheck() {

    String message[] = new String[2];
    String strIsRooting = CommonUtil.checkRooting(this, true, message);
    String blackAppName = message[0];
    Log.d("mVaccine ", "strIsRooting " + strIsRooting);

    if (strIsRooting.equals("1")) {
      Log.d("mVaccine ", "Rooting OK !!");
      Toast.makeText(this, "루팅 단말 입니다.", Toast.LENGTH_SHORT)
          .show();
    } else if (strIsRooting.equals("6")) {
      Log.d("mVaccine ", "verify failed");
      Toast.makeText(this, "무결성 검증에 실패 하였습니다..", Toast.LENGTH_SHORT)
          .show();
    } else if (strIsRooting.equals("4")) {
      Log.d("mVaccine ", "BlackApp Installed");
      Toast.makeText(this, blackAppName + "루팅 우회 앱이 설치되어 있습니다.", Toast.LENGTH_SHORT)
          .show();
    } else if (strIsRooting.equals("3")) {
      Log.d("mVaccine ", "BlackApp Installed");
      Toast.makeText(this, "루팅 관련 앱 활동이 탐지되었습니다.", Toast.LENGTH_SHORT)
          .show();
    } else {
      Log.d("mVaccine ", "No Rooting !!");

      Toast.makeText(this, "루팅 단말이 아닙니다.", Toast.LENGTH_SHORT)
          .show();
    }
  }

  /*------------------------- 백신구동 이후 처리  ----------------------------
  백신의 BackgroundScanActivity, ScanActivity 에서
  검사 진행이 완료 된 이후 결과에 따라 처리 할 수 있습니다.
  백신 requestCode, resultCode 값에 따라서 다음과 같이 구현하실 수 있습니다.
  com.secureland.smartmedic.core.AppConstants.ROOTING_EXIT_APP - 루팅단말 [인텐트에 rootingexitapp-true로 백신 액티비티를 실행 했을 때]
  com.secureland.smartmedic.core.AppConstants.ROOTING_YES_OR_NO - 루팅단말 [인텐트에 rootingyesorno-true로 백신 액티비티를 실행 하고 사용자가 yes를 눌렀을 때]
  com.secureland.smartmedic.core.AppConstants.EMPTY_VIRUS - 악성코드, 루팅여부 모두 정상
  com.secureland.smartmedic.core.AppConstants.EXIST_VIRUS_CASE1 - 악성코드 탐지 후 사용자가 해당 악성코드 앱을 삭제
  com.secureland.smartmedic.core.AppConstants.EXIST_VIRUS_CASE2 - 악성코드 탐지 후 사용자가 해당 악성코드 앱을 미삭제
  -------------------------------------------------------------------*/
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    Log.d("mVaccine", "resultCode :" + resultCode +", requestCode = "+requestCode);
    if (requestCode == 777) {
     /* if (resultCode == com.secureland.smartmedic.core.AppConstants.ROOTING_EXIT_APP
          || resultCode == com.secureland.smartmedic.core.AppConstants.ROOTING_YES_OR_NO) {
        this.finish();
      } else if (resultCode == com.secureland.smartmedic.core.AppConstants.EMPTY_VIRUS) {

      } else if (resultCode == com.secureland.smartmedic.core.AppConstants.EXIST_VIRUS_CASE1) {

      } else if (resultCode == com.secureland.smartmedic.core.AppConstants.EXIST_VIRUS_CASE2) {
        finish();
      }*/
      switch (resultCode) {
        case com.secureland.smartmedic.core.Constants.ROOTING_EXIT_APP:
        case com.secureland.smartmedic.core.Constants.ROOTING_YES_OR_NO:
        case com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE2:
          finish();
          break;
        case com.secureland.smartmedic.core.Constants.EMPTY_VIRUS:
        case com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE1:

          mPref.put(VzonPreference.INVESTIGATE_VACCINE, true);
          Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
          startActivity(intent1);
          break;
        default:
          break;
      }
    }
  }

  /*---------------------- 종료구현 --------------------------
        백신이 실행중인지 여부를 체크하여 백신을 종료처리 합니다.
        -------------------------------------------------------*/
  @Override
  protected void onDestroy() {
    super.onDestroy();
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(
        NOTIFICATION_SERVICE);
    mNotificationManager.cancel(MESSAGE_ID);
    mNotificationManager.cancel(MESSAGE_ID1);
    Toast.makeText(getApplicationContext(), "TouchEn mVaccine을 종료합니다.", Toast.LENGTH_SHORT)
        .show();
    this.finish();

  }

}
