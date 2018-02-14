package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchStayOutboundSuggestAnalyticsImpl implements SearchStayOutboundSuggestPresenter.SearchStayOutboundSuggestAnalyticsInterface
{
    @Override
    public void onSearchSuggestList(Activity activity, String keyword, boolean hasStayOutboundSuggestList)
    {
        if (activity == null)
        {
            return;
        }

        String category = hasStayOutboundSuggestList ? AnalyticsManager.Category.AUTO_SEARCH_LIST : AnalyticsManager.Category.AUTO_SEARCH_LIST_NO_RESULT;

        AnalyticsManager.getInstance(activity).recordEvent(category, keyword, "ob", null);
    }

    @Override
    public void onDeleteRecentlySearch(Activity activity, String keyword)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, "recent_search_history_delete", keyword, null);
    }

    @Override
    public void onVoiceSearchClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, "voice_search", "ob", null);
    }

    @Override
    public void onLocationSearchNoAddressClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, "around_no_result", "현재위치", null);
    }

    @Override
    public void onRecentlySearchList(Activity activity, boolean hasData)
    {
        if (activity == null)
        {
            return;
        }

        String action = hasData ? "yes_recent_search" : "no_recent_search";

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, action, "ob", null);
    }

    @Override
    public void onRecentlyStayOutboundList(Activity activity, boolean hasData)
    {
        if (activity == null)
        {
            return;
        }

        String action = hasData ? "yes_recent_checked_search" : "no_recent_checked_search";

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, action, "ob", null);
    }
}
