package com.pentaon.vzon.data.barcode;

import com.pentaon.vzon.pojo.BarcodeInfo;
import com.pentaon.vzon.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jongHwan.Kim  on 25,7월,2018
 * 시리얼 번호- 진위 확인 유형 코드 :: "출고"
 */
public class VerifyingShipmentIssue implements BarcodeVerification {
    private long mHoldPartyId;
    private long mProdId;

    public VerifyingShipmentIssue(JSONObject obj){
        try {
            mHoldPartyId  = obj.getLong(AppConstants.HOLD_PARTY_ID);
            mProdId  = obj.getLong(AppConstants.PROD_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BarcodeInfo pickBarcodeInfoOut(String barcode) {
        return new BarcodeInfo.Builder()
                .setHoldPartyId(mHoldPartyId)
                .setProdId(mProdId)
                .setType(AppConstants.BARCODE_VERIFY_SHIPMENT_ISSUE)
                .setSerialNumber(barcode)
                .build();
    }
}
