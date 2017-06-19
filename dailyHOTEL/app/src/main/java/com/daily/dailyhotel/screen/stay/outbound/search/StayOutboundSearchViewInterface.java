package com.daily.dailyhotel.screen.stay.outbound.search;

import com.daily.base.BaseDialogViewInterface;

public interface StayOutboundSearchViewInterface extends BaseDialogViewInterface
{
    void setCalendarText(String calendarText);

    void setSuggest(String suggest);

    void setSearchEnable(boolean enable);

    void setPeopleText(String peopleText);
}
