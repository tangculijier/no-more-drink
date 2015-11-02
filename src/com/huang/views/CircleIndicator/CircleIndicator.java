/**  
 * viewpage翻页的知识圆点
 * @author lizheHuang 
 * @Date   time :2015年10月30日  下午9:42:55
 * @version 1.0
 */ 

package com.huang.views.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.View;

import com.huang.nodrinkmore.R;
import com.huang.util.LogUtil;
import com.huang.views.ShapeHolder;

public class CircleIndicator extends View
{

	
	/**
	 * 绑定的viewpager
	 */
	private ViewPager viewPager;
	/**
	 * 当前圆点list
	 */
	private List<ShapeHolder> tabItems;
	/**
	 * 滑动的圆点
	 */
	private ShapeHolder movingItem;
	
	/**
	 * 当前在第几个viewpage
	 */
	private int mCurItemPosition;
    private float mCurItemPositionOffset;
    
	 //config list
	private float myIndicatorRadius;
	private int myIndicatorMargin;
	private int myIndicatorBackground;
	private int myIndicatorSelectdBackground;
	private CircleIndicatorMode myIndicatorMode;
	private CircleIndicatorGravity myIndicatorGravity;
	
	//defalut value
	private final int DEFAULT_INDICATOR_RADIUS = 10;
	private final int DEFAULT_INDICATOR_MARGIN = 40;
	private final int DEFAULT_INDICATOR_GRAVITY = CircleIndicatorGravity.CENTER.ordinal();
	private final int DEFAULT_INDICATOR_BACKGROUND = Color.BLUE;
	private  final int DEFAULT_INDICATOR_SELECTD_BACKGROUND = Color.RED;
	private  final int DEFAULT_INDICATOR_MODE = CircleIndicatorMode.SOLO.ordinal();

	
	
	public CircleIndicator(Context context)
	{
		this(context,null);//用this不能用super
	}
	
	public CircleIndicator(Context context, AttributeSet attrs)
	{
		this(context,attrs,0);		
	}
	
	public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr)
	{		
		super(context, attrs, defStyleAttr);
		tabItems = new ArrayList();
		handleTypedArray(context,attrs);
	}

	private void handleTypedArray(Context context, AttributeSet attrs)
	{
		if(attrs == null)
		{
			return;
		}
		else
		{
			TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleIndicator);
			myIndicatorRadius = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_radius, DEFAULT_INDICATOR_RADIUS);
			myIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, DEFAULT_INDICATOR_MARGIN);
			myIndicatorBackground = typedArray.getColor(R.styleable.CircleIndicator_ci_background, DEFAULT_INDICATOR_BACKGROUND);
			myIndicatorSelectdBackground = typedArray.getColor(R.styleable.CircleIndicator_ci_selected_background, DEFAULT_INDICATOR_SELECTD_BACKGROUND);
			
		    int gravity = typedArray.getInt(R.styleable.CircleIndicator_ci_gravity,DEFAULT_INDICATOR_GRAVITY);
		    myIndicatorGravity = CircleIndicatorGravity.values()[gravity];//返回第gravity个enum
		    
	        int mode = typedArray.getInt(R.styleable.CircleIndicator_ci_mode,DEFAULT_INDICATOR_MODE);
	        myIndicatorMode = CircleIndicatorMode.values()[mode];
			
			typedArray.recycle();
		}
		
		
	}

	public void bindViewPage(ViewPager viewPager)
	{
		this.viewPager = viewPager;
		createTabItems();
		createMovingItem();
		//setUpListener();
	}



	private void createTabItems()
	{
		for(int i = 0 ;i < viewPager.getAdapter().getCount();i++)
		{
			OvalShape circle = new OvalShape();
			ShapeDrawable drawable = new ShapeDrawable(circle);
			ShapeHolder shapeHolder = new ShapeHolder(drawable);
			Paint paint = drawable.getPaint();
			paint.setColor(myIndicatorBackground);
			paint.setAntiAlias(true);
			shapeHolder.setPaint(paint);
			tabItems.add(shapeHolder);
		}
		
	}
	
	private void createMovingItem()
	{
		OvalShape shape = new OvalShape();
		ShapeDrawable drawable = new ShapeDrawable(shape);
		movingItem = new ShapeHolder(drawable);
		Paint movingPaint = drawable.getPaint();
		movingPaint.setColor(myIndicatorSelectdBackground);
		movingPaint.setAntiAlias(true);
		
		if(myIndicatorMode == CircleIndicatorMode.INSIDE)
		{
			movingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));// 取下层非交集部分与上层交集部分
		}
		else if(myIndicatorMode == CircleIndicatorMode.OUTSIDE)
		{
			movingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));// 正常绘制显示，上下层绘制叠盖。
		}
		else if(myIndicatorMode == CircleIndicatorMode.SOLO)
		{
			movingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		}

		movingItem.setPaint(movingPaint);
	}
	
	private void setUpListener()
	{
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				if(myIndicatorMode == CircleIndicatorMode.SOLO)
				{
					trgger(position,0);
				}
		
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				//positionOffset 0.0-1.0
				if(myIndicatorMode != CircleIndicatorMode.SOLO)
				{
					trgger(position,positionOffset);
				}
				
			}
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				
			}
		});
		
	}
	
	public void trgger(int position, float positionOffset)
	{
		if(myIndicatorMode == CircleIndicatorMode.SOLO)
		{
			positionOffset = 0;
		}
		CircleIndicator.this.mCurItemPosition = position;
		CircleIndicator.this.mCurItemPositionOffset = positionOffset;
		//LogUtil.d("huang", "onPageScrolled()" + position + ":"+ positionOffset);
		requestLayout();
		invalidate();
		
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		
	        layoutTabItems(getWidth(), getHeight());
	        layoutMovingItem(mCurItemPosition,mCurItemPositionOffset);
	
		super.onLayout(changed, left, top, right, bottom);
	}

	
	private void layoutTabItems(int containerWidth, int containerHeight)
	{
		if(tabItems == null)
		{
			throw new IllegalStateException("please create tabItem first");
		}
		else
		{
			final float yCoordinate = containerHeight*0.5f;
			final float startPosition = startDrawPosition(containerWidth);
			for(int i = 0 ;i < tabItems.size();i++)
			{
				ShapeHolder item = tabItems.get(i);
				item.resizeShape(2 * myIndicatorRadius, 2 * myIndicatorRadius);
				item.setY(yCoordinate - myIndicatorRadius);
				float x = startPosition + (myIndicatorMargin + myIndicatorRadius * 2) * i;
		        item.setX(x);
		        //LogUtil.d("huang", "normal = "+item.getX()+" y ="+item.getY());
			}
		}
		
	}

	private void layoutMovingItem(final int position, final float positionOffset)
	{
		if (movingItem == null)
		{
			throw new IllegalStateException("forget to create movingItem?");
		}
		ShapeHolder item = tabItems.get(position);
		movingItem.resizeShape(item.getWidth(), item.getHeight());
		float x = item.getX() + (myIndicatorMargin + myIndicatorRadius * 2)
				* positionOffset;
	   
		movingItem.setX(x);
		movingItem.setY(item.getY());
		 // LogUtil.d("huang", "movingItem = "+item.getX()+" y ="+item.getY());

	}
	
	private float startDrawPosition(int containerWidth)
	{
		float tabItemsLength = (myIndicatorRadius + myIndicatorMargin) * 2 *tabItems.size() ;
		
		if(myIndicatorGravity == CircleIndicatorGravity.CENTER)
		{
			return (containerWidth- tabItemsLength ) /2;
		}
		else if(myIndicatorGravity == CircleIndicatorGravity.LEFT)
		{
			return 0;
		}
		else//right
		{
			return containerWidth - tabItemsLength ;
		}
		
	}
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	    int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		for(ShapeHolder item : tabItems)
		{
	        drawItem(canvas,item);
		}
		if(movingItem != null)
		{
			drawItem(canvas,movingItem);
		}
		canvas.restoreToCount(sc);
	}
	
	private void drawItem(Canvas canvas,ShapeHolder shapeHolder )
	{
		canvas.save();
        canvas.translate(shapeHolder.getX(),shapeHolder.getY());
        shapeHolder.getShape().draw(canvas);
        canvas.restore();
	}
	
}
