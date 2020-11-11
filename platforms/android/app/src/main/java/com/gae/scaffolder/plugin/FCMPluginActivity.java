package com.gae.scaffolder.plugin;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pentaon.vzon.activity.MainActivity;
import com.pentaon.vzon.common.VzonPreference;
import com.pentaon.vzon.utils.SystemUtil;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FCMPluginActivity extends Activity {

  public static boolean IS_TAPPED = false;
  private static String TAG = "FCMPlugin";

  /*
   * this activity will be started if the user touches a notification that we own.
   * We send it's data off to the push plugin for processing.
   * If needed, we boot up the main activity to kickstart the application.
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "==> FCMPluginActivity onCreate");

    Map<String, Object> data = new HashMap<String, Object>();
    if (getIntent().getExtras() != null) {
      Log.d(TAG, "==> USER TAPPED NOTFICATION");
      data.put("wasTapped", true);
      for (String key : getIntent().getExtras().keySet()) {
        String value = getIntent().getExtras().getString(key);
        Log.d(TAG, "\tKey: " + key + " Value: " + value);
        data.put(key, value);
      }
    }

    FCMPlugin.sendPushPayload(data);

    finish();

    boolean hasCheckedVaccine = new VzonPreference(getApplicationContext())
        .getValue(VzonPreference.INVESTIGATE_VACCINE, false);
    if (!hasCheckedVaccine) {
      Log.d(TAG, "&&& onCreate: restart App");
      forceMainActivityReload();
    }else if(!isMainActivityTop())
    {
      Log.d(TAG, "&&& onCreate: Start MainAct");
      startActivity(new Intent(FCMPluginActivity.this,MainActivity.class));
    }
  }

  private void forceMainActivityReload() {
    PackageManager pm = getPackageManager();
    Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
    startActivity(launchIntent);

        /*Intent intent = new Intent(FCMPluginActivity.this, MainActivity.class);
        startActivity(intent);*/
  }

  private boolean isMainActivityTop()
  {
    ActivityManager activityManager=((ActivityManager)getSystemService(ACTIVITY_SERVICE));
    List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(9);
    if(runningTaskInfos.size()>0) return false;
    else return (runningTaskInfos.get(0).topActivity.toString().equals(MainActivity.class.getCanonicalName()));
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "==> FCMPluginActivity onResume");
    final NotificationManager notificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "==> FCMPluginActivity onStart");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "==> FCMPluginActivity onStop");
  }
}