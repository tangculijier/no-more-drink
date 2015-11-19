package com.huang.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.widget.WidgetProvider;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

/**  
 *  
 * @author lizheHuang 
 * @Date 2015年11月07日   下午16:36:17
 * 桌面快捷方式widget的service 每24小时更新一次数据
 */ 
public class WidgetService extends Service
{

	private Timer timer;
	
	UpdateViewBinder updateBinder = new UpdateViewBinder();
	
	public class UpdateViewBinder extends Binder
	{
		public WidgetService getWidgetService()
		{
			return WidgetService.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent)
	{
		Log.d("huang", "onBind ");
		return updateBinder;
	}
	
	
	@Override
	public void onCreate()
	{
		Log.d("huang", "TimerService onCreate");
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			
			@Override
			public void run()
			{
				//更新widget
				updateViews();
			}
		}, 0,  24 * 60 * 1000);//每天更新一次
		super.onCreate();
	}
	
	public void updateViews()
	{
	
		DatabaseHelper databaseHelper =  DatabaseHelper.getInstance(this);
		String[] keepDaysInfo = databaseHelper.getKeepTime();
		String widgetShowStr = "已保持\n"+keepDaysInfo[0]+"天";
		Log.d("huang", "TimerService updateViews"+widgetShowStr);
		RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.keeptime_widget, widgetShowStr);
		AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
		ComponentName cn = new ComponentName(getApplicationContext(), WidgetProvider.class);
		manager.updateAppWidget(cn, rv);//调用widget.onUpdate()
	}
	
	
	@Override
	public void onDestroy()
	{
		Log.d("huang", "TimerService onDestroy");
		timer.cancel();
		timer = null;
		super.onDestroy();
	}

}
