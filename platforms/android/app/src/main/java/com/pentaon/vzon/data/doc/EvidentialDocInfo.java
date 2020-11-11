package com.pentaon.vzon.data.doc;

import android.content.Context;

import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;

import java.util.HashMap;

/**
 * 증빙 문서 관련 인포
 * Created by jongHwan.Kim  on 30,7월,2018
 */

@Deprecated
public class EvidentialDocInfo {

    private Context mContext;

    public EvidentialDocInfo(Context context){
        mContext = context;
    }

    public HashMap<String, Object> getEvidentialDocInfo(String type)
    {
        HashMap<String, Object> info = new HashMap<>();
        String docType="";
        String title = "";
        String color= "";
        String masking = "";
        String idType="";
        String formId = "";
        int maxPage = -1;
        int idCardType = 0;

        switch (type)
        {
            case AppConstants.CONTRACT_PROOF_A: //사업자 등록증
                docType = AppConstants.DOC_KIND_A;
                title = mContext.getString(R.string.doc_business_license);
                color= AppConstants.COLOR_SELECTION_B;
                masking = AppConstants.DOC_MASKING_N;
                formId = AppConstants.FORM_ID_A1;
                maxPage = 5;
                break;

            case AppConstants.CONTRACT_PROOF_B: //대표자 신분증
                docType = AppConstants.DOC_KIND_B;
                title = mContext.getString(R.string.doc_representative_id_card);
                color= AppConstants.COLOR_SELECTION_C;
                masking = AppConstants.DOC_MASKING_E;
                idType = AppConstants.IDNT_CD_1;
                formId = AppConstants.FORM_ID_J1;
                maxPage = 1;
                idCardType= 1;
                break;

            case AppConstants.CONTRACT_PROOF_C://통장 사본
                docType = AppConstants.DOC_KIND_D;
                title = mContext.getString(R.string.doc_copy_bankbook);
                color= AppConstants.COLOR_SELECTION_C;
                masking = AppConstants.DOC_MASKING_N;
                formId = AppConstants.FORM_ID_A2;
                maxPage = 1;
                break;

            case AppConstants.CONTRACT_PROOF_D://법인 인감 증명서
                docType = AppConstants.DOC_KIND_A;
                title = mContext.getString(R.string.doc_certificate_seal_impression);
                color= AppConstants.COLOR_SELECTION_B;
                masking = AppConstants.DOC_MASKING_C;
                formId = AppConstants.FORM_ID_N2;
                maxPage =3;
                break;

            case AppConstants.CONTRACT_PROOF_E://법인 등기부 등본
                docType = AppConstants.DOC_KIND_A;
                title = mContext.getString(R.string.doc_certified_copy_corporate_register);
                color= AppConstants.COLOR_SELECTION_B;
                masking = AppConstants.DOC_MASKING_C;
                formId = AppConstants.FORM_ID_N1;
                maxPage = 20;
                break;

            case AppConstants.CONTRACT_PROOF_F://위임장
                docType = AppConstants.DOC_KIND_A;
                title = mContext.getString(R.string.doc_letter_attorney);
                color= AppConstants.COLOR_SELECTION_B;
                masking = AppConstants.DOC_MASKING_N;
                formId = AppConstants.FORM_ID_J7;
                maxPage = 1;
                break;

            case AppConstants.CONTRACT_PROOF_G://위임장 신분증
                docType = AppConstants.DOC_KIND_B;
                title = mContext.getString(R.string.doc_proxy_id_card);
                color= AppConstants.COLOR_SELECTION_C;
                masking = AppConstants.DOC_MASKING_E;
                idType = AppConstants.IDNT_CD_2;
                formId = AppConstants.FORM_ID_L1;
                maxPage = 1;
                idCardType= 1;
                break;
            case AppConstants.CONTRACT_PROOF_H: //매장 사진
                docType = AppConstants.DOC_KIND_E;
                title = mContext.getString(R.string.doc_pictures_the_store);
                color= AppConstants.COLOR_SELECTION_C;
                masking = AppConstants.DOC_MASKING_N;
                formId = AppConstants.FORM_ID_A3;
                maxPage = 10; //2
                idCardType=1;
                break;
            /*case AppConstants.STOCKHOLDER_LIST:
                docType = DOC_COMMON;
                title = mContext.getString(R.string.doc_stockholder_list);
                index = "0";
                color=BLACK_WHITHE;
                masking = MASKING_NONE;
                break;*/

            default:
                docType = AppConstants.DOC_KIND_A;
                title = mContext.getString(R.string.doc_business_license);
                color= AppConstants.COLOR_SELECTION_B;
                masking = AppConstants.DOC_MASKING_N;
                formId = AppConstants.FORM_ID_A1;
                maxPage = 5;
        }


        info.put(AppConstants.INTENT_EXTRA_DOC_KIND,docType);
        info.put(AppConstants.INTENT_EXTRA_TITLE,title);
        info.put(AppConstants.INTENT_EXTRA_COLOR_SELECTION,color);
        info.put(AppConstants.INTENT_EXTRA_DOC_MASKING,masking);
        info.put(AppConstants.FORM_ID,formId);
        info.put(AppConstants.INTENT_EXTRA_DOC_MAX_PAGE,maxPage);
        info.put(AppConstants.INTENT_EXTRA_SEL_CARD_IDX,idCardType);

        if(docType.equals(AppConstants.DOC_KIND_B))
        {
            info.put(AppConstants.IDNT_CD,idType);
        }

        return info;
    }
}
