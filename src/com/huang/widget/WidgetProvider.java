package com.huang.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.huang.nodrinkmore.R;
import com.huang.service.WidgetService;
import com.huang.util.DatabaseHelper;

/**  
 *  
 * @author lizheHuang 
 * @Date 2015年11月07日   下午16:36:17
 * 桌面快捷方式的Provider
 */ 
public class WidgetProvider extends AppWidgetProvider
{

	/**
	 * widget被移除时执行
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		Log.d("huang", "WidgetProvider onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	
	/**
	 * 第一个widget添加到屏幕上执行
	 */
	@Override
	public void onEnabled(Context context)
	{
		Log.d("huang", "WidgetProvider onEnabled");
		super.onEnabled(context);
		context.startService(new Intent(context,WidgetService.class));
	}
	
	
	/**
	 * 最后一个widget被屏幕移除时执行
	 */
	@Override
	public void onDisabled(Context context)
	{
		Log.d("huang", "WidgetProvider onDisabled");
		context.stopService(new Intent(context,WidgetService.class));
		super.onDisabled(context);
	}

	
	/**
	 * 操作哪个都会调用这个方法
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d("huang", "WidgetProvider onReceive");
		super.onReceive(context, intent);
		
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		String[] keepDaysInfo = databaseHelper.getKeepTime();
		String widgetShowStr = "已保持\n"+keepDaysInfo[0]+"天";
		Log.d("huang", "onReceive updateViews"+widgetShowStr);
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.keeptime_widget, widgetShowStr);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, WidgetProvider.class);
		manager.updateAppWidget(cn, rv);//调用widget.onUpdate()
	}
	
	/**
	 * 刷新的widget时候执行 remove和Appwidget
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		Log.d("huang", "WidgetProvider onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
}
