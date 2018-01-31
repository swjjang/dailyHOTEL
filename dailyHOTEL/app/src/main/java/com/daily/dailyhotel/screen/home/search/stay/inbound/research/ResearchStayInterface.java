package com.daily.dailyhotel.screen.home.search.stay.inbound.research;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;

import io.reactivex.Observable;

public interface ResearchStayInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void showSearchStay();

        void setSearchStaySuggestText(String text);

        void setSearchStayCalendarText(String text);

        void setSearchStayButtonEnabled(boolean enabled);

        Observable getCompleteCreatedFragment();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onStaySuggestClick();

        void onStayCalendarClick();

        void onStayDoSearchClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}


