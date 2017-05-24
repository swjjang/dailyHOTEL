package com.daily.dailyhotel.screen.stay.outbound.list;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutbound;

import java.util.List;

public interface StayOutboundListViewInterface extends BaseViewInterface
{
    void setToolbarTitle(String title);

    void setCalendarText(String calendarText);

    void setPeopleText(String peopleText);

    void setStayOutboundList(List<ListItem> listItemList);

    void addStayOutboundList(List<ListItem> listItemList);

    void setStayOutboundMakeMarker(List<StayOutbound> stayOutboundList);

    void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList);

    int getMapLayoutResourceId();

    void setViewTypeOptionLayout(boolean enabled);

    void setFilterOptionLayout(boolean enabled);

    void setViewTypeOptionImage(StayOutboundListPresenter.ViewState viewState);

    void setFilterOptionImage(boolean onOff);

    // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
    void showMapLayout(FragmentManager fragmentManager);

    void hideMapLayout(FragmentManager fragmentManager);

    void setMapViewPagerVisibility(boolean visibility);

    boolean isMapViewPagerVisibility();

    void setMyLocation(Location location);

    void setRefreshing(boolean refreshing);
}
