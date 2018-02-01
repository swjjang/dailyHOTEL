package com.daily.dailyhotel.screen.home.search.stay.outbound.research;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;

import io.reactivex.Observable;

public interface ResearchStayOutboundInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void showSearch();

        void setSearchSuggestText(String text);

        void setSearchCalendarText(String text);

        void setSearchPeopleText(String text);

        void setSearchButtonEnabled(boolean enabled);

        Observable getCompleteCreatedFragment();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onSuggestClick();

        void onCalendarClick();

        void onPeopleClick();

        void onDoSearchClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}


