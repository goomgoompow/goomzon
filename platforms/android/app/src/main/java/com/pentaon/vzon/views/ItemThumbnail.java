package com.pentaon.vzon.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.pentaon.vzon.R;

public class ItemThumbnail extends RelativeLayout{

  private int mId;
  private final LayoutInflater mInflater;
  private ImageView mImageThumbnail;
  private RelativeLayout mParentLayout;

  public ItemThumbnail(Context context) {
    super(context);
    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    init();
  }

  public ItemThumbnail(Context context, AttributeSet attrs) {
    super(context, attrs);
    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    init();
  }

  public int getItemId() {
    return mId;
  }
  public void setItemId(int id) {
    this.mId = id;
  }
  public void setImage(Bitmap bitmap)
  {
    mImageThumbnail.setImageBitmap(bitmap);
  }
  public void setBgColor(int color)
  {
    mParentLayout.setBackgroundColor(color);
  }

  private void init() {
    ViewGroup root= (ViewGroup) mInflater.inflate(R.layout.item_thumbnail,this,true);
    mParentLayout = (RelativeLayout) root.getChildAt(0);
    mImageThumbnail= mParentLayout.findViewById(R.id.image_thumbnail);
  }
}
