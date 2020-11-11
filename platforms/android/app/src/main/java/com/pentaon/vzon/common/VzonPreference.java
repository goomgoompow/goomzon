package com.pentaon.vzon.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jongHwan.Kim  on 10,8월,2018
 */
public class VzonPreference {
    private final String PREF_NAME = "com.pentaon.vzon.pref";
    public final static String DEVICE_ID = "DEVICE_ID"; // 디바이스ID
    public final static String SECRET_KEY = "SECRET_KEY";
    public final static String ETRY_ID = "ETRY_ID";          // 신청정보ID
    public final static String REQUEST_NAME = "REQUEST_NAME"; // 신청유형(신규, 제신고, 서비스계약)
    public final static String BR_NUMBER = "BR_NUMBER";
    public final static String TARGET_CARD = "TARGET_CARD";
    public final static String EPRNO = "EPRNO";                  // 사업자등록번호
    public final static String LOGIN_IDBOOLEAN = "LOGIN_IDBOOLEAN";
    public final static String ID_NAME = "ID_NAME";
    public final static String EPR_TYPE_MSG = "EPR_TYPE_MSG";
    public final static String FRANCHISE_TYPE = "FRANCHISE_TYPE";
    public final static String EPR_TYPE = "EPR_TYPE"; // 사업자 유형
    //    사업자유형(A - 개인사업자(과세), B - 비사업자, C - 영리법인(본점), D - 비영리법인, E - 국가 기관 및 지방 자치단체,
    // F - 외국법인, G - 영리법인(지점), H - 종교단체, I - 개인사업자(면세))
    //public final static String USED_TARGET_CARD = "USED_TARGET_CARD"; //보여질 카드사 리스트 010010... 형태로 저장하는 변수
    public final static String PRGS_STS_CD = "PRGS_STS_CD"; //신청 상태 코드
    public final static String INPUT_ETRY_END_YN = "INPUT_ETRY_END_YN"; //자료입력 > 기본정보 Y/N
    public final static String INPUT_BIZ_END_YN = "INPUT_BIZ_END_YN"; //자료입력 > 사업장 Y/N
    public final static String INPUT_MEMS_END_YN = "INPUT_MEMS_END_YN";//자료입력 > 가맹점대표 Y/N
    public final static String INPUT_ACNO_END_YN = "INPUT_ACNO_END_YN";//자료입력 > 결제계좌등 Y/N
    public final static String INPUT_SITE_END_YN = "INPUT_SITE_END_YN";//자료입력 > 현장실사 Y/N
    public final static String INPUT_END_YN = "INPUT_END_YN";//자료입력 > 전차자료입력완료 Y/N
    public final static String ATTACH_END_YN = "ATTACH_END_YN";//증빙첨부완료 Y/N
    public final static String AUTOGRAPH_END_YN = "AUTOGRAPH_END_YN";//자필서명 Y/N
    public final static String MERGE_FILE_NAME = "MERGE_FILE_NAME";//미리보기 Merge된 PDF 파일명
    public final static String PDF_DOWNLOAD_COMPLETE = "PDF_DOWNLOAD_COMPLETE"; //미리보기 Merge된 FILE 다운로드 성공 여부
    public final static String EPR_NM = "EPR_NM"; // 상호명 : 사업자등록증 상의 상호명
    // (P0 :개인사업자 대표자본인/P1:개인사업자 대리인/C0:법인사업자 대표자본인/C1:법인사업자 임직원)
    public final static String DRAFT_RPRV_REL = "DRAFT_RPRV_REL";//작성자 대표관계
    public final static String DRAFT_RPRV_REL_NM = "DRAFT_RPRV_REL_NM";//작성자 대표관계
    public final static String EPR_STAT = "EPR_STAT"; // 사업자 휴폐업 구분 - 1: 정상 2: 휴업 3: 폐업 4: 미등록
    public final static String TOTAL_TELECOM_NAME = "TOTAL_TELECOM_NAME";// 제공하는 통신사명 모두 (SKT/KT/LGT), 앱에서만 사용
    public final static String TELECOM_NAMES = "TELECOM_NAMES";// 제공하는 통신사명 모두 (SKT/KT/LGT) 데이터, 앱에서만 사용
    public final static String MEMS_NM = "MEMS_NM";// 가맹점의 간판명
    public final static String DRAFT_NM = "DRAFT_NM";// 작성자 성명
    public final static String USER_NAME = "USER_NAME";//실사자 성명
    public final static String CRPR_NM = "CRPR_NM";// 법인명
    public final static String TOTAL_CRCM_COLPR_CD = "TOTAL_CRCM_COLPR_CD"; // 토탈 모집인 코드
    public static final String DEF_EPR_NM = "DEF_EPR_NM"; // 제신고-가맹점명
    public static final String DEF_RPRV_KRN_NM = "DEF_RPRV_KRN_NM"; //대표자 성명
    public static final String UI_REQUEST_TYPE = "UI_REQUEST_TYPE"; // UI에서 사용 (신규:A, 제신고:B 구분)
    public static final String UI_CAMERA_FILE_NAME = "UI_CAMERA_FILE_NAME"; // UI에서 사용 (다중 증빙첨부카메라파일명)
    public static final String DRAFT_MPNO = "DRAFT_MPNO"; // 작성자 휴대폰 번호
    public static final String SET_SERVER = "SET_SERVER"; // 서버 설정
    public static final String SET_DEBUG = "SET_DEBUG"; // 디버그 설정
    public static final String SET_COMM_DEBUG = "SET_COMM_DEBUG"; // 통신 디버그 설정
    public static final String SET_WIFI = "SET_WIFI"; //와이파이 설정
    public static final String SET_EXETERNAL = "SET_EXETERNAL"; // 외부저장소 설정
    public static final String SET_SERVER_URL = "SET_SERVER_URL"; // 접속할 서버 URL
    public static final String INVESTIGATE_VACCINE="INVESTIGATE_VACCINE";// 백신 검사 시행 여부
    static Context mContext;

    public VzonPreference(Context c) {
        mContext = c;
    }

    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, long value)
    {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public long getValue(String key, long dftValue){
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getLong(key, dftValue);
        }catch (Exception e){
            return dftValue;
        }
    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
