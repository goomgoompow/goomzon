package com.pentaon.vzon.activity;

import static com.pentaon.vzon.utils.AppConstants.DOC_KIND_E;
import static com.pentaon.vzon.utils.AppConstants.NOT_DEFINED;
import static com.pentaon.vzon.utils.AppConstants.REQUEST_GET_IMAGE_FROM_AAR;
import static com.pentaon.vzon.utils.AppConstants.REQUEST_GET_IMAGE_FROM_CAMERA;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import android.view.View;

import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.pentaon.vzon.R;
import com.pentaon.vzon.common.ApplicationContext;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.common.SharedInfo;
import com.pentaon.vzon.common.VzonPreference;
import com.pentaon.vzon.network.ApiClient;
import com.pentaon.vzon.network.ApiInterface;
import com.pentaon.vzon.pojo.PictureResult;
import com.pentaon.vzon.ui.scan.dialog.PentaonUploadProgressDialog;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.CryptoUtil;
import com.pentaon.vzon.utils.GUIUtil;
import com.pentaon.vzon.utils.SystemUtil;
import com.pentaon.vzon.utils.ViewUtil;
import com.pentaon.vzon.views.CustomAlertDialog;
import com.pentaon.vzon.views.ItemThumbnail;

import com.valmore.aireader.Constants;
import com.valmore.aireader.data.IntentInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import photoview.PhotoViewAttacher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 카메라로 찍은 이미지 리스트 화면
 */
public class PictureListActivity extends BaseActivity implements
    CustomAlertDialog.CustomDialogTwoButtonListener {

  private static final String TAG = "PictureListActivity";

  private HashMap<String, Object> mSelectedDocInfoMap = new HashMap<String, Object>();
  private ArrayList<String> mListImagePath; // 현재 저장되어있는 전체 이미지
  private LinkedList<ItemThumbnail> mListThumbnails = new LinkedList<>(); //썸네일 저장 List
  private ArrayList<String> mKeys = new ArrayList<>();
  //---------------------------------------------------------
  // define UI components  2019-02-20 _ PC-jhKim
  //---------------------------------------------------------
  private ConstraintLayout mContainerThumbnail;
  private LinearLayout mBtnCancel;
  private LinearLayout mBtnSave;
  private LinearLayout mLinearThumbnails;
  private RelativeLayout bg;
  private RelativeLayout mBtnOpenCamera;
  private ImageView mBgImage;    // 가져온 이미지(뒤에 깔리는)
  private ImageView mBtnRotateLeft;
  private ImageView mBtnRotateRight;
  private ImageView mBtnRemove;
  private ItemThumbnail mItemThumbnail;
  private ItemThumbnail mFocusingThumbnail;
  private TextView mDescription;
  private ToggleButton mToggleColorTransform;
  private HorizontalScrollView mHorizontalScrollView;

  private int currentAngle = 0;
  private int mCurrentPosition = 0;    // 현재 선택 된 포지션 번호 0~9
  private int attachPosition = -1;
  private int mMaxPage = 0;
  private int mSelectedIdType = 0;
  private int mIdNoSaveAlert;

  private PentaonUploadProgressDialog ppdhelper = null;

  private String rootPath;
  private String tempPath;
  private String fileName;
  private String mDocKind;
  //  private String mKey;
  private String mImageTempPath;
  private String mImagePath;


  private VzonPreference sharedPref;
  PhotoViewAttacher mAttacher;

  private boolean mIsSend = false; //매장사진 전송
  private boolean mOneTouch = false;
  private boolean mIsFromMainAct = false;
  private boolean mIsExceedMaxNum; //최대 첨부 가능 수 초과 여부

  private ProgressDialog mDownProgDlg;    // #1191 스캔이미지조회 화면에서 저장된 이미지 다운로드시 프로그레스바 사용
  private Bitmap mBitmapImage = null;
  private Bitmap mBitmapThumbnail = null;
  private Bitmap backBitmap = null;
  private CryptoUtil mCrypto = CryptoUtil.getInstance();
  private Intent mIntent;

  private CustomAlertDialog mNoSaveAlertDialog;


  //=========================================================
  // Override methods
  //=========================================================
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    sharedPref = new VzonPreference(PictureListActivity.this);

    setContentView(R.layout.activity_scan_picture_list);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 항상 켜기
    mListImagePath = new ArrayList<>();

    // TODO: 2018-08-31 ImageProcessing 화면에서 확인 버튼 연타시 아래 라인 nullpointerException 발생하며 down됨.
    SharedInfo sharedInfo = SharedInfo.getInstance();
    if (sharedInfo == null) {
      ApplicationContext.setSharedInfo(PictureListActivity.this);
      sharedInfo = SharedInfo.getInstance();
    }
    String cachePath = sharedInfo.getCachePath() + attachPosition;
    SystemUtil.deleteFile(cachePath);
    SystemUtil.deleteFileDir(cachePath + "/temp");
    SystemUtil.deleteFile(Config.getPackageURL());

    mIntent = getIntent();
    mSelectedDocInfoMap = (HashMap<String, Object>) mIntent
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);
    attachPosition = mIntent.getIntExtra(AppConstants.INTENT_EXTRA_ATTACH_POSITION, 0);

    rootPath = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_ROOT_PATH);
    tempPath = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_TEMP_PATH);
    mIsFromMainAct = (Boolean) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_FROM_MAIN);
    mDocKind = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_DOC_KIND);
    mMaxPage = (int) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_DOC_MAX_PAGE);
    if (mDocKind.equals(AppConstants.DOC_KIND_AA)) {
      mSelectedDocInfoMap.put(AppConstants.INTENT_EXTRA_DOC_KIND, AppConstants.DOC_KIND_B);
    }

    PictureListActivity.this.setResult(RESULT_OK, mIntent);
    init();

    /*String temp = sharedInfo.getCachePath() + attachPosition;
    File dir = new File(temp);

    if (!dir.exists()) {
      SystemUtil.makeDirectory(temp);
    }*/

    if (mIsFromMainAct) {
      mIsFromMainAct = false;
      gotoOptimalActivity(mDocKind);
    }
  }


  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void initLayout() {
  }

  @Override
  protected void initialize() {
  }

  @Override
  public void onBackPressed() { // back-key 막기

  }

  @Override
  protected void onDestroy() {
    SystemUtil.deleteFile(SharedInfo.getInstance().getCachePath() + attachPosition);
    SystemUtil.deleteFileDir(SharedInfo.getInstance().getCachePath() + attachPosition + "/temp");
    SystemUtil.deleteFile(Config.getPackageURL());

    if (mSelectedDocInfoMap != null) {
      mSelectedDocInfoMap.clear();
      mSelectedDocInfoMap = null;
    }
    if (mListImagePath != null) {
      mListImagePath.clear();
    }
    if (ppdhelper != null && ppdhelper.isShowing()) {
      ppdhelper.dismiss();
      ppdhelper = null;
    }
    if (mDownProgDlg != null && mDownProgDlg.isShowing()) { // #1191
      mDownProgDlg.dismiss();
      mDownProgDlg = null;
    }
    System.gc();
    super.onDestroy();
  }

  @Override
  public void onLeftButtonClick(int id) {
    mNoSaveAlertDialog.dismiss();
  }

  @Override
  public void onRightButtonClick(int id) {
    mNoSaveAlertDialog.dismiss();
    addInfoAndFinish(RESULT_CANCELED, mIntent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      Toast.makeText(PictureListActivity.this, getString(R.string.toast_image_loading_fail), Toast.LENGTH_SHORT).show();
      setResult(RESULT_CANCELED);
      finish();
      return;
    }
    switch (requestCode) {
      case AppConstants.REQUEST_GET_IMAGE_FROM_CAMERA:
      case AppConstants.REQUEST_GET_IMAGE_FROM_AAR:
        onRequestGetImageFromCamera(data, requestCode);
        break;

      case AppConstants.REQUEST_SAVE_IMAGE_FROM_CAMERA:// PointChangedAct-> PictureListAct
        onRequestSaveImageFromCamera(data);
        break;
    }
  }

  //=========================================================
  // private methods
  //=========================================================

  private void gotoOptimalActivity(String docType) {
    if (docType.equals(AppConstants.DOC_KIND_E)) {
      Intent intent = new Intent(this, InstallationCaptureActivity.class);
      mSelectedDocInfoMap.put(AppConstants.INTENT_EXTRA_FROM_MAIN, false);
      intent.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfoMap);
      startActivityForResult(intent, REQUEST_GET_IMAGE_FROM_CAMERA);
      return;
    }

    int targetDocument =
        (docType.equals(AppConstants.DOC_KIND_B)) ? AppConstants.DOC_TYPE_ID
            : AppConstants.DOC_TYPE_A4;
    int colorType =
        (docType.equals(AppConstants.DOC_KIND_B)) ? AppConstants.COLOR : AppConstants.BLACK_WHITE;
    long currentMills = System.currentTimeMillis();
    String fileName =
        (docType.equals(AppConstants.DOC_KIND_B)) ? "id_" + currentMills : "doc_" + currentMills;

    IntentInfo intentInfo = new IntentInfo(this);
    intentInfo.setLicenseKey(AppConstants.LICENSE_KEY)
        .setCIImageResource(R.mipmap.icon_outlined)
        .setTargetDocumentType(targetDocument)
        .setImageColorType(colorType)
        .setOutlineColor(AppConstants.LINE_COLOR)
        .setLineThickness(AppConstants.LINE_THICK)
        .setIdTrialMaxNum(Constants.DEFAULT_ID_TRIAL)
        .setOcrTrialMaxNum(Constants.DEFAULT_OCR_TRIAL)
        .setResultDisplayYN(Constants.RESULT_DISPLAY_Y)
        .setFileDirPath(tempPath+fileName+".jpg")
        .setIsShowFaceArea(false)
        .setSecretkey(mCrypto.getSecretKey())
        .startForResult(AppConstants.REQUEST_GET_IMAGE_FROM_AAR);
  }


  private void gotoNativeCamera(Intent i) {
    /*Intent intent = new Intent(this, NativeCameraActivity.class);
    ((HashMap<String, Object>) i
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP))
        .put(AppConstants.INTENT_EXTRA_FROM_MAIN, false);
    intent.putExtras(i);
    startActivityForResult(intent, AppConstants.REQUEST_GET_IMAGE_FROM_CAMERA);*/
  }

  private void init() {
    bg = findViewById(R.id.bg);
    mDescription = findViewById(R.id.act_picture_list_description);
    ViewUtil.GONE(mDescription);
    mBtnCancel = findViewById(R.id.act_picture_list_button_navigation_back);
    mBtnCancel.setOnClickListener(btnClickListener);

    mBgImage = findViewById(R.id.bg_image);
    mAttacher = new PhotoViewAttacher(mBgImage);
    mAttacher.setScale(1);

    // 우측 메뉴버튼
    mBtnRotateLeft = findViewById(R.id.act_picture_list_button_rotate_left);
    mBtnRotateRight = findViewById(R.id.act_picture_list_button_rotate_right);
    mBtnRemove = findViewById(R.id.act_picture_list_button_delete);
    mToggleColorTransform = findViewById(R.id.act_scan_picture_toggle_button_color_transform);

    mBtnRotateLeft.setOnClickListener(btnClickListener);
    mBtnRotateRight.setOnClickListener(btnClickListener);
    mBtnRemove.setOnClickListener(btnClickListener);

    //---------------------------------------------------------
    // GridView를 HorizontalScrollView로 구현 2019-03-13 _ PC-jhKim
    //---------------------------------------------------------
    mContainerThumbnail = findViewById(R.id.container_thumbnail);
    mHorizontalScrollView = findViewById(R.id.horizontal_scroll_view);
    mLinearThumbnails = findViewById(R.id.linear_thumbnails);
    mBtnOpenCamera = findViewById(R.id.btn_open_camera);

    // 하단 메뉴 버튼

    mBtnOpenCamera.setOnClickListener(btnClickListener);
    mBtnSave = findViewById(R.id.act_picture_list_button_save);
    mBtnSave.setOnClickListener(btnClickListener);
  }

  private void imageInit(int attachPosition) {
    if (Config.DEBUG) {
      Log.d(TAG, "imageInit() : attachPosition[" + attachPosition + "]");
    }

    File[] returnServerImageCheck = SystemUtil
        .getFileName(SharedInfo.getInstance().getCachePath() + attachPosition + File.separator);
    for (int p = 0; p < returnServerImageCheck.length; p++) {
      if (Config.DEBUG) {
        Log.d(TAG,
            "imageInit() : ###### returnServerImageCheck.length[" + returnServerImageCheck.length
                + "] returnServerImageCheck[" + p + "].toString() = " + returnServerImageCheck[p]
                .toString());
      }
    }

    SystemUtil.deleteFileDir(SharedInfo.getInstance().getCachePath() + attachPosition + "/temp");
    File[] returnServerImage = SystemUtil
        .getFileName(SharedInfo.getInstance()
            .getCachePath() + attachPosition + File.separator);
    SystemUtil.sortNameFileList(returnServerImage);

    for (int q = 0; q < returnServerImage.length; q++) {
      if (Config.DEBUG) {
        Log.d(TAG, "imageInit() : returnServerImage.length[" + returnServerImage.length
            + "] returnServerImage[" + q + "].toString() = " + returnServerImage[q].toString());
      }
      setThumbnailImageView(returnServerImage[q].toString(), mBgImage);
    }

    if (!(returnServerImage.length <= 0)) {
      showExtendImage(returnServerImage[0].toString(), mBgImage);
      mCurrentPosition = 0;
    }
    updateImageList();
    String IMAGE_REMARK = (String) mSelectedDocInfoMap.get(AppConstants.IMAGE_REMARK);

    if (Config.DEBUG) {
      Log.d(TAG, "imageInit() : IMAGE_REMARK" + IMAGE_REMARK);
    }
  }

  private void setThumbnailImageView(String path, ImageView imageView) {
    // TODO Thread 로...
    if (Config.DEBUG) {
      Log.d(TAG, "setThumbnailImageView() : file[" + path + "]");
    }
    File file = new File(path);
//    mCrypto.encryptDecryptFile(Cipher.DECRYPT_MODE, rootPath, file);
    String tempExt = SystemUtil.getExtName(path);
    updateImageList();
    if (tempExt.equals("tif")) {
      mBitmapThumbnail = GUIUtil.getTiffThumbnailImage(path);
    } else {
      mBitmapThumbnail = GUIUtil.getThumbnailImage(path);
    }
  }

  /**
   * 화면 가운데에 선택된 썸네일(하단)의 큰 이미지를 보여줌
   */
  private void showExtendImage(String path, ImageView imageView) {
    // TODO Thread 로...
    Bitmap tempBitmapImage;
    if (path != null) {
//      File imgFile = new File(path);
//      decryptImage(imgFile);
      tempBitmapImage = BitmapFactory.decodeFile(path);
      /*String tempExt = SystemUtil.getExtName(path);
      if (tempExt.equals("tif")) {
        tempBitmapImage = GUIUtil.getTiffBackGroundImage(path);
      } else {
        tempBitmapImage = BitmapFactory.decodeFile(path);
      }*/
      imageView.setImageBitmap(tempBitmapImage);
      mBitmapImage = tempBitmapImage;
      ViewUtil.INVISIBLE(mDescription);
//      mCrypto.encryptDecryptFile(Cipher.ENCRYPT_MODE, rootPath, imgFile);
    } else {
      ViewUtil.VISIBLE(mDescription);
      imageView.setImageResource(R.drawable.picture_list_no_image_background);
    }

    imageView.refreshDrawableState();
    imageView.invalidate();

    mAttacher = null;
    mAttacher = new PhotoViewAttacher(mBgImage);
    mAttacher.setScale(1);
  }

  private void decryptImage(File file) {
//    boolean isEncrypted = CryptoUtil.getInstance().getEncryptStatus(file.getAbsolutePath());
//    if (isEncrypted) {
//    mCrypto.encryptDecryptFile(Cipher.DECRYPT_MODE, rootPath, file);
//    }
  }

  private boolean decryptImage(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return false;
    }
    decryptImage(file);
    return true;
  }


  View.OnClickListener btnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (v == null) {
        return;
      }
      switch (v.getId()) {
        case R.id.act_picture_list_button_rotate_right:
          onClickButtonRotateRight();
          break;

        case R.id.act_picture_list_button_rotate_left:
          onClickButtonRotateLeft();
          break;

        case R.id.act_picture_list_button_delete:
          onClickButtonDelete();
          break;

        case R.id.btn_open_camera:   //촬영 //btn_shutter
//          onClickButtonOpenCamera();
          gotoOptimalActivity(mDocKind);
          break;

        case R.id.act_picture_list_button_save: //스캔 완료
          onClickButtonSave();
          break;
        case R.id.act_picture_list_button_navigation_back:
          onClickButtonCancel();
          break;
        default:
          break;
      }
    }
  };

  private void onClickButtonCancel() {
    if (sharedPref.getValue(VzonPreference.ATTACH_END_YN, "").equals("Y")) {
      addInfoAndFinish(RESULT_OK, mIntent);
    } else {
      int tempImgCnt = SystemUtil
          .getFolderImgFileCnt(SharedInfo.getInstance().getCachePath() + attachPosition);
      if (AppConstants.DOC_KIND_E
          .equals(mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_DOC_KIND))
          && mIsSend == true) {
        File[] returnImages = SystemUtil.getFileName(
            SharedInfo.getInstance().getCachePath() + attachPosition + File.separator);

        mContainerThumbnail.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams paramsImage = (RelativeLayout.LayoutParams) bg
            .getLayoutParams();
        paramsImage.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        paramsImage.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        paramsImage.leftMargin = 0;
        paramsImage.rightMargin = 0;
        mIsSend = false;
        if (returnImages.length > 0) {
          showExtendImage(returnImages[0].toString(), mBgImage);
          mCurrentPosition = 0;
        }

        if (tempImgCnt == 1)//특이사항 초과입력후 취소
        {
          imageInit(attachPosition);
        }
      } else {

        mNoSaveAlertDialog = new CustomAlertDialog(this);
        mNoSaveAlertDialog.show();
        mNoSaveAlertDialog.setCanceledOnTouchOutside(false);
        mNoSaveAlertDialog.setCancelable(false);
        mNoSaveAlertDialog.setButtons(CustomAlertDialog.TWO_BUTTON);
        mNoSaveAlertDialog.setTwoButtonClickListener(this)
            .setDialogMessage(R.string.str_picturelist_exit_popup);
        mIdNoSaveAlert = mNoSaveAlertDialog.getId();
      }
    }
  }

  private void onClickButtonSave() {
    showProgress(getString(R.string.common_progress_loading));
    //캡쳐된 사진이 있을 때만 서버에 upload 요청
    if (mListImagePath.size() > 0) {
      uploadCaptureFile(mListImagePath);
    } else {
      hideProgress();
      Intent intent1 = new Intent();
      setResult(RESULT_OK, intent1);
      addInfoAndFinish(RESULT_OK, mIntent);
    }
    if (mOneTouch == false || isFinishing()) {
      return;
    }
  }

 /* private void onClickButtonOpenCamera() {
    int tempPaperTotal = mListImagePath.size();
    if (tempPaperTotal >= mMaxPage) {
      Toast.makeText(PictureListActivity.this,
          getString(R.string.toast_picture_list_max_scan, String.valueOf(mMaxPage)),
          Toast.LENGTH_SHORT).show();
      return;
    }

    // TODO: 2018-09-04 file name reset 및 selectedDocInfo 정보 가져오기
    SharedInfo si = ApplicationContext.getInstance().getSharedInfo();
    UUID uuid = UUID.randomUUID();
    fileName = uuid.toString();

    String[] arrayTempFileName = fileName.split("-");
    //arrayTempFileName[0] ->System.currentTimeMillis();
    String tempFileName = System.currentTimeMillis() + "_" + arrayTempFileName[1] + "_"
        + arrayTempFileName[2];
    String tempFilePath = SharedInfo.getInstance().getCachePath() + attachPosition + "/";
    tempFilePath.toLowerCase();
    int folderImgFileCnt = SystemUtil.getFolderImgFileCnt(tempFilePath);
    tempFileName = SystemUtil.modifyFileName(folderImgFileCnt, tempFileName);
    fileName = tempFileName;
    mSelectedDocInfoMap.put(AppConstants.INTENT_EXTRA_FILE_NAME, fileName);

    Intent intent = new Intent(PictureListActivity.this, NativeCameraActivity.class);
    intent.putExtra(AppConstants.INTENT_EXTRA_ATTACH_POSITION, attachPosition);
    intent
        .putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfoMap);
    intent.putExtra(AppConstants.INTENT_EXTRA_NUM_PICTURE, mListImagePath.size());

    startActivityForResult(intent, AppConstants.REQUEST_GET_IMAGE_FROM_CAMERA);
  }*/

  private void onClickButtonDelete() {
    if (mListThumbnails.size() == 0) {
      Toast.makeText(PictureListActivity.this,
          getResources().getString(R.string.toast_picture_list_delete_no_image),
          Toast.LENGTH_SHORT).show();
      return;
    }
    if (mCurrentPosition == mListImagePath.size()) {
      Toast.makeText(PictureListActivity.this,
          getResources().getString(R.string.toast_picture_list_delete_no_select_error),
          Toast.LENGTH_SHORT).show();
      return;
    }
    if (mListImagePath.size() == 1) {
      mCurrentPosition = 0;
    }
    removeThumbnail(mCurrentPosition);

  }

  private void onClickButtonRotateLeft() {
    if (mBgImage != null) {
      if (mListThumbnails.size() == 0) {
        Toast.makeText(PictureListActivity.this,
            getResources().getString(R.string.toast_picture_list_rotation_no_image),
            Toast.LENGTH_SHORT).show();
        return;
      }
      if (mCurrentPosition == mListImagePath.size()) {
        Toast.makeText(PictureListActivity.this,
            getResources().getString(R.string.toast_picture_list_rotation_no_select_error),
            Toast.LENGTH_SHORT).show();
        return;
      }
      if (mListImagePath.size() == 1) {
        mCurrentPosition = 0;
      }
      int tempWidth = mBgImage.getWidth();
      int tempHeight = mBgImage.getHeight();
      if (currentAngle == 0) {
        currentAngle = 270;
      } else if (currentAngle == 90) {
        currentAngle = 0;
      } else if (currentAngle == 180) {
        currentAngle = 90;
      } else if (currentAngle == 270) {
        currentAngle = 180;
      }
      Bitmap resize = getImageProcess(mBitmapImage, currentAngle, tempWidth, tempHeight);
      if (resize != null) {
        mBgImage.setImageBitmap(resize);
      }

      mAttacher = new PhotoViewAttacher(mBgImage);
      mAttacher.setScale(1);

      hideProgress();
    }
  }


  private void onClickButtonRotateRight() {
    if (mBgImage != null) {
      if (mListThumbnails.size() == 0) {
        Toast.makeText(PictureListActivity.this,
            getResources().getString(R.string.toast_picture_list_rotation_no_image),
            Toast.LENGTH_SHORT).show();
        return;
      }
      if (mCurrentPosition == mListImagePath.size()) {
        Toast.makeText(PictureListActivity.this,
            getResources().getString(R.string.toast_picture_list_rotation_no_select_error),
            Toast.LENGTH_SHORT).show();
        return;
      }
      if (mListImagePath.size() == 1) {
        mCurrentPosition = 0;
      }
      int width = 0;
      int height = 0;
      width = mBgImage.getWidth();
      height = mBgImage.getHeight();

      if (currentAngle == 270) {
        currentAngle = -90;
      }
      currentAngle = AppConstants.DEGREE_90 + currentAngle;

      Bitmap resize = getImageProcess(mBitmapImage, currentAngle, width, height);
      if (resize != null) {
        mBgImage.setImageBitmap(resize);
      }

      mAttacher = new PhotoViewAttacher(mBgImage);
      mAttacher.setScale(1);

      hideProgress();
    }
  }

  /**
   * 특정 index의 thumbnail을 삭제함
   */
  private void removeThumbnail(int index) {
    int prevFocusId =
        (mListThumbnails.size() == 1) ? NOT_DEFINED : (index > 0) ? index - 1 : index + 1;

    ItemThumbnail focusingItem = (prevFocusId < 0) ? null : mListThumbnails.get(prevFocusId);
    mListThumbnails.remove(index);
    mListImagePath.remove(index);
    mLinearThumbnails.removeView(mFocusingThumbnail);
    mFocusingThumbnail = null;
    String currentPath;
    if (focusingItem != null) {
      onChangeThumbnailFocus(focusingItem, false);
      currentPath = mListImagePath.get(mCurrentPosition);
    } else {
      mCurrentPosition = NOT_DEFINED;
      currentPath = null;
    }
    showExtendImage(currentPath, mBgImage);
    //최대 첨부 가능 수 초과 상태에서 thumbnail 삭제시 추가 촬영 버튼 다시 보여주기
    if (mIsExceedMaxNum) {
      mBtnOpenCamera.setVisibility(View.VISIBLE);
      mIsExceedMaxNum = false;
    }
  }

  private Bitmap getImageProcess(Bitmap bmp, int nRotate, int viewW, int viewH) {
    android.graphics.Matrix matrix = new android.graphics.Matrix();
    matrix.postRotate(nRotate);
    Bitmap rotateBitmap = null;
    if (bmp != null) {
      rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }
    return rotateBitmap;
  }

  /**
   * 기존 캡쳐한 이미지 있을 경우, PictureListAct 화면 유지 "        없을 경우, NativeCameraAct로 이동
   */
  private void hasPreviousCapturedImage() {
    Log.d(TAG, "hasPreviousCapturedImage: mIntent= " + mIntent);
    if (mListImagePath.size() == 0 && mIntent != null) {
      gotoNativeCamera(mIntent);
    }
  }

  /**
   * 문서의 최대 첨부 가능 수를 체크해서 최대 수이면 추가 촬영 버튼 숨기기
   */
  private void checkThumbnailMax() {
    if (mListThumbnails.size() >= mMaxPage) {
      mBtnOpenCamera.setVisibility(View.GONE);
      mIsExceedMaxNum = true;
    }
  }

  /**
   * 해당 아이템으로 focus 이동.
   *
   * @param item item will have a focus.
   * @param isScrollToRight :whether move to right or not.
   */
  private void onChangeThumbnailFocus(ItemThumbnail item, boolean isScrollToRight) {
    if (mFocusingThumbnail != null) {
      mFocusingThumbnail.setBgColor(getResources().getColor(R.color.dark_gray));
    }
    mFocusingThumbnail = item;
    mCurrentPosition = mListThumbnails.indexOf(item); //현재 포지션 값 저장
    if (isScrollToRight) {
      mHorizontalScrollView.post(new Runnable() {
        @Override
        public void run() {
          mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
          mFocusingThumbnail
              .setBgColor(getResources().getColor(R.color.act_native_camera_preview_vertex));
        }
      });
    } else {
      mFocusingThumbnail
          .setBgColor(getResources().getColor(R.color.act_native_camera_preview_vertex));
    }

  }

  /**
   * thumbnail 이미지를 추가.
   */
  private ItemThumbnail addThumbnail(Bitmap bitmap) {
    int id = mListThumbnails.size();
    ItemThumbnail item = new ItemThumbnail(this);
    item.setItemId(id);
    item.setImage(bitmap);
    mListThumbnails.add(item);
    mLinearThumbnails.addView(item);
    item.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onChangeThumbnailFocus((ItemThumbnail) v, false);
        showExtendImage(mListImagePath.get(mCurrentPosition), mBgImage);
      }
    });
    return item;
  }

  private void doCropAction(HashMap<String, Object> mSelectedDocInfoMap) {
    Intent intent = new Intent(this, PointChangedActivity.class);
    intent.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfoMap);
    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
    startActivityForResult(intent, AppConstants.REQUEST_SAVE_IMAGE_FROM_CAMERA);
  }

  private void doCropAction(String source, String target, String cameraMode, String colorSelection,
      String docKind) {
    Intent intent = new Intent(PictureListActivity.this, PointChangedActivity.class);
    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

    intent.putExtra(AppConstants.INTENT_EXTRA_IMAGE_TEMP_PATH, source);
    intent.putExtra(AppConstants.INTENT_EXTRA_IMAGE_PATH, target);
    intent.putExtra(AppConstants.INTENT_EXTRA_ROOT_PATH, rootPath);
    intent.putExtra(AppConstants.INTENT_EXTRA_TEMP_PATH, tempPath);
    intent.putExtra(AppConstants.INTENT_EXTRA_CAMERA_MODE, cameraMode);
    intent.putExtra(AppConstants.INTENT_EXTRA_COLOR_SELECTION, colorSelection);
    intent.putExtra(AppConstants.INTENT_EXTRA_DOC_KIND, docKind);
    intent.putExtra(AppConstants.INTENT_EXTRA_SEL_CARD_IDX, mSelectedIdType);
    intent.putExtra(AppConstants.INTENT_EXTRA_TOTAL_PAGE, String.valueOf(mListImagePath.size()));
    intent.putExtra(AppConstants.FORM_ID, (String) mSelectedDocInfoMap.get(AppConstants.FORM_ID));
    intent.putExtra(AppConstants.INTENT_EXTRA_DOC_MASKING,
        (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_DOC_MASKING));//shlee add 마스킹 구분
    intent.putExtra(AppConstants.IDNT_CD, (String) mSelectedDocInfoMap.get(AppConstants.IDNT_CD));

    startActivityForResult(intent, AppConstants.REQUEST_SAVE_IMAGE_FROM_CAMERA);
  }

  /**
   *
   */
  private void updateImageList() {
    File[] tempArrayListFileImage = SystemUtil
        .getFileName(
            SharedInfo.getInstance()
                .getCachePath() + attachPosition + File.separator);
    SystemUtil.sortNameFileList(tempArrayListFileImage);

    if (mListImagePath != null) {
      mListImagePath.clear();
    } else {
      mListImagePath = new ArrayList<>();
    }

    for (int imageLen = 0; imageLen < tempArrayListFileImage.length; imageLen++) {
      mListImagePath.add(tempArrayListFileImage[imageLen].toString());
    }
  }

  /**
   * 암.복호화에 사용되는 대칭키(Symmetric key) 생성을 위해 Random으로 캐릭터 생성
   */
  private String getRandomChar(int numOfChar) {
    String BASE = "abcdefghijklmnopqrstuvwxyz1234567890";
    StringBuilder stringBuilder = new StringBuilder();
    Random rnd = new Random();
    while (stringBuilder.length() < numOfChar) {
      int index = (int) (rnd.nextFloat() * BASE.length());
      stringBuilder.append(BASE.charAt(index));
    }
    return stringBuilder.toString();
  }


  /**
   * 서버에 촬영한 파일 업로드함.
   *
   * @param fileNames 캡쳐된 파일 Absolute Path들
   */
  private void uploadCaptureFile(ArrayList<String> fileNames) {
    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

    for (String name : fileNames) {
      // TODO: 2018-11-01 이미지 복호화
      File file = new File(name);
      String absPath = file.getAbsolutePath();
/*//      boolean isEncrypted = CryptoUtil.getInstance().getEncryptStatus(absPath);
      Log.d(TAG, "uploadCaptureFile: cryptoStatus :  " + isEncrypted);
      if (isEncrypted) {
//        mCrypto.encryptDecryptFile(Cipher.DECRYPT_MODE, absPath, file);
      }*/
      Log.d(TAG, "uploadCaptureFile: fileNames = " + name);
      Log.d(TAG, "uploadCaptureFile: absPath = " + absPath);
      String imgType =
          SystemUtil.getExtName(name).equals("jpg") ? AppConstants.TYPE_JPEG
              : AppConstants.TYPE_TIFF;
      builder.addFormDataPart(AppConstants.KEY_FILE, absPath,
          RequestBody.create(MediaType.parse(imgType), file));
    }

    RequestBody requestBody = builder.build();
    Retrofit retrofit = ApiClient.getClient();
    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    Call<PictureResult> call = (mDocKind.equals(AppConstants.DOC_KIND_E)) ?
        apiInterface.uploadInstallPicture(requestBody) :
        apiInterface.uploadContractPicture(requestBody);

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
          Log.d(TAG, "onClick: mKeys.size() = " + mKeys.size());
          mIntent.putExtra(AppConstants.INTENT_EXTRA_FROM_INSTALLATION_ACT, mKeys);
          addInfoAndFinish(RESULT_OK, mIntent);
          Toast
              .makeText(PictureListActivity.this, getText(R.string.image_saved), Toast.LENGTH_SHORT)
              .show();
        } else {
          Log.d(TAG, "onResponse: NOT is Successful");
          showResponseFailToast(response.code());
        }
      }

      @Override
      public void onFailure(Call<PictureResult> call, Throwable t) {
        hideProgress();
        Log.d(TAG, "onFailure: ");
        Toast.makeText(PictureListActivity.this,
            getText(R.string.toast_picture_list_message_server_error).toString(),
            Toast.LENGTH_SHORT).show();
        addInfoAndFinish(RESULT_OK, mIntent);
      }
    });
  }

  /**
   * onActivityResult()에서 request code= AppConstants.REQUEST_SAVE_IMAGE_FROM_CAMERA 일 때
   * PointchangedActivity에서 이미지 프로세싱 거친 후
   *
   * @param data : 전달된 Intent
   */
  private void onRequestSaveImageFromCamera(Intent data) {
    mAttacher.setScale(1); //shlee add 이미지크기를 원래대로 돌아가게 함
    HashMap<String, Object> docInfos = (HashMap<String, Object>) data
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);

    String scanCancel = (String) docInfos.get(AppConstants.INTENT_EXTRA_SCAN_CANCEL);
    boolean scanUserCancel = data
        .getBooleanExtra(AppConstants.INTENT_EXTRA_SCAN_USER_CANCEL, false);
    String rootPath = (String) docInfos.get(AppConstants.INTENT_EXTRA_ROOT_PATH);
    mImageTempPath = (String) docInfos.get(AppConstants.INTENT_EXTRA_IMAGE_TEMP_PATH);
    mImagePath = (String) docInfos.get(AppConstants.INTENT_EXTRA_IMAGE_PATH);
    mDocKind = (String) docInfos.get(AppConstants.INTENT_EXTRA_DOC_KIND);
    mSelectedIdType = (Integer) docInfos.get(AppConstants.INTENT_EXTRA_SEL_CARD_IDX); //shlee add

    //---------------------------------------------------------
    //PointChangedActivity에서 back버튼 터치해서 PictureListAct로 진입했을 때
    // 1.기존 촬영한 사진 있을 경우 -> 화면 유지
    // 2. 기존 촬영한 사진 없을 경우 -> NativeCameraAct로 이동
    // 2019-03-25 _ PC-jhKim
    //---------------------------------------------------------
    if (scanUserCancel) {
      hasPreviousCapturedImage();
      return;
    }
    rootPath.toLowerCase();
    int folderImgFileCnt = SystemUtil.getFolderImgFileCnt(rootPath);

    File[] folderImgFile = SystemUtil.getFolderImgFile(rootPath);
    SystemUtil.sortNameFileList(folderImgFile);

    if (folderImgFile.length > 0) {
      String tempExt = SystemUtil.getExtName(mImagePath);

      if (tempExt.equals("tif")) {
        mBitmapThumbnail = GUIUtil.getTiffThumbnailImage(mImagePath);
      } else {
        mBitmapThumbnail = GUIUtil.getThumbnailImage(mImagePath);
      }
      showExtendImage(mImagePath, mBgImage);
      mListImagePath.add(mImagePath);
      mCurrentPosition = folderImgFile.length - 1;
    }
    mItemThumbnail = addThumbnail(mBitmapThumbnail);
    onChangeThumbnailFocus(mItemThumbnail, true);
    checkThumbnailMax();
  }

  /**
   * onActivityResult()에서 request code= Constants.REQUEST_CAMERA_OCR 일 때
   */
  private void onRequestGetImageFromCamera(Intent intent, int requestCode) {

    String tempPath, fileName ="";
    if (requestCode == REQUEST_GET_IMAGE_FROM_CAMERA) {
      HashMap<String, Object> hashMap = (HashMap<String, Object>) intent
          .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);
      tempPath = (String) hashMap.get(AppConstants.INTENT_EXTRA_TEMP_PATH);
      fileName = (String) hashMap.get(AppConstants.INTENT_EXTRA_FILE_NAME);
      mImagePath=tempPath + fileName;
    } else if (requestCode == REQUEST_GET_IMAGE_FROM_AAR) {
      mImagePath=intent.getStringExtra(Constants.INTENT_EXTRA_FILE_DIR_PATH);;
    }
//    mImagePath = docType.equals(DOC_KIND_E) ? tempPath + fileName : imgPath;
    if(fileName!=null){
      showExtendImage(mImagePath, mBgImage);
      mBitmapThumbnail = GUIUtil.getThumbnailImage(mImagePath);
      mListImagePath.add(mImagePath);
//    mCurrentPosition = folderImgFile.length - 1;
      mItemThumbnail = addThumbnail(mBitmapThumbnail);
      onChangeThumbnailFocus(mItemThumbnail, true);
      checkThumbnailMax();
    }

    /*OcrResult result = new OcrResult(intent);
    String filePath = result.getFilePath();
    decryptImage(filePath);
    Bitmap bitmap = (result.getDocType() == Constants.DOC_TYPE_ID) ?
        result.getImage() : BitmapFactory.decodeFile(filePath);
    showExtendImage(filePath, mBgImage);
    mListImagePath.add(filePath);
    mCurrentPosition = mListImagePath.size() - 1;
    mItemThumbnail = addThumbnail(bitmap);
    onChangeThumbnailFocus(mItemThumbnail, true);
    checkThumbnailMax();*/
  }

  /**
   * onActivityResult()에서 request code= AppConstants.REQUEST_GET_IMAGE_FROM_CAMERA 일 때
   * PictureList->Native->PictureList->PointChanged->PictueList ??
   */
  private void onRequestGoToInstallationCapture(Intent data) {
    boolean isCancelScan = data.getBooleanExtra(AppConstants.INTENT_EXTRA_SCAN_USER_CANCEL, false);
    if (isCancelScan) {
      if (mListImagePath.size() == 0) {
        addInfoAndFinish(RESULT_OK, mIntent);
      }
      return;
    }
    mAttacher.setScale(1); //shlee add 이미지크기를 원래대로 돌아가게 함
    mSelectedDocInfoMap = (HashMap<String, Object>) data
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);

    mImageTempPath = (String) mSelectedDocInfoMap.get(AppConstants.INTENT_EXTRA_TEMP_PATH);

    File file = new File(mImageTempPath);
    boolean bFileExist = file.exists();
    boolean isEncrypted = CryptoUtil.getInstance().getEncryptStatus(mImageTempPath);

    if (bFileExist) {
      mSelectedIdType = (int) mSelectedDocInfoMap
          .get(AppConstants.INTENT_EXTRA_SEL_CARD_IDX); //shlee add

      if (isEncrypted) {
//        mCrypto.encryptDecryptFile(Cipher.DECRYPT_MODE, tempPath, file);
      }
      String imageCapturePathExt = SystemUtil.getExtName(mImageTempPath);

      if (imageCapturePathExt.equals("tif")) {
        mBitmapThumbnail = GUIUtil.getTiffThumbnailImage(mImageTempPath);
      } else {
        mBitmapThumbnail = GUIUtil.getThumbnailImage(mImageTempPath);
      }
      doCropAction(mSelectedDocInfoMap);
    } else {
      if (mListImagePath.size() == 0) {
        addInfoAndFinish(RESULT_OK, mIntent);
        return;
      }
    }
  }

}


