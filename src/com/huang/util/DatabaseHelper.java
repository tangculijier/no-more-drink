package com.huang.util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.utils.Utils;
import com.huang.model.Habit;
/**
 * 操纵sqlite的工具类
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private final String TAG = this.getClass().getSimpleName();
	/**
	 * 数据库的名称
	 */
	private static final String DB_NAME = "mydatabase.db";
	/**
	 * 数据库的版本
	 */
	private static final int version = 1;//数据库版本
	
	public static final String CREATE_DRINK = "create table habit (id integer PRIMARY KEY AUTOINCREMENT, date timestamp ,type SMALLINT);";

	private Context ctx;
	
	public DatabaseHelper(Context ctx)
	{
		super(ctx, DB_NAME, null, version);
		this.ctx = ctx;
	}
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public void onCreate(SQLiteDatabase db)
	{
		LogUtil.d("huang", "create database");
		db.execSQL("DROP TABLE IF EXISTS habit");  
		db.execSQL(CREATE_DRINK);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param currentDate  当前的日期
	 * @return List<Habit> 当前日期所属月份喝过饮料的时间集合
	 */
	
	public List<Habit> getCurrentMonthDrinkRecord(Date currentDate)
	{
		List<Habit> currentMonthDrinkRecord = new ArrayList<Habit>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		LogUtil.d("huang", "this month first day="+FirstDayAndLast[0]+" lastday="+FirstDayAndLast[1]);

		 Cursor cursor = db.rawQuery("select  DATE(date),COUNT(id) from habit  where date between ? and ? group by DATE(date)",FirstDayAndLast);
		//select result
		if (cursor.moveToFirst()) 
		{  
		    for (int i = 0; i < cursor.getCount(); i++) 
		    {  
		    	String date = cursor.getString(0) ;
		    	int dateDrinkTimes = cursor.getInt(1);
		    	Habit habit = new Habit();
		    	habit.setDate(date);
		    	habit.setDateDrinkTimes(dateDrinkTimes);
		    	LogUtil.d("huang", "cursor i="+i+" date="+date +" dateDrinkTimes="+dateDrinkTimes);
		    	currentMonthDrinkRecord.add(habit);
		    	cursor.moveToNext();  
		    	
		    } 
		}
		if(!cursor.isClosed())
		cursor.close();
		return currentMonthDrinkRecord;
		
		
	}
	
	/**
	 * 记录当天喝饮料的时间
	 */
	public void insertDrinkTime()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		java.util.Date utilDate = new java.util.Date();  
		db.execSQL("insert into habit values(null,?,?)",new Object[]{DateUtil.DateToString(utilDate),"1"});
		LogUtil.d("huang", "insert success");
	}
	
	
	/**
	 * 查询距离上次喝饮料保持的时间
	 */
	public String[] getKeepTime()
	{
		String[] res = new String[2];
		String keepTimeFromLastDrink = "0";
		String recordNumber = "0";
		java.util.Date utilDate = new java.util.Date();  
		java.sql.Date nowDay = new java.sql.Date(utilDate.getTime());  
		
			Habit habit = getRecentDrinkRecord();
			if(habit == null)//no record
			{
				SharedPreferences  setting =ctx.getSharedPreferences(Constant.SHARE_PS_Name, ctx.MODE_PRIVATE);
				String firstDay = setting.getString("firstDay", "");
				res[0] = DateUtil.daysBetween(DateUtil.StringToDate(firstDay), nowDay);
				recordNumber = "0";
				LogUtil.d("huang", "no records firstday="+firstDay+" nowday="+nowDay);
			}
			else
			{
				Date lastDayDate = DateUtil.StringToDate(habit.getDate());
				res[0] = DateUtil.daysBetween(lastDayDate, nowDay);
				recordNumber = "more";
				LogUtil.d("huang", "lastDay="+habit.getDate()+"nowDay="+nowDay+"  daysbetween="+res[0]);
			}
	
		res[1] = recordNumber;
		LogUtil.d("huang", "recordNumber="+res[1]);
		
		return res;
		
	
	}
	
	/**
	 * 得到最近一天喝饮料的记录
	 */
	public Habit getRecentDrinkRecord()
	{
		Habit habit = null;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor=db.rawQuery("select * from habit order by id DESC limit ?,?",new String[]{"0","1"});  
		if(cursor.moveToFirst())  
		{
			int id = cursor.getInt(0);
			String date = cursor.getString(1);
			int type = cursor.getInt(2);
			habit = new Habit(id, date, type); 
			
		}
		cursor.close();
		return habit;
	}

	/**
	 * @param currentDate 当前日期
	 * @return monthSumOfDrinkTimes 本月喝饮料总次数
	 */
	public int getMonthSumOfDrinkTimes(Date currentDate)
	{
		int monthSumOfDrinkTimes = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil
				.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery(
				"select  COUNT(id) from habit  where date between ? and ? ",
				FirstDayAndLast);
		// select result
		if (cursor.moveToFirst())
		{
			monthSumOfDrinkTimes = cursor.getInt(0);
		}
		if (!cursor.isClosed())
			cursor.close();
		return monthSumOfDrinkTimes;
	}

	/**
	 * @param currentDate 当前日期
	 * @return 返回当月喝饮料最多一天的喝饮料次数
	 */
	public int getMostDrinkTimesOfMonth(Date currentDate)
	{
		// TODO Auto-generated method stub
		int mostDrinkTimesOfMonth = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil
				.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery("select COUNT(id) from habit  where date between ? and ? group by DATE(date) order by COUNT(id) DESC",
						FirstDayAndLast); 
		// select result
		if (cursor.moveToFirst())
		{
			mostDrinkTimesOfMonth = cursor.getInt(0);
		}
		if (!cursor.isClosed())
			cursor.close();
		return mostDrinkTimesOfMonth;
	}
	
	
	/**
	 * @param currentDate 当前时间
	 * @return 返回数组PartTimeOfDrinktimesOfMonth，数组里边三项分别为一个月上午，下午晚上喝饮料的统计次数
	 */
	public int[] getTimeSectionOfDrinktimesOfMonth(Date currentDate)
	{
		int[] timeSectionOfDrinktimesOfMonth = new int[3];
		SQLiteDatabase db = this.getReadableDatabase();
		
		int[] timeSection = {0 ,12 ,18 ,24 };
		int hour;
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = null;
		
			cursor = db.rawQuery("select strftime('%H',date)  from habit  where   date between ? and ? ",
							FirstDayAndLast); 
		if (cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				hour = Integer.parseInt(cursor.getString(0));
				if (timeSection[0] <= hour && hour < timeSection[1])
					timeSectionOfDrinktimesOfMonth[0]++;
				else if (timeSection[1] <= hour && hour < timeSection[2])
					timeSectionOfDrinktimesOfMonth[1]++;
				else
					timeSectionOfDrinktimesOfMonth[2]++;
				cursor.moveToNext();
			}
		}
		
		if (!cursor.isClosed())
			cursor.close();
		return timeSectionOfDrinktimesOfMonth;
	}
	
	
	/**
	 * @param currentDat 当前日期时间
	 * @return 返回月最长保持不喝饮料天数
	 */
	public int getLongestKeepingDayOfMonth(Date currentDate)
	{
		
		int longestKeepingDayOfMonth = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = null;
		
		cursor = db.rawQuery("select strftime('%d',date) from habit  where   date between ? and ? ",
						FirstDayAndLast); 
		
		int firstDayIndexInThisMonth = getFirstDayIndexInThisMonth(currentDate);//得到这个月的第一天index
		if (!cursor.moveToFirst())//如果本月无纪录 说明从一开始就保持没有喝
		{
			//最长保持不喝饮料天数= 当天-第一天
	    	 longestKeepingDayOfMonth = DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth ;
		}
		else
		{
			int thisRecordDate = 0;
			LogUtil.d("huang",firstDayIndexInThisMonth+"=lastRecordDate");
			for (int i = 0; i < cursor.getCount(); i++)
			{
				thisRecordDate = Integer.parseInt(cursor.getString(0));
				LogUtil.d("huang",thisRecordDate+"=thisRecordDate");
				longestKeepingDayOfMonth = (thisRecordDate - firstDayIndexInThisMonth - 1) > longestKeepingDayOfMonth ? (thisRecordDate - firstDayIndexInThisMonth - 1) : longestKeepingDayOfMonth;
				firstDayIndexInThisMonth = thisRecordDate;
				cursor.moveToNext();
			}
		}
	
	if (!cursor.isClosed())
		cursor.close();
		
		return longestKeepingDayOfMonth; 
	}
	
	
	/**
	 * @param time 当前日期
	 * @return 本月没喝饮料的自觉天数
	 */
	public int getNoDrinkDaysNumber(Date currentDate)
	{
		int conscienceDays = 0;
		SQLiteDatabase db = this.getReadableDatabase();

		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery("select  COUNT(*) from (select  COUNT(DATE(date)) from habit  where date between ? and ? group by DATE(date)) ",FirstDayAndLast); 
		// select result
		int firstDayIndexInThisMonth = getFirstDayIndexInThisMonth(currentDate);//得到这个月的第一天index

		if (!cursor.moveToFirst()) //如果本月没有记录
		{
			//则  本月没喝饮料的自觉天数 = 当前日期 - 第一天
			conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth + 1;
		} 
		else//如果本月有纪录
		{
			//则  本月没喝饮料的自觉天数 = 当前日期 - 第一天 - 喝了多少天
			 int drinkDays = Integer.parseInt(cursor.getString(0));//多少天喝了
			 conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth 
					 -drinkDays + 1;
		}
		if (!cursor.isClosed())
			cursor.close();
		return conscienceDays;
	}
	
	/**
	 * 
	 * @param currentDate 当前时间
	 * @return 得到当前时间所属月份的第一天
	 */
	public int getFirstDayIndexInThisMonth(Date currentDate)
	{
		int firstDayIndexInThisMonth;
		SharedPreferences  setting =ctx.getSharedPreferences(Constant.SHARE_PS_Name, ctx.MODE_PRIVATE);
		String firstDay = setting.getString("firstDay", "");//example 2015-11-04
		
		Calendar cal = Calendar.getInstance();
		if (DateUtil.isSameMonth(DateUtil.StringToDate(firstDay), currentDate))//如果是安装的时间和当前时间属于同一个月份
		{
			
			cal.setTime(DateUtil.StringToDate(firstDay));
			firstDayIndexInThisMonth = cal.get(Calendar.DAY_OF_MONTH);//第一天从安装的那一天开始算起
		}
		else
		{
			firstDayIndexInThisMonth = 0;//如果不是这个月安装的 从这月的第一天开始算 
		}
		return firstDayIndexInThisMonth;
	}
	
	
}
