package com.pentaon.vzon.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.pentaon.vzon.R;
import com.pentaon.vzon.activity.ScanBarcodeActivity;

import java.util.Map;

/**
 * Created by jongHwan.Kim  on 06,7월,2018
 */
public class DecodeHandler extends Handler {
    private final static String TAG = DecodeHandler.class.getSimpleName();

    private final ScanBarcodeActivity mActivity;
    private final MultiFormatReader mMultiFormatReader;
    private boolean mIsRunning = true;

    public DecodeHandler(ScanBarcodeActivity activity, Map<DecodeHintType, Object> hints) {
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
        this.mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg==null ||!mIsRunning) return;
        switch (msg.what)
        {
            case R.id.decode:
                decode((byte[])msg.obj, msg.arg1, msg.arg2);
                break;
            case R.id.quit:
                mIsRunning = false;
                Looper.myLooper().quit();
                break;
        }

    }
    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start=  System.currentTimeMillis();
        Result rawResult= null;

        byte[] rotationData = new byte[data.length];
        for(int y= 0; y<height; y++)
        {
            for(int x = 0; x<width; x++)
            {
                rotationData[x*height + height-y-1] =data[x+y*width];
            }
        }

        int tmp = width;
        width = height;
        height = tmp;
        data =rotationData;

        PlanarYUVLuminanceSource source = mActivity.getCameraManager().buildLuminanceSource(data,width,height);
        if(source !=null)
        {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = mMultiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException e) {
                e.printStackTrace();
            }finally {
                mMultiFormatReader.reset();
            }
        }

        Handler handler= mActivity.getHandler();
        if(rawResult!=null)
        {
            long end = System.currentTimeMillis();
            if(handler !=null)
            {
                Message message= Message.obtain(handler, R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(source, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        }else
        {
            if (handler != null){
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }

    }

    private void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
       /* int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(com.google.zxing.client.android.DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(com.google.zxing.client.android.DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());*/
    }
}
