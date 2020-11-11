package com.pentaon.vzon.data.barcode;

import com.pentaon.vzon.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jongHwan.Kim  on 25,7월,2018
 */
public class VerificationAssorter {

    private String mType;
    private JSONObject mObj;
    private BarcodeVerification mVerifier;
    
    public VerificationAssorter(JSONObject obj) {
        try {
            mType= obj.getString(AppConstants.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.mObj = obj;
    }

    public BarcodeVerification getBarcodeVerifier()
    {
        switch (mType)
        {
            case AppConstants.BARCODE_VERIFY_PURCHASE_WAREHOUSING:
                mVerifier =new VerifyingPurchaseWarehousing(mObj);
                break;
            case AppConstants.BARCODE_VERIFY_MERCHANT_INSTALL:
                mVerifier =new VerifyingMerchantInstall(mObj);
                break;
            case AppConstants.BARCODE_VERIFY_RETURN_INSTALL:
                mVerifier = new VerifyingReturnInstall(mObj);
                break;
            case AppConstants.BARCODE_VERIFY_RETURN_ORDER_RETRIEVAL:
                mVerifier = new VerifyingReturnOrderRetrieval(mObj);
                break;
            case AppConstants.BARCODE_VERIFY_REPAIR_ITEM:
                mVerifier = new VerifyingRepairItem(mObj);
                break;
            case AppConstants.BARCODE_VERIFY_SHIPMENT_ISSUE:
                mVerifier = new VerifyingShipmentIssue(mObj);
                break;
            default:

        }
        return mVerifier;
    }
}
