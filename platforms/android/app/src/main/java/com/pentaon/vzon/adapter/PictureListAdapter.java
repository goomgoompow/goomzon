package com.pentaon.vzon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pentaon.vzon.R;
import com.pentaon.vzon.dataset.GridListItem;

import java.util.ArrayList;

/**
 * Created by Pentaon on 15,5월,2018
 */
public class PictureListAdapter extends BaseAdapter {
    public static ArrayList<GridListItem> gridListItems = new ArrayList<GridListItem>();
    private LayoutInflater layoutinflater;
    private Context mContext;

    public PictureListAdapter(Context context)
    {
        mContext = context;
        layoutinflater = LayoutInflater.from(context);
    }

    /**
     * 항목 추가
     * @param id 고유아이디
     * @param icon 아이콘 drawable
     * @param title 앱 이름
     */
    public void addItem(CharSequence id, Drawable icon, CharSequence title, int choice)
    {
        GridListItem item = new GridListItem(getCount(), id, icon, title, choice);
        gridListItems.add(item);
    }

    /**
     * 항목 추가
     * @param id 고유아이디
     * @param icon 아이콘 리소스 id
     * @param title 앱 이름
     */
    public void addItem(CharSequence id, int icon, CharSequence title, int choice)
    {
        GridListItem item = new GridListItem(getCount(), id, mContext.getResources().getDrawable(icon), title, choice);
        gridListItems.add(item);
    }

    /**
     * 항목 추가
     * @param id 고유아이디
     * @param icon 아이콘 bitmap
     * @param title 앱 이름
     */
    public void addItem(CharSequence id, Bitmap icon, CharSequence title, int choice)
    {
        GridListItem item = new GridListItem(getCount(), id, icon, title,  choice);
        gridListItems.add(item);
    }

    /**
     * 특정 위치에 아이템 넣기
     * @param index
     * @param item
     */
    public void addItemAt(int index, GridListItem item)
    {
        gridListItems.add(index, item);
    }

    /**
     * 아이콘 변경
     * @param index 위치
     * @param icon 아이콘 drawable
     */
    public void setItemIcon(int index, Drawable icon)
    {
        gridListItems.get(index).gridicon = icon;
        notifyDataSetChanged();
    }

    /**
     * 선택된곳 표시
     * @param index 위치
     * @param color 컬러
     */
    public void setItemBackGroundColor(int index, int color)
    {
        gridListItems.get(index).mChoice = color;
        notifyDataSetChanged();
    }
    /**
     * 특정 위치의 아이템 지우기
     * @param index
     */
    public GridListItem removeItemAt(int index)
    {
        return gridListItems.remove(index);
    }

    /**
     * 항목들 전부 지우기
     */
    public void clear()
    {
        gridListItems.clear();
    }

    @Override
    public int getCount()
    {
        return gridListItems.size();
    }

    @Override
    public Object getItem(int index)
    {
        return gridListItems.get(index);
    }

    @Override
    public long getItemId(int index)
    {
        return gridListItems.get(index).id;
    }

    /**
     * 항목의 고유아이디
     * @param  index 선택된 행
     * @return
     */
    public CharSequence getItemAuId(int index)
    {
        return gridListItems.get(index).gridauid;
    }

    /**
     * 앱 이름 가져오기
     * @param index
     * @return
     */
    public CharSequence getItemTitle(int index)
    {
        return gridListItems.get(index).gridtitle;
    }


    /**
     * 아이콘 가져오기
     * @param index
     * @return
     */
    public Drawable getItemIcon(int index)
    {
        return gridListItems.get(index).gridicon;
    }


    @Override
    public View getView(int index, View convertView, ViewGroup parent)
    {
        View view = layoutinflater.inflate(R.layout.adapter_picture_list_item, null);

        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        RelativeLayout backGround= (RelativeLayout) view.findViewById(R.id.choose_thumbnail);
        icon.setImageDrawable(gridListItems.get(index).gridicon);
        if(gridListItems.get(index).mChoice == 1)
        {
            backGround.setBackgroundColor(mContext.getResources().getColor(R.color.act_native_camera_preview_vertex));
        } else{
            backGround.setBackgroundColor(mContext.getResources().getColor(R.color.cache_hint_color));
        }
        /*TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(gridListItems.get(index).gridtitle);*/

        return view;
    }
}
