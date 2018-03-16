package com.twoheart.dailyhotel.screen.gourmet.list;

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
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.repository.local.CartLocalImpl;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.payment.GourmetPaymentActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainActivity;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.screen.gourmet.region.GourmetRegionListActivity;
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
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class GourmetMainActivity extends PlaceMainActivity
{
    GourmetCuration mGourmetCuration;
    DailyDeepLink mDailyDeepLink;

    CartLocalImpl mCartLocalImpl;

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, GourmetMainActivity.class);

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

        mGourmetCuration = new GourmetCuration();

        mCartLocalImpl = new CartLocalImpl(this);

        Intent intent = getIntent();

        initDeepLink(intent);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        addCompositeDisposable(mCartLocalImpl.getGourmetCartTotalCount().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>()
        {
            @Override
            public void accept(Integer count) throws Exception
            {
                if (mPlaceMainLayout == null)
                {
                    return;
                }

                if (count > 0)
                {
                    mPlaceMainLayout.setToolbarCartMenusVisible(true);
                    mPlaceMainLayout.setToolbarCartMenusCount(count);
                } else
                {
                    mPlaceMainLayout.setToolbarCartMenusVisible(false);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mPlaceMainLayout.setToolbarCartMenusVisible(false);
            }
        }));
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

                if (externalDeepLink.isGourmetDetailView() == true && moveDeepLinkDetail(externalDeepLink) == true)
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
            AnalyticsManager.Action.GOURMET_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
    }

    @Override
    protected PlaceMainLayout getPlaceMainLayout(Context context)
    {
        return new GourmetMainLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceMainNetworkController getPlaceMainNetworkController(Context context)
    {
        return new GourmetMainNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void onRegionActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
            {
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();
                gourmetCurationOption.clear();

                Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
                mGourmetCuration.setProvince(province);

                mPlaceMainLayout.setToolbarRegionText(province.name);
                mPlaceMainLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);

                PreferenceRegion preferenceRegion = getPreferenceRegion(DailyCategoryType.GOURMET_ALL);

                if (preferenceRegion == null || preferenceRegion.equalsArea(province) == false)
                {
                    setPreferenceRegion(DailyCategoryType.GOURMET_ALL, province);

                    String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                    String realProvinceName = Util.getRealProvinceName(province);
                    AnalyticsManager.getInstance(this).onRegionChanged(country, realProvinceName);
                }

                refreshCurrentFragment(true);
            }
        } else if (resultCode == RESULT_ARROUND_SEARCH_LIST && data != null)
        {
            // 검색 결과 화면으로 이동한다.
            String region = data.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
            String callByScreen = AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC;

            if (PlaceRegionListActivity.Region.DOMESTIC.name().equalsIgnoreCase(region) == true)
            {
                callByScreen = AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC;
            }

            startAroundSearchResult(this, mTodayDateTime, mGourmetCuration.getGourmetBookingDay(), callByScreen);
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
            GourmetBookingDay gourmetBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (gourmetBookingDay == null)
            {
                return;
            }

            mGourmetCuration.setGourmetBookingDay(gourmetBookingDay);
            ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(gourmetBookingDay);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

            if (placeCuration instanceof GourmetCuration == false)
            {
                return;
            }

            GourmetCuration changedGourmetCuration = (GourmetCuration) placeCuration;
            GourmetCurationOption changedGourmetCurationOption = (GourmetCurationOption) changedGourmetCuration.getCurationOption();

            mGourmetCuration.setCurationOption(changedGourmetCurationOption);
            mPlaceMainLayout.setOptionFilterSelected(changedGourmetCurationOption.isDefaultFilter() == false);

            if (changedGourmetCurationOption.getSortType() == SortType.DISTANCE)
            {
                mGourmetCuration.setLocation(changedGourmetCuration.getLocation());

                if (mGourmetCuration.getLocation() != null)
                {
                    lockUI();

                    onLocationChanged(mGourmetCuration.getLocation());
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
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        if (gourmetCurationOption.getSortType() == SortType.DISTANCE)
        {
            if (location == null)
            {
                if (mGourmetCuration.getLocation() != null)
                {
                    refreshCurrentFragment(true);
                } else
                {
                    DailyToast.showToast(this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);

                    gourmetCurationOption.setSortType(SortType.DEFAULT);
                    refreshCurrentFragment(true);
                }
            } else
            {
                mGourmetCuration.setLocation(location);
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

        Intent intent = GourmetCalendarActivity.newInstance(this, todayDateTime //
            , mGourmetCuration.getGourmetBookingDay(), GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , callByScreen, true, true);

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    private void startAroundSearchResult(Context context, TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay, String callByScreen)
    {
        if (todayDateTime == null || gourmetBookingDay == null || isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        GourmetSuggestV2.Location suggestItem = new GourmetSuggestV2.Location();
        suggestItem.name = getString(R.string.label_search_nearby_empty_address);
        suggestItem.address = getString(R.string.label_search_nearby_empty_address);

        GourmetSuggestV2 suggest = new GourmetSuggestV2(GourmetSuggestV2.MenuType.REGION_LOCATION, suggestItem);

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(context, gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
            , suggest, null), CODE_REQUEST_ACTIVITY_SEARCH_RESULT);

        //        Intent intent = GourmetSearchResultActivity.newInstance(context, todayDateTime, gourmetBookingDay, null, gourmetSuggest, null, callByScreen);
        //        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mGourmetCuration;
    }

    void recordAnalyticsGourmetList(String screen)
    {
        if (AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        GourmetBookingDay gourmetBookingDay = mGourmetCuration.getGourmetBookingDay();
        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
        params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");

        if (DailyHotel.isLogin() == false)
        {
            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
        } else
        {
            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
        }

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.FILTER, mGourmetCuration.getCurationOption().toAdjustString());

        Province province = mGourmetCuration.getProvince();

        if (province == null)
        {
            Util.restartApp(this);
            return;
        }

        if (province instanceof Area)
        {
            Area area = (Area) province;
            params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else
        {
            params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
        }

        AnalyticsManager.getInstance(this).recordScreen(this, screen, null, params);
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
            Util.restartApp(GourmetMainActivity.this);
            return;
        }

        lockUI();

        GourmetListFragment gourmetListFragment = (GourmetListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();

        switch (mViewType)
        {
            case LIST:
            {
                // 맵리스트 진입시에 솔드아웃은 맵에서 보여주지 않기 때문에 맵으로 진입시에 아무것도 볼수 없다.
                if (gourmetListFragment.hasSalesPlace() == false)
                {
                    unLockUI();

                    DailyToast.showToast(GourmetMainActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                    return;
                }

                mViewType = ViewType.MAP;

                AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_MAP, null);
                break;
            }

            case MAP:
            {
                mViewType = ViewType.LIST;

                AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_LIST_, null);
                break;
            }
        }

        // 고메는 리스트를 한번에 받기 때문에 계속 요청할 필요는 없다.
        mPlaceMainLayout.setOptionViewTypeView(mViewType);

        for (PlaceListFragment placeListFragment : mPlaceMainLayout.getPlaceListFragment())
        {
            boolean isCurrentFragment = placeListFragment == gourmetListFragment;
            placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NONE, isCurrentFragment);
        }

        refreshCurrentFragment(false);

        unLockUI();
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mOnPlaceListFragmentListener == null)
        {
            return;
        }

        mOnPlaceListFragmentListener.onGourmetClick(view, placeViewItem, listCount);
    }

    @Override
    protected void onRegionClick()
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Province province = mGourmetCuration.getProvince();

        Intent intent = GourmetRegionListActivity.newInstance(GourmetMainActivity.this, province, mGourmetCuration.getGourmetBookingDay());
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        switch (mViewType)
        {
            case LIST:
                AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._GOURMET_LIST_, null);
                break;

            case MAP:
                AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label._GOURMET_MAP, null);
                break;
        }
    }

    void startPayment(GourmetCart gourmetCart)
    {
        if (gourmetCart == null || gourmetCart.getMenuCount() == 0)
        {
            return;
        }

        startActivity(GourmetPaymentActivity.newInstance(this, gourmetCart, null));

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.SHORTCUT_ORDER, Integer.toString(gourmetCart.gourmetIndex), null);
    }

    PreferenceRegion getPreferenceRegion(DailyCategoryType categoryType)
    {
        return DailyPreference.getInstance(this).getDailyRegion(categoryType);
    }

    void setPreferenceRegion(DailyCategoryType categoryType, Province province)
    {
        try
        {
            DailyPreference.getInstance(this).setDailyRegion(categoryType, new PreferenceRegion(province));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
    {
        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            // Gourmet은 카테고리가 없음.
        }

        @Override
        public void onCategoryTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onCategoryTabReselected(TabLayout.Tab tab)
        {
            setScrollListTop();
        }

        @Override
        public void onSearchClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = SearchActivity.newInstance(GourmetMainActivity.this, ServiceType.GOURMET, mGourmetCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT));
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.GOURMET_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.GOURMET_MAP_VIEW, null);
                    break;
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
            GourmetMainActivity.this.onRegionClick();
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

            Province province = mGourmetCuration.getProvince();

            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = GourmetCurationActivity.newInstance(GourmetMainActivity.this, mViewType, mGourmetCuration);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMETCURATION);

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

            AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, viewType, null);
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
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            addCompositeDisposable(mCartLocalImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
            {
                @Override
                public void accept(GourmetCart gourmetCart) throws Exception
                {
                    startPayment(gourmetCart);

                    unLockUI();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockUI();
                }
            }));
        }

        @Override
        public void finish()
        {
            GourmetMainActivity.this.finish();
        }
    };

    private GourmetMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetMainNetworkController.OnNetworkControllerListener()
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
                GourmetBookingDay gourmetBookingDay = mGourmetCuration.getGourmetBookingDay();

                if (gourmetBookingDay == null)
                {
                    gourmetBookingDay = new GourmetBookingDay();

                    gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);

                    mGourmetCuration.setGourmetBookingDay(gourmetBookingDay);
                } else
                {
                    // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                    // 체크인 날짜 체크

                    // 날짜로 비교해야 한다.
                    Calendar todayCalendar = DailyCalendar.getInstance(mTodayDateTime.dailyDateTime, true);
                    Calendar visitCalendar = DailyCalendar.getInstance(gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT), true);

                    // 하루가 지나서 체크인 날짜가 전날짜 인 경우
                    if (todayCalendar.getTimeInMillis() > visitCalendar.getTimeInMillis())
                    {
                        gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);
                    }
                }

                if (mDailyDeepLink != null && processDeepLinkByDateTime(GourmetMainActivity.this, mTodayDateTime, mDailyDeepLink) == true)
                {
                    // 딥링크 이동
                } else
                {
                    ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(mGourmetCuration.getGourmetBookingDay());

                    ((GourmetMainNetworkController) mPlaceMainNetworkController).requestRegionList();
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
            if (isFinishing() == true || provinceList == null || areaList == null)
            {
                return;
            }

            Province selectedProvince = mGourmetCuration.getProvince();

            if (selectedProvince == null)
            {
                selectedProvince = searchLastRegion(GourmetMainActivity.this, provinceList, areaList);
            }

            // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
            if (selectedProvince == null)
            {
                selectedProvince = provinceList.get(0);
            }

            // 마지막으로 지역이 Area로 되어있으면 Province
            if (selectedProvince instanceof Area)
            {
                int provinceIndex = selectedProvince.getProvinceIndex();

                for (Province province : provinceList)
                {
                    if (province.getProvinceIndex() == provinceIndex)
                    {
                        setPreferenceRegion(DailyCategoryType.GOURMET_ALL, province);

                        String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                        String realProvinceName = Util.getRealProvinceName(province);
                        AnalyticsManager.getInstance(GourmetMainActivity.this).onRegionChanged(country, realProvinceName);
                        break;
                    }
                }
            } else
            {
                String country = selectedProvince.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                AnalyticsManager.getInstance(GourmetMainActivity.this).onRegionChanged(country, selectedProvince.name);
            }

            mGourmetCuration.setProvince(selectedProvince);

            if (mDailyDeepLink != null && processDeepLinkByRegionList(GourmetMainActivity.this, provinceList, areaList, mTodayDateTime, mDailyDeepLink) == true)
            {

            } else
            {
                // 리스트 요청하면 됨.
                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
                mPlaceMainLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), //
                    null, mOnPlaceListFragmentListener);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetMainActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetMainActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetMainActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetMainActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetMainActivity.this.onErrorResponse(call, response);
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

                if (externalDeepLink.isGourmetListView() == false)
                {
                    externalDeepLink.clear();
                }
            }

            return false;
        }

        private boolean processDeepLinkByRegionList(BaseActivity baseActivity, List<Province> provinceList, List<Area> areaList, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
        {
            if (dailyDeepLink == null)
            {
                return false;
            }

            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                if (externalDeepLink.isGourmetListView() == true)
                {
                    unLockUI();

                    return moveDeepLinkGourmetList(provinceList, areaList, todayDateTime, externalDeepLink);
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
            Province selectedProvince = null;

            String provinceName = null;
            String areaName = null;
            String regionName;

            // 마지막으로 선택한 지역을 가져온다. - old and new 추후 2.0.4로 강업 이후 Old 부분 삭제 필요
            PreferenceRegion preferenceRegion = getPreferenceRegion(DailyCategoryType.GOURMET_ALL);
            if (preferenceRegion != null)
            {
                // new version preference value 사용
                areaName = preferenceRegion.areaName;
                provinceName = preferenceRegion.areaGroupName;
            }

            // Api 구조상 province 내에 area가 존재하지 않고 독립적이기때문에 작은단위로 찾아야 함
            regionName = DailyTextUtils.isTextEmpty(areaName) == true ? provinceName : areaName;

            if (DailyTextUtils.isTextEmpty(regionName) == true)
            {
                selectedProvince = provinceList.get(0);
            }

            if (selectedProvince == null)
            {
                for (Province province : provinceList)
                {
                    if (province.name.equals(regionName) == true)
                    {
                        selectedProvince = province;
                        break;
                    }
                }

                if (selectedProvince == null)
                {
                    for (Area area : areaList)
                    {
                        if (area.name.equals(regionName) == true)
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

                            selectedProvince = area;
                            break;
                        }
                    }
                }
            }

            return selectedProvince;
        }
    };

    GourmetListFragment.OnGourmetListFragmentListener mOnPlaceListFragmentListener = new GourmetListFragment.OnGourmetListFragmentListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGourmetClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (isFinishing() == true || placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Gourmet gourmet = placeViewItem.getItem();
                    Province province = mGourmetCuration.getProvince();

                    PreferenceRegion preferenceRegion = getPreferenceRegion(DailyCategoryType.GOURMET_ALL);

                    if (preferenceRegion == null || preferenceRegion.equalsArea(province) == false)
                    {
                        setPreferenceRegion(DailyCategoryType.GOURMET_ALL, province);

                        String country = province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC;
                        String realProvinceName = Util.getRealProvinceName(province);
                        AnalyticsManager.getInstance(GourmetMainActivity.this).onRegionChanged(country, realProvinceName);
                    }

                    // --> 추후에 정리되면 메소드로 수정
                    GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
                    analyticsParam.price = gourmet.price;
                    analyticsParam.discountPrice = gourmet.discountPrice;
                    analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
                    analyticsParam.setProvince(province);
                    analyticsParam.entryPosition = gourmet.entryPosition;
                    analyticsParam.totalListCount = listCount;
                    analyticsParam.isDailyChoice = gourmet.isDailyChoice;
                    analyticsParam.setAddressAreaName(gourmet.addressSummary);

                    // <-- 추후에 정리되면 메소드로 수정

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

                        View simpleDraweeView = view.findViewById(R.id.imageView);
                        View nameTextView = view.findViewById(R.id.nameTextView);
                        View gradientTopView = view.findViewById(R.id.gradientTopView);
                        View gradientBottomView = view.findViewById(R.id.gradientView);

                        ActivityOptionsCompat optionsCompat;
                        Intent intent;

                        if (view instanceof DailyGourmetCardView == true)
                        {
                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(GourmetMainActivity.this, ((DailyGourmetCardView) view).getOptionsCompat());

                            intent = GourmetDetailActivity.newInstance(GourmetMainActivity.this //
                                , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                                , mGourmetCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                                , gourmet.category, gourmet.isSoldOut, false, false, true//
                                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                                , analyticsParam);
                        } else
                        {
                            intent = GourmetDetailActivity.newInstance(GourmetMainActivity.this //
                                , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                                , mGourmetCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                                , gourmet.category, gourmet.isSoldOut, false, false, true//
                                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP//
                                , analyticsParam);

                            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(GourmetMainActivity.this,//
                                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
                        }

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
                    } else
                    {
                        Intent intent = GourmetDetailActivity.newInstance(GourmetMainActivity.this //
                            , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                            , mGourmetCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                            , gourmet.category, gourmet.isSoldOut, false, false, false//
                            , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                            , analyticsParam);

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }

                    if (mViewType == ViewType.LIST)
                    {
                        AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.GOURMET_ITEM_CLICK, Integer.toString(gourmet.index), null);

                        AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , gourmet.isDailyChoice ? AnalyticsManager.Action.GOURMET_DAILYCHOICE_CLICK_Y : AnalyticsManager.Action.GOURMET_DAILYCHOICE_CLICK_N, Integer.toString(gourmet.index), null);

                        // 할인 쿠폰이 보이는 경우
                        if (DailyTextUtils.isTextEmpty(gourmet.couponDiscountText) == false)
                        {
                            AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.COUPON_GOURMET, Integer.toString(gourmet.index), null);
                        }

                        if (gourmet.reviewCount > 0)
                        {
                            AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.TRUE_REVIEW_GOURMET, Integer.toString(gourmet.index), null);
                        }

                        if (gourmet.discountRate > 0)
                        {
                            AnalyticsManager.getInstance(GourmetMainActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                                , AnalyticsManager.Action.DISCOUNT_GOURMET, Integer.toString(gourmet.index), null);
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
        public void onGourmetLongClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (isFinishing() == true || placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    mPlaceMainLayout.setBlurVisibility(GourmetMainActivity.this, true);

                    // 기존 데이터를 백업한다.
                    mViewByLongPress = view;
                    mPlaceViewItemByLongPress = placeViewItem;
                    mListCountByLongPress = listCount;

                    Gourmet gourmet = placeViewItem.getItem();
                    Intent intent = GourmetPreviewActivity.newInstance(GourmetMainActivity.this, mGourmetCuration.getGourmetBookingDay(), gourmet);

                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
                    break;
                }

                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void onGourmetCategoryFilter(int page, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
        {
            if (page <= 1 && mGourmetCuration.getCurationOption().isDefaultFilter() == true)
            {
                ((GourmetCurationOption) mGourmetCuration.getCurationOption()).setCategoryCoderMap(categoryCodeMap);
                ((GourmetCurationOption) mGourmetCuration.getCurationOption()).setCategorySequenceMap(categorySequenceMap);
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
                currentPlaceListFragment.setPlaceCuration(mGourmetCuration);
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
                        GourmetListAdapter gourmetListAdapter = (GourmetListAdapter) recyclerView.getAdapter();

                        if (gourmetListAdapter != null)
                        {
                            int count = gourmetListAdapter.getItemCount();

                            if (count == 0)
                            {
                            } else
                            {
                                PlaceViewItem placeViewItem = gourmetListAdapter.getItem(gourmetListAdapter.getItemCount() - 1);

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
                    recordAnalyticsGourmetList(AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP);
                } else
                {
                    recordAnalyticsGourmetList(AnalyticsManager.Screen.DAILYGOURMET_LIST);
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
            Intent intent = GourmetDetailActivity.newInstance(this, dailyDeepLink.getDeepLink());
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

            overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

            mIsDeepLink = true;
            //            } else
            //            {
            //
            //            }
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

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, //
                                         List<Province> provinceList, List<Area> areaList)
    {
        if (provinceIndex <= 0 && areaIndex <= 0)
        {
            return null;
        }

        Province selectedProvince = null;

        try
        {
            if (areaIndex <= 0)
            {
                // 전체 지역으로 이동
                for (Province province : provinceList)
                {
                    if (province.index == provinceIndex)
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

    boolean moveDeepLinkGourmetList(List<Province> provinceList, List<Area> areaList, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
    {
        if (todayDateTime == null || dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();
                gourmetCurationOption.setSortType(externalDeepLink.getSorting());

                mPlaceMainLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);

                int provinceIndex = externalDeepLink.getProvinceIndex();
                int areaIndex = externalDeepLink.getAreaIndex();

                // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
                Province selectedProvince = searchDeeLinkRegion(provinceIndex, areaIndex, provinceList, areaList);

                if (selectedProvince == null)
                {
                    selectedProvince = mGourmetCuration.getProvince();
                }

                mGourmetCuration.setProvince(selectedProvince);
                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);

                GourmetBookingDay gourmetBookingDay = externalDeepLink.getGourmetBookDateTime(todayDateTime.getCommonDateTime(), externalDeepLink).getGourmetBookingDay();
                mGourmetCuration.setGourmetBookingDay(gourmetBookingDay);

                ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(gourmetBookingDay);

                ((GourmetMainNetworkController) mPlaceMainNetworkController).requestRegionList();
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
