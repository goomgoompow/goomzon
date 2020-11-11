package com.pentaon.vzon.dataset;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by jh.Kim on 15,5월,2018
 */
public class GridListItem {
    public long id;
    public CharSequence gridauid;
    public Drawable gridicon;
    public CharSequence gridtitle;
    public int mChoice;

    public GridListItem(int id, CharSequence auid, Drawable icon, CharSequence title, int choice)
    {
        gridauid = auid;
        gridicon = icon;
        gridtitle = title;
        mChoice = choice;
    }


    public GridListItem(int id, CharSequence auid, Bitmap icon, CharSequence title, int choice)
    {
        gridauid = auid;
        gridicon = new BitmapDrawable(icon);
        gridtitle = title;
        mChoice = choice;
    }
}
