package com.twoheart.dailyhotel.screen.hotel.region;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayRegionListFragment extends PlaceRegionListFragment
{
    @Override
    protected void recordAnalyticsScreen(PlaceRegionListActivity.Region region)
    {
        try
        {
            switch (region)
            {
                case DOMESTIC:
                    Map<String, String> params = new HashMap<>();

                    if (DailyHotel.isLogin() == false)
                    {
                        params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
                    } else
                    {
                        params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
                    }

                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                    params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.ALL);

                    AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null, params);
                    break;

                case GLOBAL:
                    AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
                    break;
            }
        } catch (Exception e)
        {

        }
    }

    @Override
    protected String getAroundPlaceText()
    {
        return mBaseActivity.getString(R.string.label_view_myaround_hotel);
    }
}