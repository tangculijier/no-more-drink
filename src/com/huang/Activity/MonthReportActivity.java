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
import android.view.Window;
import android.widget.ImageView;
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

public class MonthReportActivity extends Activity implements
		OnChartValueSelectedListener
{
	DatabaseHelper databaseHelper;
	Calendar calendar = Calendar.getInstance();
	Date currentTime ;
	/**
	 * 时间区间
	 */
	TextView dateDurationTextView;  
	String dateDuration;

	LinearLayout noDrinkDaysLayout;
	TextView noDrinkDaysTextView;  
	/**
	 * 自觉天数(没有喝饮料的天数)
	 */
	int noDrinkDays = 0;

	TextView monthSumOfDrinkTimesTextView; // 月饮用总量
	/**
	 * 月饮用总量
	 */
	int monthSumOfDrinkTimes = 0;

	TextView longestKeepingDayOfMonthTextView; // 最长保持纪录
	/**
	 * 最长保持纪录(天)
	 */
	int longestKeepingDayOfMonth = 0;

	TextView amDrinkTimesOfMonthTextView;// 4:00--12:00
	TextView pmDrinkTimesOfMonthTextView;// 12:00--20:00
	TextView eveningDrinkTimesOfMonthTextView;// 20:00--4:00
	int partTimeOfDrinktimesOfMonth[] ;

	private PieChart mChart;// 喝饮料时间分布图表
	private Typeface tf;// 字体

	protected String[] sectionString = new String[] { "早上", "中午", "晚上" };

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		init();

	}

	public void init()
	{
		noDrinkDaysLayout = (LinearLayout)findViewById(R.id.noDrinkDaysLayout);
		dateDurationTextView = (TextView) findViewById(R.id.dateDuration);
		noDrinkDaysTextView = (TextView) findViewById(R.id.noDrinkDays);
		//monthSumOfDrinkTimesTextView = (TextView) findViewById(R.id.monthSumOfDrinkTimes);
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth);
		mChart = (PieChart) findViewById(R.id.pie_chart);

		currentTime = calendar.getTime();
		databaseHelper = new DatabaseHelper(this);
		dateDuration = DateUtil.getDateDuration(currentTime);
		noDrinkDays = databaseHelper.getNoDrinkDaysNumber(currentTime);
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(currentTime);
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(currentTime);
		partTimeOfDrinktimesOfMonth = databaseHelper.getTimeSectionOfDrinktimesOfMonth(currentTime);

		String tempText = dateDurationTextView.getText().toString().replace("%s", dateDuration);
		dateDurationTextView.setText(tempText);

		tempText = noDrinkDaysTextView.getText().toString().replace("%s", noDrinkDays + "");
		noDrinkDaysTextView.setText(tempText);
		
		//用程序来控制 给几个大红花
		for(int i = 0 ;i < 3; i++)
		{
			ImageView image = new ImageView(this);
			image.setBackgroundResource(R.drawable.balance_32);
			noDrinkDaysLayout.addView(image);
		}
	
		//tempText = monthSumOfDrinkTimesTextView.getText().toString().replace("%s", monthSumOfDrinkTimes + "");
		//monthSumOfDrinkTimesTextView.setText(tempText);

		tempText = longestKeepingDayOfMonthTextView.getText().toString().replace("%s", longestKeepingDayOfMonth + "");
		longestKeepingDayOfMonthTextView.setText(tempText);

		initChart();
		initData();

	

	
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
		
		
	}
	  private SpannableString generateCenterSpannableText() {

		  	String text = "喝饮料时间分布\n饮用总量 "+monthSumOfDrinkTimes+"瓶";
		  	int x = 7;
		  	int y = 7;
	        SpannableString spannableString = new SpannableString(text);
	        spannableString.setSpan(new RelativeSizeSpan(1.7f), 0, x, 0);
	        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), x, spannableString.length() - y, 0);
	        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), x, spannableString.length() - y, 0);
	        spannableString.setSpan(new RelativeSizeSpan(.8f), x, spannableString.length() - y, 0);
	        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), spannableString.length() - x, spannableString.length(), 0);
	        spannableString.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), spannableString.length() - x, spannableString.length(), 0);
	        return spannableString;
	    }
	
	
	private void initData()
	{

		ArrayList<String> legendStringArray = new ArrayList<String>();
		
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		//添加数据
		for(int i = 0 ;i < partTimeOfDrinktimesOfMonth.length ; i++)
		{
			yVals.add(new Entry(partTimeOfDrinktimesOfMonth[i], i));
			legendStringArray.add(sectionString[i]+" "+partTimeOfDrinktimesOfMonth[i] + "瓶");//拼接成 早上:5 瓶
		}

		PieDataSet dataSet = new PieDataSet(yVals, "");
		dataSet.setSliceSpace(5f);// 设置每个薄片之间的间距
		dataSet.setSelectionShift(12f);// 点击后放大倍数
		// add a lot of colors

		ArrayList<Integer> colors = new ArrayList<Integer>();
		int morningColor = getResources().getColor(R.color.green_light_more);
		int afternoonColor = getResources().getColor(R.color.blue);
		int evevingColor = getResources().getColor(R.color.grey);
		colors.add(morningColor);
		colors.add(afternoonColor);
		colors.add(evevingColor);
		dataSet.setColors(colors);

		PieData data = new PieData(sectionString, dataSet);
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
