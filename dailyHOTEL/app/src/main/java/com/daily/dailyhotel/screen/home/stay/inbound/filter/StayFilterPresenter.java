package com.daily.dailyhotel.screen.home.stay.inbound.filter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayFilterPresenter extends BaseExceptionPresenter<StayFilterActivity, StayFilterInterface> implements StayFilterView.OnEventListener
{
    private static final int CLICK_FILTER_DELAY_TIME = 500;

    StayFilterAnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;

    StayFilter mFilter;
    StaySuggest mSuggest;
    StayBookDateTime mStayBookDateTime;
    List<String> mCategoryList;
    Location mLocation;
    float mRadius;
    String mSearchWord;
    Constants.ViewType mViewType;
    StayFilterCount milterCount;
    DailyCategoryType mCategoryType = DailyCategoryType.STAY_ALL;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface StayFilterAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onConfirmClick(Activity activity, StaySuggest suggest, StayFilter stayFilter, int listCountByFilter);

        void onBackClick(Activity activity);

        void onResetClick(Activity activity);

        void onEmptyResult(Activity activity, StayFilter stayFilter);
    }

    public StayFilterPresenter(@NonNull StayFilterActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayFilterInterface createInstanceViewInterface()
    {
        return new StayFilterView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayFilterActivity activity)
    {
        setContentView(R.layout.activity_stay_filter_data);

        setAnalytics(new StayFilterAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayFilterAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        String checkInDateTime = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        try
        {
            mStayBookDateTime = new StayBookDateTime();
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        }

        try
        {
            mCategoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_CATEGORY_TYPE));
        } catch (Exception e)
        {
            mCategoryType = DailyCategoryType.STAY_ALL;
        }

        String viewType = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_VIEW_TYPE);

        if (DailyTextUtils.isTextEmpty(viewType) == true)
        {
            return false;
        }

        mViewType = Constants.ViewType.valueOf(viewType);

        StayFilterParcel stayFilterParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_FILTER);

        if (stayFilterParcel == null)
        {
            return false;
        }

        mFilter = stayFilterParcel.getFilter();

        StaySuggestParcel suggestParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel == null)
        {
            return false;
        }

        mSuggest = suggestParcel.getSuggest();

        mCategoryList = intent.getStringArrayListExtra(StayFilterActivity.INTENT_EXTRA_DATA_CATEGORIES);

        mLocation = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION);
        mRadius = intent.getFloatExtra(StayFilterActivity.INTENT_EXTRA_DATA_RADIUS, 0);
        mSearchWord = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_SEARCH_WORD);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.activity_curation_title));

        notifyFilterChanged();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
            case StayFilterActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onCheckedChangedSort(StayFilter.SortType.DISTANCE);
                        break;

                    default:
                        mFilter.sortType = StayFilter.SortType.DEFAULT;

                        notifyFilterChanged();
                        break;
                }
                break;
            }

            case StayFilterActivity.REQUEST_CODE_SETTING_LOCATION:
                onCheckedChangedSort(StayFilter.SortType.DISTANCE);
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        onRefresh(0);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();

        mAnalytics.onBackClick(getActivity());
    }

    @Override
    public void onMinusPersonClick()
    {
        if (--mFilter.person < StayFilter.PERSON_COUNT_OF_MIN)
        {
            mFilter.person = StayFilter.PERSON_COUNT_OF_MIN;
        }

        getViewInterface().setPerson(mFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onPlusPersonClick()
    {
        if (++mFilter.person > StayFilter.PERSON_COUNT_OF_MAX)
        {
            mFilter.person = StayFilter.PERSON_COUNT_OF_MAX;
        }

        getViewInterface().setPerson(mFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onResetClick()
    {
        if (lock() == true)
        {
            return;
        }

        mFilter.reset();

        notifyFilterChanged();

        onRefresh(CLICK_FILTER_DELAY_TIME);

        mAnalytics.onResetClick(getActivity());
    }

    @Override
    public void onConfirmClick()
    {
        if (milterCount == null || lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(StayFilterActivity.INTENT_EXTRA_DATA_FILTER, new StayFilterParcel(mFilter));

        if (mFilter.sortType == StayFilter.SortType.DISTANCE)
        {
            intent.putExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION, mLocation);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();

        try
        {
            mAnalytics.onConfirmClick(getActivity(), mSuggest, mFilter, milterCount == null ? 0 : milterCount.searchCount);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCheckedChangedSort(StayFilter.SortType sortType)
    {
        if (sortType == null)
        {
            return;
        }

        if (sortType == StayFilter.SortType.DISTANCE)
        {
            if (lock() == true)
            {
                return;
            }

            screenLock(true);

            // https://fabric.io/daily/android/apps/com.twoheart.dailyhotel/issues/5a5f14458cb3c2fa63ff8597?time=last-seven-days
            Observable<Location> observable = searchMyLocation();

            if (observable == null)
            {
                unLockAll();

                finish();
                return;
            }

            addCompositeDisposable(observable.subscribe(new Consumer<Location>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                {
                    mFilter.sortType = sortType;

                    mLocation = location;

                    onRefresh(CLICK_FILTER_DELAY_TIME);

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                }
            }));
        } else
        {
            mFilter.sortType = sortType;
        }
    }

    @Override
    public void onCheckedChangedBedType(int flag)
    {
        mFilter.flagBedTypeFilters ^= flag;

        getViewInterface().setBedTypeCheck(mFilter.flagBedTypeFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedAmenities(int flag)
    {
        mFilter.flagAmenitiesFilters ^= flag;

        getViewInterface().setAmenitiesCheck(mFilter.flagAmenitiesFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedRoomAmenities(int flag)
    {
        mFilter.flagRoomAmenitiesFilters ^= flag;

        getViewInterface().setRoomAmenitiesCheck(mFilter.flagRoomAmenitiesFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    void notifyFilterChanged()
    {
        if (mFilter == null)
        {
            return;
        }

        getViewInterface().setSortLayout(mFilter.sortType);
        getViewInterface().setSortLayoutEnabled(mViewType == Constants.ViewType.LIST);
        getViewInterface().setPerson(mFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);
        getViewInterface().setBedTypeCheck(mFilter.flagBedTypeFilters);
        getViewInterface().setAmenitiesCheck(mFilter.flagAmenitiesFilters);
        getViewInterface().setRoomAmenitiesCheck(mFilter.flagRoomAmenitiesFilters);
    }

    void onRefresh(int delay)
    {
        clearCompositeDisposable();

        milterCount = null;

        getViewInterface().setConfirmText(getString(R.string.label_searching));

        addCompositeDisposable(Observable.zip(getLocalPlusListCountByFilter(), mStayRemoteImpl.getListCountByFilter(mCategoryType, getQueryMap()//
            , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()), new BiFunction<StayFilterCount, StayFilterCount, StayFilterCount>()
        {
            @Override
            public StayFilterCount apply(StayFilterCount stayBMFilterCount, StayFilterCount stayFilterCount) throws Exception
            {
                stayFilterCount.searchCount += stayBMFilterCount.searchCount;

                return stayFilterCount;
            }
        }).delaySubscription(delay, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayFilterCount>()
        {
            @Override
            public void accept(StayFilterCount stayFilterCount) throws Exception
            {
                milterCount = stayFilterCount;

                if (stayFilterCount.searchCount <= 0)
                {
                    getViewInterface().setConfirmText(getString(R.string.label_hotel_filter_result_empty));
                    getViewInterface().setConfirmEnabled(false);

                    mAnalytics.onEmptyResult(getActivity(), mFilter);

                } else if (stayFilterCount.searchCount < stayFilterCount.searchCountOfMax)
                {
                    getViewInterface().setConfirmText(getString(R.string.label_hotel_filter_result_count, stayFilterCount.searchCount));
                    getViewInterface().setConfirmEnabled(true);
                } else
                {
                    getViewInterface().setConfirmText(getString(R.string.label_hotel_filter_result_over_count, stayFilterCount.searchCountOfMax));
                    getViewInterface().setConfirmEnabled(true);
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private Observable<StayFilterCount> getLocalPlusListCountByFilter()
    {
        if (isLocalPlusEnabled() == true)
        {
            Map<String, Object> queryMap = getQueryMap();
            queryMap.put("category", DailyCategoryType.STAY_BOUTIQUE.getCodeString(getActivity()));

            return mStayRemoteImpl.getLocalPlusListCountByFilter(queryMap);
        } else
        {
            return Observable.just(new StayFilterCount());
        }
    }

    private boolean isLocalPlusEnabled()
    {
        if (mCategoryType == DailyCategoryType.STAY_BOUTIQUE && mFilter.sortType == StayFilter.SortType.DEFAULT)
        {
            return DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigBoutiqueBMEnabled();
        } else
        {
            return false;
        }
    }

    Map<String, Object> getQueryMap()
    {
        Map<String, Object> queryMap = new HashMap<>();

        Map<String, Object> bookDateTimeQueryMap = getBookDateTimeQueryMap(mStayBookDateTime);

        if (bookDateTimeQueryMap != null)
        {
            queryMap.putAll(bookDateTimeQueryMap);
        }

        // category [Hotel, Boutique, GuestHouse, Pension, Motel]
        if (mCategoryList != null && mCategoryList.size() > 0)
        {
            List<String> categoryList = new ArrayList<>();

            for (String category : mCategoryList)
            {
                if (Category.ALL.code.equalsIgnoreCase(category) == false)
                {
                    categoryList.add(category);
                }
            }

            if (categoryList.size() > 0)
            {
                queryMap.put("category", categoryList);
            }
        }

        Map<String, Object> suggestQueryMap = getSuggestQueryMap(mSuggest, mRadius);

        if (suggestQueryMap != null)
        {
            queryMap.putAll(suggestQueryMap);
        }

        Map<String, Object> filterQueryMap = getFilterQueryMap(mFilter);

        if (filterQueryMap != null)
        {
            queryMap.putAll(filterQueryMap);
        }

        // details
        queryMap.put("details", false);

        return queryMap;
    }

    private Map<String, Object> getBookDateTimeQueryMap(StayBookDateTime bookDateTime)
    {
        if (bookDateTime == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        queryMap.put("dateCheckIn", bookDateTime.getCheckInDateTime("yyyy-MM-dd"));
        queryMap.put("stays", bookDateTime.getNights());

        return queryMap;
    }

    private Map<String, Object> getSuggestQueryMap(StaySuggest suggest, float radius)
    {
        if (suggest == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        switch (suggest.getSuggestType())
        {
            case STAY:
            {
                StaySuggest.Stay suggestItem = (StaySuggest.Stay) suggest.getSuggestItem();
                queryMap.put("targetIndices", suggestItem.index);
                break;
            }

            case DIRECT:
                queryMap.put("term", suggest.getSuggestItem().name);
                break;

            case LOCATION:
            {
                StaySuggest.Location suggestItem = (StaySuggest.Location) suggest.getSuggestItem();

                queryMap.put("latitude", suggestItem.latitude);
                queryMap.put("longitude", suggestItem.longitude);
                queryMap.put("radius", radius);
                break;
            }

            case STATION:
            {
                StaySuggest.Station suggestItem = (StaySuggest.Station) suggest.getSuggestItem();

                queryMap.put("subwayIdx", suggestItem.index);
                break;
            }

            case AREA_GROUP:
            {
                StaySuggest.AreaGroup areaGroupSuggestItem = (StaySuggest.AreaGroup) suggest.getSuggestItem();

                queryMap.put("provinceIdx", areaGroupSuggestItem.index);

                StaySuggest.Area areaSuggestItem = areaGroupSuggestItem.area;

                if (areaSuggestItem != null && areaSuggestItem.index > 0)
                {
                    queryMap.put("areaIdx", areaSuggestItem.index);
                }
                break;
            }
        }

        return queryMap;
    }

    private Map<String, Object> getFilterQueryMap(StayFilter filter)
    {
        if (filter == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();
        List<String> bedTypeList = filter.getBedTypeList();

        List<String> luxuryFilterList = new ArrayList<>();
        List<String> amenitiesFilterList = filter.getAmenitiesFilter();
        List<String> roomAmenitiesFilterList = filter.getRoomAmenitiesFilterList();

        queryMap.put("persons", filter.person);

        if (bedTypeList != null && bedTypeList.size() > 0)
        {
            queryMap.put("bedType", bedTypeList);
        }

        if (amenitiesFilterList != null && amenitiesFilterList.size() > 0)
        {
            luxuryFilterList.addAll(amenitiesFilterList);
        }

        if (roomAmenitiesFilterList != null && roomAmenitiesFilterList.size() > 0)
        {
            luxuryFilterList.addAll(roomAmenitiesFilterList);
        }

        if (luxuryFilterList.size() > 0)
        {
            queryMap.put("luxury", luxuryFilterList);
        }

        Map<String, Object> sortQueryMap = getSortQueryMap(filter.sortType, mLocation);

        if (sortQueryMap != null)
        {
            queryMap.putAll(sortQueryMap);
        }

        return queryMap;
    }

    private Map<String, Object> getSortQueryMap(StayFilter.SortType sortType, Location location)
    {
        if (sortType == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        switch (sortType)
        {
            case DEFAULT:
                break;

            case DISTANCE:
                queryMap.put("sortProperty", "Distance");
                queryMap.put("sortDirection", "Asc");

                if (location != null)
                {
                    queryMap.put("latitude", location.getLatitude());
                    queryMap.put("longitude", location.getLongitude());
                }
                break;

            case LOW_PRICE:
                queryMap.put("sortProperty", "Price");
                queryMap.put("sortDirection", "Asc");
                break;

            case HIGH_PRICE:
                queryMap.put("sortProperty", "Price");
                queryMap.put("sortDirection", "Desc");
                break;

            case SATISFACTION:
                queryMap.put("sortProperty", "Rating");
                queryMap.put("sortDirection", "Desc");
                break;
        }

        return queryMap;
    }

    private Observable<Location> searchMyLocation()
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            return null;
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
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
                    public void onProviderDisabled()
                    {
                        observer.onError(new ProviderException());
                    }

                    @Override
                    public void onProviderEnabled()
                    {
                        mDailyLocationExFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
                        {
                            @Override
                            public void onFailed()
                            {
                                observer.onError(new Exception());
                            }

                            @Override
                            public void onAlreadyRun()
                            {
                                observer.onError(new DuplicateRunException());
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

                            @Override
                            public void onCheckSetting(ResolvableApiException exception)
                            {
                                observer.onError(exception);
                            }
                        });
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
                    startActivityForResult(intent, StayFilterActivity.REQUEST_CODE_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    getViewInterface().showSimpleDialog(//
                        getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                        getString(R.string.dialog_btn_text_dosetting), //
                        getString(R.string.dialog_btn_text_cancel), //
                        new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, StayFilterActivity.REQUEST_CODE_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                                onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                                getViewInterface().setSortLayout(mFilter.sortType);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                                onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                                getViewInterface().setSortLayout(mFilter.sortType);
                            }
                        }, null, true);
                } else if (throwable instanceof DuplicateRunException)
                {

                } else if (throwable instanceof ResolvableApiException)
                {
                    try
                    {
                        ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), StayFilterActivity.REQUEST_CODE_SETTING_LOCATION);
                    } catch (Exception e)
                    {
                        DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                        onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                        getViewInterface().setSortLayout(mFilter.sortType);
                    }
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                    onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                    getViewInterface().setSortLayout(mFilter.sortType);
                }
            }
        });
    }
}
