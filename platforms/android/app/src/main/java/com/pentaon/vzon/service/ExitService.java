package com.pentaon.vzon.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class ExitService extends Service {

    private static final String TAG = ExitService.class.getSimpleName();

    public ExitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: ");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            clearCache();
        }else
        {
            ((ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        }
        this.stopSelf();
    }

    /**
     * 앱에 쌓여있는 캐시 데이터 삭제
     */
    private void clearCache() {
        try{
            File dir = getApplicationContext().getCacheDir();
            deleteDir(dir);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 해당 파일(sub 폴더 및 파일 포함) 삭제
     * @param dir
     * @return
     */
    private boolean deleteDir(File dir)
    {
        if(dir!=null && dir.isDirectory())
        {
            String[] children = dir.list();
            for(int i=0; i<children.length;i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if(!success) return false;
            }
            return dir.delete();
        }else if(dir!=null&&dir.isFile())
        {
            return dir.delete();
        }else{
            return false;
        }
    }

}
