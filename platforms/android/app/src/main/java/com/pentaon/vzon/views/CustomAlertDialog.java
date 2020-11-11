package com.pentaon.vzon.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pentaon.vzon.R;

public class CustomAlertDialog extends Dialog {

  public static final int ONE_BUTTON = 1;
  public static final int TWO_BUTTON = 2;

  private Context mContext;
  private CustomDialogOneButtonListener mOneButtonListener;
  private CustomDialogTwoButtonListener mTwoButtonListener;
  private TextView mAlertMessage;
  private Button mButtonRight;
  private Button mButtonLeft;
  private Button mButtonNeutral;

  private int mId;
  private RelativeLayout mContainerOfButtons;
  private ConstraintLayout mContainer;
  private ConstraintLayout mConstraintTwoButtons;


  public CustomAlertDialog(Context context) {
    super(context);
    this.mContext = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_alert_dialog);
    this.getWindow().setBackgroundDrawableResource(R.color.transparent);
    mContainer = findViewById(R.id.container);
    mAlertMessage = findViewById(R.id.alert_dialog_message);
    mConstraintTwoButtons = findViewById(R.id.container_two_buttons);

    mButtonLeft = findViewById(R.id.alert_dialog_button_negative);
    mButtonRight = findViewById(R.id.alert_dialog_button_positive);
    mButtonNeutral = findViewById(R.id.alert_dialog_button_neutral);

    //---------------------------------------------------------
    // 한 activity(CustomalertDialog.CustomDialogButtonListener 구현)에
    // 여러 CustomDialog가 있을 경우 구분을 위해 identifier 생성
    //---------------------------------------------------------
    mId = (int) (Math.random() * 1000000);

    mButtonLeft.setOnClickListener(mButtonClickListener);
    mButtonRight.setOnClickListener(mButtonClickListener);
    mButtonNeutral.setOnClickListener(mButtonClickListener);

    mContainer.setBackground(getBackgroundDrawable(Color.WHITE, 25f, true, true, true, true));
    mContainerOfButtons = findViewById(R.id.alert_Dialog_button_container);
    mContainerOfButtons
        .setBackground(getBackgroundDrawable(0xff0077ff, 25f, false, false, true, true));
  }

  public CustomAlertDialog setOneButtonClickListener(CustomDialogOneButtonListener listener) {
    mOneButtonListener = listener;
    return this;
  }
  public CustomAlertDialog setTwoButtonClickListener(CustomDialogTwoButtonListener listener) {
    mTwoButtonListener = listener;
    return this;
  }

  /**
   * 다이얼로그에 표시할 버튼 수 지정
   */
  public void setButtons(int numOfButtons) {
    switch (numOfButtons) {
      case 1:
      default:
        mButtonNeutral.setVisibility(View.VISIBLE);
        mConstraintTwoButtons.setVisibility(View.INVISIBLE);
        mButtonNeutral.setClickable(true);
        mContainerOfButtons.setClickable(false);
        break;
      case 2:
        mConstraintTwoButtons.setVisibility(View.VISIBLE);
        mButtonNeutral.setVisibility(View.INVISIBLE);
        mContainerOfButtons.setClickable(true);
        mButtonNeutral.setClickable(false);
        break;
    }
  }

  public CustomAlertDialog setDialogMessage(int resID) {
    mAlertMessage.setText(resID);
    return this;
  }

  public CustomAlertDialog setDialogMessage(String message) {
    mAlertMessage.setText(message);
    return this;
  }


  public int getId() {
    return mId;
  }

  private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.alert_dialog_button_negative:
          mTwoButtonListener.onLeftButtonClick(mId);
          dismiss();
          break;
        case R.id.alert_dialog_button_positive:
          mTwoButtonListener.onRightButtonClick(mId);
          dismiss();
          break;
        case R.id.alert_dialog_button_neutral:
          mOneButtonListener.onNeutralButtonClick(mId);
          dismiss();
          break;
      }
    }
  };

  /**
   * color와 rounded corner가 적용된 GradientDrawable 객체를 반환
   *
   * @param color : 컬러 값
   * @param radius : rounded 정도
   * @param topLeft : 좌상단 코너 round 적용 여부
   * @param topRight : 우상단 코너 round 적용 여부
   * @param bottomLeft : 좌하단 코너 round 적용 여부
   * @param bottomRight : 우하단 코너 round 적용 여부
   * @return GradientDrawable 객체
   */
  private GradientDrawable getBackgroundDrawable(int color, float radius, boolean topLeft,
      boolean topRight, boolean bottomLeft, boolean bottomRight) {
    GradientDrawable gradientDrawable = new GradientDrawable();
    gradientDrawable.setColor(color);
    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
    float[] radii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
    if (topLeft) {
      radii[0] = radii[1] = radius;
    }
    if (topRight) {
      radii[2] = radii[3] = radius;
    }
    if (bottomLeft) {
      radii[4] = radii[5] = radius;
    }
    if (bottomRight) {
      radii[6] = radii[7] = radius;
    }
    gradientDrawable.setCornerRadii(radii);
    return gradientDrawable;
  }

  public interface CustomDialogOneButtonListener {

    void onNeutralButtonClick(int id);
  }

  public interface CustomDialogTwoButtonListener {

    void onLeftButtonClick(int id);

    void onRightButtonClick(int id);
  }
}
