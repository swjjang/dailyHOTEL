package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.model.DailyCategoryType;

public class StayAreaAnalyticsImpl implements StayAreaTabInterface.AnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, String categoryCode)
    {

    }

    @Override
    public void onEventSearchClick(Activity activity, DailyCategoryType dailyCategoryType)
    {

    }

    @Override
    public void onEventChangedDistrictClick(Activity activity, String previousDistrictName, String previousTownName, String changedDistrictName, String changedTownName, StayBookDateTime stayBookDateTime)
    {

    }

    @Override
    public void onEventChangedDateClick(Activity activity)
    {

    }

    @Override
    public void onEventTownClick(Activity activity, String districtName, String townName)
    {

    }

    @Override
    public void onEventClosedClick(Activity activity, String stayCategory)
    {

    }

    @Override
    public void onEventAroundSearchClick(Activity activity, DailyCategoryType dailyCategoryType)
    {

    }
}
