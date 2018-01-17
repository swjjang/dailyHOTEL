package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.daily.dailyhotel.base.BaseBlurFragmentViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StayListFragmentInterface extends BaseBlurFragmentViewInterface
{
    void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR);

    void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR);

    void setStayMakeMarker(List<Stay> stayList, boolean clear);

    void setStayMapViewPagerList(Context context, List<Stay> stayList, boolean nightsEnabled, boolean rewardEnabled);

    void setMapViewPagerVisible(boolean visible);

    void setSwipeRefreshing(boolean refreshing);

    void setEmptyViewVisible(boolean visible, boolean applyFilter);

    void setListLayoutVisible(boolean visible);

    // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
    void showMapLayout(FragmentManager fragmentManager, boolean hide);

    void hideMapLayout(FragmentManager fragmentManager);

    void setMapList(List<Stay> stayList, boolean moveCameraBounds, boolean clear, boolean hide);

    void setWish(int position, boolean wish);

    void scrollTop();
}
