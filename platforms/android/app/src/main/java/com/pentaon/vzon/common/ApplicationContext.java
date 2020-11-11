package com.pentaon.vzon.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.pentaon.vzon.activity.MainActivity;
import com.pentaon.vzon.activity.PictureListActivity;

/**
 * Created by Pentaon on 14,5월,2018
 */
public class ApplicationContext extends Application{

    private static MainActivity activity = null;
    private static PictureListActivity pictureListActivity = null;

    //  													  0xF0000000 (Activity=0,Dialog=1,Toast=2)
    //  													  0x0FF00000 (General Activity)

    public static final int MSG_NAVIGATION_PASSWD_FIND = 0x10200000;
    public static final int MSG_NAVIGATION_MEMBER_INFO = 0x10300000;
    public static final int MSG_NAVIGATION_DEVICE_INFO = 0x20100000;
    public static final int MSG_NAVIGATION_SERVICE_CONTRACT = 0x000F0000;

    public static final int MSG_NAVIGATION_SERVICE_CONTRACT_DOCUMENTARY_EVIDENCE = MSG_NAVIGATION_SERVICE_CONTRACT | 0x01;
    public static final int MSG_NAVIGATION_SERVICE_CONTRACT_DATA_INPUT = MSG_NAVIGATION_SERVICE_CONTRACT | 0x02;
    public static final int MSG_NAVIGATION_SERVICE_CONTRACT_SIGN = MSG_NAVIGATION_SERVICE_CONTRACT | 0x03;
    public static final int MSG_NAVIGATION_SERVICE_CONTRACT_PREVIEW = MSG_NAVIGATION_SERVICE_CONTRACT | 0x04;
    public static final int MSG_NAVIGATION_SERVICE_CONTRACT_AUTHENTICATE = MSG_NAVIGATION_SERVICE_CONTRACT | 0x05;

    /**
     * 첫 Main이 생성되는 옵션
     */
    public static final int MSG_PARAM_FIRST_MAIN = 0x0000000F; // 외부 솔루션들이 자꾸 죽는다 죽는경우 Login 화면으로 가도록 하자
    // 이 옵션이 있는 경우만 Member 정보 띄우는 걸로..

    //	private static final String TAG = ApplicationContext.class.getSimpleName();
    private static final String TAG = "ApplicationContext";

    private static ApplicationContext instance = null;

    private static SharedInfo sharedInfo = null;

    public boolean mRequestNewState = true;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // Application class 에서 onCreate 에서 수행할 내용을 여기에서 수행하도록 수정
    // dex 로딩 후 이 함수를 최초에 자동으로 호출해 준다.
    public void loaderInitApplication(android.app.Application value) {
        instance = this;
    }


    public ApplicationContext() {
        instance = this;
//        instance.sharedInfo = new SharedInfo(instance);
    }

    public static ApplicationContext getInstance() {

        if (null == instance) {
            instance = new ApplicationContext();
//            instance.sharedInfo = new SharedInfo(instance);
        }
        return instance;
    }

    // TODO: 2018-08-13 ContextWrapper의 mBase 가 null이어서 임의로 attatchBaseContext이용해서 set해줌
        public static void setSharedInfo(Context context)
    {
        if(sharedInfo==null)
        {
            getInstance().attachBaseContext(context);
            sharedInfo = new SharedInfo(context);
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setMainActivity(MainActivity activity) {
        this.activity = activity;
    }

    public PictureListActivity getPictureListActivity() {
        return pictureListActivity;
    }

    public void setPictureListActivity(PictureListActivity pictureListActivity) {
        this.pictureListActivity = pictureListActivity;
    }

    public SharedInfo getSharedInfo() {
        return sharedInfo;
    }

    public static void printWarnLog(String tag, String msg) {
        if (Config.DEBUG) {
            Log.w(tag, msg == null ? "" : msg);
        }
    }

    /**
     * App 종료
     */
    @SuppressLint("NewApi")
    /*public void requestKillProcess() {
        // finish();
        int sdkVersion = Build.VERSION.SDK_INT;
        final ActivityManager am = (ActivityManager) getInstance().getSystemService(Context.ACTIVITY_SERVICE);

        PDFSupport.DestroypdfInatance(); //shlee add
        if (this.activity != null) {
            this.activity.finish();
            this.activity = null;
        }

        // android version 2.2 (SDK code : 8)
        if (sdkVersion < 8) {
            am.restartPackage(getPackageName());

        } else {

            String packageName = getPackageName();
            am.killBackgroundProcesses(packageName);
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }*/

    /**
     * 네트워크 설정화면으로 이동함
     */
    public void doNetWorkSetting() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
