package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.app.Activity;

import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class StayOutboundListAnalyticsImpl implements StayOutboundListPresenter.StayOutboundListAnalyticsInterface
{
    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_HOTELLIST_OUTBOUND, null);
    }

    @Override
    public StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutbound stayOutbound, String grade, int rankingPosition, int listSize)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutbound != null)
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.rating = Float.toString(stayOutbound.tripAdvisorRating);
        }

        analyticsParam.grade = grade;
        analyticsParam.rankingPosition = rankingPosition;
        analyticsParam.listSize = listSize;

        return analyticsParam;
    }
}
