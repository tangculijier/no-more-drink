package com.huang.Activity;

import java.util.Calendar;

import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.views.CalendarView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MonthReportActivity extends Activity 
{
	DatabaseHelper databaseHelper;
	Calendar calendar = Calendar.getInstance();
	
	TextView monthSumOfDrinkTimesTextView; // 月总量
	int monthSumOfDrinkTimes = 0;
	
	TextView longestKeepingDayOfMonthTextView; // 最长保持纪录
	int longestKeepingDayOfMonth = 0;
	
	int mostDrinkTimesOfMonth = 0;
	
	TextView amDrinkTimesOfMonthTextView;//4:00--12:00
	TextView pmDrinkTimesOfMonthTextView;//12:00--20:00
	TextView eveningDrinkTimesOfMonthTextView;//20:00--4:00
	int partTimeOfDrinktimesOfMonth[]=new int[3];
	
	
	
	
	

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		init();

	}

	public void init()
	{
		databaseHelper = new DatabaseHelper(this);
		
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(calendar.getTime());
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(calendar.getTime());
	} 

}
