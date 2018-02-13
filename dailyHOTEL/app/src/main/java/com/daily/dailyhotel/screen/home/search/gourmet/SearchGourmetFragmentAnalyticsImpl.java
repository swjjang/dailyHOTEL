package com.daily.dailyhotel.screen.home.search.gourmet;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchGourmetFragmentAnalyticsImpl implements SearchGourmetFragmentInterface.AnalyticsInterface
{
    @Override
    public void onEventRecentlyList(Activity activity, boolean empty)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , empty ? "no_recent_checked_search" : "yes_recent_checked_search", null, null);
    }

    @Override
    public void onEventRecentlyDeleteClick(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "recent_search_place_delete", gourmetName, null);
    }
}
