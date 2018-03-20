package com.daily.dailyhotel.screen.home.search.stay.inbound;


import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setRecentlyHistory(List<StaySearchResultHistory> recentlyHistoryList);

        void setPopularSearchTagList(List<CampaignTag> tagList);

        void setPopularSearchTagVisible(boolean visible);

        void setRecentlyHistoryVisible(boolean visible);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onRecentlyHistoryDeleteClick(StaySearchResultHistory recentlyHistory);

        void onRecentlyHistoryClick(StaySearchResultHistory recentlyHistory);

        void onPopularTagClick(CampaignTag campaignTag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventRecentlyHistory(Activity activity, boolean empty);

        void onEventRecentlyHistoryDeleteClick(Activity activity, String stayName);
    }
}
