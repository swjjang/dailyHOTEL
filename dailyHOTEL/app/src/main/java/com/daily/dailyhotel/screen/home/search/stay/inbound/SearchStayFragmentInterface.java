package com.daily.dailyhotel.screen.home.search.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setRecentlySearchResultList(List<RecentlyDbPlace> recentlyList);

        void setPopularSearchTagList(List<CampaignTag> tagList);

        void setPopularSearchTagVisible(boolean visible);

        void setRecentlySearchResultVisible(boolean visible);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onRecentlySearchResultDeleteClick(int index);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
