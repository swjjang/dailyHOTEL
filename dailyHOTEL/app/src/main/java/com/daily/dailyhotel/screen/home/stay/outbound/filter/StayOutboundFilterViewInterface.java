package com.daily.dailyhotel.screen.home.stay.outbound.filter;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundFilters;

public interface StayOutboundFilterViewInterface extends BaseDialogViewInterface
{
    void setSort(StayOutboundFilters.SortType sortType);

    void setRating(int rating);

    void setSortLayoutEnabled(boolean enabled);
}
