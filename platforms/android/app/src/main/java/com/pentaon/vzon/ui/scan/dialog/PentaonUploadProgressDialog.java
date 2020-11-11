package com.pentaon.vzon.ui.scan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pentaon.vzon.R;
import com.pentaon.vzon.network.UIThread;
import com.pentaon.vzon.utils.AppConstants;
import com.pentaon.vzon.views.PentaonProgressBar;

//  파일 업로드 진행을 보여주는 UI Dialog 
public class PentaonUploadProgressDialog extends Dialog {

    /*
     * Layout
     */
    private TextView tv_title;    // 타이틀
    private Button cancel;    // 취소
    private Button ok;        // 확인
//    private ImageView btn_close; // 상단 x버튼
    private String mTitle;
    private PentaonProgressBar pb = null;
    
    protected CallBack fcallback;
    private int ALERT = AppConstants.ALERT_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setCancelable(false);
        setContentView(R.layout.progress_dialog);
        setLayout();
        setTitle(mTitle);
    }

    public PentaonUploadProgressDialog(Context context) {
        // Dialog 배경을 투명처리해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public PentaonUploadProgressDialog(Context context, String title) {
        super(context, android.R.style.Theme_DeviceDefault_Light);
        this.mTitle = title;
    }
    public PentaonUploadProgressDialog(Context context, String title, int ALERT) {
    	super(context, android.R.style.Theme_Translucent_NoTitleBar);
    	this.mTitle = title;
    	this.ALERT = ALERT;
    }

    public PentaonUploadProgressDialog(Context context, String title, int ALERT, CallBack fcallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.ALERT = ALERT;
        this.fcallback = fcallback;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    
    
    private RelativeLayout sending = null;
    private RelativeLayout sendFail = null;
    
    /**
     * 전송실패 화면전환
     */
    public void setFailLayout(){
    	sending  = (RelativeLayout) findViewById(R.id.sending);
    	sendFail = (RelativeLayout) findViewById(R.id.sendFail);
    	sending.setVisibility(View.GONE);
    	sendFail.setVisibility(View.VISIBLE);
    	ok.setVisibility(View.VISIBLE);
    	cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(onclick);
        ok.setOnClickListener(onclick);
        cancel.setEnabled(true);
        ok.setEnabled(true);
    }
    
    /**
     * 전송중인 화면전환
     */
    public void setSendLayout(){
    	
    	sending  = (RelativeLayout) findViewById(R.id.sending);
    	sendFail = (RelativeLayout) findViewById(R.id.sendFail);
    	sending.setVisibility(View.VISIBLE);
    	sendFail.setVisibility(View.GONE);
    	cancel.setVisibility(View.VISIBLE);
    	ok.setVisibility(View.GONE);
        cancel.setOnClickListener(onclick);
        cancel.setEnabled(true);
        pb.setProgress(0);      // 전송화면 전환시 Progress bar 초기화
        pb.setText("");
    }
 
    /*
     * Layout
     */
    private void setLayout() {

        pb       = (PentaonProgressBar) findViewById(R.id.pb);
        tv_title = (TextView) findViewById(R.id.sendingText);
        cancel   = (Button) findViewById(R.id.cancel);
        ok       = (Button) findViewById(R.id.ok);
//        btn_close = (ImageView) findViewById(R.id.btn_close);

        if (ALERT == AppConstants.ALERT_DEFAULT) {
        	ok.setVisibility(View.GONE);
        	cancel.setVisibility(View.GONE);
        	return;
        }

        if (ALERT == AppConstants.ALERT_CANCEL) {
        	ok.setVisibility(View.GONE);
        }

        if (ALERT == AppConstants.ALERT_OK) {
        	cancel.setVisibility(View.VISIBLE);
        }

        
        if (ALERT == AppConstants.ALERT_OKANDCANCEL) {
        	ok.setVisibility(View.VISIBLE);
        	cancel.setVisibility(View.VISIBLE);
        }
        
//        if (ALERT == ALERT_BTN_CLOSE) {
//        	btn_close.setVisibility(View.VISIBLE);
//        }

        cancel.setOnClickListener(onclick);
        ok.setOnClickListener(onclick);
//        btn_close.setOnClickListener(onclick);
    }

    /**
     * Progress 진행처리함
     * @param myProgress
     */
    public void setProgressValues(int myProgress){
        pb.setProgress(myProgress);
        pb.setText(myProgress + "%");
    }
    
    /**
     * Progress 진행처리함
     * @param myProgress
     */
    public void setProgressValues(int myProgress,boolean run){
    	pb.setProgress(myProgress);
    }
    
    /**
     *  Progress 진행처리함
     * @param myProgress
     * @param msg
     */
    public void setProgressValues(int myProgress,String msg){
    	pb.setProgress(myProgress);
    	pb.setText(msg+"  "+myProgress + "%");
//    	setTitle(msg);
    }

    public void setCancelButtonEnable(boolean isEnable) {
        cancel.setEnabled(isEnable);
    }


	private View.OnClickListener onclick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cancel:
				dismiss();
				if (fcallback != null) {
					fcallback.onPressButton(AppConstants.ALERT_CANCEL);
				}
				break;
			case R.id.ok:
				
				if(sending.isShown()){
					dismiss();
				}
				UIThread.getInstance().executeInUIThread(new Runnable() {
					@Override
					public void run() {
						if (fcallback != null) {
							fcallback.onPressButton(AppConstants.ALERT_OK);
						}
					}
				},10);
				break;
/*			case R.id.btn_close:
				dismiss();
				if(fcallback != null){
					fcallback.onPressButton(AppConstants.ALERT_BTN_CLOSE);
				}
				break;*/
            default:
			}
		}
	};


    public interface CallBack {
        void onPressButton(int btnIndex);
    }
}
