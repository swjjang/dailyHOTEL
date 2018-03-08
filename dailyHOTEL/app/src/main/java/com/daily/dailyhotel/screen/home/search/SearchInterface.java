package com.daily.dailyhotel.screen.home.search;

import android.app.Activity;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SearchInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void showSearchStay();

        void refreshStay();

        void setSearchStaySuggestText(String text);

        void setSearchStayCalendarText(String text);

        void setSearchStayButtonEnabled(boolean enabled);

        Completable getStaySuggestAnimation();


        void showSearchStayOutbound();

        void refreshStayOutbound();

        void setSearchStayOutboundSuggestText(String text);

        void setSearchStayOutboundCalendarText(String text);

        void setSearchStayOutboundPeopleText(String text);

        void setSearchStayOutboundButtonEnabled(boolean enabled);

        Completable getStayOutboundSuggestAnimation();


        void showSearchGourmet();

        void refreshGourmet();

        void setSearchGourmetSuggestText(String text);

        void setSearchGourmetCalendarText(String text);

        void setSearchGourmetButtonEnabled(boolean enabled);

        Completable getGourmetSuggestAnimation();


        Observable<Boolean> getCompleteCreatedFragment();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onStayClick();

        void onStaySuggestClick();

        void onStayCalendarClick();

        void onStayDoSearchClick();

        void onStayRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace);

        void onStayPopularTagClick(CampaignTag campaignTag);


        void onStayOutboundClick();

        void onStayOutboundSuggestClick();

        void onStayOutboundCalendarClick();

        void onStayOutboundPeopleClick();

        void onStayOutboundDoSearchClick();

        void onStayOutboundRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace);

        void onStayOutboundPopularAreaClick(StayOutboundSuggest stayOutboundSuggest);


        void onGourmetClick();

        void onGourmetSuggestClick();

        void onGourmetCalendarClick();

        void onGourmetDoSearchClick();

        void onGourmetRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace);

        void onGourmetPopularTagClick(CampaignTag campaignTag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventStayClick(Activity activity);

        void onEventStayOutboundClick(Activity activity);

        void onEventGourmetClick(Activity activity);


        void onEventStaySuggestClick(Activity activity);

        void onEventStayDoSearch(Activity activity, StaySuggest suggest);

        void onEventStayCalendarClick(Activity activity);


        void onEventStayOutboundSuggestClick(Activity activity);

        void onEventStayOutboundDoSearch(Activity activity, StayOutboundSuggest suggest);

        void onEventStayOutboundPeopleClick(Activity activity);


        void onEventGourmetSuggestClick(Activity activity);

        void onEventGourmetDoSearch(Activity activity, GourmetSuggest suggest);
    }
}
