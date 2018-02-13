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
}
