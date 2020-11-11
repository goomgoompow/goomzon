package com.pentaon.vzon.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.manager.TokenManager;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pentaon on 15,6월,2018
 */
public class ApiClient {
    private static final String KEY_HEADER ="vzon_access_token";
    private static Retrofit mRetrofit = null;
    private static String mAccessToken = "";

    public static Retrofit getClient()
    {
        mAccessToken = TokenManager.getInstance().getToken();
        if(mAccessToken==null)
        {
            mAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ2enUiOiJJQDFAN0BIQDMiLCJpc3MiOiJodHRwczpcL1wvd3d3LnZ6b24uY29tIiwiZXhwIjoxNTI4MjYyMjQxLCJpYXQiOjE1MjgxNzU4NDF9.yppAh4loM-j7IP1XU0mNCVJtMID03usNQjM4j9fdF-0_Dw-WgPTeTxFiVLc18YQqR5d_Faxl3cwGr3ljVL_YJv-CTYf_-gCCagklz9OWigoYV6EkYVQ1utm6Fnbty4SpSCCnPhNek6HelQRFv58PIvorV8QZiH99DsQSdclSez8";
        }
        Interceptor headerAuthorizationInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Headers headers = request.headers()
                        .newBuilder()
                        .add(KEY_HEADER,mAccessToken)
                        .build();
                request  = request.newBuilder()
                        .headers(headers)
                        .build();
                return chain.proceed(request);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(headerAuthorizationInterceptor)
                            .build();
        mRetrofit = new Retrofit.Builder().baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return mRetrofit;
    }
}
