package com.pentaon.vzon.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Debug;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pentaon.vzon.R;
import com.pentaon.vzon.common.Config;
import com.pentaon.vzon.transaction.Vzon;
import com.tiffdecoder.TiffDecoder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jh.Kim * on 15,5월,2018
 */
public class GUIUtil {
    private static final String TAG  = "GUIUtil";
    public static void setListViewHeightBasedOnChildren(ListView listView, int maxRow) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        if(Config.DEBUG)
        {
            Log.d(TAG," >>>> GUIUtil ["+listAdapter.getCount()+"]");
        }
        int totalHeight = 0;
        int totalRow = listAdapter.getCount();
        totalRow = maxRow > 0 ? maxRow : totalRow;

        for (int i = 0; i < totalRow; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        final ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (totalRow -1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        setListViewHeightBasedOnChildren(listView, -1);
    }

    public static int getDeviceHeight(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getDeviceWidth(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static int Dip2Pixel(Context context, int DP)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
    }

    public static int Pixel2Dip(Context context, int pixel)
    {
        float scale = getDensity(context);

        return (int)(pixel / scale);
    }

    public static float getDensity(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.density;
    }

    public static float getDensityDpi(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.densityDpi;
    }

    public static void setKeyboard(Context context, boolean show, EditText editText)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(show)
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        else
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void setKeyboard(Context context, boolean show, int edId)
    {
        EditText ed = null;
        try {
            ed = (EditText) ((Activity) context).findViewById(edId);
        } catch (Exception e) {
            if(Config.DEBUG){
                Log.d(TAG, e.getCause().toString());
            }
            return ;
        }

        setKeyboard(context, show, ed);
    }

    public static boolean isViewContains(View view, int rx, int ry) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }

    public static Bitmap getThumbnailImage(String filename)
    {
        return getThumbnailImage(filename, true);
    }

    public static Bitmap getThumbnailImage(String filename, boolean sampling)
    {
        if(Config.DEBUG)
        {
            Log.d(TAG,"getThumbnailImage filename["+filename+"] sampling["+sampling+"]");
        }

        Bitmap bmp = null;

        try {
            // TODO: 2018-11-02 이미지 복호화
            if(sampling)
            {
                BitmapFactory.Options bo = new BitmapFactory.Options();
                bo.inSampleSize = 16; // 1/32
                bmp = BitmapFactory.decodeFile(filename, bo);
            }
            else
            {
                bmp = BitmapFactory.decodeFile(filename);
            }

            if(null == bmp)
                return bmp;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return bmp;

    }

    public static Bitmap getTiffThumbnailImage(String filename)
    {
        return getTiffThumbnailImage(filename, true);
    }

    public static Bitmap getTiffThumbnailImage(String filename, boolean sampling)
    {
        if(Config.DEBUG)
        {
            Log.d(TAG,"getTiffThumbnailImage filename["+filename+"] sampling["+sampling+"]");
        }

        Bitmap bmp = null;

        try {

            if(Config.DEBUG)
            {
                Log.d(TAG,"getTiffThumbnailImage 1111"); // 메모리 릭 체크
            }
            TiffDecoder.nativeTiffOpen(filename);
            int[] pixels = TiffDecoder.nativeTiffGetBytes();
            bmp = Bitmap.createBitmap(pixels, TiffDecoder.nativeTiffGetWidth(), TiffDecoder.nativeTiffGetHeight(),Bitmap.Config.ARGB_8888);

            double maxMemory = Runtime.getRuntime().maxMemory() / ( 1024.0f );
            double allocateMemory = Debug.getNativeHeapAllocatedSize() / ( 1024.0f );
            if(Config.DEBUG)
            { // 메모리 릭 체크
                Log.d(TAG, "getTiffThumbnailImage 최대 메모리 : " + maxMemory + "KB " );
                Log.d(TAG, "getTiffThumbnailImage 사용 메모리 : " + allocateMemory + "KB " );
            }
            if(null == bmp) {
                if(Config.DEBUG)
                {
                    Log.d(TAG,"getTiffThumbnailImage 2222"); // 메모리 릭 체크
                }
                TiffDecoder.nativeTiffClose();
                return bmp;
            }

            if(sampling)
            {
                if(Config.DEBUG)
                {
                    Log.d(TAG,"getTiffThumbnailImage 3333"); // 메모리 릭 체크
                }
                bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*0.03125),(int)(bmp.getHeight()*0.03125),true);
            }
            else
            {
                if(Config.DEBUG)
                {
                    Log.d(TAG,"getTiffThumbnailImage 4444"); // 메모리 릭 체크
                }
                TiffDecoder.nativeTiffClose();
                return bmp;
            }

            TiffDecoder.nativeTiffClose();
            if(Config.DEBUG)
            {
                Log.d(TAG,"getTiffThumbnailImage 8888"); // 메모리 릭 체크
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public static Bitmap getTiffBackGroundImage(String filename)
    {
        return getTiffBackGroundImage(filename, false);
    }

    public static Bitmap getTiffBackGroundImage(String filename, boolean sampling)
    {
        if(Config.DEBUG)
        {
            Log.d(TAG,"getTiffBackGroundImage filename["+filename+"] sampling["+sampling+"]");
        }

        Bitmap bmp = null;

        try {

            TiffDecoder.nativeTiffOpen(filename);
            int[] pixels = TiffDecoder.nativeTiffGetBytes();
            bmp = Bitmap.createBitmap(pixels, TiffDecoder.nativeTiffGetWidth(), TiffDecoder.nativeTiffGetHeight(),Bitmap.Config.ARGB_8888);

            if(sampling)
            {

                bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*0.03125),(int)(bmp.getHeight()*0.03125),true);
            }

            if(null == bmp)
                return bmp;

            TiffDecoder.nativeTiffClose();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;

    }

    public static void setDateChange(Context context, int res_id, int year, int monthOfYear,int dayOfMonth)
    {
        String strDate = String.format("%d-%02d-%02d", year, monthOfYear, dayOfMonth);

        View dateEdit = ((Activity) context).findViewById(res_id);

        ((TextView) dateEdit).setText(strDate);
    }

    public static void setDateChange(Dialog context, int res_id, int year, int monthOfYear, int dayOfMonth)
    {
        String strDate = String.format("%d-%02d-%02d", year, monthOfYear, dayOfMonth);

        View dateEdit = context.findViewById(res_id);

        ((TextView) dateEdit).setText(strDate);
    }

    public static void setDateChange(Context context, int res_id, Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        TextView dateEdit = (TextView) ((Activity) context).findViewById(res_id);

        dateEdit.setText(formatter.format(date));
    }

    /**
     * "-"(하이픈) 없는 형태의 날짜팝업
     * @param context
     * @param res_id
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public static void setDateChangeNoHyphen(Context context, int res_id, int year, int monthOfYear,int dayOfMonth)
    {
        String strDate = String.format("%d%02d%02d", year, monthOfYear, dayOfMonth);

        View dateEdit = ((Activity) context).findViewById(res_id);

        ((TextView) dateEdit).setText(strDate);
    }


    public static void setTimeChange(Context context, int res_id, int hourOfDay, int minute)
    {
        String strTime= String.format("%02d:%02d", hourOfDay, minute);

        EditText timeEdit =(EditText) ((Activity) context).findViewById(res_id);

        timeEdit.setText(strTime);
    }

    public static int getExifDegree(String filepath) {
        if(Config.DEBUG)
        {
            Log.d(TAG, "getExifDegree() : filepath["+filepath+"]");
        }
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if(Config.DEBUG)
            {
                Log.d(TAG, "getExifDegree() : orientation["+orientation+"]");
            }
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } else {
            if(Config.DEBUG)
            {
                Log.d(TAG, "getExifDegree() : exif is null");
            }
        }
        if(Config.DEBUG)
        {
            Log.d(TAG, "getExifDegree() : degree["+degree+"]");
        }
        return degree;
    }

    public static void showExif(ExifInterface exif) {
        Log.d( TAG, "======================== showExif (start) ========================");
        Log.d( TAG, getTagString(ExifInterface.TAG_APERTURE, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_DATETIME, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_EXPOSURE_TIME, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_FLASH, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_FOCAL_LENGTH, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_ALTITUDE, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_ALTITUDE_REF, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_DATESTAMP, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_LATITUDE, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_PROCESSING_METHOD, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_GPS_TIMESTAMP, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_ISO, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_MAKE, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_MODEL, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_ORIENTATION, exif));
        Log.d( TAG, getTagString(ExifInterface.TAG_WHITE_BALANCE, exif));
        Log.d( TAG, "======================== showExif ( end ) ========================");
    }

    public static String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    public final static int MAX_LEN = 34; // 체크박스 글자 최대 갯수
    //    public final static int MAX_STR_LEN = 13; // 글자 최대 갯수
    public final static int MAX_WIDTH = 240; // 최대 넓이

    public static int getCheckBoxCount(ArrayList<HashMap<String,Object>> data) {

        int layoutcnt = 0;
        String tempName = "";

        for (int i=0;i<data.size();i++) {
            tempName += (String) data.get(i).get(Vzon.OPT_NAME);
            if ((tempName.length() +2*(i+1))> MAX_LEN) {
                layoutcnt = Math.max(i-1, 1);//(i+2)/2;
                break;
            }
            else {
                layoutcnt++;
            }
        }

        return Math.min(layoutcnt, 5);// 한행당 최대 5개로 제한
    }


    /**
     * RadioButton 형태로 구성된 UI를 구성해주는 api
     * @param context
     * @param data
     * @param mainview
     * @param addedView
     * @param enablableView
     */
    public static void setRadioButtonHorizontalView(Context context, final ArrayList<HashMap<String,Object>> data, final LinearLayout mainview, View addedView, final View enablableView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainview.setOrientation(LinearLayout.VERTICAL);
        mainview.removeAllViews();

        if (data == null || data.size() == 0) {
            return;
        }

        mainview.setTag(R.id.itemptotalcnt, data.size());
        final int dividecnt = getCheckBoxCount(data);
        mainview.setTag(R.id.itempcnt,dividecnt);
        mainview.setTag(R.id.itemdata, data);
        int layoutcnt = (data.size()-1)/dividecnt+1;

        if (addedView != null) {
            layoutcnt++;
        }
        else {
            if (enablableView != null) {
                enablableView.setVisibility(View.GONE);
            }
        }

        if (enablableView != null) {
            enablableView.setEnabled(false);
            if (enablableView instanceof EditText) {
                ((EditText) enablableView).setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
        }

        LinearLayout[] llayout = new LinearLayout[layoutcnt];

        for (int i=0;i<llayout.length;i++) {
            llayout[i] = new LinearLayout(context);
            llayout[i].setOrientation(LinearLayout.HORIZONTAL);
            llayout[i].removeAllViews();
        }

        for (int i=0;i<data.size();i++) {
            View v = inflater.inflate(R.layout.adapter_check_option_item, mainview, false);
            if (llayout.length > 1) {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = Dip2Pixel(context, 45);

                v.setLayoutParams(params);
            }
            TextView txtview = (TextView)v.findViewById(R.id.text_chnl);
            txtview.setText((String) data.get(i).get(Vzon.OPT_NAME));
            v.setTag(data.get(i).get(Vzon.OPT_CODE));
            llayout[i/dividecnt].addView(v);
        }


        if (addedView != null) {

            int len = 0;
            for (int i=0;i<llayout[llayout.length-2].getChildCount();i++) {
                len += getActualWidth(llayout[llayout.length-2].getChildAt(i).findViewById(R.id.text_chnl));
            }

            int tempidx = llayout.length - 1;

            if (len < MAX_WIDTH*getDensity(context)) { // 직전 라인의 최대 넚이가 넘지 않으면 같은 라인에 새로운 View를 추가
                tempidx = llayout.length - 2;
            }

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.height = Dip2Pixel(context, (tempidx > 0)? 45:57);

            addedView.setLayoutParams(params);

            llayout[tempidx].addView(addedView);
        }

        for (int i=0;i<llayout.length;i++) {
            mainview.addView(llayout[i]);
        }

        int clickidx = -1;

        if (mainview.getTag(R.id.itemclickidx) != null) {
            clickidx = Integer.parseInt(mainview.getTag(R.id.itemclickidx).toString());
        }

        mainview.setTag(R.id.itemcode, ""); // 값이 설정되기 전에 공백으로 세팅

        for (int i=0;i<data.size();i++) {
            final int tempcnt = i;
            final int enableidx = clickidx;
            ((LinearLayout)mainview.getChildAt(i/dividecnt)).getChildAt(i % dividecnt).findViewById(R.id.join_chnl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int j = 0; j < data.size(); j++) {
                        View view = ((LinearLayout)mainview.getChildAt(j/dividecnt)).getChildAt(j % dividecnt).findViewById(R.id.check_chnl);
                        if (j == tempcnt) {
                            view.setSelected(true);
                            mainview.setTag(R.id.itemcode, data.get(j).get(Vzon.OPT_CODE));

                            if (enablableView != null && enablableView.getVisibility() == View.VISIBLE) {
                                if (enableidx == tempcnt || tempcnt == data.size()-1) {
                                    enablableView.setEnabled(true);
                                }
                                else {
                                    enablableView.setEnabled(false);
                                    enablableView.clearFocus();
                                    if (enablableView instanceof EditText) {
                                        ((EditText) enablableView).setText("");
                                    }

                                }
                            }

                        } else {
                            view.setSelected(false);
                        }
                    }
                }
            });

        }
    }

    /**
     * checkbox 형태로 구성된 UI를 구성해주는 api
     * @param context
     * @param data
     * @param mainview
     * @param delimiter 선택된 값의 구분자
     */
    public static void setCheckBoxHorizontalView(final Context context, final ArrayList<HashMap<String,Object>> data, final LinearLayout mainview, final String delimiter) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainview.setOrientation(LinearLayout.VERTICAL);
        mainview.removeAllViews();

        if (data == null || data.size() == 0) {
            return;
        }

        mainview.setTag(R.id.itemptotalcnt, data.size());
        final int dividecnt = getCheckBoxCount(data);
        mainview.setTag(R.id.itempcnt,dividecnt);
        mainview.setTag(R.id.itemdata, data);
        int layoutcnt = (data.size()-1)/dividecnt+1;

        LinearLayout[] llayout = new LinearLayout[layoutcnt];

        for (int i=0;i<llayout.length;i++) {
            llayout[i] = new LinearLayout(context);
            llayout[i].setOrientation(LinearLayout.HORIZONTAL);
            llayout[i].removeAllViews();
        }

        mainview.setTag(R.id.itemcode, ""); // 값이 설정되기 전에 공백으로 세팅

        final ArrayList<String> templist = new ArrayList<String>();
        for (int i=0;i<data.size();i++) {
            templist.add("");

            View v = inflater.inflate(R.layout.adapter_check_box_item, mainview, false);
            if (llayout.length > 1) {
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = Dip2Pixel(context, 45);

                v.setLayoutParams(params);
            }
            CheckBox cbview = (CheckBox)v.findViewById(R.id.check_chnl);
            cbview.setText((String) data.get(i).get(Vzon.OPT_NAME));

            final int tempint = i;

            cbview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    String str = "";
                    if (isChecked) {
                        templist.set(tempint, (String)data.get(tempint).get(Vzon.OPT_CODE));
                    }
                    else {
                        templist.set(tempint,"");
                    }
                    for (String tempstr:templist) {
                        if (!TextUtils.isEmpty(tempstr)) {
                            if (TextUtils.isEmpty(str)) {
                                str = tempstr;
                            } else {
                                str += (delimiter + tempstr);
                            }
                        }
                    }
                    mainview.setTag(R.id.itemcode, str);
                }
            });

            v.setTag(data.get(i).get(Vzon.OPT_CODE));
            llayout[i/dividecnt].addView(v);
        }

        for (int i=0;i<llayout.length;i++) {
            mainview.addView(llayout[i]);
        }

    }

    // 실제 Code값과 화면의 체크박스의 위치 idx값이 달라질수 있어서 Code값을 비교하여 인덱스값을 가져옴
    public static int getSelectedCheckBoxIndex(ArrayList<HashMap<String,Object>> data, String checkval) {
        int idx = -1;
        for (int i=0;i<data.size();i++) {
            String code = (String) data.get(i).get(Vzon.OPT_CODE);

            if (code.equals(checkval)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public static int getActualHeight(View view){ //스크롤뷰 등 뷰의 실제 높이를 가져옴.
        view.measure(0, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public static int getActualWidth(View view){ //스크롤뷰 등 뷰의 실제 넓이를 가져옴.
        view.measure(0, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }
    public static void setdefaultSelected(LinearLayout lineaview) {
        int cnt = Integer.parseInt(lineaview.getTag(R.id.itempcnt).toString());
        for (int i = 0; i < cnt; i++) {
            (((LinearLayout) lineaview.getChildAt(i / cnt)).getChildAt(i % cnt).findViewById(R.id.check_chnl)).setSelected(false);
        }
        lineaview.setTag(R.id.itemcode, "");
    }

}
