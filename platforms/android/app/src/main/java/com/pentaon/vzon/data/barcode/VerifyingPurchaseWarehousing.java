package com.pentaon.vzon.data.barcode;

import com.pentaon.vzon.pojo.BarcodeInfo;
import com.pentaon.vzon.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jongHwan.Kim  on 25,7월,2018
 * 시리얼 번호- 진위 확인 유형 코드 :: "구매 입고"
 */
public class VerifyingPurchaseWarehousing implements BarcodeVerification {
//    private long mHoldPartyId;
    private long mParamId;
    private long mProdId;

    public VerifyingPurchaseWarehousing(JSONObject obj) {
        try {
//            mHoldPartyId = obj.getLong(AppConstants.HOLD_PARTY_ID);
            mParamId = obj.getLong(AppConstants.PARAM_ID);
            mProdId = obj.getLong(AppConstants.PROD_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BarcodeInfo pickBarcodeInfoOut(String barcode) {
        return new BarcodeInfo.Builder()
                .setParamId(mParamId)
                .setProdId(mProdId)
                .setType(AppConstants.BARCODE_VERIFY_PURCHASE_WAREHOUSING)
                .setSerialNumber(barcode)
                .build()
                ;
    }
}
