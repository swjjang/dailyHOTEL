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
import com.twoheart.dailyhotel.util.ExLog;

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
			if (element.getPayType() == 20)
			{
				TextView ment = (TextView) v.findViewById(R.id.tv_booking_row_ment);
				ment.setText(context.getString(R.string.actionbar_title_payment_wait_activity));
			}

			String str = items.get(position).getSday();
			str = str.replace("-", " / ");
			str = "20" + str;
			ExLog.e("SDAY : " + items.get(position).getSday() + " / " + str);
			day.setText(str);
			name.setText(items.get(position).getHotel_name());
		}

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) v);
		return v;
	}

}
