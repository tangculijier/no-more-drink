package com.huang.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 计算date时间的工具类
 */
public class DateUtil
{

	
	/**
	 * 
	 * @param last 前一个日期
	 * @param now  后一个日期
	 * @return 两个日期相差的天数
	 */
	public static String daysBetween(Date last,Date now)
	{
		if(last != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			long lastTime = cal.getTimeInMillis();
			cal.setTime(now);
			long nowTime = cal.getTimeInMillis();       
	        long between_days=(nowTime - lastTime) / ( 1000 * 3600 * 24);  
	        return String.valueOf(between_days); 
		}
		else
		{
			return "0";
		}
		
	}

	/**
	 * @param dataStr eg:2015-10-01
	 * @return Date eg:Thu Oct 01 16:46:18 CST 2015
	 */
	public static Date StringToDate(String dataStr)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		Date temp = null;
		try
		{
			temp = sdf.parse(dataStr);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return temp;  
	}
	
	/**
	 * @param Date eg:Thu Oct 01 16:46:18 CST 2015
	 * @return String eg:2015-10-01 12:39:52
	 */
	public static String DateToString(Date date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		return sdf.format(date); 
	}
	
	/**
	 * @param Date eg:Thu Oct 01 16:46:18 CST 2015
	 * @return String eg:2015-10-01 
	 */
	public static String DateToStringNoHour(Date date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		return sdf.format(date); 
	}
	
	
	public static String DateToStringNoMinute(Date date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		return sdf.format(date); 
	}
	
	/**
	 * @param Date eg:Thu Oct 01 16:46:18 CST 2015
	 * @return String[] eg:2015-10-01 00:00:00 and 2015-10-31 23:59:59
	 */
	public static String[] getMonthFirstAndLastDate(Date currentDate)
	{
		String[] res = new String[2];
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date first = cal.getTime();
		res[0] =DateToStringNoHour(first) + " 00:00:00";
		cal.add(Calendar.MONTH, 1);// 在calendar推后一个月
		cal.set(Calendar.DAY_OF_MONTH, 0);
		Date last = cal.getTime();
		res[1] =DateToStringNoHour(last) + " 23:59:59";
		return res;
	}
	
	/**
	 * @param currentDate 当前时间 example : 2015.11.15
	 * @return 返回本月期间 example: 2015/11/01~2015/11/15
	 */
	
	public static String getDateDuration(Date currentDate)
	{
		String dateDuration ="";
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date first = cal.getTime();
		dateDuration = DateToStringNoHour(first) + "~" + DateToStringNoHour(currentDate);
		dateDuration.replace('.', '/');
		return dateDuration;
	}
}
