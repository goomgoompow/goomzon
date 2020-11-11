package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 설치확인서
 */
public class PictureOfInstall extends DocInfo implements IEvidentialDoc {

  public PictureOfInstall(Context context) {
    super(context);
  }

  @Override
  public HashMap<String, Object> getEvidentialInfo() {
    contractProof = 'I';
    docType = AppConstants.DOC_KIND_A;
    title = mContext.getString(R.string.doc_installation_confirm);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_N;
    formId = AppConstants.FORM_ID_A1;
    maxPage = 1;
    putData();
    return mInfos;
  }
}
