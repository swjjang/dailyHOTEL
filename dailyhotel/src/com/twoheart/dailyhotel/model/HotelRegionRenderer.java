package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.twoheart.dailyhotel.util.ui.RegionIconGenerator;

public class HotelRegionRenderer
{
	private int mCount;
	private RegionIconGenerator mRegionIconGenerator;

	public HotelRegionRenderer(Context context, int count)
	{
		mCount = count;
		mRegionIconGenerator = new RegionIconGenerator(context);

		if (count < 5)
		{
			mRegionIconGenerator.setTextPadding(40);
		} else if (count >= 5 && count < 10)
		{
			mRegionIconGenerator.setTextPadding(45);
		} else if (count >= 10 && count < 20)
		{
			mRegionIconGenerator.setTextPadding(50);
		} else
		{
			mRegionIconGenerator.setTextPadding(60);
		}
	}

	public Bitmap getBitmap()
	{
		Bitmap icon = mRegionIconGenerator.makeIcon(String.valueOf(mCount));

		return icon;

		//		if (icon == null)
		//		{
		//			return null;
		//		}
		//
		//		return BitmapDescriptorFactory.fromBitmap(icon);
	}
}