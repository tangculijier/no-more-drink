package com.huang.Activity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.nodrinkmore.R;
import com.huang.nodrinkmore.R.id;
import com.huang.nodrinkmore.R.layout;
import com.huang.util.AppConst;
import com.huang.views.indexListview.IndexListView;
import com.huang.views.indexListview.IndexListViewAdapter;

public class ContactsActivity extends ActionBarBaseActivity
{

	IndexListView contactsView;
	ArrayAdapter<String> adapter;
	List<String> contactsList = new ArrayList<String>();
	HashMap<String,String> phonebook = new HashMap<String, String>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		contactsView = (IndexListView) findViewById(R.id.contactListView);
		readContacts();
		adapter = new IndexListViewAdapter(this, android.R.layout.simple_list_item_1, contactsList);
		contactsView.setAdapter(adapter);
		contactsView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				String name = ((TextView)view).getText().toString(); 
				Intent intent=new Intent(ContactsActivity.this,BindWatcherActivity.class);
				intent.putExtra("eyeNumber",phonebook.get(name));
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
					phonebook.put(displayName.trim(), number.trim());
					contactsList.add(displayName.trim());
				}
			
			}
			Collections.sort(contactsList, Collator.getInstance(Locale.CHINA));//按中文排序 未处理多音字
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

}
