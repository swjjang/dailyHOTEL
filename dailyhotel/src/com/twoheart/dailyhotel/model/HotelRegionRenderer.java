package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.RegionIconGenerator;

public class HotelRegionRenderer
{
	private int mCount;
	private RegionIconGenerator mRegionIconGenerator;

	public HotelRegionRenderer(Context context, int count)
	{
		mCount = count;
		mRegionIconGenerator = new RegionIconGenerator(context);
		mRegionIconGenerator.setTextColor(context.getResources().getColor(R.color.white));
	}

	public BitmapDescriptor getBitmap()
	{
		Bitmap icon = mRegionIconGenerator.makeIcon(String.valueOf(mCount));

		if (icon == null)
		{
			return null;
		}

		return BitmapDescriptorFactory.fromBitmap(icon);
	}
}