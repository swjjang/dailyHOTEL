package com.twoheart.dailyhotel.booking;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BookingListAdapter extends ArrayAdapter<BookingListElement>{
	
	private ArrayList<BookingListElement> items;
	private Context context;
	private int resourceId;
	
	public BookingListAdapter(Context context, int resourceId, ArrayList<BookingListElement> items) {
		super(context, resourceId, items);
		
		this.items = items;
		this.context = context;
		this.resourceId = resourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
			v.setTag(position);
		}
		
		BookingListElement element = items.get(position);
		
		if(element != null) {
			TextView day = (TextView) v.findViewById(R.id.tv_booking_row_day);
			TextView name = (TextView) v.findViewById(R.id.tv_booking_row_name);
			
			String str = items.get(position).getSday();
			str.replace(".", "/");
			str = "20" + str;
			day.setText(str);
			name.setText(items.get(position).getHotel_name());
		}
		
		return v;
	}

}
