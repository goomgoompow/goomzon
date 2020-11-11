package com.pentaon.vzon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pentaon.vzon.R;
import com.pentaon.vzon.network.ApiClient;
import com.pentaon.vzon.network.ApiInterface;
import com.pentaon.vzon.pojo.PictureResult;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.views.MyCameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InstallationCaptureActivity extends BaseActivity implements View.OnClickListener,
    MyCameraPreview.OnMyCameraPreviewEventListener {

  private static final String TAG = "SampleCamera_MainAct";
  private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
  private File mCapturedFile;
  private ApiInterface mApiInterface;
  private ArrayList<String> mKeys = new ArrayList<>();
  private ArrayList<String> mArrFilePath = new ArrayList<>();

  Camera mCamera;
  Context mContext;
  Intent mPassedIntent;
  private MyCameraPreview mCameraPreview;
  private Intent mIntent;
  private String mTempPath;
  private HashMap<String, Object> mHashMap;
  private int mWidthPixel;
  private int mHeightPixel;
  private String mFileName;
  private LinearLayout mResultLayout;
  private RelativeLayout mContainer;
  private ImageView mCapturedImage;
  private Bitmap mBitmap;
  private boolean mIsShowingCapturedImage;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_installation_capture);
    mIntent = getIntent();
    mHashMap = (HashMap<String, Object>) mIntent
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);
    mTempPath = (String) mHashMap.get(AppConstants.INTENT_EXTRA_TEMP_PATH);
  }

  @Override
  public void onBackPressed() {
    backToPictureList();
  }

  private void backToPictureList() {
    mHashMap.put(AppConstants.INTENT_EXTRA_FILE_NAME,mFileName);
//    mIntent.putExtra(AppConstants.INTENT_EXTRA_INSTALLATION_FILE_PATH, mArrFilePath);
    setResult(RESULT_OK, mIntent);
    finish();
  }

  Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {

      //set a width, height of the image.
      int w = camera.getParameters().getPictureSize().width;
      int h = camera.getParameters().getPictureSize().height;

      int orientation = setCameraDisplayOrientation(InstallationCaptureActivity.this, CAMERA_FACING,
          camera);

      //convert a byte array to mBitmap
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

      //이미지를 디바이스 방향으로 회전
      Matrix matrix = new Matrix();
      matrix.postRotate(orientation);
      mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, w, h, matrix, true);

      //파일로 저장
      new SaveImageTask().execute();
//      resetCamera();
    }
  };

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void initLayout() {
    mContainer = findViewById(R.id.container_layout_installation);
    ImageButton btnCapture = findViewById(R.id.act_main_img_btn_capture);
    mCameraPreview = findViewById(R.id.act_installation_capture_preview);
    btnCapture.setOnClickListener(this);
  }

  @Override
  protected void initialize() {
    mContext = InstallationCaptureActivity.this;
    Retrofit retrofit = ApiClient.getClient();
    mApiInterface = retrofit.create(ApiInterface.class);
    mPassedIntent = getIntent();
    mWidthPixel = getResources().getDisplayMetrics().widthPixels;
    mHeightPixel = getResources().getDisplayMetrics().heightPixels;
    mResultLayout = new LinearLayout(this);
    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    layoutInflater.inflate(R.layout.image_viewer_dialog,mResultLayout);
    mContainer.addView(mResultLayout);
    mResultLayout.setVisibility(View.INVISIBLE);

    mCapturedImage = mResultLayout.findViewById(R.id.image_view_ocr);
    LinearLayout btnRecapture = mResultLayout.findViewById(R.id.btn_recapture);
    LinearLayout btnSave = mResultLayout.findViewById(R.id.btn_save);

    btnRecapture.setOnClickListener(this);
    btnSave.setOnClickListener(this);
    mIsShowingCapturedImage = false;
  }

  private static int setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
    Camera.CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(cameraId, info);

    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    int degree = 0;
    switch (rotation) {
      case Surface.ROTATION_0:
        degree = 0;
        break;
      case Surface.ROTATION_90:
        degree = 90;
        break;
      case Surface.ROTATION_180:
        degree = 180;
        break;
      case Surface.ROTATION_270:
        degree = 270;
        break;
    }
    int result = 0;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degree) % 360;
      result = (360 - result) % 360;
    } else {
      result = (info.orientation - degree + 360) % 360;
    }
    return result;
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.act_main_img_btn_capture:
        if(mIsShowingCapturedImage)return;
        mCamera.autoFocus(new AutoFocusCallback() {
          @Override
          public void onAutoFocus(boolean success, Camera camera) {
            synchronized (this) {
              mCamera.takePicture(null, null, jpegCallback);
            }
          }
        });
        showProgress(getString(R.string.common_progress_loading));
        break;

      case R.id.btn_recapture:
        mResultLayout.setVisibility(View.INVISIBLE);
        mCapturedImage.setImageBitmap(null);
        mCameraPreview.setVisibility(View.VISIBLE);
        mIsShowingCapturedImage = false;
        break;

      case R.id.btn_save:
        backToPictureList();
        break;
    }
  }

  @Override
  public void onReadyForUsingCamera() {
    mCamera = mCameraPreview.getCamera();
  }

  private class SaveImageTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... aVoid) {
      FileOutputStream outputStream = null;
      try {
        /*File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/sampleCamera");
        if (!dir.exists()) {
          dir.mkdir();
        }*/
        File dir = new File(mTempPath);
        if (!dir.exists()) {
          dir.mkdirs();
        }
        mFileName = String.format("install_%d.jpg", System.currentTimeMillis());
        /*StringBuilder builder = new StringBuilder();
        builder.append(mFileName);
        builder.append(".jpg");
        mFileName=builder.toString();*/
        mCapturedFile = new File(dir, mFileName);
        if (!mCapturedFile.exists()) {
          mCapturedFile.createNewFile();
        }

        outputStream = new FileOutputStream(mCapturedFile);
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

        mArrFilePath.add(mCapturedFile.getAbsolutePath());
        refreshGallery(mCapturedFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      hideProgress();
      showCapturedImage();
      mCameraPreview.setVisibility(View.GONE);
      mIsShowingCapturedImage = true;
//      uploadCaptureFile();
//      backToPictureList();

    }
  }

  private void showCapturedImage() {
    mResultLayout.setVisibility(View.VISIBLE);
    mCapturedImage.setImageBitmap(mBitmap);
  }

  private void refreshGallery(File file) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    mediaScanIntent.setData(Uri.fromFile(file));
    sendBroadcast(mediaScanIntent);
  }

  /*private void resetCamera() {
    startCamera();
  }*/


  private String getTempDirectoryPath() {
    File cache = null;

    // SD Card Mounted
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      cache = this.getExternalCacheDir();
    }
    // Use internal storage
    else {
      cache = this.getCacheDir();
    }

    // Create the cache directory if it doesn't exist
    cache.mkdirs();
    return cache.getAbsolutePath();
  }

  private void uploadCaptureFile() {
    showProgress(getString(R.string.common_progress_loading));
    //InstallPictureInfo pictureInfo = new InstallPictureInfo(KEY_INSTALL_PICTURE,mCapturedFile );
    String pathCapturedFile = mCapturedFile.getAbsolutePath();
    Log.d(TAG, "uploadCaptureFile: mCapturedFile = " + pathCapturedFile);
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(AppConstants.KEY_FILE, pathCapturedFile,
            RequestBody.create(MediaType.parse("image/jpeg"), mCapturedFile))
        .build();

    Call<PictureResult> call = mApiInterface.uploadInstallPicture(requestBody);
    call.enqueue(new Callback<PictureResult>() {
      @Override
      public void onResponse(Call<PictureResult> call, Response<PictureResult> response) {
        hideProgress();
        if (response.isSuccessful()) {
          Log.d(TAG, "onResponse: is Successful");
          List<PictureResult.Embedded.Content> contents = response.body().embedded.content;
          int size = contents.size();
          for (int i = 0; i < size; i++) {
            mKeys.add(contents.get(i).fileKey);
          }
          backToPictureList();
        } else {
          Log.d(TAG, "onResponse: NOT is Successful");
        }
      }

      @Override
      public void onFailure(Call<PictureResult> call, Throwable t) {
        hideProgress();
        Toast
            .makeText(InstallationCaptureActivity.this, "Response is Failure!!", Toast.LENGTH_SHORT)
            .show();
      }
    });
  }




}
