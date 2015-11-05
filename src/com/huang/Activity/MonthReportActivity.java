package com.huang.Activity;

import java.util.Calendar;

import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.views.CalendarView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MonthReportActivity extends Activity 
{
	DatabaseHelper databaseHelper;
	Calendar calendar = Calendar.getInstance();
	
	TextView dateDurationTextView; // 时间区间
	String dateDuration;
	
	TextView conscienceDaysTextView; // 自觉天数
	int conscienceDays = 0;
	
	TextView monthSumOfDrinkTimesTextView; // 月总量
	int monthSumOfDrinkTimes = 0;
	
	TextView longestKeepingDayOfMonthTextView; // 最长保持纪录
	int longestKeepingDayOfMonth = 0;
	
	int mostDrinkTimesOfMonth = 0; //暂时不用此变量
	
	
	
	TextView amDrinkTimesOfMonthTextView;//4:00--12:00
	TextView pmDrinkTimesOfMonthTextView;//12:00--20:00
	TextView eveningDrinkTimesOfMonthTextView;//20:00--4:00
	int partTimeOfDrinktimesOfMonth[]=new int[3];
	
	
	//test soureTree
	//tst2
	

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		init();

	}

	public void init()
	{
		
		dateDuration = DateUtil.getDateDuration(calendar.getTime());
		dateDurationTextView= (TextView)findViewById(R.id.dateDuration);
		String text  = dateDurationTextView.getText().toString().replace("%s", dateDuration);
		dateDurationTextView.setText(text);
		
		databaseHelper = new DatabaseHelper(this);
		
		conscienceDays = databaseHelper.getConscienceDays(calendar.getTime());
		conscienceDaysTextView= (TextView)findViewById(R.id.conscienceDays);
		text  = conscienceDaysTextView.getText().toString().replace("%s", conscienceDays + "");
		conscienceDaysTextView.setText(text);
		
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(calendar.getTime());
		monthSumOfDrinkTimesTextView= (TextView)findViewById(R.id.monthSumOfDrinkTimes);
		text  = monthSumOfDrinkTimesTextView.getText().toString().replace("%s", monthSumOfDrinkTimes + "");
		monthSumOfDrinkTimesTextView.setText(text);
		
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(calendar.getTime());
		longestKeepingDayOfMonthTextView= (TextView)findViewById(R.id.longestKeepingDayOfMonth);
		text  = longestKeepingDayOfMonthTextView.getText().toString().replace("%s", longestKeepingDayOfMonth + "");
		longestKeepingDayOfMonthTextView.setText(text);
	} 

}
