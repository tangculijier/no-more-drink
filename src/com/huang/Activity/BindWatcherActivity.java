package com.huang.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.views.Switch.OnCheckListener;
import com.gc.materialdesign.widgets.Dialog;
import com.huang.nodrinkmore.R;
import com.huang.util.AppConst;
import com.huang.util.LogUtil;

public class BindWatcherActivity extends ActionBarBaseActivity
{
	EditText bindNumberText;
	EditText bindMessageText;
	ImageView addWatcherImgButton;
	ButtonFlat confirmBindButton;
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
		addWatcherImgButton = (ImageView)findViewById(R.id.add_watcher);
		confirmBindButton = (ButtonFlat)findViewById(R.id.confirm_bind_button);
		confirmBindButton.getTextView().setTextSize(22);
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
	
		openSwitch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LogUtil.d("huang", "onclcik");
				
			}
		});
		openSwitch.setOncheckListener(new OnCheckListener()
		{
			
			@Override
			public void onCheck(Switch view, boolean isChecked)
			{
				if(isChecked == true)
				{
					showBindInfo();
					setBindInfo();
				
				}
				else
				{
					hideBindInfo();
				}
				setting.edit().putBoolean(AppConst.IS_OPEN_WATCHER, isChecked).commit();

				
			}
		});
		addWatcherImgButton.setOnClickListener(new OnClickListener()
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
				
			
			
		
				
				
				final Dialog dialog = new Dialog(BindWatcherActivity.this,"", "");
				dialog.show();
				if(!TextUtils.isEmpty(setting.getString(AppConst.WATCHER_NUMBER, "")))
				{
					//还要检查电话号码
					dialog.setTitle("绑定成功");
					dialog.setOnAcceptButtonClickListener(new OnClickListener()
					{
						
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							finish();
							
						}
					});
				}
				else
				{
					dialog.setTitle("绑定失败,请重试");
				}
			
				ButtonFlat acceptButton = dialog.getButtonAccept();
				acceptButton.setText("确定");
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
