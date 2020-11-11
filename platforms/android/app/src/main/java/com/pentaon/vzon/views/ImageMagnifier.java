package com.pentaon.vzon.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.pentaon.vzon.R;

@SuppressLint("AppCompatCustomView")
public class ImageMagnifier extends ImageView {

  private static final int SIZE_MAGNIFIER = 250;
  private static final String TAG ="ImageMagnifier";
  private PointF mZoomPos;
  private Matrix mMatrix;
  private Paint mPaint;
  private boolean mIsZooming;
  private Bitmap mBitmap;
  private BitmapShader mShader;

  //=========================================================
  // Constructors
  //=========================================================
  public ImageMagnifier(Context context) {
    super(context);
    init();
  }

  public ImageMagnifier(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ImageMagnifier(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  //=========================================================
  // Override methods
  //=========================================================
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.d(TAG, "onDraw: mIsZooming = "+mIsZooming);
    if(!mIsZooming)
    {
      buildDrawingCache();
    }else
    {
      mBitmap = getDrawingCache();
      mShader = new BitmapShader(mBitmap, TileMode.CLAMP, TileMode.CLAMP);
      mPaint = new Paint();
      mPaint.setShader(mShader);
      mMatrix.reset();
      mMatrix.postScale(2f,2f,mZoomPos.x , mZoomPos.y);
      mPaint.getShader().setLocalMatrix(mMatrix);
      canvas.drawCircle(mZoomPos.x,mZoomPos.y, SIZE_MAGNIFIER,mPaint);
    }
  }

/*
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    mZoomPos.x = event.getX();
    mZoomPos.y = event.getY();


    switch (action)
    {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_MOVE:
        mIsZooming = true;
        invalidate();
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        mIsZooming = false;
        invalidate();
        break;
      default:
        break;
    }
    return false;
  }
*/

  //=========================================================
  // public methods
  //=========================================================


  //=========================================================
  // private methods
  //=========================================================
  private void init()
  {
    mZoomPos = new PointF(0,0);
    mMatrix = new Matrix();
    mPaint = new Paint();
  }

}
