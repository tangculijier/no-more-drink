/**  
 * 月报的基本数据结构
 * @author lizheHuang 
 * @Date   time :2015年11月13日  上午11:19:07
 * @version 1.0
 */ 

package com.huang.model;

public class Report
{
	/**
	 * 月报的所属年月 设定为每月最后一天 如2015-11-30
	 */
	String date;
	/**
	 * 本月没有喝饮料的天数
	 */
	int noDrinkDays;
	/**
	 * 本月保持最长的天数
	 */
	int longestKeepDays;
	/**
	 * 月报早上喝饮料的次数
	 */
	int morningtimes;
	int afternoontimes;
	int eveningtimes;
	
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public int getNoDrinkDays()
	{
		return noDrinkDays;
	}
	public void setNoDrinkDays(int noDrinkDays)
	{
		this.noDrinkDays = noDrinkDays;
	}
	public int getLongestKeepDays()
	{
		return longestKeepDays;
	}
	public void setLongestKeepDays(int longestKeepDays)
	{
		this.longestKeepDays = longestKeepDays;
	}
	public int getMorningtimes()
	{
		return morningtimes;
	}
	public void setMorningtimes(int morningtimes)
	{
		this.morningtimes = morningtimes;
	}
	public int getAfternoontimes()
	{
		return afternoontimes;
	}
	public void setAfternoontimes(int afternoontimes)
	{
		this.afternoontimes = afternoontimes;
	}
	public int getEveningtimes()
	{
		return eveningtimes;
	}
	public void setEveningtimes(int eveningtimes)
	{
		this.eveningtimes = eveningtimes;
	}
}
