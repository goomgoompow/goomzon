package com.pentaon.vzon.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.pentaon.vzon.R;
import com.pentaon.vzon.data.PhotoVO;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

  private static final String TAG = GridAdapter.class.getSimpleName();
  private final Activity mActivity;
  private final ArrayList<PhotoVO> mPhotoList;
  private final LayoutInflater mInflater;
  private int mDisplayWidth;
  private int mDisplayHeight;
  private LayoutParams mImageViewParam;

  public GridAdapter(Activity activity,
      ArrayList<PhotoVO> list) {

    mActivity = activity;
    mPhotoList = list;
    mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    DisplayMetrics displayMetrics = ((Context)mActivity).getResources().getDisplayMetrics();
    mDisplayWidth = displayMetrics.widthPixels;
    mDisplayHeight = displayMetrics.heightPixels;

  }

  @Override
  public int getCount() {
    return mPhotoList.size();
  }

  @Override
  public PhotoVO getItem(int position) {
    return mPhotoList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    final ViewHolder viewHolder;
    final PhotoVO photoVO = mPhotoList.get(position);

    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.act_image_loading_item_photo, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.imageView= convertView.findViewById(R.id.image_photo);
      mImageViewParam = (LayoutParams)viewHolder.imageView.getLayoutParams();
      mImageViewParam.width = mImageViewParam.height =mDisplayWidth/3;
      viewHolder.imageView.setLayoutParams(mImageViewParam);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    Glide.with(mActivity).load(photoVO.getImagePath())
        .listener(requestListener)
        .into(viewHolder.imageView);

    return convertView;
  }

  private RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
        boolean isFirstResource) {
      Log.d(TAG, "onLoadFailed: ");
      return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
        DataSource dataSource, boolean isFirstResource) {
      Log.d(TAG, "onResourceReady: ");
      Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
      RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
          .create(mActivity.getResources(), bitmap);
      roundedBitmapDrawable.setCornerRadius(25f);
      ((DrawableImageViewTarget) target).getView().setImageDrawable(roundedBitmapDrawable);
      return true;
    }
  };

  public static class ViewHolder {
    public ImageView imageView;
  }

}
