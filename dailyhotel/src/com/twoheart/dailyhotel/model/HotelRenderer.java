package com.twoheart.dailyhotel.model;

import java.text.DecimalFormat;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.util.ui.HotelIconGenerator;

public class HotelRenderer
{
	private Context mContext;
	private String mPrice;
	private int mMarkerResId;
	private HotelIconGenerator mIconGenerator;

	public HotelRenderer(Context context, Hotel hotel)
	{
		mContext = context;

		DecimalFormat comma = new DecimalFormat("###,##0");

		mPrice = "₩" + comma.format(hotel.getDiscount());

		mMarkerResId = hotel.getCategory().getMarkerResId();

		mIconGenerator = new HotelIconGenerator(mContext);
		mIconGenerator.setTextColor(mContext.getResources().getColor(R.color.white));
	}

	public BitmapDescriptor getBitmap(boolean isSelected)
	{
		Bitmap icon = null;

		if (isSelected == false)
		{
			icon = mIconGenerator.makeIcon(mPrice, mMarkerResId);
		} else
		{
			icon = mIconGenerator.makeSelectedIcon(mPrice, mMarkerResId);
		}

		if (icon == null)
		{
			return null;
		}

		return BitmapDescriptorFactory.fromBitmap(icon);
	}
}
