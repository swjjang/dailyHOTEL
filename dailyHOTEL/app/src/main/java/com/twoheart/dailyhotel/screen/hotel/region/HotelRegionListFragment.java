package com.twoheart.dailyhotel.screen.hotel.region;

import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class HotelRegionListFragment extends PlaceRegionListFragment
{
    @Override
    protected void recordAnalyticsScreen(PlaceRegionListActivity.Region region)
    {
        switch (region)
        {
            case DOMESTIC:
                AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
                break;

            case GLOBAL:
                AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
                break;
        }
    }
}