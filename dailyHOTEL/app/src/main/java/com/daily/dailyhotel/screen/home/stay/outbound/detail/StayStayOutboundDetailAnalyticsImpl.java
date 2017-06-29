package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayStayOutboundDetailAnalyticsImpl implements StayOutboundDetailPresenter.StayOutboundDetailAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        Map<String, String> params = new HashMap<>();

        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT);
        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX);
        String rating = params.get(AnalyticsManager.KeyType.RATING);
        String placeCount = params.get(AnalyticsManager.KeyType.PLACE_COUNT);

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, "overseas");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
    }
}
