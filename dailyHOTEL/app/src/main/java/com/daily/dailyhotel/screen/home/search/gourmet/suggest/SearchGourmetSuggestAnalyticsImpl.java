package com.daily.dailyhotel.screen.home.search.gourmet.suggest;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchGourmetSuggestAnalyticsImpl implements SearchGourmetSuggestPresenter.SearchGourmetSuggestAnalyticsInterface
{
    @Override
    public void onSearchSuggestList(Activity activity, String keyword, boolean hasGourmetSuggestList)
    {
        if (activity == null)
        {
            return;
        }

        String category = hasGourmetSuggestList ? AnalyticsManager.Category.AUTO_SEARCH_LIST : AnalyticsManager.Category.AUTO_SEARCH_LIST_NO_RESULT;

        AnalyticsManager.getInstance(activity).recordEvent(category, keyword, null, null);
    }
}
