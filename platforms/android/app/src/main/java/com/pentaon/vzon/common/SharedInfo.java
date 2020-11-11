package com.pentaon.vzon.common;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by jh.Kim on 14,5월,2018
 */
public class SharedInfo {

    private static final String TAG  = "SharedInfo";


    private Context mContext;
    //임시 저장 Path
    private String mCachePathName;

    // pdf 저장 Path
    private String mPdfPathName;

    private HashMap<String , Object> mInstantData;

    public SharedInfo(Context context)
    {
        mContext = context;
    }

    public static SharedInfo getInstance()
    {
        return ApplicationContext.getInstance().getSharedInfo();
    }

    public String getCachePath() {
        if(null == this.mCachePathName)
        {
            if (Config.EXTERNAL_MODE) {
                if(Config.DEBUG)
                {
                    Log.d(TAG,"##### getCachePath() : EXTERNAL_MODE == " + Config.EXTERNAL_MODE + " : "
                            + getExternalCacheDir().getAbsolutePath() + File.separator);
                }
                this.mCachePathName = getExternalCacheDir().getAbsolutePath() + File.separator;
            } else {
                if(Config.DEBUG)
                {
                    File cDir = getInternalCacheDir();
                    String aPath = cDir.getAbsolutePath();

                    Log.d(TAG,"##### getCachePath() : EXTERNAL_MODE == " + Config.EXTERNAL_MODE + " : "
                            + getInternalCacheDir().getAbsolutePath() + File.separator);
                }
                this.mCachePathName = getInternalCacheDir().getAbsolutePath() + File.separator;
            }
        }

        return this.mCachePathName;
    }
    /**
     * @return  File : /data/data/&lt;package name&gt;/cache
     */
    private File getInternalCacheDir() {
        Log.d(TAG, "getInternalCacheDir: ");
        return mContext.getCacheDir();
    }

    /**
     * @return  File : /storage/emulated/0/Android/data/&lt;package name&gt;/cache
     */
    private File getExternalCacheDir() {
        return mContext.getExternalCacheDir();
    }


    /**
     *
     * @return "/data/data/&lt;package name&gt;/files/"
     */
    public String getmPdfPathName()
    {
        if(null == this.mPdfPathName)
        {

            this.mPdfPathName = getFilesDir().getAbsolutePath() + File.separator;
        }

        return this.mPdfPathName;
    }

    /**
     *
     * @return  File : /data/data/&lt;package name&gt;/files
     */
    public File getFilesDir()
    {
        return mContext.getFilesDir();
    }

}