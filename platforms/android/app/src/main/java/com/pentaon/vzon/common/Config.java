package com.pentaon.vzon.common;

import android.content.Context;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

import com.pentaon.vzon.utils.AppConstants;

import java.io.File;

/**
 * Created by jh.Kim on 14,5월,2018
 *
 * @desc : 각종 설정값 정의
 */
public class Config {
    private static final String TAG = Config.class.getSimpleName();
    public static String BASE_URL = AppConstants.REAL_SERVER_URL;
    public static boolean WIFI_MODE = false; 	  // WiFi 허용 :true, WiFi 허용하지 않음:false
    public static boolean DEBUG = false; 		  // Log 출력:true, 미출력:false
    public  static boolean EXTERNAL_MODE = true; //외부 저장소 사용: true, 사용안함: false;
    // PDF 정식 라이선스 HiPDFReader[pentaon, 임시] 버전 (패키지명 : com.pentaon.bizfastcard) (KNOX 2.x 지원 버전, 롤리팝 지원 버전)
    public static final String PDF_LICENSE_KEY = "PDF_LIENSE_KEY";

    public static String getPackageURL() {
        if (Config.EXTERNAL_MODE) {
            if(Config.DEBUG)
            {
                Log.d(TAG,"##### getPackageURL() : EXTERNAL_MODE == " + Config.EXTERNAL_MODE + " : "
                        + ApplicationContext.getInstance().getExternalFilesDir(null).getPath() + File.separator);
            }
            return ApplicationContext.getInstance().getExternalFilesDir(null).getPath() + File.separator;
        }
        else {
            if(Config.DEBUG)
            {
                Log.d(TAG,"##### getPackageURL() : EXTERNAL_MODE == " + Config.EXTERNAL_MODE+ " : "
                        + ApplicationContext.getInstance().getDir("files", Context.MODE_PRIVATE).getPath() + File.separator);
            }
            return ApplicationContext.getInstance().getDir("files", Context.MODE_PRIVATE).getPath() + File.separator;
        }
    }

    public static void setBaseUrl(String name)
    {
        String url = "";
        switch (name)
        {
            case "53":
            case "94":
            case "95":
            case "96":
                BASE_URL ="http://192.168.0.92:18080/";
                String start = BASE_URL.substring(0,17);
                String end = BASE_URL.substring(19);
                url=start+name+end;
                break;
            case "tapp":
                url= AppConstants.DEV_URL;
                break;
            case "demoWeb":
                url = AppConstants.TEST_SERVER_URL;
                break;
            case "real":
                url = AppConstants.REAL_SERVER_URL;
                break;
        }
        BASE_URL = url;
    }
}
