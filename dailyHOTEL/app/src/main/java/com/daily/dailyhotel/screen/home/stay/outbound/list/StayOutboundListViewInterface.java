package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

import io.reactivex.Observable;

public interface StayOutboundListViewInterface extends BaseBlurViewInterface
{
    enum EmptyScreenType
    {
        NONE,
        SEARCH_SUGGEST_DEFAULT,
        SEARCH_SUGGEST_FILTER_ON,
        LOCATION_DEFAULT,
        LOCATOIN_FILTER_ON
    }

    void setToolbarTitle(String title);

    void setToolbarTitle(String titleText, CharSequence subTitleText);

    void setRadius(float radius);

    void setRadiusVisible(boolean visible);

    void setCalendarText(String calendarText);

    void setStayOutboundList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled);

    void addStayOutboundList(List<ObjectItem> objectItemList);

    void setStayOutboundMakeMarker(List<StayOutbound> stayOutboundList, boolean fixedMap, boolean clear);

    void setStayOutboundMapViewPagerList(Context context, List<StayOutbound> stayOutboundList, boolean isNights, boolean rewardEnabled);

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

    void setSearchLocationScreenVisible(boolean visible);

    void setListScreenVisible(boolean visible);

    void setShimmerScreenVisible(boolean visible);

    void showEmptyScreen(EmptyScreenType emptyScreenType);

    void hideEmptyScreen();

    void setBottomLayoutType(EmptyScreenType emptyScreenType);

    void setBottomLayoutVisible(boolean visible);

    Observable<Long> getLocationAnimation();

    void showPreviewGuide();

    void setWish(int position, boolean wish);

    ObjectItem getObjectItem(int position);

    void setMapProgressBarVisible(boolean visible);

    void setPopularAreaList(List<StayOutboundSuggest> popularAreaList);

    void setPopularAreaVisible(boolean visible);

    void showRadiusPopup();

    void setShimmerViewVisible(boolean visible);
}
