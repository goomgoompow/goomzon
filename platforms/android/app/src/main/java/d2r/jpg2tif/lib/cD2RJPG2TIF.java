package d2r.jpg2tif.lib;

public class cD2RJPG2TIF {
    static {
        try {
            System.loadLibrary("AndroidLibForD2RJpg2Tif");
        } catch (SecurityException e) {
            throw e;
        } catch (UnsatisfiedLinkError e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("JniMissingFunction")
    private static native int JNIimg2tif(int imgWidth,
                                         int imgHeight,
                                         int imgBpl,
                                         byte[] imgData,
                                         int optSaveBpp,
                                         int optBWMode,
                                         int optBWThreshold,
                                         String tifFilename);

    @SuppressWarnings("JniMissingFunction")
    private static native int JNIimg2tif2(int imgWidth,
                                          int imgHeight,
                                          int imgBpl,
                                          int[] imgData,
                                          String tifFilename);

    // ------------------------------------------------------------------------
    /*
    INPUT :
		imgWidth : image width
		imgHeight : image height
		imgBpl : image BytesPerLine
		imgData : image buffer
		optSaveBpp : [1 : Black&White] [8 : 8bits Gray] [24 : 24bits Color]
		optBWMode : [0 : Scatter] [1 : Threshold]
				- if (optSaveBpp : [1 : Black&White])
		optBWThreshold : 1~255 (if imgData[i] < optThreshold, Black)(else White)
				- if (optMode : [1 : Threshold])
		tifFilename : tif filepath
	RETURN :
		[1 : success]
		[0 : input err]
		[-1 : memory err]
		[-2 : converting b/w err]
		[-3 : BitsPerPixel err]
		[-4 : saving tif err]
	*/
    public static int img2tif(int imgWidth,
                              int imgHeight,
                              int imgBpl,
                              byte[] imgData,
                              int optSaveBpp,
                              int optBWMode,
                              int optBWThreshold,
                              String tifFilename) throws Exception, Throwable {
        int ret = 0;

        try {
            ret = JNIimg2tif(imgWidth, imgHeight, imgBpl, imgData, optSaveBpp, optBWMode, optBWThreshold, tifFilename);
        } finally {
        }

        return ret;
    }

    // from 32bits Integer Image, to 24bits TIF
    public static int img2tif2(int imgWidth,
                               int imgHeight,
                               int imgBpl,
                               int[] imgData,
                               String tifFilename) throws Exception, Throwable {
        int ret = 0;

        try {
            ret = JNIimg2tif2(imgWidth, imgHeight, imgBpl, imgData, tifFilename);
        } finally {
        }

        return ret;
    }
}
