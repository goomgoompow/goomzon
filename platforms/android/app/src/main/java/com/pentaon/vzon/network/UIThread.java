package com.pentaon.vzon.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by jongHwan.Kim  on 20,7월,2018
 */
public class UIThread {

    private static Handler mHandler = null;

    private UIThread(){
        initHandler();}

    public static UIThread getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder
    {
        private static final UIThread INSTANCE = new UIThread();
    }

    private void initHandler() {

        if(mHandler==null)
        {
            Log.d("UIThread", "initHandler: mHandler is null");
            mHandler=new Handler();
        }
    }
    public void executeInUIThread(Runnable r){mHandler.post(r);}
    public void executeInUIThread(Runnable r, long time){mHandler.postDelayed(r, time);}

}
