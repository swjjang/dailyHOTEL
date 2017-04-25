package com.twoheart.dailyhotel.screen.home.category.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainActivity;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.home.category.region.HomeCategoryRegionListActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 4. 19..
 */
public class StayCategoryListActivity extends PlaceMainActivity
{
    private StayCuration mStayCuration;
    private DailyCategoryType mDailyCategoryType;
    private DailyDeepLink mDailyDeepLink;

    public static Intent newInstance(Context context, DailyCategoryType categoryType, String deepLink)
    {
        Intent intent = new Intent(context, StayCategoryListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE, (Parcelable) categoryType);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // category 정보를 layout 넘기기 위해 먼저 진행 되어야 함
        Intent intent = getIntent();
        initIntent(intent);

        super.onCreate(savedInstanceState);

        mStayCuration = new StayCuration();

        if (mDailyCategoryType == null //
            || DailyCategoryType.STAY_AROUND_SEARCH == mDailyCategoryType //
            || DailyCategoryType.NONE == mDailyCategoryType)
        {
            Util.restartApp(this);
            return;
        }

        String name = getResources().getString(mDailyCategoryType.getNameResId());
        String code = getResources().getString(mDailyCategoryType.getCodeResId());
        mStayCuration.setCategory(new Category(name, code));

        initDeepLink(intent);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);
    }

    private void initIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        mDailyCategoryType = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE);

        if (mDailyCategoryType == null)
        {
            mDailyCategoryType = DailyCategoryType.NONE;
        }
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
        } catch (Exception e)
        {
            mDailyDeepLink = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        //        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
        //            AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
    }

    @Override
    protected PlaceMainLayout getPlaceMainLayout(Context context)
    {
        String titleText;
        try
        {
            titleText = context.getResources().getString(mDailyCategoryType.getNameResId());
        } catch (Exception e)
        {
            titleText = "";
        }

        return new StayCategoryListLayout(this, titleText, mOnEventListener);
    }

    @Override
    protected PlaceMainNetworkController getPlaceMainNetworkController(Context context)
    {
        return new StayCategoryListNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void onRegionActivityResult(int resultCode, Intent data)
    {
        // 지역 선택하고 돌아온 경우
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
            {
                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
                stayCurationOption.clear();

                Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
                mStayCuration.setProvince(province);

                String categoryName = this.getResources().getString(mDailyCategoryType.getNameResId());
                String categoryCode = this.getResources().getString(mDailyCategoryType.getCodeResId());

                Category category = new Category(categoryName, categoryCode);
                // subCategory 의 경우 category 를 preference에 저장 안함
                mStayCuration.setCategory(category);

                mPlaceMainLayout.setToolbarRegionText(province.name);
                mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

                // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
                JSONObject savedRegionJsonObject = DailyPreference.getInstance(this).getDailyRegion(mDailyCategoryType);
                JSONObject currentRegionJsonObject = Util.getDailyRegionJSONObject(province);

                if (savedRegionJsonObject.equals(currentRegionJsonObject) == false)
                {
                    DailyPreference.getInstance(this).setDailyRegion(mDailyCategoryType, currentRegionJsonObject);

                    //                    String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                    //                    String realProvinceName = Util.getRealProvinceName(province); // 대지역 반환
                    //                    DailyPreference.getInstance(this).setSelectedRegionTypeProvince(PlaceType.HOTEL, realProvinceName);
                    //                    AnalyticsManager.getInstance(this).onRegionChanged(country, realProvinceName);
                }

                ArrayList<Category> categoryList = new ArrayList<>();
                categoryList.add(mStayCuration.getCategory());

                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                    mStayCuration.getCategory(), mStayListFragmentListener);
            }
        } else if (resultCode == RESULT_CHANGED_DATE && data != null)
        {
            // 날짜 선택 화면으로 이동한다.
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
            {
                StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
                stayCurationOption.clear();

                Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
                StayBookingDay stayBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                mStayCuration.setProvince(province);

                String categoryName = this.getResources().getString(mDailyCategoryType.getNameResId());
                String categoryCode = this.getResources().getString(mDailyCategoryType.getCodeResId());

                Category category = new Category(categoryName, categoryCode);
                // subCategory 의 경우 category 를 preference에 저장 안함
                mStayCuration.setCategory(category);

                mPlaceMainLayout.setToolbarRegionText(province.name);
                mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

                // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
                JSONObject savedRegionJsonObject = DailyPreference.getInstance(this).getDailyRegion(mDailyCategoryType);
                JSONObject currentRegionJsonObject = Util.getDailyRegionJSONObject(province);

                if (savedRegionJsonObject.equals(currentRegionJsonObject) == false)
                {
                    DailyPreference.getInstance(this).setDailyRegion(mDailyCategoryType, currentRegionJsonObject);

                    //                    String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                    //                    String realProvinceName = Util.getRealProvinceName(province);
                    //                    DailyPreference.getInstance(this).setSelectedRegionTypeProvince(PlaceType.HOTEL, realProvinceName);
                    //                    AnalyticsManager.getInstance(this).onRegionChanged(country, realProvinceName);
                }

                mStayCuration.setStayBookingDay(stayBookingDay);

                ((StayCategoryListLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

                startCalendar(AnalyticsManager.Label.CHANGE_LOCATION, mTodayDateTime);

                ArrayList<Category> categoryList = new ArrayList<>();
                categoryList.add(mStayCuration.getCategory());

                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                    mStayCuration.getCategory(), mStayListFragmentListener);
            }
        } else if (resultCode == RESULT_ARROUND_SEARCH_LIST && data != null)
        {
            // 검색 결과 화면으로 이동한다.
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_LOCATION) == true)
            {
                Location location = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_LOCATION);
                mStayCuration.setLocation(location);

                String region = data.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                String callByScreen = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;

                if (PlaceRegionListActivity.Region.DOMESTIC.name().equalsIgnoreCase(region) == true)
                {
                    callByScreen = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;
                } else if (PlaceRegionListActivity.Region.GLOBAL.name().equalsIgnoreCase(region) == true)
                {
                    callByScreen = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL;
                }

                startAroundSearchResult(this, mTodayDateTime, mStayCuration.getStayBookingDay(), location, callByScreen);
            }
        } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
        {
            setResult(resultCode);
            finish();
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

            ((StayCategoryListLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
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

                searchMyLocation();
            } else
            {
                refreshCurrentFragment(true);
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
        if (todayDateTime == null || isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = StayCalendarActivity.newInstance(this, todayDateTime, mStayCuration.getStayBookingDay(), callByScreen, true, true);

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        //        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
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

    // GA 주석 처리
    void recordAnalyticsStayList(String screen)
    {
        //        if (AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == false //
        //            && AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screen) == false)
        //        {
        //            return;
        //        }
        //
        //        StayBookingDay stayBookingDay = mStayCuration.getStayBookingDay();
        //        Map<String, String> params = new HashMap<>();
        //
        //        try
        //        {
        //            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
        //            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
        //            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));
        //
        //            if (DailyHotel.isLogin() == false)
        //            {
        //                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
        //            } else
        //            {
        //                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
        //            }
        //
        //            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.HOTEL);
        //            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.HOTEL);
        //            params.put(AnalyticsManager.KeyType.CATEGORY, mStayCuration.getCategory().code);
        //            params.put(AnalyticsManager.KeyType.FILTER, mStayCuration.getCurationOption().toAdjustString());
        //
        //            Province province = mStayCuration.getProvince();
        //
        //            if (province == null)
        //            {
        //                Util.restartApp(this);
        //                return;
        //            }
        //
        //            if (province instanceof Area)
        //            {
        //                Area area = (Area) province;
        //                params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
        //                params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
        //                params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        //            } else if (province != null)
        //            {
        //                params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
        //                params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
        //                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
        //            }
        //
        //            AnalyticsManager.getInstance(this).recordScreen(this, screen, null, params);
        //        } catch (Exception e)
        //        {
        //            ExLog.e(e.toString());
        //        }
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
            Util.restartApp(StayCategoryListActivity.this);
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

                    DailyToast.showToast(StayCategoryListActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                    return;
                }

                mViewType = ViewType.MAP;

                //                AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
                break;
            }

            case MAP:
            {
                mViewType = ViewType.LIST;

                //                AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_LIST, null);
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
    {
        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            Category category = (Category) tab.getTag();
            mStayCuration.setCategory(StayCategoryListActivity.this, category);

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
            Intent intent = SearchActivity.newInstance(StayCategoryListActivity.this, PlaceType.HOTEL, mStayCuration.getStayBookingDay());
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                {
                    String label = "";
                    switch (mDailyCategoryType)
                    {
                        case STAY_HOTEL:
                            label = AnalyticsManager.Label.HOTEL_LIST;
                            break;
                        case STAY_BOUTIQUE:
                            label = AnalyticsManager.Label.BOUTIQUE_LIST;
                            break;
                        case STAY_PENSION:
                            label = AnalyticsManager.Label.PENSION_LIST;
                            break;
                        case STAY_RESORT:
                            label = AnalyticsManager.Label.RESORT_LIST;
                            break;
                    }

                    AnalyticsManager.getInstance(StayCategoryListActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
                    break;
                }

                case MAP:
                {
                    String label = "";
                    switch (mDailyCategoryType)
                    {
                        case STAY_HOTEL:
                            label = AnalyticsManager.Label.HOTEL_LIST_MAP;
                            break;
                        case STAY_BOUTIQUE:
                            label = AnalyticsManager.Label.BOUTIQUE_LIST_MAP;
                            break;
                        case STAY_PENSION:
                            label = AnalyticsManager.Label.PENSION_LIST_MAP;
                            break;
                        case STAY_RESORT:
                            label = AnalyticsManager.Label.RESORT_LIST_MAP;
                            break;
                    }

                    AnalyticsManager.getInstance(StayCategoryListActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
                    break;
                }
            }
        }

        @Override
        public void onDateClick()
        {
            startCalendar(AnalyticsManager.ValueType.LIST, mTodayDateTime);
        }

        @Override
        public void onRegionClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(HomeCategoryRegionListActivity.newInstance( //
                StayCategoryListActivity.this, mDailyCategoryType, mStayCuration.getStayBookingDay()) //
                , Constants.CODE_REQUEST_ACTIVITY_REGIONLIST);

            switch (mViewType)
            {
                case LIST:
                    //                    AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_LIST, null);
                    break;

                case MAP:
                    //                    AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._HOTEL_MAP, null);
                    break;
            }
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

            Province province = mStayCuration.getProvince();

            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = StayCurationActivity.newInstance(StayCategoryListActivity.this, mViewType, mStayCuration);
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

            //            AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            //                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
        }

        @Override
        public void finish()
        {
            StayCategoryListActivity.this.finish();
        }
    };

    private PlaceMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener //
        = new PlaceMainNetworkController.OnNetworkControllerListener()
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
                    && processDeepLinkByDateTime(StayCategoryListActivity.this, mTodayDateTime, mDailyDeepLink) == true)
                {

                } else
                {
                    ((StayCategoryListLayout) mPlaceMainLayout).setToolbarDateText(mStayCuration.getStayBookingDay());

                    // 이벤트 영역의 경우 사용하지 않음으로 주석처리
                    //                    mPlaceMainNetworkController.requestEventBanner();
                    mPlaceMainNetworkController.requestRegionList();
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        @Override
        public void onEventBanner(List<EventBanner> eventBannerList)
        {
            //            StayEventBannerManager.getInstance().setList(eventBannerList);
            //
            //            mPlaceMainNetworkController.requestRegionList();
        }

        @Override
        public void onRegionList(List<Province> provinceList, List<Area> areaList)
        {
            if (isFinishing() == true || provinceList == null || areaList == null)
            {
                return;
            }

            Province selectedProvince = mStayCuration.getProvince();

            if (selectedProvince == null)
            {
                selectedProvince = searchLastRegion(StayCategoryListActivity.this, provinceList, areaList);
            }

            // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
            if (selectedProvince == null)
            {
                selectedProvince = provinceList.get(0);
            }

            //                String country = selectedProvince.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
            //                AnalyticsManager.getInstance(StaySubCategoryActivity.this).onRegionChanged(country, selectedProvince.name);

            DailyPreference.getInstance(StayCategoryListActivity.this).setDailyRegion(mDailyCategoryType, Util.getDailyRegionJSONObject(selectedProvince));

            mStayCuration.setProvince(selectedProvince);

            if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                && processDeepLinkByRegionList(StayCategoryListActivity.this, provinceList, areaList, mTodayDateTime, mDailyDeepLink) == true)
            {

            } else
            {
                ArrayList<Category> categoryList = new ArrayList<>();
                categoryList.add(mStayCuration.getCategory());

                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                    mStayCuration.getCategory(), mStayListFragmentListener);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayCategoryListActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayCategoryListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayCategoryListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayCategoryListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayCategoryListActivity.this.onErrorResponse(call, response);
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

                if (externalDeepLink.isHotelDetailView() == true)
                {
                    unLockUI();

                    return moveDeepLinkDetail(baseActivity, todayDateTime, dailyDeepLink);
                } else if (externalDeepLink.isHotelSearchView() == true)
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

        private boolean processDeepLinkByRegionList(BaseActivity baseActivity //
            , List<Province> provinceList, List<Area> areaList, TodayDateTime todayDateTime //
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

                    return moveDeepLinkStayList(provinceList, areaList, todayDateTime, externalDeepLink);
                } else
                {
                    externalDeepLink.clear();
                }
            } else
            {

            }

            return false;
        }

        private Province searchLastRegion(BaseActivity baseActivity, //
                                          List<Province> provinceList, //
                                          List<Area> areaList)
        {
            // 마지막으로 선택한 지역을 가져온다.
            JSONObject lastRegionJsonObject = DailyPreference.getInstance(baseActivity).getDailyRegion(mDailyCategoryType);
            String lastProvinceName = Util.getDailyProvinceString(lastRegionJsonObject);
            String lastAreaName = Util.getDailyAreaString(lastRegionJsonObject);

            if (DailyTextUtils.isTextEmpty(lastProvinceName) == true)
            {
                return null;
            }

            if (DailyTextUtils.isTextEmpty(lastAreaName) == false)
            {
                for (Area area : areaList)
                {
                    if (area.name.equals(lastAreaName) == true)
                    {
                        for (Province province : provinceList)
                        {
                            if (area.getProvinceIndex() == province.index)
                            {
                                area.isOverseas = province.isOverseas;
                                area.setProvince(province);
                                break;
                            }
                        }

                        return area;
                    }
                }
            }

            for (Province province : provinceList)
            {
                if (province.name.equals(lastProvinceName) == true)
                {
                    return province;
                }
            }

            return null;
        }
    };

    StayListFragment.OnStayListFragmentListener mStayListFragmentListener = new StayListFragment.OnStayListFragmentListener()
    {
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
                    Province province = mStayCuration.getProvince();

                    JSONObject lastRegionObject = DailyPreference.getInstance(StayCategoryListActivity.this).getDailyRegion(mDailyCategoryType);
                    boolean isSameProvince = Util.isSameProvinceName(province, lastRegionObject);

                    if (isSameProvince == false)
                    {
                        DailyPreference.getInstance(StayCategoryListActivity.this).setDailyRegion(mDailyCategoryType, Util.getDailyRegionJSONObject(province));

                        //                        String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                        //                        String realProvinceName = Util.getRealProvinceName(province);
                        //                        DailyPreference.getInstance(StayMainActivity.this).setSelectedRegionTypeProvince(PlaceType.HOTEL, realProvinceName);
                        //                        AnalyticsManager.getInstance(StayMainActivity.this).onRegionChanged(country, realProvinceName);
                    }

                    if (Util.isUsedMultiTransition() == true)
                    {
                        Intent intent = StayDetailActivity.newInstance(StayCategoryListActivity.this, //
                            mStayCuration.getStayBookingDay(), province, stay, listCount, true);

                        View simpleDraweeView = view.findViewById(R.id.imageView);
                        View gradeTextView = view.findViewById(R.id.gradeTextView);
                        View nameTextView = view.findViewById(R.id.nameTextView);
                        View gradientTopView = view.findViewById(R.id.gradientTopView);
                        View gradientBottomView = view.findViewById(R.id.gradientView);

                        Object mapTag = gradientBottomView.getTag();

                        if (mapTag != null && "map".equals(mapTag) == true)
                        {
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
                        }

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StayCategoryListActivity.this,//
                            android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                            android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                            android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                            android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                            android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
                    } else
                    {
                        Intent intent = StayDetailActivity.newInstance(StayCategoryListActivity.this, //
                            mStayCuration.getStayBookingDay(), province, stay, listCount, false);

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }

                    if (mViewType == ViewType.LIST)
                    {
                        //                        AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        //                            , AnalyticsManager.Action.STAY_ITEM_CLICK, Integer.toString(stay.index), null);
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
                    mPlaceMainLayout.setBlurVisibility(StayCategoryListActivity.this, true);

                    // 기존 데이터를 백업한다.
                    mViewByLongPress = view;
                    mPlaceViewItemByLongPress = placeViewItem;
                    mListCountByLongPress = listCount;

                    Stay stay = placeViewItem.getItem();
                    Intent intent = StayPreviewActivity.newInstance(StayCategoryListActivity.this, mStayCuration.getStayBookingDay(), stay);

                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
                    break;
                }

                default:
                    unLockUI();
                    break;
            }
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
    // Deep Link - 해당 경우는 고려하지 않아도 되나 임시로 넣어 둠
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean moveDeepLinkDetail(BaseActivity baseActivity, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                // 신규 타입의 화면이동
                int hotelIndex = Integer.parseInt(externalDeepLink.getIndex());
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

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                boolean isShowCalendar = externalDeepLink.isShowCalendar();
                int ticketIndex = externalDeepLink.getOpenTicketIndex();

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

                Intent intent = StayDetailActivity.newInstance(baseActivity, stayBookingDay, hotelIndex, ticketIndex, isShowCalendar, false);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

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
                String word = externalDeepLink.getSearchWord();

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

                Intent intent = SearchActivity.newInstance(baseActivity, PlaceType.HOTEL, stayBookingDay, word);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

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

    boolean moveDeepLinkStayList(List<Province> provinceList, List<Area> areaList//
        , TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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
                Province selectedProvince = searchDeeLinkRegion(provinceIndex, areaIndex, isOverseas, provinceList, areaList);

                if (selectedProvince == null)
                {
                    selectedProvince = mStayCuration.getProvince();
                }

                mStayCuration.setProvince(selectedProvince);

                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);

                // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
                if (DailyTextUtils.isTextEmpty(categoryCode) == false)
                {
                    for (Category category : selectedProvince.getCategoryList())
                    {
                        if (category.code.equalsIgnoreCase(categoryCode) == true)
                        {
                            mStayCuration.setCategory(StayCategoryListActivity.this, category);
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

                ((StayCategoryListLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

                mPlaceMainNetworkController.requestRegionList();
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
