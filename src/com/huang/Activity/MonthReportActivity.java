package com.huang.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.huang.model.ReportOfMonth;
import com.huang.nodrinkmore.R;
import com.huang.util.AppConst;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;
import com.huang.util.MyTextUtil;
import com.huang.views.MyDatePicker;

public class MonthReportActivity extends Activity implements
		OnChartValueSelectedListener
{
	
	DatabaseHelper databaseHelper;
	
	Calendar calendar ;
	
	Date currentTime ;
	/**
	 * 所属月份-title
	 */
	private String currentMonth;
	/**
	 * 所属月份-title textview
	 */
	private TextView currentMonthTextView;  
	/**
	 * 所属月份-title的字体颜色
	 */
	private int currentMonthColor;
	/**
	 * 自觉天数(没有喝饮料的天数)
	 */
	private int noDrinkDays = 0;
	/**
	 * 显示自觉天数即不喝饮料的天数的textview
	 */
	private TextView noDrinkDaysTextView;  
	/**
	 * 最长保持纪录(天)
	 */
	private int longestKeepingDayOfMonth = 0;
	/**
	 * 最长保持纪录的textiview
	 */
	private TextView longestKeepingDayOfMonthTextView; // 最长保持纪录


	/**
	 * 月饮用总量
	 */
	private int monthSumOfDrinkTimes = 0;
	
	/**
	 * 隐藏的cup 默认gone
	 * 一个月如果没有任何记录则显示cup
	 */
	ImageView cup;
	/**
	 * 底部的小提示
	 */
	private TextView tipsTextView;
	
	/**
	 *  喝饮料时间数组分别代表早上次数，中午次数，晚上次数
	 */
	private int partTimeOfDrinktimesOfMonth[] ;

	/**
	 * 喝饮料时间分布图表
	 */
	private PieChart mChart;
	private Typeface tf;

	protected String[] sectionString = new String[] { "早上", "下午", "晚上" };
	/**
	 * 分布图的颜色
	 */
	protected int[] sectionColor;
	
	/**
	 * mDatePicker初始年份和月份
	 */
	int curyear = -1;
	int curmonth = -1;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		Intent intent = getIntent();
		String currentTimeStr  = intent.getStringExtra(AppConst.INTENT_EXTRA_TIME);
		if(!TextUtils.isEmpty(currentTimeStr))
		{
			LogUtil.d("huang", "intnt="+currentTimeStr);
			currentTime = DateUtil.StringToDate(currentTimeStr);
		}
		ifFirstAnalysis();
		findById();
		initData();

	}

	public void ifFirstAnalysis()
	{
		SharedPreferences setting = getSharedPreferences(AppConst.SHARE_PS_Name, MODE_PRIVATE);
		boolean isFirstAnalysis= setting.getBoolean(AppConst.IS_FIRST_ANANLYSIS, true);
		if(isFirstAnalysis == true)
		{
			
			final Dialog dialog = new Dialog(MonthReportActivity.this,
					getResources().getString(R.string.tip),
					getResources().getString(R.string.report_dialog_text));
			dialog.show();
			ButtonFlat acceptButton = dialog.getButtonAccept();
			acceptButton.setText("我知道了");
			setting.edit().putBoolean(AppConst.IS_FIRST_ANANLYSIS, false).commit();
			
		}
	}

	public void findById()
	{
		sectionColor= new int[] { 
				getResources().getColor(R.color.green_light_more)
				, getResources().getColor(R.color.blue)
				, getResources().getColor(R.color.grey)};
		currentMonthTextView = (TextView) findViewById(R.id.currentMonth);
		noDrinkDaysTextView = (TextView) findViewById(R.id.noDrinkDays);
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth);
		cup = (ImageView)findViewById(R.id.cup);
		tipsTextView = (TextView)findViewById(R.id.tips);
		mChart = (PieChart) findViewById(R.id.pie_chart);
		currentMonthTextView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Calendar cal = Calendar.getInstance();
				MyDatePicker mDatePicker = new MyDatePicker(MonthReportActivity.this,Datelistener, Calendar.getInstance());
				mDatePicker.setIcon(R.drawable.datepicker_icon);
				mDatePicker.setTitle(R.string.report_datepicker_dialog_title);
				if(curyear != -1 && curmonth != -1)//如果都不是初始值的话将mDatePicker初始时间置为当前所选
				{
					mDatePicker.updateDate(curyear, curmonth, 1);
				}
		
				mDatePicker.show();
			}
		});
	}
	private DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
        	
        	//得到所选月份的最后一天
        	monthOfYear += 1 ;
        	String monthOfYearStr = monthOfYear < 10 ? ( "0" + monthOfYear ): monthOfYear + "";
            String pickDateStr = myyear + "-" + monthOfYearStr + "-" + "01";
            Date pickDate = DateUtil.getLastDateInMonth(DateUtil.StringToDate(pickDateStr)); 
            LogUtil.d("huang", "pickDate="+pickDate);
            final ReportOfMonth report = databaseHelper.getAnalysis(DateUtil.DateToStringNoHour(pickDate));
            if(!TextUtils.isEmpty(report.getDate()))
            {
            	//刷新所有
            	currentMonthTextView.setText(myyear + "/" + monthOfYearStr);
            	

				longestKeepingDayOfMonthTextView.setText(MyTextUtil.highLightNumber(longestKeepingDayOfMonthTextView, 
						report.getLongestKeepDays()+"", currentMonthColor));

				noDrinkDaysTextView.setText(MyTextUtil.highLightNumber(noDrinkDaysTextView, 
						report.getNoDrinkDays()+"", currentMonthColor));

            	int[] sectionDate = new int[]{report.getMorningtimes(),
            			report.getAfternoontimes(),report.getEveningtimes()}; 
    			initChartData(sectionDate);
    			curyear = myyear;
    			curmonth = monthOfYear - 1;
    		
            }
            else if(DateUtil.isSameMonth(currentTime, pickDate) == true)//如果重新选择回当前月份
            {
            	curyear = myyear;
    			curmonth = monthOfYear - 1;
            	initData();
            }
            else
            {
            	Toast.makeText(MonthReportActivity.this, "所选月份没有记录", Toast.LENGTH_LONG).show();

            }
            
        }
    };
	public void initData()
	{
		calendar = Calendar.getInstance();
		if(currentTime == null)
		{
			
			currentTime = calendar.getTime();
			LogUtil.d("huang", "currentTime == null:"+currentTime);

		}
		else//不为null说明是从其他activity跳转而来
		{
			calendar.setTime(currentTime);
			LogUtil.d("huang", "currentTime != null:"+currentTime);

		}

		
		databaseHelper = DatabaseHelper.getInstance(this);
		
		noDrinkDays = databaseHelper.getNoDrinkDaysNumber(currentTime);
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(currentTime);
		partTimeOfDrinktimesOfMonth = databaseHelper.getTimeSectionOfDrinktimesOfMonth(currentTime);

		int month = calendar.get(Calendar.MONTH) + 1;
		String monthStr = month < 10 ? ( "0" + month ): month + "";
		tf = Typeface.createFromAsset(getAssets(), "AgencyFB.ttf");
		currentMonthTextView.setText(calendar.get(Calendar.YEAR)+"/"+monthStr);
		currentMonthTextView.setTypeface(tf,Typeface.BOLD);

		currentMonthColor = getResources().getColor(R.color.green_dark);
		noDrinkDaysTextView.setText(MyTextUtil.highLightNumber(noDrinkDaysTextView, noDrinkDays+"", currentMonthColor));
		longestKeepingDayOfMonthTextView.setText(MyTextUtil.highLightNumber(longestKeepingDayOfMonthTextView, longestKeepingDayOfMonth+"", currentMonthColor));

		initChart();



	
	}

	private void initChart()
	{
		
		mChart.setDescription("");// 设置表格的描述 在右下角
		mChart.setExtraOffsets(5, 10, 5, 5);// 设置图表外，布局内显示的偏移量

		mChart.setDragDecelerationFrictionCoef(0.95f);// 拖拽滚动时的速度快慢，[0,1) 0代表立即停止
		tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
		mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(),"OpenSans-Light.ttf"));
		mChart.setDrawHoleEnabled(true);// 是否有中间掏空的圆
		mChart.setCenterTextSize(12f);
		mChart.setRotationAngle(270);// 从最中间开始动画
		mChart.setRotationEnabled(true);// 可以手动旋转
		mChart.setOnChartValueSelectedListener(this);
		mChart.setUsePercentValues(true);// 是否带百分号
		mChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
		
		initChartData(partTimeOfDrinktimesOfMonth);
	}

	private SpannableString generateCenterSpannableText(int sum)
	{
		String title = "喝饮料时间分布";
		String subTitle = "\n饮用总量 " ;
		String numberStr =  sum + "瓶";
		int titleLength = title.length();
		int subTitleLength = subTitle.length();
		int numberStrLength = numberStr.length();
		
		SpannableString spannableString = new SpannableString(title + subTitle + numberStr);
		spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, titleLength, 0);
		
		spannableString.setSpan(new StyleSpan(Typeface.NORMAL), titleLength,titleLength + subTitleLength, 0);
		spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), titleLength,titleLength + subTitleLength, 0);
		spannableString.setSpan(new RelativeSizeSpan(1.1f), titleLength,titleLength + subTitleLength, 0);
		
		spannableString.setSpan(new StyleSpan(Typeface.ITALIC),spannableString.length() - numberStrLength, spannableString.length(), 0);
		spannableString.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
				spannableString.length() - numberStrLength, spannableString.length(), 0);
		return spannableString;
	}
	
	
	private void initChartData(int[] sectionData)
	{

		boolean isAllZero = true;
		//数据在饼图上的文字列表
		ArrayList<String> yValStringArray = new ArrayList<String>();
		//底部说明列表
		ArrayList<String> legendStringArray = new ArrayList<String>();
		//数据饼图
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		//数据颜色
		ArrayList<Integer> colors = new ArrayList<Integer>();
		
		int maxDrinkTimes = 0;
		int maxIndex = -1;
		int sum = 0;
		//添加数据
		for(int i = 0 , j = 0;i < sectionData.length ; i++)
		{
			if(sectionData[i] != 0 )//说明此时间段有记录
			{
				sum += sectionData[i];
				if(sectionData[i] > maxDrinkTimes)
				{
					maxDrinkTimes = sectionData[i] ;
					maxIndex = i;
			
				}
				yVals.add(new Entry(sectionData[i], j++));
				yValStringArray.add(sectionString[i]+" "+sectionData[i] + "瓶");//拼接成 早上:5 瓶
				legendStringArray.add(sectionString[i]);
				colors.add(sectionColor[i]);
				isAllZero = false;
			}
		
		}
		mChart.setCenterText(generateCenterSpannableText(sum));
		makeTips(maxIndex);//give the suggestion of drink
		if(isAllZero == false)
		{
			

			PieDataSet dataSet = new PieDataSet(yVals, "");
			dataSet.setSliceSpace(5f);// 设置每个薄片之间的间距
			dataSet.setSelectionShift(12f);// 点击后放大倍数
			// add a lot of colors

			dataSet.setColors(colors);
			
			PieData data = new PieData(yValStringArray, dataSet);
			data.setValueFormatter(new PercentFormatter());
			data.setValueTextSize(12f);
			data.setValueTextColor(Color.BLACK);
			data.setValueTypeface(tf);

			mChart.setData(data);

			// undo all highlights
			mChart.highlightValues(null);
			
			
			// 类别说明
			Legend legend = mChart.getLegend();
			legend.setPosition(LegendPosition.BELOW_CHART_CENTER);
			/*
			 * 	legend.setCustom(colors, legendStringArray);//自定义的底部分类说明 
			 *	小米4上有bug :java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
			 *	暂时不知道原因
			 */
			legend.setXEntrySpace(7f);
			legend.setYEntrySpace(0f);
			legend.setTextSize(12f);
			legend.setYOffset(5f);

			
			mChart.invalidate();
			showMyChart();
		
		}
		else
		{
			hideMyChart();
		}
		
	}

	
	
	private void showMyChart()
	{
		if(mChart.getVisibility() == View.GONE)
		{
			cup.setVisibility(View.GONE);
			mChart.setVisibility(View.VISIBLE);
			
		}
	}
	private void hideMyChart()
	{
		if(mChart.getVisibility() == View.VISIBLE)
		{
			cup.setVisibility(View.VISIBLE);
			mChart.setVisibility(View.GONE);
		
		}
	
	}
	private void makeTips(int maxIndex)//根据哪个时段喝的最多给出健康提示
	{
		String tips = "";
		switch (maxIndex)
		{
			case -1://没有喝的记录
					tips +="没有喝饮料的记录！再接再厉！";
					break;
			case 0://早上喝的最多
					tips +="早上喝水的话一天充满活力。";
					break;
			case 1://中午喝的最多
					tips +="下午渴了请喝水。";
					break;
			case 2://晚上喝的最多
					tips +="晚上喝太多饮料不宜于睡眠。";
					break;
			default:
					break;
		}
		tipsTextView.setText(tips);
		
	}

	//选中饼图的监听事件
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
	{

		if (e == null)
			return;
		LogUtil.i("VAL SELECTED",
				"Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
						+ ", DataSet index: " + dataSetIndex);
	}

	@Override
	public void onNothingSelected()
	{
		LogUtil.i("PieChart", "nothing selected");
	}

}
