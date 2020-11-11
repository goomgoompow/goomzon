package com.pentaon.vzon.ui.scan.support;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pentaon.vzon.R;

import java.util.Calendar;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class UISupport {

    private static UISupport mSupoort = null;
    private static  Toast mToast;

    private final Calendar mCalendar = Calendar.getInstance();
    private Dialog mDialog = null;

    public static UISupport getInstance()
    {
        if(mSupoort==null) mSupoort = new UISupport();
        return mSupoort;
    }

    public static void showToastCustomViewCenter(Context ctx, String text, int top){
        cancelToastCustomView();
        mToast = new Toast(ctx);
        LayoutInflater layoutInflater = LayoutInflater.from(ctx);
        View view = layoutInflater.inflate(R.layout.toast_view, null);
        TextView txt = (TextView)view.findViewById(R.id.toast_txt);
        txt.setText(text);

        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP,0,top);
        mToast.setView(view);
        mToast.show();
    }


    public static void cancelToastCustomView(){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
