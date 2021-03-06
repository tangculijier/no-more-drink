package com.huang.Activity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.model.Phone;
import com.huang.nodrinkmore.R;
import com.huang.nodrinkmore.R.id;
import com.huang.nodrinkmore.R.layout;
import com.huang.util.AscNameComparator;
import com.huang.util.LogUtil;
import com.huang.views.indexListview.IndexListView;
import com.huang.views.indexListview.IndexListViewAdapter;

public class ContactsActivity extends ActionBarBaseActivity
{

	IndexListView contactsView;
	ArrayAdapter<Phone> adapter;
	List<Phone> contactsList = new ArrayList<Phone>();
	//List<Phone> contactsList = new ArrayList<Phone>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		contactsView = (IndexListView) findViewById(R.id.contactListView);
		readContacts();
		adapter = new IndexListViewAdapter(this, R.layout.listview_phone, contactsList);
		contactsView.setAdapter(adapter);
		contactsView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent intent=new Intent(ContactsActivity.this,BindWatcherActivity.class);
				intent.putExtra("eyeNumber",contactsList.get(position).getUserPhone());
				setResult(RESULT_OK,intent);
				finish();
				
			}
		});
	}
	
	private void readContacts()
	{
		Cursor cursor = null;
		try
		{
			cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			while(cursor.moveToNext())
			{
				String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				//if(number.length() == AppConst.CELLPHONE_LENGTH)//手机号
				{
					contactsList.add(new Phone(displayName.trim(),number));
				}
			
			}
			Collections.sort(contactsList, new AscNameComparator());//按中文排序 未处理多音字
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
				
			}
		}
		
	}
	
	/**
	 * 
	 * @param phoneNumberStr 未经处理的电话 eg:150-343-43312
	 * @return 纯数字 eg:15034343312
	 */
	public String getPhoneNum(String phoneNumberStr)
	{
		StringBuilder res = new StringBuilder(256);
		phoneNumberStr = phoneNumberStr.trim();
		if (phoneNumberStr != null && phoneNumberStr.length() != 0)
		{
			for (int i = 0; i < phoneNumberStr.length(); i++)
			{
				if (phoneNumberStr.charAt(i) >= '0' && phoneNumberStr.charAt(i) <= '9')
				{
					res.append(phoneNumberStr.charAt(i));
				}
			}
		}
		return res.toString();
	}

}
