package com.pentaon.vzon.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pentaon.vzon.R;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.data.WidthHeight;
import com.pentaon.vzon.manager.ScreenInfoManager;
import com.pentaon.vzon.network.UIThread;
import com.pentaon.vzon.ui.scan.ImageProcessing;
import com.pentaon.vzon.ui.scan.PointView;
import com.pentaon.vzon.ui.scan.PointView.ImageProcessListener;
import com.pentaon.vzon.ui.scan.PointView.RotateListener;
import com.pentaon.vzon.ui.scan.PointView.ZoomImageListener;
import com.pentaon.vzon.ui.scan.PointView.pointChangedListener;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.CryptoUtil;
import com.pentaon.vzon.utils.GUIUtil;
import com.pentaon.vzon.utils.SystemUtil;
import com.pentaon.vzon.utils.ViewUtil;
import com.pentaon.vzon.views.CustomAlertDialog;
import com.pentaon.vzon.views.CustomAlertDialog.CustomDialogOneButtonListener;
import com.pentaon.vzon.views.CustomAlertDialog.CustomDialogTwoButtonListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class PointChangedActivity extends BaseActivity implements pointChangedListener,
    ImageProcessListener, ZoomImageListener, CustomDialogOneButtonListener
    , CustomDialogTwoButtonListener, RotateListener, OnClickListener {

  private static final String TAG = "PointChangedActivity";
  private static final int MAX_MASKING_NUM = 10;//30
  private static final int[] ROTATION_DEGREE = {0, 90, 180, 270};
  public static View mBackTopView, mBackBottomView = null;
  public static final boolean isHighExtraHighDensity = SystemUtil.isHighExtraHighDensity();

  private String mImagePath = null;
  private String mRootPath = null;
  private String mTempPath = null;
  private String mColorSelection = null;
  private String mDocKind = null;
  private String mSelectedFormId = ""; // 선택된 문서의 id
  private String mBankNM, mBankACNO, mDpsrNM;
  private String mSelMasking; //일반문서에서 마스킹이 필요한지 여부(E : 필수, C : 선택, N : 사용하지 않음)

  private Point mPointLT = new Point();
  private Point mPointRT = new Point();
  private Point mPointLB = new Point();
  private Point mPointRB = new Point();


  private ArrayList<Point> mMaskingPoint = new ArrayList<Point>();
  private HashMap<String, Object> mSelectedDocInfo = new HashMap<>();

  private Bitmap mImageBitmap;
  private Handler mHandler = null;

  private boolean mFirstLoading = true;
  private boolean mFirstImageLoading = true;
  private boolean isBlackBackground = false;
  private boolean isMasking = false;
  private boolean mIsImageProcessingNow = false;
  private boolean mIsFromGallery;
  private boolean mIsConfirmVertex; //사각잡기 영역 확인 화면인지 여부

  private float mZoomRate;

  private int mFindCornerPoints;  // -1(N/A), 0(false), 1(true)
  private int selectedIDCardIndex = 0; // 어떤 종류의 신분증인지 0: none, 1: 주민등록증 2: 운전면허증 3: 외국인등록증 앞면 6: 외국인등록증 뒷면 4: 국내거소증 앞면 5: 국내거소증 뒷면
  private int mTotalPage = 0;
  private int mMaskingNumber = 1;
  private int mDpi;
  private int mIndexRotationLeft = 0;
  private int mIndexRotationRight = 4;

  private HandlerThread mLoadingBarThread = null;
  private CryptoUtil mCrypto = CryptoUtil.getInstance();


  //UI componets
  private FrameLayout mPointViewLayout;
  private LinearLayout mTopLayout;
  private ConstraintLayout mTopButtonContainer;
  private PointView mPointView = null;
  private ImageButton mAddBtn, mRemoveBtn;
  //  private Button mRemoveBtn , mResetBtn;
  private LinearLayout mSaveBtn, mOkBtn, mCancelBtn;
  //  private ImageView mZoomView;
  private LinearLayout mContainerMaskAddRemove;
  private CustomAlertDialog mPointChangedActDialog;

  private int mIdOfAlertCompleteImageLoading;//이미지 프로세싱 후 alert의 id
  private int mIdConfirmHideIdNum; //주민 번호 masking 확인 alert의 id
  private int mIdExceedMaskMaxNum; //마스크 개수 초과  alert의 id
  private int mIdUnableToAddMask; //마스크 더 이상 추가 할 수 없음 alert의 id
  private int mIdUnableToRemoveMask; //마스크를 삭제 할 수 없음 alert의 id
  private int mIdGuideMaskAddAlert; //마스크 추가 가이드 alert의 id
  private int mIdGuideMaskMoveAlert;
  private Intent mIntent;
//  private PhotoVO mPhotoObj;

  //private ImageMagnifier mMagnifierView;

  //=========================================================
  // Override methods
  //=========================================================
  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (Config.DEBUG) {
      Log.d(TAG, "onCreate()");
    }
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 제거

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 항상 켜기

    mIntent = getIntent();
    mSelectedDocInfo = (HashMap<String, Object>) mIntent
        .getSerializableExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP);
    Log.d(TAG, "onCreate: super.getIntent()  = " + mIntent);
    mDocKind = (String) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_DOC_KIND);
    mRootPath = (String) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_ROOT_PATH);
    mSelMasking = (String) mSelectedDocInfo
        .get(AppConstants.INTENT_EXTRA_DOC_MASKING); //shlee add 마스킹 구분
    mTempPath = (String) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_TEMP_PATH);
    mImagePath = (String)mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_IMAGE_PATH);
    mColorSelection = (String) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_COLOR_SELECTION);
    mSelectedFormId = (String) mSelectedDocInfo.get(AppConstants.FORM_ID);
    mTotalPage = (int) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_DOC_MAX_PAGE);
//    mPhotoObj = (PhotoVO) sIntent.getSerializableExtra(AppConstants.INTENT_EXTRA_FROM_GALLERY);

   /* if (mPhotoObj != null) {
      String photoPath = mPhotoObj.getImagePath();
      String copiedPath = mTempPath + "copy.jpg";
      copyFile(photoPath, copiedPath);
      mImageTempPath = mImagePath = copiedPath;
    }*/

//    if(mImageTempPath==null){
//      mImagePath = mImageTempPath = mPhotoObj.getImagePath();
//    }
    mIsFromGallery = (boolean)mIntent.getBooleanExtra(AppConstants.INTENT_EXTRA_FROM_GALLERY,false);

    mDpi = SystemUtil.getDensityDpi(PointChangedActivity.this);

    if (Config.DEBUG) {
      Log.d(TAG, "onCreate() : mImagePath[" + mImagePath + "]");
      Log.d(TAG, "onCreate() : mRootPath[" + mRootPath + "]");
      Log.d(TAG, "onCreate() : mTempPath[" + mTempPath + "]");
//      Log.d(TAG, "onCreate() : mCameraMode[" + mCameraMode + "]");
      Log.d(TAG, "onCreate() : mColorSelection[" + mColorSelection + "]");
      Log.d(TAG, "onCreate() : mDocKind[" + mDocKind + "]");
      Log.d(TAG, "onCreate() : mSelMasking[" + mSelMasking + "]");
      Log.d(TAG, "onCreate() : mTotalPage[" + mTotalPage + "]");
      Log.d(TAG, "onCreate() : mSelectedFormId[" + mSelectedFormId + "]");
    }

    if (AppConstants.DOC_KIND_D.equals(mDocKind)) {
      mBankNM = (String) mSelectedDocInfo.get(AppConstants.STLN_BANK_NM);
      mBankACNO = (String) mSelectedDocInfo.get(AppConstants.STLN_ACNO);
      mDpsrNM = (String) mSelectedDocInfo.get(AppConstants.DPSR_NM);
      if (Config.DEBUG) {
        Log.d(TAG, "onCreate() : mBankNM[" + mBankNM + "]");
        Log.d(TAG, "onCreate() : mBankACNO[" + mBankACNO + "]");
        Log.d(TAG, "onCreate() : mDpsrNM[" + mDpsrNM + "]");
      }
    }

    if (AppConstants.DOC_KIND_B.equals(mDocKind) && !mIsFromGallery) { // "B" (신분증), "E" (매장사진)
      isBlackBackground = true;
    } else {
      isBlackBackground = false;
    }
    if (AppConstants.DOC_KIND_E.equals(mDocKind)) {//매장사진
      selectedIDCardIndex = (int) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_SEL_CARD_IDX);
      if (Config.DEBUG) {
        Log.d(TAG, "onCreate() : selectedIDCardIndex[" + selectedIDCardIndex + "]");
      }
    }
    if (AppConstants.DOC_KIND_B.equals(mDocKind)) { // "B" (신분증)
      isMasking = true;
      selectedIDCardIndex = (int) mSelectedDocInfo.get(AppConstants.INTENT_EXTRA_SEL_CARD_IDX);
      if (Config.DEBUG) {
        Log.d(TAG, "onCreate() : selectedIDCardIndex[" + selectedIDCardIndex + "]");
      }
    } else {
      isMasking = false;
    }
    createHandler();
    setContentView(R.layout.activity_scan_point_changed);
    mHandler.sendMessage(
        Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_PROCESSING)); // call rImageProcessing
  }

  /*@Override
  public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
  }*/



  @Override
  public void onBackPressed() {

  }

  @Override
  protected void onDestroy() {
    if (Config.DEBUG) {
      Log.d(TAG, "onDestroy()");
    }
    super.onDestroy();

    mPointView.freeImage();
    mPointView.onDestroy();
    clearTempFile();

//		if (m_progressDialog != null && m_progressDialog.isShowing()) {
//			m_progressDialog.dismiss();
//			m_progressDialog = null;
//		}
  }

  private void clearTempFile() {
    File file = new File(mImagePath);
    if (file.exists()) {
      file.delete();
    }
  }

  @Override
  protected void initLayout() {
    mPointViewLayout = findViewById(R.id.PointViewLayout);
    mTopLayout = findViewById(R.id.top_layout);
    mTopButtonContainer = findViewById(R.id.top_button_container);
    mPointView = findViewById(R.id.point_view);
    //---------------------------------------------------------
    // 19.01.07 PointView의 container 레이아웃에 magnifier view 추가
    //---------------------------------------------------------
        /*mParentLayout = (RelativeLayout) mPointView.getParent();
        mMagnifierView = new ImageMagnifier(getApplicationContext());
        mParentLayout.addView(mMagnifierView);*/

    mPointView.setImageProcessListener(PointChangedActivity.this);
    mPointView.setDocKind(mDocKind, mColorSelection, mSelMasking); //shlee 문서 종류 구분을 위해 추가함

    mBackTopView = findViewById(R.id.back_top);
    mBackBottomView = findViewById(R.id.back_bottom);
//    mZoomView = findViewById(R.id.act_point_changed_zoom_view);

    mOkBtn = findViewById(R.id.act_point_changed_button_ok);
    mCancelBtn = findViewById(R.id.act_point_changed_button_cancel);
    mSaveBtn = findViewById(R.id.act_point_changed_button_save);
//    mResetBtn = findViewById(R.id.reset); // 사각잡기
    mContainerMaskAddRemove = findViewById(R.id.act_point_changed_container_mask_button);
    mAddBtn = findViewById(R.id.btn_mask_add); // 마스킹 추가
    mRemoveBtn = findViewById(R.id.btn_mask_remove); // 마스킹 제거

    /*findViewById(R.id.btn_rotate_left).setOnClickListener(this);
    findViewById(R.id.btn_rotate_right).setOnClickListener(this);*/
  }

  @Override
  protected void initialize() {
    //----------------------------------------
    //편집할 이미지 출처 (갤러리 or 카메라) PointView에 저장
    //----------------------------------------
    mPointView.setIsFromGallery(mIsFromGallery);
    mPointView.setZoomImageListener(this);
    mPointChangedActDialog = new CustomAlertDialog(this);
    mPointChangedActDialog.setCancelable(false);
    mPointChangedActDialog.setCanceledOnTouchOutside(false);
//    mPointView.setImageRotation(mPhotoObj.getRotation());
  }


  @Override
  public void onCompleteImageLoading() {
//        hideProgressDialog();
    if (mDocKind.equals(AppConstants.DOC_KIND_B)) {

      mPointChangedActDialog.show();
      mPointChangedActDialog.setButtons(CustomAlertDialog.ONE_BUTTON);
      mPointChangedActDialog.setOneButtonClickListener(this)
          .setDialogMessage(R.string.idcard_point_msg);
      mIdOfAlertCompleteImageLoading = mPointChangedActDialog.getId();
    }
  }

  @Override
  public void onChangeZoomPoint(int action, Point p) {

  }

  @Override
  public void setZoomImage(Bitmap bitmap) {
//    mZoomView.setImageBitmap(bitmap);
//    mZoomView.setImageAlpha(0);
  }

  //=========================================================
  // Public methods
  //=========================================================
  public void pointChanged(float zoomRate, Point pointLT, Point pointRT, Point pointLB,
      Point pointRB, Bitmap imageBitmap, int findCornerPoints) {
    if (Config.DEBUG) {
      Log.d(TAG, "pointChanged: 11111111111111111111111");
      Log.d(TAG, "### pointChanged() : zoomRate[" + zoomRate + "]");
      Log.d(TAG, "### pointChanged() : pointLT[" + pointLT + "]");
      Log.d(TAG, "### pointChanged() : pointRT[" + pointRT + "]");
      Log.d(TAG, "### pointChanged() : pointLB[" + pointLB + "]");
      Log.d(TAG, "### pointChanged() : pointRB[" + pointRB + "]");
      Log.d(TAG, "### pointChanged() : findCornerPoints[" + findCornerPoints + "]");
      Log.d(TAG, "### pointChanged() : mFirstLoading[" + mFirstLoading + "] mFirstImageLoading["
          + mFirstImageLoading + "]");
    }

    mZoomRate = zoomRate;
    mPointLT = pointLT;
    mPointRT = pointRT;
    mPointLB = pointLB;
    mPointRB = pointRB;
    mImageBitmap = imageBitmap;
    mFindCornerPoints = findCornerPoints;

    if (mFirstLoading
        && mFindCornerPoints == 1) { // mFindCornerPoints : -1(N/A), 0(false), 1(true)
      mFirstLoading = false;
      mFirstImageLoading = false;
      mHandler.sendMessage(
          Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_SAVING)); // call rImageSaving
    } else if (mFindCornerPoints != -1) {
      //	mHandler.sendMessage(Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_PROCESSED)); // call rImageProcessed
    }

  }

  public void pointChanged(float zoomRate, Point pointLT, Point pointRT, Point pointLB,
      Point pointRB, ArrayList<Point> pointmask, Bitmap imageBitmap, int findCornerPoints) {
    if (Config.DEBUG) {
      Log.d(TAG, "pointChanged: 22222222222222222222");
      Log.d(TAG, "### pointChanged() : zoomRate[" + zoomRate + "]");
      Log.d(TAG, "### pointChanged() : pointLT[" + pointLT + "]");
      Log.d(TAG, "### pointChanged() : pointRT[" + pointRT + "]");
      Log.d(TAG, "### pointChanged() : pointLB[" + pointLB + "]");
      Log.d(TAG, "### pointChanged() : pointRB[" + pointRB + "]");
      Log.d(TAG, "### pointChanged() : pointmask[" + pointmask + "]");
      Log.d(TAG, "### pointChanged() : findCornerPoints[" + findCornerPoints + "]");
      Log.d(TAG, "### pointChanged() : mFirstLoading[" + mFirstLoading + "] mFirstImageLoading["
          + mFirstImageLoading + "]");
    }

    mZoomRate = zoomRate;
    mPointLT = pointLT;
    mPointRT = pointRT;
    mPointLB = pointLB;
    mPointRB = pointRB;

    mMaskingPoint = pointmask;
    mImageBitmap = imageBitmap;
    mFindCornerPoints = findCornerPoints;

    if (mFirstLoading
        && mFindCornerPoints == 1) { // mFindCornerPoints : -1(N/A), 0(false), 1(true)
      mFirstLoading = false;
      mFirstImageLoading = false;
      mHandler.sendMessage(
          Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_SAVING)); // call rImageSaving
    } else if (mFindCornerPoints != -1) {
      //	mHandler.sendMessage(Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_PROCESSED)); // call rImageProcessed
    }
  }

  public void pointChanged(Bitmap imageBitmap, int findCornerPoints) {
    if (Config.DEBUG) {
      Log.d(TAG, "pointChanged: 333333333333333");
      Log.d(TAG, "### pointChanged() Bitmap: mFirstLoading[" + mFirstLoading + "]");
    }
    mImageBitmap = imageBitmap;
    mFindCornerPoints = findCornerPoints;

    if (mFirstLoading && mFindCornerPoints == 1) {
//      ((LinearLayout) findViewById(R.id.image_processing)).setVisibility(View.GONE);
//      ((LinearLayout) findViewById(R.id.image_processed)).setVisibility(View.GONE);
//      ((LinearLayout) findViewById(R.id.image_saving)).setVisibility(View.VISIBLE);
      mOkBtn.setVisibility(View.VISIBLE);
      mSaveBtn.setVisibility(View.GONE);

      if (isBlackBackground) {
        ViewUtil.GONE(mBackTopView);
        ViewUtil.GONE(mBackBottomView);
      }
      mPointView.loadImageForImageSaving(mImageBitmap, mColorSelection);
      checkAccountDoc();
      System.gc();
    }
  }

  //=========================================================
  // Private methods
  //=========================================================
  private Runnable rImageProcessing = new Runnable() {
    public void run() {
      if (Config.DEBUG) {
        Log.d(TAG, "Runnable rImageProcessing : run() : mFirstLoading[" + mFirstLoading
            + "] mFirstImageLoading[" + mFirstImageLoading + "] mFindCornerPoints["
            + mFindCornerPoints + "]");
      }

      File imgTempFile = new File(mImagePath);
      if (!imgTempFile.exists()) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.INTENT_EXTRA_IMAGE_PATH, mImagePath);
        intent.putExtra(AppConstants.INTENT_EXTRA_ROOT_PATH, mRootPath);
        intent.putExtra(AppConstants.INTENT_EXTRA_TEMP_PATH, mTempPath);
        intent.putExtra(AppConstants.INTENT_EXTRA_COLOR_SELECTION, mColorSelection);
        intent.putExtra(AppConstants.INTENT_EXTRA_DOC_KIND, mDocKind);
        intent.putExtra(AppConstants.INTENT_EXTRA_SCAN_CANCEL, "Y");
        PointChangedActivity.this.setResult(RESULT_OK, intent);
        finish();
        hideProgress(); // #1281
      }

      int displayWidth = getResources().getDisplayMetrics().widthPixels;
      int displayHeight = getResources().getDisplayMetrics().heightPixels;
      ConstraintLayout.LayoutParams topParam = (ConstraintLayout.LayoutParams) mTopLayout
          .getLayoutParams();
      ConstraintLayout.LayoutParams bottomParam = (ConstraintLayout.LayoutParams) mTopButtonContainer
          .getLayoutParams();

      int orangeHeight = displayWidth * 4 / 3;//이미지 세로 길이
      int idCardHeight =
          displayWidth * AppConstants.ID_CARD_SIZE_MIN
              / AppConstants.ID_CARD_SIZE_MAX;//신분증 사각잡기 세로 길이
      int margin = displayHeight - orangeHeight; //top + bottop 길이

      if (!AppConstants.IS_DISPLAY_ASPECT_RATIO_4TO3) {
        topParam.height = margin / 2;
        bottomParam.height = margin / 2;
      }
      int mainParamHeight = topParam.height;
      int bottomParamHeight = bottomParam.height;

      RelativeLayout.LayoutParams backTopParam = (RelativeLayout.LayoutParams) mBackTopView
          .getLayoutParams();
      RelativeLayout.LayoutParams backBottomParam = (RelativeLayout.LayoutParams) mBackBottomView
          .getLayoutParams();
      int backHeight =
          displayHeight - idCardHeight - (mainParamHeight + bottomParamHeight);//신분증 위 아래 여백

      if (backHeight % 2 == 0) {
        backTopParam.height = backHeight / 2;
        backBottomParam.height = backTopParam.height;
      } else {
        backHeight = backHeight + 1;
        backTopParam.height = backHeight / 2;
        backBottomParam.height = backTopParam.height - 1;
      }
      ViewGroup.LayoutParams pointViewLayoutLayoutParams = mPointViewLayout.getLayoutParams();
      pointViewLayoutLayoutParams.height = (displayHeight - mainParamHeight - bottomParamHeight);
      mPointViewLayout.setLayoutParams(pointViewLayoutLayoutParams);

      /*if (Config.DEBUG) {
        Log.d(TAG,
            "GHOOM_run: pointViewLayoutLayoutParams.height = " + pointViewLayoutLayoutParams.height
                + " ,width: " + pointViewLayoutLayoutParams.width);
        Log.d(TAG, "GHOOM_run: backtopParam.height = " + backTopParam.height);
        Log.d(TAG, "GHOOM_run: backbottomParam.height = " + backBottomParam.height);
        Log.d(TAG, "idenheight= " + idCardHeight + " ,orangeheight= " + orangeHeight);
        Log.d(TAG,
            "mainparam.height= " + topParam.height + " ,bottomparam.height= " + bottomParam.height);
        Log.d(TAG, "backtopParam.height= " + backTopParam.height + " backbottomParam.height= "
            + backBottomParam.height);
        Log.d(TAG, "Runnable rImageProcessing : run() : mDocKind [" + mDocKind + "]");
        Log.d(TAG, "Runnable rImageProcessing : run() : mColorSelection [" + mColorSelection + "]");
        Log.d(TAG, "Runnable rImageProcessing : run() : mSelMasking [" + mSelMasking + "]");
      }*/

//      (findViewById(R.id.image_processing)).setVisibility(View.GONE);
//      (findViewById(R.id.image_processed)).setVisibility(View.VISIBLE);
//      (findViewById(R.id.image_saving)).setVisibility(View.GONE);
      mOkBtn.setVisibility(View.VISIBLE);
      mSaveBtn.setVisibility(View.GONE);

//      mMaskingControlContainer = findViewById(R.id.masking_control_container);//마스킹 확대 축소 버튼
      // TODO: 2018-08-28 show loading progress...
//            showProgressDialog();
      mPointView.loadImageForImageProcessing(mImagePath, mColorSelection);

      if (AppConstants.DOC_KIND_E.equals(mDocKind)) {
        mPointView.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            return true;
          }
        });//매장사진일경우 촬영후 포인트점 움직이지 못하게 터치이벤트 막음
        mPointView.setIsVisiblePoint(false);
      }
      addButtonClickEvent();

      int visibility = (isBlackBackground) ? View.VISIBLE : View.GONE;
      mBackTopView.setVisibility(visibility);
      mBackBottomView.setVisibility(visibility);

      if (AppConstants.DOC_KIND_B.equals(mDocKind)) {
        ViewUtil.INVISIBLE(findViewById(R.id.tvmasking));
        if (PointView.retMaskingPoint.size() == 0) {
          PointView.retMaskingPoint.add(0, getIDCardPoint(selectedIDCardIndex));
        } else {
          PointView.retMaskingPoint.set(0, getIDCardPoint(selectedIDCardIndex));
        }
        mPointView.setDocKind(mDocKind, mColorSelection,
            mSelMasking); // 국내 거소증 또는 외국인 등록증 뒷면 사진은 마스킹 표시가 나오지 않게 국내 거소증 앞면 사진 및 다른 신분증은 마스킹 표시가 나오게
      }
      hideProgress();
      System.gc();
    }
  };



  /**
   * button 클릭 이벤트 등록
   */
  private void addButtonClickEvent() {
    mOkBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (mIsImageProcessingNow) {
          return;
        }
        Log.d(TAG, "onClick: Saved: mIsImageProcessingNow: " + mIsImageProcessingNow);
        if (AppConstants.DOC_KIND_E.equals(mDocKind)) {
          mPointView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              return false;
            }
          });//저장시 포인트점 위치로 canvas 생성하므로 터치이벤트 받을수 있게
        }
        mFirstLoading = false;
        mFirstImageLoading = false;
        if (Config.DEBUG) {
          Log.d(TAG, "Runnable rImageProcessing : run() : mSaveBtn onClick : call rImageSaving");
        }
        mIsConfirmVertex = mIsImageProcessingNow = true;

        //---------------------------------------------------------
        // 설치 사진일 경우 engine 거치지 않고 바로 저장 2019-03-18 _ PC-jhKim
        //---------------------------------------------------------
        if (mDocKind.equals(AppConstants.DOC_KIND_E)) {
          gotoPictureList(null);
        } else {
          mHandler.sendMessage(
              Message.obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_SAVING)); // call rImageSaving
        }

      }
    });

    mCancelBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (Config.DEBUG) {
          Log.d(TAG, "Runnable rImageProcessing : run() : mCancelBtn onClick : deleteFile["
              + mImagePath + "]");
        }
        if (mIsConfirmVertex) {
          resetVertexPosition();
        } else {
          cancel();
        }
      }
    });

    mSaveBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (Config.DEBUG) {
          Log.d(TAG,
              "Runnable rImageProcessing : run() : mOkBtn onClick : deleteFileDir[" + mTempPath
                  + "]");
        }
        //reset a pointView
        mPointView.reset();

        if ((AppConstants.DOC_KIND_B.equals(mDocKind) && mPointView.getMasking())
            || AppConstants.DOC_KIND_AA.equals(mDocKind)
            || (AppConstants.DOC_MASKING_E.equals(mSelMasking)
            || AppConstants.DOC_MASKING_C
            .equals(mSelMasking))) { //shlee AA타입인 주민등록증 발금신청확인서의 경우에도 매스킹 체크
          boolean isRight = false;    // 마스킹 설정된 위치 이동여부(true:이동하지 않음, false:이동)

          if (AppConstants.DOC_MASKING_E.equals(mSelMasking) && (selectedIDCardIndex
              < 5)) {//마스킹 필수인 경우만 마스킹 위치 체크
            try {
              Point tempPT = getIDCardPoint(selectedIDCardIndex);
              Point calMaskingPoint;
              if (AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
                  .equals(mSelectedFormId)) { // 주민등록신청발급서 주민등록초본  #1341
                calMaskingPoint = mPointView.getCalculatedMaskingPoint(tempPT);
              } else {    // 신분증인 경우
                calMaskingPoint = mPointView.getCalculatedMaskingPointForId(tempPT);
              }
              if (calMaskingPoint != null
                  && Math.abs(mMaskingPoint.get(0).x - calMaskingPoint.x) < 20
                  && Math.abs(mMaskingPoint.get(0).y - calMaskingPoint.y) < 20) {
                isRight = true;
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }//마스킹 필수인 경우만 마스킹 위치 체크

          if (AppConstants.DOC_MASKING_E.equals(mSelMasking) && !isRight
              && selectedIDCardIndex < 5) { //신분증 및 주민등록증 발급신청 확인서 마스킹 위치 변경시
            int resId = getIdNumResource(selectedIDCardIndex);

            mPointChangedActDialog.show();
            mPointChangedActDialog.setButtons(CustomAlertDialog.TWO_BUTTON);
            mPointChangedActDialog.setTwoButtonClickListener(PointChangedActivity.this)
                .setDialogMessage(R.string.idcard_waring);
            mIdConfirmHideIdNum = mPointChangedActDialog.getId();
          } else {
            gotoPictureList(mMaskingPoint);
          }
        } else {
          gotoPictureList(null);
        }
      }
    });

    mAddBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (AppConstants.DOC_MASKING_C.equals(mSelMasking) || AppConstants.FORM_ID_J9
            .equals(mSelectedFormId)) {  //주민등록발급신청서  주민등록초본 (doc A  maskin E)  #1341
          if (mPointView.mMaskingPoint.size() < MAX_MASKING_NUM) { // 최대 갯수 설정

            Point retpt = new Point();

            retpt.x = (mPointView.getWidth() - mPointView.MASKING_WIDTH) / 2;
            retpt.y =
                (mPointView.getHeight() - mPointView.MASKING_HEIGHT) / 2 + (10 * mMaskingNumber);

            float rangeHEIGHT = PointView.mImageMaxY;
            if (Config.DEBUG) {
              Log.d(TAG, "rangeHEIGHT= " + rangeHEIGHT);
            }
            int maskingHeight = retpt.y + mPointView.MASKING_HEIGHT
                + mPointView.mHeight[mPointView.mSelectedMaskingIdx];//마스킹 세로 길이
            if (maskingHeight < rangeHEIGHT) //생성시 이미지범위 제한
            {
              mMaskingNumber++;
            }

            mPointView.mMaskingPoint.add(retpt);
            PointView.retMaskingPoint = mPointView.mMaskingPoint;
            mPointView.mSelectedMaskingIdx = mPointView.mMaskingPoint.size() - 1;//추가한 마스킹 선택
            mPointView.NewMasking = true;
            mPointView.invalidate();
            if (Config.DEBUG) {
              Log.d(TAG, "mMaskingNumber" + mMaskingNumber + "mPointView.mMaskingPoint.size()"
                  + mPointView.mMaskingPoint.size());
            }
//            if (mPointView.mMaskingPoint.size() == 1) {
//              mMaskingControlContainer.setVisibility(View.VISIBLE);
//            }

          } else { //mPointView.mMaskingPoint.size()  100개 일경우
            mPointChangedActDialog.show();
            mPointChangedActDialog.setDialogMessage(R.string.masking_size_msg)
                .setOneButtonClickListener(PointChangedActivity.this);
            mPointChangedActDialog.setButtons(CustomAlertDialog.ONE_BUTTON);
            mIdExceedMaskMaxNum = mPointChangedActDialog.getId();
          }
        } else if (AppConstants.DOC_KIND_B.equals(mDocKind)) {
          if (mPointView.mMaskingPoint.size() < 2) {
            foreignmaskingadds();
          } else { //mPointView.mMaskingPoint.size()  3개 일경우

            mPointChangedActDialog.show();
            mPointChangedActDialog.setOneButtonClickListener(PointChangedActivity.this)
                .setDialogMessage(R.string.masking_no_msg);
            mPointChangedActDialog.setButtons(CustomAlertDialog.ONE_BUTTON);
            mIdUnableToAddMask = mPointChangedActDialog.getId();
          }
        }
      }
    });

    mRemoveBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (mPointView.mMaskingPoint.size() >= 1) {

          if ((AppConstants.DOC_KIND_B.equals(mDocKind) && mPointView.mSelectedMaskingIdx == 0) ||
              //외국인등록증 또는 국내거소증 이고 디폴트 마스킹 지울 경우
              (AppConstants.FORM_ID_J9.equals(mSelectedFormId)
                  && mPointView.mSelectedMaskingIdx == 0)) {//주민등록초본 이고 디폴트 마스킹 지울 경우  #1341

            mPointChangedActDialog.show();
            mPointChangedActDialog.setButtons(CustomAlertDialog.ONE_BUTTON);
            mPointChangedActDialog.setDialogMessage(R.string.masking_delete_msg)
                .setOneButtonClickListener(PointChangedActivity.this);
            mIdUnableToRemoveMask = mPointChangedActDialog.getId();

            /*new PentaonAlertCommonDialog(PointChangedActivity.this
                , getString(R.string.alert_title)
                , getString(R.string.masking_delete_msg)
                , AppConstants.ALERT_OK).show();*/
            return;
          }

          mPointView.mMaskingPoint.remove(mPointView.mSelectedMaskingIdx);
          mPointView.DelMasking = true;
          PointView.retMaskingPoint = mPointView.mMaskingPoint;
          mMaskingNumber--;
          mPointView.invalidate();
        }
      }
    });
  }

  private void foreignmaskingadds() {
    Point tempPT = getIDCardSubPoint(selectedIDCardIndex);
    Point retpt = mPointView.getCalculatedMaskingPointForId(tempPT);

    mPointView.mMaskingPoint.add(retpt);
    PointView.retMaskingPoint = mPointView.mMaskingPoint;
    mPointView.mSelectedMaskingIdx = mPointView.mMaskingPoint.size() - 1;//추가한 마스킹 선택
    mPointView.NewMasking = true;
    mPointView.invalidate();
  }

  private void gotoPictureList(final ArrayList<Point> p) {
    if (Config.DEBUG) {
      Log.d(TAG, "gotoPictureList");
    }
    UIThread.getInstance().executeInUIThread(new Runnable() { // #1281
      @Override
      public void run() {
        showProgress(getString(R.string.image_processing));
      }
    });
    new Thread(new Runnable() { // #1281
      @Override
      public void run() {
        if ((AppConstants.DOC_MASKING_E.equals(mSelMasking) && selectedIDCardIndex < 5)
            || AppConstants.FORM_ID_J9.equals(mSelectedFormId)
            || AppConstants.DOC_MASKING_C
            .equals(mSelMasking)) {  // 정형마스킹 ( 주민등록, 운전면허, 외국인등록앞면, 국내거소앞면 ,주민등록발급확인서),비정형마스킹
          Bitmap copyBitmap = mImageBitmap.copy(Bitmap.Config.ARGB_8888, true);

          Canvas canvas = new Canvas(copyBitmap);
          Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
          paint.setStyle(Style.FILL);
          paint.setColor(Color.BLACK);

          float widthRate = 0;
          float heightRate = 0;
          float roundVal = GUIUtil.Dip2Pixel(getBaseContext(),
              (int) getResources().getDimension(R.dimen.pointchanged_roundval));
          if (mPointView.mImageDrawable != null) { // #1213
            for (int i = 0; i < p.size(); i++) {
              //pointView 의 마스킹 사이즈는 단말기 해상도 기준 이므로 여기서는 A4 픽셀 기준으로 변경
              RectF rect = null;
              int marginX = mPointView.mImageDrawable.getBounds().left;
              int marginY = mPointView.mImageDrawable.getBounds().top;
              int drawWidth =
                  mPointView.mImageDrawable.getBounds().right - mPointView.mImageDrawable
                      .getBounds().left;
              int drawHeight =
                  mPointView.mImageDrawable.getBounds().bottom - mPointView.mImageDrawable
                      .getBounds().top;
              if (Config.DEBUG) {
                Log.d(TAG, "gotoPictureList marginX : " + marginX + ", marginY : " + marginY);
                Log.d(TAG,
                    "gotoPictureList drawWidth : " + drawWidth + ", drawHeight : " + drawHeight);
              }
              int screenWidth = ScreenInfoManager.getInstance().getDisplayMetrics().getWidth();
              widthRate=heightRate=(float) (AppConstants.PAPER_SIZE_MAX/2)/screenWidth;
              float l = (p.get(i).x - marginX) * widthRate;
              float t = (p.get(i).y - marginY) * heightRate;
              float r = (p.get(i).x + mPointView.MASKING_WIDTH + mPointView.mWidth[i] - marginX)
                  * widthRate;
              float b = (p.get(i).y + mPointView.MASKING_HEIGHT + mPointView.mHeight[i] - marginY)
                  * heightRate;
              rect = new RectF(l, t, r, b);

              canvas.drawRoundRect(rect, roundVal, roundVal, paint);
            }
          }

          mImageBitmap = copyBitmap.copy(Bitmap.Config.ARGB_8888, true);


        }

        if (AppConstants.DOC_KIND_B.equals(mDocKind) || AppConstants.DOC_KIND_C.equals(mDocKind)
            || AppConstants.DOC_KIND_D.equals(mDocKind) || AppConstants.DOC_KIND_E
            .equals(mDocKind)) { // 신분증,여권,거래통장,매장사진은 상단 정렬

          int width = PointView.retPoint[1].x - PointView.retPoint[0].x;
          int height = PointView.retPoint[2].y - PointView.retPoint[0].y;
          int top = 188;//188
          if (AppConstants.DOC_KIND_D.equals(mDocKind) && width > height) { //거래통장이 가로로 캡춰된 경우
            top = 0;
          }

          ImageProcessing
              .saveFile(mImageBitmap, mImagePath, mColorSelection, mDocKind, top);
        } else {
          ImageProcessing.saveFile(mImageBitmap, mImagePath, mColorSelection);
        }
        Intent intent = new Intent();
        intent.putExtra(AppConstants.INTENT_EXTRA_IMAGE_PATH,mImagePath);
        setResult(RESULT_OK,intent);
        finish();
        /*try {
          if (selectedIDCardIndex >= 3 || AppConstants.DOC_KIND_E
              .equals(mDocKind)) { //shlee 어떤 신분증 종류를 선택했는지 파일에 idx값을 저장함

            ExifInterface ei = new ExifInterface(mImagePath);
            ei.setAttribute(ExifInterface.TAG_MAKE, "" + selectedIDCardIndex);
            ei.saveAttributes();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }*/

        /*Intent intent = new Intent(PointChangedActivity.this,MainActivity.class);

        mSelectedDocInfo.put(AppConstants.INTENT_EXTRA_IMAGE_PATH,mImagePath);
        if (selectedIDCardIndex >= 3 || AppConstants.DOC_KIND_E.equals(mDocKind)) {
          mSelectedDocInfo.put(AppConstants.INTENT_EXTRA_SEL_CARD_IDX, selectedIDCardIndex);
        }
        intent.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfo);*/
        /*PointChangedActivity.this.setResult(RESULT_OK, intent);
        finish();*/

        hideProgress();   // #1281


      }
    }).start();
  }

  /**
   * 신분증 마스킹 기본 위치 가져오기(mm기준)
   *
   * @see PointView#getCalculatedMaskingPoint(Point)
   * @see PointView#getCalculatedMaskingPointForId(Point)
   */
  private Point getIDCardPoint(int index) {
    if (Config.DEBUG) {
      Log.d(TAG, "getIDCardPoint= " + index);
    }
    Point retpt = new Point();

    switch (index) {
      case 1: //주민등록증
        retpt.x = 27;
        retpt.y = 21;
        break;
      case 2: //운전면허증
        retpt.x = 46;
        retpt.y = 20;
        break;
      case 3: //외국인등록증
      case 4: //국내거소신고증
        retpt.x = 52;
        retpt.y = 12;
        break;
      case 0: // 신분증(or 개인사업이 미성년대표 인경우 법정대리인 신분증 사본)의 주민등록증 발급신청 확인서
        retpt.x = 49;
        retpt.y = 52;
        break;
      case 100: // 주민등록초본
        retpt.x = 170;
        retpt.y = 45;
        break;
      default: // default로 오는 경우는 없으나 예외처리를 위해 추가
        retpt.x = 1;
        retpt.y = 1;
        break;
    }
    return retpt;
  }

  /**
   * 외국인등록증/국내거소신고증 추가 마스킹 위치 가져오기(mm기준)
   *
   * @see PointView#getCalculatedMaskingPointForId(Point)
   */
  private Point getIDCardSubPoint(int index) {
    if (Config.DEBUG) {
      Log.d(TAG, "getIDCardSubPoint= " + index);
    }
    Point retpt = new Point();

    switch (index) {
      case 3: //외국인등록증
      case 4: //국내거소신고증
        retpt.x = 17;
        retpt.y = 45;
        break;
      default: // default로 오는 경우는 없으나 예외처리를 위해 추가
        retpt.x = 1;
        retpt.y = 1;
        break;
    }
    return retpt;
  }

  private Runnable rImageSaving = new Runnable() {
    public void run() {
      Log.d(TAG, "run: rImageSaving-------");
      if (Config.DEBUG) {
        Log.d(TAG, "Runnable rImageSaving : run() : mFirstLoading[" + mFirstLoading
            + "] mFirstImageLoading[" + mFirstImageLoading + "] mFindCornerPoints["
            + mFindCornerPoints + "]");
        Log.d(TAG,
            "Runnable rImageSaving : run() : mZoomRate=" + mZoomRate + " mPointLT=[" + mPointLT
                + "] mPointRT=[" + mPointRT + "] mPointLB=[" + mPointLB + "] mPointRB=["
                + mPointRB);
      }

      if (mImageBitmap == null) {
        cancel();
        return;
      }

      Point[] pt = new Point[4];
      pt[0] = new Point();
      pt[1] = new Point();
      pt[2] = new Point();
      pt[3] = new Point();

      pt[0].set(mPointLT.x, mPointLT.y);
      pt[1].set(mPointRT.x, mPointRT.y);
      pt[2].set(mPointLB.x, mPointLB.y);
      pt[3].set(mPointRB.x, mPointRB.y);

      if (pt[0].x >= PointView.mImageMinX) {
        pt[0].x -= PointView.mImageMinX;
        pt[1].x -= PointView.mImageMinX;
        pt[2].x -= PointView.mImageMinX;
        pt[3].x -= PointView.mImageMinX;
      }
      int gap = mDocKind.equals(AppConstants.DOC_KIND_A) ? 0
          : (isHighExtraHighDensity ? 7 : 12); // 문서가 아닌 경우 크롭하는 영역이 넓어지는 문제가 있어 이를 보정함
      int gap2 =
          (!mDocKind.equals(AppConstants.DOC_KIND_A) && !isHighExtraHighDensity) ? 5
              : 0; // tab A인 경우  문서가 아닌경우 위아래로 더 크롭하는 영역이 넓어지는 문제가 있어 이를 보정함

      int lbVal = (int) ((mPointLB.y - (PointView.mImageMinY)) / mZoomRate);
      int rbVal = (int) ((mPointRB.y - (PointView.mImageMinY)) / mZoomRate);

      if (lbVal >= mImageBitmap.getHeight()) {
        lbVal = mImageBitmap.getHeight();
      }
      if (rbVal >= mImageBitmap.getHeight()) {
        rbVal = mImageBitmap.getHeight();
      }

      pt[0].set((int) ((mPointLT.x - (PointView.mImageMinX)) / mZoomRate) + gap,
          (int) ((mPointLT.y - (PointView.mImageMinY)) / mZoomRate) + gap + gap2);
      pt[1].set((int) ((mPointRT.x - (PointView.mImageMinX)) / mZoomRate) - gap,
          (int) ((mPointRT.y - (PointView.mImageMinY)) / mZoomRate) + gap + gap2);
      pt[2]
          .set((int) ((mPointLB.x - (PointView.mImageMinX)) / mZoomRate) + gap, lbVal - gap - gap2);
      pt[3]
          .set((int) ((mPointRB.x - (PointView.mImageMinX)) / mZoomRate) - gap, rbVal - gap - gap2);

      if (Config.DEBUG) {
        Log.d(TAG,
            "Runnable rImageSaving : run() : mZoomRate=" + mZoomRate + " mPointLT=[" + mPointLT
                + "] mPointRT=[" + mPointRT + "] mPointLB=[" + mPointLB + "] mPointRB=["
                + mPointRB);
      }

      try {
        if (Config.DEBUG) {
          Log.d(TAG,
              "Runnable rImageSaving : run() : ]" + mColorSelection + "["
                  + mColorSelection + "] mDocKind[" + mDocKind + "]");
        }

        mImageBitmap = ImageProcessing
            .saving(PointChangedActivity.this, mImageBitmap, pt, mColorSelection, mDocKind, true);

      } catch (Exception e) {
        e.printStackTrace();
      } catch (Throwable e) {
        e.printStackTrace();
      }

      hideProgress(); // #1281

//            UIThread.getInstance().executeInUIThread(new Runnable() { // #1281
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
//          (findViewById(R.id.image_processing)).setVisibility(View.GONE);
//          (findViewById(R.id.image_processed)).setVisibility(View.GONE);
//          (findViewById(R.id.image_saving)).setVisibility(View.VISIBLE);
          mOkBtn.setVisibility(View.GONE);
          mSaveBtn.setVisibility(View.VISIBLE);

          if (AppConstants.DOC_KIND_E.equals(mDocKind)) {
            //ViewUtil.GONE(mResetBtn);//매장사진일경우 안보이게
            ViewUtil.GONE(mAddBtn);
            ViewUtil.GONE(mRemoveBtn);
          }
          if (isBlackBackground) {
            ViewUtil.GONE(mBackTopView);
            ViewUtil.GONE(mBackBottomView);

            if (AppConstants.DOC_KIND_B.equals(mDocKind) && mPointView.getMasking()) {
              ViewUtil.VISIBLE(findViewById(R.id.tvmasking));
              int resId = getIdNumResource(selectedIDCardIndex);
              ((TextView) findViewById(R.id.tvmasking)).setText(R.string.masking_msg);
            }
          }

          if (AppConstants.DOC_KIND_B.equals(mDocKind) || AppConstants.DOC_KIND_AA
              .equals(mDocKind)) { // 사각잡기
            mMaskingNumber = 1;
//                        mPointView.mMaskingPoint.add(getIDCardPoint(selectedIDCardIndex));
            // TODO: 2018-10-08 check mPointView.mMaskingPoint.size()== 0 occurs ArrayIndexOutOfBoundsException!

            mPointView.mMaskingPoint.add(getIDCardPoint(selectedIDCardIndex));
            PointView.retMaskingPoint = mPointView.mMaskingPoint;

          }
          mPointView.loadImageForImageSaving(mImageBitmap, mColorSelection);
          checkAccountDoc();
          mIsImageProcessingNow = false;
          //m_progressDialog.dismiss();
          //removeDialog(DIALOG_PROGRESS);
          System.gc();
        }
      });
    }
  };

  private void createHandler() {
    mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (msg.what == AppConstants.MSG_WHAT_IMAGE_PROCESSING) {
          runOnUiThread(rImageProcessing);
        } else if (msg.what == AppConstants.MSG_WHAT_IMAGE_PROCESSED) {
//                    runOnUiThread(rImageProcessed);
        } else if (msg.what == AppConstants.MSG_WHAT_IMAGE_SAVING) {
          runOnUiThread(new Runnable() { // #1281
            @Override
            public void run() {
              showProgress(getString(R.string.image_processing));
            }
          });
          new Thread(rImageSaving).start();
        }
      }
    };
  }

  /**
   * 거래통장인지를 확인하여 화면 아래에 통장 정보를 보이게 함
   */
  private void checkAccountDoc() {
    if (AppConstants.DOC_KIND_D.equals(mDocKind)) { // shlee 거래통장이면 경고 문구 추가
      TextView masktxt = (TextView) findViewById(R.id.tvmasking);
//            ViewUtil.VISIBLE(masktxt);
      masktxt.setGravity(Gravity.LEFT);
      masktxt.setTextColor(Color.BLACK);
      masktxt.setPadding(0, 20, 0, 0);
      RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) masktxt.getLayoutParams();

      int marginB;
      if (mDpi == DisplayMetrics.DENSITY_MEDIUM) {
        marginB = 60;
      } else if (mDpi == DisplayMetrics.DENSITY_HIGH) {
        marginB = 180;
      } else if (AppConstants.IS_EXTRA_HIGH_DENSITY_2048X1536) {
        marginB = 120;
      } else {
        marginB = 200;
      }
      layout.setMargins(0, 0, 0, marginB);//mPointView.isHighExtraHighDensity ? 200 : 60

      masktxt.setLayoutParams(layout);
//            masktxt.setText(getString(R.string.bankid_check_msg, mBankACNO, mDpsrNM, mBankNM));
    }

    if (AppConstants.DOC_KIND_AA.equals(mDocKind)) {  //shlee 주민등록증 발급신청서인 경우 매스킹 위치 세팅
      selectedIDCardIndex = 0;
      mPointView.mMaskingPoint.set(0, getIDCardPoint(selectedIDCardIndex));
      mPointView.retMaskingPoint = mPointView.mMaskingPoint;
    } else if (AppConstants.FORM_ID_J9.equals(
        mSelectedFormId)) { // 주민등록초본 // 마스킹 구분 (E : 필수 -> 정형, C : 선택 -> 비정형, N : 사용하지 않음) 1341
      selectedIDCardIndex = 100;
      mPointView.mMaskingPoint.set(0, getIDCardPoint(selectedIDCardIndex));
      mPointView.retMaskingPoint = mPointView.mMaskingPoint;
    }

    if (AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9.equals(mSelectedFormId)
        || AppConstants.DOC_MASKING_C.equals(mSelMasking)) {//주민등록발급신청서 및 , 주민등록초본, 비정형 마스킹  #1341
      int resId = getIdNumResource(selectedIDCardIndex);

//      PentaonAlertCommonDialog dialog = null;
      mOkBtn.setClickable(false);
      if (AppConstants.DOC_MASKING_C.equals(mSelMasking)) {
        mPointChangedActDialog.show();
        mPointChangedActDialog.setDialogMessage(R.string.masking_add_msg)
            .setOneButtonClickListener(this);
        mIdGuideMaskAddAlert = mPointChangedActDialog.getId();

      } else {
        mPointChangedActDialog.show();
        mPointChangedActDialog.setDialogMessage(R.string.masking_msg)
            .setOneButtonClickListener(this);
        mIdGuideMaskMoveAlert = mPointChangedActDialog.getId();
      }
    }

    if (AppConstants.DOC_MASKING_C.equals(mSelMasking) || selectedIDCardIndex == 3
        || selectedIDCardIndex == 4 || selectedIDCardIndex == 100) { //비정형마스킹 ,외국인등록증 앞면, 국내거소증 앞면
      mContainerMaskAddRemove.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 선택된 신분증 ID를 받아서 신분증 번호 리소스 ID를 준다 종류 : 주민등록번호/외국인등록번호/국내거소신고번호
   *
   * @return 신분증 번호 리소스 ID
   */
  private int getIdNumResource(int selectedIndex) {
    int resId = R.string.idcard_num_type1;  // 주민등록증 발급신청 확인서(AA), 주민등록초본의 경우 selectedIDCardIndex가 선택되지 않음
    switch (selectedIndex) {
      case 1: // 주민등록증
      case 2: // 운전면허증
        resId = R.string.idcard_num_type1;
        break;
      case 3: // 외국인등록증 앞면
      case 6: // 외국인등록증 뒷면
        resId = R.string.idcard_num_type3;
        break;
      case 4: // 국내거소증 앞면
      case 5: // 국내거소증 뒷면
        resId = R.string.idcard_num_type4;
        break;
    }
    return resId;
  }

  private void cancel() {
//    SystemUtil.deleteFile(mImagePath);
    //reset a pointView
    mPointView.reset();
    Intent intent = new Intent();
    intent.putExtra(AppConstants.INTENT_EXTRA_IMAGE_PATH,"");
    intent.putExtra(AppConstants.INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP, mSelectedDocInfo);
    intent.putExtra(AppConstants.INTENT_EXTRA_SCAN_USER_CANCEL, true);

    PointChangedActivity.this.setResult(RESULT_OK, intent);
    finish();
  }

  /**
   * 사각잡기 좌표 재설정
   */
  private void resetVertexPosition() {
    mFirstLoading = false;
    mFirstImageLoading = true;
    mIsConfirmVertex = false;
    mPointView.setIsRotated(false);

    if (Config.DEBUG) {
      Log.d(TAG,
          "Runnable rImageProcessing : run() : mResetBtn onClick : deleteFile[" + mImagePath
              + "] call rImageProcessing");
    }

    UIThread.getInstance().executeInUIThread(new Runnable() { // #1281
      @Override
      public void run() {
        showProgress(getString(R.string.please_wait_loading));
      }
    });

    new Thread(new Runnable() { // #1281
      @Override
      public void run() {
//        SystemUtil.deleteFile(mImagePath);
        mHandler.sendMessage(Message
            .obtain(mHandler, AppConstants.MSG_WHAT_IMAGE_PROCESSING)); // call rImageProcessing
      }
    }).start();
  }


  @Override
  public void onLeftButtonClick(int id) {
    /*if(id==mIdConfirmHideIdNum)
    {
      mPointChangedActDialog.dismiss();
    }*/
  }

  @Override
  public void onRightButtonClick(int id) {
    if (id == mIdConfirmHideIdNum) {
      gotoPictureList(mMaskingPoint);
    }
  }

  @Override
  public void onNeutralButtonClick(int id) {
    mPointChangedActDialog.dismiss();
    if (id == mIdGuideMaskAddAlert || id == mIdGuideMaskMoveAlert) {
      mOkBtn.setClickable(true);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_rotate_left:
        mIndexRotationLeft++;
        if (mIndexRotationLeft > 3) {
          mIndexRotationLeft = 0;
        }
        mPointView.onRotateImage(ROTATION_DEGREE[mIndexRotationLeft]);
        break;
      case R.id.btn_rotate_right:
        mIndexRotationRight--;
        if (mIndexRotationRight < 0) {
          mIndexRotationRight = 3;
        }
        mPointView.onRotateImage(ROTATION_DEGREE[mIndexRotationRight]);
        break;
    }
  }

  @Override
  public void onRotateComplete(WidthHeight widthHeight) {
    LayoutParams layoutParams = mPointView.getLayoutParams();
//    layoutParams.width = widthHeight.getWidth();
    layoutParams.height = layoutParams.width * widthHeight.getHeight() / widthHeight.getWidth();
    mPointView.setLayoutParams(layoutParams);
    mPointView.requestLayout();

  }
}
