/**  
 *  
 * @author lizheHuang 
 * @Date 2015年10月27日   下午9:01:17
 * 暂时不用取消
 */ 

package com.huang.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.huang.Activity.MainActivity;
import com.huang.nodrinkmore.R;
import com.huang.util.LogUtil;

public class ForeService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		LogUtil.d("huang", "ForeService onBind");
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		LogUtil.d("huang", "ForeService onCreate");
		
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		LogUtil.d("huang", "ForeService onStartCommand");
		String keepday = intent.getStringExtra("keepday");
		if(!TextUtils.equals(keepday, "0 天"))
		{
			//Notification.Builder builder  = new Builder(context);
			Notification notification = new Notification(R.drawable.face_simle, "1", System.currentTimeMillis());
			Intent notIntent = new Intent(this,MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notIntent, 0);
			//notification.setLatestEventInfo(this, "不喝饮料已保持", keepday, pendingIntent);
			startForeground(1, notification);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy()
	{
		LogUtil.d("huang", "ForeService onDestroy");
		super.onDestroy();
	}

}
