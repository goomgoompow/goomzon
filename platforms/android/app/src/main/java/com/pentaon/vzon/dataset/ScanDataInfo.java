package com.pentaon.vzon.dataset;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by jh.kim on 15,5월,2018
 *
 * 제출용 카드정보 class
 * PDF를 만들거나 보여주거나 PDF 전송을 위해 사용
 */
public class ScanDataInfo {


    private Bitmap mBitmap;
    private Point[] retpt;

    public ScanDataInfo() {
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Point[] getPoint() {
        return retpt;
    }

    public void setPoint(Point[] retpt) {
        this.retpt = retpt;
    }

}
