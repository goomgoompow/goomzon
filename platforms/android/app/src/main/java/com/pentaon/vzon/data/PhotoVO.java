package com.pentaon.vzon.data;

import java.io.Serializable;

public class PhotoVO implements Serializable {

  private String mImagePath;
  private int mRotation;

  public PhotoVO(String path) {
    this.mImagePath = path;
  }

  public PhotoVO(String path, int rotation) {
    this.mImagePath = path;
    this.mRotation = rotation;
  }

  public String getImagePath(){return mImagePath;}
  public void setImagePath(String path){mImagePath = path;}

  public int getRotation() {
    return mRotation;
  }

  public void setRotation(int rotation) {
    this.mRotation = rotation;
  }

}
