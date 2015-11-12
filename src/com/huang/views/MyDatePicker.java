package com.huang.views;


import java.util.Calendar;

import android.R;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class MyDatePicker extends DatePickerDialog
{

	private int titleId;
	
	public MyDatePicker(Context context, OnDateSetListener callBack,Calendar cal )
	{		
		this(context, callBack, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
	}
	
	public MyDatePicker(Context context, OnDateSetListener callBack, int year,
			int monthOfYear, int dayOfMonth)
	{
		super(context, callBack, year, monthOfYear, dayOfMonth);
		
		//not show the day
		((ViewGroup) this.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android"))
				.setVisibility(View.GONE);
		
		this.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub

			}
		});
		
		this.setButton(DialogInterface.BUTTON_POSITIVE, "确定",new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
	
	}
	
	@Override
	public void setTitle(int titleId)
	{
		this.titleId = titleId;
		super.setTitle(titleId);
	}
	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day)
	{

		super.onDateChanged(view, year, month, day);
		setTitle(titleId);
	}

}
