package com.daily.dailyhotel.screen.home.search;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;

public interface SearchInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void showSearchStay();

        void setSearchStaySuggestText(String text);

        void setSearchStayCalendarText(String text);

        void setSearchStayButtonEnabled(boolean enabled);



        void showSearchStayOutbound();

        void setSearchStayOutboundSuggestText(String text);

        void setSearchStayOutboundCalendarText(String text);

        void setSearchStayOutboundPeopleText(String text);

        void setSearchStayOutboundButtonEnabled(boolean enabled);


        void showSearchGourmet();

        void setSearchGourmetSuggestText(String text);

        void setSearchGourmetCalendarText(String text);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onStayClick();

        void onStaySuggestClick();

        void onStayCalendarClick();

        void onStayDoSearchClick();


        void onStayOutboundClick();

        void onStayOutboundSuggestClick();

        void onStayOutboundCalendarClick();

        void onStayOutboundPeopleClick();

        void onStayOutboundDoSearchClick();


        void onGourmetClick();

        void onGourmetSuggestClick();

        void onGourmetCalendarClick();

        void onGourmetDoSearchClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
