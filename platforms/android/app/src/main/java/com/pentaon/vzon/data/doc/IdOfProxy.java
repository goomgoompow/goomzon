package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

public class IdOfProxy extends DocInfo implements IEvidentialDoc {

  public IdOfProxy(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'G';
    docType = AppConstants.DOC_KIND_B;
    title = mContext.getString(R.string.doc_proxy_id_card);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_E;
    idType = AppConstants.IDNT_CD_2;
    formId = AppConstants.FORM_ID_L1;
    maxPage = 1;
    idCardType= 1;
    putData();
    return mInfos;
  }
}
