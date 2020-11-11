package com.pentaon.vzon.manager;

/**
 * Created by jongHwan.Kim  on 11,7월,2018
 */
public class TokenManager {

    private String mToken;

    private TokenManager(){}
    public  static TokenManager getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final TokenManager INSTANCE = new TokenManager();
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }
}
