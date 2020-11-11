package com.google.zxing.client.android.act;

import com.google.zxing.client.android.camera.CameraManager;

import android.app.Activity;
import android.os.Handler;

public class ScanActivity extends Activity {
	
	protected CameraManager mCameraManager;
	protected Handler mHandler;
	
	
	public CameraManager getCameraManager() {
		// TODO Auto-generated method stub
		return mCameraManager;
	}
	
	public void setCameraManager(CameraManager mgr) {this.mCameraManager = mgr;}

	public Handler getHandler() {
		// TODO Auto-generated method stub
		return mHandler;
	}
	public void setHandler(Handler handler) {this.mHandler = handler;}
	
	
}
