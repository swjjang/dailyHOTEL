package com.twoheart.dailyhotel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;

public class BookingListAdapter extends ArrayAdapter<Booking>
{

	private ArrayList<Booking> items;
	private Context context;
	private int resourceId;

	public BookingListAdapter(Context context, int resourceId, ArrayList<Booking> items)
	{
		super(context, resourceId, items);

		this.items = items;
		this.context = context;
		this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;

		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
			v.setTag(position);
		}

		Booking element = items.get(position);

		if (element != null)
		{
			TextView day = (TextView) v.findViewById(R.id.tv_booking_row_day);
			TextView name = (TextView) v.findViewById(R.id.tv_booking_row_name);
			TextView ment = (TextView) v.findViewById(R.id.tv_booking_row_ment);

			String str = items.get(position).getSday();
			str = str.replace("-", " / ");
			str = "20" + str;
			day.setText(str);

			name.setText(items.get(position).getHotel_name());
			ment.setText(items.get(position).ment);
		}

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) v);
		return v;
	}

}
