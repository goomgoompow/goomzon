package com.pentaon.vzon.network;

import com.pentaon.vzon.pojo.BarcodeInfo;
import com.pentaon.vzon.pojo.BarcodeResult;
import com.pentaon.vzon.pojo.PictureResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by Pentaon on 15,6월,2018
 */
public interface ApiInterface {
    @PUT("api/barcode/verify")
    Call<BarcodeResult> doVerifySerialNumber(@Body BarcodeInfo barcodeInfo);

    @POST("api/file/picture/install")
    Call<PictureResult> uploadInstallPicture(@Body RequestBody requestBody);

    @POST("/api/file/document/install")
    Call<PictureResult> uploadInstallDocument(@Body RequestBody requestBody);


    @POST("api/file/picture/contract")
    Call<PictureResult> uploadContractPicture(@Body RequestBody requestBody);
}
