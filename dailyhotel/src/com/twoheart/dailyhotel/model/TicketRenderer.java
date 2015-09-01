package com.twoheart.dailyhotel.model;

import java.text.DecimalFormat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.ui.HotelIconGenerator;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;

public class TicketRenderer
{
	private Context mContext;
	private String mPrice;
	private int mMarkerResId;
	private HotelIconGenerator mIconGenerator;

	public TicketRenderer(Context context, int price, int markerResId)
	{
		mContext = context;

		DecimalFormat comma = new DecimalFormat("###,##0");

		mPrice = "₩" + comma.format(price);

		mMarkerResId = markerResId;

		mIconGenerator = new HotelIconGenerator(mContext);
		mIconGenerator.setTextColor(mContext.getResources().getColor(R.color.white));
	}

	public BitmapDescriptor getBitmap(boolean isSelected)
	{
		Bitmap icon = null;

		try
		{
			if (isSelected == false)
			{
				icon = mIconGenerator.makeIcon(mPrice, mMarkerResId);
			} else
			{
				icon = mIconGenerator.makeSelectedIcon(mPrice, mMarkerResId);
			}
		} catch (OutOfMemoryError e)
		{
			ExLog.d(e.toString());
		}

		if (icon == null)
		{
			return null;
		}

		return BitmapDescriptorFactory.fromBitmap(icon);
	}
}
