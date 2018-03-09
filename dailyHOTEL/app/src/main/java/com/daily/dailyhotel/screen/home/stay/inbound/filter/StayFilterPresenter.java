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
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StayRegionParcel;
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

    StayFilter mStayFilter;
    StayRegion mStayRegion;
    StayBookDateTime mStayBookDateTime;
    List<String> mCategoryList;
    Location mLocation;
    double mRadius;
    String mSearchWord;
    Constants.ViewType mViewType;
    StayFilterCount mStayFilterCount;
    DailyCategoryType mCategoryType = DailyCategoryType.STAY_ALL;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface StayFilterAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onConfirmClick(Activity activity, StayRegion stayRegion, StayFilter stayFilter, int listCountByFilter);

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

        StayFilterParcel stayFilterParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_FILTER);

        if (stayFilterParcel == null)
        {
            return false;
        }

        mStayFilter = stayFilterParcel.getStayFilter();

        StayRegionParcel stayRegionParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_REGION);

        if (stayRegionParcel != null)
        {
            mStayRegion = stayRegionParcel.getRegion();
        }

        mCategoryList = intent.getStringArrayListExtra(StayFilterActivity.INTENT_EXTRA_DATA_CATEGORIES);

        mLocation = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION);
        mRadius = intent.getDoubleExtra(StayFilterActivity.INTENT_EXTRA_DATA_RADIOUS, 0);
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
                        mStayFilter.sortType = StayFilter.SortType.DEFAULT;

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
        if (--mStayFilter.person < StayFilter.PERSON_COUNT_OF_MIN)
        {
            mStayFilter.person = StayFilter.PERSON_COUNT_OF_MIN;
        }

        getViewInterface().setPerson(mStayFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onPlusPersonClick()
    {
        if (++mStayFilter.person > StayFilter.PERSON_COUNT_OF_MAX)
        {
            mStayFilter.person = StayFilter.PERSON_COUNT_OF_MAX;
        }

        getViewInterface().setPerson(mStayFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onResetClick()
    {
        if (lock() == true)
        {
            return;
        }

        mStayFilter.resetFilter();

        notifyFilterChanged();

        onRefresh(CLICK_FILTER_DELAY_TIME);

        mAnalytics.onResetClick(getActivity());
    }

    @Override
    public void onConfirmClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_FILTER, new StayFilterParcel(mStayFilter));

        if (mStayFilter.sortType == StayFilter.SortType.DISTANCE)
        {
            intent.putExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION, mLocation);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();

        try
        {
            mAnalytics.onConfirmClick(getActivity(), mStayRegion, mStayFilter, mStayFilterCount == null ? 0 : mStayFilterCount.searchCount);
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
                    mStayFilter.sortType = sortType;

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
            mStayFilter.sortType = sortType;
        }
    }

    @Override
    public void onCheckedChangedBedType(int flag)
    {
        mStayFilter.flagBedTypeFilters ^= flag;

        getViewInterface().setBedTypeCheck(mStayFilter.flagBedTypeFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedAmenities(int flag)
    {
        mStayFilter.flagAmenitiesFilters ^= flag;

        getViewInterface().setAmenitiesCheck(mStayFilter.flagAmenitiesFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedRoomAmenities(int flag)
    {
        mStayFilter.flagRoomAmenitiesFilters ^= flag;

        getViewInterface().setRoomAmenitiesCheck(mStayFilter.flagRoomAmenitiesFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    void notifyFilterChanged()
    {
        if (mStayFilter == null)
        {
            return;
        }

        getViewInterface().setSortLayout(mStayFilter.sortType);
        getViewInterface().setSortLayoutEnabled(mViewType == Constants.ViewType.LIST);
        getViewInterface().setPerson(mStayFilter.person, StayFilter.PERSON_COUNT_OF_MAX, StayFilter.PERSON_COUNT_OF_MIN);
        getViewInterface().setBedTypeCheck(mStayFilter.flagBedTypeFilters);
        getViewInterface().setAmenitiesCheck(mStayFilter.flagAmenitiesFilters);
        getViewInterface().setRoomAmenitiesCheck(mStayFilter.flagRoomAmenitiesFilters);
    }

    void onRefresh(int delay)
    {
        clearCompositeDisposable();

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
                mStayFilterCount = stayFilterCount;

                if (stayFilterCount.searchCount <= 0)
                {
                    getViewInterface().setConfirmText(getString(R.string.label_hotel_filter_result_empty));
                    getViewInterface().setConfirmEnabled(false);

                    mAnalytics.onEmptyResult(getActivity(), mStayFilter);

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

            return mStayRemoteImpl.getLocalPlusListCountByFilte(queryMap);
        } else
        {
            return Observable.just(new StayFilterCount());
        }
    }

    private boolean isLocalPlusEnabled()
    {
        if (mCategoryType == DailyCategoryType.STAY_BOUTIQUE && mStayFilter.sortType == StayFilter.SortType.DEFAULT)
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

        // dateCheckIn
        queryMap.put("dateCheckIn", mStayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));

        // stays
        queryMap.put("stays", mStayBookDateTime.getNights());

        if (mStayRegion != null)
        {
            switch (mStayRegion.getAreaType())
            {
                case AREA:
                {
                    // provinceIdx
                    Area areaGroup = mStayRegion.getAreaGroup();
                    if (areaGroup != null)
                    {
                        queryMap.put("provinceIdx", areaGroup.index);
                    }

                    Area area = mStayRegion.getArea();
                    if (area != null && area.index != StayArea.ALL)
                    {
                        // areaIdx
                        queryMap.put("areaIdx", area.index);
                    }
                    break;
                }

                case SUBWAY_AREA:
                {
                    Area area = mStayRegion.getArea();
                    if (area != null)
                    {
                        queryMap.put("subwayIdx", area.index);
                    }
                    break;
                }
            }
        }

        // persons
        queryMap.put("persons", mStayFilter.person);

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

        // term
        if (DailyTextUtils.isTextEmpty(mSearchWord) == false)
        {
            queryMap.put("term", mSearchWord);
        }

        // bedType [Double, Twin, Ondol, Etc]
        List<String> flagBedTypeFilters = mStayFilter.getBedTypeList();

        if (flagBedTypeFilters != null && flagBedTypeFilters.size() > 0)
        {
            queryMap.put("bedType", flagBedTypeFilters);
        }

        // luxury [Breakfast, Cooking, Bath, Parking, Pool, Finess, WiFi, NoParking, Pet, ShareBbq, KidsPlayRoom
        // , Sauna, BusinessCenter, Tv, Pc, SpaWallPool, Karaoke, PartyRoom, PrivateBbq
        List<String> luxuryFilterList = new ArrayList<>();
        List<String> amenitiesFilterList = mStayFilter.getAmenitiesFilter();
        List<String> roomAmenitiesFilterList = mStayFilter.getRoomAmenitiesFilterList();

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

        // sortProperty
        // sortDirection
        switch (mStayFilter.sortType)
        {
            case DEFAULT:
                break;

            case DISTANCE:
                queryMap.put("sortProperty", "Distance");
                queryMap.put("sortDirection", "Asc");
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

        // longitude
        // latitude
        // radius
        if (mStayFilter.sortType == StayFilter.SortType.DISTANCE && mLocation != null)
        {
            queryMap.put("latitude", mLocation.getLatitude());
            queryMap.put("longitude", mLocation.getLongitude());

            if (mRadius > 0)
            {
                queryMap.put("radius", mRadius);
            }
        }

        // page
        // limit
        // 사용하지 않음

        // details
        queryMap.put("details", false);

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
                                getViewInterface().setSortLayout(mStayFilter.sortType);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                                onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                                getViewInterface().setSortLayout(mStayFilter.sortType);
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
                        getViewInterface().setSortLayout(mStayFilter.sortType);
                    }
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                    onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                    getViewInterface().setSortLayout(mStayFilter.sortType);
                }
            }
        });
    }
}
