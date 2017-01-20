package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;

import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetListMapFragment extends PlaceListMapFragment
{
    public GourmetListMapFragment()
    {
    }

    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new GourmetMapViewPagerAdapter(context);
    }

    @Override
    protected void onAnalyticsMarkerClick(String placeName)
    {
        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_MAP_ICON_CLICKED, placeName, null);
    }
}
