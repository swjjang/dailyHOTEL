package com.daily.dailyhotel.screen.home.search.stay.outbound;


import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.StayObSearchResultHistory;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayOutboundFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setRecentlyHistory(List<StayObSearchResultHistory> recentlyHistoryList);

        void setPopularAreaList(List<StayOutboundSuggest> popularAreaList);

        void setPopularAreaVisible(boolean visible);

        void setRecentlyHistoryVisible(boolean visible);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onRecentlyHistoryDeleteClick(StayObSearchResultHistory recentlyHistory);

        void onRecentlyHistoryClick(StayObSearchResultHistory recentlyHistory);

        void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventRecentlyHistory(Activity activity, boolean empty);

        void onEventRecentlyHistoryDeleteClick(Activity activity, String stayName);
    }
}
