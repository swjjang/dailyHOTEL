package com.twoheart.dailyhotel.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.appcompat.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;

public class RegionListAdapter extends ArrayAdapter<String> {
	private List<String> list = null;
	
	public RegionListAdapter(Context context, List<String> regionList) {
		super(context, R.layout.support_simple_spinner_dropdown_item, regionList);
		this.list = regionList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position-1 == list.size()) {
			notifyDataSetChanged();
		}
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
