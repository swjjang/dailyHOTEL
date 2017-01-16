package com.twoheart.dailyhotel.screen.gourmet.region;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetRegionListFragment extends PlaceRegionListFragment
{
    @Override
    protected void recordAnalyticsScreen(PlaceRegionListActivity.Region region)
    {
        switch (region)
        {
            case DOMESTIC:
                AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
                break;

            case GLOBAL:
                break;
        }
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mBaseActivity.getString(R.string.label_view_myaround_gourmet);
    }
}