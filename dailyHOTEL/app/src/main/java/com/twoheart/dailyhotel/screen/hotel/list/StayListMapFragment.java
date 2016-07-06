package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;

import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayListMapFragment extends PlaceListMapFragment
{
    public StayListMapFragment()
    {
    }

    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new StayMapViewPagerAdapter(context);
    }

    @Override
    protected void onAnalyticsMarkerClick(String placeName)
    {
        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOTEL_MAP_ICON_CLICKED, placeName, null);
    }
}
