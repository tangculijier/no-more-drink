package com.huang.views.indexListview;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.huang.model.Phone;
import com.huang.nodrinkmore.R;

public class IndexListViewAdapter extends ArrayAdapter<Phone> implements SectionIndexer
{
	//dafault index content
	private String mySection = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int resourceID;
	public IndexListViewAdapter(Context context, int resource,List<Phone> objects)
	{
		super(context, resource, objects);
		resourceID = resource;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Phone phone = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null)
		{
			view = LayoutInflater.from(getContext()).inflate(resourceID, null);
			viewHolder = new ViewHolder();
			viewHolder.userNameTextView = (TextView) view.findViewById(R.id.username);
			viewHolder.userPhoneTextView = (TextView) view.findViewById(R.id.userphone);
			view.setTag(viewHolder); // 将ViewHolder 存储在View 中
		} else
		{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.userNameTextView.setText(phone.getUserName());
		viewHolder.userPhoneTextView.setText(phone.getUserPhone());
		return view;
	}
	class ViewHolder 
	{
		TextView userNameTextView ;
		TextView userPhoneTextView ;
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
				String value = (PinYin2Abbreviation.Char2Initial(getItem(j).getUserName().charAt(0)) + "").toUpperCase() ;
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
