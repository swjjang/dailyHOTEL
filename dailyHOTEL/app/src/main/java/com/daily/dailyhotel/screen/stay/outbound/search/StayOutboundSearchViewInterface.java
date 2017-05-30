package com.daily.dailyhotel.screen.stay.outbound.search;

import com.daily.base.BaseViewInterface;

public interface StayOutboundSearchViewInterface extends BaseViewInterface
{
    void setCalendarText(String calendarText);

    void setSuggest(String suggest);

    void setSearchEnable(boolean enable);

    void setPeopleText(String peopleText);
}
