package com.daily.dailyhotel.screen.stay.outbound.list;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.ListItem;

import java.util.List;

public interface StayOutboundListViewInterface extends BaseViewInterface
{
    void setToolbarTitle(String title);

    void setStayOutboundList(List<ListItem> listItemList);

    void addStayOutboundList(List<ListItem> listItemList);
}
