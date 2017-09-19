package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundListAnalyticsImpl implements StayOutboundListPresenter.StayOutboundListAnalyticsInterface
{
    private StayOutboundListAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayOutboundListAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

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
    public void onEventStayClick(Activity activity, int index)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.STAY_ITEM_CLICK_OUTBOUND, Integer.toString(index), null);
    }

    @Override
    public void onEventDestroy(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCHRESULTVIEW_OUTBOUND, AnalyticsManager.Label.BACK_BUTTON, null);
    }

    @Override
    public void onEventList(Activity activity, String suggest, int size)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(suggest) == true || mAnalyticsParam == null)
        {
            return;
        }

        String label = DailyTextUtils.isTextEmpty(mAnalyticsParam.keyword) ? AnalyticsManager.ValueType.EMPTY : mAnalyticsParam.keyword;

        AnalyticsManager.getInstance(activity).recordEvent(size == 0 ? AnalyticsManager.Category.AUTOSEARCHNOTFOUND_OUTBOUND : AnalyticsManager.Category.AUTOSEARCH_OUTBOUND//
            , suggest, label, null);
    }

    @Override
    public StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutbound stayOutbound, String grade, int rankingPosition, int listSize)
    {
        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutbound != null)
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.rating = stayOutbound.tripAdvisorRating == 0.0f ? null : Float.toString(stayOutbound.tripAdvisorRating);
        }

        analyticsParam.grade = grade;
        analyticsParam.rankingPosition = rankingPosition;
        analyticsParam.listSize = listSize;

        return analyticsParam;
    }
}
