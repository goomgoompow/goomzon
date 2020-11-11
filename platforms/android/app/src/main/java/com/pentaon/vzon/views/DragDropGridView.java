package com.pentaon.vzon.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.pentaon.vzon.activity.PictureListActivity;

public class DragDropGridView extends GridView
{
	private Context mcontext;
	private int selectItemIndex = -1;
	private ImageView dragView;
	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;
	private Point touchPosition = new Point(0, 0);
	private Point touchPositionOffset = new Point(0, 0);
	private OnDropListener onDropListener;
	
	private float mSmallImagePlace;
	
	private PictureListActivity mPictureListActivity;
	// Drop 후에 포지션	
	private int mAfterX;
	private int mAfterY;
	public DragDropGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}
	
	
	public DragDropGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	
	
	public DragDropGridView(Context context)
	{
		super(context);
		init(context);
	}
	
	
	private void init(Context context)
	{
		mcontext = context;
		setOnItemLongClickListener(onItemLongClickListener);
		
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		windowParams = new WindowManager.LayoutParams();
//		windowParams.gravity = Gravity.LEFT | Gravity.TOP;
		windowParams.gravity = Gravity.CENTER_VERTICAL;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.format = PixelFormat.TRANSLUCENT;
		float height;
		height =  getResources().getDisplayMetrics().heightPixels;
		Log.d("ptomobile", "mDeviceHeight = "  +  height);
		
		mSmallImagePlace = (float) (height * (0.8));
		Log.d("ptomobile", "mSmallImagePlace = "  +  mSmallImagePlace);
		
		mPictureListActivity = new PictureListActivity();
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e)
	{
		int x = (int) e.getX();
		int y = (int) e.getY();
		
		switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				touchPosition.set(x, y);
				break;
		}
		return super.onInterceptTouchEvent(e);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		int x = (int) e.getX();
		int y = (int) e.getY();
		
		switch (e.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				doDragView(x, y);
				break;
			
			case MotionEvent.ACTION_UP:
				doDropView(x, y);
				break;
			
			case MotionEvent.ACTION_CANCEL:
				setNullDragView();
				break;
			
			default:
				break;
		}
		return super.onTouchEvent(e);
	}
	
	
	// 드래그 하는 동안 보일 view
	private void doMakeDragview()
	{
		View item = getChildAt(selectItemIndex - getFirstVisiblePosition()); //  getFirstVisiblePosition() 이 없으면 스크롤 됐을 때 문제 생김
		item.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
		
		ImageView image = new ImageView(mcontext);
		image.setBackgroundColor(Color.parseColor("#00000000"));
		image.setImageBitmap(bitmap);
		
		touchPositionOffset.x = (int) (item.getWidth() * 0.5);
		touchPositionOffset.y = (int) ((item.getHeight() * 0.5) -mSmallImagePlace);
		
		windowParams.x = touchPosition.x - touchPositionOffset.x;
		windowParams.y = touchPosition.y - touchPositionOffset.y;
		
		Log.d("ptomobile","touchPosition.x" +touchPosition.x);
		Log.d("ptomobile","touchPosition.y" +touchPosition.y);
		Log.d("ptomobile","touchPositionOffset.x" +touchPositionOffset.x);
		Log.d("ptomobile","touchPositionOffset.y" +touchPositionOffset.y);
		Log.d("ptomobile","windowParams.x" +windowParams.x);
		Log.d("ptomobile","windowParams.y" +windowParams.y);
		Log.d("ptomobile"," ========= ");
		windowManager.addView(image, windowParams);
		dragView = image;
		
	}
	
	
	// 드래그하기
	private void doDragView(int x, int y)
	{
		if (dragView == null)
			return;
		
		windowParams.x = x - touchPositionOffset.x;
		windowParams.y = y - touchPositionOffset.y;
		windowManager.updateViewLayout(dragView, windowParams);
	}
	
	
	// 드랍
	private void doDropView(int x, int y)
	{
		mAfterX = x;
		mAfterY = y;
		// 1430 320
		if (dragView == null)
			return;
		
		int toIndex = pointToPosition(x, y);
		if (toIndex <= INVALID_POSITION)
		{
			setNullDragView();
			return;
		}
		onDropListener.drop(selectItemIndex, toIndex);
		
		
		setNullDragView();
	}
	
	
	// 드래그 뷰 지우기
	private void setNullDragView()
	{
		if (dragView != null)
		{
			windowManager.removeView(dragView);
			dragView = null;
		}
	}
	
	
	/**
	 * 드랍 후에 실행 할 리스터
	 * @param listener
	 */
	public void setOnDropListener(OnDropListener listener)
	{
		onDropListener = listener;
	}
	
	/****************
	 * Listener
	 ***************/
	
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3)
		{
			if (position <= INVALID_POSITION)
				return true;
			
			selectItemIndex = position;
			
            doMakeDragview();
			return true;
		}
	};
	
	/************
	 * 인터페이스
	 ************/
	public interface OnDragListener
	{
		void drag(int from, int to);
	}
	
	public interface OnDropListener
	{
		void drop(int from, int to);
	}
}
