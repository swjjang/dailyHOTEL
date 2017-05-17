package com.daily.dailyhotel.screen.stay.outbound.search;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchViewInterface extends BaseViewInterface
{
    void setCalendarText(String calendarText);

    void setSuggest(String suggest);

    void setToolbarMenuEnable(boolean enable);

    void setPersons(Persons persons);
}
