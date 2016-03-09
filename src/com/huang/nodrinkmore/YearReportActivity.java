package com.huang.nodrinkmore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.huang.Activity.ActionBarBaseActivity;
import com.huang.Activity.MonthReportActivity;
import com.huang.model.ReportOfMonth;
import com.huang.util.AppConst;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;

/**  
 * 年报activity  通过年报可以进入月报界面-MonthReportActivity
 * @author lizheHuang 
 * @Date   time :2016年03月08日  下午20:35:21
 * @version 1.0
 */ 
public class YearReportActivity extends ActionBarBaseActivity implements 
OnChartValueSelectedListener
{

    private BarChart yearChart;
    private Typeface tf;
    DatabaseHelper dataBaseHelper;
    Calendar cal ;
    List<ReportOfMonth> reportList;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_year_report);
		dataBaseHelper = DatabaseHelper.getInstance(this);
		
		cal = Calendar.getInstance();
		Date currentTime = cal.getTime();
		reportList = dataBaseHelper.getYearStatist(currentTime);
		
		yearChart = (BarChart) findViewById(R.id.year_report_chart);
		yearChart.setOnChartValueSelectedListener(this);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        
        
        chartInit();
	}
	private void chartInit()
	{
		yearChart.setDrawBarShadow(false);
		yearChart.setDrawValueAboveBar(true);

		yearChart.setDescription(cal.get(Calendar.YEAR)+"年");
	        // if more than 60 entries are displayed in the chart, no values will be
	        // drawn
		yearChart.setMaxVisibleValueCount(60);
	        // scaling can now only be done on x- and y-axis separately
		yearChart.setPinchZoom(false);

		yearChart.setDrawGridBackground(false);
	        // mChart.setDrawYLabels(false);

	        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

	        XAxis xAxis = yearChart.getXAxis();
	        xAxis.setPosition(XAxisPosition.BOTTOM);
	        xAxis.setTypeface(tf);
	        xAxis.setDrawGridLines(false);
	        xAxis.setSpaceBetweenLabels(2);


	        YAxis leftAxis = yearChart.getAxisLeft();
	        leftAxis.setTypeface(tf);
	        leftAxis.setLabelCount(8, false);
	        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
	        leftAxis.setSpaceTop(15f);

	        YAxis rightAxis = yearChart.getAxisRight();
	        rightAxis.setDrawGridLines(false);
	        rightAxis.setTypeface(tf);
	        rightAxis.setLabelCount(8, false);
	        rightAxis.setSpaceTop(15f);

	        Legend l = yearChart.getLegend();
	        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
	        l.setForm(LegendForm.SQUARE);
	        l.setFormSize(9f);
	        l.setTextSize(11f);
	        l.setXEntrySpace(4f);

	        setData(reportList);
	        yearChart.animateY(2500);


//	        Legend l = yearChart.getLegend();
//	        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
//	        l.setFormSize(8f);
//	        l.setXEntrySpace(4f);

	        // mChart.setDrawLegend(false);
		//setData(50);
	
		//yearChart.animateY(2500);//设置动画效果时间
	}
	
	 private void setData( List<ReportOfMonth> reportList) {

	        ArrayList<String> xVals = new ArrayList<String>();//列名
	        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//数据列

	        for (int i = 0, j = 0 ; i < AppConst.mMonths.length; i++) 
	        {
	            xVals.add(AppConst.mMonths[i % 12]);
	            if(reportList != null && reportList.size() > 0)
	            {
	            	if(j < reportList.size())
	            	{
	            		ReportOfMonth report = reportList.get(j);
			            int monthIndex = DateUtil.getMonth(DateUtil.StringToDate(report.getDate()));
			            if(monthIndex == i+1)
			            {
				            yVals1.add(new BarEntry(report.getTotaltime(), i,report));
				            j++;
			            }
	            	}
	            	
	            }
	            
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
		ReportOfMonth report = (ReportOfMonth) e.getData();
		if(report != null)
		{
			Intent gotoMonthReport = new Intent(YearReportActivity.this,MonthReportActivity.class);
			gotoMonthReport.putExtra(AppConst.INTENT_EXTRA_TIME, report.getDate());
			startActivity(gotoMonthReport);
		}
		
		
		 // Toast.makeText(YearReportActivity.this, report.getDate()+"", Toast.LENGTH_SHORT)
         // .show();
		
	}
	@Override
	public void onNothingSelected()
	{
		
	}
	
	

}
