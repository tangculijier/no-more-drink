/**  
 * text工具类
 * @author lizheHuang 
 * @Date   time :2015年11月9日  下午3:39:23
 * @version 1.0
 */ 

package com.huang.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;

public class MyTextUtil
{

	public static SpannableString getSuperscriptSpan(String text,String highLightStr,int color)
	{
		text = text.replace("%s", highLightStr);//先替换%s
		int start = 5;
		int end = start + String.valueOf(highLightStr).length();
		
		SpannableString spanText = new SpannableString(text);
		spanText.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spanText.setSpan(new RelativeSizeSpan(2.0f), start,end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//相对大小（文本字体）
		spanText.setSpan(new StyleSpan(Typeface.NORMAL), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//字体
		
		spanText.setSpan(new SuperscriptSpan(), end + 1, end + 2, 
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//上标
		spanText.setSpan(new RelativeSizeSpan(0.7f), end + 1,end + 2,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//相对大小（文本字体）
		return spanText;
		
	}
	
}
