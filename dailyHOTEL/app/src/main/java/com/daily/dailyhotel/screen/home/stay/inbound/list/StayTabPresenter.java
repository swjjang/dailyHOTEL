package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.area.stay.StayAreaListActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.calendar.StayCalendarActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTabPresenter extends BaseExceptionPresenter<StayTabActivity, StayTabInterface> implements StayTabView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 60;
    private static final int NIGHTS_OF_MAXCOUNT = 60;

    private StayTabAnalyticsInterface mAnalytics;

    CommonRemoteImpl mCommonRemoteImpl;
    StayRemoteImpl mStayRemoteImpl;

    enum ViewType
    {
        LIST,
        MAP,
    }

    StayViewModel mStayViewModel;
    DailyDeepLink mDailyDeepLink;
    boolean mHasDeepLink;

    class StayViewModel extends ViewModel
    {
        MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
        MutableLiveData<StayBookDateTime> stayBookDateTime = new MutableLiveData<>();
        MutableLiveData<StayFilter> stayFilter = new MutableLiveData<>();
        MutableLiveData<StayRegion> stayRegion = new MutableLiveData<>();
        MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
        MutableLiveData<Location> location = new MutableLiveData<>();
        MutableLiveData<ViewType> viewType = new MutableLiveData<>();

        //        // 화면 갱신
        //        MutableLiveData<Boolean> notifyRefreshFragment = new MutableLiveData<>();
    }

    class StayViewModelFactory implements ViewModelProvider.Factory
    {
        private Context mContext;

        public StayViewModelFactory(Context context)
        {
            mContext = context;
        }

        @NonNull
        @Override
        public StayViewModel create(@NonNull Class modelClass)
        {
            StayViewModel stayViewModel = new StayViewModel();

            stayViewModel.stayFilter.setValue(new StayFilter());
            stayViewModel.viewType.setValue(ViewType.LIST);

            if (mContext == null)
            {
                stayViewModel.selectedCategory.setValue(Category.ALL);
            } else
            {
                String oldCategoryCode = DailyPreference.getInstance(mContext).getStayCategoryCode();
                String oldCategoryName = DailyPreference.getInstance(mContext).getStayCategoryName();

                if (DailyTextUtils.isTextEmpty(oldCategoryCode, oldCategoryName) == false)
                {
                    stayViewModel.selectedCategory.setValue(new Category(oldCategoryCode, oldCategoryName));
                } else
                {
                    stayViewModel.selectedCategory.setValue(Category.ALL);
                }
            }

            //            stayViewModel.notifyRefreshFragment.setValue(false);

            return stayViewModel;
        }
    }

    public interface StayTabAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayTabPresenter(@NonNull StayTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayTabInterface createInstanceViewInterface()
    {
        return new StayTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayTabActivity activity)
    {
        setContentView(R.layout.activity_stay_tab_data);

        setAnalytics(new StayTabAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayTabAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_daily_hotel));


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

        switch (requestCode)
        {
            case StayTabActivity.REQUEST_CODE_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;

            case StayTabActivity.REQUEST_CODE_REGION:
                onRegionActivityResult(resultCode, data);
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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<CommonDateTime, ObservableSource<List<StayAreaGroup>>>()
        {
            @Override
            public ObservableSource<List<StayAreaGroup>> apply(CommonDateTime commonDateTime) throws Exception
            {
                setCommonDateTime(commonDateTime);

                try
                {
                    // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                    if (mStayViewModel.stayBookDateTime.getValue() == null || mStayViewModel.stayBookDateTime.getValue().validate() == false)
                    {
                        setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                    } else
                    {
                        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                        if (DailyCalendar.compareDateDay(commonDateTime.dailyDateTime, mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)) > 0)
                        {
                            setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                        }
                    }

                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                        && processDeepLinkByDateTime(commonDateTime, mDailyDeepLink) == true)
                    {
                        return Observable.just(new ArrayList<>());
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return mStayRemoteImpl.getRegionList(DailyCategoryType.STAY_ALL);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayAreaGroup>>()
        {
            @Override
            public void accept(List<StayAreaGroup> areaGroupList) throws Exception
            {
                if (mHasDeepLink == true)
                {

                } else
                {
                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true//
                        && processDeepLinkByRegionList(areaGroupList, mStayViewModel.commonDateTime.getValue(), mDailyDeepLink) == true)
                    {

                    } else
                    {
                        notifyDateTextChanged();

                        if (mStayViewModel.stayRegion.getValue() == null)
                        {
                            mStayViewModel.stayRegion.setValue(searchRegion(areaGroupList, getDistrictNTownNameByCategory(DailyCategoryType.STAY_ALL)));
                        }

                        if (mStayViewModel.stayRegion.getValue() == null)
                        {
                            mStayViewModel.stayRegion.setValue(new StayRegion(areaGroupList.get(0), new StayArea(areaGroupList.get(0))));
                        }

                        notifyRegionTextChanged();
                        notifyCategoryChanged();

                        getViewInterface().notifyRefreshFragments(false);
                    }
                }
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
    public void onCategoryTabSelected(TabLayout.Tab tab)
    {
        if (tab == null)
        {
            return;
        }

        Category category = (Category) tab.getTag();

        mStayViewModel.selectedCategory.setValue(category);

        getViewInterface().setCategoryTab(tab.getPosition());

        getViewInterface().notifyRefreshFragments(false);
    }

    @Override
    public void onCategoryTabReselected(TabLayout.Tab tab)
    {

    }

    @Override
    public void onRegionClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            String checkInDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

            startActivityForResult(StayAreaListActivity.newInstance(getActivity()//
                , checkInDateTime, checkOutDateTime, DailyCategoryType.STAY_ALL//
                , mStayViewModel.selectedCategory.getValue().code), StayTabActivity.REQUEST_CODE_REGION);

            //            switch (mViewType)
            //            {
            //                case LIST:
            //                    AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_LIST, null);
            //                    break;
            //
            //                case MAP:
            //                    AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_MAP, null);
            //                    break;
            //            }
        } catch (Exception e)
        {
            Crashlytics.logException(e);

            setRefresh(true);
            onRefresh(true);
        }
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        startCalendar(AnalyticsManager.ValueType.LIST);
        //
        //        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    @Override
    public void onFilterClick()
    {

    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayViewModel = ViewModelProviders.of(activity, new StayViewModelFactory(getActivity())).get(StayViewModel.class);

        mStayViewModel.selectedCategory.observe(activity, new Observer<Category>()
        {
            @Override
            public void onChanged(@Nullable Category category)
            {
                DailyPreference.getInstance(getActivity()).setStayCategory(category.name, category.code);
            }
        });

        mStayViewModel.stayRegion.observe(activity, new Observer<StayRegion>()
        {
            @Override
            public void onChanged(@Nullable StayRegion stayRegion)
            {

            }
        });
    }

    private void startCalendar(String callByScreen)
    {
        try
        {
            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(DailyCalendar.convertDate(mStayViewModel.commonDateTime.getValue().dailyDateTime, DailyCalendar.ISO_8601_FORMAT));

            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT - 1);

            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, callByScreen, true//
                , 0, true);

            startActivityForResult(intent, StayTabActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mStayViewModel.commonDateTime.setValue(commonDateTime);
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayViewModel.stayBookDateTime.getValue() == null)
        {
            mStayViewModel.stayBookDateTime.setValue(new StayBookDateTime());
        }

        try
        {
            mStayViewModel.stayBookDateTime.getValue().setCheckInDateTime(checkInDateTime);
            mStayViewModel.stayBookDateTime.getValue().setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setStayBookDateTime(String checkInDateTime, int afterDay)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime) == true || afterDay == 0)
        {
            return;
        }

        if (mStayViewModel.stayBookDateTime.getValue() == null)
        {
            mStayViewModel.stayBookDateTime.setValue(new StayBookDateTime());
        }

        try
        {
            mStayViewModel.stayBookDateTime.getValue().setCheckInDateTime(checkInDateTime);
            mStayViewModel.stayBookDateTime.getValue().setCheckOutDateTime(checkInDateTime, afterDay);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyDateTextChanged()
    {
        if (mStayViewModel == null || mStayViewModel.stayBookDateTime.getValue() == null)
        {
            return;
        }

        String checkInDay = mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime("M.d(EEE)");
        String checkOutDay = mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime("M.d(EEE)");

        getViewInterface().setToolbarDateText(String.format(Locale.KOREA, "%s - %s", checkInDay, checkOutDay));
    }

    void notifyRegionTextChanged()
    {
        if (mStayViewModel == null || mStayViewModel.stayRegion.getValue() == null)
        {
            return;
        }

        getViewInterface().setToolbarRegionText(mStayViewModel.stayRegion.getValue().getAreaName());
    }

    void notifyCategoryChanged()
    {
        if (mStayViewModel == null || mStayViewModel.stayRegion.getValue() == null || mStayViewModel.selectedCategory.getValue() == null)
        {
            return;
        }

        getViewInterface().setCategoryTabLayout(getSupportFragmentManager(), mStayViewModel.stayRegion.getValue().getArea().getCategoryList(), mStayViewModel.selectedCategory.getValue());
    }

    StayRegion searchRegion(List<StayAreaGroup> areaGroupList, Pair<String, String> namePair)
    {
        if (areaGroupList == null || namePair == null)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.name.equalsIgnoreCase(namePair.first) == true)
            {
                if (areaGroup.getAreaCount() > 0)
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.name.equalsIgnoreCase(namePair.second) == true)
                        {
                            return new StayRegion(areaGroup, area);
                        }
                    }
                } else
                {
                    return new StayRegion(areaGroup, new StayArea(areaGroup));
                }
            }
        }

        return null;
    }

    StayRegion searchRegion(List<StayAreaGroup> areaGroupList, int areaGroupIndex, int areaIndex)
    {
        if (areaGroupList == null || areaGroupIndex < 0)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.index == areaGroupIndex)
            {
                if (areaIndex >= 0 && areaGroup.getAreaCount() > 0)
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.index == areaIndex)
                        {
                            return new StayRegion(areaGroup, area);
                        }
                    }
                } else
                {
                    return new StayRegion(areaGroup, new StayArea(areaGroup));
                }
            }
        }

        return null;
    }

    Pair<String, String> getDistrictNTownNameByCategory(DailyCategoryType dailyCategoryType)
    {
        if (dailyCategoryType == null)
        {
            return null;
        }

        JSONObject jsonObject = DailyPreference.getInstance(getActivity()).getDailyRegion(dailyCategoryType);

        if (jsonObject == null)
        {
            return null;
        }

        try
        {
            return new Pair<>(jsonObject.getString(Constants.JSON_KEY_PROVINCE_NAME), jsonObject.getString(Constants.JSON_KEY_AREA_NAME));

        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private void onCalendarActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:

                if (data != null && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                    && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                {
                    String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                    String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                    if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                    {
                        return;
                    }

                    setStayBookDateTime(checkInDateTime, checkOutDateTime);
                    notifyDateTextChanged();

                    setRefresh(true);
                }
                break;
        }
    }

    private void onRegionActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
            case com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR:
                if (data != null && data.hasExtra(StayAreaListActivity.INTENT_EXTRA_DATA_REGION) == true)
                {
                    StayRegionParcel stayRegionParcel = data.getParcelableExtra(StayAreaListActivity.INTENT_EXTRA_DATA_REGION);

                    if (stayRegionParcel == null)
                    {
                        return;
                    }

                    StayRegion region = stayRegionParcel.getRegion();

                    if (region == null || region.getAreaGroup() == null || region.getArea() == null)
                    {
                        return;
                    }

                    mStayViewModel.stayRegion.setValue(region);

                    // 지역이 수정 되면 필터가 초기화 된다.
                    mStayViewModel.stayFilter.getValue().resetFilter();
                    mStayViewModel.selectedCategory.setValue(Category.ALL);

                    setRefresh(true);
                }

                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR)
                {
                    lock();
                    startCalendar(AnalyticsManager.Label.CHANGE_LOCATION);
                }
                break;


            case com.daily.base.BaseActivity.RESULT_CODE_START_AROUND_SEARCH:
            {
                // 검색 결과 화면으로 이동한다.
                //                        startAroundSearchResult(this, mTodayDateTime, mStayCuration.getStayBookingDay()//
                //                            , null, AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
                break;
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean processDeepLinkByDateTime(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelSearchView() == true //
                || externalDeepLink.isCampaignTagListView() == true)
            {
                //                unLockAll();

                return moveDeepLinkSearch(commonDateTime, dailyDeepLink);
            } else if (externalDeepLink.isHotelSearchResultView() == true)
            {
                //                unLockAll();

                return moveDeepLinkSearchResult(commonDateTime, dailyDeepLink);
            } else
            {
                // 더이상 진입은 없다.
                if (externalDeepLink.isHotelListView() == false)
                {
                    externalDeepLink.clear();
                }
            }
        } else
        {

        }

        return false;
    }

    boolean processDeepLinkByRegionList(List<StayAreaGroup> stayDistrictList, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelListView() == true)
            {
                //                unLockAll();

                return moveDeepLinkStayList(stayDistrictList, commonDateTime, externalDeepLink);
            } else
            {
                externalDeepLink.clear();
            }
        } else
        {

        }

        return false;
    }

    boolean moveDeepLinkSearch(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (commonDateTime == null || dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();

                int nights = 1;

                try
                {
                    nights = Integer.parseInt(externalDeepLink.getNights());
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                } finally
                {
                    if (nights <= 0)
                    {
                        nights = 1;
                    }
                }

                StayBookDateTime stayBookDateTime = new StayBookDateTime();

                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookDateTime.setCheckInDateTime(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
                }

                stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

                mStayViewModel.stayBookDateTime.setValue(stayBookDateTime);

                if (externalDeepLink.isCampaignTagListView() == true)
                {
                    int index;
                    try
                    {
                        index = Integer.parseInt(externalDeepLink.getIndex());
                    } catch (Exception e)
                    {
                        index = -1;
                    }

                    if (index != -1)
                    {
                        startActivityForResult(SearchActivity.newInstance(getActivity(), Constants.PlaceType.HOTEL//
                            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), index)//
                            , StayTabActivity.REQUEST_CODE_SEARCH);
                    }
                } else
                {
                    String word = externalDeepLink.getSearchWord();
                    startActivityForResult(SearchActivity.newInstance(getActivity(), Constants.PlaceType.HOTEL//
                        , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), word)//
                        , StayTabActivity.REQUEST_CODE_SEARCH);
                }

                mHasDeepLink = true;
            } else
            {

            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkSearchResult(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                String word = externalDeepLink.getSearchWord();
                DailyExternalDeepLink.SearchType searchType = externalDeepLink.getSearchLocationType();
                LatLng latLng = externalDeepLink.getLatLng();
                double radius = externalDeepLink.getRadius();

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                int nights = 1;

                try
                {
                    nights = Integer.parseInt(externalDeepLink.getNights());
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                } finally
                {
                    if (nights <= 0)
                    {
                        nights = 1;
                    }
                }

                try
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();

                    if (DailyTextUtils.isTextEmpty(date) == false)
                    {
                        Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                        stayBookDateTime.setCheckInDateTime(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                    } else if (datePlus >= 0)
                    {
                        stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime, datePlus);
                    } else
                    {
                        stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
                    }

                    stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

                    mStayViewModel.stayBookDateTime.setValue(stayBookDateTime);

                    switch (searchType)
                    {
                        case LOCATION:
                        {
                            if (latLng != null)
                            {
                                startActivityForResult(StaySearchResultActivity.newInstance(getActivity()//
                                    , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                    , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                    , latLng, radius, true), StayTabActivity.REQUEST_CODE_SEARCH_RESULT);
                            } else
                            {
                                return false;
                            }
                            break;
                        }

                        default:
                            if (DailyTextUtils.isTextEmpty(word) == false)
                            {
                                startActivityForResult(StaySearchResultActivity.newInstance(getActivity()//
                                    , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                    , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                    , null, new Keyword(0, word), Constants.SearchType.SEARCHES), StayTabActivity.REQUEST_CODE_SEARCH_RESULT);
                            } else
                            {
                                return false;
                            }
                            break;
                    }

                    mHasDeepLink = true;
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    return false;
                }
            } else
            {

            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkStayList(List<StayAreaGroup> stayDistrictList, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                String categoryCode = externalDeepLink.getCategoryCode();
                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();

                if (mStayViewModel.stayFilter.getValue() == null)
                {
                    mStayViewModel.stayFilter.setValue(new StayFilter());
                    mStayViewModel.stayFilter.getValue().resetFilter();
                }

                mStayViewModel.stayFilter.getValue().sortType = StayFilter.SortType.valueOf(externalDeepLink.getSorting().name());
                getViewInterface().setOptionFilterSelected(mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

                int nights = 1;
                int provinceIndex;
                int areaIndex;

                try
                {
                    nights = Integer.parseInt(externalDeepLink.getNights());
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                } finally
                {
                    if (nights <= 0)
                    {
                        nights = 1;
                    }
                }

                try
                {
                    provinceIndex = Integer.parseInt(externalDeepLink.getProvinceIndex());
                } catch (Exception e)
                {
                    provinceIndex = -1;
                }

                try
                {
                    areaIndex = Integer.parseInt(externalDeepLink.getAreaIndex());
                } catch (Exception e)
                {
                    areaIndex = -1;
                }

                boolean isOverseas = externalDeepLink.getIsOverseas();

                // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
                StayRegion region = searchRegion(stayDistrictList, provinceIndex, areaIndex);

                if (region == null)
                {
                    return false;
                }

                mStayViewModel.stayRegion.setValue(region);
                notifyRegionTextChanged();

                // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
                if (DailyTextUtils.isTextEmpty(categoryCode) == false && region.getArea() != null && region.getArea().getCategoryCount() > 0)
                {
                    for (Category category : region.getArea().getCategoryList())
                    {
                        if (category.code.equalsIgnoreCase(categoryCode) == true)
                        {
                            mStayViewModel.selectedCategory.setValue(category);
                            break;
                        }
                    }
                }

                StayBookDateTime stayBookDateTime = new StayBookDateTime();

                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookDateTime.setCheckInDateTime(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
                }

                stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), nights);

                setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
                notifyDateTextChanged();
                notifyRegionTextChanged();
                notifyCategoryChanged();
            } else
            {

            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }
}
