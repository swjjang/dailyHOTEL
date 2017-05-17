package com.daily.dailyhotel.screen.stay.outbound.filter;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.StayOutboundFilters;

public interface StayOutboundFilterViewInterface extends BaseViewInterface
{
    void setSort(StayOutboundFilters.SortType sortType);

    void setRating(int rating);
}
