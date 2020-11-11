package com.pentaon.vzon.transaction;

/**
 * Created by jh.Kim on 15,5월,2018
 * * <pre>
 * JSON Object name define (서버 API와 통신시 입출력 파라미터)
 * PDF Data field define (PDF 솔루션에 Data set시에 입력 파라미터)
 * </pre>
 */
public class Vzon {

    public static final String DOC_MASKING = "INTENT_EXTRA_DOC_MASKING"; //마스킹 구분(E : 필수, C : 선택, N : 사용하지 않음)
    public static final String FORM_ID = "FORM_ID"; // 폼(양식) 아이디
    public static final String SELCARDIDX ="SELCARDIDX";//신분증 인덱스

    public static final String OPT_CODE = "OPT_CODE";
    public static final String OPT_NAME = "OPT_NAME";

}
