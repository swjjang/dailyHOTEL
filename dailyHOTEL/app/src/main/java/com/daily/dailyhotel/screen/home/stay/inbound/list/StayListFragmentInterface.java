package com.daily.dailyhotel.screen.home.stay.inbound.list;


import com.daily.base.BaseFragmentDialogViewInterface;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StayListFragmentInterface extends BaseFragmentDialogViewInterface
{
    void addList();

    void setSwipeRefreshing(boolean refreshing);
}
