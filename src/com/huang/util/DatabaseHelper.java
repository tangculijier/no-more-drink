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
		Cursor cursor = db
				.rawQuery(
						"select COUNT(id) from habit  where date between ? and ? group by DATE(date) order by COUNT(id) DESC",
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
	 * @return 返回月最长保持不喝饮料天数longestKeepingDayOfMonth
	 */
	public int getLongestKeepingDayOfMonth(Date currentDate)
	{
		int longestKeepingDayOfMonth = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = null;
		
		SharedPreferences  setting =ctx.getSharedPreferences(Constant.SHARE_PS_Name, ctx.MODE_PRIVATE);
		String firstDay = setting.getString("firstDay", "");
		
		cursor = db.rawQuery("select strftime('%d',date) from habit  where   date between ? and ? ",
						FirstDayAndLast); 
		if (cursor.moveToFirst())
		{
			int lastRecordDate = 0;
			int thisRecordDate = 0;
			for (int i = 0; i < cursor.getCount(); i++)
			{
				thisRecordDate = Integer.parseInt(cursor.getString(0));
				LogUtil.d("lin",thisRecordDate+"");
				longestKeepingDayOfMonth = (thisRecordDate - lastRecordDate - 1) > longestKeepingDayOfMonth ? (thisRecordDate - lastRecordDate - 1) : longestKeepingDayOfMonth;
				lastRecordDate = thisRecordDate;
				cursor.moveToNext();
			}
		}
      else//如果本月无纪录
		{
			if (DateUtil.StringToDate(firstDay).getMonth() == currentDate
					.getMonth())//本月安装
				longestKeepingDayOfMonth = Integer.parseInt(DateUtil
						.daysBetween(DateUtil.StringToDate(firstDay),
								currentDate));
			else
				longestKeepingDayOfMonth = Integer.parseInt(DateUtil
						.daysBetween(DateUtil.StringToDate(FirstDayAndLast[0]),
								currentDate));

		}
	
	if (!cursor.isClosed())
		cursor.close();
		
		return longestKeepingDayOfMonth; 
	}
	
	
	/**
	 * @param time
	 * @return
	 */
	public int getConscienceDays(Date currentDate)
	{
		int conscienceDays = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil
				.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db
				.rawQuery(
						"select COUNT(date) from habit  where date between ? and ? group by DATE(date) ",
						FirstDayAndLast); 
		// select result
		if (cursor.moveToFirst())
		{
			conscienceDays = Integer.parseInt(DateUtil
					.daysBetween(DateUtil.StringToDate(FirstDayAndLast[0]),
							currentDate)) - cursor.getInt(0);
		}
		if (!cursor.isClosed())
			cursor.close();
		return conscienceDays;
	}
	
	
	
	
}
