package com.daily.dailyhotel.screen.home.stay.outbound.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundDetailAnalyticsImpl implements StayOutboundDetailPresenter.StayOutboundDetailAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity, StayOutboundDetailAnalyticsParam analyticsParam)
    {
        if (analyticsParam == null)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.DBENEFIT, analyticsParam.benefit ? "yes" : "no");
        params.put(AnalyticsManager.KeyType.GRADE, analyticsParam.grade);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(analyticsParam.index));
        params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(analyticsParam.rankingPosition));
        params.put(AnalyticsManager.KeyType.RATING, DailyTextUtils.isTextEmpty(analyticsParam.rating) == true ? AnalyticsManager.ValueType.EMPTY : analyticsParam.rating);
        params.put(AnalyticsManager.KeyType.PLACE_COUNT, analyticsParam.listCount < 0 ? AnalyticsManager.ValueType.EMPTY : Integer.toString(analyticsParam.listCount));
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, "overseas");

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND, null, params);
    }
}
