package com.twoheart.dailyhotel.screen.hotel.list;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.StayDistrict;
import com.daily.dailyhotel.entity.StayTown;
import com.daily.dailyhotel.parcel.StayTownParcel;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.district.stay.StayDistrictListActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainActivity;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class StayMainActivity extends PlaceMainActivity
{
    StayCuration mStayCuration;
    DailyDeepLink mDailyDeepLink;

    StayRemoteImpl mStayRemoteImpl;

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayMainActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mStayCuration = new StayCuration();

        mStayRemoteImpl = new StayRemoteImpl(this);

        Intent intent = getIntent();

        initDeepLink(intent);

        String oldCategoryCode = DailyPreference.getInstance(this).getStayCategoryCode();
        String oldCategoryName = DailyPreference.getInstance(this).getStayCategoryName();

        if (DailyTextUtils.isTextEmpty(oldCategoryCode, oldCategoryName) == false)
        {
            mStayCuration.setCategory(this, new Category(oldCategoryName, oldCategoryCode));
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);
    }

    private void initDeepLink(Intent intent)
    {
        if (intent == null || intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == false)
        {
            return;
        }

        try
        {
            mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));

            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isHotelDetailView() == true && moveDeepLinkDetail(externalDeepLink) == true)
                {
                    unLockUI();
                }
            }
        } catch (Exception e)
        {
            mDailyDeepLink = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
            AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
    }

    @Override
    protected PlaceMainLayout getPlaceMainLayout(Context context)
    {
        return new StayMainLayout(this, mOnEventListener);
    }

    @Override
    protected PlaceMainNetworkController getPlaceMainNetworkController(Context context)
    {
        return new StayMainNetworkController(this, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void onRegionActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
            case com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR:
                if (data != null && data.hasExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_STAY_TOWN) == true)
                {
                    StayTownParcel stayTownParcel = data.getParcelableExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_STAY_TOWN);

                    if (stayTownParcel == null)
                    {
                        return;
                    }

                    StayTown stayTown = stayTownParcel.getStayTown();

                    if (stayTown == null || stayTown.getDistrict() == null)
                    {
                        return;
                    }

                    StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
                    stayCurationOption.clear();

                    mStayCuration.setTown(stayTown);
                    mStayCuration.setCategory(this, Category.ALL);

                    mPlaceMainLayout.setToolbarRegionText(stayTown.name);
                    mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

                    boolean changedDistrict = data.getBooleanExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CHANGED_DISTRICT, false);

                    if (changedDistrict == true)
                    {
                        AnalyticsManager.getInstance(this).onRegionChanged(AnalyticsManager.ValueType.DOMESTIC, stayTown.getDistrict().name);
                    }

                    mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), stayTown.getCategoryList(), //
                        mStayCuration.getCategory(), mStayListFragmentListener);
                }

                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR)
                {
                    startCalendar(AnalyticsManager.Label.CHANGE_LOCATION, mTodayDateTime);
                }
                break;


            case com.daily.base.BaseActivity.RESULT_CODE_START_AROUND_SEARCH:
            {
                // 검색 결과 화면으로 이동한다.
                startAroundSearchResult(this, mTodayDateTime, mStayCuration.getStayBookingDay()//
                    , null, AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
                break;
            }
        }
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            StayBookingDay stayBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (stayBookingDay == null)
            {
                return;
            }

            mStayCuration.setStayBookingDay(stayBookingDay);

            ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            try
            {
                PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

                if ((placeCuration instanceof StayCuration) == false)
                {
                    return;
                }

                StayCuration changedStayCuration = (StayCuration) placeCuration;
                StayCurationOption changedStayCurationOption = (StayCurationOption) changedStayCuration.getCurationOption();

                mStayCuration.setCurationOption(changedStayCurationOption);
                mPlaceMainLayout.setOptionFilterSelected(changedStayCurationOption.isDefaultFilter() == false);

                if (changedStayCurationOption.getSortType() == SortType.DISTANCE)
                {
                    mStayCuration.setLocation(changedStayCuration.getLocation());

                    if (mStayCuration.getLocation() != null)
                    {
                        lockUI();

                        onLocationChanged(mStayCuration.getLocation());
                    } else
                    {
                        searchMyLocation();
                    }
                } else
                {
                    refreshCurrentFragment(true);
                }
            } catch (Exception e)
            {
                // 예외 처리 추가 원인 찾기
                Crashlytics.log(data.toString());
                Crashlytics.logException(e);

                setResult(RESULT_CANCELED);
                finish();
            }
        } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
        {
            setResult(resultCode);
            finish();
        }
    }

    @Override
    protected void onLocationFailed()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        if (stayCurationOption.getSortType() == SortType.DISTANCE)
        {
            if (location == null)
            {
                // 이전에 가지고 있던 데이터를 사용한다.
                if (mStayCuration.getLocation() != null)
                {
                    refreshCurrentFragment(true);
                } else
                {
                    DailyToast.showToast(this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);

                    stayCurationOption.setSortType(SortType.DEFAULT);
                    refreshCurrentFragment(true);
                }
            } else
            {
                mStayCuration.setLocation(location);
                refreshCurrentFragment(true);
            }
        }
    }

    void startCalendar(String callByScreen, TodayDateTime todayDateTime)
    {
        if (todayDateTime == null || isFinishing() == true)
        {
            return;
        }

        Intent intent = StayCalendarActivity.newInstance(this, todayDateTime, mStayCuration.getStayBookingDay() //
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT, callByScreen, true, true);

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    private void startAroundSearchResult(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, Location location, String callByScreen)
    {
        if (todayDateTime == null || stayBookingDay == null || isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        Intent intent = StaySearchResultActivity.newInstance(context, todayDateTime, stayBookingDay, location, callByScreen);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mStayCuration;
    }

    void recordAnalyticsStayList(String screen)
    {
        if (AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        StayBookingDay stayBookingDay = mStayCuration.getStayBookingDay();
        Map<String, String> params = new HashMap<>();

        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, mStayCuration.getCategory().code);
            params.put(AnalyticsManager.KeyType.FILTER, mStayCuration.getCurationOption().toAdjustString());

            StayTown stayTown = mStayCuration.getTown();

            if (stayTown == null)
            {
                Util.restartApp(this);
                return;
            }

            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, stayTown.getDistrict().name);
            params.put(AnalyticsManager.KeyType.DISTRICT, stayTown.index == StayTown.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : stayTown.name);

            AnalyticsManager.getInstance(this).recordScreen(this, screen, null, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void changeViewType()
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        if (mPlaceMainLayout.getPlaceListFragment() == null)
        {
            Util.restartApp(StayMainActivity.this);
            return;
        }

        lockUI();

        StayListFragment currentFragment = (StayListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();

        switch (mViewType)
        {
            case LIST:
            {
                // 고메 쪽에서 보여지는 메세지로 Stay의 경우도 동일한 처리가 필요해보여서 추가함
                if (currentFragment.hasSalesPlace() == false)
                {
                    unLockUI();

                    DailyToast.showToast(StayMainActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                    return;
                }

                mViewType = ViewType.MAP;

                AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
                break;
            }

            case MAP:
            {
                mViewType = ViewType.LIST;

                AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_LIST, null);
                break;
            }
        }

        mPlaceMainLayout.setOptionViewTypeView(mViewType);

        // 현재 페이지 선택 상태를 Fragment에게 알려준다.
        for (PlaceListFragment placeListFragment : mPlaceMainLayout.getPlaceListFragment())
        {
            boolean isCurrentFragment = placeListFragment == currentFragment;
            placeListFragment.setVisibility(mViewType, isCurrentFragment);
        }

        refreshCurrentFragment(false);

        unLockUI();
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mStayListFragmentListener == null)
        {
            return;
        }

        mStayListFragmentListener.onStayClick(view, placeViewItem, listCount);
    }

    @Override
    protected void onRegionClick()
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        //        Intent intent = StayRegionListActivity.newInstance(StayMainActivity.this, //
        //            mStayCuration.getProvince(), mStayCuration.getStayBookingDay(), mStayCuration.getCategory().code);

        String checkInDateTime = mStayCuration.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT);
        String checkOutDateTime = mStayCuration.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT);

        startActivityForResult(StayDistrictListActivity.newInstance(StayMainActivity.this//
            , checkInDateTime, checkOutDateTime, DailyCategoryType.STAY_ALL, mStayCuration.getCategory().code), Constants.CODE_REQUEST_ACTIVITY_REGIONLIST);

        switch (mViewType)
        {
            case LIST:
                AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_LIST, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_MAP, null);
                break;
        }
    }

    Pair<String, String> getDistrictNTownNameByCategory(DailyCategoryType dailyCategoryType)
    {
        if (dailyCategoryType == null)
        {
            return null;
        }

        JSONObject jsonObject = DailyPreference.getInstance(this).getDailyRegion(dailyCategoryType);

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

    StayTown searchTown(List<StayDistrict> stayDistrictList, Pair<String, String> namePair)
    {
        if (stayDistrictList == null || namePair == null)
        {
            return null;
        }

        for (StayDistrict stayDistrict : stayDistrictList)
        {
            if (stayDistrict.name.equalsIgnoreCase(namePair.first) == true)
            {
                if (stayDistrict.getTownCount() > 0)
                {
                    for (StayTown stayTown : stayDistrict.getTownList())
                    {
                        if (stayTown.name.equalsIgnoreCase(namePair.second) == true)
                        {
                            return stayTown;
                        }
                    }
                } else
                {
                    return new StayTown(stayDistrict);
                }
            }
        }

        return null;
    }

    StayTown searchTown(List<StayDistrict> stayDistrictList, int districtIndex, int townIndex)
    {
        if (stayDistrictList == null || districtIndex < 0)
        {
            return null;
        }

        for (StayDistrict stayDistrict : stayDistrictList)
        {
            if (stayDistrict.index == districtIndex)
            {
                if (townIndex >= 0 && stayDistrict.getTownCount() > 0)
                {
                    for (StayTown stayTown : stayDistrict.getTownList())
                    {
                        if (stayTown.index == townIndex)
                        {
                            return stayTown;
                        }
                    }
                } else
                {
                    return new StayTown(stayDistrict);
                }
            }
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
    {
        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            Category category = (Category) tab.getTag();
            mStayCuration.setCategory(StayMainActivity.this, category);

            mPlaceMainLayout.setCurrentItem(tab.getPosition());

            refreshCurrentFragment(false);
        }

        @Override
        public void onCategoryTabUnselected(TabLayout.Tab tab)
        {
            // do nothing!
        }

        @Override
        public void onCategoryTabReselected(TabLayout.Tab tab)
        {
            setScrollListTop();
        }

        @Override
        public void onSearchClick()
        {
            Intent intent = SearchActivity.newInstance(StayMainActivity.this, PlaceType.HOTEL, mStayCuration.getStayBookingDay());
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_MAP_VIEW, null);
                    break;
            }
        }

        @Override
        public void onDateClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startCalendar(AnalyticsManager.ValueType.LIST, mTodayDateTime);
        }

        @Override
        public void onRegionClick()
        {
            StayMainActivity.this.onRegionClick();
        }

        @Override
        public void onViewTypeClick()
        {
            mPlaceMainLayout.showAppBarLayout(false);

            changeViewType();
        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayTown stayTown = mStayCuration.getTown();

            if (stayTown == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = StayCurationActivity.newInstance(StayMainActivity.this, mViewType, mStayCuration);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            String viewType = AnalyticsManager.Label.VIEWTYPE_LIST;

            switch (mViewType)
            {
                case LIST:
                    viewType = AnalyticsManager.Label.VIEWTYPE_LIST;
                    break;

                case MAP:
                    viewType = AnalyticsManager.Label.VIEWTYPE_MAP;
                    break;
            }

            AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
        }

        @Override
        public void onPageScroll()
        {
            // 목록을 맵으로 보고 있는 경우 페이지 스크롤 중에 하단 옵션 레이아웃의 위치가 초기화 되는 이슈 수정
            if (mViewType == ViewType.MAP)
            {
                try
                {
                    PlaceListFragment placeListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();

                    if (placeListFragment.getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                    {
                        placeListFragment.getPlaceListLayout().getListMapFragment().clickMap();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

                    changeViewType();
                }
            }
        }

        @Override
        public void onPageSelected(int changedPosition, int prevPosition)
        {
            // 목록을 맵으로 보고 있는 경우 페이지 스크롤 중에 하단 옵션 레이아웃의 위치가 초기화 되는 이슈 수정
            if (mViewType == ViewType.MAP)
            {
                try
                {
                    PlaceListFragment placeListFragment = mPlaceMainLayout.getPlaceListFragment().get(prevPosition);

                    if (placeListFragment.getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                    {
                        placeListFragment.getPlaceListLayout().getListMapFragment().clickMap();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

                    changeViewType();
                }
            }
        }

        @Override
        public void onCartMenusBookingClick()
        {

        }

        @Override
        public void finish()
        {
            StayMainActivity.this.finish();
        }
    };

    private PlaceMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceMainNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onDateTime(TodayDateTime todayDateTime)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mTodayDateTime = todayDateTime;

            try
            {
                StayBookingDay stayBookingDay = mStayCuration.getStayBookingDay();

                // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                if (stayBookingDay == null)
                {
                    stayBookingDay = new StayBookingDay();

                    stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);
                    stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, 1);

                    mStayCuration.setStayBookingDay(stayBookingDay);
                } else
                {
                    // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                    // 체크인 날짜 체크

                    // 날짜로 비교해야 한다.
                    Calendar todayCalendar = DailyCalendar.getInstance(mTodayDateTime.dailyDateTime, true);
                    Calendar checkInCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), true);
                    Calendar checkOutCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT), true);

                    // 하루가 지나서 체크인 날짜가 전날짜 인 경우
                    if (todayCalendar.getTimeInMillis() > checkInCalendar.getTimeInMillis())
                    {
                        stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);

                        checkInCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), true);
                    }

                    // 체크인 날짜가 체크 아웃 날짜와 같거나 큰경우.
                    if (checkInCalendar.getTimeInMillis() >= checkOutCalendar.getTimeInMillis())
                    {
                        stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), 1);
                    }
                }

                if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                    && processDeepLinkByDateTime(StayMainActivity.this, mTodayDateTime, mDailyDeepLink) == true)
                {

                } else
                {
                    ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(mStayCuration.getStayBookingDay());

                    addCompositeDisposable(mStayRemoteImpl.getDistrictList(DailyCategoryType.STAY_ALL).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayDistrict>>()
                    {
                        @Override
                        public void accept(List<StayDistrict> stayDistrictList) throws Exception
                        {
                            if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true//
                                && processDeepLinkByRegionList(StayMainActivity.this, stayDistrictList, mTodayDateTime, mDailyDeepLink) == true)
                            {

                            } else
                            {
                                StayTown stayTown = mStayCuration.getTown();

                                if (stayTown == null)
                                {
                                    stayTown = searchTown(stayDistrictList, getDistrictNTownNameByCategory(DailyCategoryType.STAY_ALL));
                                }

                                if (stayTown == null)
                                {
                                    stayTown = new StayTown(stayDistrictList.get(0));
                                }

                                mStayCuration.setTown(stayTown);

                                mPlaceMainLayout.setToolbarRegionText(stayTown.name);
                                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), stayTown.getCategoryList(), //
                                    mStayCuration.getCategory(), mStayListFragmentListener);
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {

                        }
                    }));
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        @Override
        public void onRegionList(List<Province> provinceList, List<Area> areaList)
        {
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayMainActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayMainActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayMainActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayMainActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayMainActivity.this.onErrorResponse(call, response);
        }

        private boolean processDeepLinkByDateTime(BaseActivity baseActivity, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
        {
            if (dailyDeepLink == null)
            {
                return false;
            }

            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                //                if (externalDeepLink.isHotelDetailView() == true)
                //                {
                //                    unLockUI();
                //
                //                    return moveDeepLinkDetail(baseActivity, todayDateTime, dailyDeepLink);
                //                } else
                if (externalDeepLink.isHotelSearchView() == true //
                    || externalDeepLink.isCampaignTagListView() == true)
                {
                    unLockUI();

                    return moveDeepLinkSearch(baseActivity, todayDateTime, dailyDeepLink);
                } else if (externalDeepLink.isHotelSearchResultView() == true)
                {
                    unLockUI();

                    return moveDeepLinkSearchResult(baseActivity, todayDateTime, dailyDeepLink);
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

        private boolean processDeepLinkByRegionList(BaseActivity baseActivity//
            , List<StayDistrict> stayDistrictList, TodayDateTime todayDateTime//
            , DailyDeepLink dailyDeepLink)
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
                    unLockUI();

                    return moveDeepLinkStayList(stayDistrictList, todayDateTime, externalDeepLink);
                } else
                {
                    externalDeepLink.clear();
                }
            } else
            {

            }

            return false;
        }
    };

    StayListFragment.OnStayListFragmentListener mStayListFragmentListener = new StayListFragment.OnStayListFragmentListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onStayClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (isFinishing() == true || placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Stay stay = placeViewItem.getItem();
                    StayTown stayTown = mStayCuration.getTown();

                    StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
                    analyticsParam.setAddressAreaName(stay.addressSummary);
                    analyticsParam.discountPrice = stay.discountPrice;
                    analyticsParam.price = stay.price;
                    analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
                    analyticsParam.setTown(stayTown);
                    analyticsParam.entryPosition = stay.entryPosition;
                    analyticsParam.totalListCount = listCount;
                    analyticsParam.isDailyChoice = stay.isDailyChoice;
                    analyticsParam.gradeName = stay.getGrade().getName(StayMainActivity.this);

                    StayBookingDay stayBookingDay = mStayCuration.getStayBookingDay();

                    if (Util.isUsedMultiTransition() == true)
                    {
                        setExitSharedElementCallback(new SharedElementCallback()
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

                        ActivityOptionsCompat optionsCompat;
                        Intent intent;

                        if (view instanceof DailyStayCardView == true)
                        {
                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StayMainActivity.this, ((DailyStayCardView) view).getOptionsCompat());

                            intent = StayDetailActivity.newInstance(StayMainActivity.this //
                                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);
                        } else
                        {
                            View simpleDraweeView = view.findViewById(R.id.imageView);
                            View gradeTextView = view.findViewById(R.id.gradeTextView);
                            View nameTextView = view.findViewById(R.id.nameTextView);
                            View gradientTopView = view.findViewById(R.id.gradientTopView);
                            View gradientBottomView = view.findViewById(R.id.gradientView);

                            intent = StayDetailActivity.newInstance(StayMainActivity.this //
                                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP, analyticsParam);

                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StayMainActivity.this,//
                                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
                        }

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
                    } else
                    {
                        Intent intent = StayDetailActivity.newInstance(StayMainActivity.this //
                            , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                            , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                            , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                            , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }

                    if (mViewType == ViewType.LIST)
                    {
                        AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", stay.entryPosition, stay.index), null);

                        AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.STAY_DAILYCHOICE_CLICK, stay.isDailyChoice ? AnalyticsManager.Label.Y : AnalyticsManager.Label.N, null);

                        // 할인 쿠폰이 보이는 경우
                        if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
                        {
                            AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
                        }

                        if (stay.reviewCount > 0)
                        {
                            AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
                        }

                        if (stay.truevr == true)
                        {
                            AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
                        }

                        if (DailyRemoteConfigPreference.getInstance(StayMainActivity.this).isKeyRemoteConfigRewardStickerEnabled()//
                            && stay.provideRewardSticker == true)
                        {
                            AnalyticsManager.getInstance(StayMainActivity.this).recordEvent(AnalyticsManager.Category.REWARD//
                                , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(stay.index), null);
                        }
                    }
                    break;
                }

                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void onStayLongClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (isFinishing() == true || placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    mPlaceMainLayout.setBlurVisibility(StayMainActivity.this, true);

                    // 기존 데이터를 백업한다.
                    mViewByLongPress = view;
                    mPlaceViewItemByLongPress = placeViewItem;
                    mListCountByLongPress = listCount;

                    Stay stay = placeViewItem.getItem();
                    Intent intent = StayPreviewActivity.newInstance(StayMainActivity.this, mStayCuration.getStayBookingDay(), stay);

                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
                    break;
                }

                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void onRegionClick()
        {
            mOnEventListener.onRegionClick();
        }

        @Override
        public void onCalendarClick()
        {
            mOnEventListener.onDateClick();
        }

        @Override
        public void onActivityCreated(PlaceListFragment placeListFragment)
        {
            if (mPlaceMainLayout == null || placeListFragment == null)
            {
                return;
            }

            PlaceListFragment currentPlaceListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();

            if (currentPlaceListFragment == placeListFragment)
            {
                currentPlaceListFragment.setVisibility(mViewType, true);
                currentPlaceListFragment.setPlaceCuration(mStayCuration);
                currentPlaceListFragment.refreshList(true);
            } else
            {
                placeListFragment.setVisibility(mViewType, false);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            switch (newState)
            {
                case RecyclerView.SCROLL_STATE_IDLE:
                {
                    if (recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() >= recyclerView.computeVerticalScrollRange())
                    {
                        StayListAdapter stayListAdapter = (StayListAdapter) recyclerView.getAdapter();

                        if (stayListAdapter != null)
                        {
                            int count = stayListAdapter.getItemCount();

                            if (count == 0)
                            {
                            } else
                            {
                                PlaceViewItem placeViewItem = stayListAdapter.getItem(stayListAdapter.getItemCount() - 1);

                                if (placeViewItem != null && placeViewItem.mType == PlaceViewItem.TYPE_FOOTER_VIEW)
                                {
                                    mPlaceMainLayout.showAppBarLayout(true);
                                    mPlaceMainLayout.showBottomLayout();
                                }
                            }
                        }
                    }
                    break;
                }

                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;

                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onShowMenuBar()
        {
            if (mPlaceMainLayout == null)
            {
                return;
            }

            mPlaceMainLayout.showBottomLayout();
        }

        @Override
        public void onBottomOptionVisible(boolean visible)
        {
            if (mPlaceMainLayout == null)
            {
                return;
            }

            mPlaceMainLayout.setBottomOptionVisible(visible);
        }

        @Override
        public void onUpdateFilterEnabled(boolean isShowFilterEnabled)
        {
            if (mPlaceMainLayout == null)
            {
                return;
            }

            mPlaceMainLayout.setOptionFilterEnabled(isShowFilterEnabled);
        }

        @Override
        public void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled)
        {
            if (mPlaceMainLayout == null)
            {
                return;
            }

            mPlaceMainLayout.setOptionViewTypeEnabled(isShowViewTypeEnabled);
        }

        @Override
        public void onFilterClick()
        {
            mOnEventListener.onFilterClick();
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {
            if (isShow == true)
            {
                mPlaceMainLayout.hideBottomLayout();
                mPlaceMainLayout.setOptionFilterEnabled(false);
            } else
            {
                mPlaceMainLayout.showBottomLayout();
                mPlaceMainLayout.setOptionFilterEnabled(true);
            }
        }

        @Override
        public void onRecordAnalytics(ViewType viewType)
        {
            try
            {
                if (viewType == ViewType.MAP)
                {
                    recordAnalyticsStayList(AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP);
                } else
                {
                    recordAnalyticsStayList(AnalyticsManager.Screen.DAILYHOTEL_LIST);
                }
            } catch (Exception e)
            {
                // GA 수집시에 메모리 해지 에러는 버린다.
            }
        }

        @Override
        public void onSearchCountUpdate(int searchCount, int searchMaxCount)
        {

        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean moveDeepLinkDetail(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                //                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;
                //
                //                // 신규 타입의 화면이동
                //                int hotelIndex = Integer.parseInt(externalDeepLink.getIndex());
                //                int nights = 1;
                //
                //                try
                //                {
                //                    nights = Integer.parseInt(externalDeepLink.getNights());
                //                } catch (Exception e)
                //                {
                //                    ExLog.d(e.toString());
                //                } finally
                //                {
                //                    if (nights <= 0)
                //                    {
                //                        nights = 1;
                //                    }
                //                }
                //
                //                String date = externalDeepLink.getDate();
                //                int datePlus = externalDeepLink.getDatePlus();
                //                boolean isShowCalendar = externalDeepLink.isShowCalendar();
                //                boolean isShowVR = externalDeepLink.isShowVR();
                //                int ticketIndex = externalDeepLink.getOpenTicketIndex();
                //                boolean overseas = externalDeepLink.getIsOverseas();
                //
                //                StayBookingDay stayBookingDay = new StayBookingDay();
                //
                //                if (DailyTextUtils.isTextEmpty(date) == false)
                //                {
                //                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                //                    stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                //                } else if (datePlus >= 0)
                //                {
                //                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                //                } else
                //                {
                //                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                //                }
                //
                //                stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);
                //
                //                mStayCuration.setStayBookingDay(stayBookingDay);
                //
                //                Intent intent = StayDetailActivity.newInstance(baseActivity, stayBookingDay, overseas, hotelIndex, ticketIndex, isShowCalendar, isShowVR, false);

                Intent intent = StayDetailActivity.newInstance(this, dailyDeepLink.getDeepLink());
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                mIsDeepLink = true;
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

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, boolean isOverseas, //
                                         List<Province> provinceList, List<Area> areaList)
    {
        if (provinceIndex < 0 && areaIndex < 0)
        {
            return null;
        }

        Province selectedProvince = null;

        try
        {
            if (areaIndex == -1)
            {
                // 전체 지역으로 이동
                for (Province province : provinceList)
                {
                    if (province.index == provinceIndex && province.isOverseas == isOverseas)
                    {
                        selectedProvince = province;
                        break;
                    }
                }
            } else
            {
                // 소지역으로 이동
                for (Area area : areaList)
                {
                    if (area.index == areaIndex)
                    {
                        for (Province province : provinceList)
                        {
                            if (area.getProvinceIndex() == province.index)
                            {
                                area.setProvince(province);
                                break;
                            }
                        }

                        selectedProvince = area;
                        break;
                    }
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return selectedProvince;
    }

    boolean moveDeepLinkSearch(BaseActivity baseActivity, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                StayBookingDay stayBookingDay = new StayBookingDay();

                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                }

                stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);

                mStayCuration.setStayBookingDay(stayBookingDay);

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
                        Intent intent = SearchActivity.newInstance(baseActivity, PlaceType.HOTEL, stayBookingDay, index);
                        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);
                    }
                } else
                {
                    String word = externalDeepLink.getSearchWord();
                    Intent intent = SearchActivity.newInstance(baseActivity, PlaceType.HOTEL, stayBookingDay, word);
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);
                }

                mIsDeepLink = true;
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

    boolean moveDeepLinkSearchResult(BaseActivity baseActivity, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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
                    StayBookingDay stayBookingDay = new StayBookingDay();

                    if (DailyTextUtils.isTextEmpty(date) == false)
                    {
                        Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                        stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                    } else if (datePlus >= 0)
                    {
                        stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                    } else
                    {
                        stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                    }

                    stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);

                    mStayCuration.setStayBookingDay(stayBookingDay);

                    switch (searchType)
                    {
                        case LOCATION:
                        {
                            if (latLng != null)
                            {
                                Intent intent = StaySearchResultActivity.newInstance(baseActivity, todayDateTime, stayBookingDay, latLng, radius, true);
                                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                            } else
                            {
                                return false;
                            }
                            break;
                        }

                        default:
                            if (DailyTextUtils.isTextEmpty(word) == false)
                            {
                                Intent intent = StaySearchResultActivity.newInstance(baseActivity, todayDateTime, stayBookingDay, new Keyword(0, word), SearchType.SEARCHES);
                                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                            } else
                            {
                                return false;
                            }
                            break;
                    }

                    mIsDeepLink = true;
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

    boolean moveDeepLinkStayList(List<StayDistrict> stayDistrictList, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
                stayCurationOption.setSortType(externalDeepLink.getSorting());

                mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

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
                StayTown stayTown = searchTown(stayDistrictList, provinceIndex, areaIndex);

                if (stayTown == null)
                {
                    return false;
                }

                mStayCuration.setTown(stayTown);

                mPlaceMainLayout.setToolbarRegionText(stayTown.name);

                // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
                if (DailyTextUtils.isTextEmpty(categoryCode) == false && stayTown.getCategoryCount() > 0)
                {
                    for (Category category : stayTown.getCategoryList())
                    {
                        if (category.code.equalsIgnoreCase(categoryCode) == true)
                        {
                            mStayCuration.setCategory(StayMainActivity.this, category);
                            break;
                        }
                    }
                }

                StayBookingDay stayBookingDay = new StayBookingDay();

                if (DailyTextUtils.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                }

                stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);

                mStayCuration.setStayBookingDay(stayBookingDay);

                ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

                mPlaceMainLayout.setToolbarRegionText(stayTown.name);
                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), stayTown.getCategoryList(), //
                    mStayCuration.getCategory(), mStayListFragmentListener);
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
