package com.pentaon.vzon.ui.scan;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pentaon.vzon.R;
import com.pentaon.vzon.activity.PointChangedActivity;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.common.LayoutInfo;
import com.pentaon.vzon.data.WidthHeight;
import com.pentaon.vzon.dataset.ScanDataInfo;
import com.pentaon.vzon.manager.ScreenInfoManager;
import com.pentaon.vzon.ui.scan.support.UISupport;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.GUIUtil;
import com.pentaon.vzon.utils.SystemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class PointView extends View {

  //===================================================
  //Static members
  //===================================================

  private static final int BIAS_WIDTH = 0x000; //width 기준
  private static final int BIAS_HEIGHT = 0x001; //height 기준
  private static final int SIZE_MAGNIFIER = 200;
  private static final float NOT_DEFINE = -1f;
  private static final int POINT_BUFFER = 50;

  public static int mImageWidth, mImageHeight, mImageMinX, mImageMinY, mImageMaxX, mImageMaxY;

  public static ArrayList<Point> retMaskingPoint = new ArrayList<>();
  public static Point[] retPoint = null;

  private static final float TOUCH_AREA = 300f;
  private static float mStrokeWidth;
  private static float CIRCLE_RADIUS = 20f;//(isHighExtraHighDensity) ? 60 : 874 * 60 / 2260;
  private static final float MIN_DIFF_RATIO = 0.01f;
  private static int CORRECTION_POINT_TOP_X = 10;
  private static int CORRECTION_POINT_TOP_Y = 10;
  private static int CORRECTION_POINT_BOTTOM_X = 10;
  private static int CORRECTION_POINT_BOTTOM_Y = 10;
  private static int DEFAULT_LINE_STROKE_WIDTH = 2;    // 포인트 라인 1dp
  private static int DEFAULT_MASKING_STROKE_WIDTH = 2;    // 마스킹 테두리 라인 2dp
  private static ScanDataInfo mScanInfo;
  private static boolean isTouched = false;
  private final int mHorizontalMargin;
  private final int mVerticalMargin;
  private final ScreenInfoManager mScreenInfoManager;

  //===================================================
  //Non-static members
  //===================================================
  public Drawable mImageDrawable; // #1213
  public ArrayList<Point> mMaskingPoint = new ArrayList<Point>();
  public int MASKING_WIDTH;//= (isHighExtraHighDensity) ? 300 : 130;
  public int MASKING_HEIGHT;// =(isHighExtraHighDensity) ? 85 : 30;
  public int mSelectedMaskingIdx;
  public int[] mWidth = new int[100];
  public int[] mHeight = new int[100];
  public boolean NewMasking = false;
  public boolean DelMasking = false;


  private final LayoutInfo mLayoutInfo;
  private final int mTopMargin;// NativeCameraAct에서 back, backTop의 height정보를 가져와서 circle 핸들러 좌표 구할 때 사용
  private final int mPreviewHeight; //NativeCameraAct에서 전체 preview 영역 중에서 화면에 보인 preview 영역의 height값

  private ImageProcessListener mImageProcessListener = null;
  private RotateListener mRotateListener;

  private Context mContext;
  private pointChangedListener mListener;
  private ZoomImageListener mZoomImgListener;
  private WidthHeight mRealDisplaySize;
  //  private WidthHeight mPreviewSize;
  private PointF mZoomPos;
  private Matrix mMatrix;
  private Point mPointLT = new Point();
  private Point mPointRT = new Point();
  private Point mPointLB = new Point();
  private Point mPointRB = new Point();
  private LinkedList<Point> mCornerPoints = new LinkedList<>();
  private Paint mCirclePaint;
  private Paint mZoomingPaint;

  private int mMeasuredWidth, mMeasuredHeight;
  private int mWidthPixels;
  private int mHeightPixels;
  private int mDiffHeight;
  private int selectedIDCardIndex;
  private int dpi;
  private int mImageRotation;
  private int mFindCornerPoints; // -1(N/A), 0(false), 1(true)
  private int maskingSizeIndex = 0;
  private float mTouchX, mTouchY;
  private float mTouchRemainderX;   // touch 움직임 반영한 나머지 X 값
  private float mTouchRemainderY;   // touch 움직임 반영한 나머지 Y 값
  private float mTargetCircleX;
  private float mTargetCircleY;
  private float mZoomRate;
  private float mWidthZoomRate;
  private float mHeightZoomRate;

  private String TAG = "PointView";
  private String mLoadImagePath;
  //  private String mCameraMode;
  private String mColorSelection;
  private String mDocKind = "";
  private String mSelMasking; //shlee add 마스킹 구분
  private String mSelectedFormId; // #1341

  //  private boolean mMoveLT, mMoveRT, mMoveLB, mMoveRB, mMoveMask, mMoveCT, mMoveCB, mMoveCL, mMoveCR;
  private boolean mMoveMask;
  private boolean mSetAnchor;
  private boolean isBlackBackground = false;
  private boolean isMasking = false;
  private boolean mIsRotated = false;
  private boolean mIsVisiblePoint;
  private boolean mIsImageFromGallery = false;
  private boolean mIsZooming;
  private boolean mIsTouch = false;

  private Bitmap mImageBitmap;
  private Bitmap mTempBitmap;
  private Bitmap mZoomingBitmap;
  private BitmapShader mShader;

  private float[] mTextSize = new float[100];
  private float[] mTextWidth = new float[100];
  private float[] mTextHeight = new float[100];
  private int mTouchCircleIndex = AppConstants.NOT_DEFINED;// 모서리 잡기 할 때 touch 중인 circle의 mCornerCircle에서 index

  //================================================================
  // interface
  //================================================================

  public interface pointChangedListener {

    void pointChanged(float zoomRate, Point pointLT, Point pointRT, Point pointLB, Point pointRB,
        Bitmap imageBitmap, int findCornerPoints);

    void pointChanged(float zoomRate, Point pointLT, Point pointRT, Point pointLB, Point pointRB,
        ArrayList<Point> pointMask,
        Bitmap imageBitmap, int findCornerPoints);

    void pointChanged(Bitmap imageBitmap, int findCornerPoints);
  }

  /**
   * 이미지 loading 완료 listener
   */
  public interface ImageProcessListener {

    void onCompleteImageLoading();
  }

  public interface ZoomImageListener {

    void onChangeZoomPoint(int action, Point p);

    void setZoomImage(Bitmap bitmap);
  }

  public interface RotateListener{
    void onRotateComplete(WidthHeight widthHeight);
  }

  //===============================================================
  // Constructor
  //===============================================================
  public PointView(Context context) {
    this(context, null);
    init();
  }

  public PointView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
    if (Config.DEBUG) {
      Log.d(TAG, "PointView() : attrs[" + attrs + "] this.getTag()[" + this.getTag()
          + "] this.getMeasuredWidth()[" + this.getMeasuredWidth() + "] this.getMeasuredHeight()["
          + this.getMeasuredHeight() + "]");
    }
    mContext = context;
    mIsVisiblePoint = true;
    if (context instanceof pointChangedListener) {
      mListener = (pointChangedListener) context;
    }

    if(context instanceof RotateListener){
      mRotateListener = (RotateListener)context;
    }

    setBackgroundColor(Color.BLACK);
    mSelectedMaskingIdx = 0;
    Log.d(TAG, "PointView: mStrokeWidth  = " + mStrokeWidth);
    if (mStrokeWidth < 1) {
      mStrokeWidth = DEFAULT_LINE_STROKE_WIDTH * SystemUtil.getDensityRate(context);
    }

    mCirclePaint = new Paint(Paint.DITHER_FLAG);
    mCirclePaint.setAntiAlias(true); //경계면을 부드럽게
    mCirclePaint.setDither(true);
    mCirclePaint.setStrokeJoin(Paint.Join.ROUND); //끝모양을 둥글게
    mCirclePaint.setStrokeCap(Paint.Cap.ROUND); //모서리 둥글게
//        mCirclePaint.setColor(mSignColor); //펜 컬러
    mCirclePaint.setStyle(Paint.Style.STROKE); //펜 스타일
    mCirclePaint.setStrokeJoin(Paint.Join.ROUND); //끝모양을 둥글게
    mCirclePaint.setStrokeCap(Paint.Cap.ROUND); //모서리 둥글게
    mCirclePaint.setStrokeWidth(mStrokeWidth); //펜 굵기

    //전무님지시 5mm 기준  갤럭시2014에서 5mm 는 60 픽셀 (5mm/25.4 X 320dpi) 갤럭시탭 9.7 , 10.1 도 같은 비율 60/2260
    dpi = SystemUtil.getDensityDpi(context);

    Log.d(TAG, "PointView: pointchanged_roundval = " + (int) getResources()
        .getDimension(R.dimen.pointchanged_radius));
    CIRCLE_RADIUS = SystemUtil
        .mmtoPixel(context, (int) getResources().getDimension(R.dimen.pointchanged_radius));
    mTextSize[mSelectedMaskingIdx] = SystemUtil.mmtoPixel(getContext(),
        getResources().getDimension(R.dimen.pointchanged_textsize));//비정형문서, 주민등록초본일경우
    if (Config.DEBUG) {
      Log.d(TAG, "### CIRCLE_RADIUS() " + CIRCLE_RADIUS);
    }

    mLayoutInfo = LayoutInfo.getInstance();
    mTopMargin = mLayoutInfo.getTopHeight() + mLayoutInfo.getBackTopHeight();
    mPreviewHeight = mLayoutInfo.getCornerBackHeight();
    mHorizontalMargin = mLayoutInfo.getHorizontalMargin();
    mVerticalMargin = mLayoutInfo.getVerticalMargin();

    mScreenInfoManager = ScreenInfoManager.getInstance();
//    mPreviewSize = mScreenInfoManager.getPreviewSize();
    mRealDisplaySize = mScreenInfoManager.getRealDisplaySize();
    mImageRotation = mScreenInfoManager.getImageRotation();
    WidthHeight pixels = mScreenInfoManager.getDisplayMetrics();
    mWidthPixels = pixels.getWidth();
    mHeightPixels = pixels.getHeight();


  }

  //===============================================================
  // Override methods
  //===============================================================
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mLoadImagePath != null) {
      if (mImageDrawable == null) {
        loadImageForOnDraw(mLoadImagePath, mTempBitmap, mSetAnchor);
        setMaskingPoint();
      }
      if (mImageDrawable != null) {
        mImageDrawable.draw(canvas);
        drawLineAndCircle(canvas);
        if ((isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind)) && mPointLT.x == -1000) {
          updateMasking(canvas);
        }
      }
    }
  }

  private void updateMasking(Canvas canvas) {
    float roundVal = GUIUtil.Dip2Pixel(getContext(), (int) getResources()
        .getDimension(R.dimen.pointchanged_roundval)); // 모서리의 둥근 정도 값이 클수록 모서리가 둥글다
    float textSize = SystemUtil.mmtoPixel(getContext(),
        getResources().getDimension(R.dimen.pointchanged_textsize));// 신분증,비정형 텍스트 사이즈
    if (AppConstants.DOC_KIND_AA.equals(mDocKind)) {
      textSize = SystemUtil.mmtoPixel(getContext(), getResources()
          .getDimension(R.dimen.pointchanged_id_confirm_textsize)); //주민등록발급신청서 텍스트 사이즈
    }
    float rangeH = mImageMaxY;
    float rangeW = mImageMaxX;
    MASKING_WIDTH = Math.round(SystemUtil.mmtoPixel(getContext(),
        (int) getResources().getDimension(R.dimen.pointchanged_id_masking_width))*0.8f);
    if (selectedIDCardIndex == 3 || selectedIDCardIndex == 4) {
      MASKING_WIDTH = Math.round(SystemUtil.mmtoPixel(getContext(),
          (int) getResources().getDimension(R.dimen.pointchanged_foreign_masking_width))*0.8f);
    }
    MASKING_HEIGHT = Math.round(SystemUtil.mmtoPixel(getContext(),
        (int) getResources().getDimension(R.dimen.pointchanged_id_masking_height))*0.9f);

    if (Config.DEBUG) {
      Log.d(TAG, "roundval : " + roundVal);
      Log.d(TAG, "theight= " + textSize);
      Log.d(TAG, "rangeW= " + rangeW + " rangeH= " + rangeH);
      Log.d(TAG, "MASKING_HEIGHT= " + MASKING_HEIGHT + " MASKING_WIDTH= " + MASKING_WIDTH);
    }
    if (NewMasking) {//새로 마스킹 생성
      if (mSelectedMaskingIdx > 0) {//사이즈 조절후 새로 생성시 같은 크기로
        mWidth[mSelectedMaskingIdx] = mWidth[mSelectedMaskingIdx - 1];
        mHeight[mSelectedMaskingIdx] = mHeight[mSelectedMaskingIdx - 1];
        mTextSize[mSelectedMaskingIdx] = mTextSize[mSelectedMaskingIdx - 1];
        mTextWidth[mSelectedMaskingIdx] = mTextWidth[mSelectedMaskingIdx - 1];
        mTextHeight[mSelectedMaskingIdx] = mTextHeight[mSelectedMaskingIdx - 1];
      } else {  //mSelectedMaskingIdx=0  //마스킹 0개에서 새로 추가시
        mWidth[mSelectedMaskingIdx] = 0;
        mHeight[mSelectedMaskingIdx] = 0;
        mTextSize[mSelectedMaskingIdx] = textSize;
        mTextWidth[mSelectedMaskingIdx] = 0;
        mTextHeight[mSelectedMaskingIdx] = 0;
      }
      if (Config.DEBUG) {
        Log.d(TAG, "NewMasking mSelectedMaskingIdx" + mSelectedMaskingIdx);
      }
    }

    // 마스킹 확대 축소
    switch (maskingSizeIndex) {
      case 1:
        int MaskingWidth = mMaskingPoint.get(mSelectedMaskingIdx).x + MASKING_WIDTH
            + mWidth[mSelectedMaskingIdx];//마스킹 가로 길이
        if (MaskingWidth < rangeW) {//이미지크기 만큼 범위제한
          mWidth[mSelectedMaskingIdx] += 5;
          mTextWidth[mSelectedMaskingIdx] += 3;
          if (mTextSize[mSelectedMaskingIdx] < textSize) {
            mTextSize[mSelectedMaskingIdx] += 1;
          }
        }
        break;
      case 2: //가로 축소
        if (mWidth[mSelectedMaskingIdx] <= 0) {
          if (mTextSize[mSelectedMaskingIdx] > 2) {
            mTextSize[mSelectedMaskingIdx] -= 1;
          }
        }
        mTextWidth[mSelectedMaskingIdx] -= 3;
        mWidth[mSelectedMaskingIdx] -= 5;
        if (MASKING_WIDTH + mWidth[mSelectedMaskingIdx] <= 0) {   //마스킹 완전히 안보일정도
          mMaskingPoint.remove(mSelectedMaskingIdx);
          DelMasking = true;
        }
        break;
      case 3://세로확대
        int MaskingHeight = mMaskingPoint.get(mSelectedMaskingIdx).y + MASKING_HEIGHT
            + mHeight[mSelectedMaskingIdx];//마스킹세로길이
        if (MaskingHeight < rangeH) {// 이미지크기 만큼 범위 제한
          mHeight[mSelectedMaskingIdx] += 4;
          if (mTextSize[mSelectedMaskingIdx] < textSize) {
            mTextSize[mSelectedMaskingIdx] += 1;
          }
          mTextHeight[mSelectedMaskingIdx] += 2;
        }
        break;
      case 4://세로축소
        if (mHeight[mSelectedMaskingIdx] <= 0) {
          if (mTextSize[mSelectedMaskingIdx] > 2) {
            mTextSize[mSelectedMaskingIdx] -= 1;
          }
        }
        mTextHeight[mSelectedMaskingIdx] -= 2;
        mHeight[mSelectedMaskingIdx] -= 4;
        if (MASKING_HEIGHT + mHeight[mSelectedMaskingIdx] <= 0) {  //마스킹 완전히 안보일정도
          mMaskingPoint.remove(mSelectedMaskingIdx);
          DelMasking = true;
        }

        break;
      default:
        break;
    }
    if (DelMasking) {
      if (mSelectedMaskingIdx != mMaskingPoint.size()) {//가장 마지막에 생성된것이 아닌 특정 마스킹 삭제경우
        for (int i = 0; i < mMaskingPoint.size(); i++) {
          if (i >= mSelectedMaskingIdx) {//선택한 마스킹 이상 인경우만 마스킹 스위칭
            mWidth[i] = mWidth[i + 1];
            mHeight[i] = mHeight[i + 1];
            mTextSize[i] = mTextSize[i + 1];
            mTextWidth[i] = mTextWidth[i + 1];
            mTextHeight[i] = mTextHeight[i + 1];
          }
        }
      }
      if (mSelectedMaskingIdx > 0) {//mSelectedMaskingIdx=0인경우는 0
        mSelectedMaskingIdx = mMaskingPoint.size() - 1;//삭제후 최근 마스킹순서
      }
      if (Config.DEBUG) {
        Log.d(TAG, "DelMasking mSelectedMaskingIdx" + mSelectedMaskingIdx);
      }
    }
    String maskingtext = getContext().getString(R.string.idcard_masking);//주민등록증 , 운전면허증
    if (selectedIDCardIndex == 3) {//외국인등록증앞면
      maskingtext = getContext().getString(R.string.idcard_masking2);
    } else if (selectedIDCardIndex == 4) {//국내거소면 앞면
      maskingtext = getContext().getString(R.string.idcard_masking3);
    }

    for (int i = 0; i < mMaskingPoint.size(); i++) {
      if (AppConstants.DOC_KIND_B.equals(mDocKind)) {//신분증일경우
        RectF rect = null;
        if (0 == i) {//신분증 큰 마스킹
          mCirclePaint.setColor(Color.BLACK);
          mCirclePaint.setStyle(Paint.Style.FILL);
          rect = new RectF(mMaskingPoint.get(i).x, mMaskingPoint.get(i).y,
              mMaskingPoint.get(i).x + MASKING_WIDTH + mWidth[i],
              mMaskingPoint.get(i).y + MASKING_HEIGHT + mHeight[i]);
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

          mCirclePaint.setColor(Color.WHITE);
          mTextSize[i] = textSize;
          Log.d(TAG, "big= " + mTextSize[i]);
          mCirclePaint.setTextSize(mTextSize[i]);
          float ycenter = mMaskingPoint.get(i).y + (MASKING_HEIGHT - mCirclePaint
              .descent() - mCirclePaint.ascent()) / 3;
          for (String ttxt : maskingtext.split("\n")) {
            float txtWidth = mCirclePaint.measureText(ttxt);
            canvas.drawText(ttxt,
                mMaskingPoint.get(i).x + (MASKING_WIDTH - txtWidth) / 2 + mTextWidth[i],
                ycenter + mTextHeight[i],
                mCirclePaint);
            ycenter -= (mCirclePaint.descent() + mCirclePaint.ascent()) * 3 / 2;
          }

        } else {//신분증 작은 마스킹(외국인등록증 , 국내거소증)
          mWidth[i] = -(MASKING_WIDTH / 2);
          mCirclePaint.setColor(Color.BLACK);
          mCirclePaint.setStyle(Paint.Style.FILL);
          rect = new RectF(mMaskingPoint.get(i).x, mMaskingPoint.get(i).y,
              mMaskingPoint.get(i).x + MASKING_WIDTH + mWidth[i],
              mMaskingPoint.get(i).y + MASKING_HEIGHT + mHeight[i]);
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

          mCirclePaint.setColor(Color.WHITE);
          mTextSize[i] = SystemUtil.mmtoPixel(getContext(),
              getResources().getDimension(R.dimen.pointchanged_foreign_textsize));
          Log.d(TAG, "small= " + mTextSize[i]);
          mCirclePaint.setTextSize(mTextSize[i]);
          float ycenter = mMaskingPoint.get(i).y + (MASKING_HEIGHT - mCirclePaint
              .descent() - mCirclePaint.ascent()) / 3;
          for (String ttxt : maskingtext.split("\n")) {
            float txtWidth = mCirclePaint.measureText(ttxt);
            canvas.drawText(ttxt,
                mMaskingPoint.get(i).x + ((MASKING_WIDTH / 2) - txtWidth) / 2
                    + mTextWidth[i], ycenter + mTextHeight[i],
                mCirclePaint);
            ycenter -= (mCirclePaint.descent() + mCirclePaint.ascent()) * 3 / 2;
          }

        }
        if (i == mSelectedMaskingIdx) {
          mCirclePaint
              .setColor(getResources().getColor(R.color.act_native_camera_preview_vertex));
          mCirclePaint.setStyle(Paint.Style.STROKE);//외곽선
          mCirclePaint.setStrokeWidth(
              DEFAULT_MASKING_STROKE_WIDTH * SystemUtil.getDensityRate(getContext()));
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);
        }
      } else if (AppConstants.DOC_KIND_AA.equals(mDocKind)) { // 주민등록증 발급신청 확인서
        if (i == mSelectedMaskingIdx) {// i==mSelectedMaskingIdx 선택된 마스킹
          mWidth[i] = -(MASKING_WIDTH / 3);
          mHeight[i] = -(int) (MASKING_HEIGHT / 2.5);

          mCirclePaint.setStyle(Paint.Style.FILL);
          mCirclePaint.setColor(Color.BLACK);
          float l = mMaskingPoint.get(mSelectedMaskingIdx).x;
          float t = mMaskingPoint.get(mSelectedMaskingIdx).y;
          float r = mMaskingPoint.get(mSelectedMaskingIdx).x + MASKING_WIDTH
              + mWidth[mSelectedMaskingIdx];
          float b = mMaskingPoint.get(mSelectedMaskingIdx).y + MASKING_HEIGHT
              + mHeight[mSelectedMaskingIdx];
          RectF rect = new RectF(l, t, r, b);
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

          mCirclePaint.setColor(Color.WHITE);
          mTextSize[mSelectedMaskingIdx] = textSize;
          mCirclePaint.setTextSize(mTextSize[mSelectedMaskingIdx]);
          float ycenter = mMaskingPoint.get(mSelectedMaskingIdx).y
              + (MASKING_HEIGHT - (int) (MASKING_HEIGHT / 2.5) - mCirclePaint
              .descent() - mCirclePaint.ascent()) / 3;
          //ascent : baseline 위로의 크기를 리턴.descent() : baseline 밑으로의 크기를 리턴. descent+ascent= 문자의높이
          for (String ttxt : maskingtext.split("\n")) {
            float txtWidth = mCirclePaint
                .measureText(ttxt);  //파라미터로 전달된 글자의 크기를 리턴 . 문자의 가로폭
            canvas.drawText(ttxt, mMaskingPoint.get(mSelectedMaskingIdx).x
                    + (MASKING_WIDTH - (MASKING_WIDTH / 3) - txtWidth) / 2
                    + mTextWidth[mSelectedMaskingIdx],
                ycenter + mTextHeight[mSelectedMaskingIdx],
                mCirclePaint);
            ycenter -= (mCirclePaint.descent() + mCirclePaint.ascent()) * 3 / 2;
          }

          mCirclePaint
              .setColor(getResources().getColor(R.color.act_native_camera_preview_vertex));
          mCirclePaint.setStyle(Paint.Style.STROKE);//외곽선
          mCirclePaint.setStrokeWidth(
              DEFAULT_MASKING_STROKE_WIDTH * SystemUtil.getDensityRate(getContext()));
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

        }
      } else {//비정형마스킹  주민등록초본
        if (i != mSelectedMaskingIdx) {
          mCirclePaint.setColor(Color.BLACK);
          mCirclePaint.setStyle(Paint.Style.FILL);
          RectF rect = new RectF(mMaskingPoint.get(i).x, mMaskingPoint.get(i).y,
              mMaskingPoint.get(i).x + MASKING_WIDTH + mWidth[i],
              mMaskingPoint.get(i).y + MASKING_HEIGHT + mHeight[i]);
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

          mCirclePaint.setColor(Color.WHITE);
          mCirclePaint.setTextSize(mTextSize[i]);
          float ycenter = mMaskingPoint.get(i).y + (MASKING_HEIGHT - mCirclePaint
              .descent() - mCirclePaint.ascent()) / 3;
          for (String ttxt : maskingtext.split("\n")) {
            float txtWidth = mCirclePaint.measureText(ttxt);
            canvas.drawText(ttxt,
                mMaskingPoint.get(i).x + (MASKING_WIDTH - txtWidth) / 2 + mTextWidth[i],
                ycenter + mTextHeight[i],
                mCirclePaint);
            ycenter -= (mCirclePaint.descent() + mCirclePaint.ascent()) * 3 / 2;
          }

        }
        if (i == mSelectedMaskingIdx) {// i==mSelectedMaskingIdx 선택된 마스킹
          mCirclePaint.setStyle(Paint.Style.FILL);
          mCirclePaint.setColor(Color.BLACK);
          RectF rect = new RectF(mMaskingPoint.get(mSelectedMaskingIdx).x,
              mMaskingPoint.get(mSelectedMaskingIdx).y,
              mMaskingPoint.get(mSelectedMaskingIdx).x + MASKING_WIDTH
                  + mWidth[mSelectedMaskingIdx],
              mMaskingPoint.get(mSelectedMaskingIdx).y + MASKING_HEIGHT
                  + mHeight[mSelectedMaskingIdx]);
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

          mCirclePaint.setColor(Color.WHITE);
          mCirclePaint.setTextSize(mTextSize[mSelectedMaskingIdx]);
          float ycenter =
              mMaskingPoint.get(mSelectedMaskingIdx).y + (MASKING_HEIGHT - mCirclePaint
                  .descent() - mCirclePaint.ascent()) / 3;
          //ascent : baseline 위로의 크기를 리턴.descent() : baseline 밑으로의 크기를 리턴. descent+ascent= 문자의높이
          for (String ttxt : maskingtext.split("\n")) {
            float txtWidth = mCirclePaint
                .measureText(ttxt);  //파라미터로 전달된 글자의 크기를 리턴 . 문자의 가로폭
            canvas.drawText(ttxt,
                mMaskingPoint.get(mSelectedMaskingIdx).x + (MASKING_WIDTH - txtWidth) / 2
                    + mTextWidth[mSelectedMaskingIdx],
                ycenter + mTextHeight[mSelectedMaskingIdx],
                mCirclePaint);
            ycenter -= (mCirclePaint.descent() + mCirclePaint.ascent()) * 3 / 2;
          }

          mCirclePaint
              .setColor(getResources().getColor(R.color.act_native_camera_preview_vertex));
          mCirclePaint.setStyle(Paint.Style.STROKE);//외곽선
          mCirclePaint.setStrokeWidth(
              DEFAULT_MASKING_STROKE_WIDTH * SystemUtil.getDensityRate(getContext()));
          canvas.drawRoundRect(rect, roundVal, roundVal, mCirclePaint);

        }
      }
    }//for 문 END
    maskingSizeIndex = 0;
    NewMasking = false;
    DelMasking = false;
  }

  /**
   * saves a bitmap image into specific file.
   */
  private void saveBitmap(Bitmap bitmap) {
    String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vzon";
    File dir = new File(rootPath);
    if (!dir.exists()) {
      dir.mkdir();
    }
    File file = new File(rootPath + "/sample.txt");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      FileOutputStream fos = new FileOutputStream(file);
      bitmap.compress(CompressFormat.JPEG, 90, fos);
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();
    int topMargin = PointChangedActivity.mBackTopView.getLayoutParams().height;//이미지 아래  검은화면
    int bottomMargin = PointChangedActivity.mBackBottomView.getLayoutParams().height;//이미지 아래  검은화면
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mIsTouch = true;
        mIsZooming = true;
        mTouchX = x;
        mTouchY = y;
        mTouchRemainderX = 0;   // 초기화
        mTouchRemainderY = 0;   // 초기화
        getNearestCirclePoint(x, y);
        if ((isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
            .equals(mSelectedFormId)) && mPointLT.x == -1000) {  // #1341
          for (int i = mMaskingPoint.size() - 1; i >= 0;
              i--) {   //마스킹 이 같은곳에 2개이상 있을경우 맨 마지막에 생성된것이 선택되게
            if ((mMaskingPoint.get(i).x < mTouchX
                && mTouchX < mMaskingPoint.get(i).x + MASKING_WIDTH + mWidth[i]) &&  //마스킹 크기 가로 만큼
                (mMaskingPoint.get(i).y < mTouchY && mTouchY
                    < mMaskingPoint.get(i).y + MASKING_HEIGHT + mHeight[i])) {    //마스킹 세로 크기만큼
              mMoveMask = true;
              mSelectedMaskingIdx = i;//마스킹 개수  0번 부터
              if (Config.DEBUG) {
                Log.d(TAG, "### mSelectedMaskingIdx " + mSelectedMaskingIdx);
              }
              break;
            }
          }
        }

        break;
      case MotionEvent.ACTION_MOVE:
        mIsZooming = true;
        int dx = (int) (x - mTouchX);
        int dy = (int) (y - mTouchY);
        int touchRemainderX, touchRemainderY;   // 반영되지 않은 이동값 중 좌표에 반영해 줄 값
        mTouchRemainderX += x - mTouchX - dx;   // 이동한 값 중 반영되지 않는 값을 추가 한다
        mTouchRemainderY += y - mTouchY - dy;   // 이동한 값 중 반영되지 않는 값을 추가 한다
        touchRemainderX = (int) mTouchRemainderX;
        touchRemainderY = (int) mTouchRemainderY;
        mTouchRemainderX -= touchRemainderX;    // 반영되지 않은 이동값에 반영할 이동값을 제한다
        mTouchRemainderY -= touchRemainderY;    // 반영되지 않은 이동값에 반영할 이동값을 제한다

        if (mTouchCircleIndex != NOT_DEFINE) {
          if (mTouchCircleIndex == 1) {  // 상단 좌측
            int px = 0, py = 0;
            if (mPointLT.x + (int) (x - mTouchX) + touchRemainderX
                < mMeasuredWidth / 2) {   // 터치 영역이 MAX값 보다 작은 경우
              if (mPointLT.x + (int) (x - mTouchX) + touchRemainderX
                  > mImageMinX) { // 터치로 이동한 영역이 MIN값 보다 큰 경우
                px = mPointLT.x + (int) (x - mTouchX) + touchRemainderX;
              } else {    // 터치 영역이 MIN값 보다 작은 경우 MIN값 처리
                px = mImageMinX + 1;
              }
            } else {    // 터치로 이동한 영역이 제한 범위내에 없는 경우(MAX값 보다 큰 경우) MAX값으로 처리
              px = mMeasuredWidth / 2 - 1;
            }
            if (mPointLT.y + (int) (y - mTouchY) + touchRemainderY < mMeasuredHeight / 2) {
              if (mPointLT.y + (int) (y - mTouchY) + touchRemainderY > mImageMinY + (
                  isBlackBackground ? topMargin : 0)) {
                py = mPointLT.y + (int) (y - mTouchY) + touchRemainderY;
              } else {
                py = mImageMinY + (isBlackBackground ? topMargin : 0) + 1;
              }
            } else {
              py = mMeasuredHeight / 2 - 1;
            }

            mPointLT.set(px, py);
            mTargetCircleX = mPointLT.x;
            mTargetCircleY = mPointLT.y;
          }
          if (mTouchCircleIndex == 2) {  //상단 우측
            int px = 0, py = 0;
            if (mPointRT.x + (int) (x - mTouchX) + touchRemainderX > mMeasuredWidth / 2) {
              if (mPointRT.x + (int) (x - mTouchX) + touchRemainderX < mImageMaxX) {
                px = mPointRT.x + (int) (x - mTouchX) + touchRemainderX;
              } else {
                px = mImageMaxX - CORRECTION_POINT_TOP_X;
              }
            } else {
              px = mMeasuredWidth / 2 + 1;
            }
            if (mPointRT.y + (int) (y - mTouchY) + touchRemainderY < mMeasuredHeight / 2) {
              if (mPointRT.y + (int) (y - mTouchY) + touchRemainderY > mImageMinY + (
                  isBlackBackground ? topMargin : 0)) {
                py = mPointRT.y + (int) (y - mTouchY) + touchRemainderY;
              } else {
                py = mImageMinY + (isBlackBackground ? topMargin : 0) + CORRECTION_POINT_TOP_Y
                    + 1; // 시작 포인트는 1부터
              }
            } else {
              py = mMeasuredHeight / 2 - 1;
            }
            if (mPointRB.x == px) { // 좌표가 같으면 이미지가 비정상적으로 출력되는 경우가 있음
              px -= 1;
            }
            mPointRT.set(px, py);
            mTargetCircleX = mPointRT.x;
            mTargetCircleY = mPointRT.y;
          }

          if (mTouchCircleIndex == 0) {  // 좌측 하단
            int px = 0, py = 0;
            if (mPointLB.x + (int) (x - mTouchX) + touchRemainderX < mMeasuredWidth / 2) {
              if (mPointLB.x + (int) (x - mTouchX) + touchRemainderX > mImageMinX) {
                px = mPointLB.x + (int) (x - mTouchX) + touchRemainderX;
              } else {
                px = mImageMinX + 1;    // 시작 포인트는 1부터
              }
            } else {
              px = mMeasuredWidth / 2 - 1;
            }
            if (mPointLB.y + (int) (y - mTouchY) + touchRemainderY > mMeasuredHeight / 2) {
              if (mPointLB.y + (int) (y - mTouchY) + touchRemainderY < mImageMaxY - (
                  isBlackBackground ? bottomMargin : 0)) {
                py = mPointLB.y + (int) (y - mTouchY) + touchRemainderY;
              } else {
                py = mImageMaxY - (isBlackBackground ? bottomMargin : 0);
              }
            } else {
              py = mMeasuredHeight / 2 + 1;
            }
            if (mPointRB.y == py) { // 좌표가 같으면 이미지가 비정상적으로 출력되는 경우가 있음
              py -= 1;
            }
            mPointLB.set(px, py);
            mTargetCircleX = mPointLB.x;
            mTargetCircleY = mPointLB.y;
          }
          if (mTouchCircleIndex == 3) {  // 우측 하단
            int px = 0, py = 0;
            if (mPointRB.x + (int) (x - mTouchX) + touchRemainderX > mMeasuredWidth / 2) {
              if (mPointRB.x + (int) (x - mTouchX) + touchRemainderX < mImageMaxX) {
                px = mPointRB.x + (int) (x - mTouchX) + touchRemainderX;
              } else {
                px = mImageMaxX;
              }
            } else {
              px = mMeasuredWidth / 2 + 1;
            }
            if (mPointRB.y + (int) (y - mTouchY) + touchRemainderY > mMeasuredHeight / 2) { //하단 우측
              if (mPointRB.y + (int) (y - mTouchY) + touchRemainderY < mImageMaxY - (
                  isBlackBackground ? bottomMargin : 0)) {
                py = mPointRB.y + (int) (y - mTouchY) + touchRemainderY;
              } else {
                py = mImageMaxY - (isBlackBackground ? bottomMargin : 0);
              }
            } else {
              py = mMeasuredHeight / 2 + 1;
            }
            if (mPointLB.y == py) { // 좌표가 같으면 이미지가 비정상적으로 출력되는 경우가 있음
              py -= 1;
            }
            if (mPointRT.x == px) { // 좌표가 같으면 이미지가 비정상적으로 출력되는 경우가 있음
              px -= 1;
            }
            mPointRB.set(px, py);
            mTargetCircleX = mPointRB.x;
            mTargetCircleY = mPointRB.y;
          }
          mTouchX = x;
          mTouchY = y;
          isTouched = true;
          //invalidate();
          //}
        }

        if ((isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
            .equals(mSelectedFormId)) && mMoveMask) { // #1341
          int halfWidth = (int) ((AppConstants.PAPER_SIZE_MIN
              - AppConstants.PAPER_SIZE_MIN * AppConstants.ID_CARD_SIZE_MAX / AppConstants.ID_CARD_SIZE_MIN/*신분증인 경우 신분증 비율로 맞춘 값*/ / 2) * mZoomRate
              /2); //#1216
          int halHeight = (int) (
              (AppConstants.PAPER_SIZE_MAX - AppConstants.PAPER_SIZE_MIN / 2) * mZoomRate
                  / 2); //#1216

          if (AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
              .equals(mSelectedFormId)
              || AppConstants.DOC_MASKING_C.equals(mSelMasking)) { // #1341
            halfWidth = 0;
            halHeight = 0;
          }
          halfWidth = 0;
          halHeight = 0;
          int px = 0, py = 0;
          if (mMaskingPoint.get(mSelectedMaskingIdx).x + (int) (x - mTouchX) + touchRemainderX
              > mImageMinX + halfWidth &&
              mMaskingPoint.get(mSelectedMaskingIdx).x + (int) (x - mTouchX) + touchRemainderX
                  < mImageMaxX - MASKING_WIDTH - mWidth[mSelectedMaskingIdx] - halfWidth) {
            px = mMaskingPoint.get(mSelectedMaskingIdx).x + (int) (x - mTouchX) + touchRemainderX;
          } else if (
              mMaskingPoint.get(mSelectedMaskingIdx).x + (int) (x - mTouchX) + touchRemainderX
                  <= mImageMinX + halfWidth) {
            px = mImageMinX + halfWidth;
          } else {
            px = mImageMaxX - MASKING_WIDTH - halfWidth - mWidth[mSelectedMaskingIdx];
          }

          if (mMaskingPoint.get(mSelectedMaskingIdx).y + (int) (y - mTouchY) + touchRemainderY
              > mImageMinY + halHeight &&
              mMaskingPoint.get(mSelectedMaskingIdx).y + (int) (y - mTouchY) + touchRemainderY
                  < mImageMaxY - MASKING_HEIGHT - mHeight[mSelectedMaskingIdx] - halHeight) {
            py = mMaskingPoint.get(mSelectedMaskingIdx).y + (int) (y - mTouchY) + touchRemainderY;
          } else if (
              mMaskingPoint.get(mSelectedMaskingIdx).y + (int) (y - mTouchY) + touchRemainderY
                  <= mImageMinY + halHeight) {
            py = mImageMinY + halHeight;
          } else {
            py = mImageMaxY - MASKING_HEIGHT - halHeight - mHeight[mSelectedMaskingIdx];
          }

          Point pt = new Point(px, py);
          mMaskingPoint.set(mSelectedMaskingIdx, pt);
          retMaskingPoint.set(mSelectedMaskingIdx, pt);
          mTouchX = x;
          mTouchY = y;
        }
        break;


      case MotionEvent.ACTION_UP:
        if (mSetAnchor && !mMoveMask && mTouchCircleIndex == NOT_DEFINE) {
          isTouched = true;
        }
        mMoveMask = false;

        Point LT = new Point();
        Point RT = new Point();
        Point LB = new Point();
        Point RB = new Point();
        if (mPointLT.x > -1000) { //shlee 마스킹하는 화면과 네포인트를 이동시키는 화면이 달라서 추가한 코드임

          LT.x = mPointLT.x;
          LT.y = mPointLT.y;
          RT.x = mPointRT.x;
          RT.y = mPointRT.y;
          LB.x = mPointLB.x;
          LB.y = mPointLB.y;
          RB.x = mPointRB.x;
          RB.y = mPointRB.y;

          //shlee 사각 점을 이동시켰을 때 위치를 기억함
          Point[] pt = {LT, RT, LB, RB};
          retPoint = pt;
        }
        if (isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
            .equals(mSelectedFormId)) { //마스킹  #1341
          if (mPointLT.x == -1000) {  //shlee 마스킹하는 화면과 네포인트를 이동시키는 화면이 달라서 추가한 코드임
            LT = retPoint[0];
            RT = retPoint[1];
            LB = retPoint[2];
            RB = retPoint[3];
            mListener.pointChanged(mZoomRate, LT, RT, LB, RB, mMaskingPoint, mImageBitmap,
                -1); // mFindCornerPoints : -1(N/A), 0(false), 1(true)
          }
        } else {
          mListener.pointChanged(mZoomRate, LT, RT, LB, RB, mImageBitmap,
              -1); // mFindCornerPoints : -1(N/A), 0(false), 1(true)
        }
      case MotionEvent.ACTION_CANCEL:
        mIsZooming = false;
        mIsTouch = false;
        mTargetCircleX = NOT_DEFINE;
        mTargetCircleY = NOT_DEFINE;
        break;
      default:
        break;
    }

//    Point touchPoints = new Point((int) mTargetCircleX, (int) mTargetCircleY);
//    mZoomImgListener.onChangeZoomPoint(event.getAction(), touchPoints);

    invalidate();
    return true;
  }

  private void getNearestCirclePoint(float x, float y) {

    Point nearestP = null;
    int minIndex, minVal;
    minIndex = minVal = AppConstants.NOT_DEFINED;

    for (int i = 0; i < mCornerPoints.size(); i++) {
      int distance = (int) (Math.pow(mCornerPoints.get(i).x - x, 2) + Math
          .pow(mCornerPoints.get(i).y - y, 2));
      if (distance < Math.pow(TOUCH_AREA, 2)) {
        if (nearestP == null || distance < minVal) {
          nearestP = mCornerPoints.get(i);
          minVal = distance;
          minIndex = i;
        }
      }
    }
    if (nearestP != null) {
      mTargetCircleX = nearestP.x;
      mTargetCircleY = nearestP.y;
    }
    mTouchCircleIndex = minIndex;
  }

  //===============================================================
  // public methods
  //===============================================================
  public void onDestroy() {
    mScanInfo = null;
    retPoint = null;
    retMaskingPoint.clear();
    isTouched = false;
  }

  public void setImageProcessListener(ImageProcessListener listener) {
    mImageProcessListener = listener;
  }

  public void setZoomImageListener(ZoomImageListener listener) {
    mZoomImgListener = listener;
  }

  public void loadImageForImageProcessing(String filePath, String colorSelection) {
    freeImage();
    mLoadImagePath = filePath;
    mSetAnchor = true;
    mColorSelection = colorSelection;
    if (Config.DEBUG) {
      Log.d(TAG, "### loadImageForImageProcessing() : before invalidate() 935");
    }
    invalidate();
    if (Config.DEBUG) {
      Log.d(TAG, "### loadImageForImageProcessing() : after invalidate() 939");
    }
  }

  public void loadImageForImageSaving(Bitmap bitmap, String colorSelection) {
    if (Config.DEBUG) {
      Log.d(TAG,
          "### loadImageForImageSaving() : bitmap[" + bitmap + "] colorSelection[" + colorSelection
              + "]");
    }

    freeImage();
    mTempBitmap = bitmap;
    mSetAnchor = false;
    mColorSelection = colorSelection;
    if (Config.DEBUG) {
      Log.d(TAG, "### loadImageForImageSaving() : before invalidate() 953");
    }
    invalidate();
    if (Config.DEBUG) {
      Log.d(TAG, "### loadImageForImageSaving() : after invalidate() 957");
    }
  }

  public void freeImage() {
    if (mImageBitmap != null && !mImageBitmap.isRecycled()) {
      mImageBitmap.recycle();
      mImageBitmap = null;
    }
    if (mImageDrawable != null) {
      mImageDrawable = null;
    }
  }

  public void setDocKind(String docKind, String colorSelection, String docMasking) {
    if (Config.DEBUG) {
      Log.d(TAG, "### setDocKind=" + docKind);
    }
    if (AppConstants.DOC_KIND_B.equals(docKind) || AppConstants.DOC_KIND_AA.equals(docKind) || (
        AppConstants.DOC_KIND_A.equals(docKind) && AppConstants.DOC_MASKING_E
            .equals(docMasking))) { //신분증 및 주민등록발급신청서 및 주민등록초본
      /*mMaskingPoint.add(new Point(-1000, -1000)); // 기본 하나의 마스킹 추가
      retMaskingPoint = mMaskingPoint;*/
    }
    mDocKind = docKind;
    mColorSelection = colorSelection;
    setIsBlackBackground(mDocKind, mIsImageFromGallery);
    mSelMasking = docMasking;

    if (AppConstants.DOC_MASKING_E.equals(mSelMasking) || AppConstants.DOC_MASKING_C
        .equals(mSelMasking)) {
      isMasking = true;
    }
  }

  /**
   * 문서 종류 신분증(DOC_KIND_B)일 경우,갤러리에서 가져온 이미지가 아닐 경우만 상,하단 검은 bg 이미지 보여짐
   *
   * @param docKind : 문서 종류
   * @param fromGallery : 이미지 출처 갤러리 여부
   */
  private void setIsBlackBackground(String docKind, boolean fromGallery) {
    isBlackBackground = AppConstants.DOC_KIND_B.equals(mDocKind) && !fromGallery;
  }

  public boolean getMasking() {
    return isMasking;
  }

  public void setImageRotation(int rotation){
    mImageRotation = rotation;
  }

  /**
   * <pre>
   * mm로 계산되어져 있는(주민등록증 발급신청 확인서) 마스킹 좌표 계산
   * 마스킹 좌표 : 이미지 영역의 margin + 이미지 영역내에서의 좌표(마스킹 비율 * 이미지 크기)
   * 이미지 영역의 margin x : mImageDrawable.getBounds().left
   *                      y : mImageDrawable.getBounds().top
   * 마스킹 비율 x : 마스킹 포인트 x / 210 mm
   *             y : 마스킹 포인트 y / 297 mm
   * 마스킹 좌표 : margin + 계산된 마스킹 위치
   * </pre>
   */
  public Point getCalculatedMaskingPoint(Point point) {
    if (mImageDrawable == null || mImageDrawable.getBounds() == null) {
      return null;
    }

    Point maskingPoint = new Point();

    int marginX = mImageDrawable.getBounds().left;
    int marginY = mImageDrawable.getBounds().top;
    int drawWidth = mImageDrawable.getBounds().right - mImageDrawable.getBounds().left;
    int drawHeight = mImageDrawable.getBounds().bottom - mImageDrawable.getBounds().top;
    float maskingRateX = (float) point.x / 210;
    float maskingRateY = (float) point.y / 297;
    maskingPoint.x = (int) (drawWidth * maskingRateX);
    maskingPoint.y = (int) (drawHeight * maskingRateY);
    maskingPoint.x += marginX;
    maskingPoint.y += marginY;

    return maskingPoint;
  }

  /**
   * <pre>
   * mm로 계산되어져 있는 신분증 마스킹 좌표 계산(ImageProcessing소스 참고)
   * 마스킹 좌표 : 전체 이미지 영역의 margin + 이미지 영역의 시작 위치 + 이미지 영역내에서의 좌표(마스킹 비율 * 이미지 크기)
   * 전체 이미지 영역의 margin x : mImageDrawable.getBounds().left
   *                           y : mImageDrawable.getBounds().top
   * 이미지 영역 w : AppConstants.PAPER_SIZE_MIN * 856 / 540 / 2 = 1309
   *             h : AppConstants.PAPER_SIZE_MIN / 2 = 826
   * 이미지 영역의 시작 좌표 x : AppConstants.PAPER_SIZE_MIN - tempBitmap.getHeight() / 2) / 2 = 171
   *                         y : AppConstants.PAPER_SIZE_MAX - PICTURE_WIDTH) / 2 = 755
   * 마스킹 비율 x : 마스킹 포인트 x / 85.6 mm
   *             y : 마스킹 포인트 y / 54.0 mm
   * 마스킹 좌표 : margin + 계산된 이미지 영역의 시작 좌표 + 계산된 마스킹 위치
   * </pre>
   *
   * @see ImageProcessing#createBitmapFromOverlap(int[], int, int, boolean)
   */
  public Point getCalculatedMaskingPointForId(Point point) {
    if (mImageDrawable == null || mImageDrawable.getBounds() == null) {
      return null;
    }

    Point maskingPoint = new Point();

    int marginX = mImageDrawable.getBounds().left;
    int marginY = mImageDrawable.getBounds().top;
    int drawWidth = mImageDrawable.getBounds().right - mImageDrawable.getBounds().left;
    int drawHeight = mImageDrawable.getBounds().bottom - mImageDrawable.getBounds().top;
    int drawAreaWidth = drawWidth * 1309 / AppConstants.PAPER_SIZE_MIN;
    int drawAreaHeight = drawHeight * 826 / AppConstants.PAPER_SIZE_MAX;
    float maskingRateX = (float) point.x / 85.6f;
    float maskingRateY = (float) point.y / 54.0f;
    maskingPoint.x = (int) (drawAreaWidth * maskingRateX);
    maskingPoint.y = (int) (drawAreaHeight * maskingRateY);
    int pictureLocationX = (int) ((171f / AppConstants.PAPER_SIZE_MIN) * drawWidth);
    int pictureLocationY = (int) ((755f / AppConstants.PAPER_SIZE_MAX) * drawHeight);
    maskingPoint.x += marginX + pictureLocationX;
    maskingPoint.y += marginY + pictureLocationY;

    return maskingPoint;
  }

  /**
   * <pre>
   * mm로 계산되어져 있는 여권 마스킹 좌표 계산(ImageProcessing소스 참고) - 추후 필요시 참고
   * 마스킹 좌표 : 전체 이미지 영역의 margin + 이미지 영역의 시작 위치 + 이미지 영역내에서의 좌표(마스킹 비율 * 이미지 크기)
   * 전체 이미지 영역의 margin x : mImageDrawable.getBounds().left
   *                           y : mImageDrawable.getBounds().top
   * 이미지 영역 w : AppConstants.PAPER_SIZE_MIN / 2 = 826
   *             h : AppConstants.PAPER_SIZE_MAX / 2 = 1168
   * 이미지 영역의 시작 좌표 x : (AppConstants.PAPER_SIZE_MIN - AppConstants.PAPER_SIZE_MIN/2) / 2 = 413
   *                         y : (AppConstants.PAPER_SIZE_MAX - AppConstants.PAPER_SIZE_MAX/2) / 2 = 584
   * 마스킹 비율 x : 마스킹 포인트 x / 125 mm
   *             y : 마스킹 포인트 y / 88 * 2 mm (이미지 영역이 여권 양면을 찍어서 처리하도록 되어 있음)
   *
   * 마스킹 좌표 : margin + 계산된 이미지 영역의 시작 좌표 + 계산된 마스킹 위치
   * </pre>
   *
   * @see ImageProcessing#createBitmapFromOverlap(int[], int, int, boolean)
   */
  public Point getCalculatedMaskingPointForPassport(Point point) {
    if (mImageDrawable == null || mImageDrawable.getBounds() == null) {
      return null;
    }

    Point maskingPoint = new Point();

    int marginX = mImageDrawable.getBounds().left;
    int marginY = mImageDrawable.getBounds().top;
    int drawWidth = mImageDrawable.getBounds().right - mImageDrawable.getBounds().left;
    int drawHeight = mImageDrawable.getBounds().bottom - mImageDrawable.getBounds().top;
    int drawAreaWidth = drawWidth * 826 / AppConstants.PAPER_SIZE_MIN;
    int drawAreaHeight = drawHeight * 1168 / AppConstants.PAPER_SIZE_MAX;
    float maskingRateX = (float) point.x / 125f;
    float maskingRateY = (float) point.y / 176f;
    maskingPoint.x = (int) (drawAreaWidth * maskingRateX);
    maskingPoint.y = (int) (drawAreaHeight * maskingRateY);
    int pictureLocationX = (int) ((413f / AppConstants.PAPER_SIZE_MIN) * drawWidth);
    int pictureLocationY = (int) ((584f / AppConstants.PAPER_SIZE_MAX) * drawHeight);
    maskingPoint.x += marginX + pictureLocationX;
    maskingPoint.y += marginY + pictureLocationY;

    return maskingPoint;
  }

  /**
   * 포인트 영역을 보여주는 설정
   *
   * @param isVisible true : 보여줌, false : 안보여줌
   */
  public void setIsVisiblePoint(boolean isVisible) {
    mIsVisiblePoint = isVisible;
  }

  public void reset() {
    mIsRotated = false;
    mScreenInfoManager.setImageRotation(0);
  }

  public void setIsRotated(boolean bRotated) {
    mIsRotated = bRotated;
    mImageRotation = 0;
  }

  public void setIsFromGallery(boolean bFromGallery) {
    mIsImageFromGallery = bFromGallery;
    setIsBlackBackground(mDocKind, mIsImageFromGallery);
  }

  public void setMaskingSizeIndex(int index) {
    this.maskingSizeIndex = index;
  }

  public void onRotateImage(int degree){
//    mToDegree= degree;
//    freeImage();
//    loadImageForOnDraw(mLoadImagePath, mTempBitmap, mSetAnchor);
      if(mImageBitmap!=null)rotateImage(mImageBitmap, degree);
      mRotateListener.onRotateComplete(new WidthHeight(mImageBitmap.getWidth(),mImageBitmap.getHeight()));
      invalidate();

  }

  //===============================================================
  // private methods
  //===============================================================
  private void init() {
    mZoomPos = new PointF(0, 0);
    mMatrix = new Matrix();
    mZoomingPaint = new Paint();
    mCornerPoints.add(mPointLB);
    mCornerPoints.add(mPointLT);
    mCornerPoints.add(mPointRT);
    mCornerPoints.add(mPointRB);
  }

  private void loadImageForOnDraw(String filePath, Bitmap bitmap, boolean isSetAnchor) {
    try {
      setMeasuredWH();
      Log.d(TAG, "loadImageForOnDraw: filePath  = " + filePath + " ,bitmap = " + bitmap);
      if (bitmap == null || bitmap.isRecycled()) {
        mImageBitmap = BitmapFactory.decodeFile(filePath);
      } else {
        mImageBitmap = bitmap;//BitmapFactory.decodeFile(filePath);
      }


      mImageDrawable = new BitmapDrawable(this.getContext().getResources(), mImageBitmap);
      if (mImageBitmap == null) return;

      mImageWidth = mImageBitmap.getWidth();
      mImageHeight = mImageBitmap.getHeight();
      mDiffHeight = mRealDisplaySize.getHeight() - mHeightPixels;
      getImageMinMaxXY();

      if (Config.DEBUG) {
        Log.d(TAG,
            "### loadImageForOnDraw() : mZoomRate[" + mZoomRate + "] mImageMinX[" + mImageMinX
                + "] mImageMinY[" + mImageMinY + "] mImageMaxX[" + mImageMaxX + "] mImageMaxY["
                + mImageMaxY + "]");
      }

      if (isSetAnchor) {
        setAnchor();
      } else {
        initMaskingPoint();
      }

      if (isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
          .equals(mSelectedFormId)) {  //마스킹 // #1341
        mListener.pointChanged(mZoomRate, mPointLT, mPointRT, mPointLB, mPointRB, mMaskingPoint,
            mImageBitmap, 0); // mFindCornerPoints : -1(N/A), 0(false), 1(true)
      } else {
        mListener.pointChanged(mZoomRate, mPointLT, mPointRT, mPointLB, mPointRB, mImageBitmap,
            0); // mFindCornerPoints : -1(N/A), 0(false), 1(true)
      }
      //-----------------------------------------------------
      // 이미지 영역 지정
      //-----------------------------------------------------
      if (mImageDrawable != null) {
        mImageDrawable.setBounds(mImageMinX, mImageMinY, mImageMaxX, mImageMaxY);
      }
      invalidate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initMaskingPoint() {
    mFindCornerPoints = 0; // mFindCornerPoints : -1(N/A), 0(false), 1(true)
    mPointLT.set(-1000, -1000);
    mPointRT.set(-1000, -1000);
    mPointLB.set(-1000, -1000);
    mPointRB.set(-1000, -1000);

    if (isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
        .equals(mSelectedFormId)) { // #1341
      Point maskPt;
      if (mMaskingPoint.size() > 0) {
        if (retMaskingPoint == null) {
          mMaskingPoint.set(mSelectedMaskingIdx, new Point((mMeasuredWidth - MASKING_WIDTH) / 2,
              (mMeasuredHeight - MASKING_HEIGHT) / 2));
          maskPt = new Point((mMeasuredWidth - MASKING_WIDTH) / 2,
              (mMeasuredHeight - MASKING_HEIGHT) / 2);
          retMaskingPoint.set(mSelectedMaskingIdx, maskPt);
        } else {
          maskPt = new Point(retMaskingPoint.get(mSelectedMaskingIdx).x,
              retMaskingPoint.get(mSelectedMaskingIdx).y);
          mMaskingPoint.set(mSelectedMaskingIdx, maskPt);
        }
      }
    }
  }

  private void setAnchor() {
    Point[] pt = new Point[4];
    pt[0] = new Point(); // pointLT
    pt[1] = new Point(); // pointRT
    pt[2] = new Point(); // pointLB
    pt[3] = new Point(); // pointRB

    try {
      if (mScanInfo == null) {
        mScanInfo = ImageProcessing
            .loading(mContext, mImageBitmap, mColorSelection, mDocKind);
        retPoint = mScanInfo.getPoint();
        UISupport.cancelToastCustomView();
        if (mImageProcessListener != null) {
          mImageProcessListener.onCompleteImageLoading();
        }
        // 사각이 인식된 경우 바로 변환된 이미지를 보여주도록 함
        // 신분증인 경우 사각 포인트가 인식 되더라도 저장 화면이 나오도록 함(신분증인 경우 masking을 해야 하기 때문)
        if (mScanInfo.getBitmap() != null && !AppConstants.DOC_KIND_B.equals(mDocKind)) {
          mListener.pointChanged(mScanInfo.getBitmap(), 1);
          mFindCornerPoints = 0;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Throwable e1) {
      e1.printStackTrace();
    }

    if (retPoint != null) {
      if (retPoint[3].y > 0) {
        pt = retPoint;
      } else {
        retPoint = null;
      }
    }

//    int orientation = getPhotoOrientationDegree(mImageWidth, mImageHeight);
    if (retPoint == null || (mScanInfo.getBitmap() != null
        && !isTouched)) { //네 꼭지점을 잡지 못했을 경우나 네 꼭지점을 잡았고 사각점 위치를 변경하지 않았을 때
      mPointLT.set((int) (pt[0].x * mZoomRate) + mImageMinX + CORRECTION_POINT_TOP_X,
          (int) (pt[0].y * mZoomRate) + mImageMinY + CORRECTION_POINT_TOP_Y);
      mPointRT.set((int) (pt[1].x * mZoomRate) + mImageMinX - CORRECTION_POINT_TOP_X,
          (int) (pt[1].y * mZoomRate) + mImageMinY + CORRECTION_POINT_TOP_Y);
      mPointLB.set((int) (pt[2].x * mZoomRate) + mImageMinX + CORRECTION_POINT_BOTTOM_X,
          (int) (pt[2].y * mZoomRate) + mImageMinY - CORRECTION_POINT_BOTTOM_Y);
      mPointRB.set((int) (pt[3].x * mZoomRate) + mImageMinX - CORRECTION_POINT_BOTTOM_X,
          (int) (pt[3].y * mZoomRate) + mImageMinY - CORRECTION_POINT_BOTTOM_Y);
    } else {
      mPointLT.set(retPoint[0].x, retPoint[0].y);
      mPointRT.set(retPoint[1].x, retPoint[1].y);
      mPointLB.set(retPoint[2].x, retPoint[2].y);
      mPointRB.set(retPoint[3].x, retPoint[3].y);
    }
    if (pt[3].y == 0) {
      mFindCornerPoints = 0; // mFindCornerPoints : -1(N/A), 0(false), 1(true)
      if (isBlackBackground) {
        int topMargin = PointChangedActivity.mBackTopView
            .getLayoutParams().height;//이미지 아래  검은화면
        int bottomMargin = PointChangedActivity.mBackBottomView
            .getLayoutParams().height;//이미지 아래  검은화면
        if (AppConstants.DOC_KIND_B.equals(mDocKind)) { // 신분증인 경우에만 사각 포인트의 위치를 조정함
          int width = getResources().getDisplayMetrics().widthPixels;
          int defaultMargin = width * 75 / 768;
          int pWidth = (width - defaultMargin - defaultMargin) * (mImageMaxX - mImageMinX)
              / width; //사각잡기에서 가로길이
          int oHeight = mImageMaxY - topMargin - bottomMargin;//이미지 세로 길이
          int bHeight =
              pWidth * AppConstants.ID_CARD_SIZE_MIN
                  / AppConstants.ID_CARD_SIZE_MAX;//사각 잡기  세로길이
          int hMargin = (oHeight - bHeight) / 2;//이미지- 사각잡기 세로 여백
          int wMargin = (mImageMaxX - mImageMinX - pWidth) / 2;//이미지- 사각잡기 가로여백
          int top = topMargin + hMargin;
          int bottom = top + bHeight;

          mPointLT.set(mImageMinX + wMargin, top);
          mPointRT.set(mImageMaxX - wMargin, top);
          mPointLB.set(mImageMinX + wMargin, bottom);
          mPointRB.set(mImageMaxX - wMargin, bottom);
        } else { //매장사진
          mPointLT.set(mImageMinX, topMargin);
          mPointRT.set(mImageMaxX, topMargin);
          mPointLB.set(mImageMinX, this.getMeasuredHeight() - bottomMargin);
          mPointRB.set(mImageMaxX, this.getMeasuredHeight() - bottomMargin);

        }
      } else { //일반문서
        //===========================================================
        // vertex 좌표 계산하기(18.12.11)
        // - intent로 전달된(@PointChangedAct) ArrayList 정보 여부에 따라 분기
        //===========================================================
        setCornerPoint(mIsImageFromGallery);
      }
      onTouchEvent(MotionEvent
          .obtain(10, 10, MotionEvent.ACTION_DOWN, mPointRB.x, mPointRB.y, 2, 2, 2, 2, 2, 2,
              2));
      onTouchEvent(MotionEvent
          .obtain(10, 10, MotionEvent.ACTION_MOVE, mPointRB.x - 1, mPointRB.y - 1, 2, 2, 2, 2,
              2, 2, 2));
      onTouchEvent(MotionEvent
          .obtain(10, 10, MotionEvent.ACTION_UP, mPointRB.x - 1, mPointRB.y - 1, 2, 2, 2, 2,
              2, 2, 2));

    } else {
      mFindCornerPoints = 1; // mFindCornerPoints : -1(N/A), 0(false), 1(true)
    }

    if (isMasking || AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
        .equals(mSelectedFormId)) { // #1341
      if (mMaskingPoint.size() > 0) {
        mMaskingPoint.set(mSelectedMaskingIdx, new Point(-1000, -1000));
      }
    }
  }


  public Bitmap rotateBitmap(Bitmap bitmap, float angle) {
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  }

  /**
   * image processing 사각 영역의 모서리 좌표를 계산
   */
  private void setCornerPoint(boolean imgFromGallery) {
    if (imgFromGallery || mWidthZoomRate <= mHeightZoomRate) {
      mPointLT.set(mImageMinX + POINT_BUFFER, mImageMinY + POINT_BUFFER);
      mPointRT.set(mImageMaxX - POINT_BUFFER, mImageMinY + POINT_BUFFER);
      mPointLB.set(mImageMinX + POINT_BUFFER, mImageMaxY - POINT_BUFFER);
      mPointRB.set(mImageMaxX - POINT_BUFFER, mImageMaxY - POINT_BUFFER);
    } else {
      int pictureW = mImageMaxX - mImageMinX;
      float widthRatio = 1 / SystemUtil.getRatioByMetricsWidth(pictureW);
      int pLeft = (int) (mImageMinX + mHorizontalMargin * widthRatio);
      int pRight = (int) (mImageMinX + (mMeasuredWidth - mHorizontalMargin) * widthRatio);
      int pTop = (int) ((mTopMargin + mVerticalMargin) * widthRatio);
      int pBottom = (int) ((mTopMargin + mPreviewHeight - mVerticalMargin) * widthRatio);
      Log.d(TAG, "loadImageForOnDraw: pLeft= " + pLeft + " ,pRight = " + pRight + " ,pTop = " + pTop
          + " ,pBottom = " + pBottom);
      mPointLT.set(pLeft, pTop);
      mPointRT.set(pRight, pTop);
      mPointLB.set(pLeft, pBottom);
      mPointRB.set(pRight, pBottom);
    }
  }

  /**
   * Measured width(height) 값과 imageWidth(height)값을 비교하여 min(max) X,Y 값을 구함
   */
  private void getImageMinMaxXY() {
    //---------------------------------------------------
    // 비율값(ZoomRate)이 클수록 size 차이가 많이 난다는 의미
    //---------------------------------------------------
    mWidthZoomRate = ((float) mMeasuredWidth / (float) mImageWidth);
    mHeightZoomRate = ((float) mMeasuredHeight / (float) mImageHeight);
    float diffRatioX = Math.abs(1 - mWidthZoomRate); //1에 가까울수록 이미지와 measuredWidth간 차이가 작음을 의미
    float diffRatioY = Math.abs(1 - mHeightZoomRate);
    Log.d(TAG, "getImageMinMaxXY: mWidthZoomRate : " + mWidthZoomRate + ",mHeightZoomRate: "
        + mHeightZoomRate + " ,diffRatioX: " + diffRatioX + " ,diffRatioY: " + diffRatioY);
    float diffRatio = Math.abs(mWidthZoomRate - mHeightZoomRate);

    if (diffRatioX < diffRatioY) {
      int tempImageWidth = mImageWidth * mMeasuredHeight / mImageHeight;
      resizeWidthHeight(tempImageWidth, BIAS_WIDTH);
    } else { // diffRatioX>diffRatioY
      int tempImageHeight = mImageHeight * mMeasuredWidth / mImageWidth;
      resizeWidthHeight(tempImageHeight, BIAS_HEIGHT);
    }

    Log.d(TAG, "getImageMinMaxXY: zoomRate = " + mZoomRate);
    Log.d(TAG, "getImageMinMaxXY: mImageMinX = " + mImageMinX + " ,mImageMaxX = " + mImageMaxX);
    Log.d(TAG, "getImageMinMaxXY: mImageMinY = " + mImageMinY + " ,mImageMaxY = " + mImageMaxY);
  }


  /**
   * temp가 target의 길이(mMeasuredWidth or mMeasuredHeight)보다 클 경우
   */
  private void resizeWidthHeight(int temp, int target) {
    switch (target) {
      case BIAS_WIDTH:
        mZoomRate = mHeightZoomRate;
        if (temp > mMeasuredWidth) {
          mImageMinX = 0;
          mImageMaxX = mMeasuredWidth;
          float wRatio = (float) mMeasuredWidth / (float) temp;
          int tmpHeight = Math.round(mMeasuredHeight * wRatio);
          mImageMinY = (mMeasuredHeight - tmpHeight) / 2;
          mImageMaxY = mImageMinY + tmpHeight;
          mZoomRate *= wRatio;
        } else {
          mImageMinY = 0;
          mImageMaxY = mMeasuredHeight;
          mImageMinX = (mMeasuredWidth - temp) / 2;
          mImageMaxX = mImageMinX + temp;
        }
        break;
      case BIAS_HEIGHT:
        mZoomRate = mWidthZoomRate;
        if (temp > mMeasuredHeight) {
          mImageMinY = 0;
          mImageMaxY = mMeasuredHeight;
          float hRatio = (float) mMeasuredHeight / (float) temp;
          int tmpWidth = Math.round(mMeasuredWidth * hRatio);
          mImageMinX = (mMeasuredWidth - tmpWidth) / 2;
          mImageMaxX = mImageMinX + tmpWidth;
          mZoomRate *= hRatio;
        } else {
          mImageMinX = 0;
          mImageMaxX = mMeasuredWidth;
          mImageMinY = (mMeasuredHeight - temp) / 2;
          mImageMaxY = mImageMinY + temp;
        }
        break;
    }
  }

  private void setMeasuredWH() {
    int configOrientation = getContext().getResources().getConfiguration().orientation;
    Log.d(TAG, "setMeasuredWH: configOrientation = " + configOrientation + " ,getMeasureWidth() = "
        + getMeasuredWidth() + " ,getMeasureHeight() = " + getMeasuredHeight());
    mMeasuredWidth = configOrientation == Configuration.ORIENTATION_LANDSCAPE ?
        Math.max(this.getMeasuredWidth(), this.getMeasuredHeight()) :
        Math.min(this.getMeasuredWidth(), this.getMeasuredHeight()); // 1600
    mMeasuredHeight = getContext().getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE ?
        Math.min(this.getMeasuredWidth(), this.getMeasuredHeight()) :
        Math.max(this.getMeasuredWidth(), this.getMeasuredHeight()); // 2260
    if (Config.DEBUG) {
      Log.d(TAG, "### setMeasuredWH() : mMeasuredWidth[" + mMeasuredWidth + "] mMeasuredHeight["
          + mMeasuredHeight + "]");
    }
  }

  private int getPhotoOrientationDegree(int imageWidth, int imageHeight) {
    int degree = AppConstants.DEGREE_0;

    if (imageWidth > imageHeight) {
      degree = AppConstants.DEGREE_90;
    }

    if (Config.DEBUG) {
      Log.d(TAG,
          "getPhotoOrientationDegree() : imageWidth[" + imageWidth + "] imageHeight[" + imageHeight
              + "] degree[" + degree + "]");
    }
    return degree;
  }

  /**
   * 마스킹 위치 설정
   */
  private void setMaskingPoint() {
    if (mMaskingPoint.size() == 0 || mMaskingPoint.get(0).x == -1000) {
      return;
    }

    if (AppConstants.DOC_KIND_AA.equals(mDocKind) || AppConstants.FORM_ID_J9
        .equals(mSelectedFormId)) {   // A4 사이즈  #1341
      for (int i = 0; i < mMaskingPoint.size(); i++) {
        Point maskingPoint = getCalculatedMaskingPoint(mMaskingPoint.get(i));
        if (maskingPoint != null) {
          mMaskingPoint.set(i, maskingPoint);
        }
      }
    } else if (AppConstants.DOC_KIND_B.equals(mDocKind)) {  // 신분증
      for (int i = 0; i < mMaskingPoint.size(); i++) {
        Point maskingPoint = getCalculatedMaskingPointForId(mMaskingPoint.get(i));
        if (maskingPoint != null) {
          mMaskingPoint.set(i, maskingPoint);
        }
      }
    }
  }

  private Bitmap rotateImage(Bitmap img, int degree) {
    mIsRotated = true;
    int width = img.getWidth();
    int height = img.getHeight();
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    Bitmap rotateImg = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        /*Bitmap scaledBitmap = Bitmap.createScaledBitmap(img,img.getWidth(),img.getHeight(),true);
        Bitmap rotateImg = Bitmap.createBitmap(scaledBitmap,0,0,scaledBitmap.getWidth(), scaledBitmap.getHeight(),martix,true);*/
    img.recycle();
    mImageBitmap = rotateImg;
    mImageDrawable = new BitmapDrawable(this.getContext().getResources(), mImageBitmap);
    return rotateImg;
  }

  /**
   * 터치 포인트와 라인과의 거리 계산 참고 : https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
   *
   * @param x 터치 포인트 x
   * @param y 터치 포인트 y
   * @param x1 시작 라인 포인트 x
   * @param y1 시작 라인 포인트 y
   * @param x2 종료 라인 포인트 x
   * @param y2 종료 라인 포인트 y
   * @return 터치 포인트와 라인과의 거리
   */
  private double getPointDistance(float x, float y, float x1, float y1, float x2, float y2) {
    float A = x - x1;
    float B = y - y1;
    float C = x2 - x1;
    float D = y2 - y1;

    float dot = A * C + B * D;
    float len_sq = C * C + D * D;
    float param = -1;
    if (len_sq != 0) //in case of 0 length line
    {
      param = dot / len_sq;
    }

    float xx, yy;

    if (param < 0) {
      xx = x1;
      yy = y1;
    } else if (param > 1) {
      xx = x2;
      yy = y2;
    } else {
      xx = x1 + param * C;
      yy = y1 + param * D;
    }

    float dx = x - xx;
    float dy = y - yy;
    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Canvas에 사각잡기 line 및 circle 그려준다.
   */
  private void drawLineAndCircle(Canvas canvas) {
    if (mIsVisiblePoint) {

      int colorLineDefault = getResources().getColor(R.color.point_view_vertex_line_default);
      int colorLineChange = getResources().getColor(R.color.point_view_vertex_line_change);

      int colorCircleDefault = getResources().getColor(R.color.point_view_vertex_circle_change);
      int colorCircleChange = getResources().getColor(R.color.point_view_vertex_circle_default);

      canvas.drawLine(mPointLT.x, mPointLT.y, mPointRT.x, mPointRT.y, mCirclePaint);//위선
      canvas.drawLine(mPointRT.x, mPointRT.y, mPointRB.x, mPointRB.y, mCirclePaint);//오른쪽 선
      canvas.drawLine(mPointRB.x, mPointRB.y, mPointLB.x, mPointLB.y, mCirclePaint);//밑 선
      canvas.drawLine(mPointLB.x, mPointLB.y, mPointLT.x, mPointLT.y, mCirclePaint);//왼쪽선

      /* 동그라미. */
      mCirclePaint.setStyle(Paint.Style.FILL);//채우기
      if (mTouchCircleIndex == 1) {
        mCirclePaint.setColor(colorCircleChange);
      } else {
        mCirclePaint.setColor(colorCircleDefault);//연한 파랑
      }
      canvas.drawCircle(mPointLT.x, mPointLT.y, CIRCLE_RADIUS, mCirclePaint);//좌상 원
      if (mTouchCircleIndex == 2) {
        mCirclePaint.setColor(colorCircleChange);
      } else {
        mCirclePaint.setColor(colorCircleDefault);//연한 파랑
      }
      canvas.drawCircle(mPointRT.x, mPointRT.y, CIRCLE_RADIUS, mCirclePaint);//우상 원
      if (mTouchCircleIndex == 3) {
        mCirclePaint.setColor(colorCircleChange);//연한 파랑
      } else {
        mCirclePaint.setColor(colorCircleDefault);//연한 파랑
      }
      canvas.drawCircle(mPointRB.x, mPointRB.y, CIRCLE_RADIUS, mCirclePaint); //우하 원
      if (mTouchCircleIndex == 0) {
        mCirclePaint.setColor(colorCircleChange);
      } else {
        mCirclePaint.setColor(colorCircleDefault);//연한 파랑
      }
      canvas.drawCircle(mPointLB.x, mPointLB.y, CIRCLE_RADIUS, mCirclePaint); //좌하 원
    }
  }
}
