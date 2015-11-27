package com.huang.views.indexListview;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexBar
{

	private float myBarWidth;
	private float myBarMargin;
	private float myToastPadding;
	private float myToastWidth;
	private float myToastHeight;
	private float myToastTextSize;
	private float myDensity;
	private float myTextDensity;
	private float myAlpha;
	/**
	 * bar round rect
	 */
	private RectF myBarRectF;
	private float myBarRectFCorner;
	private float myBarIndexTextSize;
	/**
	 * bind Listview
	 */
	private ListView myListView;
	private int myListViewWidth;
	private int myListViewHeight;

	/**
	 * 来自adpter的索引条文本string数组  default #-Z
	 */
	private String[] mySection ;
	private SectionIndexer mySectionIndexer;
	private int myCurrentSelect = -1;
	//default config
	private final int  DEFALUT_BAR_WIDTH = 20;
	private final int  DEFALUT_BAR_MARGIN = 5;
	private final int  DEFALUT_TOAST_PADDING = 15;
	private final int 	DEFALUT_TOAST_WIDTH = 30;
	private final int 	DEFALUT_TOAST_TEXT_SIZE = 20;
	private final float  DEFALUT_BAR_ALPHA= 0f;
	private final float  DEFALUT_BAR_RECTF_CORNER= 5f;
	private final float  DEFALUT_BAR_INDEX_TEXT_SIZE= 12;
	private final int  DEFALUT_BAR_INDEX_TEXT_Color= Color.BLACK;
	private final int  DEFALUT_BAR_BG_Color= Color.GRAY;
	
	public IndexBar(Context context, IndexListView indexListView)
	{
		//init config
	    myDensity = context.getResources().getDisplayMetrics().density;  
	    myTextDensity = context.getResources().getDisplayMetrics().scaledDensity;  
		this.myListView = indexListView;
		
		
		myBarWidth = DEFALUT_BAR_WIDTH * myDensity; // 索引条宽度  
		myBarMargin = DEFALUT_BAR_MARGIN * myDensity;// 索引条间距  
		myToastWidth = DEFALUT_TOAST_WIDTH * myDensity;
		myToastHeight = myToastWidth;
		myToastTextSize = myToastWidth / 3 * 2;
		myToastPadding = myToastWidth / 2;
		myAlpha = DEFALUT_BAR_ALPHA * 255; 
		myBarRectFCorner = DEFALUT_BAR_RECTF_CORNER * myDensity;
		myBarIndexTextSize = DEFALUT_BAR_INDEX_TEXT_SIZE * myTextDensity;
	}

	/**
	 * 绘制索引条
	 * @param canvas from IndexListview ondraw canvas
	 */
	public void draw(Canvas canvas)
	{
		if(mySection != null && mySection.length >= 0)
		{
			drawRectF(canvas);
			drawIndexText(canvas);
			if(myCurrentSelect >= 0)//说明选中了
			{
				drawCenterToast(canvas,mySection[myCurrentSelect]);
			}
		}
	}

	
	/**
	 * 绘制索引条的背景框
	 */
	private void drawRectF(Canvas canvas)
	{
		Paint RectFPaint = new Paint();
		RectFPaint.setColor(DEFALUT_BAR_BG_Color);
		RectFPaint.setAlpha((int) myAlpha);
		RectFPaint.setAntiAlias(true);  
		
		canvas.drawRoundRect(myBarRectF, myBarRectFCorner, myBarRectFCorner, RectFPaint);
	}

	/**
	 * 绘制索引条里面的文本序列
	 */
	private void drawIndexText(Canvas canvas)
	{
		Paint indexPaint = new Paint();  
		indexPaint.setColor(DEFALUT_BAR_INDEX_TEXT_Color);  
		indexPaint.setAntiAlias(true);  
		indexPaint.setTextSize(myBarIndexTextSize);  
         
		float BarX = myBarRectF.left;
		float BarY = myBarRectF.top + myBarMargin ; 
	    float gap = (myBarRectF.height() - 2 * myBarMargin)  / mySection.length; 

		
		for(int i = 0 ; i < mySection.length; i++)
		{
			String  indexStr = mySection[i];
			float textX = BarX + (myBarWidth - indexPaint.measureText(indexStr)) / 2;
			canvas.drawText(indexStr, textX, BarY + gap * i - indexPaint.ascent(), indexPaint);
		}
		
		
	}
	
	private void drawCenterToast(Canvas canvas, String selectString)
	{
		Paint toastBgPaint = new Paint(); // 用来绘画所以条背景的画笔  
        toastBgPaint.setColor(Color.BLACK);// 设置画笔颜色为黑色  
        toastBgPaint.setAlpha(96); // 设置透明度  
        toastBgPaint.setAntiAlias(true);// 设置抗锯齿  
        toastBgPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0)); // 设置阴影层
        
        float toastLength = myToastWidth + 2 * myToastPadding;
        float toastBgRectfLeft = (myListViewWidth -  toastLength) / 2;
        float toastBgRectfTop = (myListViewHeight - toastLength ) / 2;
        float toastBgRectfRight = toastBgRectfLeft + toastLength;
        float toastBgRectfBottom = toastBgRectfTop + toastLength;
        RectF toastBgRectf = new RectF(toastBgRectfLeft, toastBgRectfTop, toastBgRectfRight, toastBgRectfBottom);
		canvas.drawRoundRect(toastBgRectf, myBarRectFCorner , myBarRectFCorner , toastBgPaint);
		
		
		Paint toastTextPaint = new Paint(); // 用来绘画索引字母的画笔  
		toastTextPaint.setColor(Color.WHITE); // 设置画笔为白色  
		toastTextPaint.setAntiAlias(true); // 设置抗锯齿  
		toastTextPaint.setTextSize(myToastTextSize); // 设置字体大小  
		float textX = toastBgRectf.left + (toastLength - toastTextPaint.measureText(selectString)) / 2;
		float textY = toastBgRectf.top + myToastPadding - toastTextPaint.ascent() + 1;
		canvas.drawText(selectString, textX, textY, toastTextPaint);
		
	}

	
	//get the adapter section
	public void setAdapter(ListAdapter adapter)
	{
		if(adapter instanceof IndexListViewAdapter)
		{
			mySectionIndexer = (SectionIndexer) adapter;
			mySection = (String[] ) mySectionIndexer.getSections();
		}
		
	}
	public void onSizeChanged(int bindListViewWidth, int bindListViewHeight)
	{  
        myListViewWidth = bindListViewWidth;  
        myListViewHeight = bindListViewHeight;  
        //init the BarRectF
        myBarRectF = new RectF(bindListViewWidth - myBarMargin - myBarWidth, myBarMargin , bindListViewWidth - myBarMargin, bindListViewHeight - myBarMargin );
	}

	public void onTouchEvent(MotionEvent ev)
	{
		switch(ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(contains(ev.getX(), ev.getY()) == true)
			{
				myCurrentSelect = getSelectPointIndex(ev.getY());
				//take the listview to right place
				myListView.setSelection(mySectionIndexer.getPositionForSection(myCurrentSelect));
			}
			break;
		case MotionEvent.ACTION_UP:
			myCurrentSelect = -1;
			break;
		}
		
	}
	
	 private boolean contains(float x, float y) 
	 {  
	        // Determine if the point is in index bar region, which includes the  
	        // right margin of the bar  
	        return (x >= myBarRectF.left && y >= myBarRectF.top && y <= myBarRectF.top  
	                + myBarRectF.height());  
	 }  

	private int getSelectPointIndex(float touchY)
	{
	    float gap = (myBarRectF.height() - 2 * myBarMargin)  / mySection.length; 
	    int selectIndex = (int) ((touchY - myBarRectF.top) / gap);
	    selectIndex = selectIndex >= mySection.length ? mySection.length - 1 : selectIndex;
		return selectIndex ;
	}  
	  
}
