package com.huang.views;
/**
 * 暂时不用
 */
import com.huang.util.LogUtil;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements Callback, Runnable
{

	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	
	/**
	 * 用于绘制的线程
	 * 
	 */
	private Thread thread;
	
	/**
	 * 
	 * 线程的控制开关
	 */
	private boolean isRunning;
	
	
	public MySurfaceView(Context context)
	{
		super(context,null);
		// TODO Auto-generated constructor stub
	}


	public MySurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		setFocusable(true);//可获得焦点
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		
		
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		isRunning = false;
		
	}


	@Override
	public void run()
	{
		while(isRunning)
		{
			draw();
		}
		
	}


	private void draw()
	{
		try
		{
			mCanvas = mHolder.lockCanvas();
			if(mCanvas != null)
			{
				Paint p = new Paint();
				p.setColor(color.black);
				mCanvas.drawCircle(50, 50, 50,p);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(mCanvas != null)
			{
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		
		
	}
	
	

}
