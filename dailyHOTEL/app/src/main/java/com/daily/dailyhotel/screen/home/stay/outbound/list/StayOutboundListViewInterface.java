package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.StayOutbound;

import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundListViewInterface extends BaseDialogViewInterface
{
    enum EmptyScreenType
    {
        DEFAULT,
        FILTER_ON,
    }

    void setToolbarTitle(String title);

    void setCalendarText(String calendarText);

    void setPeopleText(String peopleText);

    void setStayOutboundList(List<ListItem> listItemList, boolean isSortByDistance, boolean isNights);

    void addStayOutboundList(List<ListItem> listItemList);

    void setStayOutboundMakeMarker(List<StayOutbound> stayOutboundList);

    void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList);

    int getMapLayoutResourceId();

    void setViewTypeOptionImage(StayOutboundListPresenter.ViewState viewState);

    void setFilterOptionImage(boolean onOff);

    // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
    void showMapLayout(FragmentManager fragmentManager);

    void hideMapLayout(FragmentManager fragmentManager);

    void setMapViewPagerVisibility(boolean visibility);

    boolean isMapViewPagerVisibility();

    void setMyLocation(Location location);

    void setRefreshing(boolean refreshing);

    void setErrorScreenVisible(boolean visible);

    void setEmptyScreenVisible(boolean visible);

    void setSearchLocationScreenVisible(boolean visible);

    void setListScreenVisible(boolean visible);

    void setEmptyScreenType(EmptyScreenType emptyScreenType);

    void setBottomLayoutType(EmptyScreenType emptyScreenType);

    void setBottomLayoutVisible(boolean visible);

    Observable<Long> getLocationAnimation();
}
