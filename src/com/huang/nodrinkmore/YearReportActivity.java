package com.huang.nodrinkmore;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.huang.Activity.ActionBarBaseActivity;
import com.huang.util.AppConst;

/**  
 * 年报activity  通过年报可以进入月报界面-MonthReportActivity
 * @author lizheHuang 
 * @Date   time :2016年03月08日  下午20:35:21
 * @version 1.0
 */ 
public class YearReportActivity extends ActionBarBaseActivity implements 
OnChartValueSelectedListener
{

    private HorizontalBarChart yearChart;
    private Typeface tf;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_year_report);
		yearChart = (HorizontalBarChart) findViewById(R.id.year_report_chart);
		yearChart.setOnChartValueSelectedListener(this);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        
        chartInit();
	}
	private void chartInit()
	{
		yearChart.setDrawBarShadow(false);
		yearChart.setDrawValueAboveBar(true);

		yearChart.setDescription("");

	        // if more than 60 entries are displayed in the chart, no values will be
	        // drawn
		yearChart.setMaxVisibleValueCount(100);

	        // scaling can now only be done on x- and y-axis separately
		yearChart.setPinchZoom(false);

	        // draw shadows for each bar that show the maximum value
	        // mChart.setDrawBarShadow(true);

	        // mChart.setDrawXLabels(false);

		yearChart.setDrawGridBackground(false);
		yearChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF);
	        // mChart.setDrawYLabels(false);
			//控制x轴列表
	        XAxis xl = yearChart.getXAxis();
	        xl.setPosition(XAxisPosition.BOTTOM);
	        xl.setTypeface(tf);
	        xl.setDrawAxisLine(true);
	        xl.setDrawGridLines(true);
	        xl.setGridLineWidth(0.3f);
	        xl.setTextSize(12f);

	        YAxis yl = yearChart.getAxisLeft();
	        yl.setTypeface(tf);
	        yl.setDrawAxisLine(true);
	        yl.setDrawGridLines(true);
	        yl.setGridLineWidth(0.3f);
	        yl.setTextSize(12f);

	        YAxis yr = yearChart.getAxisRight();
	        yr.setTypeface(tf);
	        yr.setDrawAxisLine(true);
	        yr.setDrawGridLines(false);
//	        yr.setInverted(true);

	        setData(50);
	        yearChart.animateY(2500);


//	        Legend l = yearChart.getLegend();
//	        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
//	        l.setFormSize(8f);
//	        l.setXEntrySpace(4f);

	        // mChart.setDrawLegend(false);
		//setData(50);
	
		//yearChart.animateY(2500);//设置动画效果时间
	}
	
	 private void setData( float range) {

	        ArrayList<String> xVals = new ArrayList<String>();//列名
	        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//数据列

	        for (int i = AppConst.mMonths.length ; i >= 0; i--) 
	        {
	            xVals.add(AppConst.mMonths[i % 12]);
	            yVals1.add(new BarEntry((float) (Math.random() * range), i));
	        }

	        BarDataSet set1 = new BarDataSet(yVals1,getResources().getString( R.string.activity_data_title));

	        ArrayList<BarDataSet> yearDrinkData = new ArrayList<BarDataSet>();
	        yearDrinkData.add(set1);

	        BarData data = new BarData(xVals, yearDrinkData);
	        data.setValueTextSize(12f);
	        data.setValueTypeface(tf);

	        yearChart.setData(data);
	    }
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNothingSelected()
	{
		// TODO Auto-generated method stub
		
	}
	
	

}
