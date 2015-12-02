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
	 * @param currentDate 当前时间 example : 2015-11-15
	 * @return 返回本月最后一天 example:2015/11/30
	 */
	
	public static Date getLastDateInMonth(Date currentDate)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		return cal.getTime();
	}
	
	
	/**
	 * @param 日期1 日期2
	 * @return 是否是相同的年份和月份
	 */
	
	public static boolean isSameMonth(Date date1,Date date2)
	{
		if(date1 ==null || date2 == null )
		{
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int year1 = cal1.get(Calendar.YEAR); 
		int year2 = cal2.get(Calendar.YEAR); 
		boolean isSameYear = year1 == year2 ? true : false;
		
		int month1 = cal1.get(Calendar.MONTH) + 1; 
		int month2 = cal2.get(Calendar.MONTH) + 1; 
		
		boolean isSameMonth = month1 == month2 ? true : false;
		return isSameYear && isSameMonth;
	}
	
	/**
	 * @param 当前时间
	 * @return 第几天（在当前月份中）
	 */
	public static int getDateIndexInMonth(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return  cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * @param 当前时间
	 * @return 第几个月
	 */
	public static int getMonth(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return  cal.get(Calendar.MONDAY) + 1;
	}
	
	
	/**
	 * @param currentDate 当前时间 example : 2015-11-15
	 * @return 返回上个月最后一天 example:2015/10/31
	 */
	
	public static Date getPreMonthLastDay(Date currentDate)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);//得到本月第一天
		cal.add(Calendar.DATE, -1);//往前推一天
		return cal.getTime();
	}
	
}
