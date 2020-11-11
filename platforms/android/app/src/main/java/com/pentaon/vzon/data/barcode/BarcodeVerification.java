package com.pentaon.vzon.data.barcode;

import com.pentaon.vzon.pojo.BarcodeInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jongHwan.Kim  on 25,7월,2018
 * 바코드(시리얼 번호) 진위 확인 유형 코드
 */
public interface BarcodeVerification {
    BarcodeInfo pickBarcodeInfoOut(String barcode);
}
