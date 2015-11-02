package com.huang.model;

import java.util.Date;
/**
 * 习惯实体类
 * 
 */
public class Habit
{
	/**
	 * 习惯id
	 */
	private int id;

	/**
	 * 发生习惯的时间
	 */
	private String date;
	
	/**
	 * 习惯的类型
	 */
	private int type;

	/**
	 * 习惯发生次数
	 */
	private int dateDrinkTimes;
	
	public Habit(int id, String date, int type)
	{
		super();
		this.id = id;
		this.date = date;
		this.type = type;
	}
	
	public Habit()
	{
		
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public int getDateDrinkTimes()
	{
		return dateDrinkTimes;
	}

	public void setDateDrinkTimes(int dateDrinkTimes)
	{
		this.dateDrinkTimes = dateDrinkTimes;
	}
	//test
	
}
