package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.region.GourmetRegionListActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.collection.CollectionGourmetActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetMainFragment extends PlaceMainFragment
{
    private GourmetCuration mGourmetCuration;

    public GourmetMainFragment()
    {
        mGourmetCuration = new GourmetCuration();
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
                mPlaceMainLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

                String savedRegion = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.FNB);

                if (province.name.equalsIgnoreCase(savedRegion) == false)
                {
                    DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.FNB, province.isOverseas);
                    DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.FNB, province.name);

                    String country = province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;
                    String realProvinceName = Util.getRealProvinceName(province);
                    DailyPreference.getInstance(mBaseActivity).setSelectedRegionTypeProvince(PlaceType.FNB, realProvinceName);
                    AnalyticsManager.getInstance(mBaseActivity).onRegionChanged(country, realProvinceName);
                }

                refreshCurrentFragment(true);
            }
        } else if (resultCode == RESULT_ARROUND_SEARCH_LIST && data != null)
        {
            // 검색 결과 화면으로 이동한다.
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_LOCATION) == true)
            {
                Location location = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_LOCATION);
                mGourmetCuration.setLocation(location);

                String region = data.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                String callByScreen = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;

                if (PlaceRegionListActivity.Region.DOMESTIC.name().equalsIgnoreCase(region) == true)
                {
                    callByScreen = AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC;
                }

                startAroundSearchResult(mBaseActivity, mGourmetCuration.getSaleTime(), location, callByScreen);
            }
        }
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            SaleTime saleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (saleTime == null)
            {
                return;
            }

            mGourmetCuration.setSaleTime(saleTime);
            ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(saleTime);

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
            mPlaceMainLayout.setOptionFilterEnabled(changedGourmetCurationOption.isDefaultFilter() == false);

            if (changedGourmetCurationOption.getSortType() == SortType.DISTANCE)
            {
                mGourmetCuration.setLocation(changedGourmetCuration.getLocation());

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
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceMainLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

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
                    DailyToast.showToast(mBaseActivity, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);

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

    private void startCalendar(String callByScreen)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = GourmetCalendarActivity.newInstance(getContext(), mGourmetCuration.getSaleTime(), callByScreen, true, true);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
    }

    private void startAroundSearchResult(Context context, SaleTime saleTime, Location location, String callByScreen)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, saleTime, location, callByScreen);
        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mGourmetCuration;
    }

    private void recordAnalyticsGourmetList(String screen)
    {
        if (AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.CHECK_IN, mGourmetCuration.getSaleTime().getDayOfDaysDateFormat("yyyy-MM-dd"));
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
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

        }

        @Override
        public void onSearchClick()
        {
            Intent intent = SearchActivity.newInstance(mBaseActivity, PlaceType.FNB, mGourmetCuration.getSaleTime(), 1);
            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.GOURMET_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.GOURMET_MAP, null);
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

            SaleTime saleTime = mGourmetCuration.getSaleTime();
            Province province = mGourmetCuration.getProvince();

            Intent intent = GourmetRegionListActivity.newInstance(getContext(), province, saleTime);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label.GOURMET_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_LOCATION, AnalyticsManager.Label.GOURMET_MAP, null);
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

            GourmetListFragment gourmetListFragment = (GourmetListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();

            switch (mViewType)
            {
                case LIST:
                {
                    // 맵리스트 진입시에 솔드아웃은 맵에서 보여주지 않기 때문에 맵으로 진입시에 아무것도 볼수 없다.
                    if (gourmetListFragment.hasSalesPlace() == false)
                    {
                        unLockUI();

                        DailyToast.showToast(mBaseActivity, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label.GOURMET_MAP, null);
                    break;
                }

                case MAP:
                {
                    mViewType = ViewType.LIST;

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label.GOURMET_LIST, null);
                    break;
                }
            }

            // 고메는 리스트를 한번에 받기 때문에 계속 요청할 필요는 없다.
            mPlaceMainLayout.setOptionViewTypeView(mViewType);

            for (PlaceListFragment placeListFragment : mPlaceMainLayout.getPlaceListFragment())
            {
                boolean isCurrentFragment = placeListFragment == gourmetListFragment;
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

            Province province = mGourmetCuration.getProvince();

            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = GourmetCurationActivity.newInstance(mBaseActivity, mViewType, mGourmetCuration);
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, viewType, null);
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
                mGourmetCuration.setSaleTime(currentDateTime, dailyDateTime);

                String lastViewDate = DailyPreference.getInstance(mBaseActivity).getGourmetLastViewDate();

                if (Util.isTextEmpty(lastViewDate) == false)
                {
                    DailyPreference.getInstance(mBaseActivity).setGourmetLastViewDate(null);

                    SaleTime changedSaleTime = SaleTime.changeDateSaleTime(mGourmetCuration.getSaleTime(), lastViewDate);

                    if (changedSaleTime != null)
                    {
                        mGourmetCuration.setSaleTime(changedSaleTime);
                        ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(changedSaleTime);
                    }
                }

                if (DailyDeepLink.getInstance().isValidateLink() == true //
                    && processDeepLinkByDateTime(mBaseActivity) == true)
                {
                    // 딥링크 이동
                } else
                {
                    ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(mGourmetCuration.getSaleTime());

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
            GourmetEventBannerManager.getInstance().setList(eventBannerList);

            mPlaceMainNetworkController.requestRegionList();
        }

        @Override
        public void onRegionList(List<Province> provinceList, List<Area> areaList)
        {
            if (isFinishing() == true || provinceList == null || areaList == null)
            {
                return;
            }

            if (mGourmetCuration.getSaleTime() == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            Province selectedProvince = mGourmetCuration.getProvince();

            if (selectedProvince == null)
            {
                selectedProvince = searchLastRegion(mBaseActivity, provinceList, areaList);
            }

            // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
            if (selectedProvince == null)
            {
                selectedProvince = provinceList.get(0);
            }

            boolean mIsProvinceSetting = DailyPreference.getInstance(mBaseActivity).isSettingRegion(PlaceType.FNB);
            DailyPreference.getInstance(mBaseActivity).setSettingRegion(PlaceType.FNB, true);

            // 마지막으로 지역이 Area로 되어있으면 Province로 바꾸어 준다.
            if (mIsProvinceSetting == false && selectedProvince instanceof Area)
            {
                int provinceIndex = selectedProvince.getProvinceIndex();

                for (Province province : provinceList)
                {
                    if (province.getProvinceIndex() == provinceIndex)
                    {
                        selectedProvince = province;
                        DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.FNB, province.isOverseas);
                        DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.FNB, province.name);

                        String country = province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;
                        String realProvinceName = Util.getRealProvinceName(province);
                        DailyPreference.getInstance(mBaseActivity).setSelectedRegionTypeProvince(PlaceType.FNB, realProvinceName);
                        AnalyticsManager.getInstance(mBaseActivity).onRegionChanged(country, realProvinceName);
                        break;
                    }
                }
            }

            mGourmetCuration.setProvince(selectedProvince);

            // 기존 저장 Province 가 소지역이 아닐수도 있고, 또한 default 지역인 서울로 하드 코딩 될수 있음으로 한번더 검사
            String saveProvinceName = DailyPreference.getInstance(mBaseActivity).getSelectedRegionTypeProvince(PlaceType.FNB);
            if (selectedProvince.name.equalsIgnoreCase(saveProvinceName) == false)
            {
                String country = selectedProvince.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;
                String realProvinceName = Util.getRealProvinceName(selectedProvince);
                DailyPreference.getInstance(mBaseActivity).setSelectedRegionTypeProvince(PlaceType.FNB, realProvinceName);
                AnalyticsManager.getInstance(mBaseActivity).onRegionChanged(country, realProvinceName);
            }

            if (DailyDeepLink.getInstance().isValidateLink() == true//
                && processDeepLinkByRegionList(mBaseActivity, provinceList, areaList) == true)
            {

            } else
            {
                // 리스트 요청하면 됨.
                mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
                mPlaceMainLayout.setCategoryTabLayout(getChildFragmentManager(), new ArrayList<Category>(), //
                    null, mOnPlaceListFragmentListener);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Throwable e)
        {
            mBaseActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            mBaseActivity.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            mBaseActivity.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call<JSONObject> call, Response<JSONObject> response)
        {

        }

        private boolean processDeepLinkByDateTime(BaseActivity baseActivity)
        {
            if (DailyDeepLink.getInstance().isGourmetDetailView() == true)
            {
                unLockUI();

                return moveDeepLinkDetail(baseActivity);
            } else if (DailyDeepLink.getInstance().isGourmetEventBannerWebView() == true)
            {
                unLockUI();

                return moveDeepLinkEventBannerWeb(baseActivity);
            } else if (DailyDeepLink.getInstance().isGourmetRegionListView() == true)
            {
                unLockUI();

                return moveDeepLinkRegionList(baseActivity);
            } else if (DailyDeepLink.getInstance().isGourmetSearchView() == true)
            {
                unLockUI();

                return moveDeepLinkSearch(baseActivity);
            } else if (DailyDeepLink.getInstance().isGourmetSearchResultView() == true)
            {
                unLockUI();

                return moveDeepLinkSearchResult(baseActivity);
            } else if (DailyDeepLink.getInstance().isCollectionView() == true)
            {
                unLockUI();

                return moveDeepLinkCollection(baseActivity);
            } else
            {
                // 더이상 진입은 없다.
                if (DailyDeepLink.getInstance().isGourmetListView() == false)
                {
                    DailyDeepLink.getInstance().clear();
                }
            }

            return false;
        }

        private boolean processDeepLinkByRegionList(BaseActivity baseActivity, List<Province> provinceList, List<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isGourmetListView() == true)
            {
                unLockUI();

                return moveDeepLinkGourmetList(provinceList, areaList);
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
            String regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.FNB);

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

    private GourmetListFragment.OnGourmetListFragmentListener mOnPlaceListFragmentListener = new GourmetListFragment.OnGourmetListFragmentListener()
    {
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

                    String savedRegion = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.FNB);

                    if (province.name.equalsIgnoreCase(savedRegion) == false)
                    {
                        DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.FNB, province.isOverseas);
                        DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.FNB, province.name);

                        String country = province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;
                        String realProvinceName = Util.getRealProvinceName(province);
                        DailyPreference.getInstance(mBaseActivity).setSelectedRegionTypeProvince(PlaceType.FNB, realProvinceName);
                        AnalyticsManager.getInstance(mBaseActivity).onRegionChanged(country, realProvinceName);
                    }

                    Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                        mGourmetCuration.getSaleTime(), province, gourmet, listCount);

                    if (Util.isUsedMutilTransition() == true)
                    {
                        View simpleDraweeView = view.findViewById(R.id.imageView);
                        View nameTextView = view.findViewById(R.id.nameTextView);
                        View gradientTopView = view.findViewById(R.id.gradientTopView);
                        View gradientBottomView = view.findViewById(R.id.gradientView);

                        Object mapTag = gradientBottomView.getTag();

                        if (mapTag != null && "map".equals(mapTag) == true)
                        {
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
                        }

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                            android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                            android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                            android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                            android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL, options.toBundle());
                    } else
                    {
                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
                    }

                    if (mViewType == ViewType.LIST)
                    {
                        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.GOURMET_ITEM_CLICKED, gourmet.name, null);
                    }
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
                , AnalyticsManager.Action.GOURMET_EVENT_BANNER_CLICKED, eventBanner.name, null);

            // SaleTime saleTime = mGourmetCuration.getSaleTime().getClone(0);

            // 이벤트 배너 딥링크 사용하지 않기로 했음.
            if (eventBanner.isDeepLink() == true)
            {
                // 이벤트 베너 클릭후 바로 딥링크로 이동하는 것은 사용하지 않기로 한다.
                //                try
                //                {
                //                    Calendar calendar = DailyCalendar.getInstance();
                //                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                //                    calendar.setTimeInMillis(eventBanner.dateTime);
                //
                //                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                //                    Date schemeDate = format.parse(format.format(calendar.getTime()));
                //                    Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));
                //
                //                    int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);
                //
                //                    saleTime.setOffsetDailyDay(dailyDayOfDays);
                //
                //                    if (eventBanner.isHotel() == true)
                //                    {
                //                        Intent intent = new Intent(mBaseActivity, StayDetailActivity.class);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, eventBanner.index);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, eventBanner.nights);
                //                        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, 0);
                //
                //                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
                //                    } else
                //                    {
                //                        startGourmetDetailByDeepLink(eventBanner.index, saleTime);
                //                    }
                //                } catch (Exception e)
                //                {
                //                    ExLog.e(e.toString());
                //                }
            } else
            {
                Intent intent = EventWebActivity.newInstance(mBaseActivity, //
                    EventWebActivity.SourceType.GOURMET_BANNER, eventBanner.webLink, eventBanner.name);
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
                currentPlaceListFragment.setPlaceCuration(mGourmetCuration);
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

    private boolean moveDeepLinkDetail(BaseActivity baseActivity)
    {
        try
        {
            int gourmetIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();
            int ticketIndex = DailyDeepLink.getInstance().getOpenTicketIndex();

            String startDate = DailyDeepLink.getInstance().getStartDate();
            String endDate = DailyDeepLink.getInstance().getEndDate();

            SaleTime changedSaleTime = mGourmetCuration.getSaleTime().getClone(0);
            SaleTime startSaleTime = null, endSaleTime = null;

            if (Util.isTextEmpty(date) == false)
            {
                changedSaleTime = SaleTime.changeDateSaleTime(changedSaleTime, date);
            } else if (datePlus >= 0)
            {
                changedSaleTime.setOffsetDailyDay(datePlus);
            } else if (Util.isTextEmpty(startDate, endDate) == false)
            {
                startSaleTime = SaleTime.changeDateSaleTime(changedSaleTime, startDate);
                endSaleTime = SaleTime.changeDateSaleTime(changedSaleTime, endDate, -1);

                // 캘린더에서는 미만으로 날짜를 처리하여 1을 더해주어야 한다.
                endSaleTime.setOffsetDailyDay(endSaleTime.getOffsetDailyDay() + 1);

                changedSaleTime = startSaleTime.getClone();
            }

            if (changedSaleTime == null)
            {
                return false;
            }

            if (Util.isTextEmpty(startDate, endDate) == false)
            {
                Intent intent = GourmetDetailActivity.newInstance(baseActivity, startSaleTime, endSaleTime, gourmetIndex, ticketIndex, isShowCalendar);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(baseActivity, changedSaleTime, gourmetIndex, ticketIndex, isShowCalendar);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
            }

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
            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.GOURMET_BANNER, url, null);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);

            mIsDeepLink = true;
            return true;
        } else
        {
            return false;
        }
    }

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, //
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

        Intent intent = GourmetRegionListActivity.newInstance(baseActivity, provinceIndex, areaIndex, mGourmetCuration.getSaleTime());
        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
        mIsDeepLink = true;
        return true;
    }

    private boolean moveDeepLinkSearch(BaseActivity baseActivity)
    {
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();
        String word = DailyDeepLink.getInstance().getSearchWord();

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mGourmetCuration.getSaleTime().getClone(0);
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

        Intent intent = SearchActivity.newInstance(baseActivity, PlaceType.FNB, checkInSaleTime, 1, word);
        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

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

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mGourmetCuration.getSaleTime().getClone(0);
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
                    Intent intent = GourmetSearchResultActivity.newInstance(baseActivity, checkInSaleTime, latLng, radius, true);
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
            }

            default:
                if (Util.isTextEmpty(word) == false)
                {
                    Intent intent = GourmetSearchResultActivity.newInstance(baseActivity, checkInSaleTime, new Keyword(0, word), SearchType.SEARCHES);
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
        }

        mIsDeepLink = true;

        return true;
    }

    private boolean moveDeepLinkGourmetList(List<Province> provinceList, List<Area> areaList)
    {
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();
        gourmetCurationOption.setSortType(DailyDeepLink.getInstance().getSorting());

        mPlaceMainLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

        int provinceIndex;
        int areaIndex;

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

        // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
        Province selectedProvince = searchDeeLinkRegion(provinceIndex, areaIndex, provinceList, areaList);

        if (selectedProvince == null)
        {
            selectedProvince = mGourmetCuration.getProvince();
        }

        mGourmetCuration.setProvince(selectedProvince);
        mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mGourmetCuration.getSaleTime().getClone(0);
        SaleTime changedSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            changedSaleTime = SaleTime.changeDateSaleTime(saleTime, date);
        } else if (datePlus >= 0)
        {
            try
            {
                changedSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            changedSaleTime = saleTime;
        }

        if (changedSaleTime == null)
        {
            return false;
        }

        mGourmetCuration.setSaleTime(changedSaleTime);
        ((GourmetMainLayout) mPlaceMainLayout).setToolbarDateText(changedSaleTime);

        mPlaceMainNetworkController.requestRegionList();

        return true;
    }

    private boolean moveDeepLinkCollection(BaseActivity baseActivity)
    {
        String title = DailyDeepLink.getInstance().getTitle();
        String titleImageUrl = DailyDeepLink.getInstance().getTitleImageUrl();
        String queryType = DailyDeepLink.getInstance().getQueryType();
        String query = DailyDeepLink.getInstance().getQuery();

        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();

        String startDate = DailyDeepLink.getInstance().getStartDate();
        String endDate = DailyDeepLink.getInstance().getEndDate();

        DailyDeepLink.getInstance().clear();

        SaleTime saleTime = mGourmetCuration.getSaleTime().getClone(0);
        SaleTime checkInSaleTime;
        SaleTime startSaleTime = null, endSaleTime = null;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);
        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else if (Util.isTextEmpty(startDate, endDate) == false)
        {
            startSaleTime = SaleTime.changeDateSaleTime(saleTime, startDate);
            endSaleTime = SaleTime.changeDateSaleTime(saleTime, endDate, -1);

            // 캘린더에서는 미만으로 날짜를 처리하여 1을 더해주어야 한다.
            endSaleTime.setOffsetDailyDay(endSaleTime.getOffsetDailyDay() + 1);

            checkInSaleTime = startSaleTime.getClone();
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

        if (Util.isTextEmpty(startDate, endDate) == false)
        {
            Intent intent = CollectionGourmetActivity.newInstance(baseActivity, startSaleTime, endSaleTime, title, titleImageUrl, queryType, query);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);
        } else
        {
            Intent intent = CollectionGourmetActivity.newInstance(baseActivity, checkInSaleTime, title, titleImageUrl, queryType, query);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);
        }

        mIsDeepLink = true;

        return true;
    }
}
