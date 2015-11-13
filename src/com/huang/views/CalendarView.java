package com.huang.views;

/**
 * 日历控件
 */


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huang.model.Habit;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class CalendarView extends View  implements View.OnTouchListener
{
	/**
	 * 屏幕密度
	 */
	private float density;
	
	/**
	 * 日历控件整体高度
	 */
	private int calHeight ;
	
	private int calWidth ;
	
	/**
	 * 周一 到周日 框的高度
	 */
	private float weekHeight;
	
	/**
	 * 显示年月 框的高度
	 */
	private float yearHeight;
	
	/**
	 * 显示具体某一天 框的长度
	 */
	private float cellWidth;
	
	private float cellHeight;
	
	/**
	 * 日历控件距离父控件左边的边距
	 */
	private float calLeftMargin;
	/**
	 * 日历控件距离父控件上边的边距
	 */
	private float calTopMargin;
	
	/**
	 * 画日历边框的线条颜色
	 */
	private int borderColor = Color.parseColor("#CCCCCC");
	/**
	 * 日历周一到周日的字体颜色
	 */
	private int WeekTextColor = Color.BLACK;
	/**
	 * 具体日期的颜色 暂用灰色
	 */
	private int dateTextColor = Color.parseColor("#666666");//Color.BLACK;//Color.argb(255, 245, 247, 249);////RGB(245,247,249)
	/**
	 * 今日日期框背景的颜色
	 */
	public int todayBgColor = Color.parseColor("#379BFF");//蓝色	
	/**
	 * 今日日期数字的颜色
	 */
	public int todayNumberColor = Color.WHITE;
	/**
	 * 今日如果喝了饮料日期背景颜色
	 */
	public int todayDrinkedBgColor = Color.BLACK;
	
	/**
	 * 日历展示日期数量 7 * 6
	 */
	private int[] date = new int[42];
	
	public String[] weekText = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat","Sun"};
	
	private Calendar calendar;
	
	/**
	 * 当前页面展示的date
	 */
	private Date currentDate;
	public Date getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(Date currentDate)
	{
		this.currentDate = currentDate;
	}

	/**
	 * 实际当天的date
	 */
	private Date todayDate;
	/**
	 * 当前展示月份开始date和结束date
	 */
	private Date firstShowDate,lastShowDate;
	/**
	 * 从展示开始向后推算这个月实际的第一天在date[]数组中的index
	 */
	private int currentStartIndex,currentEndIndex;
	
	//给控件设置监听事件
	private OnItemClickListener onItemClickListener;
	
	/**
	 * 今天是否喝过饮料
	 */
	public boolean isTodayDrinked = false;
	
	List<Habit> drinkRecords ;
	
	public List<Habit> getDrinkRecords()
	{
		return drinkRecords;
	}

	public void setDrinkRecords(List<Habit> drinkDateRecords)
	{
		this.drinkRecords = drinkDateRecords;
	}

	public CalendarView(Context context)
	{
		this(context,null);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
	}
	
	void init()
	{
		 density = getResources().getDisplayMetrics().density;
		 calHeight = getResources().getDisplayMetrics().heightPixels * 2 / 5;//phone的2/5
		 calWidth = getResources().getDisplayMetrics().widthPixels;

		 cellHeight =  yearHeight = weekHeight = calHeight / 7f;//一共7行
		 cellWidth = calWidth / (weekText.length + 1);//周一-周日7列
		 calLeftMargin = cellWidth / 2;
		 calTopMargin = cellWidth / 3;
		 
		 //初始化时间
		 currentDate = todayDate = new Date();
		 calendar = Calendar.getInstance();
		 calendar.setTime(todayDate);
		 
		 setOnTouchListener(this);
		 
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		//LogUtil.d("huang", "onDraw");
		Paint borderPaint = setBorderPaint();
		Path borderPath = drawBorderPath();//画边框
		
		String yearText = getYearAndMonth(currentDate);//画年月
		Paint yearPaint = setYearPaint();
		//Paint yearBgPaint = setYearBgPaint();
		float textWidth = yearPaint.measureText(yearText);
		
		canvas.drawText(yearText, (calWidth - textWidth) / 2f,yearHeight+10 , yearPaint);
		
		
		Paint weekPaint = setWeekPaint();//画周一-周日的字
		for(int i = 0 ; i < weekText.length; i++ )
		{
			float x =  calLeftMargin + i * cellWidth + (cellWidth - weekPaint.measureText(weekText[i]))/2f;//居中显示
			float y =  yearHeight + weekHeight * 3 / 4f;// 3/4字体大小
			canvas.drawText(weekText[i],x,y, weekPaint);
		}
		
		calculateDate(date);
		
		//画日期
		int todayIndexArray = -1;
		calendar.setTime(currentDate);
		String currentYearAndMonth = getYearAndMonth(currentDate);//滑动后的所在年月
		calendar.setTime(todayDate);
		String todayYearAndMonth = getYearAndMonth(todayDate);//原始今天的年月
		if(TextUtils.equals(currentYearAndMonth, todayYearAndMonth))
		{
			todayIndexArray = currentStartIndex + calendar.get(Calendar.DAY_OF_MONTH)  - 1 ;
		}
		//LogUtil.d("huang", "currentdate="+currentDate+" currentStartIndex="+currentStartIndex);
		
		drawDrinkRecords(canvas);//画喝过饮料的日期背景
		
		drawDateNumberAndBg(canvas, todayIndexArray);//画所有数字
		
		

		canvas.drawPath(borderPath, borderPaint);
	
		super.onDraw(canvas);
	}


	public void drawDrinkRecords(Canvas canvas)
	{
		//LogUtil.d("huang", "drawDrinkRecords");
		if(drinkRecords != null && drinkRecords.size() != 0)
		{
			Paint drinkBgPaint = new Paint();
			drinkBgPaint.setColor(todayDrinkedBgColor);
			
			Calendar calTemp = Calendar.getInstance();
			for(int j = 0;j < drinkRecords.size();j++)
			{
				Habit habit = drinkRecords.get(j);
				int dateDrinkTimes = habit.getDateDrinkTimes();
				Date drinkDate = DateUtil.StringToDate(habit.getDate());
				calTemp.setTime(drinkDate);
				int dayInMonth  =  calTemp.get(Calendar.DAY_OF_MONTH);
				int index = currentStartIndex + dayInMonth - 1;
				drawBgByIndex(canvas, index, drinkBgPaint,dateDrinkTimes);
			//	LogUtil.d("huang", "drinkDate="+drinkDate.toString()+" dayInMonth="+dayInMonth +" index="+index);
				if(TextUtils.equals(DateUtil.DateToStringNoMinute(drinkDate), DateUtil.DateToStringNoMinute(todayDate)))
				{
					isTodayDrinked = true;
				}
				//and habit drintime
			
				
				
			}
			
			
	
		}
	}


	public void drawDateNumberAndBg(Canvas canvas, int todayIndexArray)
	{
		for(int i = 0 ; i < date.length ; i++ )
		{
			int color = dateTextColor;
			if( i < currentStartIndex)//当前月之前上个月几个凑数日期，展示成灰色
			{
				color = borderColor;
			}
			else if( i >= currentEndIndex)//下个月也展示灰色
			{
				color = borderColor;
			}
			if( todayIndexArray != -1 && i == todayIndexArray) //如果是今天 用红色字体标注
			{
				//LogUtil.d("huang", "isTodayDrinked="+isTodayDrinked);
				if(isTodayDrinked == false)//今天还没有喝
				{
					color = todayNumberColor;
					Paint bgPaint = new Paint();
					bgPaint.setColor(todayBgColor);
					drawBgByIndex(canvas, todayIndexArray, bgPaint,0);
				}
			
			}
			
			drawCellText(canvas,i,date[i]+"",color);//其他的用黑色

		}
	}
	
	public void drawBgByIndex(Canvas canvas, int index, Paint Paint, int dateDrinkTimes)
	{
			//LogUtil.d("huang", "drawBgByIndex 接收到的index="+index+" dateDrinkTimes"+dateDrinkTimes);
			//LogUtil.d("huang", "today行"+index % weekText.length );
			//LogUtil.d("huang", "today列"+(index / weekText.length ));
			float left = calLeftMargin + (index % weekText.length ) * cellWidth;
			float top = yearHeight + weekHeight + (index / weekText.length ) * cellHeight;
			float right = left + cellWidth;
			float bottom = top + cellHeight;
			canvas.drawRect(left,top,right,bottom,Paint);//今天的背景
			if(dateDrinkTimes > 1)
			{
				LogUtil.d("huang", "dateDrinkTimes>1 left="+left+" top="+top);
				Paint drinkTimePaint = new Paint();
				float drinkTimesTextSize = cellHeight * 1 / 3;
				drinkTimePaint.setColor(Color.WHITE);
				drinkTimePaint.setTextSize(drinkTimesTextSize);
				String dateDrinkTimeStr = "×"+dateDrinkTimes;
				canvas.drawText(dateDrinkTimeStr, 
						right - drinkTimePaint.measureText(dateDrinkTimeStr) - 5
						, top + drinkTimesTextSize , drinkTimePaint);
			}
	}

	

	private void calculateDate(int[] date)//以2015年10月11日为例
	{
		calendar.setTime(currentDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 置到2015年10月1号
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);// 计算1号是第几天
		int monthStart = dayInWeek;
		if (monthStart == 1)
		{
			monthStart = 8;
		}
		monthStart -= 2; // 以日为开头-1，以星期一为开头-2 主要是要对应数组所以-1
		currentStartIndex = monthStart;
		date[monthStart] = 1;
		// last month
		if (monthStart > 0)
		{
			calendar.set(Calendar.DAY_OF_MONTH, 0);// 上个月9月最后一天
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--)
			{
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);// 置到9月27日
		}
		firstShowDate = calendar.getTime();// 上面set后得到第一个显示的时间 9月27
		// this month
		calendar.setTime(currentDate);// 置now
		calendar.add(Calendar.MONTH, 1);// 在calendar推后一个月
		calendar.set(Calendar.DAY_OF_MONTH, 0);// 推后一个月的第一天 的前一天 也就是这个月的最后一天
												// 10月31日
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);// 31日
		for (int i = 1; i < monthDay; i++)// 从 monthStart这个月开始赋值
		{
			date[monthStart + i] = i + 1;
		}
		currentEndIndex = monthStart + monthDay;// 上个月到这个月 一共有多少个数字 4+31
		// next month
		for (int i = monthStart + monthDay; i < 42; i++)
		{
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (currentEndIndex < 42)
		{
			// 显示了下一月的
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);// 将日历置到最后一天
		lastShowDate = calendar.getTime();
		
	}

	private void drawCellText(Canvas canvas, int index, String text,int dateTextColor)
	{
		int x = getXByIndex(index);
		int y = getYByIndex(index);
	
		Paint datePaint = new Paint();
		datePaint.setColor(dateTextColor);
		datePaint.setAntiAlias(true);
		float dateTextSize = cellHeight * 0.5f;
		datePaint.setTextSize(dateTextSize);
		datePaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		float dateX = calLeftMargin + cellWidth * x +(cellWidth - datePaint.measureText(text))/2f;
		float dateY = yearHeight + weekHeight + y * cellHeight + cellHeight * 3 / 4f;
		canvas.drawText(text, dateX, dateY, datePaint);
	}

	/**
	 * @param index 当前日期在date[]数组中的下标
	 * @return 当前日期在日历框的第几列
	 */
	private int getXByIndex(int index)
	{
		return  index % weekText.length ;
	}

	/**
	 * @param index 当前日期在date[]数组中的下标
	 * @return 当前日期在日历框的第几行
	 */
	private int getYByIndex(int index)
	{
		return  index / weekText.length ;
	}

	/**
	 * 星期画笔
	 */
	private Paint setWeekPaint()
	{
		Paint weekPaint = new Paint();
		weekPaint.setColor(dateTextColor);
		weekPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.4f;
		weekPaint.setTextSize(weekTextSize);
		//weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
		return weekPaint;
	}
	
	/**
	 * 年月字体画笔
	 */
	private Paint setYearPaint()
	{
		Paint yearPaint = new Paint();
		yearPaint.setColor(dateTextColor);
		yearPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.5f;
		yearPaint.setTextSize(weekTextSize);
		yearPaint.setTypeface(Typeface.DEFAULT_BOLD);
		return yearPaint;
	}

	/**
	 * 边框画笔
	 */
	private Paint setBorderPaint()
	{
		Paint borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Paint.Style.STROKE);
		float borderWidth = (float) (0.5 * density);
		borderWidth = borderWidth < 1 ? 1 : borderWidth;
		borderPaint.setStrokeWidth(borderWidth);
		return borderPaint;
	}

	/**
	 * 边框路径
	 */
	private Path drawBorderPath()
	{
		Path borderPath = new Path();
		float beginX = calLeftMargin;
		float beginY = yearHeight + weekHeight;
		float drawLength = calWidth - 2 * calLeftMargin;
		float drawHeight = 6 * cellHeight;
		borderPath.moveTo(beginX,calTopMargin);
		borderPath.rLineTo(drawLength,0 );//最上面横线
		borderPath.moveTo(beginX, yearHeight + weekHeight + (weekText.length )  * cellHeight );//最下面横线
		borderPath.rLineTo(drawLength, 0);
		
		for(int i = 0 ;i <= 31/7 + 2 ; i++)
		{
			borderPath.moveTo(beginX, beginY + i * cellHeight);//画每一行的横线
			borderPath.rLineTo(drawLength, 0);
		}
		for(int i = 0 ;i < weekText.length ; i++)
		{//画竖线
			borderPath.moveTo(beginX + i * cellWidth,beginY );
			borderPath.rLineTo(0,drawHeight);
		}
		borderPath.moveTo(calWidth - calLeftMargin, beginY );//画最右边的竖线
		borderPath.rLineTo(0,drawHeight);
		return borderPath;
	}

	/**
	 * 得到当前年月
	 * @return eg:2015 年 10 月
	 */
	public  String getYearAndMonth(Date date)
	{
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) + " 年 " +  ((calendar.get(Calendar.MONTH) + 1)+" 月" );
		
	}
	
	/**
	 * 得到上个月的 年份和月份
	 * @return eg:2015年9月
	 */
	public String getLastMonth()
	{
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -1);
		currentDate = calendar.getTime();
		invalidate();//刷新界面一次
		return getYearAndMonth(currentDate);
	}
	
	/**
	 * 得到下个月的 年份和月份
	 * @return eg:2015年11月
	 */
	public String getNextMonth()
	{
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, 1);
		currentDate = calendar.getTime();
		invalidate();//刷新界面一次
		return getYearAndMonth(currentDate);
	}
	
	public void makeBlackToday()
	{
		todayBgColor = todayDrinkedBgColor;
		invalidate();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		
		// TODO Auto-generated method stub
		return false;
	}

}
