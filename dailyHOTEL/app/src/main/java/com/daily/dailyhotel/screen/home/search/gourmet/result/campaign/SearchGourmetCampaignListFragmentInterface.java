package com.daily.dailyhotel.screen.home.search.gourmet.result.campaign;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BaseBlurFragmentViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.screen.home.stay.inbound.list.StayTabPresenter;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchGourmetCampaignListFragmentInterface
{
    interface ViewInterface extends BaseBlurFragmentViewInterface
    {
        void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR);

        void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean nightsEnabled, boolean rewardEnabled, boolean supportTrueVR);

        void setStayMapViewPagerList(Context context, List<Stay> stayList, boolean nightsEnabled, boolean rewardEnabled);

        void setMapViewPagerVisible(boolean visible);

        boolean isMapViewPagerVisible();

        void setSwipeRefreshing(boolean refreshing);

        void setEmptyViewVisible(boolean visible, boolean applyFilter);

        void setListLayoutVisible(boolean visible);

        void setMapLayoutVisible(boolean visible);

        // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
        void showMapLayout(FragmentManager fragmentManager, boolean hide);

        void hideMapLayout(FragmentManager fragmentManager);

        void setMapList(List<Stay> stayList, boolean moveCameraBounds, boolean clear, boolean hide);

        void setWish(int position, boolean wish);

        void scrollTop();

        void scrollStop();

        Observable<Long> getLocationAnimation();

        void setMyLocation(Location location);

        void setFloatingActionViewVisible(boolean visible);

        void setFloatingActionViewTypeMapEnabled(boolean enabled);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onSwipeRefreshing();

        void onMoreRefreshing();

        void onGourmetClick(int position, Gourmet stay, int listCount, android.support.v4.util.Pair[] pairs, int gradientType);

        void onGourmetLongClick(int position, Gourmet stay, int listCount, android.support.v4.util.Pair[] pairs);

        // Map Event
        void onMapReady();

        void onMarkerClick(Stay stay, List<Stay> stayList);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void onCallClick();

        void onFilterClick();

        void onRegionClick();

        void onCalendarClick();

        void onWishClick(int position, Stay stay);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType, StayBookDateTime stayBookDateTime, String categoryCode, StayFilter stayFilter, StayRegion stayRegion);

        void onEventStayClick(Activity activity, DailyCategoryType categoryType, StayTabPresenter.ViewType viewType, Stay stay);

        void onEventWishClick(Activity activity, DailyCategoryType categoryType, boolean wish);

        void onEventMarkerClick(Activity activity, DailyCategoryType categoryType, String name);
    }
}
