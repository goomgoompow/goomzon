package com.pentaon.vzon.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.pentaon.vzon.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jh.Kim on 14,5월,2018
 */
public class StringUtil {

    private static final String TAG = StringUtil.class.getSimpleName();

//	public static String EmailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
//            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
//            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
//            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    public static String EmailPattern = Patterns.EMAIL_ADDRESS.toString();

    public static String nullToEmptyString(String str){
        if(str == null || "".equals(str) || "null".equals(str))
        {
            return "";
        }

        return str.trim();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String stringTrim(String str){
        if(str == null)
        {
            return "";
        }

        return str.trim();
    }

    public static String inputStreamToString(InputStream is){
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 사업자번호 '-' 제거처리함
     * @param brNumber
     * @return
     */
    public static String getReplaceValue(String brNumber){
        if(TextUtils.isEmpty(brNumber) == true) {
            return "";
        }

        return brNumber.replaceAll("-", "");
    }

    /**
     * 사업자 번호 '-' 붙이기
     * @param eprno
     * @return
     */
    public static String getEprNoAddMinus(String eprno){
        if(TextUtils.isEmpty(eprno) == true) {
            return "";
        }

        String keyword = eprno.toString();
        keyword.trim();
        int a = -1;
        a = keyword.indexOf("-");
        if(keyword.length() < 4 && a != -1)
        {
            keyword = keyword.replaceAll("-", "");
        }

        int keyCnt = keyword.length();
        if(keyCnt >= 4 && keyCnt < 7 && a != 3)
        {
            keyword = keyword.replaceAll("-", "");
            String leftkeyword = keyword.substring(0, 3);
            String rightkeyword = keyword.substring(3, keyword.length());
            keyword = leftkeyword + "-" + rightkeyword;
//			if(cpoint == 4)
//			{
//				cpoint ++;
//			}
        }

        int b = -1;
        b = keyword.indexOf("-", 5);
        keyCnt = keyword.length();
        if(keyCnt >= 7 && b != 6)
        {
            keyword = keyword.replaceAll("-", "");
            String leftkeyword = keyword.substring(0, 3);
            String rightkeyword = keyword.substring(3, keyword.length());
            keyword = leftkeyword + "-" + rightkeyword;

            leftkeyword = keyword.substring(0, 6);
            rightkeyword = keyword.substring(6, keyword.length());
            keyword = leftkeyword + "-" + rightkeyword;


//			if(cpoint == 7)
//			{
//				cpoint ++;
//			}
//			if(cpoint == 4 && beforeText.length() < keyword.length())
//			{
//				cpoint ++;
//			}


        }
        return keyword;
    }


    public static String getSafeText(int edId, Activity context)
    {
        EditText ed = null;
        try {
            ed = (EditText) context.findViewById(edId);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return getSafeText(ed);
    }

    public static String getSafeText(int edId, Dialog context)
    {
        EditText ed = null;
        try {
            ed = (EditText) context.findViewById(edId);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return getSafeText(ed);
    }



    public static String getSafeText(EditText ed)
    {
        return getSafeText(ed, true);
    }

    public static String getSafeText(int edId, Activity context, boolean trim)
    {
        EditText ed = null;
        try {
            ed = (EditText) context.findViewById(edId);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return getSafeText(ed, trim);
    }

    public static String getSafeText(int edId, Dialog context, boolean trim)
    {
        EditText ed = null;
        try {
            ed = (EditText) context.findViewById(edId);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return getSafeText(ed, trim);
    }

    public static String getSafeText(EditText ed, boolean trim)
    {

        String text = "";
        final Editable textSequence = ed.getText();

        if(textSequence.length()>0)
        {
            if(trim){
                text = textSequence.toString().trim();
            }else {
                text = textSequence.toString();
            }
        }

        return text;
    }
    /**
     * "second"(int형)를 입력받아 "X분 X초"(String형)으로 반환
     * @param second
     * @return returnString;
     * @author Dohun
     */
    public static String getSecondToMinute(int second)
    {
        String returnString = "";
        String strMinute = "";
        String strSecond = "";

        int intMinute = (second/60);
        int intSecond = (second%60);

        strMinute = String.valueOf(intMinute);
        strSecond = String.valueOf(intSecond);

        returnString = strMinute+"분"+" "+strSecond+"초";
        return returnString;
    }

//	/**
//	 * 숫자나 문자에 "-" 빼주는 메소드
//	 * @param str
//	 * @return str
//	 */
//	public static String getDeleteMinus(String str)
//	{
//		if(TextUtils.isEmpty(str) == true) {
//			return "";
//		}
//
//		String[] tempStr;
//		tempStr = str.split("-");
//		str = "";
//		for(int i = 0 ; i < tempStr.length ; i++)
//		{
//			str += tempStr[i];
//		}
//		return str;
//	}

    /**
     * 주민등록번호에 "-" 넣어주는 메소드
     * @param str
     * @return rrnum
     */
    public static String conRrNum(String str){
        if(TextUtils.isEmpty(str) == true) {
            return "";
        }

        String leftkeyword = str.substring(0, 6);
        String rightkeyword = str.substring(6, str.length());
        String rrnum = leftkeyword +"-"+rightkeyword;

        return rrnum;
    }

    /**
     * 날짜에 "-" 넣어주는 메소드 (****-**-**) 년-월-일
     * @param str
     * @return tempDate
     */
    public static String conDateFormat(String str){
        if(TextUtils.isEmpty(str) == true || str.length() < 8) {
            return "";
        }
        String leftkeyword = str.substring(0, 4);
        String middlekeyword = str.substring(4, 6);
        String rightkeyword = str.substring(6, str.length());
        String tempDate = leftkeyword +"-"+ middlekeyword +"-"+ rightkeyword;

        return tempDate;
    }

    /**
     * 법인등록번호에 "-" 넣어주는 메소드 (******-*******)
     * @param str
     * @return tempDate
     */
    public static String conCrprNoFormat(String str){
        if(TextUtils.isEmpty(str) == true) {
            return "";
        }

        String leftkeyword = str.substring(0, 6);
        String rightkeyword = str.substring(6, str.length());
        String tempCrprNo = leftkeyword +"-"+ rightkeyword;

        return tempCrprNo;
    }

    /**
     * "-" 넣어주는 메소드
     * @param str
     * @param strcnt
     * @return tempStr
     */
    public static String conHypenFormat(String str, Integer... strcnt){

        if (TextUtils.isEmpty(str)) return str;

        String tempStr = "";
        int firstidx = 0;
        int lastidx = 0;
        for (Integer cnt:strcnt) {
            lastidx = firstidx + cnt;
            lastidx = lastidx <= str.length()? lastidx:str.length();

            if (firstidx == 0)  tempStr = str.substring(firstidx, lastidx);
            else tempStr += "-"+str.substring(firstidx, lastidx);
            firstidx += cnt;

            if (str.length() <= firstidx) break;
        }

        return tempStr;
    }


    /**
     * 주민번호를 체크하여 date string 변환
     */
    public static String convertJumintoDate(String minNum) {

        if (!TextUtils.isEmpty(minNum)) {
//				※ 주민번호 7번째 자리가 의미하는것은 아래와 같다.
//				1,2 : 1900년대생 내국인
//				3,4 : 2000년대생 내국인
//				5,6 : 1900년대생 외국인
//				7,8 : 2000년대생 외국인
            String birthYear;
            try {
                switch (Integer.valueOf(minNum.substring(6, 7))) {
                    case 1:case 2:case 5:case 6:
                        birthYear = "19";
                        break;
                    default:
                        birthYear = "20";
                }

                return birthYear+minNum.substring(0, 6);
            } catch(Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";

    }
    /**
     * euc-kr로 변환시 깨지는 문자를 쪽자로 변환시켜 준다.
     * @param istr
     * @return lastStr
     */
    public static String convertEuckrtoPieces(String istr)
    {
        if(TextUtils.isEmpty(istr) == true) {
            return "";
        }

        String pure = "";
        String bbb = "";
        String euc_kr = "";

        if (!detectCharOutOfRange(istr)) return istr;

        for (int i = 0; i<istr.length(); i=i+1)
        {
            if (i==istr.length()-1)
            {
                bbb = istr.substring(i);
                euc_kr = convert(bbb);
            }
            else
            {
                bbb = istr.substring(i,i+1);
                euc_kr = convert(bbb);
            }
//			if (bbb.equals(".") || bbb.equals("@"))
//				euc_kr = bbb;
            //Log.d("lee","i = " + i + " " + euc_kr);
            if (!bbb.equals("?") && euc_kr.equals("?"))
            {
                pure = pure + convertHangultoPieces(bbb);
            }
            else
            {
                pure = pure + bbb;
            }
        }
        return pure;

    }

    public static boolean isHanGul(String s){
        if(s == null || s.equals("")) return false;

        for(int i=0; i<s.length(); i++){
            if( isHangulCh(s.charAt(i)) ){
                return true;
            }
        }
        return false;
    }

    public static boolean isHangulCh(char c){

        String unicodeBlock = Character.UnicodeBlock.of(c).toString();

        return unicodeBlock.equals("HANGUL_JAMO") ||
                unicodeBlock.equals("HANGUL_SYLLABLES") ||
                unicodeBlock.equals("HANGUL_COMPATIBILITY_JAMO");

    }


    public static boolean detectCharOutOfRange(String str) {
        if (!isHanGul(str))
            return false;
        else
            return convert(str).contains("?");
    }

    public static boolean isNumber(String str) {
        Pattern integerPattern = Pattern.compile("^[0-9]+$");
        Matcher matchesInteger = integerPattern.matcher(str);
        return matchesInteger.matches( );
    }

    /**
     * 문자중 euc-kr로 변환시 깨지는 문자를 '?'로 변환해 준다.
     * @param str
     * @return lastStr
     */

    public static String convert(String str) {
        if(TextUtils.isEmpty(str) == true) {
            return "";
        }

        if (str.length() == 1 && !isHanGul(str)) return str; // 한글자이고 한글이 아닌 경우에는 그대로 리턴함(모든 특수문자는 그대로 표현됨)

        String regex = "[^- a-zA-Z0-9ㄱ-ㅎ가-힣ㅏ-ㅣ”^”, ”$”, ”*”, ”+”, ”?”, ”.”, ”(”, ”)”, ”|”, ”{”, ”}”, ”[”, ”]”)]";
        String name = str;
        String clearUserName = "";


        try {
            String userName = new String(name.getBytes("ISO-2022-KR"), "ISO-2022-KR");
            clearUserName = new String(userName.getBytes("utf-8"), "utf-8");
            clearUserName = clearUserName.replaceAll(regex, "?");
            return clearUserName;
        }
        catch (Exception e) {
            return "?";
        }

    }


    private static final char[] CHO =
            /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
            {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
    private static final char[] JUN =
            /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
            {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
                    0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160,	0x3161,	0x3162,
                    0x3163};
    /*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
    private static final char[] JON =
            {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
                    0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

    /**
     * 한글을 자음과 모음으로 분리
     * @param tempStr
     * @return lastStr
     */
    public static String convertHangultoPieces(String tempStr) {
        if(TextUtils.isEmpty(tempStr) == true) {
            return "";
        }

        String lastStr = "";

        for(int i = 0 ; i < tempStr.length();i++)
        {
            char test = tempStr.charAt(i);

            if(test >= 0xAC00)
            {
                char uniVal = (char) (test - 0xAC00);

                char cho = (char) (((uniVal - (uniVal % 28))/28)/21);
                char jun = (char) (((uniVal - (uniVal % 28))/28)%21);
                char jon = (char) (uniVal %28);

                lastStr += ""+CHO[cho] + JUN[jun] ;
                if(jon != 0x0000)
                    lastStr += JON[jon];
            }
        }

        return lastStr;


    }

    /**
     * 자음과 모음으로 분리된 한글을 다시 합침(단, 종성이 없는 경우에는 구분 못함. 따라서 반드시 종성까지 있어야 함)
     * @param tempStr
     * @return lastStr
     */
    public static String convertPiecestoHangul(String tempStr) {
        if(TextUtils.isEmpty(tempStr) == true) {
            return "";
        }

        String lastStr = "";
        int cnt = 0;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0 ; i < tempStr.length();i++)
        {
            char ch = tempStr.charAt(i);

            if(ch >= 0xAC00) //
            {
                lastStr+= ""+ch;
                cnt = 0;
            }
            else {

                if (cnt == 0) {
                    map.put("cho", (int) ch);
                }
                else if (cnt == 1) {
                    map.put("jun", (int) ch);
                }
                else if (cnt == 2) {
                    map.put("jon", (int) ch);
                }

                cnt++;

                if (cnt == 3) {
                    cnt = 0;
                    int a = map.get("cho");
                    int b = map.get("jun");
                    int c = map.get("jon");

                    for (int k=0 ;k <CHO.length;k++) {
                        if (CHO[k] == a) {
                            a = k;
                            break;
                        }
                    }

                    for (int k=0 ;k <JUN.length;k++) {
                        if (JUN[k] == b) {
                            b = k;
                            break;
                        }
                    }

                    for (int k=0 ;k <JON.length;k++) {
                        if (JON[k] == c) {
                            c = k;
                            break;
                        }
                    }

                    char temp = (char)(0xAC00 + 28 * 21 *(a) + 28 * (b) + (c) );
                    lastStr += ""+temp;
                }
            }
        }

        return lastStr;


    }

//	/**
//	 * 전화번호를 **-****-**** 형식으로 변경
//	 * @param str
//	 * @return str
//	 */
//	public static String formatNumber(String str) {
//		if(TextUtils.isEmpty(str) == true) {
//			return "";
//		}
//
//		if (str.length() < 9)
//			return str.replaceAll("([0-9]+)([0-9]{4})","$1-$2");
//        else if (str.length() == 9)
//            return str.replaceAll("([0-9]{2})([0-9]{3})([0-9]{4})","$1-$2-$3");
//        else if (str.length() > 10)
//            return str.replaceAll("([0-9]+)([0-9]{4})([0-9]{4})","$1-$2-$3");
//		else
//			return str.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})","$1-$2-$3");
//	}

    /**
     * 특수 형태 포함 전화번호를 **-****-**** 형식으로 변경
     * @param str
     * @return str
     */
    public static String formatNumber(String str) {
        if(TextUtils.isEmpty(str) == true) {
            return "";
        }

        str = str.replace("-","");

        if (str.startsWith("050")) {
            if (str.length() < 11) {
                str = StringUtil.conHypenFormat(str, 3,3,4);
            }
            else if (str.length() < 12) {
                str = StringUtil.conHypenFormat(str, 3,4,4);
            }
            else {
                str = StringUtil.conHypenFormat(str, 4,4,4);
            }
        }
        else {
            if (str.length() < 9) {
                return str.replaceAll("([0-9]+)([0-9]{4})", "$1-$2");
            }
            else if (str.length() == 9) {
                return str.replaceAll("([0-9]{2})([0-9]{3})([0-9]{4})", "$1-$2-$3");
            }
            else if (str.length() > 10) {
                return str.replaceAll("([0-9]+)([0-9]{4})([0-9]{4})", "$1-$2-$3");
            }
            else {
                return str.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$1-$2-$3");
            }

//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // 구형폰
//                str = PhoneNumberUtils.formatNumber(str);
//            } else { //신형폰
//                str = PhoneNumberUtils.formatNumber(str, Locale.getDefault().getCountry());
//            }
        }

        return str;
    }



    /**
     * 핸드폰 번호인지 체크
     * @param newPhoneNo
     * @return bool
     */
    public static boolean isMobilePhoneNo(String newPhoneNo) { //번호의 맨처음 3자리로 핸드폰 번호인지 체크
        if (TextUtils.isEmpty(newPhoneNo) || newPhoneNo.length() < 3) return false;

        String phoneNo = newPhoneNo.replace("-", "").substring(0, 3);
        String regex = "^01[0|1|6|7|8|9]";//"^01[0|1|6|7|8|9]-[\\d{4}|\\d{3}]-\\d{4}$";
        return Pattern.matches(regex, phoneNo);
    }

    /**
     * 핸드폰 번호 형태인지 체크
     * @param newPhoneNo
     * @return bool
     */
    public static boolean checkMobilePhoneNo(String newPhoneNo) {
        if (TextUtils.isEmpty(newPhoneNo) || newPhoneNo.length() < 3) return false;
        //String phoneNo = newPhoneNo.replace("-", "").substring(0, 3);
        String regex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
        return Pattern.matches(regex, newPhoneNo);
    }

    /*
    public static boolean isPhoneNo(Context ctx, String newPhoneNo, String cardnm) {
        if (TextUtils.isEmpty(newPhoneNo)) return true;
        String[] numprefixlist = ctx.getResources().getStringArray(cardnm.equals("IB") ? R.array.phoneprefixibk : R.array.phoneprefix);

        for (int i = 0; i < numprefixlist.length; i++) {
            if (newPhoneNo.startsWith(numprefixlist[i])) {
                return true;
            }
        }
        return false;

    }*/
}
