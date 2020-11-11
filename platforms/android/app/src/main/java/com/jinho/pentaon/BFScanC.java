package com.jinho.pentaon;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class BFScanC {

    private static BFScanC mInstance;
    private BFScanC()
    {
        System.loadLibrary("BFScanC");
    }

    public static BFScanC getInstance()
    {
        if(mInstance==null) mInstance = new BFScanC();
        return mInstance;
    }

    public native boolean EngineInit();
    public native void    EngineRelease();

    public native boolean EngineScanIDCard(  byte[] dst, byte[] src, int info[],
                                             int corner, int color,
                                             int[] region1, int[] region2, int[] region3, int[] region4, int[] region5, int[] region6,
                                             int[] recchr1, int[] recchr2, int[] recchr3, int[] recchr4, int[] recchr5
    );

    public native boolean EngineScanDocument(byte[] dst, byte[] src, int info[], int corner, int color);
}
