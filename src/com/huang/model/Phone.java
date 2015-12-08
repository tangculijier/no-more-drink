/**  
 * 查询手机内置联系人的时候的输出类 
 * @author lizheHuang 
 * @Date   time :2015年12月8日  下午4:28:44
 * @version 1.0
 */ 

package com.huang.model;

public class Phone
{

	private String userName;
	
	private String userPhone;
	
	public Phone(String userName, String userPhone)
	{
		super();
		this.userName = userName;
		this.userPhone = userPhone;
	}

	public String getUserName()
	{
		return userName;
	}

	

	public String getUserPhone()
	{
		return userPhone;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setUserPhone(String userPhone)
	{
		this.userPhone = userPhone;
	}
	
	
}
