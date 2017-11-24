package com.daily.dailyhotel.screen.home.stay.inbound.region;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayRegionListAnalyticsImpl implements StayRegionListPresenter.StayRegionListAnalyticsInterface
{
    @Override
    public void onEventSearchClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_LOCATION_LIST, null);
    }
}
