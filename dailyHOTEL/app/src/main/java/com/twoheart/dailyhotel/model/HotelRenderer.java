package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.R;

import java.text.DecimalFormat;

public class HotelRenderer
{
    private String mPrice;
    private int mMarkerResId;
    private HotelIconGenerator mIconGenerator;

    public HotelRenderer(Context context, Hotel hotel)
    {
        DecimalFormat comma = new DecimalFormat("###,##0");

        mPrice = comma.format(hotel.averageDiscountPrice) + context.getString(R.string.currency);

        mMarkerResId = hotel.getGrade().getMarkerResId();

        mIconGenerator = new HotelIconGenerator(context);
        mIconGenerator.setTextColor(context.getResources().getColor(R.color.white));
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
