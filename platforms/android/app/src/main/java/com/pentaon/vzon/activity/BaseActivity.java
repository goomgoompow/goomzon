package com.pentaon.vzon.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.pentaon.vzon.R;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.common.VzonPreference;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.SystemUtil;

/**
 * Created by jh.Kim on 14,5월,2018
 */
public abstract class BaseActivity extends Activity{

    public static final String TAG = "BaseActivity";
    private static boolean mIsReversedWindow= false;
    protected ProgressDialog mProgDialog;
    protected VzonPreference mPref;
//    protected Intent mIntent;
//    protected boolean mIsTargetAct = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initStrictMode();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkDensity();
        mPref = new VzonPreference(getApplicationContext());
    }

    private void checkDensity() {
        int dpi = SystemUtil.getDensityDpi(this);
        String density = SystemUtil.getDensity(dpi);
        Log.d(TAG, "checkDensity: dpi = "+dpi +" ,density = "+density);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String action  = intent.getAction();
        Log.d(TAG, "onStart: intent = "+intent );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsReversedWindow)
        {
            if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }else if(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }else
        {
            if(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else if(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        /*// TODO: 2018-12-14 background 여부 판단해서 앱 초기화 기능 구현
        mIntent = getIntent();

        Log.d(TAG, "onResume: mIntent = "+mIntent +" ,mIsTargetAct ="+mIsTargetAct);
        if(mIntent!=null&&mIsTargetAct)
        {
            boolean bFromSomeActivity = mIntent.getBooleanExtra(AppConstants.INTENT_EXTRA_FROM_SOME_ACTIVITY,false);
            Log.d(TAG, "onResume: bFromSomeActivity = "+bFromSomeActivity);

            // TODO: 2018-12-17 check better solution
            //=================================================================================
            // onResume() 호출이 background-> foreground 호출인지, activity간 호출인지 분별하기 위해
            //=================================================================================
            if(!bFromSomeActivity){
                //restart app
                long resumeTime = System.currentTimeMillis();
                checkStartTime(resumeTime);
            }
            mIntent.putExtra(AppConstants.INTENT_EXTRA_FROM_SOME_ACTIVITY,false);
        }*/
    }
/*

    private void checkStartTime(long resumeTime) {
        long savedTime = mPref.getValue(AppConstants.APP_START_TIME, 0L);
        long diff = resumeTime-savedTime;
        Log.d(TAG, "checkStartTime: saved time = "+savedTime +" ,diff = "+diff);
        Toast.makeText(this, "Diff: "+(int)(diff/1000)+"sec", Toast.LENGTH_LONG).show();
        mPref.put(AppConstants.APP_START_TIME,resumeTime);

        if(savedTime!=0 && diff>AppConstants.MILLISEC_A_DAY)
        {
            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
*/

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initLayout();
        initialize();
    }

    public void showProgress(final String msg)
    {
        if(mProgDialog==null)
        {
            mProgDialog=new ProgressDialog(this);
            mProgDialog.setMessage(msg);
            mProgDialog.setCancelable(false);
            mProgDialog.setCanceledOnTouchOutside(false);
            mProgDialog.setIndeterminate(true);
        }
        mProgDialog.show();
    }

    public void hideProgress() {
        if(mProgDialog!=null && mProgDialog.isShowing()){
            mProgDialog.dismiss();
            mProgDialog = null;
        }
    }

    protected abstract void initLayout();//UI 초기화
    protected abstract void initialize();//data 초기화

    private void initStrictMode()
    {
        if(Config.DEBUG)
        {
            StrictMode.enableDefaults();
        }else
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    /**
     * intent에 특정 boolean값을 extend data로 넣어준다.
     * 이 data는 각 activity 의 onResume()의 호출 경로가 background->foreground인지 activity간 이동인지
     * 구별하는데 쓰임.
     * @param resultCode result code(ex,RESULT_OK, RESULT_CANCLED)
     * @param intent target intent
     */
    protected void addInfoAndFinish(int resultCode,Intent intent)
    {
        intent.putExtra(AppConstants.INTENT_EXTRA_FROM_SOME_ACTIVITY, true);
        setResult(resultCode,intent);
        finish();
    }

    /**
     * 서버로 부터 받은 Response의 code 값으로 해당 요청에 대한 오류를 토스트로 알려줌.
     * @param code
     */
    protected void showResponseFailToast(int code) {
        String strNum = Integer.toString(code);
        char head = strNum.charAt(0);
        String message ="";
        switch (head)
        {
            case '4':
                message = getText(R.string.toast_client_error).toString();
                break;
            case '5':
                message = getText(R.string.toast_server_error).toString();
                break;
            default:
                message = getText(R.string.toast_picture_list_message_server_error).toString();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showSimpleToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
