package com.pentaon.vzon.data;

import android.graphics.Point;
import android.hardware.Camera;

public class WidthHeight {
    private int width;
    private int height;

    public WidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public WidthHeight(Camera.Size size){
        this.width=size.width;
        this.height= size.height;
    }

    public static WidthHeight convertTo(Point point)
    {
        return new WidthHeight(point.x, point.y);
    }

    public boolean isEquals(WidthHeight widthHeight){
        return (width==widthHeight.width&& height==widthHeight.height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
