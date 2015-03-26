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
import com.twoheart.dailyhotel.util.RenewalGaManager;

public class RegionListAdapter extends ArrayAdapter<String>
{
	private List<String> list = null;
	private int count;
	private Context context;

	public RegionListAdapter(Context context, List<String> regionList)
	{
		super(context, R.layout.support_simple_spinner_dropdown_item, regionList);
		this.list = regionList;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (position - 1 == list.size())
		{
			notifyDataSetChanged();
		}

		TextView view = (TextView) super.getView(position, convertView, parent);

		view.setTypeface(DailyHotel.getTypeface());
		view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		TextView view = (TextView) super.getView(position, convertView, parent);

		view.setTypeface(DailyHotel.getTypeface());
		view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

		if (position == 0)
		{
			count++;

			if (count == 1)
			{
				RenewalGaManager.getInstance(this.context).recordScreen("regionList", "/todays-hotels/region");
				RenewalGaManager.getInstance(this.context).recordEvent("click", "requestRegionList", null, null);
			}

			if (count == 3)
			{
				count = 0;
			}
		}

		return view;
	}
}
