package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서- 매장 사진
 */
public class PictureOfStore extends DocInfo implements IEvidentialDoc {
  public PictureOfStore(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'H';
    docType = AppConstants.DOC_KIND_E;
    title = mContext.getString(R.string.doc_pictures_the_store);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_N;
    formId = AppConstants.FORM_ID_A3;
    maxPage = 2;
    idCardType=1;
    putData();
    return mInfos;
  }
}
