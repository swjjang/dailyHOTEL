package com.daily.dailyhotel.screen.stay.outbound;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface StayOutboundViewInterface extends BaseViewInterface
{
    void setCalendarText(String calendarText);

    void setRecentlySuggests(List<Suggest> suggestList);

    void setRecentlySuggestsVisibility(boolean visibility);

    void setSuggestsVisibility(boolean visibility);

    void setSuggests(List<Suggest> suggestList);

    void setToolbarMenuEnable(boolean enable);
}
