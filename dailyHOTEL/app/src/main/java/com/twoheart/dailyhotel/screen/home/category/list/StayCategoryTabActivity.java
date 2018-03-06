package com.twoheart.dailyhotel.screen.home.category.list;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.area.stay.StayAreaListActivity;
import com.daily.dailyhotel.screen.common.area.stay.inbound.StayAreaTabActivity;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayMapCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCategoryCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainActivity;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.home.category.filter.StayCategoryCurationActivity;
import com.twoheart.dailyhotel.screen.home.category.nearby.StayCategoryNearByActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 4. 19..
 */
public class StayCategoryTabActivity extends PlaceMainActivity
{
    StayCategoryCuration mStayCategoryCuration;
    DailyCategoryType mDailyCategoryType;
    DailyDeepLink mDailyDeepLink;

    StayRemoteImpl mStayRemoteImpl;

    public static Intent newInstance(Context context, DailyCategoryType categoryType, String deepLink)
    {
        Intent intent = new Intent(context, StayCategoryTabActivity.class);
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

        mStayCategoryCuration = new StayCategoryCuration();

        mStayRemoteImpl = new StayRemoteImpl(this);

        if (mDailyCategoryType == null //
            || DailyCategoryType.STAY_NEARBY == mDailyCategoryType //
            || DailyCategoryType.NONE == mDailyCategoryType)
        {
            Util.restartApp(this);
            return;
        }

        String name = getResources().getString(mDailyCategoryType.getNameResId());
        String code = getResources().getString(mDailyCategoryType.getCodeResId());
        mStayCategoryCuration.setCategory(new Category(name, code));

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

        try
        {
            String label = StayCategoryTabActivity.this.getResources().getString(mDailyCategoryType.getCodeResId());

            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK, label, null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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

        return new StayCategoryTabLayout(this, titleText, mDailyCategoryType, mOnEventListener);
    }

    @Override
    protected PlaceMainNetworkController getPlaceMainNetworkController(Context context)
    {
        return new StayCategoryTabNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void onRegionActivityResult(int resultCode, Intent data)
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

                    StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryCuration.getCurationOption();
                    stayCurationOption.clear();

                    mStayCategoryCuration.setRegion(region);

                    mPlaceMainLayout.setToolbarRegionText(region.getAreaName());
                    mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

                    boolean changedAreaGroup = data.getBooleanExtra(StayAreaListActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, false);

                    if (changedAreaGroup == true)
                    {
                        AnalyticsManager.getInstance(this).onRegionChanged(AnalyticsManager.ValueType.DOMESTIC, region.getAreaGroupName());
                    }

                    ArrayList<Category> categoryList = new ArrayList<>();
                    categoryList.add(mStayCategoryCuration.getCategory());

                    mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                        mStayCategoryCuration.getCategory(), mStayCategoryListFragmentListener);
                }

                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR)
                {
                    startCalendar(AnalyticsManager.Label.CHANGE_LOCATION, mTodayDateTime);
                }
                break;

            case com.daily.base.BaseActivity.RESULT_CODE_START_AROUND_SEARCH:
            {
                // 검색 결과 화면으로 이동한다.
                startAroundSearchResult(this, mTodayDateTime, mStayCategoryCuration.getStayBookingDay()//
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
            try
            {
                String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                StayBookingDay stayBookingDay = new StayBookingDay();
                stayBookingDay.setCheckInDay(checkInDateTime);
                stayBookingDay.setCheckOutDay(checkOutDateTime);

                mStayCategoryCuration.setStayBookingDay(stayBookingDay);

                ((StayCategoryTabLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

                refreshCurrentFragment(true);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    @Override
    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

            if ((placeCuration instanceof StayCategoryCuration) == false)
            {
                return;
            }

            StayCategoryCuration changedStayCuration = (StayCategoryCuration) placeCuration;
            StayCurationOption changedStayCurationOption = (StayCurationOption) changedStayCuration.getCurationOption();

            mStayCategoryCuration.setCurationOption(changedStayCurationOption);
            mPlaceMainLayout.setOptionFilterSelected(changedStayCurationOption.isDefaultFilter() == false);

            if (changedStayCurationOption.getSortType() == SortType.DISTANCE)
            {
                mStayCategoryCuration.setLocation(changedStayCuration.getLocation());

                if (mStayCategoryCuration.getLocation() != null)
                {
                    lockUI();

                    onLocationChanged(mStayCategoryCuration.getLocation());
                } else
                {
                    searchMyLocation();
                }
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
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryCuration.getCurationOption();

        if (stayCurationOption.getSortType() == SortType.DISTANCE)
        {
            if (location == null)
            {
                // 이전에 가지고 있던 데이터를 사용한다.
                if (mStayCategoryCuration.getLocation() != null)
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
                mStayCategoryCuration.setLocation(location);
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

        final int DAYS_OF_MAX_COUNT = 60;

        try
        {
            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(DailyCalendar.convertDate(todayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT));

            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);

            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookingDay stayBookingDay = mStayCategoryCuration.getStayBookingDay();

            Intent intent = StayCalendarActivity.newInstance(this//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , callByScreen, true//
                , 0, true);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

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

        Intent intent = StayCategoryNearByActivity.newInstance(context, todayDateTime //
            , stayBookingDay, location, mDailyCategoryType, callByScreen);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mStayCategoryCuration;
    }

    // GA 주석 처리
    void recordAnalyticsStayList(String screen)
    {
        if (AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        StayBookingDay stayBookingDay = mStayCategoryCuration.getStayBookingDay();
        Map<String, String> params = new HashMap<>();

        try
        {
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
                switch (DailyUserPreference.getInstance(this).getType())
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, mStayCategoryCuration.getCategory().code);
            params.put(AnalyticsManager.KeyType.FILTER, mStayCategoryCuration.getCurationOption().toAdjustString());
            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(this).isBenefitAlarm() ? "on" : "off");

            params.put(AnalyticsManager.KeyType.VIEW_TYPE //
                , AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == true //
                    ? AnalyticsManager.ValueType.MAP : AnalyticsManager.ValueType.LIST);

            StayRegion region = mStayCategoryCuration.getRegion();

            if (region == null)
            {
                Util.restartApp(this);
                return;
            }

            if (region != null)
            {
                params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.PROVINCE, region.getAreaGroupName());

                com.daily.dailyhotel.entity.Area area = region.getArea();
                params.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
            }

            AnalyticsManager.getInstance(this).recordScreen(this, screen, null, params);
            // 숏컷 리스트 진입용 GA Screen 중복 발송

            String shortcutScreen = getCallByScreen(mDailyCategoryType);
            if (DailyTextUtils.isTextEmpty(shortcutScreen) == false)
            {
                AnalyticsManager.getInstance(this).recordScreen(this, shortcutScreen, null, params);
            }

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private String getCallByScreen(DailyCategoryType dailyCategoryType)
    {
        if (dailyCategoryType == null)
        {
            return null;
        }

        String shortcutScreen = "";
        switch (dailyCategoryType)
        {
            case STAY_HOTEL:
                shortcutScreen = AnalyticsManager.Screen.STAY_LIST_SHORTCUT_HOTEL;
                break;
            case STAY_BOUTIQUE:
                shortcutScreen = AnalyticsManager.Screen.STAY_LIST_SHORTCUT_BOUTIQUE;
                break;
            case STAY_PENSION:
                shortcutScreen = AnalyticsManager.Screen.STAY_LIST_SHORTCUT_PENSION;
                break;
            case STAY_RESORT:
                shortcutScreen = AnalyticsManager.Screen.STAY_LIST_SHORTCUT_RESORT;
                break;
            case STAY_NEARBY:
                shortcutScreen = AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY;
                break;
        }

        return shortcutScreen;
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
            Util.restartApp(StayCategoryTabActivity.this);
            return;
        }

        lockUI();

        StayCategoryListFragment currentFragment = (StayCategoryListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();

        switch (mViewType)
        {
            case LIST:
            {
                // 고메 쪽에서 보여지는 메세지로 Stay의 경우도 동일한 처리가 필요해보여서 추가함
                if (currentFragment.hasSalesPlace() == false)
                {
                    unLockUI();

                    DailyToast.showToast(StayCategoryTabActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
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
            placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NONE, isCurrentFragment);
        }

        refreshCurrentFragment(false);

        unLockUI();
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mStayCategoryListFragmentListener == null)
        {
            return;
        }

        mStayCategoryListFragmentListener.onStayClick(view, placeViewItem, listCount);
    }

    @Override
    protected void onRegionClick()
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        try
        {
            String checkInDateTime = mStayCategoryCuration.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = mStayCategoryCuration.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT);

            startActivityForResult(StayAreaTabActivity.newInstance(this//
                , checkInDateTime, checkOutDateTime, mDailyCategoryType, mStayCategoryCuration.getCategory().code), Constants.CODE_REQUEST_ACTIVITY_REGIONLIST);
        } catch (Exception e)
        {
            Crashlytics.logException(e);

            lockUI();

            mPlaceMainNetworkController.requestDateTime();
        }
    }

    PreferenceRegion getPreferenceRegion(DailyCategoryType dailyCategoryType)
    {
        return DailyPreference.getInstance(this).getDailyRegion(dailyCategoryType);
    }

    StayRegion searchRegion(List<StayAreaGroup> areaGroupList, PreferenceRegion preferenceRegion)
    {
        if (areaGroupList == null || preferenceRegion == null)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.name.equalsIgnoreCase(preferenceRegion.areaGroupName) == true)
            {
                if (areaGroup.getAreaCount() == 0)
                {
                    return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, areaGroup);
                } else
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                        {
                            return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area);
                        }
                    }
                }
            }
        }

        return null;
    }

    StayRegion searchRegion(List<StayAreaGroup> areaGroupList, int areaGroupIndex, int areaIndex)
    {
        if (areaGroupList == null || areaGroupIndex <= 0)
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
                            return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area);
                        }
                    }
                } else
                {
                    return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, areaGroup);
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
            mStayCategoryCuration.setCategory(StayCategoryTabActivity.this, category);

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
            if (mStayCategoryCuration == null || mStayCategoryCuration.getStayBookingDay() == null)
            {
                return;
            }

            StayBookingDay stayBookingDay = mStayCategoryCuration.getStayBookingDay();

            if (stayBookingDay == null)
            {
                return;
            }

            startActivityForResult(SearchActivity.newInstance(StayCategoryTabActivity.this, ServiceType.HOTEL//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)), CODE_REQUEST_ACTIVITY_SEARCH);

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

                    AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
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

                    AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
                    break;
                }
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
            StayCategoryTabActivity.this.onRegionClick();
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

            StayRegion region = mStayCategoryCuration.getRegion();

            if (region == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = StayCategoryCurationActivity.newInstance(StayCategoryTabActivity.this, mViewType, mStayCategoryCuration);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            //            String viewType = AnalyticsManager.Label.VIEWTYPE_LIST;
            //
            //            switch (mViewType)
            //            {
            //                case LIST:
            //                    viewType = AnalyticsManager.Label.VIEWTYPE_LIST;
            //                    break;
            //
            //                case MAP:
            //                    viewType = AnalyticsManager.Label.VIEWTYPE_MAP;
            //                    break;
            //            }
            //
            //            AnalyticsManager.getInstance(StaySubCategoryActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            //                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
        }

        @Override
        public void onPageScroll()
        {

        }

        @Override
        public void onPageSelected(int changedPosition, int prevPosition)
        {

        }

        @Override
        public void onCartMenusBookingClick()
        {

        }

        @Override
        public void finish()
        {
            StayCategoryTabActivity.this.finish();
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
                StayBookingDay stayBookingDay = mStayCategoryCuration.getStayBookingDay();

                // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                if (stayBookingDay == null || stayBookingDay.validate() == false)
                {
                    stayBookingDay = new StayBookingDay();

                    stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);
                    stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, 1);
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

                mStayCategoryCuration.setStayBookingDay(stayBookingDay);

                ((StayCategoryTabLayout) mPlaceMainLayout).setToolbarDateText(mStayCategoryCuration.getStayBookingDay());

                addCompositeDisposable(mStayRemoteImpl.getAreaList(mDailyCategoryType).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayAreaGroup>>()
                {
                    @Override
                    public void accept(List<StayAreaGroup> areaGroupList) throws Exception
                    {
                        if (mDailyDeepLink != null && processDeepLinkByRegionList(StayCategoryTabActivity.this, areaGroupList, mTodayDateTime, mDailyDeepLink) == true)
                        {

                        } else
                        {
                            StayRegion region = mStayCategoryCuration.getRegion();

                            if (region == null)
                            {
                                region = searchRegion(areaGroupList, getPreferenceRegion(mDailyCategoryType));
                            }

                            if (region == null)
                            {
                                region = new StayRegion(PreferenceRegion.AreaType.AREA, areaGroupList.get(0), areaGroupList.get(0));
                            }

                            mStayCategoryCuration.setRegion(region);

                            List<Category> categoryList = new ArrayList<>();
                            categoryList.add(mStayCategoryCuration.getCategory());

                            mPlaceMainLayout.setToolbarRegionText(region.getAreaName());
                            mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                                mStayCategoryCuration.getCategory(), mStayCategoryListFragmentListener);
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {

                    }
                }));

            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayCategoryTabActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayCategoryTabActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayCategoryTabActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayCategoryTabActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayCategoryTabActivity.this.onErrorResponse(call, response);
        }

        boolean processDeepLinkByRegionList(BaseActivity baseActivity //
            , List<StayAreaGroup> stayDistrictList, TodayDateTime todayDateTime //
            , DailyDeepLink dailyDeepLink)
        {
            if (dailyDeepLink == null)
            {
                return false;
            }

            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                if (externalDeepLink.isShortcutView() == true)
                {
                    unLockUI();

                    return moveDeepLinkShortcutList(stayDistrictList, todayDateTime, externalDeepLink);
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

    StayCategoryListFragment.OnStayListFragmentListener mStayCategoryListFragmentListener = new StayCategoryListFragment.OnStayListFragmentListener()
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
                    StayRegion region = mStayCategoryCuration.getRegion();

                    StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
                    analyticsParam.setAddressAreaName(stay.addressSummary);
                    analyticsParam.discountPrice = stay.discountPrice;
                    analyticsParam.price = stay.price;
                    analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
                    analyticsParam.setRegion(region);
                    analyticsParam.entryPosition = stay.entryPosition;
                    analyticsParam.totalListCount = listCount;
                    analyticsParam.isDailyChoice = stay.isDailyChoice;
                    analyticsParam.gradeName = stay.getGrade().getName(StayCategoryTabActivity.this);

                    StayBookingDay stayBookingDay = mStayCategoryCuration.getStayBookingDay();

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
                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StayCategoryTabActivity.this, ((DailyStayCardView) view).getOptionsCompat());

                            intent = StayDetailActivity.newInstance(StayCategoryTabActivity.this //
                                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);
                        } else if (view instanceof DailyStayMapCardView == true)
                        {
                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StayCategoryTabActivity.this, ((DailyStayMapCardView) view).getOptionsCompat());

                            intent = StayDetailActivity.newInstance(StayCategoryTabActivity.this //
                                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP, analyticsParam);
                        } else
                        {
                            unLockUI();
                            return;
                        }

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
                    } else
                    {
                        Intent intent = StayDetailActivity.newInstance(StayCategoryTabActivity.this //
                            , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                            , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                            , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                            , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }

                    if (mViewType == ViewType.LIST)
                    {
                        AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", stay.entryPosition, stay.index), null);

                        AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , stay.isDailyChoice ? AnalyticsManager.Action.STAY_DAILYCHOICE_CLICK_Y : AnalyticsManager.Action.STAY_DAILYCHOICE_CLICK_N, Integer.toString(stay.index), null);

                        // 할인 쿠폰이 보이는 경우
                        if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
                        }

                        if (stay.reviewCount > 0)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
                        }

                        if (stay.truevr == true)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
                        }

                        if (stay.isLocalPlus == true)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.STAY_ITEM_CLICK_BOUTIQUE_AD, Integer.toString(stay.index), null);
                        }

                        if (DailyRemoteConfigPreference.getInstance(StayCategoryTabActivity.this).isKeyRemoteConfigRewardStickerEnabled()//
                            && stay.provideRewardSticker == true)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.REWARD//
                                , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(stay.index), null);
                        }

                        if (stay.discountRate > 0)
                        {
                            AnalyticsManager.getInstance(StayCategoryTabActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(stay.index), null);
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
                    mPlaceMainLayout.setBlurVisibility(StayCategoryTabActivity.this, true);

                    // 기존 데이터를 백업한다.
                    mViewByLongPress = view;
                    mPlaceViewItemByLongPress = placeViewItem;
                    mListCountByLongPress = listCount;

                    Stay stay = placeViewItem.getItem();
                    Intent intent = StayPreviewActivity.newInstance(StayCategoryTabActivity.this, mStayCategoryCuration.getStayBookingDay(), stay);

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
                currentPlaceListFragment.setVisibility(mViewType, Constants.EmptyStatus.NOT_EMPTY, true);
                currentPlaceListFragment.setPlaceCuration(mStayCategoryCuration);
                currentPlaceListFragment.refreshList(true);
            } else
            {
                placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NOT_EMPTY, false);
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
    // Deep Link - 해당 경우는 고려하지 않아도 되나 임시로 넣어 둠
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean moveDeepLinkShortcutList(List<StayAreaGroup> stayDistrictList, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                int provinceIndex = externalDeepLink.getProvinceIndex();
                int areaIndex = externalDeepLink.getAreaIndex();

                // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
                StayRegion region = searchRegion(stayDistrictList, provinceIndex, areaIndex);

                if (region == null)
                {
                    return false;
                }

                mStayCategoryCuration.setRegion(region);

                StayBookingDay stayBookingDay = externalDeepLink.getStayBookDateTime(todayDateTime.getCommonDateTime(), externalDeepLink).getStayBookingDay();
                mStayCategoryCuration.setStayBookingDay(stayBookingDay);

                ((StayCategoryTabLayout) mPlaceMainLayout).setToolbarDateText(stayBookingDay);

                ArrayList<Category> categoryList = new ArrayList<>();
                categoryList.add(mStayCategoryCuration.getCategory());

                mPlaceMainLayout.setToolbarRegionText(region.getAreaName());
                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), categoryList, //
                    mStayCategoryCuration.getCategory(), mStayCategoryListFragmentListener);
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
