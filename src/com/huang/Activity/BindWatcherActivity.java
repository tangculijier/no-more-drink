package com.huang.Activity;

import com.huang.nodrinkmore.R;
import com.huang.nodrinkmore.R.id;
import com.huang.nodrinkmore.R.layout;
import com.huang.util.AppConst;
import com.huang.util.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class BindWatcherActivity extends ActionBarBaseActivity
{
	EditText bindNumberText;
	EditText bindMessageText;
	ImageButton addWatcherButton;
	Button confirmBindButton;
	Switch openSwitch;
	LinearLayout bindInfoLayout;
	
	private int myRequestCode = 1;
	
	SharedPreferences setting  ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_watcher);
	
		bindNumberText = (EditText)findViewById(R.id.bind_number);
		addWatcherButton = (ImageButton)findViewById(R.id.add_watcher);
		confirmBindButton = (Button)findViewById(R.id.confirm_bind_button);
		bindMessageText = (EditText)findViewById(R.id.bind_message_text);
		openSwitch = (Switch)findViewById(R.id.open_switch);
		bindInfoLayout = (LinearLayout)findViewById(R.id.bind_info_layout);
		
		setting  = getSharedPreferences(AppConst.SHARE_PS_Name,MODE_PRIVATE);

		boolean isOpenWatcher = setting.getBoolean(AppConst.IS_OPEN_WATCHER, false);
		openSwitch.setChecked(isOpenWatcher);
		if(isOpenWatcher == true)
		{
			showBindInfo();
			setBindInfo();
		
		}
	
		
		openSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked == true)
				{
					showBindInfo();
					setting.edit().putBoolean(AppConst.IS_OPEN_WATCHER, isChecked).commit();
					setBindInfo();
				}
				else
				{
					hideBindInfo();
					setting.edit().putBoolean(AppConst.IS_OPEN_WATCHER, isChecked).commit();
				}
				
			}
		});
		addWatcherButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent toReadContactIntent = new Intent(BindWatcherActivity.this,ContactsActivity.class);
				startActivityForResult(toReadContactIntent, myRequestCode, null);
				
			}
		});
		confirmBindButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				//sp记录
				String bindNumber = bindNumberText.getText().toString();
				String bindMessage = bindMessageText.getText().toString();
				setting.edit().putString(AppConst.WATCHER_NUMBER, bindNumber).commit();
				setting.edit().putString(AppConst.WATCHER_MESSAGE, bindMessage).commit();
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(BindWatcherActivity.this);
				if(!TextUtils.isEmpty(setting.getString(AppConst.WATCHER_NUMBER, "")))
				{
					//还要检查电话号码
					dialog.setTitle("绑定成功");
					dialog.setPositiveButton("确定",new DialogInterface.OnClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							
							finish();
						}
					});
				}
				else
				{
					dialog.setTitle("绑定失败,请重试");
					dialog.setPositiveButton("确定",null);
				}
				
				dialog.create();
				dialog.show();
			}
		});
	}

	public void setBindInfo()
	{
		String telephoneNum = setting.getString(AppConst.WATCHER_NUMBER, "");
		if(!TextUtils.isEmpty(telephoneNum))
		{
			bindNumberText.setText(telephoneNum);
		}
		String message = setting.getString(AppConst.WATCHER_MESSAGE, "");
		if(!TextUtils.isEmpty(message))
		{
			bindMessageText.setText(message);
		}
	}
	
	private void showBindInfo()
	{
		if(bindInfoLayout.getVisibility() == View.INVISIBLE)
		{
			bindInfoLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideBindInfo()
	{
		if(bindInfoLayout.getVisibility() == View.VISIBLE)
		{
			bindInfoLayout.setVisibility(View.INVISIBLE);
		}
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK)
		{
			  if(requestCode == myRequestCode)
			  {
				  bindNumberText.setText(data.getStringExtra("eyeNumber"));
			  }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
