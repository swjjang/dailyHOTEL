package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.daily.base.util.ScreenUtils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class PlaceRegionRenderer
{
    private int mCount;
    private RegionIconGenerator mRegionIconGenerator;

    public PlaceRegionRenderer(Context context, int count)
    {
        mCount = count;
        mRegionIconGenerator = new RegionIconGenerator(context);

        if (count < 4)
        {
            mRegionIconGenerator.setTextPadding(ScreenUtils.dpToPx(context, 10));
        } else if (count >= 4 && count < 10)
        {
            mRegionIconGenerator.setTextPadding(ScreenUtils.dpToPx(context, 15));
        } else if (count >= 10 && count < 21)
        {
            mRegionIconGenerator.setTextPadding(ScreenUtils.dpToPx(context, 18));
        } else
        {
            mRegionIconGenerator.setTextPadding(ScreenUtils.dpToPx(context, 25));
        }
    }

    public BitmapDescriptor getBitmap()
    {
        Bitmap icon = mRegionIconGenerator.makeIcon(String.valueOf(mCount));

        return BitmapDescriptorFactory.fromBitmap(icon);
    }
}