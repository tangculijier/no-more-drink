package com.huang.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;
import com.huang.util.MyTextUtil;

public class MonthReportActivity extends Activity implements
		OnChartValueSelectedListener
{
	
	DatabaseHelper databaseHelper;
	
	
	Calendar calendar ;
	
	Date currentTime ;
	/**
	 * 时间区间
	 */
	private TextView dateDurationTextView;  
	private String dateDuration;

	private TextView noDrinkDaysTextView;  
	/**
	 * 自觉天数(没有喝饮料的天数)
	 */
	private int noDrinkDays = 0;

	private TextView monthSumOfDrinkTimesTextView; // 月饮用总量
	/**
	 * 月饮用总量
	 */
	private int monthSumOfDrinkTimes = 0;

	private TextView longestKeepingDayOfMonthTextView; // 最长保持纪录
	/**
	 * 最长保持纪录(天)
	 */
	private int longestKeepingDayOfMonth = 0;

	/**
	 * 底部的小提示
	 */
	private TextView tipsTextView;
	
	private int partTimeOfDrinktimesOfMonth[] ;

	private PieChart mChart;// 喝饮料时间分布图表
	private Typeface tf;// 字体

	protected String[] sectionString = new String[] { "早上", "下午", "晚上" };
	protected int[] sectionColor;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		findById();
		initData();

	}

	public void findById()
	{
		sectionColor= new int[] { 
				getResources().getColor(R.color.green_light_more)
				, getResources().getColor(R.color.blue)
				, getResources().getColor(R.color.grey)};
		dateDurationTextView = (TextView) findViewById(R.id.dateDuration);
		noDrinkDaysTextView = (TextView) findViewById(R.id.noDrinkDays);
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth);
		tipsTextView = (TextView)findViewById(R.id.tips);
		mChart = (PieChart) findViewById(R.id.pie_chart);
	}
	
	public void initData()
	{
		
		calendar = Calendar.getInstance();
		currentTime = calendar.getTime();
		databaseHelper = new DatabaseHelper(this);
		
		dateDuration = DateUtil.getDateDuration(currentTime);
		noDrinkDays = databaseHelper.getNoDrinkDaysNumber(currentTime);
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(currentTime);
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(currentTime);
		partTimeOfDrinktimesOfMonth = databaseHelper.getTimeSectionOfDrinktimesOfMonth(currentTime);

		
		String tempText = dateDurationTextView.getText().toString().replace("%s", dateDuration);
		dateDurationTextView.setText(tempText);

		//tempText = noDrinkDaysTextView.getText().toString().replace("%s", noDrinkDays + "");
		noDrinkDaysTextView.setText(MyTextUtil.getSuperscriptSpan(noDrinkDaysTextView.getText().toString(),noDrinkDays+"",getResources().getColor(R.color.green_light_more)));

		tempText = longestKeepingDayOfMonthTextView.getText().toString().replace("%s", longestKeepingDayOfMonth + "");
		longestKeepingDayOfMonthTextView.setText(MyTextUtil.getSuperscriptSpan(longestKeepingDayOfMonthTextView.getText().toString(),longestKeepingDayOfMonth+"",getResources().getColor(R.color.green_light_more)));

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
		mChart.setCenterText(generateCenterSpannableText());
		mChart.setCenterTextSize(12f);
		mChart.setRotationAngle(270);// 从最中间开始动画
		mChart.setRotationEnabled(true);// 可以手动旋转
		mChart.setOnChartValueSelectedListener(this);
		mChart.setUsePercentValues(true);// 是否带百分号
		mChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
		
		initChartData();
	}

	private SpannableString generateCenterSpannableText()
	{

		String text = "喝饮料时间分布\n饮用总量 " + monthSumOfDrinkTimes + "瓶";
		int x = 7;
		int y = 7;
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, x, 0);
		spannableString.setSpan(new StyleSpan(Typeface.NORMAL), x,spannableString.length() - y, 0);
		spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), x,spannableString.length() - y, 0);
		spannableString.setSpan(new RelativeSizeSpan(.8f), x,spannableString.length() - y, 0);
		spannableString.setSpan(new StyleSpan(Typeface.ITALIC),spannableString.length() - x, spannableString.length(), 0);
		spannableString.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
				spannableString.length() - x, spannableString.length(), 0);
		return spannableString;
	}
	
	
	private void initChartData()
	{

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
		//添加数据
		for(int i = 0 , j = 0;i < partTimeOfDrinktimesOfMonth.length ; i++)
		{
			if(partTimeOfDrinktimesOfMonth[i] != 0 )//说明此时间段有记录
			{
				if(partTimeOfDrinktimesOfMonth[i] > maxDrinkTimes)
				{
					maxDrinkTimes = partTimeOfDrinktimesOfMonth[i] ;
					maxIndex = i;
				}
				yVals.add(new Entry(partTimeOfDrinktimesOfMonth[i], j++));
				yValStringArray.add(sectionString[i]+" "+partTimeOfDrinktimesOfMonth[i] + "瓶");//拼接成 早上:5 瓶
				legendStringArray.add(sectionString[i]);
				colors.add(sectionColor[i]);
			}
		
		}
		makeTips(maxIndex);//give the suggestion of drink

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
		legend.setXEntrySpace(7f);
		legend.setYEntrySpace(0f);
		legend.setTextSize(12f);
		legend.setYOffset(5f);
		legend.setCustom(colors, legendStringArray);//自定义的底部分类说明
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
			default:break;
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
