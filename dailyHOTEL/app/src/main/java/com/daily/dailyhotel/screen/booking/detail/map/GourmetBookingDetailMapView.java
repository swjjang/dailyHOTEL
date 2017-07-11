package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Context;

import com.daily.base.BaseActivity;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class GourmetBookingDetailMapView extends PlaceBookingDetailMapView
{
    public GourmetBookingDetailMapView(BaseActivity baseActivity, GourmetBookingDetailMapView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    public PlaceBookingDetailMapViewPagerAdapter getViewPagerAdapter(Context context)
    {
        return new GourmetBookingDetailMapViewPagerAdapter(context);
    }
}
