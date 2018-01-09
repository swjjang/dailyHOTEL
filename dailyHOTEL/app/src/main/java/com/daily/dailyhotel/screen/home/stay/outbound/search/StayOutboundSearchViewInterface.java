package com.daily.dailyhotel.screen.home.stay.outbound.search;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchViewInterface extends BaseDialogViewInterface
{
    void setCalendarText(String calendarText);

    void setSuggest(String suggest);

    void setSearchEnable(boolean enable);

    void setPeopleText(String peopleText);

    void setPopularAreaList(List<Suggest> suggestList);

    void setPopularAreaVisible(boolean visible);
}
