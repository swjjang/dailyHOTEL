package com.daily.dailyhotel.screen.stay.outbound;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundSearchViewInterface extends BaseViewInterface
{
    void setCalendarText(String calendarText);

    void setRecentlySuggests(List<Suggest> suggestList);

    void setRecentlySuggestsVisibility(boolean visibility);

    void setSuggestsVisibility(boolean visibility);

    void setSuggests(List<Suggest> suggestList);

    void setSuggest(Suggest suggest);

    void setToolbarMenuEnable(boolean enable);
}
