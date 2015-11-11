package com.huang.Activity;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.model.Habit;
import com.huang.nodrinkmore.R;
import com.huang.service.ForeService;
import com.huang.service.WidgetService;
import com.huang.service.WidgetService.UpdateViewBinder;
import com.huang.util.AnimationUtil;
import com.huang.util.Constant;
import com.huang.util.DatabaseHelper;
import com.huang.util.LogUtil;
import com.huang.views.CalendarView;

public class MainActivity extends ActionBarActivity
{
	/**
	 * 日历控件
	 */
	CalendarView calendar;

	/**
	 * 月报分析按钮
	 */
	ImageView analyticsImage;
	/**
	 * 自觉值图片--向日葵
	 */
	ImageView balanceImage;
	/**
	 * 自觉值
	 */
	TextView balanceText;
	
	/**
	 * 当前表情是否为sad
	 */
	boolean isSadFace ;
	/**
	 * 当前表情
	 */
	ImageView face;
	/**
	 * 当前保持时间
	 */
	TextView keeptimeText;
	
	/**
	 * 喝饮料button
	 */
	ImageView drinkBtn;
	
	/**
	 * 表情跳动动画
	 */
	TranslateAnimation upAnimation;
	TranslateAnimation dwonAnimation;
	

	private boolean isKeepingNotDrink = true;
	/**
	 * 左右滑动监听
	 */
	GestureDetector gestureDetector;
	DatabaseHelper databaseHelper;
	SharedPreferences  setting;

	/**
	 * 桌面快捷方式的service binder
	 */
	private WidgetService.UpdateViewBinder updateViewBinder;
	
	private ServiceConnection serviceConn = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			LogUtil.d("huang", "onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			LogUtil.d("huang", "onServiceConnected");
			updateViewBinder = (UpdateViewBinder)service;
	
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//to title
		setContentView(R.layout.activity_main);
		init();
		
	}
	public void init()
	{
		calendar = (CalendarView)findViewById(R.id.calendar);
		analyticsImage = (ImageView)findViewById(R.id.analytics);
		balanceImage = (ImageView)findViewById(R.id.balanceImage);
		balanceText = (TextView)findViewById(R.id.balance);
		face = (ImageView)findViewById(R.id.face);
		isSadFace = false;
		keeptimeText = (TextView)findViewById(R.id.keeptimeText);
		drinkBtn = (ImageView)findViewById(R.id.drinkBtn);
		int balance = getIntent().getIntExtra("balance", 3);
		balanceText.setText(balance+"");
		balanceTextSetColor(balance);
		balanceImage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)//balance explain dialog
			{
				LayoutInflater inflater = getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.balance_dialog, null);
				final AlertDialog.Builder  dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setView(dialogView);
				dialog.create().show();
				
			}
		});
		balanceText.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				balanceImage.callOnClick();//go to balanceimage onclick
			}
		});
		
		analyticsImage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this,MonthReportActivity.class);
				startActivity(intent);
			}
		});
		
		databaseHelper = new DatabaseHelper(this);
	
		String[] keepDaysInfo = databaseHelper.getKeepTime();
	//	LogUtil.i("huang", "keepDaysInfo"+keepDaysInfo[0]+" "+keepDaysInfo[1]);
		int keepDay = Integer.parseInt(keepDaysInfo[0]);
		boolean keepDayIsZero = TextUtils.equals(keepDaysInfo[0], "0");
		boolean isFirstUseNoRecord = TextUtils.equals(keepDaysInfo[1],"0");
		if( keepDayIsZero && !isFirstUseNoRecord)
		{
			faceToSad(false);
			keepTimeToZero();
		}
		else
		{
			isSadFace = false;
		}
		
		
	
		String text  = keeptimeText.getText().toString().replace("%s", keepDaysInfo[0]);
		keeptimeText.setText(text);
	

		gestureDetector = new GestureDetector(MainActivity.this,onGestureListener);  
		
		setting = getSharedPreferences(Constant.SHARE_PS_Name, MODE_PRIVATE);
		
		
		if(keepDay !=0 && (keepDay % Constant.BALANCE_REWARD_VALUE == 0))
		{
			int roundToday = keepDay / Constant.BALANCE_REWARD_VALUE;
			int roundDay = setting.getInt("roundDay", 0);
			if(roundToday != roundDay)	//这次奖励还没有加过
			{
				Toast.makeText(this, "已保持3天,自觉值+1,加油!", Toast.LENGTH_SHORT).show();
				calculateBalance(true);
				setting.edit().putInt("roundDay", roundToday).commit();
			}
			
		}
		
//		if(!TextUtils.equals(text, "0 天"))
//		{
//	
//		    keepDayNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			Notification.Builder notficationBuilder = new Notification.Builder(this);
//			notficationBuilder.setAutoCancel(false);//设置可以清除
//			notficationBuilder.setSmallIcon(R.drawable.face_simle);
//			notficationBuilder.setLargeIcon(BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.face_simle));
//			notficationBuilder.setContentTitle("不喝饮料已保持");
//			notficationBuilder.setContentText(text);
//			notficationBuilder.setPriority(Notification.FLAG_FOREGROUND_SERVICE);
//			notficationBuilder.setOngoing(true);
//		//	notficationBuilder.setWhen(1000l);//设置时间发生时间
//			keepDayNotification.notify(NotificationID, notficationBuilder.build());
//		}
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				while(true)
				{
				
					int sleepTime = (int)(Math.random()*5000)+5000;//random jump time
					try
					{
						Thread.sleep(sleepTime);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				upAnimation = new TranslateAnimation
						(0, 0, 0, -40);
				upAnimation.setDuration(500);
				upAnimation.setFillAfter(true);
				upAnimation.setZAdjustment(Animation.ZORDER_TOP);
				upAnimation.setInterpolator(new AccelerateDecelerateInterpolator());//new BounceInterpolator()
				upAnimation.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
					}
					
					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}
					
					@Override
					public void onAnimationEnd(Animation animation)
					{
						
							dwonAnimation = new TranslateAnimation(0, 0, -40, 0);
							dwonAnimation.setDuration(1000);
							dwonAnimation.setFillAfter(true);
							dwonAnimation.setInterpolator(new BounceInterpolator());
							myHandler.sendEmptyMessage(AnimationUtil.START_DOWNANIMATION);
					
					
					}
				});
				if(isKeepingNotDrink == true)
				{
					myHandler.sendEmptyMessage(AnimationUtil.START_UPANIMATION);
				}
			
				
				}
				
				
			}
		}).start();
		
		
		drinkBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				final AlertDialog.Builder  dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("喝饮料了吧 ?");
				dialog.setIcon(R.drawable.dialog_icon);
				dialog.setPositiveButton("敢作敢当 !", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						drink();
						calculateBalance(false);
					}
				});
//				dialog.setNegativeButton("放我一马 !", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) 
//					{
//						calculateBalance(false);
//					}
//				});
				dialog.create();
				dialog.show();
				
			}
		});
	
	
	}
	
	Handler myHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			
			case AnimationUtil.START_UPANIMATION:
				face.startAnimation(upAnimation);
				break;
			case AnimationUtil.START_DOWNANIMATION:	
				face.startAnimation(dwonAnimation);
				break;
				default:
					break;
			
			}
			super.handleMessage(msg);
		}
		
	};

	@Override
	protected void onStart()
	{
		List<Habit>  drinkDateRecords = databaseHelper.getCurrentMonthDrinkRecord(calendar.getCurrentDate());
		calendar.setDrinkRecords(drinkDateRecords);
		calendar.invalidate();
		
		Intent bindIntent = new Intent(this,WidgetService.class);
		bindService(bindIntent, serviceConn, BIND_AUTO_CREATE);  
		super.onStart();
	}
	
	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			
			if (x > 10)//right gesture
			{
				calendar.getLastMonth();
				
				LogUtil.d("huang", "右滑动"+x);
			} else if (x < -10)//left gesture
			{
			
				calendar.getNextMonth();
				LogUtil.d("huang", "左"+x);
			}
			List<Habit>  drinkDateRecords = databaseHelper.getCurrentMonthDrinkRecord(calendar.getCurrentDate());
			calendar.setDrinkRecords(drinkDateRecords);
			calendar.invalidate();
			return true;
		}
	};
	
	public boolean onTouchEvent(MotionEvent event) 
	{  
        return gestureDetector.onTouchEvent(event);  
    }  
	

	/**
	 * 喝饮料
	 */
	private void drink()
	{
		LogUtil.d("huang", "drink()");
		
		faceToSad(true);
		
		databaseHelper.insertDrinkTime();
		List<Habit>  drinkDateRecords = databaseHelper.getCurrentMonthDrinkRecord(calendar.getCurrentDate());
		calendar.setDrinkRecords(drinkDateRecords);
		calendar.invalidate();
		
		updateViewBinder.getWidgetService().updateViews();//更新桌面
	}
	/**
	 * 表情变化
	 */
	private void faceToSad(boolean isClickDrinkButton)
	{
		face.clearAnimation();
		
		isKeepingNotDrink = false;// stop animation
		if( isSadFace == false && isClickDrinkButton == true)
		{
			AnimationSet downOutAnimaiton = AnimationUtil.getFaceRollOutAnimation(0f, - face.getLeft() - face.getWidth(),3*1000);
			myHandler.postDelayed(new Runnable()
			{
				
				@Override
				public void run()
				{
					LogUtil.d("huang","end");
					face.setImageResource(R.drawable.face_sad);
					float fromX =getResources().getDisplayMetrics().widthPixels - 2 * face.getWidth() ;
					float toX = 0f;
					AnimationSet appearFromRightAnimation = AnimationUtil.getFaceRollInAndOutAnimation(fromX,toX,5*1000);
					appearFromRightAnimation.setAnimationListener(new AnimationListener()
					{
						@Override
						public void onAnimationRepeat(Animation animation){}
						@Override
						public void onAnimationEnd(Animation animation)
						{
							keepTimeToZero();
						}
						@Override
						public void onAnimationStart(Animation animation){}
					});
					face.startAnimation(appearFromRightAnimation);
					
				}
			}, 3000);
			
			myHandler.postDelayed(new Runnable()
			{
				
				@Override
				public void run()
				{
					TranslateAnimation jumpAnimaiton = new TranslateAnimation(0, 0, 0, -150);
					jumpAnimaiton.setDuration(800);
					jumpAnimaiton.setInterpolator(new AccelerateInterpolator());//new BounceInterpolator()
					jumpAnimaiton.setAnimationListener(new AnimationListener()
					{
						@Override
						public void onAnimationStart(Animation animation)
						{
						}
						
						@Override
						public void onAnimationRepeat(Animation animation)
						{
						}
						
						@Override
						public void onAnimationEnd(Animation animation)
						{
							TranslateAnimation textDownAnimation = new TranslateAnimation(0, 0, -150, 0);
							textDownAnimation.setDuration(2600);
							textDownAnimation.setFillAfter(true);
							textDownAnimation.setInterpolator(new BounceInterpolator());
							keeptimeText.startAnimation(textDownAnimation);
						
						
						}
					});
					keeptimeText.startAnimation(jumpAnimaiton);
					
				}
			}, 3000);
			face.startAnimation(downOutAnimaiton);
		}
		else
		{
			face.setImageResource(R.drawable.face_sad);
			keepTimeToZero();
		}
		isSadFace = true;
		calendar.makeBlackToday();
		
		
	
	}
	/**
	 * 喝饮料时间归0
	 */
	private void keepTimeToZero()
	{
		keeptimeText.setText("0 天");
		//keeptimeText.setTextColor(Color.BLACK);
	}
	
	private void keep()
	{
		face.setImageResource(R.drawable.face_simle);
		isKeepingNotDrink = true;// stop animation
		keeptimeText.setText("0 天");
		keeptimeText.setTextColor(getResources().getColor(R.color.green_light));
	}
	
	/**
	 * 计算自觉值
	 * @param isAdd 是否是增加自觉值
	 */
	private void calculateBalance(boolean isAdd)
	{
			int balance = setting.getInt("balance", Constant.BALANCE_INIT_VALUE);
			balance = isAdd == true ? balance + 1 : balance - 1 ;
			final int balanceFinal = balance;
			AnimationSet animation = isAdd == true ? 
					AnimationUtil.getAddBalanceAnimation() : AnimationUtil.getSubBalanceAnimation(0.6f);
			animation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}
				
				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}
				
				@Override
				public void onAnimationEnd(Animation animation)
				{	//hanlder the balance after the animation end
					setting.edit().putInt("balance", balanceFinal).commit();
					balanceText.setText(balanceFinal+"");
					balanceTextSetColor(balanceFinal);
				}
			});
		
			balanceImage.startAnimation(animation);
		
			
	}
	/**
	 * 设置自觉值的颜色
	 */
	private void balanceTextSetColor(int balance)
	{
		if(balance > 0)
		{
			balanceText.setTextColor(getResources().getColor(R.color.green_light));
		}
		else if(balance == 0)
		{
			balanceText.setTextColor(Color.BLACK);
			Toast.makeText(MainActivity.this, "自觉值已经归0", Toast.LENGTH_SHORT).show();
		}
		else if(balance < 0)
		{
			balanceText.setTextColor(Color.RED);
			
		}
	}
	@Override
	protected void onDestroy()
	{
		unbindService(serviceConn);  
		LogUtil.d("huang", "onDestroy unbindService");
		super.onDestroy();
	}

}
