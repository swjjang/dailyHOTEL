package com.daily.dailyhotel.screen.home.search.gourmet.research;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface ResearchGourmetInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void showSearch();

        void setSearchSuggestText(String text);

        void setSearchCalendarText(String text);

        void setSearchButtonEnabled(boolean enabled);

        Completable getSuggestAnimation();

        Observable getCompleteCreatedFragment();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onSuggestClick();

        void onCalendarClick();

        void onDoSearchClick();

        void onRecentlyHistoryClick(GourmetSearchResultHistory recentlyHistory);

        void onPopularTagClick(CampaignTag campaignTag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}


