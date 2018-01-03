package com.daily.dailyhotel.screen.home.stay.inbound.list;


import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StayListFragmentInterface extends BaseFragmentDialogViewInterface
{
    void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR);

    void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR);

    void setSwipeRefreshing(boolean refreshing);

    void setEmptyViewVisible(boolean visible, boolean applyFilter);
}
