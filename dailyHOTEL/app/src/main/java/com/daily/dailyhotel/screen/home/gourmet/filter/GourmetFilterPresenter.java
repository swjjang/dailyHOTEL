package com.daily.dailyhotel.screen.home.gourmet.filter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetFilterCount;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetFilterParcel;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabPresenter;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
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
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetFilterPresenter extends BaseExceptionPresenter<GourmetFilterActivity, GourmetFilterInterface.ViewInterface> implements GourmetFilterInterface.OnEventListener
{
    private static final int CLICK_FILTER_DELAY_TIME = 500;

    GourmetFilterInterface.AnalyticsInterface mAnalytics;

    GourmetRemoteImpl mGourmetRemoteImpl;

    GourmetFilter mFilter;
    SearchGourmetResultTabPresenter.ListType mListType;
    GourmetSuggest mSuggest;
    GourmetBookDateTime mBookDateTime;
    Location mLocation;
    float mRadius;
    String mSearchWord;
    Constants.ViewType mViewType;
    GourmetFilterCount mFilterCount;

    DailyLocationExFactory mDailyLocationExFactory;

    public GourmetFilterPresenter(@NonNull GourmetFilterActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetFilterInterface.ViewInterface createInstanceViewInterface()
    {
        return new GourmetFilterView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetFilterActivity activity)
    {
        setContentView(R.layout.activity_gourmet_filter_data);

        mAnalytics = new GourmetFilterAnalyticsImpl();

        mGourmetRemoteImpl = new GourmetRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)


        {
            try
            {
                mListType = SearchGourmetResultTabPresenter.ListType.valueOf(intent.getStringExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_LIST_TYPE));
            } catch (Exception e)
            {
                mListType = SearchGourmetResultTabPresenter.ListType.DEFAULT;
            }
        }

        String visitDateTime = intent.getStringExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);

        try
        {
            mBookDateTime = new GourmetBookDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        }

        String viewType = intent.getStringExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_VIEW_TYPE);

        if (DailyTextUtils.isTextEmpty(viewType) == true)
        {
            return false;
        }

        mViewType = Constants.ViewType.valueOf(viewType);

        GourmetFilterParcel filterParcel = intent.getParcelableExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_FILTER);

        if (filterParcel == null)
        {
            return false;
        }

        mFilter = filterParcel.getFilter();

        GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel == null)
        {
            return false;
        }

        mSuggest = suggestParcel.getSuggest();

        mLocation = intent.getParcelableExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_LOCATION);
        mRadius = intent.getFloatExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_RADIUS, 0);
        mSearchWord = intent.getStringExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_SEARCH_WORD);

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

        if (mSuggest.isLocationSuggestType())
        {
            getViewInterface().defaultSortLayoutGone();
        }

        getViewInterface().setCategory(mFilter.getCategoryMap());

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
            case GourmetFilterActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onCheckedChangedSort(GourmetFilter.SortType.DISTANCE);
                        break;

                    default:
                        mFilter.sortType = GourmetFilter.SortType.DEFAULT;

                        notifyFilterChanged();
                        break;
                }
                break;
            }

            case GourmetFilterActivity.REQUEST_CODE_SETTING_LOCATION:
                onCheckedChangedSort(GourmetFilter.SortType.DISTANCE);
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
        if (mFilterCount == null || lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_FILTER, new GourmetFilterParcel(mFilter));

        if (mFilter.sortType == GourmetFilter.SortType.DISTANCE)
        {
            intent.putExtra(GourmetFilterActivity.INTENT_EXTRA_DATA_LOCATION, mLocation);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();

        try
        {
            mAnalytics.onConfirmClick(getActivity(), mSuggest, mFilter, mFilterCount == null ? 0 : mFilterCount.searchCount);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCheckedChangedSort(GourmetFilter.SortType sortType)
    {
        if (sortType == null)
        {
            return;
        }

        if (sortType == GourmetFilter.SortType.DISTANCE)
        {
            if (lock() == true)
            {
                return;
            }

            screenLock(true);

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
    public void onCheckedChangedCategories(GourmetFilter.Category category)
    {
        if (mFilter.hasCategory(category) == true)
        {
            mFilter.removeCategory(category);
        } else
        {
            mFilter.addCategory(category);
        }

        getViewInterface().setCategoriesCheck(category);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedTimes(int flag)
    {
        mFilter.flagTimeFilter ^= flag;

        getViewInterface().setTimesCheck(mFilter.flagTimeFilter);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    @Override
    public void onCheckedChangedAmenities(int flag)
    {
        mFilter.flagAmenitiesFilters ^= flag;

        getViewInterface().setAmenitiesCheck(mFilter.flagAmenitiesFilters);

        onRefresh(CLICK_FILTER_DELAY_TIME);
    }

    void notifyFilterChanged()
    {
        if (mFilter == null)
        {
            return;
        }

        getViewInterface().setSortCheck(mFilter.sortType);
        getViewInterface().setSortLayoutEnabled(mViewType == Constants.ViewType.LIST);
        getViewInterface().setCategoriesCheck(mFilter.getCategoryFilterMap());
        getViewInterface().setTimesCheck(mFilter.flagTimeFilter);
        getViewInterface().setAmenitiesCheck(mFilter.flagAmenitiesFilters);
    }

    void onRefresh(int delay)
    {
        clearCompositeDisposable();

        mFilterCount = null;

        getViewInterface().setConfirmText(getString(R.string.label_searching));

        addCompositeDisposable(mGourmetRemoteImpl.getListCountByFilter(getActivity(), getQueryMap()).delaySubscription(delay, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetFilterCount>()
        {
            @Override
            public void accept(GourmetFilterCount filterCount) throws Exception
            {
                mFilterCount = filterCount;

                if (filterCount.searchCount <= 0)
                {
                    getViewInterface().setConfirmText(getString(R.string.label_gourmet_filter_result_empty));
                    getViewInterface().setConfirmEnabled(false);

                    mAnalytics.onEmptyResult(getActivity(), mFilter);

                } else
                {
                    getViewInterface().setConfirmText(getString(R.string.label_gourmet_filter_result_count, filterCount.searchCount));
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

    Map<String, Object> getQueryMap()
    {
        Map<String, Object> queryMap = new HashMap<>();

        if (mListType != null)
        {
            switch (mListType)
            {
                case SEARCH:
                    queryMap.put("saleSearchType", "SHOW_SOLD_OUT");
                    break;

                default:
                    break;
            }
        }

        Map<String, Object> bookDateTimeQueryMap = getBookDateTimeQueryMap(mBookDateTime);

        if (bookDateTimeQueryMap != null)
        {
            queryMap.putAll(bookDateTimeQueryMap);
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

    private Map<String, Object> getBookDateTimeQueryMap(GourmetBookDateTime bookDateTime)
    {
        if (bookDateTime == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        queryMap.put("reserveDate", mBookDateTime.getVisitDateTime("yyyy-MM-dd"));

        return queryMap;
    }

    private Map<String, Object> getSuggestQueryMap(GourmetSuggest suggest, float radius)
    {
        if (suggest == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        switch (suggest.getSuggestType())
        {
            case AREA_GROUP:
            {
                GourmetSuggest.AreaGroup suggestItem = (GourmetSuggest.AreaGroup) suggest.getSuggestItem();

                queryMap.put("provinceIdx", suggestItem.index);

                if (suggestItem.area != null && suggestItem.area.index > 0)
                {
                    queryMap.put("areaIdx", suggestItem.area.index);
                }
                break;
            }

            case LOCATION:
            {
                GourmetSuggest.Location suggestItem = (GourmetSuggest.Location) suggest.getSuggestItem();

                queryMap.put("latitude", suggestItem.latitude);
                queryMap.put("longitude", suggestItem.longitude);

                if (mRadius > 0)
                {
                    queryMap.put("radius", mRadius);
                }
                break;
            }

            case STATION:
            {
                break;
            }

            case DIRECT:
            {
                GourmetSuggest.Direct suggestItem = (GourmetSuggest.Direct) suggest.getSuggestItem();

                queryMap.put("term", suggestItem.name);
                break;
            }

            case GOURMET:
            {
                GourmetSuggest.Gourmet suggestItem = (GourmetSuggest.Gourmet) suggest.getSuggestItem();

                queryMap.put("targetIndices", suggestItem.index);
                break;
            }
        }

        return queryMap;
    }

    private Map<String, Object> getFilterQueryMap(GourmetFilter filter)
    {
        if (filter == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();
        List<String> categoryList = new ArrayList(filter.getCategoryFilterMap().values());
        List<String> timesFilterList = filter.getTimeFilter();
        List<String> amenitiesFilterList = filter.getAmenitiesFilter();

        if (categoryList != null && categoryList.size() > 0)
        {
            queryMap.put("category", categoryList);
        }

        if (timesFilterList != null && timesFilterList.size() > 0)
        {
            queryMap.put("timeFrame", timesFilterList);
        }

        if (amenitiesFilterList != null && amenitiesFilterList.size() > 0)
        {
            queryMap.put("luxury", amenitiesFilterList);
        }

        Map<String, Object> sortQueryMap = getSortQueryMap(filter.sortType, mLocation);

        if (sortQueryMap != null)
        {
            queryMap.putAll(sortQueryMap);
        }

        return queryMap;
    }

    private Map<String, Object> getSortQueryMap(GourmetFilter.SortType sortType, Location location)
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
                queryMap.put("sortProperty", "PricePerPerson");
                queryMap.put("sortDirection", "Asc");
                break;

            case HIGH_PRICE:
                queryMap.put("sortProperty", "PricePerPerson");
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
                    startActivityForResult(intent, GourmetFilterActivity.REQUEST_CODE_PERMISSION_MANAGER);
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
                                startActivityForResult(intent, GourmetFilterActivity.REQUEST_CODE_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                                onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                                getViewInterface().setSortCheck(mFilter.sortType);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                                onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                                getViewInterface().setSortCheck(mFilter.sortType);
                            }
                        }, null, true);
                } else if (throwable instanceof DuplicateRunException)
                {

                } else if (throwable instanceof ResolvableApiException)
                {
                    try
                    {
                        ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), GourmetFilterActivity.REQUEST_CODE_SETTING_LOCATION);
                    } catch (Exception e)
                    {
                        DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                        onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                        getViewInterface().setSortCheck(mFilter.sortType);
                    }
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);

                    onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                    getViewInterface().setSortCheck(mFilter.sortType);
                }
            }
        });
    }
}
