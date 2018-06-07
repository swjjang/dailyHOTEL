package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.daily.base.util.DailyTextUtils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.R;

public class PlaceRenderer
{
    private String mPrice;
    private int mMarkerResId;
    private PlaceIconGenerator mIconGenerator;

    public PlaceRenderer(Context context, int price, int markerResId)
    {
        mPrice = DailyTextUtils.getPriceFormat(context, price, false);
        mMarkerResId = markerResId;
        mIconGenerator = new PlaceIconGenerator(context);
    }

    public BitmapDescriptor getBitmap(boolean isSelected)
    {
        Bitmap icon;

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
