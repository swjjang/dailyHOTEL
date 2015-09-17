package com.twoheart.dailyhotel.model;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.view.HotelIconGenerator;

import java.text.DecimalFormat;

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

        mPrice = "₩" + comma.format(hotel.averageDiscount);

        mMarkerResId = hotel.getCategory().getMarkerResId();

        mIconGenerator = new HotelIconGenerator(mContext);
        mIconGenerator.setTextColor(mContext.getResources().getColor(R.color.white));
    }

    public BitmapDescriptor getBitmap(boolean isSelected)
    {
        Bitmap icon = null;

        try
        {
            if (isSelected == false)
            {
                icon = mIconGenerator.makeIcon(mPrice, mMarkerResId);
            } else
            {
                icon = mIconGenerator.makeSelectedIcon(mPrice, mMarkerResId);
            }
        } catch (OutOfMemoryError e)
        {
            ExLog.d(e.toString());
        }

        if (icon == null)
        {
            return null;
        }

        return BitmapDescriptorFactory.fromBitmap(icon);
    }
}
