package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchStaySuggestAnalyticsImpl implements SearchStaySuggestPresenter.SearchStaySuggestAnalyticsInterface
{
    @Override
    public void onSearchSuggestList(Activity activity, String keyword, boolean hasStaySuggestList)
    {
        if (activity == null)
        {
            return;
        }

        String category = hasStaySuggestList ? AnalyticsManager.Category.AUTO_SEARCH_LIST : AnalyticsManager.Category.AUTO_SEARCH_LIST_NO_RESULT;

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
