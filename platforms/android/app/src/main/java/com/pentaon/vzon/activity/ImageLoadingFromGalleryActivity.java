package com.pentaon.vzon.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.pentaon.vzon.R;
import com.pentaon.vzon.adapter.GridAdapter;
import com.pentaon.vzon.data.PhotoVO;
import com.pentaon.vzon.manager.GalleryManager;
import com.pentaon.vzon.network.ApiClient;
import com.pentaon.vzon.network.ApiInterface;
import com.pentaon.vzon.pojo.PictureResult;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.SystemUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImageLoadingFromGalleryActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = ImageLoadingFromGalleryActivity.class.getSimpleName();
  private static final int SELECT_IMAGE = 0X0001;
  private static final int ROTATE_IMAGE = 0x0002;
  private static final int SAVE_BITMAP_AT_FILE = 0x0003;

  private ArrayList<String> mKeys = new ArrayList<>();
  private Intent mIntent;
  private char mContractProof;
  private String mTempPath, mImagePath;
  private String mImageName;
  private HashMap<String, Object> mSelectedDocInfoMap;

  private File mGalleryFile;
  private LinearLayout mButtonNavigationBack, mButtonSave;
  private ImageView mBigImage;
  private ImageView mButtonRotateImage;
  private GridView mGridView;
  private GalleryManager mGalleryManager;
  private ArrayList<PhotoVO> mListAllPhotoPath;
  private GridAdapter mGridAdapter;
  private ConstraintLayout mContainerButtons;
  private boolean mIsShowExtendedImage; //갤러리 gridd에서 이미지 선택해서 큰 이미지 보여지고 있는지 여부
  private boolean mIsEnableEditMode; //사각잡기 편집 진행 여부(true, 사각잡기 편집 모드로 진입, false: 서버로 업로드)
  private ConstraintLayout mContainerGrid;
  private WorkerThread mWorkerThread;
  private ImageHandler mImageHandler = new ImageHandler(this);
  private PhotoVO mPhotoVO;
  private final int[] ROTATE_DEGREE = {0, 90, 180, 270};
  private int mDocType;
  private int mImageRotation;
  private int mIndex;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mIntent = getIntent();
    mSelectedDocInfoMap = (HashMap<String, Object>) mIntent
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);
    String docKind = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_DOC_KIND);
    mDocType = (docKind.equals(AppConstants.DOC_KIND_B)) ? AppConstants.DOC_TYPE_ID
        : AppConstants.DOC_TYPE_A4;
    mContractProof = (char) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_CONTRACT_PROOF);
    mTempPath = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_TEMP_PATH);
    mImagePath = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_IMAGE_PATH);
    mImageName = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_FILE_NAME);
    mIsEnableEditMode = mIntent.getBooleanExtra(AppConstants.INTENT_EXTRA_ALLOW_EDIT_MODE, false);
    setContentView(R.layout.activity_image_loading_from_gallery);
//    init(mIntent);
  }

  @Override
  protected void initLayout() {
    mContainerButtons = findViewById(R.id.container_buttons);
    mContainerGrid = findViewById(R.id.container_grid);
    mButtonNavigationBack = findViewById(R.id.act_image_loading_button_navigation_back);
    mButtonSave = findViewById(R.id.act_image_loading_button_save);
    mBigImage = findViewById(R.id.act_image_loading_big_image);
    mGridView = findViewById(R.id.act_image_loading_grid_view);
    mButtonRotateImage = findViewById(R.id.btn_rotate_image);
    mButtonSave.setVisibility(View.GONE);
    mButtonRotateImage.setVisibility(View.GONE);
  }

  @Override
  protected void initialize() {
    ConstraintLayout.LayoutParams layoutParams = (LayoutParams) mContainerButtons.getLayoutParams();
    Log.d(TAG, "initialize: layoutParams = " + layoutParams.height);

//    int height = getResources().getDisplayMetrics().heightPixels;

    Point point = new Point();
    ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealSize(point);

    int height = point.y;

    LayoutParams gridLayoutParams = (LayoutParams) mContainerGrid.getLayoutParams();
    gridLayoutParams.height = height - layoutParams.height;

    mContainerGrid.setLayoutParams(gridLayoutParams);

    mButtonNavigationBack.setOnClickListener(this);
    mButtonSave.setOnClickListener(this);
    mButtonRotateImage.setOnClickListener(this);
    mBigImage.setVisibility(View.GONE);

    mGalleryManager = new GalleryManager(this);
    mListAllPhotoPath = mGalleryManager.getAllPhotoList();
    initGridAdapter();

    int widthPixels = getResources().getDisplayMetrics().widthPixels;
    int heightPixels = getResources().getDisplayMetrics().heightPixels;
    mGridView.setColumnWidth(widthPixels / 3);

    mGridView.setClickable(false);
    mGridView.setFocusable(false);
    startWorkerThread();
  }

  private void startWorkerThread() {
    mWorkerThread = new WorkerThread();
    mWorkerThread.start();
  }

  private void initGridAdapter() {
    mGridAdapter = new GridAdapter(this, mListAllPhotoPath);
    mGridView.setAdapter(mGridAdapter);
    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mIsShowExtendedImage) {
          mGridView.setEnabled(false);
          mGridView.setVisibility(View.INVISIBLE);
          mPhotoVO = mGridAdapter.getItem(position);
          mIndex = 0;
          mWorkerThread.selectImage();
        }
      }
    });
  }

  private void showExtendedImageFromPhotoVo(PhotoVO photoVo) {
    String path = photoVo.getImagePath();
    mImageRotation = photoVo.getRotation();
    Log.d(TAG,
        "showExtendedImageFromPhotoVo: path = " + path + " ,orientation = " + mImageRotation
            + " ,mImagePath = " + mImagePath);
    File imgFile = new File(mImagePath);
    if (imgFile.exists()) {
      Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
      if(bitmap!=null&&mImageRotation!=0){
        Matrix matrix = new Matrix();
        matrix.setRotate(mImageRotation);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        mWorkerThread.saveBitmapAtFile(bitmap);
      }
//      bitmap = rotateBitmap(bitmap, mImageRotation);

      mBigImage.setImageBitmap(bitmap);
      mBigImage.setVisibility(View.VISIBLE);
      mIsShowExtendedImage = true;
    } else {
      mGridView.setEnabled(true);
      Toast.makeText(this, getText(R.string.toast_image_loading_fail), Toast.LENGTH_SHORT).show();
    }
  }

  @Deprecated
  private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
    Matrix matrix = new Matrix();
//    boolean isRotated = true;
    switch (orientation) {
      case ExifInterface.ORIENTATION_NORMAL:
//        isRotated= false;
        return bitmap;
      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
        matrix.setScale(-1, 1);
        break;
      case ExifInterface.ORIENTATION_ROTATE_180:
        matrix.setRotate(180);
        break;
      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        matrix.setRotate(180);
        matrix.postScale(-1, 1);
        break;
      case ExifInterface.ORIENTATION_TRANSPOSE:
        matrix.setRotate(90);
        matrix.postScale(-1, 1);
        break;
      case ExifInterface.ORIENTATION_ROTATE_90:
        matrix.setRotate(90);
        break;
      case ExifInterface.ORIENTATION_TRANSVERSE:
        matrix.setRotate(-90);
        matrix.postScale(-1, 1);
        break;
      case ExifInterface.ORIENTATION_ROTATE_270:
        matrix.setRotate(-90);
        break;
      default:
//        isRotated= false;
        return bitmap;
    }

    try {
      Bitmap bmRotated = Bitmap
          .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//      if(isRotated)mWorkerThread.saveBitmapAtFile(bmRotated);
      bitmap.recycle();
      return bmRotated;
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
    }
    return null;
  }

/*
  private void init(Intent intent) {
    Log.d(TAG, "init: intent = " + intent);
    Intent intent1 = new Intent(Intent.ACTION_PICK);
    intent1.setType(Media.CONTENT_TYPE);
    startActivityForResult(intent1, AppConstants.REQUEST_GO_TO_GALLERY);
  }*/

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    boolean isCanceled = data.getBooleanExtra(AppConstants.INTENT_EXTRA_SCAN_USER_CANCEL,false);
    if (resultCode != RESULT_OK || data == null ||isCanceled) {
      showToastAndFinish();
    }else if(requestCode==AppConstants.REQUEST_SET_POINT){
      showProgress(getString(R.string.common_progress_loading));
      String imagePath = data.getStringExtra(AppConstants.INTENT_EXTRA_IMAGE_PATH);
      uploadCaptureFile(imagePath);
    }
  }

  private void uploadCaptureFile(String path) {
    MultipartBody.Builder builder = new Builder().setType(MultipartBody.FORM);
    mGalleryFile = new File(path);
    if(!mGalleryFile.exists()){
      showToastAndFinish();
      return;
    }
    builder.addFormDataPart(AppConstants.KEY_FILE,
        mGalleryFile.getAbsolutePath(), RequestBody.create(MediaType.parse(AppConstants.TYPE_JPEG), mGalleryFile));
    RequestBody requestBody = builder.build();
    Retrofit retrofit = ApiClient.getClient();
    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    Call<PictureResult> call = (mContractProof == 'H') ?
        apiInterface.uploadInstallDocument(requestBody) :
        apiInterface.uploadInstallPicture(requestBody);

    call.enqueue(new Callback<PictureResult>() {
      @Override
      public void onResponse(Call<PictureResult> call, Response<PictureResult> response) {
        hideProgress();
        if (response.isSuccessful()) {
          List<PictureResult.Embedded.Content> contents = response.body().embedded.content;
          if (contents != null) {
            mKeys.add(contents.get(0).fileKey);
            mIntent.putExtra(AppConstants.INTENT_EXTRA_FROM_INSTALLATION_ACT, mKeys);
            addInfoAndFinish(RESULT_OK, mIntent);
            Toast.makeText(ImageLoadingFromGalleryActivity.this, getText(R.string.image_saved),
                Toast.LENGTH_SHORT).show();
          }
        } else {
          addInfoAndFinish(RESULT_CANCELED, mIntent);
          showResponseFailToast(response.code());
        }
        SystemUtil.deleteFile(mGalleryFile.getAbsolutePath());
      }

      @Override
      public void onFailure(Call<PictureResult> call, Throwable t) {
        hideProgress();
        Log.d(TAG, "onFailure: ");
        Toast.makeText(ImageLoadingFromGalleryActivity.this,
            getText(R.string.toast_picture_list_message_server_error).toString(),
            Toast.LENGTH_SHORT).show();
        addInfoAndFinish(RESULT_OK, mIntent);
        SystemUtil.deleteFile(mGalleryFile.getAbsolutePath());
      }
    });
  }

  private void showToastAndFinish() {
    Toast.makeText(ImageLoadingFromGalleryActivity.this, getString(R.string.toast_image_loading_fail), Toast.LENGTH_SHORT).show();
    setResult(RESULT_CANCELED);
    finish();
  }

  /**
   * 특정 경로 파일을 jpg 형식 파일로 바꾼다.
   */
  private File createNewJpegFile(String path) {
    String filePath = mTempPath + mImageName + ".jpg";
    File dir = new File(mTempPath);
    File file = new File(filePath);

    try {
      if (!dir.exists()) {
        dir.mkdirs();
      }
      if (!file.exists()) {
        file.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(file);
//      Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
      Bitmap bitmap = BitmapFactory.decodeFile(path);
      bitmap.compress(CompressFormat.JPEG, 90, fos);
      fos.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return file;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mImageRotation = 0;
    mBigImage.setRotation(0);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.act_image_loading_button_navigation_back:
        int toVisibility = (mBigImage.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        //1.close the extended image

        //2.close activity
        switch (toVisibility) {
          case View.VISIBLE:
            setResult(RESULT_CANCELED);
            Toast.makeText(this, getText(R.string.toast_image_loading_fail), Toast.LENGTH_SHORT)
                .show();
            finish();
            break;

          case View.GONE:
            mIsShowExtendedImage = false;
            mButtonSave.setVisibility(View.GONE);
            mBigImage.setVisibility(toVisibility);
            mGridView.setEnabled(true);
            mGridView.setVisibility(View.VISIBLE);
            break;
          default:
        }

        break;
      case R.id.act_image_loading_button_save:
        if (mIsEnableEditMode) {
//          mPhotoVO.setRotation(mImageRotation);

          Intent intent = new Intent(this, PointChangedActivity.class);
//          intent.putExtra(AppConstants.INTENT_EXTRA_FROM_GALLERY, mPhotoVO);
//          mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_IMAGE_PATH);
          intent.putExtra(AppConstants.INTENT_EXTRA_FROM_GALLERY,true);
          mSelectedDocInfoMap.put(AppConstants.INTENT_EXTRA_IMAGE_PATH,mImagePath);
          intent
              .putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfoMap);
          startActivityForResult(intent,AppConstants.REQUEST_SET_POINT);

//          String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ghoom/20190826_180016.jpg";
//          File file = new File(path);
//          Log.d(TAG, "onClick: file.exists() "+file.exists());

          /*IntentInfo intentInfo = new IntentInfo(this);
          intentInfo.setLicenseKey(AppConstants.LICENSE_KEY)
              .setCIImageResource(R.mipmap.icon_outlined)
              .setTargetDocumentType(mDocType)
//              .setImageColorType(colorType)
              .setOutlineColor(AppConstants.LINE_COLOR)
              .setLineThickness(AppConstants.LINE_THICK)
              .setIdTrialMaxNum(Constants.DEFAULT_ID_TRIAL)
              .setOcrTrialMaxNum(Constants.DEFAULT_OCR_TRIAL)
              .setResultDisplayYN(Constants.RESULT_DISPLAY_Y)
              .setFileDirPath(mPhotoVO.getImagePath())
              .setIsShowFaceArea(false)
              .setSecretkey(mCrypto.getSecretKey())
              .setIsFromGallery(true)
              .startForResult(Constants.REQUEST_CAMERA_OCR);*/

        }
        else {
          showProgress(getString(R.string.common_progress_loading));
          uploadCaptureFile(mPhotoVO.getImagePath());
        }
        break;

      case R.id.btn_rotate_image:
        mWorkerThread.rotateImage();

        break;

      default:

    }
  }

  private void showButtons() {
    mButtonSave.setVisibility(View.VISIBLE);
    mButtonRotateImage.setVisibility(View.VISIBLE);
  }

  private void rotateImage() {
//    int mIndex = Arrays.binarySearch(ROTATE_DEGREE,mImageRotation);
    /*Log.d(TAG, "onClick: btn_rotate_image = " + mIndex);
    mIndex++;
    if (mIndex >= ROTATE_DEGREE.length) {
      mIndex = 0;
    }*/

//              mBigImage.setRotation(ROTATE_DEGREE[mIndex]);
//    mImageRotation = 90;//ROTATE_DEGREE[mIndex]
//    mIndex++;
//    if (mIndex >= ROTATE_DEGREE.length) {
//      mIndex = 0;
//    }
    Bitmap bitmap = ((BitmapDrawable) mBigImage.getDrawable()).getBitmap();
    Matrix matrix = new Matrix();
//    matrix.setRotate(ROTATE_DEGREE[mIndex],0,0);
    matrix.preRotate(90);
    bitmap = Bitmap
        .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    mBigImage.setImageBitmap(bitmap);
    mWorkerThread.saveBitmapAtFile(bitmap);
  }


  private static class ImageHandler extends Handler {

    private WeakReference<ImageLoadingFromGalleryActivity> mActivity;

    public ImageHandler(ImageLoadingFromGalleryActivity activity) {
      this.mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SELECT_IMAGE:
          PhotoVO vo = (PhotoVO) msg.obj;
          mActivity.get().showExtendedImageFromPhotoVo(vo);
          mActivity.get().showButtons();
          break;
        case ROTATE_IMAGE:
          mActivity.get().rotateImage();
          break;
        default:
      }
    }
  }

  private void copyFile(String origin, String copy) {
    boolean isFinishedCopy = SystemUtil.copyFile(origin, copy);
    if (!isFinishedCopy) {
      showSimpleToast(getText(R.string.toast_image_loading_fail).toString());
      setResult(RESULT_CANCELED);
      finish();
    }
  }

  private class WorkerThread extends HandlerThread {


    private Handler mHandler;


    WorkerThread() {
      super("ImageLoadingThread");
    }

    @Override
    protected void onLooperPrepared() {
      super.onLooperPrepared();
      mHandler = new Handler(getLooper()) {
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what) {
            case SELECT_IMAGE:
              if (mPhotoVO != null) {
                String photoPath = mPhotoVO.getImagePath();
                String copiedPath = mTempPath + "gallery_"+System.currentTimeMillis()+".jpg";
                copyFile(photoPath, copiedPath);
                mImagePath = copiedPath;
                Message message = Message.obtain(mImageHandler, SELECT_IMAGE, mPhotoVO);
                mImageHandler.sendMessage(message);
              }
              break;
            case ROTATE_IMAGE:
              mImageHandler.sendEmptyMessage(ROTATE_IMAGE);
              break;
            case SAVE_BITMAP_AT_FILE:
              Bitmap bitmap = (Bitmap) msg.obj;
              SystemUtil.savePhoto(bitmap, mImagePath);
              break;
          }
        }
      };
    }

    void selectImage() {
      mHandler.sendEmptyMessage(SELECT_IMAGE);
    }

    void rotateImage() {
      mHandler.sendEmptyMessage(ROTATE_IMAGE);
    }

    void saveBitmapAtFile(Bitmap bitmap) {
      Message msg = Message.obtain(mHandler, SAVE_BITMAP_AT_FILE, bitmap);
      mHandler.sendMessage(msg);
    }
  }

}
