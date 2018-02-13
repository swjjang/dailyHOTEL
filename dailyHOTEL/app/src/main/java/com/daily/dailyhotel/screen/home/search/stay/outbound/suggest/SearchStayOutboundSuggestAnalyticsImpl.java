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
}
