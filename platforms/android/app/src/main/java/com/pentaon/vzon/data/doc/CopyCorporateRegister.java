package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 법인 등기부 등본
 */
public class CopyCorporateRegister extends DocInfo implements IEvidentialDoc {

  public CopyCorporateRegister(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'E';
    docType = AppConstants.DOC_KIND_A;
    title = mContext.getString(R.string.doc_certified_copy_corporate_register);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_C;
    formId = AppConstants.FORM_ID_N1;
    maxPage = 20;
    putData();
    return mInfos;
  }
}
