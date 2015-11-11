package com.huang.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.huang.Activity.MainActivity;
import com.huang.nodrinkmore.R;
import com.huang.service.WidgetService;
import com.huang.util.DatabaseHelper;
import com.huang.util.LogUtil;

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
		LogUtil.d("huang", "WidgetProvider onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	
	/**
	 * 第一个widget添加到屏幕上执行
	 */
	@Override
	public void onEnabled(Context context)
	{
		LogUtil.d("huang", "WidgetProvider onEnabled");
		super.onEnabled(context);
		context.startService(new Intent(context,WidgetService.class));
	}
	
	
	/**
	 * 最后一个widget被屏幕移除时执行
	 */
	@Override
	public void onDisabled(Context context)
	{
		LogUtil.d("huang", "WidgetProvider onDisabled");
		context.stopService(new Intent(context,WidgetService.class));
		super.onDisabled(context);
	}

	
	/**
	 * 操作哪个都会调用这个方法
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.d("huang", "WidgetProvider onReceive");
		super.onReceive(context, intent);
		
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		String[] keepDaysInfo = databaseHelper.getKeepTime();
		String widgetShowStr = "已保持\n"+keepDaysInfo[0]+"天";
		LogUtil.d("huang", "onReceive updateViews"+widgetShowStr);
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.keeptime_widget, widgetShowStr);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, WidgetProvider.class);
		manager.updateAppWidget(cn, rv);//调用widget.onUpdate()
	}
	
	/**
	 * 刷新的widget时候执行 
	 * 在界面上绑定事件 点击后触发app主界面
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		final int N = appWidgetIds.length;
		LogUtil.d("huang", "WidgetProvider onUpdate N="+N);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int i = 0; i < N; i++) 
		{
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
			views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
}
