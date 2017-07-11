package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundSearchSuggestAnalyticsImpl implements StayOutboundSearchSuggestPresenter.StayOutboundSearchSuggestAnalyticsInterface
{
    @Override
    public void onEventSuggestClick(Activity activity, String keyword, Suggest suggest)
    {
        if (DailyTextUtils.isTextEmpty(keyword) == true || suggest == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.AUTOSEARCH_OUTBOUND//
            , suggest.display, keyword, null);
    }

    @Override
    public void onEventSuggestEmpty(Activity activity, String keyword)
    {
        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.KEYWORD_NOT_MATCH_OUTBOUND, keyword, null);
    }
}
