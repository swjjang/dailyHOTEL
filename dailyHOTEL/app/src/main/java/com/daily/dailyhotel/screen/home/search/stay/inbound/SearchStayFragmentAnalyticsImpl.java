package com.daily.dailyhotel.screen.home.search.stay.inbound;

import android.app.Activity;

import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SearchStayFragmentAnalyticsImpl implements SearchStayFragmentInterface.AnalyticsInterface
{
    @Override
    public void onEventRecentlyHistory(Activity activity, boolean empty)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , empty ? "no_recent_search" : "yes_recent_search", "stay", null);
    }

    @Override
    public void onEventRecentlyHistoryDeleteClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SEARCH_//
            , "recent_search_delete", "stay", null);
    }
}
