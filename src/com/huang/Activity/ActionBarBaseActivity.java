/**  
 * 基类actionbarActivity 写进去一些基本的功能
 * @author lizheHuang 
 * @Date   time :2015年11月27日  下午4:05:21
 * @version 1.0
 */ 

package com.huang.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class ActionBarBaseActivity extends ActionBarActivity
{

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);//show the back arrow
		actionBar.setDisplayShowHomeEnabled(false);//without Logo
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
		{
			case android.R.id.home:
            		finish();
			default:
				return super.onOptionsItemSelected(item);
        }
	}
}
