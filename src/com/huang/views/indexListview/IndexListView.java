package com.huang.views.indexListview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IndexListView extends ListView
{
	private IndexBar myIndexBar ;

	public IndexListView(Context context)
	{
		this(context,null);

	}
	
	public IndexListView(Context context, AttributeSet attrs)
	{
		this(context, attrs,0);
	}

	public IndexListView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(myIndexBar != null)
		{
			myIndexBar.draw(canvas);
		}
		super.onDraw(canvas);
	}

	
	@Override
	public void setAdapter(ListAdapter adapter)
	{
		if(myIndexBar == null)
		{
			myIndexBar = new IndexBar(getContext(), this);
			myIndexBar.setAdapter(adapter);
		}
		super.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int thisWidth, int thisHeight, int oldw, int oldh)
	{
		if(myIndexBar != null)
		{
			myIndexBar.onSizeChanged(thisWidth, thisHeight);
		}
		super.onSizeChanged(thisWidth, thisHeight, oldw, oldh);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		myIndexBar.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}
	
}
