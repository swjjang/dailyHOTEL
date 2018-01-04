package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.v4.app.FragmentManager;

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

    // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
    void showMapLayout(FragmentManager fragmentManager);

    void hideMapLayout(FragmentManager fragmentManager);
}
