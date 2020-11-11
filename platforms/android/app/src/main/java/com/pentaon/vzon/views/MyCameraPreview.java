package com.pentaon.vzon.views;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import com.pentaon.vzon.data.WidthHeight;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  private static final int THRESHOLD_SIZE = 2000;
  private static final String TAG = MyCameraPreview.class.getSimpleName();
  private static final float THRESHOLD_RATIO = 0.1f;
  private static final float TARGET_RATIO = 1.333f;
  private final int mWidthPixel;
  private final int mHeightPixel;
  private SurfaceHolder mHolder;
  private Context mContext;
  private Camera mCamera;
  private OnMyCameraPreviewEventListener mListener;
  private WidthHeight mPreviewSize;

  public MyCameraPreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    mHolder = getHolder();
    mHolder.addCallback(this);
    mContext = context;
    mListener = (OnMyCameraPreviewEventListener) mContext;
    mWidthPixel = context.getResources().getDisplayMetrics().widthPixels;
    mHeightPixel = context.getResources().getDisplayMetrics().heightPixels;
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    synchronized (this) {
      if (mCamera == null) {
        mCamera = Camera.open(getCameraId());
        mCamera.setDisplayOrientation(90);
      }

      try {
        mCamera.setPreviewDisplay(mHolder);
      } catch (IOException e) {
        e.printStackTrace();
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
      }
      Log.d(TAG, "surfaceCreated: measuredWidth = "+this.getMeasuredWidth() +",measuredHeight = "+getMeasuredHeight() );
      Log.d(TAG, "surfaceCreated: Width = "+this.getWidth() +",Height = "+getHeight() );
//      mCameraParam = mCamera.getParameters();
//      mCameraParam=getOptimalSize(mCameraParam,NOT_DEFINED_SIZE,NOT_DEFINED_SIZE);
//      mCamera.setParameters(mCameraParam);
      mListener.onReadyForUsingCamera();
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    try {
      Camera.Parameters params = mCamera.getParameters();
      mCamera.setParameters(getOptimalSize(params));
      ViewGroup.LayoutParams prms = getLayoutParams();
      prms.height=getResizePixels(mPreviewSize).getHeight();
      prms.width=getResizePixels(mPreviewSize).getWidth();
      setLayoutParams(prms);


    }catch (RuntimeException e){
      e.printStackTrace();
    }finally {
      mCamera.startPreview();
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (mCamera != null) {
      mCamera.stopPreview();
      mCamera.release();
      mCamera = null;
    }
  }

  //---------------------------------------------------------
  // public methods
  //---------------------------------------------------------
  public Camera getCamera() {
    return mCamera;
  }

  //---------------------------------------------------------
  // private methods
  //---------------------------------------------------------
  private int getCameraId(){
    int cameraId = -1;
    int numberOfCameras = Camera.getNumberOfCameras();
    for (int i = 0; i < numberOfCameras; i++) {
      Camera.CameraInfo info = new Camera.CameraInfo();
      Camera.getCameraInfo(i, info);
      if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        cameraId = i;
        break;
      }
    }
    if (cameraId == -1) {
      cameraId = 0;
    }
    return cameraId;
  }


  private Parameters getOptimalSize(Parameters param) {
    List<Size> previewSize = getDescendingSize(param.getSupportedPreviewSizes());
    List<Size> pictureSize = getDescendingSize(param.getSupportedPictureSizes());

    for (Camera.Size pictures : pictureSize) {
      if (pictures.width > THRESHOLD_SIZE) {
        continue;
      }
      for (Camera.Size previews : previewSize) {
        float ratioPreview = getRatio(pictures);
        float ratioPicture = getRatio(previews);
        float diffRatio = Math.abs(ratioPicture- ratioPreview);
        if (diffRatio < THRESHOLD_RATIO //Picture Size, preview Size 비율이 0.1f 차이 미만
            &&getDiffRatio(ratioPicture,TARGET_RATIO)<0.02f //pictureSize 비율이 4:3 비율에 가깝게
            &&getDiffRatio(ratioPreview,TARGET_RATIO)<0.02f//preview 비율이 4:3 비율에 가깝게
        ){
          param.setPictureSize(pictures.width, pictures.height);
          param.setPreviewSize(previews.width, previews.height);
          mPreviewSize = new WidthHeight(previews);
          return param;
        }
      }
    }
    return param;
  }


  private WidthHeight getResizePixels(WidthHeight size){
    int width = size.getWidth();
    int height = size.getHeight();

    int resizeHeight = mWidthPixel*width/height;

    return new WidthHeight(mWidthPixel,resizeHeight);
  }

  private float getDiffRatio(float ratio, float targetRatio){
    return Math.abs(ratio-targetRatio);
  }


  private float getRatio(Camera.Size size) {
    return (size.width > size.height) ? (float) size.width / size.height
        : (float) size.height / size.width;
  }
  private float getRatio(int width, int height) {
    return (width > height) ? (float) width / height
        : (float) height / width;
  }

  private List<Size> getDescendingSize(List<Size> sizeList) {
    if (sizeList.size() == 0) {
      return null;
    }
    Size firstOne = sizeList.get(0);
    Size lastOne = sizeList.get(sizeList.size() - 1);
    ArrayList<Size> temp = new ArrayList<>();
    for (Size size : sizeList) {
      temp.add(size);
    }
    if (firstOne.width < lastOne.width && firstOne.height < lastOne.height) {
      for (int i = 0; i < sizeList.size(); i++) {
        sizeList.set(i, temp.get(sizeList.size() - (i + 1)));
      }
    }
    return sizeList;
  }

  public interface OnMyCameraPreviewEventListener{
    void onReadyForUsingCamera();
  }
}
