package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 통장사본
 */
public class CopyOfBankbook extends DocInfo implements IEvidentialDoc {

  public CopyOfBankbook(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'C';
    docType = AppConstants.DOC_KIND_D;
    title = mContext.getString(R.string.doc_copy_bankbook);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_N;
    formId = AppConstants.FORM_ID_A2;
    maxPage = 1;
    putData();
    return mInfos;
  }
}
