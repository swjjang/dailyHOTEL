package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;
import com.twoheart.dailyhotel.screen.hotel.region.StayRegionListActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StayMainFragment extends PlaceMainFragment
{
    private StayCuration mStayCuration;

    public StayMainFragment()
    {
        mStayCuration = new StayCuration();
    }

    @Override
    protected PlaceMainLayout getPlaceMainLayout(Context context)
    {
        return new StayMainLayout(mBaseActivity, mOnEventListener);
    }

    @Override
    protected PlaceMainNetworkController getPlaceMainNetworkController(Context context)
    {
        return new StayMainNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
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
                mStayCuration.setCategory(Category.ALL);

                mPlaceMainLayout.setToolbarRegionText(province.name);
                mPlaceMainLayout.setOptionFilterEnabled(stayCurationOption.isDefaultFilter() == false);

                // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
                String savedRegion = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.HOTEL);

                if (province.name.equalsIgnoreCase(savedRegion) == false)
                {
                    DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.HOTEL, province.isOverseas);
                    DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.HOTEL, province.name);
                }

                mPlaceMainLayout.setCategoryTabLayout(getChildFragmentManager(), province.getCategoryList(), //
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
                SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
                int nights = data.getIntExtra(StayRegionListActivity.INTENT_EXTRA_DATA_NIGHTS, 1);

                mStayCuration.setProvince(province);
                mStayCuration.setCategory(Category.ALL);

                mPlaceMainLayout.setToolbarRegionText(province.name);
                mPlaceMainLayout.setOptionFilterEnabled(stayCurationOption.isDefaultFilter() == false);

                // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
                String savedRegion = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.HOTEL);

                if (province.name.equalsIgnoreCase(savedRegion) == false)
                {
                    DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.HOTEL, province.isOverseas);
                    DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.HOTEL, province.name);
                }

                SaleTime checkOutSaleTime = checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + nights);

                mStayCuration.setCheckInSaleTime(checkInSaleTime);
                mStayCuration.setCheckOutSaleTime(checkOutSaleTime);

                ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(checkInSaleTime, checkOutSaleTime);

                startCalendar(AnalyticsManager.Label.CHANGE_LOCATION);

                mPlaceMainLayout.setCategoryTabLayout(getChildFragmentManager(), province.getCategoryList(), //
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

                startAroundSearchResult(mBaseActivity, mStayCuration.getCheckInSaleTime(), mStayCuration.getNights(), location, callByScreen);
            }
        }
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
            SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

            if (checkInSaleTime == null || checkOutSaleTime == null)
            {
                return;
            }

            mStayCuration.setCheckInSaleTime(checkInSaleTime);
            mStayCuration.setCheckOutSaleTime(checkOutSaleTime);

            ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(checkInSaleTime, checkOutSaleTime);

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
            mPlaceMainLayout.setOptionFilterEnabled(changedStayCurationOption.isDefaultFilter() == false);

            if (changedStayCurationOption.getSortType() == SortType.DISTANCE)
            {
                mStayCuration.setLocation(changedStayCuration.getLocation());

                searchMyLocation();
            } else
            {
                refreshCurrentFragment(true);
            }
        }
    }

    @Override
    protected void onLocationFailed()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterEnabled(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterEnabled(stayCurationOption.isDefaultFilter() == false);

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
                    DailyToast.showToast(mBaseActivity, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);

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

    private void startCalendar(String callByScreen)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
        int nights = mStayCuration.getNights();

        Intent intent = StayCalendarActivity.newInstance(getContext(), checkInSaleTime, nights, callByScreen, true, true);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    private void startAroundSearchResult(Context context, SaleTime saleTime, int nights, Location location, String callByScreen)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        Intent intent = StaySearchResultActivity.newInstance(context, saleTime, nights, location, callByScreen);
        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mStayCuration;
    }

    private void recordAnalyticsStayList(String screen)
    {
        if (AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.CHECK_IN, mStayCuration.getCheckInSaleTime().getDayOfDaysDateFormat("yyyy-MM-dd"));
        params.put(AnalyticsManager.KeyType.CHECK_OUT, mStayCuration.getCheckOutSaleTime().getDayOfDaysDateFormat("yyyy-MM-dd"));

        if (DailyHotel.isLogin() == false)
        {
            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
        } else
        {
            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
        }

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.HOTEL);
        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.HOTEL);
        params.put(AnalyticsManager.KeyType.CATEGORY, mStayCuration.getCategory().code);

        Province province = mStayCuration.getProvince();

        if (province == null)
        {
            Util.restartApp(getContext());
            return;
        }

        if (province instanceof Area)
        {
            Area area = (Area) province;
            params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else if (province != null)
        {
            params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordScreen(screen, params);
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
            mStayCuration.setCategory(category);

            mPlaceMainLayout.setCurrentItem(tab.getPosition());
            mPlaceMainLayout.showBottomLayout(false);

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

            Intent intent = SearchActivity.newInstance(mBaseActivity, PlaceType.HOTEL, mStayCuration.getCheckInSaleTime(), mStayCuration.getNights());
            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_MAP, null);
                    break;
            }
        }

        @Override
        public void onDateClick()
        {
            startCalendar(AnalyticsManager.ValueType.LIST);
        }

        @Override
        public void onRegionClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
            int night = mStayCuration.getNights();

            Intent intent = StayRegionListActivity.newInstance(getContext(), //
                mStayCuration.getProvince(), checkInSaleTime, night);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label.HOTEL_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label.HOTEL_MAP, null);
                    break;
            }
        }

        @Override
        public void onViewTypeClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mPlaceMainLayout.getPlaceListFragment() == null)
            {
                Util.restartApp(mBaseActivity);
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

                        DailyToast.showToast(mBaseActivity, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label.HOTEL_MAP, null);
                    break;
                }

                case MAP:
                {
                    mViewType = ViewType.LIST;

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label.HOTEL_LIST, null);
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

            Intent intent = StayCurationActivity.newInstance(mBaseActivity, mViewType, mStayCuration);
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
        }

        @Override
        public void onMenuBarTranslationY(float y)
        {
            if (mOnMenuBarListener != null)
            {
                mOnMenuBarListener.onMenuBarTranslationY(y);
            }
        }

        @Override
        public void onMenuBarEnabled(boolean enabled)
        {
            if (mOnMenuBarListener != null)
            {
                mOnMenuBarListener.onMenuBarEnabled(enabled);
            }
        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();
        }
    };

    private PlaceMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceMainNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onDateTime(long currentDateTime, long dailyDateTime)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                mStayCuration.setCheckInSaleTime(currentDateTime, dailyDateTime);

                SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
                mStayCuration.setCheckOutSaleTime(checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + 1));

                String lastViewDate = DailyPreference.getInstance(mBaseActivity).getStayLastViewDate();

                if (Util.isTextEmpty(lastViewDate) == false)
                {
                    DailyPreference.getInstance(mBaseActivity).setStayLastViewDate(null);

                    String[] lastViewDates = lastViewDate.split("\\,");
                    int nights = 1;

                    try
                    {
                        nights = Integer.parseInt(lastViewDates[1]);
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

                    checkInSaleTime = SaleTime.changeDateSaleTime(checkInSaleTime, lastViewDates[0]);

                    if (checkInSaleTime != null)
                    {
                        mStayCuration.setCheckInSaleTime(checkInSaleTime);
                        mStayCuration.setCheckOutSaleTime(checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + nights));
                    }
                }

                if (DailyDeepLink.getInstance().isValidateLink() == true //
                    && processDeepLinkByDateTime(mBaseActivity) == true)
                {

                } else
                {
                    ((StayMainLayout) mPlaceMainLayout).setToolbarDateText( //
                        mStayCuration.getCheckInSaleTime(), //
                        mStayCuration.getCheckOutSaleTime());

                    mPlaceMainNetworkController.requestEventBanner();
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
            StayEventBannerManager.getInstance().setList(eventBannerList);

            mPlaceMainNetworkController.requestRegionList();
        }

        @Override
        public void onRegionList(List<Province> provinceList, List<Area> areaList)
        {
            if (isFinishing() == true || provinceList == null || areaList == null)
            {
                return;
            }

            if (mStayCuration.getCheckInSaleTime() == null//
                || mStayCuration.getCheckOutSaleTime() == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            Province selectedProvince = mStayCuration.getProvince();

            if (selectedProvince == null)
            {
                selectedProvince = searchLastRegion(mBaseActivity, provinceList, areaList);
            }

            // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
            if (selectedProvince == null)
            {
                selectedProvince = provinceList.get(0);
            }

            // 처음 시작시에는 지역이 Area로 저장된 경우 Province로 변경하기 위한 저장값.
            boolean mIsProvinceSetting = DailyPreference.getInstance(mBaseActivity).isSettingRegion(PlaceType.HOTEL);
            DailyPreference.getInstance(mBaseActivity).setSettingRegion(PlaceType.HOTEL, true);

            // 마지막으로 지역이 Area로 되어있으면 Province로 바꾸어 준다.
            if (mIsProvinceSetting == false && selectedProvince instanceof Area)
            {
                int provinceIndex = selectedProvince.getProvinceIndex();

                for (Province province : provinceList)
                {
                    if (province.getProvinceIndex() == provinceIndex)
                    {
                        selectedProvince = province;
                        DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.HOTEL, province.isOverseas);
                        DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.HOTEL, province.name);
                        break;
                    }
                }
            }

            mStayCuration.setProvince(selectedProvince);

            if (DailyDeepLink.getInstance().isValidateLink() == true//
                && processDeepLinkByRegionList(mBaseActivity, provinceList, areaList) == true)
            {

            } else
            {
                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
                mPlaceMainLayout.setCategoryTabLayout(getChildFragmentManager(), selectedProvince.getCategoryList(), //
                    mStayCuration.getCategory(), mStayListFragmentListener);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayMainFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            StayMainFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayMainFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayMainFragment.this.onErrorToastMessage(message);
        }

        private boolean processDeepLinkByDateTime(BaseActivity baseActivity)
        {
            if (DailyDeepLink.getInstance().isHotelDetailView() == true)
            {
                unLockUI();

                return moveDeepLinkDetail(baseActivity);
            } else if (DailyDeepLink.getInstance().isHotelEventBannerWebView() == true)
            {
                unLockUI();

                return moveDeepLinkEventBannerWeb(baseActivity);
            } else if (DailyDeepLink.getInstance().isHotelRegionListView() == true)
            {
                unLockUI();

                return moveDeepLinkRegionList(baseActivity);
            } else if (DailyDeepLink.getInstance().isHotelSearchView() == true)
            {
                unLockUI();

                return moveDeepLinkSearch(baseActivity);
            } else if (DailyDeepLink.getInstance().isHotelSearchResultView() == true)
            {
                unLockUI();

                return moveDeepLinkSearchResult(baseActivity);
            } else
            {
                // 더이상 진입은 없다.
                if (DailyDeepLink.getInstance().isHotelListView() == false)
                {
                    DailyDeepLink.getInstance().clear();
                }
            }

            return false;
        }

        private boolean processDeepLinkByRegionList(BaseActivity baseActivity, //
                                                    List<Province> provinceList, //
                                                    List<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isHotelListView() == true)
            {
                unLockUI();

                return moveDeepLinkStayList(provinceList, areaList);
            } else
            {
                DailyDeepLink.getInstance().clear();
            }

            return false;
        }

        private Province searchLastRegion(BaseActivity baseActivity, //
                                          List<Province> provinceList, //
                                          List<Area> areaList)
        {
            Province selectedProvince = null;

            // 마지막으로 선택한 지역을 가져온다.
            String regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.HOTEL);

            if (Util.isTextEmpty(regionName) == true)
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

    private StayListFragment.OnStayListFragmentListener mStayListFragmentListener = new StayListFragment.OnStayListFragmentListener()
    {
        @Override
        public void onStayClick(PlaceViewItem placeViewItem, int listCount)
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

                    String region = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.HOTEL);
                    DailyPreference.getInstance(mBaseActivity).setGASelectedRegion(region);
                    DailyPreference.getInstance(mBaseActivity).setGAHotelName(stay.name);

                    Intent intent = StayDetailActivity.newInstance(mBaseActivity, //
                        mStayCuration.getCheckInSaleTime(), mStayCuration.getProvince(), stay, listCount);

                    mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);

                    if (mViewType == ViewType.LIST)
                    {
                        String label = String.format("%s-%s", stay.categoryCode, stay.name);
                        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.HOTEL_ITEM_CLICKED, label, null);
                    }
                    break;
                }

                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
        {
            if (isFinishing())
            {
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_EVENT_BANNER_CLICKED, eventBanner.name, null);

            SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();

            // 이벤트 배너 딥링크 사용하지 않기로 했음.
            if (eventBanner.isDeepLink() == true)
            {
                //                try
                //                {
                //                    Calendar calendar = DailyCalendar.getInstance();
                //                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                //                    calendar.setTimeInMillis(eventBanner.checkInTime);
                //
                //                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                //                    Date schemeDate = format.parse(format.format(calendar.getTime()));
                //                    Date dailyDate = format.parse(checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
                //
                //                    int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);
                //
                //                    checkInSaleTime.setOffsetDailyDay(dailyDayOfDays);
                //
                //                    if (eventBanner.isHotel() == true)
                //                    {
                //                        startStayDetailByDeeplink(eventBanner.index, checkInSaleTime, eventBanner.nights);
                //                    } else
                //                    {
                //                        Intent intent = new Intent(mBaseActivity, GourmetDetailActivity.class);
                //
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, eventBanner.index);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, eventBanner.nights);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, 0);
                //
                //                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
                //                    }
                //                } catch (Exception e)
                //                {
                //                    ExLog.e(e.toString());
                //                }
            } else
            {
                Intent intent = EventWebActivity.newInstance(mBaseActivity, //
                    EventWebActivity.SourceType.HOTEL_BANNER, eventBanner.webLink, eventBanner.name, checkInSaleTime);
                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
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
            mPlaceMainLayout.calculationMenuBarLayoutTranslationY(dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            switch (newState)
            {
                case RecyclerView.SCROLL_STATE_IDLE:
                {
                    mPlaceMainLayout.animationMenuBarLayout();

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
                                    mPlaceMainLayout.showBottomLayout(false);
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
            mPlaceMainLayout.showBottomLayout(false);
        }

        @Override
        public void onFilterClick()
        {
            mOnEventListener.onFilterClick();
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {

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
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void startStayDetailByDeeplink(int hotelIndex, SaleTime saleTime, int nights, boolean isShowCalendar)
    {
        if (isFinishing() == true || hotelIndex < 0 || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        Intent intent = StayDetailActivity.newInstance(mBaseActivity, saleTime, nights, hotelIndex, isShowCalendar);

        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
    }

    private boolean moveDeepLinkDetail(BaseActivity baseActivity)
    {
        try
        {
            // 신규 타입의 화면이동
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            if (nights <= 0)
            {
                nights = 1;
            }

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();

            SaleTime changedSaleTime = mStayCuration.getCheckInSaleTime().getClone(0);

            if (Util.isTextEmpty(date) == false)
            {
                changedSaleTime = SaleTime.changeDateSaleTime(changedSaleTime, date);
            } else if (datePlus >= 0)
            {
                changedSaleTime.setOffsetDailyDay(datePlus);
            }

            if (changedSaleTime == null)
            {
                return false;
            }

            startStayDetailByDeeplink(hotelIndex, changedSaleTime, nights, isShowCalendar);

            mIsDeepLink = true;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
            return false;
        } finally
        {
            DailyDeepLink.getInstance().clear();
        }

        return true;
    }

    private boolean moveDeepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();

            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.HOTEL_BANNER, url, null, checkInSaleTime);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
            mIsDeepLink = true;

            return true;
        } else
        {
            return false;
        }
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

    private boolean moveDeepLinkRegionList(BaseActivity baseActivity)
    {
        int provinceIndex = -1;
        int areaIndex = -1;

        try
        {
            provinceIndex = Integer.parseInt(DailyDeepLink.getInstance().getProvinceIndex());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        try
        {
            areaIndex = Integer.parseInt(DailyDeepLink.getInstance().getAreaIndex());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        SaleTime checkInSaleTime = mStayCuration.getCheckInSaleTime();
        int night = checkInSaleTime.getOffsetDailyDay();

        Intent intent = StayRegionListActivity.newInstance(baseActivity, provinceIndex, areaIndex, checkInSaleTime, night);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
        mIsDeepLink = true;

        return true;
    }

    private boolean moveDeepLinkSearch(BaseActivity baseActivity)
    {
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();
        String word = DailyDeepLink.getInstance().getSearchWord();

        int nights = 1;

        try
        {
            nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());
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

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mStayCuration.getCheckInSaleTime().getClone(0);
        SaleTime checkInSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);

            if (checkInSaleTime == null)
            {
                return false;
            }
        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            // 날짜 정보가 없는 경우 예외 처리 추가
            try
            {
                checkInSaleTime = saleTime;
            } catch (Exception e)
            {
                return false;
            }
        }

        if (checkInSaleTime == null)
        {
            return false;
        }

        Intent intent = SearchActivity.newInstance(mBaseActivity, PlaceType.HOTEL, checkInSaleTime, nights, word);
        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        mIsDeepLink = true;

        return true;
    }

    private boolean moveDeepLinkSearchResult(BaseActivity baseActivity)
    {
        String word = DailyDeepLink.getInstance().getSearchWord();
        DailyDeepLink.SearchType searchType = DailyDeepLink.getInstance().getSearchLocationType();
        LatLng latLng = DailyDeepLink.getInstance().getLatLng();
        double radius = DailyDeepLink.getInstance().getRadius();

        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();
        int nights = 1;

        try
        {
            nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());
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

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mStayCuration.getCheckInSaleTime().getClone(0);
        SaleTime checkInSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);

            if (checkInSaleTime == null)
            {
                return false;
            }

        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            // 날짜 정보가 없는 경우 예외 처리 추가
            try
            {
                checkInSaleTime = saleTime;
            } catch (Exception e)
            {
                return false;
            }
        }

        if (checkInSaleTime == null)
        {
            return false;
        }

        switch (searchType)
        {
            case LOCATION:
            {
                if (latLng != null)
                {
                    Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, checkInSaleTime, nights, latLng, radius, true);
                    mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
            }

            default:
                if (Util.isTextEmpty(word) == false)
                {
                    Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, checkInSaleTime, nights, new Keyword(0, word), SearchType.SEARCHES);
                    mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
        }

        mIsDeepLink = true;

        return true;
    }

    private boolean moveDeepLinkStayList(List<Province> provinceList, List<Area> areaList)
    {
        String categoryCode = DailyDeepLink.getInstance().getCategoryCode();
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();

        StayCurationOption stayCurationOption = (StayCurationOption) mStayCuration.getCurationOption();
        stayCurationOption.setSortType(DailyDeepLink.getInstance().getSorting());

        mPlaceMainLayout.setOptionFilterEnabled(stayCurationOption.isDefaultFilter() == false);

        int nights = 1;
        int provinceIndex;
        int areaIndex;

        try
        {
            nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());
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
            provinceIndex = Integer.parseInt(DailyDeepLink.getInstance().getProvinceIndex());
        } catch (Exception e)
        {
            provinceIndex = -1;
        }

        try
        {
            areaIndex = Integer.parseInt(DailyDeepLink.getInstance().getAreaIndex());
        } catch (Exception e)
        {
            areaIndex = -1;
        }

        boolean isOverseas = DailyDeepLink.getInstance().getIsOverseas();

        // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
        Province selectedProvince = searchDeeLinkRegion(provinceIndex, areaIndex, isOverseas, provinceList, areaList);

        if (selectedProvince == null)
        {
            selectedProvince = mStayCuration.getProvince();
        }

        mStayCuration.setProvince(selectedProvince);

        mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);

        // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
        if (Util.isTextEmpty(categoryCode) == false)
        {
            for (Category category : selectedProvince.getCategoryList())
            {
                if (category.code.equalsIgnoreCase(categoryCode) == true)
                {
                    mStayCuration.setCategory(category);
                    break;
                }
            }
        }

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mStayCuration.getCheckInSaleTime().getClone(0);
        SaleTime checkInSaleTime;
        SaleTime checkOutSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);

            if (checkInSaleTime == null)
            {
                return false;
            }

            checkOutSaleTime = checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + nights);
        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
                checkOutSaleTime = checkInSaleTime.getClone(datePlus + nights);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            // 날짜 정보가 없는 경우 예외 처리 추가
            try
            {
                checkInSaleTime = saleTime;
                checkOutSaleTime = checkInSaleTime.getClone(nights);
            } catch (Exception e)
            {
                return false;
            }
        }

        if (checkInSaleTime == null || checkOutSaleTime == null)
        {
            return false;
        }

        mStayCuration.setCheckInSaleTime(checkInSaleTime);
        mStayCuration.setCheckOutSaleTime(checkOutSaleTime);

        ((StayMainLayout) mPlaceMainLayout).setToolbarDateText(checkInSaleTime, checkOutSaleTime);

        mPlaceMainNetworkController.requestRegionList();

        return true;
    }
}
