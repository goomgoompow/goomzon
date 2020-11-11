package com.pentaon.vzon.manager;

import com.pentaon.vzon.data.WidthHeight;
import com.valmore.aireader.Constants;

public class ScreenInfoManager {

    private WidthHeight mDisplayMetrics;//width, height pixels
    private WidthHeight mDisplaySize; //real display size
    private WidthHeight mPreviewSize; // optimal preview size
    private int mImageRotation= Constants.NOT_DEFINED; // image rotation value


    public static ScreenInfoManager getInstance()
    {
        return LazyHolder.INS;
    }

    private static class LazyHolder
    {
        private static ScreenInfoManager INS = new ScreenInfoManager();
    }

    public WidthHeight getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public void setDisplayMetrics(WidthHeight metrics) {
        this.mDisplayMetrics = metrics;
    }

    public WidthHeight getRealDisplaySize() {
        return mDisplaySize;
    }

    /**
     * Device의 Real display size 정보를 가져옴( windowManager.getDefaultDisplay().getRealSize(new Point());
     * @param size
     */
    public void setRealDisplaySize(WidthHeight size) {
        this.mDisplaySize = size;
    }

    public WidthHeight getPreviewSize() {
        return mPreviewSize;
    }

    /**
     * Camera parameter로부터 계산된 optimal preview Size를 저장
     * @param size
     */
    public void setPreviewSize(WidthHeight size) {
        this.mPreviewSize = size;
    }

    public int getImageRotation() {
        return mImageRotation;
    }

    /**
     * gallery에서 가져온 이미지의 rotation 정보
     * @param ration
     */
    public void setImageRotation(int ration) {
        this.mImageRotation = ration;
    }
}
