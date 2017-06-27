package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;

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
        FrameLayout.LayoutParams layoutParams;
        int fontSize;

        if (count < 4)
        {
            final int DPI_30 = ScreenUtils.dpToPx(context, 30);
            layoutParams = new FrameLayout.LayoutParams(DPI_30, DPI_30);
            fontSize = 18;
        } else if (count >= 4 && count < 10)
        {
            final int DPI_42 = ScreenUtils.dpToPx(context, 42);
            layoutParams = new FrameLayout.LayoutParams(DPI_42, DPI_42);
            fontSize = 20;
        } else if (count >= 10 && count < 21)
        {
            final int DPI_55 = ScreenUtils.dpToPx(context, 55);
            layoutParams = new FrameLayout.LayoutParams(DPI_55, DPI_55);
            fontSize = 23;
        } else
        {
            final int DPI_70 = ScreenUtils.dpToPx(context, 70);
            layoutParams = new FrameLayout.LayoutParams(DPI_70, DPI_70);
            fontSize = 24;
        }

        mRegionIconGenerator.setTextSize(fontSize);
        mRegionIconGenerator.setLayoutParams(layoutParams);
    }

    public BitmapDescriptor getBitmap()
    {
        Bitmap icon = mRegionIconGenerator.makeIcon(String.valueOf(mCount));

        return BitmapDescriptorFactory.fromBitmap(icon);
    }
}