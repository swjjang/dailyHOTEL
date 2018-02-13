package com.daily.dailyhotel.screen.home.search.stay.outbound;


import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayOutboundFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
        void setRecentlySearchResultList(List<RecentlyDbPlace> recentlyList);

        void setPopularAreaList(List<StayOutboundSuggest> popularAreaList);

        void setPopularAreaVisible(boolean visible);

        void setRecentlySearchResultVisible(boolean visible);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onRecentlySearchResultDeleteClick(int index, String stayName);

        void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace);

        void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventRecentlyList(Activity activity, boolean empty);

        void onEventRecentlyDeleteClick(Activity activity, String stayName);
    }
}
