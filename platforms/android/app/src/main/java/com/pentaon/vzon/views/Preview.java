package com.pentaon.vzon.views;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by Pentaon on 27,6월,2018
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback{

    private static final String TAG = "preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSize;
    Camera mCamera;

    public Preview(Context context, SurfaceView surfaceView) {
        super(context);
        this.mSurfaceView = surfaceView;

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera)
    {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the mPreview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the mCamera for use by other
            // applications. Applications should release the mCamera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
        mCamera =camera;
        if(mCamera !=null)
        {
            mSupportedPreviewSize = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            Camera.Parameters params = mCamera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(params);
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Important : Call StartPreview() to start updating the preview
            //surface. Preview must be started before you can take a picture.
            mCamera.startPreview();

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width,height);

        if(mSupportedPreviewSize !=null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSize, width, height);
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
           final double ASPECT_TOLERANCE= 0.1;
           double targetRatio = (double)width/height;
           if(sizes==null) return null;

           Size optimalSize = null;
           double minDiff = Double.MAX_VALUE;

           int targetHeight = height;

           for(Size size : sizes)
           {
               double ratio = (double)size.width/size.height;
               if(Math.abs(ratio-targetRatio)>ASPECT_TOLERANCE) continue;
               if(Math.abs(size.height-targetHeight)<minDiff)
               {
                   optimalSize = size;
                   minDiff = Math.abs(size.height-targetHeight);
               }
           }

           //cannot find the one match the aspect ratio, ignore the requirement
           if(optimalSize==null)
           {
               minDiff= Double.MAX_VALUE;
               for(Size size : sizes){
                   if(Math.abs(size.height-targetHeight)<minDiff)
                   {
                       optimalSize = size;
                       minDiff = Math.abs(size.height-targetHeight);
                   }
               }
           }


           return  optimalSize;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed && getChildCount()>0)
        {
            final View child = getChildAt(0);

            final int width = r-l;
            final int height = b-t;

            int previewWidth = width;
            int previewHeight = height;
            if(mPreviewSize !=null)
            {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // center the child SurfaceView within the parent
            if(width*previewHeight>height*previewWidth)
            {
                final int scaleChildWidth = previewWidth*height/previewHeight;
                child.layout((width-scaleChildWidth)/2,0,(width*scaleChildWidth)/2,height);
            }else{
                final int scaledChildHeight = previewHeight*width/previewWidth;
                child.layout(0,(height-scaledChildHeight)/2, width, (height*scaledChildHeight)/2);
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        // the surface has been created, acquire the camera and tell it where to draw
        try {
            if(mCamera!=null)  mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(mCamera!=null)
        {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Size> allSize = parameters.getSupportedPreviewSizes();
            Size size = allSize.get(0); //get top size
            for(Size s : allSize)
            {
                if(s.width>size.width) size = s;
            }
            //set max preview size
            parameters.setPreviewSize(size.width, size.height);
            mCamera.startPreview();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera!=null) mCamera.stopPreview();
    }
}
