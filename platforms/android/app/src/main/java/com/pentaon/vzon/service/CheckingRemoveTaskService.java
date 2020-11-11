package com.pentaon.vzon.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.pentaon.vzon.R;
import com.pentaon.vzon.common.VzonPreference;
import com.pentaon.vzon.utils.SystemUtil;


public class CheckingRemoveTaskService extends Service {

    private static final String TAG = CheckingRemoveTaskService.class.getSimpleName();

    public CheckingRemoveTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: ");
        VzonPreference vzonPreference = new VzonPreference(getApplicationContext());
        vzonPreference.put(VzonPreference.INVESTIGATE_VACCINE,false);
//        Toast.makeText(this, getString(R.string.toast_clear_cache_data), Toast.LENGTH_SHORT).show();
        SystemUtil.clearCache();

        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            SystemUtil.clearCache();
        }else
        {
            ((ActManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        }*/
        this.stopSelf();
    }
}

