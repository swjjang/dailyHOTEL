package com.twoheart.dailyhotel.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.GlobalFont;

public class RegionListAdapter extends ArrayAdapter<String> {
	
	public RegionListAdapter(Context context, List<String> regionList) {
		super(context, android.R.layout.simple_list_item_1,
				android.R.id.text1, regionList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setTypeface(DailyHotel.getTypeface());
		view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setTypeface(DailyHotel.getTypeface());
		view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		
		return view;
	}
	
	
	
	

}
