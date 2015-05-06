package com.twoheart.dailyhotel.model;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.HotelIconGenerator;

public class HotelRenderer
{
	private Context mContext;
	private String mPrice;
	private HotelIconGenerator mIconGenerator;
	//		private boolean mIsSoldOut;
	private boolean mIsDailyChoice;

	public HotelRenderer(Context context, Hotel hotel)
	{
		mContext = context;

		int originalPrice = Integer.parseInt(hotel.getDiscount().replaceAll(",", ""));
		DecimalFormat comma = new DecimalFormat("###,##0");

		mPrice = "₩" + comma.format(originalPrice);

		mIconGenerator = new HotelIconGenerator(mContext);

		// SOLD OUT
		//			mIsSoldOut = hotel.getAvailableRoom() == 0;

		mIconGenerator.setTextColor(mContext.getResources().getColor(R.color.white));
		mIconGenerator.setColor(mContext.getResources().getColor(hotel.getCategory().getColorResId()));
	}

	public BitmapDescriptor getBitmap()
	{
		Bitmap icon = mIconGenerator.makeIcon(mPrice, false, mIsDailyChoice);

		if (icon == null)
		{
			return null;
		}

		return BitmapDescriptorFactory.fromBitmap(icon);
	}
}
