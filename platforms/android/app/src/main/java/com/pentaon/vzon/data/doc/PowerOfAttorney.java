package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 위임장
 */
public class PowerOfAttorney extends DocInfo implements IEvidentialDoc {

  public PowerOfAttorney(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'F';
    docType = AppConstants.DOC_KIND_A;
    title = mContext.getString(R.string.doc_letter_attorney);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_N;
    formId = AppConstants.FORM_ID_J7;
    maxPage = 1;
    putData();
    return mInfos;
  }
}
