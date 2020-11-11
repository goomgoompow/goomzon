package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 사업자 등록증
 */
public class BusinessLicense extends DocInfo implements IEvidentialDoc {
  public BusinessLicense(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'A';
    docType = AppConstants.DOC_KIND_A;
    title = mContext.getString(R.string.doc_business_license);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_N;
    formId = AppConstants.FORM_ID_A1;
    maxPage = 5;
    putData();
    return mInfos;
  }
}
