package com.daily.dailyhotel.screen.stay.outbound.list;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.stay.outbound.filter.StayOutboundFilterActivity;
import com.daily.dailyhotel.screen.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundListPresenter extends BaseExceptionPresenter<StayOutboundListActivity, StayOutboundListViewInterface> //
    implements StayOutboundListView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    private StayOutboundListAnalyticsInterface mAnalytics;
    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private People mPeople;
    private StayOutboundFilters mStayOutboundFilters;
    private List<StayOutbound> mStayOutboundList;
    private DailyLocationExFactory mDailyLocationExFactory;

    // 리스트 요청시에 다음이 있는지에 대한 인자들
    private String mCacheKey, mCacheLocation;
    private boolean mMoreResultsAvailable;

    private ViewState mViewState = ViewState.LIST;

    enum ViewState
    {
        MAP,
        LIST
    }

    public interface StayOutboundListAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundListPresenter(@NonNull StayOutboundListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundListViewInterface createInstanceViewInterface()
    {
        return new StayOutboundListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundListActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_result_data);

        setAnalytics(new StayOutboundListAnalyticsImpl());

        mViewState = ViewState.LIST;

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        // 기본 성인 2명, 아동 0명
        onPeople(People.DEFAULT_ADULTS, null);

        onFilter(null, -1);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
        {
            SuggestParcel suggestParcel = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

            if (suggestParcel != null)
            {
                mSuggest = suggestParcel.getSuggest();
            }

            String checkInDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKOUT);

            setStayBookDateTime(checkInDateTime, checkOutDateTime);

            int numberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);

            onPeople(numberOfAdults, childAgeList);
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD) == true)
        {
        } else
        {
            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        if (mSuggest.id == 0)
        {
            // 키워드 검색인 경우
            getViewInterface().setToolbarTitle(mSuggest.city);
        } else
        {
            // Suggest 검색인 경우
            getViewInterface().setToolbarTitle(mSuggest.display);
        }

        onStayBookDateTime(mStayBookDateTime);
        onPeople(mPeople);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefreshAll(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (mViewState == ViewState.MAP)
        {
            try
            {
                if (getViewInterface().isMapViewPagerVisibility() == true)
                {
                    getViewInterface().setMapViewPagerVisibility(false);
                } else
                {
                    onViewTypeClick();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                onViewTypeClick();
            }

            return true;
        }

        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case StayOutboundListActivity.REQUEST_CODE_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                        && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                        {
                            return;
                        }

                        onCalendarDateTime(checkInDateTime, checkOutDateTime);
                        onRefreshAll(true);
                    }
                }
                break;
            }

            case StayOutboundListActivity.REQUEST_CODE_PEOPLE:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS) == true && data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST) == true)
                    {
                        int numberOfAdults = data.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> childAgeList = data.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        onPeople(numberOfAdults, childAgeList);
                        onRefreshAll(true);
                    }
                }
                break;
            }

            case StayOutboundListActivity.REQUEST_CODE_FILTER:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT) == true//
                        && data.hasExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING) == true)
                    {
                        StayOutboundFilters.SortType sortType = StayOutboundFilters.SortType.valueOf(data.getStringExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT));
                        int rating = data.getIntExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, -1);

                        onFilter(sortType, rating);

                        if (sortType != null && sortType == StayOutboundFilters.SortType.DISTANCE)
                        {
                            addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                                {
                                    unLockAll();

                                    if (mStayOutboundFilters != null && location != null)
                                    {
                                        mStayOutboundFilters.latitude = location.getLatitude();
                                        mStayOutboundFilters.longitude = location.getLongitude();

                                        onRefreshAll(true);
                                    }
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                                {
                                    onFilter(null, -1);
                                    onRefreshAll(true);
                                }
                            }));
                        } else
                        {
                            onRefreshAll(true);
                        }
                    }
                }
                break;
            }

            case StayOutboundListActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onMyLocationClick();
                        break;

                    default:
                        break;
                }
                break;
            }

            case StayOutboundListActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
                break;
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<StayOutbounds> observable;

        if (mSuggest.id == 0)
        {
            // 키워드 검색인 경우
            unLockAll();
            return;
        } else
        {
            // Suggest 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime//
                , mSuggest.id, mSuggest.categoryKey, mPeople, mStayOutboundFilters, mCacheKey, mCacheLocation);
        }

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), observable//
            , (commonDateTime, stayOutbounds) ->
            {
                onCommonDateTime(commonDateTime);

                return stayOutbounds;
            }).subscribe(stayOutbounds ->
        {
            onStayOutbounds(stayOutbounds);

            getViewInterface().setRefreshing(false);
            unLockAll();
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                // 리스트를 호출하다가 에러가 난 경우 처리 방안
                // 검색 결과 없는 것으로

                getViewInterface().setRefreshing(false);
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onRefreshAll(boolean showProgress)
    {
        clearCache();
        onRefresh(showProgress);
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance();
            startCalendar.setTime(DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT));
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);

            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);

            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.STAY, true, 0, true);

            startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onPeopleClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent;

        if (mPeople == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), mPeople.numberOfAdults, mPeople.getChildAgeList());
        }

        startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_PEOPLE);
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }

        switch (mViewState)
        {
            case LIST:
            {
                Intent intent = StayOutboundFilterActivity.newInstance(getActivity(), mStayOutboundFilters, true, true);
                startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_FILTER);
                break;
            }

            case MAP:
            {
                Intent intent = StayOutboundFilterActivity.newInstance(getActivity(), mStayOutboundFilters, false, true);
                startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_FILTER);
                break;
            }
        }
    }

    @Override
    public void onViewTypeClick()
    {
        if (lock() == true || getActivity() == null)
        {
            return;
        }

        switch (mViewState)
        {
            // 현재 리스트 화면인 경우
            case LIST:
            {
                screenLock(true);

                mViewState = ViewState.MAP;

                getViewInterface().showMapLayout(getActivity().getSupportFragmentManager());

                getViewInterface().setViewTypeOptionImage(ViewState.MAP);
                break;
            }

            // 현재 맵화면인 경우
            case MAP:
            {
                mViewState = ViewState.LIST;

                getViewInterface().hideMapLayout(getActivity().getSupportFragmentManager());

                getViewInterface().setViewTypeOptionImage(ViewState.LIST);
                break;
            }
        }

        unLockAll();
    }

    @Override
    public void onStayClick(android.support.v4.util.Pair[] pair, StayOutbound stayOutbound)
    {
        if (stayOutbound == null || lock() == true)
        {
            return;
        }

        String imageUrl;
        if (ScreenUtils.getScreenWidth(getActivity()) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(stayOutbound.getImageMap().bigUrl) == false)
            {
                imageUrl = stayOutbound.getImageMap().bigUrl;
            } else
            {
                imageUrl = stayOutbound.getImageMap().smallUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(stayOutbound.getImageMap().mediumUrl) == false)
            {
                imageUrl = stayOutbound.getImageMap().mediumUrl;
            } else
            {
                imageUrl = stayOutbound.getImageMap().smallUrl;
            }
        }

        if (Util.isUsedMultiTransition() == true && pair != null)
        {
            getActivity().setExitSharedElementCallback(new SharedElementCallback()
            {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                    for (View view : sharedElements)
                    {
                        if (view instanceof SimpleDraweeView)
                        {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair);

            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, imageUrl//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPeople.numberOfAdults, mPeople.getChildAgeList(), true, mViewState == ViewState.MAP)//
                , StayOutboundListActivity.REQUEST_CODE_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, imageUrl//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPeople.numberOfAdults, mPeople.getChildAgeList(), false, mViewState == ViewState.MAP)//
                , StayOutboundListActivity.REQUEST_CODE_DETAIL);
        }
    }

    @Override
    public void onStayLongClick()
    {

    }

    @Override
    public void onScrollList(int listSize, int lastVisibleItemPosition)
    {
        if (mMoreResultsAvailable == true && lastVisibleItemPosition > listSize / 3)
        {
            onAddList();
        }
    }

    @Override
    public void onViewPagerClose()
    {

    }

    @Override
    public void onMapReady()
    {
        getViewInterface().setStayOutboundMakeMarker(mStayOutboundList);


    }

    @Override
    public void onMarkerClick(StayOutbound stayOutbound)
    {
        if (stayOutbound == null || mStayOutboundList == null)
        {
            return;
        }

        addCompositeDisposable(Observable.just(stayOutbound).subscribeOn(Schedulers.io()).map(new Function<StayOutbound, List<StayOutbound>>()
        {
            @Override
            public List<StayOutbound> apply(@io.reactivex.annotations.NonNull StayOutbound stayOutbound) throws Exception
            {
                Comparator<StayOutbound> comparator = new Comparator<StayOutbound>()
                {
                    public int compare(StayOutbound stayOutbound1, StayOutbound stayOutbound2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(stayOutbound.latitude, stayOutbound.longitude, stayOutbound1.latitude, stayOutbound1.longitude, results1);

                        float[] results2 = new float[3];
                        Location.distanceBetween(stayOutbound.latitude, stayOutbound.longitude, stayOutbound2.latitude, stayOutbound2.longitude, results2);

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(mStayOutboundList, comparator);

                return mStayOutboundList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutbound>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<StayOutbound> stayOutboundList) throws Exception
            {
                getViewInterface().setStayOutboundMapViewPagerList(getActivity(), stayOutboundList);
                getViewInterface().setMapViewPagerVisibility(true);
            }
        }));
    }

    @Override
    public void onMarkersCompleted()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        unLockAll();
    }

    @Override
    public void onMapClick()
    {
        getViewInterface().setMapViewPagerVisibility(false);
    }

    @Override
    public void onMyLocationClick()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
            {
                unLockAll();
                getViewInterface().setMyLocation(location);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void onStayOutbounds(StayOutbounds stayOutbounds)
    {
        if (stayOutbounds == null)
        {
            return;
        }

        final boolean isAdded;

        if (DailyTextUtils.isTextEmpty(mCacheKey, mCacheLocation) == true)
        {
            isAdded = false;

            if (mStayOutboundList != null)
            {
                mStayOutboundList.clear();
            }

            mStayOutboundList = stayOutbounds.getStayOutbound();
        } else
        {
            isAdded = true;

            mStayOutboundList.addAll(stayOutbounds.getStayOutbound());
        }

        if (mViewState == ViewState.MAP)
        {
            getViewInterface().setStayOutboundMakeMarker(mStayOutboundList);
            getViewInterface().setStayOutboundMapViewPagerList(getActivity(), mStayOutboundList);
        }

        addCompositeDisposable(Observable.just(stayOutbounds).subscribeOn(Schedulers.io()).map(new Function<StayOutbounds, List<ListItem>>()
        {
            @Override
            public List<ListItem> apply(StayOutbounds stayOutbounds) throws Exception
            {
                List<ListItem> listItemList = new ArrayList<>();

                // Android 에서는 forEach를 사용하면 런타임시에 문제가 발생할수 있음.
                //                stayOutbounds.getStayOutbound().forEach((stayOutbound) -> listItemList.add(new ListItem(ListItem.TYPE_ENTRY, stayOutbound)));

                for (StayOutbound stayOutbound : stayOutbounds.getStayOutbound())
                {
                    listItemList.add(new ListItem(ListItem.TYPE_ENTRY, stayOutbound));
                }

                if (listItemList.size() > 0)
                {
                    if (stayOutbounds.moreResultsAvailable == true)
                    {
                        listItemList.add(new ListItem(ListItem.TYPE_LOADING_VIEW, null));
                    } else
                    {
                        listItemList.add(new ListItem(ListItem.TYPE_FOOTER_VIEW, null));
                    }
                }

                mCacheKey = stayOutbounds.cacheKey;
                mCacheLocation = stayOutbounds.cacheLocation;
                mMoreResultsAvailable = stayOutbounds.moreResultsAvailable;

                return listItemList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ListItem>>()
        {
            @Override
            public void accept(List<ListItem> listItems) throws Exception
            {
                if (isAdded == false)
                {
                    boolean isSortByDistance = mStayOutboundFilters != null && mStayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE;

                    getViewInterface().setStayOutboundList(listItems, isSortByDistance);
                } else
                {
                    getViewInterface().addStayOutboundList(listItems);
                }
            }
        }));
    }

    private void onCalendarDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        setStayBookDateTime(checkInDateTime, checkOutDateTime);
        onStayBookDateTime(mStayBookDateTime);
    }

    private void onPeople(People people)
    {
        if (mPeople == null)
        {
            return;
        }

        getViewInterface().setPeopleText(mPeople.toShortString(getActivity()));
    }

    private void onPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        setPeople(numberOfAdults, childAgeList);

        onPeople(mPeople);
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);
    }

    private void onFilter(StayOutboundFilters.SortType sortType, int rating)
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        if (sortType == null)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        } else
        {
            mStayOutboundFilters.sortType = sortType;
        }

        mStayOutboundFilters.rating = rating;

        if (sortType != StayOutboundFilters.SortType.DISTANCE)
        {
            mStayOutboundFilters.latitude = 0;
            mStayOutboundFilters.longitude = 0;
        }

        if (mStayOutboundFilters.sortType == StayOutboundFilters.SortType.RECOMMENDATION && mStayOutboundFilters.rating == -1)
        {
            getViewInterface().setFilterOptionImage(false);
        } else
        {
            getViewInterface().setFilterOptionImage(true);
        }
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onStayBookDateTime(@NonNull StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s"//
                , stayBookDateTime.getCheckInDateTime("M.d(EEE)")//
                , stayBookDateTime.getCheckOutDateTime("M.d(EEE)")));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onAddList()
    {
        if (getActivity().isFinishing() == true || lock() == true)
        {
            return;
        }

        Observable<StayOutbounds> observable;

        if (mSuggest.id == 0)
        {
            // 키워드 검색인 경우
            unLockAll();
            return;
        } else
        {
            // Suggest 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime//
                , mSuggest.id, mSuggest.categoryKey, mPeople, mStayOutboundFilters, mCacheKey, mCacheLocation);
        }

        addCompositeDisposable(observable.subscribe(stayOutbounds ->
        {
            onStayOutbounds(stayOutbounds);
            unLockAll();
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                // 리스트를 호출하다가 에러가 난 경우 처리 방안
                // 검색 결과 없는 것으로

                onHandleError(throwable);
            }
        }));
    }

    /**
     * 리스트를 처음부터 호출한다.
     */
    private void clearCache()
    {
        mCacheKey = null;
        mCacheLocation = null;

        if (mStayOutboundList != null)
        {
            mStayOutboundList.clear();
            mStayOutboundList = null;
        }
    }

    private Observable<Location> searchMyLocation()
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory();
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.startLocationMeasure(getActivity(), new DailyLocationExFactory.LocationListenerEx()
                {
                    @Override
                    public void onRequirePermission()
                    {
                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        observer.onError(new Exception());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderEnabled(String provider)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderDisabled(String provider)
                    {
                        observer.onError(new ProviderException());
                    }

                    @Override
                    public void onLocationChanged(Location location)
                    {
                        unLockAll();

                        mDailyLocationExFactory.stopLocationMeasure();

                        if (location == null)
                        {
                            observer.onError(new NullPointerException());
                        } else
                        {
                            observer.onNext(location);
                            observer.onComplete();
                        }
                    }
                });
            }
        }.doOnError(new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                if (throwable instanceof PermissionException)
                {
                    Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    mDailyLocationExFactory.stopLocationMeasure();

                    View.OnClickListener positiveListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_SETTING_LOCATION);
                        }
                    };

                    View.OnClickListener negativeListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                        }
                    };

                    DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                        }
                    };

                    getViewInterface().showSimpleDialog(//
                        getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                        getString(R.string.dialog_btn_text_dosetting), //
                        getString(R.string.dialog_btn_text_cancel), //
                        positiveListener, negativeListener, cancelListener, null, true);
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }
        });
    }
}
