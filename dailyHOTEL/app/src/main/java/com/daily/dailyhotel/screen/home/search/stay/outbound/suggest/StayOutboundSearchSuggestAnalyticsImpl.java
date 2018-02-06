package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayOutboundSearchSuggestAnalyticsImpl implements StayOutboundSearchSuggestPresenter.StayOutboundSearchSuggestAnalyticsInterface
{
    @Override
    public void onEventSuggestEmpty(Activity activity, String keyword)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(keyword) == true)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.AUTOSEARCHNOTFOUND_OUTBOUND//
            , keyword, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventCloseClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_CANCEL, null, null);
    }

    @Override
    public void onEventDeleteAllRecentlySuggestClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_HISTORY_DELETE, null, null);

    }

    @Override
    public void onEventSuggestClick(Activity activity, String suggestDisplayName, String keyword)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(suggestDisplayName) == true)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.OB_SEARCH_AUTO_SEARCH //
            , suggestDisplayName, keyword, null);
    }

    @Override
    public void onEventRecentlySuggestClick(Activity activity, String suggestDisplayName, String keyword)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(suggestDisplayName) == true)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.OB_SEARCH_RECENT //
            , suggestDisplayName, keyword, null);
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
