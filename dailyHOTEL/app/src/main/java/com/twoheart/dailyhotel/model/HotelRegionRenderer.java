package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.RegionIconGenerator;

public class HotelRegionRenderer
{
    private int mCount;
    private RegionIconGenerator mRegionIconGenerator;

    public HotelRegionRenderer(Context context, int count)
    {
        mCount = count;
        mRegionIconGenerator = new RegionIconGenerator(context);

        if (count < 4)
        {
            mRegionIconGenerator.setTextPadding(Util.dpToPx(context, 10));
        } else if (count >= 4 && count < 10)
        {
            mRegionIconGenerator.setTextPadding(Util.dpToPx(context, 15));
        } else if (count >= 10 && count < 21)
        {
            mRegionIconGenerator.setTextPadding(Util.dpToPx(context, 18));
        } else
        {
            mRegionIconGenerator.setTextPadding(Util.dpToPx(context, 25));
        }
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