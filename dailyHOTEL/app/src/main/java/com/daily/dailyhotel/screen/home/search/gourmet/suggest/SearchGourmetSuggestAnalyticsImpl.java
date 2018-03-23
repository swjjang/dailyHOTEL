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

        AnalyticsManager.getInstance(activity).recordEvent(category, keyword, "gourmet", null);
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

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, "voice_search", "gourmet", null);
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
    public void onDeleteRecentlyGourmet(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_, "recent_search_place_delete", "gourmet", null);
    }

    @Override
    public void onScreen(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordScreen(activity, "Search_quering", null);
    }
}
