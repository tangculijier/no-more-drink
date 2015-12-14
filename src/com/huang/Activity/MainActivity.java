package com.huang.Activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
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
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.huang.model.Habit;
import com.huang.nodrinkmore.R;
import com.huang.service.WidgetService;
import com.huang.service.WidgetService.UpdateViewBinder;
import com.huang.util.AnimationUtil;
import com.huang.util.AppConst;
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
	 * 健康值图片--向日葵
	 */
	ImageView balanceImage;
	/**
	 * 健康值
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
	ImageView eye;
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
			//LogUtil.d("huang", "onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			//LogUtil.d("huang", "onServiceConnected");
			updateViewBinder = (UpdateViewBinder)service;
	
			
		}
	};
	
	
	private IntentFilter sendFilter;
	private SendMessageStatusReceiver sendStatusReceiver;
	 
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
		eye = (ImageView)findViewById(R.id.eye);
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
		eye.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this,BindWatcherActivity.class));
				
			}
		});
		
		databaseHelper = DatabaseHelper.getInstance(this);
	
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
		
		setting = getSharedPreferences(AppConst.SHARE_PS_Name, MODE_PRIVATE);
		
		registerSendMessageBroadCast();

		
		if(keepDay !=0 && (keepDay / AppConst.BALANCE_REWARD_VALUE > 0))
		{
			int reward = keepDay / AppConst.BALANCE_REWARD_VALUE;
			int roundDay = setting.getInt(AppConst.ROUND_DAY, 0);
			if(reward != roundDay)	//这次奖励还没有加过
			{
				Toast.makeText(this, "已保持"+keepDay+"天,健康值+"+reward+",加油!", Toast.LENGTH_SHORT).show();
				calculateBalance(reward);
				setting.edit().putInt(AppConst.ROUND_DAY, reward).commit();
			}
			
		}
		databaseHelper.checkAndinsertAnalysis();
		startFaceAnimation();
		
		
		drinkBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				
				SnackBar snackbar = new SnackBar(MainActivity.this,"喝饮料了吧 ?","敢作敢当 !", new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						drink();
						calculateBalance(-1);
						
					}
				});
				snackbar.show();
				
			}
		});
	
	
	}
	public void startFaceAnimation()
	{
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
				upAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
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
	@Override
	protected void onResume()
	{
		changeWatcherImage();
		super.onResume();
	}
	@Override
	protected void onDestroy()
	{
		unbindService(serviceConn);  
		unregisterReceiver(sendStatusReceiver);
		LogUtil.d("huang", "onDestroy unbindService");
		super.onDestroy();
	}
	
	/**
	 * 喝饮料
	 */
	private void drink()
	{
		LogUtil.d("huang", "drink()");
		faceToSad(true);
		databaseHelper.insertDrinkTime();
		setting.edit().putInt(AppConst.ROUND_DAY, 0).commit();//rounday清空
		List<Habit>  drinkDateRecords = databaseHelper.getCurrentMonthDrinkRecord(calendar.getCurrentDate());
		calendar.setDrinkRecords(drinkDateRecords);
		calendar.invalidate();
		
		
		sentMessageToMyWatcher();
		
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
	 * 计算健康值
	 * @param addNumber 增加的健康值 可以是正数也可以是负数
	 */
	private void calculateBalance(int addNumber)
	{
			int balance = setting.getInt(AppConst.BALANCE, AppConst.BALANCE_INIT_VALUE);
			balance += addNumber ;
			final int balanceFinal = balance;
			AnimationSet animation = addNumber > 0 ? 
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
					setting.edit().putInt(AppConst.BALANCE, balanceFinal).commit();
					balanceText.setText(balanceFinal+"");
					balanceTextSetColor(balanceFinal);
				}
			});
		
			balanceImage.startAnimation(animation);
		
			
	}
	/**
	 * 设置健康值的颜色
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
			Toast.makeText(MainActivity.this, "健康值已经归0", Toast.LENGTH_SHORT).show();
		}
		else if(balance < 0)
		{
			balanceText.setTextColor(Color.RED);
			
		}
	}
	
	/**
	 * 修改监督人的图标
	 */
	private void changeWatcherImage()
	{
		boolean isOpenWatcher = setting.getBoolean(AppConst.IS_OPEN_WATCHER, false);
		if(isOpenWatcher == true)
		{
			if(!TextUtils.isEmpty(setting.getString(AppConst.WATCHER_NUMBER, "")))
			{
				eye.setImageResource(R.drawable.bind_ok);
			}
			
		}
		else
		{
			eye.setImageResource(R.drawable.eye);
		}
	}

	/**
	 * 给监督人发短信
	 */
	private void sentMessageToMyWatcher()
	{
		boolean isOpenWatcher = setting.getBoolean(AppConst.IS_OPEN_WATCHER, false);
		if(isOpenWatcher == true)
		{
			String telephoneNum = setting.getString(AppConst.WATCHER_NUMBER, "");
			if(!TextUtils.isEmpty(telephoneNum))
			{
				String message = setting.getString(AppConst.WATCHER_MESSAGE, "");
				Intent sentIntent = new Intent("SENT_SMS_ACTION");
				PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, sentIntent, 0);
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(telephoneNum, null,message, pi,  null); 
			}

		}
	
	}
	
	private void registerSendMessageBroadCast()
	{
		sendFilter = new IntentFilter();
		sendFilter.addAction("SENT_SMS_ACTION");
		sendStatusReceiver = new SendMessageStatusReceiver();
		registerReceiver(sendStatusReceiver, sendFilter);	
	}
	
	class SendMessageStatusReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if (getResultCode() == RESULT_OK) 
			{
				//  短信发送成功
				Toast.makeText(context, "已通知监督人",Toast.LENGTH_LONG).show();
			} 
			else
			{
				//  短信发送失败
				Toast.makeText(context, "通知监督人失败",Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY)
		{
			float x = e2.getX() - e1.getX();
			if (x > 10)//right gesture
			{
				calendar.getLastMonth();
			} else if (x < -10)//left gesture
			{
				calendar.getNextMonth();
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
}
