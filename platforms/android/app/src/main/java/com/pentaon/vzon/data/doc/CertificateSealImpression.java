package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

/**
 * 증빙문서 - 법인 인감증명서
 */
public class CertificateSealImpression extends DocInfo implements IEvidentialDoc {

  public CertificateSealImpression(Context context) {
    super(context);
  }

  @Override
  public HashMap<String,Object> getEvidentialInfo() {
    contractProof = 'D';
    docType = AppConstants.DOC_KIND_A;
    title = mContext.getString(R.string.doc_certificate_seal_impression);
    color= AppConstants.COLOR_SELECTION_C;
    masking = AppConstants.DOC_MASKING_C;
    formId = AppConstants.FORM_ID_N2;
    maxPage =3;
    putData();
    return mInfos;
  }
}
