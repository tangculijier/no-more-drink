package com.huang.views.indexListview;

import java.util.List;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

public class IndexListViewAdapter extends ArrayAdapter<String> implements SectionIndexer
{
	//dafault index content
	private String mySection = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public IndexListViewAdapter(Context context, int resource,List<String> objects)
	{
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getPositionForSection(int sectionIndex)
	{
		//find forward
		for(int i = sectionIndex ; i >= 0; i--  )
		{
			for(int j = 0; j < getCount();j++)//find though the listview
			{
				
				String keyword = (mySection.charAt(i) + "").toUpperCase() ;
				String value = (PinYin2Abbreviation.Char2Initial(getItem(j).charAt(0)) + "").toUpperCase() ;
				if(TextUtils.equals(keyword, value))
				{
					return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position)
	{
		return 0;
	}

	/**
	 * return mySection mySection[]
	 */
	@Override
	public Object[] getSections()
	{
		String[] sections = new String[mySection.length()];  
		for (int i = 0; i < mySection.length(); i++)  
		{
    	   sections[i] = String.valueOf(mySection.charAt(i));  
		}
		return sections;
	}
	
}
