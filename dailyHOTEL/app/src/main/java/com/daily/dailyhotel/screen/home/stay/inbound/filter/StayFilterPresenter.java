package com.daily.dailyhotel.screen.home.stay.inbound.filter;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.parcel.StayFitlerParcel;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayFilterPresenter extends BaseExceptionPresenter<StayFilterActivity, StayFilterInterface> implements StayFilterView.OnEventListener
{
    private StayFitlerAnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;

    StayFilter mStayFilter;
    StayRegion mStayRegion;
    StayBookDateTime mStayBookDateTime;
    List<String> mCategoryList;
    Location mLocation;
    double mRadius;
    String mSearchWord;
    Constants.ViewType mViewType;

    public interface StayFitlerAnalyticsInterface extends BaseAnalyticsInterface
    {
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
        mAnalytics = (StayFitlerAnalyticsInterface) analytics;
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

        String viewType = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_VIEW_TYPE);

        if (DailyTextUtils.isTextEmpty(viewType) == true)
        {
            return false;
        }

        mViewType = Constants.ViewType.valueOf(viewType);

        StayFitlerParcel stayFitlerParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_FILTER);

        if (stayFitlerParcel == null)
        {
            return false;
        }

        mStayFilter = stayFitlerParcel.getStayFilter();

        StayRegionParcel stayRegionParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_REGION);

        if (stayRegionParcel != null)
        {
            mStayRegion = stayRegionParcel.getRegion();
        }

        mCategoryList = intent.getStringArrayListExtra(StayFilterActivity.INTENT_EXTRA_DATA_CATEGORIES);

        mLocation = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION);
        mRadius = intent.getDoubleExtra(StayFilterActivity.INTENT_EXTRA_DATA_RADIOUS, 10);
        mSearchWord = intent.getStringExtra(StayFilterActivity.INTENT_EXTRA_DATA_SEARCH_WORD);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.activity_curation_title));

        getViewInterface().setSortLayout(mStayFilter.sortType);

        getViewInterface().setSortLayoutEnabled(mViewType == Constants.ViewType.LIST);

        getViewInterface().setPerson(mStayFilter.person);

        getViewInterface().setAmenitiesCheck(mStayFilter.flagAmenitiesFilters);

        getViewInterface().setAmenitiesCheck(mStayFilter.flagRoomAmenitiesFilters);
    }

    @Override
    public void onStart()
    {
        super.onStart();

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

        getViewInterface().setConfirmText(getString(R.string.label_searching));

        addCompositeDisposable(mStayRemoteImpl.getListCountByFilter(getQueryMap(), DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayFilterCount>()
        {
            @Override
            public void accept(StayFilterCount stayFilterCount) throws Exception
            {
                if (stayFilterCount.searchCount <= 0)
                {
                    getViewInterface().setConfirmText(getString(R.string.label_hotel_filter_result_empty));
                    getViewInterface().setConfirmEnabled(false);
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

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onResetClick()
    {

    }

    @Override
    public void onConfirmClick()
    {

    }

    @Override
    public void onCheckedChangedSort(StayFilter.SortType sortType)
    {

    }

    @Override
    public void onCheckedChangedAmenities(int falg)
    {

    }

    @Override
    public void onCheckedChangedRoomAmenities(int flag)
    {

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
            // provinceIdx
            Area areaGroup = mStayRegion.getAreaGroup();
            if (areaGroup != null)
            {
                queryMap.put("provinceIdx", mStayRegion.getAreaGroup().index);
            }

            Area area = mStayRegion.getArea();
            if (area != null && area.index != StayArea.ALL)
            {
                // areaIdx
                queryMap.put("areaIdx", mStayRegion.getArea().index);
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
        if (mLocation != null && mLocation.getLatitude() != 0 && mLocation.getLongitude() != 0 && mRadius > 0)
        {
            queryMap.put("latitude", mLocation.getLatitude());
            queryMap.put("longitude", mLocation.getLongitude());
            queryMap.put("radius", mRadius);
        }

        // page
        // limit
        // 사용하지 않음

        // details
        queryMap.put("details", false);

        return queryMap;
    }
}
