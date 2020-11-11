package com.pentaon.vzon.ui.scan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.AppConstants;


/**
 * 일반 알림 팝업창
 */
public class PentaonAlertCommonDialog extends Dialog {

    protected CallBack fcallback;
    private int ALERT = AppConstants.ALERT_OK;
    private String[] btnName;
    private boolean isBackgroundDim = true; // 팝업창이 떴을 때 뒷배경이 보이지 않게 할 때 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isBackgroundDim) {
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.5f;//0.8f
            getWindow().setAttributes(lpWindow);
        } else {
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.3f;//0.5f
            getWindow().setAttributes(lpWindow);
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        setContentView(R.layout.dialog_pentaon_alert_common);
        setLayout();
        setTitle(mTitle);
        if (TextUtils.isEmpty(mSContent)) {
            setContent(mContent);
            if (TextUtils.isEmpty(mAddContent) == false) {  // #1032
                setAddContent(mAddContent);
            }
        } else {
            setSpannedContent(mSContent);
        }
        setCancelable(false); // shlee1219 BACK KEY 막기
    }

    public PentaonAlertCommonDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public PentaonAlertCommonDialog(Context context, String title, Spanned content, int ALERT) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mSContent = content;
        this.ALERT = ALERT;
    }

    public PentaonAlertCommonDialog(Context context, String title, String content, int ALERT) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mContent = content;
        this.ALERT = ALERT;
    }

    public PentaonAlertCommonDialog(Context context, int title, String content, int ALERT) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = this.getContext().getResources().getString(title);
        this.mContent = content;
        this.ALERT = ALERT;
    }

    public PentaonAlertCommonDialog(Context context, int title, int content, int ALERT) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = this.getContext().getResources().getString(title);
        this.mContent = this.getContext().getResources().getString(content);
        this.ALERT = ALERT;
    }

    public PentaonAlertCommonDialog(Context context, String title, String content, int ALERT, CallBack fcallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mContent = content;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
    }

    // 내용1,2 사이에 라인이 그려진다.(content: 내용1, addContent: 내용2)
    public PentaonAlertCommonDialog(Context context, String title, String content, String addContent, int ALERT, CallBack fcallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mContent = content;
        this.mAddContent = addContent;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
    }

    public PentaonAlertCommonDialog(Context context, String title, Spanned content, int ALERT, CallBack fcallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mSContent = content;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
    }

    int mLeftMargin = 0;
    boolean misLeft = false;

    public PentaonAlertCommonDialog(Context context, String title, Spanned content, int ALERT, CallBack fcallback, boolean left, int leftgap, String[] btnName) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mSContent = content;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
        misLeft = left;
        this.btnName = btnName;
    }

    public PentaonAlertCommonDialog(Context context, int title, int content, int ALERT, CallBack fcallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = this.getContext().getResources().getString(title);
        this.mContent = this.getContext().getResources().getString(content);
        this.ALERT = ALERT;
        this.fcallback = fcallback;
    }

    public PentaonAlertCommonDialog(Context context, String title, String content, int ALERT, CallBack fcallback, String[] btnName) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.mTitle = title;
        this.mContent = content;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
        //default버턴명 변경함
        this.btnName = btnName;
        /*
        if(ALERT_OKANDCANCEL == ALERT){
//			setButtonText(ALERT_OK,buttonName[0]);
//			setButtonText(ALERT_CANCEL,buttonName[1]);
			btn_ok.setText(buttonName[0]);
			btn_cancel.setText(buttonName[1]);
		}
		*/
    }


    private void setTitle(String title) {
        tv_title.setText(title);
    }

    private void setContent(String content) {
        tv_contents.setText(content);
    }

    /**
     * 내용 아래에 라인을 추가하고 추가 내용을 붙인다
     * @param content 추가 내용
     */
    private void setAddContent(String content) {
        tv_add_line.setVisibility(View.VISIBLE);
        tv_add_contents.setVisibility(View.VISIBLE);
        tv_add_contents.setText(content);
    }

    private void setSpannedContent(Spanned content) {
        tv_contents.setText(content);
    }

    /**
     * 팝업창이 떴을 때 뒷배경이 보이지 않게 할 때 설정(기본값:true)
     *
     * @return
     */

    public void setBackgroundDim(boolean isBackgroundDim) {
        this.isBackgroundDim = isBackgroundDim;
    }

    /*
     * Layout
     */
    private TextView tv_title; //타이틀
    private TextView tv_contents;        //내용
    private View tv_add_line;            //추가 내용 라인
    private TextView tv_add_contents;    //추가 내용
    private Button btn_cancel;  //취소
    private Button btn_ok; //확인
    private String mTitle;
    private String mContent;
    private String mAddContent;
    private Spanned mSContent;
    //private ImageView btn_close; //X버튼


    /*
     * Layout
     */
    private void setLayout() {


        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_contents = (TextView) findViewById(R.id.tv_contents);
        tv_add_line = findViewById(R.id.tv_add_line);
        tv_add_contents = (TextView) findViewById(R.id.tv_add_contents);

        if (misLeft) {
            tv_contents.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        //btn_close = (ImageView) findViewById(R.id.btn_close);


        if (ALERT == AppConstants.ALERT_CANCEL) {
            btn_ok.setVisibility(View.GONE);
            if (btnName != null) {
                btn_cancel.setText(btnName[0]);
            }
        }

        if (ALERT == AppConstants.ALERT_OK) {
            btn_cancel.setVisibility(View.GONE);
            if (btnName != null) {
                btn_ok.setText(btnName[0]);
            }
        }

        if (ALERT == AppConstants.ALERT_OKANDCANCEL) {
            btn_ok.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
            if (btnName != null) {
                btn_ok.setText(btnName[0]);
                btn_cancel.setText(btnName[1]);
            }
        }

        if (ALERT == AppConstants.ALERT_OKANDCANCEL_REVERSE) {
            btn_ok.setVisibility(View.VISIBLE);
            btn_ok.setBackgroundResource(R.drawable.btn_normal);
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setBackgroundResource(R.drawable.btn_deep_color);
            if (btnName != null) {
                btn_ok.setText(btnName[0]);
                btn_cancel.setText(btnName[1]);
            }
        }

        btn_cancel.setOnClickListener(onclick);
        btn_ok.setOnClickListener(onclick);
        //btn_close.setOnClickListener(onclick);
    }


    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dismiss();
                    if (fcallback != null) {
                        fcallback.onPressButton(AppConstants.ALERT_CANCEL);
                    }
                    break;
                case R.id.btn_ok:
                    dismiss();
                    if (fcallback != null) {
                        fcallback.onPressButton(AppConstants.ALERT_OK);
                    }
                    break;
//			case R.id.btn_close:
//				dismiss();
//				if(fcallback != null){
//					fcallback.onPressButton(AppConstants.ALERT_BTN_CLOSE);
//				}
//				break;
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        dismiss();
    }

    public interface CallBack {
        public void onPressButton(int btnIndex);
    }

}
