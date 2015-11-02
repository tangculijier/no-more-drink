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
	
	TextView mostDrinkTimesOfMonthTextView; // 月最多瓶数
	int mostDrinkTimesOfMonth = 0;
	
	TextView amDrinkTimesOfMonthTextView;//00:00--12:00
	TextView pmDrinkTimesOfMonthTextView;//12:00--18:00
	TextView eveningDrinkTimesOfMonthTextView;//18:00--24:00
	int partTimeOfDrinktimesOfMonth[]=new int[3];
	
	
	
	
	

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.mon_report);
		init();

	}

	public void init()
	{
		databaseHelper = new DatabaseHelper(this);
		
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(calendar.getTime());
		monthSumOfDrinkTimesTextView = (TextView) findViewById(R.id.monthSumOfDrinkTimes_textview);
		monthSumOfDrinkTimesTextView.setText("本月共喝" + monthSumOfDrinkTimes + "瓶");
		
		mostDrinkTimesOfMonth = databaseHelper.getMostDrinkTimesOfMonth(calendar.getTime());
		mostDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.mostDrinkTimesOfMonth_textview);
		mostDrinkTimesOfMonthTextView.setText("本月最多喝" + mostDrinkTimesOfMonth + "瓶");
		
		partTimeOfDrinktimesOfMonth = databaseHelper.getPartTimeOfDrinktimesOfMonth(calendar.getTime());
		amDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.amDrinkTimesOfMonth_textview);
		amDrinkTimesOfMonthTextView.setText("本月上午共喝"+partTimeOfDrinktimesOfMonth[0]+"瓶");
		pmDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.pmDrinkTimesOfMonth_textview);
		pmDrinkTimesOfMonthTextView.setText("本月下午共喝"+partTimeOfDrinktimesOfMonth[1]+"瓶");
		eveningDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.eveningDrinkTimesOfMonth_textview);
		eveningDrinkTimesOfMonthTextView.setText("本月夜间共喝"+partTimeOfDrinktimesOfMonth[2]+"瓶");
		
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth_textview);
	}

}
