package com.pentaon.vzon.ui.scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.jinho.valmore.BFScanOnline;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.dataset.ScanDataInfo;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.utils.SystemUtil;
import d2r.jpg2tif.lib.cD2RJPG2TIF;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class ImageProcessing {

  private static String TAG = "ImageProcessing";
  private static int mSelectCorners = 0; // mSelectCorners = 0(no input corner points) 1(yes input corner points)
  private static int mSelectAction = 1; // mSelectAction = 0(find corner points)  1(color) 2(gray) 3(black and white)
  private static final float OUTPUT_RATIO = 0.5f;
  private static final int PICTURE_WIDTH = (int) (AppConstants.PAPER_SIZE_MIN*OUTPUT_RATIO); // 입력 영상 가로 크기
  private static final int PICTURE_HEIGHT = (int) (AppConstants.PAPER_SIZE_MAX*OUTPUT_RATIO); // 입력 영상 세로 크기
  private static final int COLOR_QUALITY_JPEG = 50;
  private static final int BW_QUALITY_JPEG = 50;
  // -------------------------------------------------------------------------------
  // 영상처리 라이브러리를 호출하는데 사용되는 파라미터들을 정의한다
  // -------------------------------------------------------------------------------
  private static int[] mInfo = new int[55];//18    //12개에서 15개로 증가함 -> 15개에서 18개로 증가함
  private static int mOverlapPointX;
  private static int mOverlapPointY;
  private static int mTempBitmapWidth;
  private static int mTempBitmapHeight;

  static ScanDataInfo loading(Context context, Bitmap imageBitmap, String colorSelection,
      String docKind) {
    if (Config.DEBUG) {
      Log.d(TAG, "### loading() : imageBitmap.getWidth()[" + imageBitmap.getWidth()
          + "] imageBitmap.getHeight()[" + imageBitmap.getHeight() + "] colorSelection["
          + colorSelection
          + "]");
    }
//    if(mInfo!=null) mInfo=null;
    Arrays.fill(mInfo, 0);
//    mInfo = new int[55];
    try {
      mSelectCorners = 0; // input no
      mSelectAction = 1; // color

      if ((!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
          && !AppConstants.DOC_KIND_AA.equalsIgnoreCase(docKind)) && (
          AppConstants.COLOR_SELECTION_C
              .equalsIgnoreCase(colorSelection))) { // shlee "AA"는 주민등록증 발급 확인서(소스에만 존재
        mSelectAction = 4; // overlap
      } else if ((!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
          && !AppConstants.DOC_KIND_AA.equalsIgnoreCase(docKind)) && (
          AppConstants.COLOR_SELECTION_B.equalsIgnoreCase(colorSelection)
      )) {// shlee "AA"는 주민등록증 발급 확인서(소스에만 존재
        mSelectAction = 5; // overlap
      } else if (AppConstants.COLOR_SELECTION_C.equalsIgnoreCase(colorSelection)) {
        mSelectAction = 1; // color
      } else {
        mSelectAction = 3; // b/w
      }

      ScanDataInfo info = enginePatternRecognition(context, imageBitmap, docKind);

      if (Config.DEBUG) {
        Point[] pt = info.getPoint();
        Log.d(TAG, "### loading() : pt[0]=[" + pt[0] + "] pt[1]=[" + pt[1] + "] pt[2]=[" + pt[2]
            + "] pt[3]=[" + pt[3] + "] pt[3].y[" + pt[3].y + "]");
      }
      return info;
    } catch (Exception e1) {
      throw e1;
    }
  }

  static Bitmap createBitmapFromOverlap(int[] raw, int width, int height, boolean rotated) {
    Bitmap tempBitmap = null;
    Bitmap bgBitmap = null;
    Bitmap resizedBitmap = null;

    if (Config.DEBUG) {
      Log.d(TAG, "createBitmapFromOverlap() : rotated[" + rotated + "] width[" + width + "] height["
          + height + "]");
    }

    try {
      tempBitmap = Bitmap.createBitmap(raw, width, height, Bitmap.Config.RGB_565);
      raw = null;

      bgBitmap = Bitmap
          .createBitmap(AppConstants.PAPER_SIZE_MAX / 2, AppConstants.PAPER_SIZE_MIN / 2,
              Bitmap.Config.RGB_565);
      Canvas canvas = new Canvas(bgBitmap);
      canvas.drawColor(Color.BLACK);

      //---------------------------------------------------------
      // 신분증 사이즈로 crop하기 위해 기준 변수 저장 2019-03-19 _ PC-jhKim
      //---------------------------------------------------------
      mOverlapPointX = 0;
      mOverlapPointY = 0;
      mTempBitmapWidth = 0;
      mTempBitmapHeight = 0;

      if (tempBitmap != null) {
        if (Config.DEBUG) {
          Log.d(TAG, "createBitmapFromOverlap() : rotated[" + rotated + "] PAPER_SIZE_MIN= "
              + AppConstants.PAPER_SIZE_MIN + " PAPER_SIZE_MAX= " + AppConstants.PAPER_SIZE_MAX);
          Log.d(TAG, "createBitmapFromOverlap() : rotated[" + rotated + "] bgBitmap.getWidth()= "
              + bgBitmap.getWidth() + " bgBitmap.getHeight()= " + bgBitmap.getHeight());
          Log.d(TAG,
              "createBitmapFromOverlap() : rotated[" + rotated + "] PICTURE_WIDTH= " + PICTURE_WIDTH
                  + " PICTURE_HEIGHT= " + PICTURE_HEIGHT);
          Log.d(TAG, "createBitmapFromOverlap() : rotated[" + rotated + "] tempBitmap.getWidth()= "
              + tempBitmap.getWidth() + " tempBitmap.getHeight()= " + tempBitmap.getHeight());
        }

        Matrix matrix = new Matrix();
        float scaleWidth = (float) 0.71;//PICTURE_WIDTH/tempBitmap.getWidth();   // 0.9125 = 2336 / 2560
        float scaleHeight = (float) 0.352;//PICTURE_HEIGHT/tempBitmap.getHeight(); // 0.86041665 = 1652 / 1920
        matrix.postScale(scaleWidth, scaleHeight); // 0.9125, 0.86041665

        try {
          tempBitmap = Bitmap
              .createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix,
                  true);

        } catch (OutOfMemoryError e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }

        mTempBitmapWidth = tempBitmap.getWidth();//852
        mTempBitmapHeight = tempBitmap.getHeight();//600

        canvas.drawBitmap(tempBitmap, 0, 0, null);
        return bgBitmap;
      } else {
        return null;
      }
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFromOverlap() : 2 : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (tempBitmap != null) {
        tempBitmap.recycle();
        tempBitmap = null;
      }
      if (resizedBitmap != null) {
        resizedBitmap.recycle();
        resizedBitmap = null;
      }
    }
  }

  //=========================================================
  // public static method
  //=========================================================
  public static Bitmap saving(Context context, Bitmap imageBitmap, Point[] pt,
      String colorSelection, String docKind,
      boolean engineLoaded) throws Exception, Throwable {

    try {
      if (Config.DEBUG) {
        Log.d(TAG, "### saving1() : imageBitmap.getWidth()=[" + imageBitmap.getWidth()
            + "] imageBitmap.getHeight()[" + imageBitmap.getHeight() + "]");
        Log.d(TAG, "### saving1() : ] colorSelection[" + colorSelection
            + "] docKind[" + docKind + "]");
      }

      mSelectCorners = 1; // input yes

      if ((!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
          && !AppConstants.DOC_KIND_AA.equalsIgnoreCase(docKind)) && (
          AppConstants.COLOR_SELECTION_C.equalsIgnoreCase(colorSelection)
      )) {// shlee "AA"는 주민등록증 발급 확인서(소스에만 존재
        mSelectAction = 4; // overlap
      } else if ((!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
          && !AppConstants.DOC_KIND_AA.equalsIgnoreCase(docKind)) && (
          AppConstants.COLOR_SELECTION_B.equalsIgnoreCase(colorSelection)
      )) {// shlee "AA"는 주민등록증 발급 확인서(소스에만 존재
        mSelectAction = 5; // overlap
      } else if (AppConstants.COLOR_SELECTION_C.equalsIgnoreCase(colorSelection)) {
        mSelectAction = 1; // color
      } else {
        mSelectAction = 3; // b/w
      }

      return enginePatternProcessing(context, imageBitmap, pt, mSelectAction,
          docKind, engineLoaded);

    } catch (Exception e1) {
      throw e1;
    } finally {
    }
  }

  public static void saveFile(Bitmap tempBitmap, String imagePath, String colorSelection) {
    long startTime = System.currentTimeMillis();
    int jpegQuality;
    try {
      if (AppConstants.COLOR_SELECTION_C.equalsIgnoreCase(colorSelection)) {
        jpegQuality = COLOR_QUALITY_JPEG; // color
      } else {
        jpegQuality = BW_QUALITY_JPEG; // b/w
      }
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing() : saveImage! cameraMode[" + colorSelection
            + "] QUALITY_JPEG[" + jpegQuality + "] imagePath[" + imagePath + "]");
      }
      // TODO: 2018-11-02 실제 파일 저장되는 부분
      FileOutputStream fos = new FileOutputStream(imagePath);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      tempBitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, bos);
      bos.close();
      fos.close();

      if (AppConstants.COLOR_SELECTION_B.equals(colorSelection)) {    // tif로 서버 변경시 적용
        try {
          Bitmap bi = BitmapFactory.decodeFile(imagePath);
          int[] pixels = new int[bi.getHeight() * bi.getWidth()];
          byte[] pixelsbyte = new byte[bi.getHeight() * bi.getWidth()];

          bi.getPixels(pixels, 0, bi.getWidth(), 0, 0, bi.getWidth(), bi.getHeight());

          for (int i = 0; i < pixels.length; i++) {
            pixelsbyte[i] = (byte) pixels[i];
          }

          cD2RJPG2TIF.img2tif(bi.getWidth(), bi.getHeight(), bi.getWidth(), pixelsbyte, 1, 1, 100,
              imagePath.replace("jpg", "tif"));

          SystemUtil.deleteFile(imagePath);
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    long stopTime2 = System.currentTimeMillis();
    long elapsedTime2 = stopTime2 - startTime;

    if (Config.DEBUG) {
      Log.d(TAG, "### enginePatternProcessing() : Image " + tempBitmap.getWidth() + "x" + tempBitmap
          .getHeight() + " processed -- " + elapsedTime2 + "ms");
    }
  }

  /**
   * ------------------------------------------------------------------------------- // 저장시 이미지를 가운데
   * 정렬하지 않고 상단 정렬한다. -------------------------------------------------------------------------------
   **/
  public static void saveFile(Bitmap tempBitmap, String imagePath, String colorSelection,
      String docType, int top) {

    Bitmap bgBitmap = Bitmap
        .createBitmap(AppConstants.PAPER_SIZE_MIN, AppConstants.PAPER_SIZE_MAX,
            Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bgBitmap);
    canvas.drawColor(Color.WHITE);//Color.WHITE

    int gap = 0;
    //---------------------------------------------------------
    // 매장 사진 일반 비율로 촬영 되도록 수정 2019-03-13 _ PC-jhKim
    //---------------------------------------------------------
    if (AppConstants.DOC_KIND_B.equals(docType)) { // 원본 이미지가 가운데 정렬이 되어 있기 때문에 y값을 보정해 준다.
      gap = (AppConstants.PAPER_SIZE_MAX - PICTURE_WIDTH) / 2;
    } else {
      gap = (AppConstants.PAPER_SIZE_MAX - PICTURE_HEIGHT) / 2;
    }
    Log.d(TAG, "saveFile: gap =  " + gap);
    canvas.drawBitmap(tempBitmap, 0, -gap + top, null);

    //---------------------------------------------------------
    // 설치 사진일 경우 A4사이즈의 배경 없이 찍은 사진 그대로 저장 2019-03-18 _ PC-jhKim
    //---------------------------------------------------------
    Bitmap sourceBitmap =
        (AppConstants.DOC_KIND_E.equals(docType) || AppConstants.DOC_KIND_B.equals(docType))
            ? tempBitmap
            : bgBitmap;
    saveFile(sourceBitmap, imagePath, colorSelection);
  }

  // bmp -> byte[]

  // --------------------------------------------------------------
  // 사각형 코너 포인터에 십자형 표시한다
  // --------------------------------------------------------------
  public static void drawCornerPoints(Bitmap drawBitmap, int info[]) {
    Canvas canvas = new Canvas(drawBitmap);
    Paint paint = new Paint();
    paint.setColor(Color.GREEN);
    paint.setStrokeWidth(10);

    for (int i = 4 + 6; i < 11 + 6; i += 2) {
      int x = info[i];
      int y = info[i + 1];
      canvas.drawLine(x - 20, y, x + 20, y, paint);
      canvas.drawLine(x, y - 20, x, y + 20, paint);
    }
  }

  /**
   * 전달된 비트맵을 아이디 카드 사이즈로 crop함
   */
  public static Bitmap cropBitmapAsIdCardSize(Bitmap bitmap) {
    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();

    mOverlapPointX =mTempBitmapWidth-bitmapWidth;
    mOverlapPointY =mTempBitmapHeight-bitmapHeight;

    mOverlapPointX= getThresholdNum(mOverlapPointX,bitmapWidth);
    mOverlapPointY =getThresholdNum(mOverlapPointY,bitmapHeight);


    return Bitmap
        .createBitmap(bitmap, 0, 0, mTempBitmapWidth, mTempBitmapHeight);
  }

  private static int getThresholdNum(int num, int max){
     if(num<0) return 0;
     else if(num>max) return max;
     else return num;
  }
  // --------------------------------------------------------------
  // 이미지가 너무 크면 처리가 곤란하다
  // 모바일 기기에서 허용되는 범위를 실험적으로 구해서 처리 사이즈를 아래 함수에서 수정할 수 있다.
  // --------------------------------------------------------------


  public static byte[] get0RGBbyteData(Bitmap bm) {
    int size = bm.getByteCount();
    ByteBuffer buffer = ByteBuffer.allocate(size);
    bm.copyPixelsToBuffer(buffer);
    byte[] bmppxl = buffer.array();

    byte[] pixels = new byte[(bmppxl.length / 4) * 3];
    for (int i = 0; i < (bmppxl.length / 4); i++) {
      pixels[i * 3] = bmppxl[i * 4 + 0]; // B
      pixels[i * 3 + 1] = bmppxl[i * 4 + 1]; // G
      pixels[i * 3 + 2] = bmppxl[i * 4 + 2]; // R
      // Alpha is discarded
    }

    return pixels; // 3 byte BGR data
  }


  //---------------------------------------------------------
  // 일반 문서 일 때
  //---------------------------------------------------------
  private static Bitmap createBitmapFrom0RGB(byte data[], int width,
      int height) // data[] -- 3 byte BGR data 12m
  {
    int len = width * height;
    int[] raw = null;
    Bitmap bitmap = null;

    if (Config.DEBUG) {
      Log.d(TAG, "createBitmapFrom0RGB() : width[" + width + "] height[" + height + "]");
    }

    try {
      raw = new int[len * 4]; // for 3bytes -- 0RGB
      for (int i = 0; i < len; i++) {
        raw[i] = 0xFF000000 |
            ((data[3 * i + 0] & 0xFF) << 16) |
            ((data[3 * i + 1] & 0xFF) << 8) |
            ((data[3 * i + 2] & 0xFF));
      }

      bitmap = Bitmap.createBitmap(raw, width, height, Bitmap.Config.RGB_565);
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFrom0RGB() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    data = null;
    raw = null;
    return bitmap;
  }
  //---------------------------------------------------------
  // 신분증 일 때
  // 2019-09-18 _ jhKim
  //---------------------------------------------------------
  private static Bitmap createBitmapFrom0RGBOverlap(byte[] data, int width, int height,
      boolean rotated) // data[] -- 3 byte BGR data 12m
  {
    int len = width * height;
    int[] raw = null;

    if (Config.DEBUG) {
      Log.d(TAG,
          "createBitmapFrom0RGBOverlap() : rotated[" + rotated + "] width[" + width + "] height["
              + height + "]");
    }

    try {
      raw = new int[len * 4]; // for 3bytes -- 0RGB
      for (int i = 0; i < len; i++) {
        raw[i] = 0xFF000000 |
            ((data[3 * i] & 0xFF) << 16) |
            ((data[3 * i + 1] & 0xFF) << 8) |
            ((data[3 * i + 2] & 0xFF));
      }
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFrom0RGBOverlap() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    data = null;
    Bitmap bitmap =createBitmapFromOverlap(raw, width, height, rotated);
    return bitmap;
  }

  //=========================================================
  // private method
  //=========================================================
  private static ScanDataInfo enginePatternRecognition(Context context, Bitmap imageBitmap,
      String docKind) {
    if (imageBitmap == null) {
      Log.d(TAG, "### enginePatternRecogntion1() : No image loaded!");
      return null;
    } else {
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternRecogntion1() : mSelectCorners[" + mSelectCorners + "] mSelectAction["
                + mSelectAction + "]");
      }
    }

    ScanDataInfo scanInfo = new ScanDataInfo();

    int src_w = imageBitmap.getWidth();
    int src_h = imageBitmap.getHeight();

    mInfo[0] = src_w; // 입력 영상 가로 크기
    mInfo[1] = src_h; // 입력 영상 세로 크기

    if (docKind.equals(AppConstants.DOC_KIND_B)) {
      mInfo[2] = AppConstants.PERSPECTIVE_WIDTH;
      mInfo[3] = AppConstants.PERSPECTIVE_DEPTH;
      mInfo[6] = 1;   // 신분증 세로방향 촬영(0), 신분증 가로방향 촬영(1)
      mInfo[51] = AppConstants.XVIEW_MARGINE;
      mInfo[52] = AppConstants.YVIEW_MARGINE;
    } else {
      mInfo[2] = AppConstants.PERSPECTIVE_WIDTH_A4;
      mInfo[3] = AppConstants.PERSPECTIVE_DEPTH_A4;
      mInfo[6] = 2;   // A4 문서
      mInfo[51] = AppConstants.XVIEW_MARGINE_A4;
      mInfo[52] = AppConstants.YVIEW_MARGINE_A4;
    }

    // 아래 사각형 영역 코너 좌표는 2기지 용도로 사용된다
    // (1) 인식엔진 라이브러리에서 구한 코너좌표가 리턴되어 온다.
    // (2) 외부에서 사각형 코너 좌표를 수정하고 인식엔진 라이브러리로 전달해서 문서를 보정할 때도 사용된다
//    mInfo[4] = 0; // 사각형 영역 좌하 x 좌표 pointLB
//    mInfo[5] = 0; // 사각형 영역 좌하 y 좌표 pointLB
//    mInfo[6] = 0; // 사각형 영역 좌상 x 좌표 pointLT
//    mInfo[7] = 0; // 사각형 영역 좌상 y 좌표 pointLT
//    mInfo[8] = 0; // 사각형 영역 우상 x 좌표 pointRT
//    mInfo[9] = 0; // 사각형 영역 우상 y 좌표 pointRT
//    mInfo[10] = 0; // 사각형 영역 우하 x 좌표 pointRB
//    mInfo[11] = 0; // 사각형 영역 우하 y 좌표 pointRB
//
//    mInfo[12] = 0;
//    mInfo[13] = 0;
//    mInfo[14] = 0;
//    mInfo[15] = 0;
//    mInfo[16] = 0;
//    mInfo[17] = 0;

    if (Config.DEBUG) {
      Log.d(TAG,
          "### enginePatternRecogntion1() : mInfo[0]=[" + mInfo[0] + "] mInfo[1]=[" + mInfo[1]
              + "] mInfo[2]=[" + mInfo[2] + "] mInfo[3]=[" + mInfo[3] + "]");
      Log.d(TAG,
          "### enginePatternRecogntion1() : mInfo[4]=[" + mInfo[4] + "] mInfo[5]=[" + mInfo[5]
              + "] mInfo[6]=[" + mInfo[6] + "] mInfo[7]=[" + mInfo[7] + "]");
      Log.d(TAG,
          "### enginePatternRecogntion1() : mInfo[8]=[" + mInfo[8] + "] mInfo[9]=[" + mInfo[9]
              + "] mInfo[10]=[" + mInfo[10] + "] mInfo[11]=[" + mInfo[11] + "]");
    }
    int dst_w = mInfo[2];
    int dst_h = mInfo[3];

    byte src[] = get0RGBbyteData(imageBitmap); // ARGB --> RGB format
    byte dst[] = new byte[dst_w * dst_h * 3];


        /*switch (mSelectAction) {
            case 1:
                dst = new byte[dst_w * dst_h * 3];
                break; // RGB color: 보정 결과 24비트 컬러 영상을 반환
            case 2:
                dst = new byte[dst_w * dst_h];
                break; // Gray     : 보정 결과 08비트 Gray 영상을 반환
            case 3:
                dst = new byte[dst_w * dst_h];
                break; // B & W    : 보정 결과 08비트 흑백 영상을 반환
            case 4:
                dst = new byte[dst_w * dst_h * 3];
                break; // RGB color: 보정 결과 24비트 컬러 영상을 반환
            case 5:
                dst = new byte[dst_w * dst_h];
                break; // B & W    : 보정 결과 08비트 흑백 영상을 반환
        }*/

    // (TIME CHECK - 시작) ------------------------------------------------------------
    long startTime = System.currentTimeMillis();
    boolean bOk = false;
    boolean bEngineLoad = false;
    if (!(AppConstants.DOC_KIND_B.equalsIgnoreCase(
        docKind))) { // shlee 신분증(B) 또는 매장사진(E) 인 경우 무조건 인식 실패로(인식실패를 가정하므로 엔진을 거치지 않음 - 추후 신분증 인식이 되면 제외할 것)
      //bOk = false;

      // ------------------------------------------------------------------------------
      // 라이브러리 엔진을 로드한다...
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternRecogntion1() : Engine.EngineLoad()");
      }
      if (BFScanOnline.getInstance().BFScanInit(context, AppConstants.LICENSE_KEY.getBytes())
          != 0) {
//      if (!BFScanC.getInstance().EngineInit()) {
        Log.d(TAG, "### enginePatternRecogntion1() : Vision Engine 초기화 실패 앱을 종료 후 다시 해보시길 바랍니다.");
      } else {
        bEngineLoad = true;
      }
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternRecogntion1() : Engine Load end!");
      }

      // ------------------------------------------------------------------------------
      // mSelectAction = 0 코너 포인트 좌표를 반환한다
      // 				 = 1 컬러 보정 이미지를 반환한다
      //				 = 2 명도 보정 이미지를 반환한다
      //				 = 3 흑백 보정 이미지를 반환한다
      // 				 = 4 컬러 보정 이미지를 반환한다
      //				 = 5 흑백 보정 이미지를 반환한다
      // ------------------------------------------------------------------------------
      // 사각형 코너 포인트를 모르고 문서를 보정한다
      if (mSelectAction == 4) {
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : start");
        }
        bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 0, 1); // color
//        bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 0, 1); // color
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : end");
        }
      } else if (mSelectAction == 5) {
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : start");
        }
        bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 0, 3); // black and white
//        bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 0, 3); // black and white
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : end");
        }
      } else {
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : start");
        }
        bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 0, mSelectAction);
//        bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 0, mSelectAction);
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternRecogntion1() : EngineCroppingImageWithoutPoints : mSelectAction == "
                  + mSelectAction + " : end");
        }
      }
    }
    Bitmap tempBitmap = null;
    // ------------------------------------------------------------------------------
    Point lt = new Point();
    Point rt = new Point();
    Point lb = new Point();
    Point rb = new Point();
    Point[] pt = {lt, rt, lb, rb};

    bOk = false;
    if (!bOk) {
      Log.d(TAG, "### enginePatternRecogntion1() : Cant't find corner points!");
      //return mInfo;
      scanInfo.setBitmap(null);
      pt[0].set(0, 0); // pointLT
      pt[1].set(0, 0); // pointRT
      pt[2].set(0, 0); // pointLB
      pt[3].set(0, 0); // pointRB
      scanInfo.setPoint(pt);
    } else {
      pt[0].set(mInfo[12], mInfo[13]); // pointLT
      pt[1].set(mInfo[14], mInfo[15]); // pointRT
      pt[2].set(mInfo[10], mInfo[11]); // pointLB
      pt[3].set(mInfo[16], mInfo[17]); // pointRB

      scanInfo.setPoint(pt);

      int width = pt[1].x - pt[0].x;  // 사각형 영역 우상 x 좌표 PointRT - 사각형 영역 좌상 x 좌표 PointLT
      int height = pt[2].y - pt[0].y; // 사각형 영역 좌하 y 좌표 PointLB - 사각형 영역 좌상 y 좌표 PointLT

      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : width[" + width + "] pt[1].x[" + pt[1].x
            + "] pt[0].x[" + pt[0].x + "]");
        Log.d(TAG, "### enginePatternProcessing1() : height[" + height + "] pt[2].y[" + pt[2].y
            + "] pt[0].y[" + pt[0].y + "]");
      }

      //shlee 나중에 추가함
      boolean rotated = false;

      /*if ((width > height) && (!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
          && !AppConstants.DOC_KIND_AA
          .equalsIgnoreCase(docKind))) { // shlee "AA"는 주민등록증 발급 확인서(소스에만 존재)
        try {
          Matrix matrix = new Matrix();
          matrix.postRotate(AppConstants.DEGREE_270); // 270
          imageBitmap = Bitmap
              .createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(),
                  matrix, true);
          if (Config.DEBUG) {
            Log.d(TAG, "### enginePatternProcessing1() : postRotate[" + AppConstants.DEGREE_270
                + "] imageBitmap.getWidth()[" + imageBitmap.getWidth()
                + "] imageBitmap.getHeight()[" + imageBitmap.getHeight() + "]");
          }
          rotated = true;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }*/

      if (rotated) {
        // postRotate 결과를  src_w scr_h에 반영
        int temp_src_w = src_w;
        src_w = src_h;
        src_h = temp_src_w;

        // src_w와 scr_h 값이 서로 바뀜
        mInfo[0] = src_w; // 입력 영상 가로 크기 2336
        mInfo[1] = src_h; // 입력 영상 세로 크기 1652

        // dst_w와 dst_h는 현상태 유지 (<- 이미지 솔루션 A4만 지원 : 세로 모드만 지원)
        mInfo[2] = dst_w; // 결과 영상 가로 크기 1652
        mInfo[3] = dst_h; // 결과 영상 세로 크기 2336

        // 아래 사각형 영역 코너 좌표는 2기지 용도로 사용된다
        // (1) 인식엔진 라이브러리에서 구한 코너좌표가 리턴되어 온다.
        // (2) 외부에서 사각형 코너 좌표를 수정하고 인식엔진 라이브러리로 전달해서 문서를 보정할 때도 사용된다
        // Point[] pt = {PointLT, PointRT, PointLB, PointRB};

//                // PointLB 좌표 조정
//                mInfo[4] = pt[0].y;
//                mInfo[5] = src_h - pt[0].x;
//
//                // PointLT 좌표 조정
//                mInfo[6] = pt[1].y;
//                mInfo[7] = src_h - pt[1].x;
//
//                // PointRT 좌표 조정
//                mInfo[8] = pt[3].y;
//                mInfo[9] = src_h - pt[3].x;
//
//                // PointRB 좌표 조정
//                mInfo[10] = pt[2].y;
//                mInfo[11] = src_h - pt[2].x;

        // PointLB 좌표 조정
        mInfo[10] = pt[0].y;
        mInfo[11] = src_h - pt[0].x;

        // PointLT 좌표 조정
        mInfo[12] = pt[1].y;
        mInfo[13] = src_h - pt[1].x;

        // PointRT 좌표 조정
        mInfo[14] = pt[3].y;
        mInfo[15] = src_h - pt[3].x;

        // PointRB 좌표 조정
        mInfo[16] = pt[2].y;
        mInfo[17] = src_h - pt[2].x;
      } else {
        mInfo[0] = src_w; // 입력 영상 가로 크기 1652
        mInfo[1] = src_h; // 입력 영상 세로 크기 2336

        mInfo[2] = dst_w; // 결과 영상 가로 크기 1652
        mInfo[3] = dst_h; // 결과 영상 세로 크기 2336

        // 아래 사각형 영역 코너 좌표는 2기지 용도로 사용된다
        // (1) 인식엔진 라이브러리에서 구한 코너좌표가 리턴되어 온다.
        // (2) 외부에서 사각형 코너 좌표를 수정하고 인식엔진 라이브러리로 전달해서 문서를 보정할 때도 사용된다
        // Point[] pt = {PointLT, PointRT, PointLB, PointRB};

//                mInfo[4] = pt[2].x; // 사각형 영역 좌하 x 좌표 PointLB
//                mInfo[5] = pt[2].y; // 사각형 영역 좌하 y 좌표 PointLB
//                mInfo[6] = pt[0].x; // 사각형 영역 좌상 x 좌표 PointLT
//                mInfo[7] = pt[0].y; // 사각형 영역 좌상 y 좌표 PointLT
//                mInfo[8] = pt[1].x; // 사각형 영역 우상 x 좌표 PointRT
//                mInfo[9] = pt[1].y; // 사각형 영역 우상 y 좌표 PointRT
//                mInfo[10] = pt[3].x; // 사각형 영역 우하 x 좌표 PointRB
//                mInfo[11] = pt[3].y; // 사각형 영역 우하 y 좌표 PointRB

        mInfo[10] = pt[2].x; // 사각형 영역 좌하 x 좌표 PointLB
        mInfo[11] = pt[2].y; // 사각형 영역 좌하 y 좌표 PointLB
        mInfo[12] = pt[0].x; // 사각형 영역 좌상 x 좌표 PointLT
        mInfo[13] = pt[0].y; // 사각형 영역 좌상 y 좌표 PointLT
        mInfo[14] = pt[1].x; // 사각형 영역 우상 x 좌표 PointRT
        mInfo[15] = pt[1].y; // 사각형 영역 우상 y 좌표 PointRT
        mInfo[16] = pt[3].x; // 사각형 영역 우하 x 좌표 PointRB
        mInfo[17] = pt[3].y; // 사각형 영역 우하 y 좌표 PointRB
      }

      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[0]=[" + mInfo[0]
            + "] mInfo[1]=[" + mInfo[1] + "] mInfo[2]=[" + mInfo[2] + "] mInfo[3]=[" + mInfo[3]
            + "]");
        Log.d(TAG,
            "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[10]=[" + mInfo[10]
                + "] mInfo[11]=[" + mInfo[11] + "] mInfo[12]=[" + mInfo[12] + "] mInfo[13]=["
                + mInfo[13]
                + "]");
        Log.d(TAG,
            "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[14]=[" + mInfo[14]
                + "] mInfo[15]=[" + mInfo[15] + "] mInfo[16]=[" + mInfo[16] + "] mInfo[17]=["
                + mInfo[17]
                + "]");
      }

      /////////////////////////////////////////////////////////////////////////////////////

      if (mSelectAction == 0) {
        Bitmap drawBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
        drawCornerPoints(drawBitmap, mInfo);
        scanInfo.setBitmap(drawBitmap);
        return scanInfo;
      } else if (mSelectAction == 4) { // RGB  COLOR  -- 24bits (Overlap) // 여권 촬영시 #1013
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternProcessing1() : createBitmapFrom0RGBOverlap : selectAction == "
                  + mSelectAction + " : start");
        }
        tempBitmap = createBitmapFrom0RGBOverlap(dst, dst_w, dst_h, rotated);
        if (Config.DEBUG) {
          Log.d(TAG,
              "### enginePatternProcessing1() : createBitmapFrom0RGBOverlap : selectAction == "
                  + mSelectAction + " : end");
        }
      } else {
        if (Config.DEBUG) {
          Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGB : selectAction == "
              + mSelectAction + " : start");
        }
        tempBitmap = createBitmapFrom0RGB(dst, dst_w, dst_h);
        if (Config.DEBUG) {
          Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGB : selectAction == "
              + mSelectAction + " : end");
        }
      }
      scanInfo.setBitmap(tempBitmap);
    }

    // (TIME CHECK - 종료) ------------------------------------------------------------
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;

    if (bEngineLoad) {
      // -------------------------------------------------------------------------------
      // 라이브러리 엔진을 헤재한다
      BFScanOnline.getInstance().BFScanRelease();
//      BFScanC.getInstance().EngineRelease();
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternRecogntion1() : EngineRelease!");
      }
      // ------------------------------------------------------------------------------
    }
    if (Config.DEBUG) {
      Log.d(TAG,
          "### enginePatternRecogntion1() : Image " + imageBitmap.getWidth() + "x" + imageBitmap
              .getHeight() + " processed -- " + elapsedTime + "ms");
    }
    return scanInfo;
  }

  private static Bitmap enginePatternProcessing(Context context, Bitmap imageBitmap, Point[] pt,
      int selectAction,
      String docKind, boolean engineLoaded) {
    if (imageBitmap == null) {
      Log.d(TAG, "### enginePatternProcessing() : No image loaded!");
      return null;
    } else {
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing() : mSelectCorners[" + mSelectCorners + "] selectAction["
                + selectAction + "]");
      }
    }

    int src_w = imageBitmap.getWidth();
    int src_h = imageBitmap.getHeight();
    int dst_w = AppConstants.PAPER_SIZE_MIN;
    int dst_h = AppConstants.PAPER_SIZE_MAX;

    if (Config.DEBUG) {
      Log.d(TAG, "### enginePatternProcessing1() : src_w[" + src_w + "] src_h[" + src_h + "]");
      Log.d(TAG, "### enginePatternProcessing1() : dst_w[" + dst_w + "] dst_h[" + dst_h + "]");
    }

    int width = pt[1].x - pt[0].x;  // 사각형 영역 우상 x 좌표 PointRT - 사각형 영역 좌상 x 좌표 PointLT
    int height = pt[2].y - pt[0].y; // 사각형 영역 좌하 y 좌표 PointLB - 사각형 영역 좌상 y 좌표 PointLT

    if (Config.DEBUG) {
      Log.d(TAG,
          "### enginePatternProcessing1() : width[" + width + "] pt[1].x[" + pt[1].x + "] pt[0].x["
              + pt[0].x + "]");
      Log.d(TAG, "### enginePatternProcessing1() : height[" + height + "] pt[2].y[" + pt[2].y
          + "] pt[0].y[" + pt[0].y + "]");
    }

    //---------------------------------------------------------
    // 매장 사진 일반 비율로 촬영 되도록 수정 2019-03-13 _ PC-jhKim
    //---------------------------------------------------------
   /* if (AppConstants.DOC_KIND_B.equals(docKind)) { //shlee add 신분증인 경우 출력 화면 비율을 8:5로 변경한다.
      dst_h = AppConstants.PAPER_SIZE_MIN * 856 / 540;      //매장사진도 신분증 비율로 출력
      // 85.6mm, 세로 54mm
    }*/
//		else if ("E".equals(docKind)) { // 매장 사진인 경우 원본 비율 유지
//		    dst_h = src_h;
//		    dst_w = src_w;
//		}

    boolean rotated = false;



    //---------------------------------------------------------
    // 문서 종류에 따라 width, height의 크기비교 통해 이미지 rotate 시켜줌
    // 2019-09-18 _ jhKim
    //---------------------------------------------------------
    /*if (AppConstants.DOC_KIND_B.equals(docKind)&& src_w<src_h) {
      rotated=true;
    }else if(AppConstants.DOC_KIND_A.equals(docKind)&&src_w>src_h){
      rotated=true;
    }else rotated=false;*/




    /*if ((width > height) && (!AppConstants.DOC_KIND_A.equalsIgnoreCase(docKind)
        && !AppConstants.DOC_KIND_AA.equalsIgnoreCase(docKind)
    )) { // shlee "AA"는 주민등록증 발급 확인서(소스에만 존재)
      rotated = true;
      *//*try {
        Matrix matrix = new Matrix();
        matrix.postRotate(AppConstants.DEGREE_270); // 270
        imageBitmap = Bitmap
            .createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(),
                matrix, false);
        if (Config.DEBUG) {
          Log.d(TAG, "### enginePatternProcessing1() : postRotate[" + AppConstants.DEGREE_270
              + "] imageBitmap.getWidth()[" + imageBitmap.getWidth() + "] imageBitmap.getHeight()["
              + imageBitmap.getHeight() + "]");
        }
        rotated = true;
      } catch (Exception e) {
        e.printStackTrace();
      }*//*
    }*/

    if (rotated) {
      Matrix matrix = new Matrix();
      matrix.postRotate(AppConstants.DEGREE_90); // 270
      imageBitmap = Bitmap
          .createBitmap(imageBitmap, 0, 0, src_w, src_h,
              matrix, true);
      // postRotate 결과를  src_w scr_h에 반영
      int temp_src_w = src_w;
      src_w = src_h;
      src_h = temp_src_w;

      // src_w와 scr_h 값이 서로 바뀜
      mInfo[0] = src_w; // 입력 영상 가로 크기 2336
      mInfo[1] = src_h; // 입력 영상 세로 크기 1652

      // dst_w와 dst_h는 현상태 유지 (<- 이미지 솔루션 A4만 지원 : 세로 모드만 지원)
      mInfo[2] = dst_w; // 결과 영상 가로 크기 1652
      mInfo[3] = dst_h; // 결과 영상 세로 크기 2336

      // 아래 사각형 영역 코너 좌표는 2기지 용도로 사용된다
      // (1) 인식엔진 라이브러리에서 구한 코너좌표가 리턴되어 온다.
      // (2) 외부에서 사각형 코너 좌표를 수정하고 인식엔진 라이브러리로 전달해서 문서를 보정할 때도 사용된다
      // Point[] pt = {PointLT, PointRT, PointLB, PointRB};

//            // PointLB 좌표 조정
//            mInfo[4] = pt[0].y;
//            mInfo[5] = src_h - pt[0].x;
//
//            // PointLT 좌표 조정
//            mInfo[6] = pt[1].y;
//            mInfo[7] = src_h - pt[1].x;
//
//            // PointRT 좌표 조정
//            mInfo[8] = pt[3].y;
//            mInfo[9] = src_h - pt[3].x;
//
//            // PointRB 좌표 조정
//            mInfo[10] = pt[2].y;
//            mInfo[11] = src_h - pt[2].x;

      // PointLB 좌표 조정
      mInfo[10] = pt[0].y;
      mInfo[11] = src_h - pt[0].x;

      // PointLT 좌표 조정
      mInfo[12] = pt[1].y;
      mInfo[13] = src_h - pt[1].x;

      // PointRT 좌표 조정
      mInfo[14] = pt[3].y;
      mInfo[15] = src_h - pt[3].x;

      // PointRB 좌표 조정
      mInfo[16] = pt[2].y;
      mInfo[17] = src_h - pt[2].x;
    } else {
      mInfo[0] = src_w; // 입력 영상 가로 크기 1652
      mInfo[1] = src_h; // 입력 영상 세로 크기 2336

      mInfo[2] = dst_w; // 결과 영상 가로 크기 1652
      mInfo[3] = dst_h; // 결과 영상 세로 크기 2336

      // 아래 사각형 영역 코너 좌표는 2기지 용도로 사용된다
      // (1) 인식엔진 라이브러리에서 구한 코너좌표가 리턴되어 온다.
      // (2) 외부에서 사각형 코너 좌표를 수정하고 인식엔진 라이브러리로 전달해서 문서를 보정할 때도 사용된다
      // Point[] pt = {PointLT, PointRT, PointLB, PointRB};

//            mInfo[4] = pt[2].x; // 사각형 영역 좌하 x 좌표 PointLB
//            mInfo[5] = pt[2].y; // 사각형 영역 좌하 y 좌표 PointLB
//            mInfo[6] = pt[0].x; // 사각형 영역 좌상 x 좌표 PointLT
//            mInfo[7] = pt[0].y; // 사각형 영역 좌상 y 좌표 PointLT
//            mInfo[8] = pt[1].x; // 사각형 영역 우상 x 좌표 PointRT
//            mInfo[9] = pt[1].y; // 사각형 영역 우상 y 좌표 PointRT
//            mInfo[10] = pt[3].x; // 사각형 영역 우하 x 좌표 PointRB
//            mInfo[11] = pt[3].y; // 사각형 영역 우하 y 좌표 PointRB

      mInfo[10] = pt[2].x; // 사각형 영역 좌하 x 좌표 PointLB
      mInfo[11] = pt[2].y; // 사각형 영역 좌하 y 좌표 PointLB
      mInfo[12] = pt[0].x; // 사각형 영역 좌상 x 좌표 PointLT
      mInfo[13] = pt[0].y; // 사각형 영역 좌상 y 좌표 PointLT
      mInfo[14] = pt[1].x; // 사각형 영역 우상 x 좌표 PointRT
      mInfo[15] = pt[1].y; // 사각형 영역 우상 y 좌표 PointRT
      mInfo[16] = pt[3].x; // 사각형 영역 우하 x 좌표 PointRB
      mInfo[17] = pt[3].y; // 사각형 영역 우하 y 좌표 PointRB
    }

    if (Config.DEBUG) {
      Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[0]=[" + mInfo[0]
          + "] mInfo[1]=[" + mInfo[1] + "] mInfo[2]=[" + mInfo[2] + "] mInfo[3]=[" + mInfo[3]
          + "]");
      Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[4]=[" + mInfo[4]
          + "] mInfo[5]=[" + mInfo[5] + "] mInfo[6]=[" + mInfo[6] + "] mInfo[7]=[" + mInfo[7]
          + "]");
      Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[8]=[" + mInfo[8]
          + "] mInfo[9]=[" + mInfo[9] + "] mInfo[10]=[" + mInfo[10] + "] mInfo[11]=[" + mInfo[11]
          + "]");
      Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[12]=[" + mInfo[12]
          + "] mInfo[13]=[" + mInfo[13] + "] mInfo[14]=[" + mInfo[14] + "] mInfo[15]=[" + mInfo[15]
          + "]");
      Log.d(TAG, "### enginePatternProcessing1() : rotated[" + rotated + "] mInfo[16]=[" + mInfo[16]
          + "] mInfo[17]=[" + mInfo[17] + "]");
    }

    byte src[] = get0RGBbyteData(imageBitmap); // ARGB --> RGB format
    byte dst[] = new byte[dst_w * dst_h * 3];

        /*switch (selectAction) {
            case 1:
                dst = new byte[dst_w * dst_h * 3];
                break; // RGB color: 보정 결과 24비트 컬러 영상을 반환
            case 2:
                dst = new byte[dst_w * dst_h];
                break; // Gray     : 보정 결과 08비트 Gray 영상을 반환
            case 3:
                dst = new byte[dst_w * dst_h];
                break; // B & W    : 보정 결과 08비트 흑백 영상을 반환
            case 4:
                dst = new byte[dst_w * dst_h * 3];
                break; // RGB color: 보정 결과 24비트 컬러 영상을 반환
            case 5:
                dst = new byte[dst_w * dst_h];
                break; // B & W    : 보정 결과 08비트 흑백 영상을 반환
        }*/

    if (engineLoaded) {
      // ------------------------------------------------------------------------------
      // 라이브러리 엔진을 로드한다...
      if (Config.DEBUG) {
        Log.d(TAG, "Engine.EngineLoad()");
      }
      if (BFScanOnline.getInstance().BFScanInit(context, AppConstants.LICENSE_KEY.getBytes())
          != 0) {
//      if (!BFScanC.getInstance().EngineInit()) {
        Log.d(TAG, "### enginePatternProcessing1() : Vision Engine 초기화 실패 앱을 종료 후 다시 해보시길 바랍니다.");
      }
      if (Config.DEBUG) {
        Log.d(TAG, "Load end!");
      }
    }

    // (TIME CHECK - 시작) ------------------------------------------------------------
    long startTime = System.currentTimeMillis();

    // ------------------------------------------------------------------------------
    // selectAction = 0 코너 포인트 좌표를 반환한다
    // 				 = 1 컬러 보정 이미지를 반환한다
    //				 = 2 명도 보정 이미지를 반환한다
    //				 = 3 흑백 보정 이미지를 반환한다

    boolean bOk;

    // ----------------------------
    // 사각형 코너 포인트를 입력하고 문서를 보정한다.
    // 이미지의 코너 포인트는 아래 배열에 입력한다
    // 현재 데모버젼에서는 이전에 엔진으로부터 반환받은 사각형 모서리 좌표를 그대로 입력하는 것으로 구현하였다.
    // 펜타온에서는 아래 좌표를 인터페이스 코너 좌표 수정 입력 부분과 연동시켜 구현할 수 있다.

// 		mConerPoints[0]= mInfo[ 4]; // 사각형 영역 좌하 x 좌표
// 		mConerPoints[1]= mInfo[ 5]; // 사각형 영역 좌하 y 좌표
// 		mConerPoints[2]= mInfo[ 6]; // 사각형 영역 좌상 x 좌표
// 		mConerPoints[3]= mInfo[ 7]; // 사각형 영역 좌상 y 좌표
// 		mConerPoints[4]= mInfo[ 8]; // 사각형 영역 우상 x 좌표
// 		mConerPoints[5]= mInfo[ 9]; // 사각형 영역 우상 y 좌표
// 		mConerPoints[6]= mInfo[10]; // 사각형 영역 우하 x 좌표
// 		mConerPoints[7]= mInfo[11]; // 사각형 영역 우하 y 좌표

    if (selectAction == 4) {
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : start");
      }
      bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 1, 1); // color
//      bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 1, 1); // color
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : end");
      }
    } else if (selectAction == 5) {
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : start");
      }
      bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 1, 3); // black and white
//      bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 1, 3); // black and white
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : end");
      }
    } else {
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : start");
      }
      bOk = BFScanOnline.getInstance().BFScanDocument(dst, src, mInfo, 1, selectAction);
//      bOk = BFScanC.getInstance().EngineScanDocument(dst, src, mInfo, 1, selectAction);
      if (Config.DEBUG) {
        Log.d(TAG,
            "### enginePatternProcessing1() : EngineCroppingImageWithPoints : selectAction == "
                + selectAction + " : end");
      }
    }

    // ------------------------------------------------------------------------------
    if (bOk == false) {
      Log.d(TAG, "### enginePatternProcessing1() : Cant't find corner points!");
      //return false;
    }

    // (TIME CHECK - 종료) ------------------------------------------------------------
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;

    if (engineLoaded) {
      // -------------------------------------------------------------------------------
      // 라이브러리 엔진을 헤재한다
      BFScanOnline.getInstance().BFScanRelease();
//      BFScanC .getInstance().EngineRelease();
      if (Config.DEBUG) {
        Log.d(TAG, "EngineRelease!");
      }
      // ------------------------------------------------------------------------------

    }
    if (Config.DEBUG) {
      Log.d(TAG,
          "### enginePatternProcessing1() : Image " + imageBitmap.getWidth() + "x" + imageBitmap
              .getHeight() + " processed -- " + elapsedTime + "ms");
    }

    Bitmap tempBitmap = null;

    if (selectAction == 0) {
      Bitmap drawBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
      drawCornerPoints(drawBitmap, mInfo);
      return drawBitmap;
    } else if (selectAction == 4) { // RGB  COLOR  -- 24bits (Overlap)  신분증
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGBOverlap : selectAction == "
            + selectAction + " : start");
      }
      tempBitmap = createBitmapFrom0RGBOverlap(dst, dst_w, dst_h, rotated);
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGBOverlap : selectAction == "
            + selectAction + " : end");
      }
    } else {  //일반문서 일경우
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGB : selectAction == "
            + selectAction + " : start");
      }
      tempBitmap = createBitmapFrom0RGB(dst, dst_w, dst_h);
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : createBitmapFrom0RGB : selectAction == "
            + selectAction + " : end");
      }
    }

    if (tempBitmap == null) {
      Log.d(TAG, "### enginePatternProcessing1() : Too Big size image for new image creation!");
      return null;
    } else {
      if (Config.DEBUG) {
        Log.d(TAG, "### enginePatternProcessing1() : tempBitmap.getWidth()[" + tempBitmap.getWidth()
            + "] tempBitmap.getHeight()[" + tempBitmap.getHeight() + "]");
      }
    }
    if (Config.DEBUG) {
      Log.d(TAG, "### enginePatternProcessing1() : After: dst_w= " + dst_w + " dst_h= " + dst_h);
    }
//    createBitmapFile(tempBitmap);
    Log.d(TAG, "enginePatternProcessing: tempBitmap = " + tempBitmap);
//    SystemUtil.saveTestFile(tempBitmap);
    return tempBitmap;
  }

  /*private static void createBitmapFile(Bitmap bitmap) {
    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ghoom");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String path = "img_" + System.currentTimeMillis() + ".jpg";
    File file = new File(dir + "/" + path);

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      FileOutputStream fos = new FileOutputStream(file);
      bitmap.compress(CompressFormat.JPEG, 100, fos);
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }*/
  /*private static void createBitmapFile(byte[] bytes) {
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    createBitmapFile(bitmap);
  }*/
  /*public static Bitmap createBitmapFromGray(byte data[], int width, int height) // data[] -- 1 byte
  {
    int len = width * height;
    int[] raw = null;
    Bitmap bitmap = null;

    if (Config.DEBUG) {
      Log.d(TAG, "createBitmapFromGray() : width[" + width + "] height[" + height + "]");
    }

    try {
      raw = new int[len * 4]; // for 3bytes -- 0RGB
      for (int i = 0; i < len; i++) {
        raw[i] = 0xFF000000 |
            ((data[i] & 0xFF) << 16) |
            ((data[i] & 0xFF) << 8) |
            ((data[i] & 0xFF));
      }

      bitmap = Bitmap.createBitmap(raw, width, height, Bitmap.Config.RGB_565);
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFromGray() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
    }

    data = null;
    raw = null;

    return bitmap;
  }*/
  /*public static Bitmap createBitmapFromGrayOverlap(byte data[], int width, int height,
      boolean rotated) // data[] -- 1 byte
  {
    int len = width * height;
    int[] raw = null;

    if (Config.DEBUG) {
      Log.d(TAG,
          "createBitmapFromGrayOverlap() : rotated[" + rotated + "] width[" + width + "] height["
              + height + "]");
    }

    try {
      raw = new int[len * 4]; // for 3bytes -- 0RGB
      for (int i = 0; i < len; i++) {
        raw[i] = 0xFF000000 |
            ((data[i] & 0xFF) << 16) |
            ((data[i] & 0xFF) << 8) |
            ((data[i] & 0xFF));
      }
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFromGrayOverlap() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
    }

    data = null;

    return createBitmapFromOverlap(raw, width, height, rotated);
  }*/
  /*public static byte[] bitmapToByteArray(Bitmap bitmap) {

    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

    byte[] byteArray = stream.toByteArray();

    return byteArray;

  }*/
  /*public static void saving(Bitmap imageBitmap, String imagePath, Point[] pt, String cameraMode, String colorSelection, String docKind) throws Exception, Throwable
	{

		try {
			if(Config.DEBUG)
			{
				Log.d(TAG, "### saving() : imageBitmap.getWidth()=["+imageBitmap.getWidth()+"] imageBitmap.getHeight()["+imageBitmap.getHeight()+"]");
				Log.d(TAG, "### saving() : imagePath=["+imagePath+"]");
				Log.d(TAG, "### saving() : cameraMode["+cameraMode+"] colorSelection["+colorSelection+"] docKind["+docKind+"]");
			}

			mSelectCorners = 1; // input yes

			if(("A".equalsIgnoreCase(docKind) == false) && ("C".equalsIgnoreCase(cameraMode) == true)) {
				mSelectAction = 4; // overlap
			} else if(("A".equalsIgnoreCase(docKind) == false) && ("B".equalsIgnoreCase(cameraMode) == true)) {
				mSelectAction = 5; // overlap
			} else if("C".equalsIgnoreCase(cameraMode) == true) {
				mSelectAction = 1; // color
			} else {
				mSelectAction = 3; // b/w
			}

			enginePatternProcessing(imageBitmap, imagePath, pt, mSelectAction, cameraMode, colorSelection, docKind);

		} catch (Exception e1) {
			throw e1;
		} finally {
		}
	}*/
  /*public Bitmap createBitmapFromARGB(byte data[], int width,
      int height) // data[] -- 4 byte ABGR data 12m
  {
    int len = width * height;
    int[] raw = null;
    Bitmap bitmap = null;

    if (Config.DEBUG) {
      Log.d(TAG, "createBitmapFromARGB() : width[" + width + "] height[" + height + "]");
    }

    try {
      raw = new int[len * 4]; // for 4bytes -- ARGB
      for (int i = 0; i < len; i++) {
        raw[i] = 0xFF000000 |
            ((data[4 * i + 0] & 0xFF) << 16) |
            ((data[4 * i + 1] & 0xFF) << 8) |
            ((data[4 * i + 2] & 0xFF));
      }

      bitmap = Bitmap.createBitmap(raw, width, height, Bitmap.Config.RGB_565);
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "createBitmapFromARGB() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    data = null;
    raw = null;

    return bitmap;
  }*/
  /*public Bitmap getResizedBitmapForBigSizeImage(Bitmap bm) {
    int nMaxLength = 2400;
    int oh = bm.getHeight();
    int ow = bm.getWidth();

    int nh = 0, nw = 0;
    double scale;
    boolean bResize = false;
    Bitmap resizedBitmap = null;

    if (ow < oh && nMaxLength < oh) {
      nh = nMaxLength;
      scale = (double) ow * (nMaxLength / (double) oh);
      nw = (int) Math.round(scale);
      bResize = true;
    } else if (oh < ow && nMaxLength < ow) {
      nw = nMaxLength;
      scale = (double) oh * (nMaxLength / (double) ow);
      nh = (int) Math.round(scale);
      bResize = true;
    }

    if (bResize == true) {
      try {
        resizedBitmap = Bitmap.createScaledBitmap(bm, nw, nh, true);
        return resizedBitmap;
      } catch (OutOfMemoryError e) {
        e.printStackTrace();
        Log.d(TAG, "getResizedBitmapForBigSizeImage() : 메모리 할당 에러 - 영상이 너무 크다 ");
        return null;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      resizedBitmap = bm;
      return resizedBitmap;
    }
  }*/
  /*public Bitmap getRotatedBitmap(Bitmap bitmap, int angle) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();
    Matrix mtx = new Matrix();
    mtx.postRotate(angle);
    Bitmap rotatedBitmap = null;

    try {
      rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
      Log.d(TAG, "getRotatedBitmap() : 메모리 할당 에러 - 영상이 너무 크다 ");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return rotatedBitmap;
  }*/
  /*public byte[] getARGBbyteData(Bitmap bm) {
    int size = bm.getByteCount();
    ByteBuffer buffer = ByteBuffer.allocate(size);
    bm.copyPixelsToBuffer(buffer);
    byte[] bmppxl = buffer.array();

    byte[] pixels = new byte[size];
    for (int i = 0; i < (bmppxl.length / 4); i++) {
      pixels[i * 4] = bmppxl[i * 4]; // B
      pixels[i * 4 + 1] = bmppxl[i * 4 + 1]; // G
      pixels[i * 4 + 2] = bmppxl[i * 4 + 2]; // R
      // Alpha is discarded
    }

    return pixels; // 3 byte BGR data
  }*/
}


