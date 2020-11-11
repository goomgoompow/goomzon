package com.pentaon.vzon.utils;

/**
 * Created by jh.Kim on 14,5월,2018
 */
public class AppConstants {
    //A4: 210 x296 (mm) mm 2 px = (210*density)/25.4
    public static final int PAPER_SIZE_MIN = 1200; // A4 1652 ,1200
    public static final int PAPER_SIZE_MAX = 1704; // A4 2336 ,1704
    public static final int ID_CARD_SIZE_MIN= 540;
    public static final int ID_CARD_SIZE_MAX= 856;
    public static final String INTENT_EXTRA_ROOT_PATH = "com.pentaon.vzon.rootPath";
    public static final String INTENT_EXTRA_TEMP_PATH = "com.pentaon.vzon.tempPath";
    public static final String INTENT_EXTRA_COLOR_SELECTION = "com.pentaon.vzon.colorSelection";
    public static final String INTENT_EXTRA_CONTRACT_PROOF = "com.pentaon.vzon.contractProof";
    public static final String INTENT_EXTRA_DOC_KIND = "com.pentaon.vzon.docKind";
    public static final String INTENT_EXTRA_CAMERA_MODE = "com.pentaon.vzon.cameraMode";
    public static final String INTENT_EXTRA_IMAGE_PATH = "com.pentaon.vzon.imagePath";
    public static final String INTENT_EXTRA_IMAGE_TEMP_PATH = "com.pentaon.vzon.imageTempPath";
    public static final String INTENT_EXTRA_SCAN_USER_CANCEL = "com.pentaon.vzon.scanUserCancel";
    public static final String INTENT_EXTRA_SCAN_CANCEL ="com.pentaon.vzon.scanCancel";
    public static final String INTENT_EXTRA_TO_SCAN_BARCODE_ACT = "com.pentaon.vzon.scanBarcodeTo";
    public static final String INTENT_EXTRA_SCAN_BARCODE_ALLOW_REGISTER = "com.pentaon.vzon.scanBarcodeAllowRegister";
    public static final String INTENT_EXTRA_FROM_SCAN_BARCODE_ACT = "com.pentaon.vzon.scanBarcodeFrom";
    public static final String INTENT_EXTRA_FROM_INSTALLATION_ACT = "com.pentaon.vzon.installationActFrom";
    public static final String INTENT_EXTRA_ATTACH_POSITION = "com.pentaon.vzon.attachPosition";
    public static final String INTENT_EXTRA_FILE_NAME = "com.pentaon.vzon.fileName";
    public static final String INTENT_EXTRA_TOTAL_PAGE = "com.pentaon.vzon.totalPage";
    public static final String INTENT_EXTRA_SEL_CARD_IDX = "com.pentaon.vzon.selCardIdx";
    public static final String INTENT_EXTRA_SELECT_DOCUMENTARY_INFO_MAP = "com.pentaon.vzon.selectDocumentaryInfoMap";
    public static final String INTENT_EXTRA_TITLE = "com.pentaon.vzon.title";
    public static final String INTENT_EXTRA_DOC_MASKING = "com.pentaon.vzon.docMasking"; // 마스킹 구분 (E : 필수 -> 정형, C : 선택 -> 비정형, N : 사용하지 않음)
    public static final String INTENT_EXTRA_FROM_MAIN = "com.pentaon.vzon.fromMainAct";
    public static final String INTENT_EXTRA_NUM_PICTURE = "com.pentaon.vzon.numOfPicture";
    public static final String INTENT_EXTRA_FROM_SOME_ACTIVITY = "com.pentaon.vzon.fromSomeActivity";
    public static final String INTENT_EXTRA_USABLE_GALLERY = "com.pentaon.vzon.usable.Gallery";
    public static final String INTENT_EXTRA_ALLOW_EDIT_MODE = "com.pentaon.vzon.allow.editmode";
    public static final String INTENT_EXTRA_FROM_GALLERY ="com.pentaon.vzon.imageFromGallery";
    public static final String INTENT_EXTRA_INSTALLATION_FILE_PATH ="installation_file_path" ;

    public static final String FORM_ID = "FORM_ID"; // 폼(양식) 아이디
    public static final String IDNT_CD = "IDNT_CD"; // 신분증 종류 (신규-대표자) (1: 주민등록증, 2: 운전면허증, 3: 재외국민 주민등록증, 4: 외국인등록증, 5: 외국국적동포 국내거소신고증)
    public static final String INTENT_EXTRA_DOC_MAX_PAGE = "INTENT_EXTRA_DOC_MAX_PAGE"; //문서 최대 장수

//    public static final String CAMERA_MODE_B = "B";
//    public static final String CAMERA_MODE_C = "C";
    public static final String TEST_SERVER_URL = "https://demoweb.vzon.co.kr/";
    public static final String DEV_URL = "http://tapp.vzon.co.kr/";
    public static final String REAL_SERVER_URL ="https://www.vzon.co.kr/";
    public static final String LICENSE_KEY = "lyof015B0VahLgrZ5B4puVByryTrHUqBZ/QmNc4bVJWQ+qe3TtpgC7uwrBBapo2MVdyWkb3FO0rPfv1Tbza189rc55s5QdtPo7XS+cNX+pefQzOXOAO5YRlO8mhSOXgxyM2yzGXV1E0qC+Jl";

    public static final int ALERT_DEFAULT     = -1;
    public static final int ALERT_CANCEL      = 0;
    public static final int ALERT_OK          = 1;
    public static final int ALERT_OKANDCANCEL = 2;
    public static final int ALERT_OKANDCANCEL_REVERSE =3;

    public static final int REQUEST_GET_IMAGE_FROM_CAMERA = 0 ;
    public static final int REQUEST_SAVE_IMAGE_FROM_CAMERA = 1;
    public static final int REQUEST_SCAN_BARCODE = 2;
    public static final int REQUEST_GET_IMAGE_FROM_AAR = 3;
    public static final int REQUEST_SET_POINT = 4;
    public static final int REQUEST_EVIDENCE_ATTACH = 33;

    //public static final boolean IS_EXTRA_HIGH_DENSITY_2048X1536 = SystemUtil.isExtraHighDensity2048x1536();


    public static final int DEGREE_0 = 0;
    public static final int DEGREE_90 = 90;
    public static final int DEGREE_180 = 180;
    public static final int DEGREE_270 = 270;

    public static final int MSG_WHAT_IMAGE_PROCESSING = 101;
    public static final int MSG_WHAT_IMAGE_PROCESSED = 102;
    public static final int MSG_WHAT_IMAGE_SAVING = 103;

    public static final String KEY_FILE = "file";
    public static final long MILLISEC_A_DAY = 24*3600*1000; //24*3600*1000
    //------------------------------------------------------
    // 바코드 관련 constants
    //------------------------------------------------------
    public static final String PROD_NAME  = "prodName";
    /*public static final String SHIP_ID  = "shipId"; //출하 아이디
    public static final String SHIP_ITEM_ID  = "shipItemId";//출하 항목 아이디
    public static final String ORDER_ID  ="orderId";//오더 아이디
    public static final String ORDER_ITEM_ID  ="orderItemId";//오더 항목 아이디*/
    public static final String PARAM_ID = "paramId"; // 출하, 오더 아이디 통합
    public static final String PARAM_ITEM_ID = "paramItemId"; // 출하 항목, 오더 항목 아이디 통합
    public static final String HOLD_PARTY_ID  ="holdPartyId";//보유 파티 아이디
    public static final String PROD_ID  = "prodId";// 상품 아이디 [필수]
    public static final String TYPE  = "type"; //바코드 유형[필수]
    public static final String SERIAL_NR  ="serialNr";//시리얼 넘버[필수]
    public static final String TOTAL_NUM  ="totalNum";

    public static final String BARCODE_VERIFY_PURCHASE_WAREHOUSING= "BARCODE_VERIFY_PURCHASE_WAREHOUSING";
    public static final String BARCODE_VERIFY_MERCHANT_INSTALL= "BARCODE_VERIFY_MERCHANT_INSTALL";
    public static final String BARCODE_VERIFY_RETURN_INSTALL= "BARCODE_VERIFY_RETURN_INSTALL";
    public static final String BARCODE_VERIFY_RETURN_ORDER_RETRIEVAL= "BARCODE_VERIFY_RETURN_ORDER_RETRIEVAL";
    public static final String BARCODE_VERIFY_REPAIR_ITEM= "BARCODE_VERIFY_REPAIR_ITEM";
    public static final String BARCODE_VERIFY_SHIPMENT_ISSUE= "BARCODE_VERIFY_SHIPMENT_ISSUE";
    public static final String BARCODE_VERIFY_SHIPMENT_COMPARE= "BARCODE_VERIFY_SHIPMENT_COMPARE";

    //------------------------------------------------------
    // 증빙문서 관련 constants
    //------------------------------------------------------
    public static final String CONTRACT_PROOF_A = "CONTRACT_PROOF_A";//사업자 등록증
    public static final String CONTRACT_PROOF_B = "CONTRACT_PROOF_B";//신분증
    public static final String CONTRACT_PROOF_C = "CONTRACT_PROOF_C";//통장 사본
    public static final String CONTRACT_PROOF_D = "CONTRACT_PROOF_D";//법인 인감 증명서
    public static final String CONTRACT_PROOF_E = "CONTRACT_PROOF_E";//법인 등기부 등본
    public static final String CONTRACT_PROOF_F = "CONTRACT_PROOF_F";//위임장
    public static final String CONTRACT_PROOF_G = "CONTRACT_PROOF_G";//위임자 신분증
    public static final String CONTRACT_PROOF_H = "CONTRACT_PROOF_H";//매장 사진
    public static final String CONTRACT_PROOF_I = "CONTRACT_PROOF_I";//설치 사진(document)

    public static final String DOC_KIND_A = "A"; // 일반 (문서종류 : 공통코드)
    public static final String DOC_KIND_B = "B"; // 신분증 (문서종류 : 공통코드)
    public static final String DOC_KIND_C = "C"; // 여권 (문서종류 : 공통코드)
    public static final String DOC_KIND_D = "D"; // 거래통장 (문서종류 : 공통코드)
    public static final String DOC_KIND_E = "E"; // 매장사진 (문서종류 : 공통코드)
    public static final String DOC_KIND_AA = "AA"; // 주민등록증 발급신청 확인서 (문서종류 : 모바일에서 내부적으로 사용하는 코드)

    public static final String FORM_ID_A1 = "A1"; // 사업자 등록증(문서코드 : 공통 코드)
    public static final String FORM_ID_A2 = "A2"; // 거래 통장(문서코드 : 공통 코드)
    public static final String FORM_ID_A3 = "A3"; // 매장 사진(문서코드 : 공통 코드)
    public static final String FORM_ID_J1 = "J1"; // 대표자 신분증 (문서코드 : 공통 코드)
    public static final String FORM_ID_J7 = "J7"; // 작성자 위임장(문서코드 : 공통 코드)
    public static final String FORM_ID_L1 = "L1"; // 대리인 신분증(문서코드 : 공통 코드)
    public static final String FORM_ID_J9 = "J9"; // 주민등록초본 (문서코드 : 공통 코드)

    public static final String FORM_ID_N1 = "N1"; // 법인등기부 등본(등기사항 전부증명서)  (문서코드 : 공통 코드) // #1188
    public static final String FORM_ID_N2 = "N2"; // 법인인감증명서  (문서코드 : 공통 코드)
//    public static final String FORM_ID_N4 = "N4"; // 정관 혹은 설립인가서  (문서코드 : 공통 코드) // #1188
//    public static final String FORM_ID_R6 = "R6"; // 공동대표자 신분증  (문서코드 : 공통 코드)

    public static final String COLOR_SELECTION_B = "B"; // 흑백 (흑백컬러구분 : 공통 코드)
    public static final String COLOR_SELECTION_C = "C"; // 컬러 (흑백컬러구분 : 공통 코드)

    public static final String DOC_MASKING_E = "E"; // 필수 (마스킹구분 : 공통 코드)
    public static final String DOC_MASKING_C = "C"; // 선택 (마스킹구분 : 공통 코드)
    public static final String DOC_MASKING_N = "N"; // 사용안함 (마스킹구분 : 공통 코드)

    public static final String IDNT_CD_1 = "1"; // 신분증 종류 (신규-대표자) (1: 주민등록증)
    public static final String IDNT_CD_2 = "2"; // 신분증 종류 (신규-대표자) (2: 운전면허증)
    /*public static final String IDNT_CD_3 = "3"; // 신분증 종류 (신규-대표자) (3: 재외국민 주민등록증) (2016년 7월 1일 부터 '재외국민 국내거소신고증'이 없어지고 '재외국민 주민등록증'이 사용됨.
    public static final String IDNT_CD_4 = "4"; // 신분증 종류 (신규-대표자) (4: 외국인등록증)
    public static final String IDNT_CD_5 = "5"; // 신분증 종류 (신규-대표자) (5: 외국국적동포 국내거소신고증)*/

    //------------------------------------------------------
    // 빌드 모드 관련 constants
    //------------------------------------------------------
    /*public static final String DEV_MODE  = "DEV_MODE";  // 개발 서버
    public static final String DEMO_MODE = "DEMO_MODE"; // 데모 서버
    public static final String TEST_MODE = "TEST_MODE"; // 운영 테스트 서버
    public static final String REAL_MODE = "REAL_MODE"; // 운영 서버
    public static final String CURRENT_MODE = "TEST_MODE";
    //------------------------------------------------------*/
    // 은행 관련 constants 
    //------------------------------------------------------
    // TODO: 2018-08-10 unused 시 삭제할 것
    public static final String STLN_BANK_NM = "STLN_BANK_NM"; // 결제은행명(신규,제신고-결제계좌)
//    public static final String STLN_BANK_CD = "STLN_BANK_CD"; // 추가 2013.12.16 (HD Card에만 있는 PDF 처리를 위해서) 결제은행 코드(신규,제신고-결제계좌)
    public static final String STLN_ACNO = "STLN_ACNO"; // 결제계좌번호(신규,제신고-결제계좌)
    public static final String DPSR_NM = "DPSR_NM"; // 예금주명(신규,제신고-결제계좌)


    public static final boolean IS_DISPLAY_ASPECT_RATIO_4TO3 = SystemUtil.isDisplayAspectRatio4to3();
    public static final boolean IS_EXTRA_HIGH_DENSITY_2048X1536 = SystemUtil.isExtraHighDensity2048x1536();

//    public static final String ATTCH_ADD_YN = "ATTCH_ADD_YN"; // 첨부보완 신청 여부(Y:첨부보완신청, N:일반신청)
    public static final String IMAGE_REMARK = "IMAGE_REMARK"; // 이미지 특이사항


    public static final int NOT_DEFINED = -1;

    //문서 종류
    public static final int DOC_TYPE_ID = 0;
    public static final int DOC_TYPE_A4 = 1;

    //------------------------------------------------------
    // 이미지 constants
    //------------------------------------------------------
    public static final String PICTURE_SIZE = "picture_size";
    public static final String PREVIEW_SIZE = "preview_size";

    public static final String APP_START_TIME = "appStartTime";
    public static final String IS_INIT = "isInit";
    public static final String TYPE_JPEG= "image/jpeg";
    public static final String TYPE_TIFF= "image/tiff";

    public static final int ID_TRIAL = 100;
    // OCR 시도 회수
    public static final int OCR_TRIAL = 15;

    // A4 문서 찾기 시도 회수
    public static final int A4_PREVIEW_TRIAL = 100;
    // A4 문서 형상보정 시도 회수
    public static final int A4_SHAPE_CORRECT_TRIAL = 3;

    // 가이드 라인 컬러 두께
    public static final int LINE_COLOR = 0xFF376AB9;
    public static final float LINE_THICK = 8.0F;

    //출력 이미지 컬러 구분
    public static final int COLOR = 0;
    public static final int GRAY = 1;
    public static final int BLACK_WHITE = 2;

    public static final int PERSPECTIVE_WIDTH = 952;               // Perspective Transform 반환 이미지 가로(수정 불가)
    public static final int PERSPECTIVE_DEPTH = 600;               // Perspective Transform 반환 이미지 세로(수정 불가)
    public static final int PERSPECTIVE_WIDTH_A4 = 1200;               // A4 문서 Perspective Transform 반환 이미지 가로(수정 불가)
    public static final int PERSPECTIVE_DEPTH_A4 = 1704;               // A4 문서 Perspective Transform 반환 이미지 세로(수정 불가)
    public static final int XVIEW_MARGINE = 5;                    // Preview Image에서 신분증 박스 그리기 수평 여유 마진 (좌우측 여유 = Image W/XVIEW_MARGINE)
    public static final int YVIEW_MARGINE = 5;                    // Preview Image에서 신분증 박스 그리기 수직 여유 마진 (상하측 여유 = Image H/XVIEW_MARGINE)
    public static final int XVIEW_MARGINE_A4 = 5;                // Preview Image에서 신분증 박스 그리기 수평 여유 마진 (좌우측 여유 = Image W/XVIEW_MARGINE)
    public static final int YVIEW_MARGINE_A4 = 5;                // Preview Image에서 신분증 박스 그리기 수직 여유 마진 (상하측 여유 = Image H/XVIEW_MARGINE)


//    public static float POINT_VIEW_DEFAULT_MARGIN_RATIO = (float)(75.f/768.f);
}
