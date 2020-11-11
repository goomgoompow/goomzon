package com.pentaon.vzon.handler;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.ViewfinderResultPointCallback;
import com.google.zxing.client.android.camera.CameraManager;
import com.pentaon.vzon.R;
import com.pentaon.vzon.activity.ScanBarcodeActivity;

import java.util.Collection;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 * Created by jongHwan.Kim  on 06,7월,2018
 */
public final class CaptureHandler extends Handler {
    private final static String TAG = CaptureHandler.class.getSimpleName();
    private final ScanBarcodeActivity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;
    private final CameraManager mCameraManager;

    private enum State{
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureHandler(ScanBarcodeActivity activity,
                          Collection<BarcodeFormat> decodeFormats,
                          Map<DecodeHintType,?> baseHints,
                          String characterSet,
                          CameraManager cameraManager){
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity, decodeFormats,baseHints,characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        mDecodeThread.start();
        mState = State.SUCCESS;

        //Starts ourselves capturing previews and decoding.
        this.mCameraManager = cameraManager;
        mCameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message)
    {
        switch(message.what)
        {
            case R.id.restart_preview:
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
                mState = State.SUCCESS;
                Bundle bundle = message.getData();
                mActivity.handleDecode((Result)message.obj);
                break;
            case R.id.decode_failed:
                mState= State.PREVIEW;
                mCameraManager.requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
                break;
            case R.id.return_scan_result:
                Toast.makeText(mActivity,"return_scan_result" , Toast.LENGTH_SHORT).show();
/*                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();*/
                break;
        }

    }

    public void quitSynchronously() {
        mState = State.DONE;
        mCameraManager.stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            //Wait at most half a second; should be enough time, and onPause() will timeout quickly
            mDecodeThread.join(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //contiune
        }

        //Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }


    private void restartPreviewAndDecode()
    {
        if(mState== State.SUCCESS)
        {
            mState = State.PREVIEW;
            mCameraManager.requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            mActivity.drawViewfinder();
        }
    }


}
