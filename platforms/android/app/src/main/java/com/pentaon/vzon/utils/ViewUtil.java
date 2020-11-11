package com.pentaon.vzon.utils;

/**
 * Created by Pentaon on 15,5월,2018
 */

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * 뷰 VISIBLE,INVISIBLE,GONE  코드 깔끔하게 하기
 * @author dohun
 *
 */
public class ViewUtil {
    public static View VISIBLE(View view)
    {
        if(view==null) return null;
        view.setVisibility(View.VISIBLE);
        return view;
    }
    public static View GONE(View view)
    {
        if(view==null) return null;
        view.setVisibility(View.GONE);
        return view;
    }
    public static View INVISIBLE(View view)
    {
        if(view==null) return null;
        view.setVisibility(View.INVISIBLE);
        return view;
    }
    /**
     * * 뷰 투명효과주기
     * 0.0f ~ 1.0f 까지
     * @param view
     * @param alpha
     * @return view
     */
    public static View SET_ALPHA(View view, float alpha)
    {
        AlphaAnimation tempAlpha = new AlphaAnimation(alpha, alpha);
        tempAlpha.setDuration(1000);
        tempAlpha.setFillAfter(true);
        view.startAnimation(tempAlpha);
        return view;
    }
}
