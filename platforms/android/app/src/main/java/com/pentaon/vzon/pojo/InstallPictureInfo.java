package com.pentaon.vzon.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by Pentaon on 03,7월,2018
 */
public class InstallPictureInfo {

    @SerializedName("Key")
    @Expose
    public String key;

    @SerializedName("Value")
    @Expose
    public File imgFIle;

    public InstallPictureInfo(String key, File imgFIle) {
        this.key = key;
        this.imgFIle = imgFIle;
    }
}
