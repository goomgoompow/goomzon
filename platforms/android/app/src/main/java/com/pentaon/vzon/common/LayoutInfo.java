package com.pentaon.vzon.common;

public class LayoutInfo {

    private int mTopHeight;
    private int mBackTopHeight;
    private int mCornerBackHeight;
    private int mHorizontalMargin;
    private int mVerticalMargin;



    private static class LazyHolder
    {
        public final static LayoutInfo INSTANCE = new LayoutInfo();
    }

    public int getTopHeight() {
        return mTopHeight;
    }

    public void setTopHeight(int mTopHeight) {
        this.mTopHeight = mTopHeight;
    }

    public int getBackTopHeight() {
        return mBackTopHeight;
    }

    public void setBackTopHeight(int mBackTopHeight) {
        this.mBackTopHeight = mBackTopHeight;
    }

    public int getCornerBackHeight() {
        return mCornerBackHeight;
    }

    public void setCornerBackHeight(int mCornerBackHeight) {
        this.mCornerBackHeight = mCornerBackHeight;
    }

    public static LayoutInfo getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public void setHorizontalMargin(int horizontalMargin) {
        mHorizontalMargin = horizontalMargin;
    }
    public int getHorizontalMargin(){return mHorizontalMargin; }
    public int getVerticalMargin(){return mVerticalMargin; }

    public void setVerticalMargin(int verticalMargin) {
        mVerticalMargin = verticalMargin;
    }

}
