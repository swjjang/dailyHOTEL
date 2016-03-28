package com.twoheart.dailyhotel.model;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DecimalFormat;

public class PlaceRenderer
{
    private String mPrice;
    private int mMarkerResId;
    private HotelIconGenerator mIconGenerator;

    public PlaceRenderer(Context context, int price, int markerResId)
    {
        DecimalFormat comma = new DecimalFormat("###,##0");

        mPrice = comma.format(price) + context.getString(com.twoheart.dailyhotel.R.string.currency);

        mMarkerResId = markerResId;

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
