package com.pentaon.vzon.data.barcode;

import com.pentaon.vzon.pojo.BarcodeInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jongHwan.Kim  on 25,7월,2018
 */
public class VerifyingshipmentCompare implements BarcodeVerification {

    public VerifyingshipmentCompare(JSONObject obj) {
    }

    @Override
    public BarcodeInfo pickBarcodeInfoOut(String barcode) {
        return null;
    }
}
