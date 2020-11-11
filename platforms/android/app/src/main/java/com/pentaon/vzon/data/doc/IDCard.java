package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 대표자 신분증
 */
public class IDCard extends DocInfo implements IEvidentialDoc {

  public IDCard(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'B';
    docType = AppConstants.DOC_KIND_B;
    title = mContext.getString(R.string.doc_representative_id_card);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_E;
    idType = AppConstants.IDNT_CD_1;
    formId = AppConstants.FORM_ID_J1;
    maxPage = 1;
    idCardType= 1;
    putData();
    return mInfos;
  }
}
