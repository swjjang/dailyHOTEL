package com.daily.dailyhotel.screen.home.search.gourmet.result.search;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BaseBlurFragmentViewInterface;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabPresenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchGourmetResultListFragmentInterface
{
    interface ViewInterface extends BaseBlurFragmentViewInterface
    {
        void setSearchResultCount(int count, int maxCount);

        void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean supportTrueVR);

        void addList(List<ObjectItem> objectItemList);

        void setMapViewPagerList(Context context, List<Gourmet> gourmetList);

        void setMapViewPagerVisible(boolean visible);

        boolean isMapViewPagerVisible();

        void setSwipeRefreshing(boolean refreshing);

        void hideEmptyViewVisible();

        void showDefaultEmptyViewVisible();

        void showLocationEmptyViewVisible(boolean applyFilter);

        void setListLayoutVisible(boolean visible);

        void setMapLayoutVisible(boolean visible);

        void setLocationProgressBarVisible(boolean visible);

        // 원래 Fragment는 Activity에서 등록이 되어야 하는데 SupportMapFragment는 View로 취급하기로 한다.
        void showMapLayout(FragmentManager fragmentManager);

        void hideMapLayout(FragmentManager fragmentManager);

        void setMapList(List<Gourmet> gourmetList, boolean moveCameraBounds, boolean clear);

        void setWish(int position, boolean wish);

        void setMapWish(int position, boolean wish);

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

        void onGourmetClick(int position, Gourmet gourmet, int listCount, android.support.v4.util.Pair[] pairs, int gradientType);

        void onGourmetLongClick(int position, Gourmet gourmet, int listCount, android.support.v4.util.Pair[] pairs);

        // Map Event
        void onMapReady();

        void onMarkerClick(Gourmet gourmet, List<Gourmet> gourmetList);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void onCallClick();

        void onFilterClick();

        void onRadiusClick();

        void onResearchClick();

        void onCalendarClick();

        void onWishClick(int position, Gourmet gourmet);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, SearchGourmetResultTabPresenter.ViewType viewType, GourmetBookDateTime gourmetBookDateTime, boolean empty);

        void onEventWishClick(Activity activity, boolean wish);

        void onEventMarkerClick(Activity activity, String name);

        void onEventCallClick(Activity activity);

        void onEventGourmetClick(Activity activity, Gourmet gourmet, GourmetSuggest suggest);

        void onEventSearchResult(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetSuggest suggest, String inputKeyword//
            , int searchCount, int searchMaxCount);

        void onEventSearchResultCountOneAndSoldOut(Activity activity, String gourmetName);

        void onEventSearchResultAllSoldOut(Activity activity, String inputKeyword);
    }
}
