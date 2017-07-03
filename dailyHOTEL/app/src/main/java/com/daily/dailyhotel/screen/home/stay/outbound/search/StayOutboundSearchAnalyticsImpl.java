package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.app.Activity;

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

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, "overseas");

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
}
