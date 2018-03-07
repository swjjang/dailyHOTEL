package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayAreaFragmentAnalyticsImpl implements StayAreaFragmentInterface.AnalyticsInterface
{
    @Override
    public void onEventAreaGroupClick(Activity activity, String name)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, "ChangeLocation_locationRegion", name, null);
    }

    @Override
    public void onEventAreaClick(Activity activity, String areaGroupName, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION, "ChangeLocation_locationArea", areaGroupName + "_" + areaGroupName, null);
    }
}
