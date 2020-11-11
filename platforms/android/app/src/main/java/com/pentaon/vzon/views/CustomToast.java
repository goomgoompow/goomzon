package com.pentaon.vzon.views;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pentaon.vzon.R;

/**
 * Created by Pentaon on 19,6월,2018
 */
public class CustomToast extends Toast {

    private static final int COLOR_BLUE = 0xff2ed0f9;
    private static final int COLOR_RED = 0Xfff65b37;
    private static final int FONT_SIZE = 17;

    private Context mContext;
    private TextView mTextViewToastState;
    private TextView mTextViewSerialNumber;

    public CustomToast(Context context) {
        super(context);
        this.mContext = context;
    }

    public void showToast(String message,String result, boolean confirmed)
    {
        if(mTextViewToastState ==null)
        {
            View view = null;
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.serial_number_toast,null);

            mTextViewToastState = (view).findViewById(R.id.act_scanbarcode_toast_tv_confirm_state);
            mTextViewSerialNumber = (view).findViewById(R.id.act_scanbarcode_toast_confirmed_serial_number);

            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            /*Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int yOffset = size.y;*/
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);

            int yOffset = displayMetrics.heightPixels/4;

            setView(view);
            setDuration(Toast.LENGTH_SHORT);
            setGravity(Gravity.CENTER_VERTICAL,0,yOffset);
        }
        isConfirmed(confirmed);
        mTextViewToastState.setText(message);
        mTextViewSerialNumber.setText(result);
        this.show();
    }

    private void isConfirmed(boolean confirmed)
    {
        if(confirmed)
        {
            mTextViewToastState.setTextColor(COLOR_BLUE);
            mTextViewSerialNumber.setTextColor(COLOR_BLUE);
        }else
        {
            mTextViewToastState.setTextColor(COLOR_RED);
            mTextViewSerialNumber.setTextColor(COLOR_RED);
        }
    }

}
