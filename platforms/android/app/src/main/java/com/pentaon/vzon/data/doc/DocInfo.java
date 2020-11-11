package com.pentaon.vzon.data.doc;

import android.content.Context;
import com.pentaon.vzon.utils.AppConstants;
import java.util.HashMap;

public class DocInfo {
  protected Context mContext;
  protected HashMap<String, Object> mInfos = new HashMap<>();
  protected String docType=""; //문서 타입
  protected String title = ""; //카메라 preview 화면 상단 타이틀
  protected String color= ""; // 촬영된 사진 흑백모드
  protected String masking = ""; // 마스킹 모드
  protected String idType=""; //(신분증일 경우) 신분증 타입
  protected String formId = ""; // 문서 양식
  protected char contractProof;
  protected int maxPage = -1; //최대 장수
  protected int idCardType = 0;
  protected boolean usableGallery = false; //갤러리에서 이미지 불러오기 기능 사용 여부

  public DocInfo(Context context) {
    this.mContext = context;
  }

  protected void putData()
  {
    mInfos.put(AppConstants.INTENT_EXTRA_CONTRACT_PROOF,contractProof);
    mInfos.put(AppConstants.INTENT_EXTRA_DOC_KIND,docType);
    mInfos.put(AppConstants.INTENT_EXTRA_TITLE,title);
    mInfos.put(AppConstants.INTENT_EXTRA_COLOR_SELECTION,color);
    mInfos.put(AppConstants.INTENT_EXTRA_DOC_MASKING,masking);
    mInfos.put(AppConstants.FORM_ID,formId);
    mInfos.put(AppConstants.INTENT_EXTRA_DOC_MAX_PAGE,99);
    mInfos.put(AppConstants.INTENT_EXTRA_SEL_CARD_IDX,idCardType);
    mInfos.put(AppConstants.INTENT_EXTRA_USABLE_GALLERY,usableGallery);
    if(docType == AppConstants.DOC_KIND_B)
    {
      mInfos.put(AppConstants.IDNT_CD,idType);
    }
  }
}
