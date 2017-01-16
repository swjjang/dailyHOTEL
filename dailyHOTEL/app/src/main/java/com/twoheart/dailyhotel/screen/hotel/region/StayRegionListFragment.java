package com.twoheart.dailyhotel.screen.hotel.region;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayRegionListFragment extends PlaceRegionListFragment
{
    @Override
    protected void recordAnalyticsScreen(PlaceRegionListActivity.Region region)
    {
        switch (region)
        {
            case DOMESTIC:
                AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
                break;

            case GLOBAL:
                AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
                break;
        }
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mBaseActivity.getString(R.string.label_view_myaround_hotel);
    }
}