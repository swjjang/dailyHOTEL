package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundSearchAnalyticsImpl implements StayOutboundSearchPresenter.StayOutboundSearchAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
        params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OVERSEAS);

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.SEARCHSCREENVIEW_OUTBOUND, null, params);
    }

    @Override
    public void onEventDestroy(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCHSCREEN_OUTBOUND, AnalyticsManager.Label.CLOSED, null);
    }

    @Override
    public void onEventSuggestClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_WINDOW_CLICK, null, null);
    }

    @Override
    public void onEventPeopleClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }


    }

    @Override
    public void onEventPopularSuggestClick(Activity activity, String suggestDisplayName)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(suggestDisplayName) == true)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.OB_SEARCH_RECOMMEND //
            , suggestDisplayName, AnalyticsManager.ValueType.EMPTY, null);
    }
}
