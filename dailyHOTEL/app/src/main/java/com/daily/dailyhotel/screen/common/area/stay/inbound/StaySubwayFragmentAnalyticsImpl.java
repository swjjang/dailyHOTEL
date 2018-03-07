package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StaySubwayFragmentAnalyticsImpl implements StaySubwayFragmentInterface.AnalyticsInterface
{
    @Override
    public void onEventRegionClick(Activity activity, String name)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , "ChangeLocation_subwayRegion", name, null);
    }

    @Override
    public void onEventAreaGroupClick(Activity activity, String regionName, String areaGroupName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , "ChangeLocation_subwayLine", regionName + "_" + areaGroupName, null);
    }

    @Override
    public void onEventAreaClick(Activity activity, String regionName, String areaGroupName, String areaName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , "ChangeLocation_subwayName", regionName + "_" + areaGroupName + "_" + areaName, null);
    }
}
