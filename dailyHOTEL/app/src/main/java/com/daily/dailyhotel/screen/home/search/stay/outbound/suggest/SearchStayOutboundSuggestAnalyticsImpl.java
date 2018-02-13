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

        AnalyticsManager.getInstance(activity).recordEvent(category, keyword, null, null);
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
}
