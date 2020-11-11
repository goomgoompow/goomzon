package com.pentaon.vzon.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pentaon on 03,7월,2018
 */
public class PictureResult {

    @SerializedName("_embedded")
    @Expose
    public Embedded embedded;

    public class Embedded
    {
        @SerializedName("links")
        @Expose
        public List<Object> links = null;
        @SerializedName("content")
        @Expose
        public List<Content> content = null;

        public class Content
        {
            @SerializedName("creatDt")
            @Expose
            public Long creatDt;
            @SerializedName("updatDt")
            @Expose
            public Long updatDt;
            @SerializedName("fileKey")
            @Expose
            public String fileKey;
            @SerializedName("fileName")
            @Expose
            public String fileName;
            @SerializedName("links")
            @Expose
            public List<Link> links = null;
            public class Link{
                @SerializedName("rel")
                @Expose
                public String rel;
                @SerializedName("href")
                @Expose
                public String href;
            }
        }
    }

}
