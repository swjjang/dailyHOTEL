package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.region.GourmetRegionListActivity;
import com.twoheart.dailyhotel.screen.gourmet.search.GourmetSearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetMainFragment_v2 extends PlaceMainFragment
{
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
    protected void onRegionActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
            {
                Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
                GourmetCurationManager.getInstance().setProvince(province);

                mPlaceMainLayout.setToolbarRegionText(province.name);

                DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.FNB, province.isOverseas);
                DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.FNB, province.name);

                refreshCurrentFragment();
            }
        }
    }

    @Override
    protected void onCalendarActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onCurationActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onSettingLocationActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onLocationFailed()
    {

    }

    @Override
    protected void onLocationProviderDisabled()
    {

    }

    @Override
    protected void onLocationChanged(Location location)
    {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mViewType == ViewType.LIST)
        {
            if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION)
            {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    searchMyLocation();
                } else
                {
                    // 퍼미션 허락하지 않음.
                }
            }
        } else if (mViewType == ViewType.MAP)
        {
            PlaceListFragment placeListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();

            if (placeListFragment != null)
            {
                placeListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void curationCurrentFragment()
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        gourmetListFragment.curationList(mViewType, mCurationOption);
    }

    public void refreshCurrentFragment(List<EventBanner> list)
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        gourmetListFragment.refreshList(list);
    }

    private void refreshEventBanner()
    {
        DailyNetworkAPI.getInstance(getContext()).requestEventBannerList(mNetworkTag, "gourmet", mEventBannerListJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                refreshCurrentFragment(getProvince());
            }
        });
    }

    private void refreshCurrentFragment(Province province)
    {
        if (province == null)
        {
            return;
        }

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        setProvince(province);

        mDailyToolbarLayout.setToolbarRegionText(province.name);
        mDailyToolbarLayout.setToolbarMenuVisibility(true);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        String savedRegion = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.FNB);

        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(baseActivity).setSelectedOverseaRegion(PlaceType.FNB, province.isOverseas);
            DailyPreference.getInstance(baseActivity).setSelectedRegion(PlaceType.FNB, province.name);
        }

        refreshCurrentFragment(mEventBannerList);
    }

    private void refreshCurrentFragment()
    {
        if(isFinishing() == true)
        {
            return;
        }

        PlaceListFragment placeListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();

        if(placeListFragment != null)
        {
            placeListFragment.refreshList();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
    {
        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            // stay는 현재 카테고리 상태를 저장한다.
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
            Intent intent = GourmetSearchActivity.newInstance(mBaseActivity, GourmetCurationManager.getInstance().getSaleTime());
            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

            switch (mViewType)
            {
                case LIST:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.GOURMET_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.GOURMET_LIST, null);

                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_POPPEDUP, AnalyticsManager.Label.GOURMET_LIST, null);
                    break;

                case MAP:
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.GOURMET_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.GOURMET_MAP, null);

                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_POPPEDUP, AnalyticsManager.Label.GOURMET_MAP, null);
                    break;
            }
        }

        @Override
        public void onDateClick()
        {

        }

        @Override
        public void onRegionClick()
        {
            SaleTime saleTime = GourmetCurationManager.getInstance().getSaleTime();
            Province province = GourmetCurationManager.getInstance().getProvince();

            Intent intent = GourmetRegionListActivity.newInstance(getContext(), province, saleTime);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);
        }

        @Override
        public void onViewTypeClick()
        {
            // 리스트

            // 맵

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            GourmetListFragment_v2 gourmetListFragment = (GourmetListFragment_v2)mPlaceMainLayout.getCurrentPlaceListFragment();

            switch (mViewType)
            {
                case LIST:
                {
                    // 맵리스트 진입시에 솔드아웃은 맵에서 보여주지 않기 때문에 맵으로 진입시에 아무것도 볼수 없다.
                    if (gourmetListFragment.hasSalesPlace() == false)
                    {
                        unLockUI();

                        BaseActivity baseActivity = (BaseActivity) getActivity();

                        if (baseActivity == null)
                        {
                            return;
                        }

                        DailyToast.showToast(baseActivity, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP);
                    break;
                }

                case MAP:
                {
                    mViewType = ViewType.LIST;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST);

                    Map<String, String> parmas = new HashMap<>();
                    Province province = GourmetCurationManager.getInstance().getProvince();

                    if (province instanceof Area)
                    {
                        Area area = (Area) province;
                        parmas.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                        parmas.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                    } else
                    {
                        parmas.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                        parmas.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                    }

                    AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST, parmas);
                    break;
                }
            }

            for (GourmetListFragment placeListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                boolean isCurrentFragment = placeListFragment == currentFragment;

                placeListFragment.setVisibility(mViewType, isCurrentFragment);
            }

            currentFragment.curationList(mViewType, mCurationOption);

            unLockUI();
        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Province province = GourmetCurationManager.getInstance().getProvince();

            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = GourmetCurationActivity.newInstance(mBaseActivity, province.isOverseas, mViewType);
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
        public void finish()
        {

        }
    };

    private PlaceMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceMainNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onDateTime(long currentDateTime, long dailyDateTime)
        {
            GourmetCurationManager.getInstance().setSaleTime(currentDateTime, dailyDateTime);

            if (DailyDeepLink.getInstance().isValidateLink() == true //
                && processDeepLinkByDateTime(mBaseActivity) == true)
            {
                // 딥링크 이동
            } else
            {
                mPlaceMainNetworkController.requestEventBanner();
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
            if(provinceList == null || areaList == null)
            {
                return;
            }

            Province selectedProvince = GourmetCurationManager.getInstance().getProvince();

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
                        break;
                    }
                }
            }

            GourmetCurationManager.getInstance().setProvince(selectedProvince);

            if (DailyDeepLink.getInstance().isValidateLink() == true//
                && processDeepLinkByRegionList(mBaseActivity, provinceList, areaList) == true)
            {

            } else
            {
                // 리스트 요청하면 됨.
                String dateText = GourmetCurationManager.getInstance().getSaleTime().getDayOfDaysDateFormat("M.d(EEE)");
                mPlaceMainLayout.setToolbarDateText(dateText);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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

        private Province searchLastRegion(BaseActivity baseActivity, List<Province> provinceList, List<Area> areaList)
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

    private GourmetListFragment_v2.OnGourmetListFragmentListener mOnGourmetListFragmentListener = new GourmetListFragment_v2.OnGourmetListFragmentListener()
    {
        @Override
        public void onGourmetClick(PlaceViewItem placeViewItem, SaleTime saleTime)
        {

        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
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
            // 신규 타입의 화면이동
            SaleTime saleTime = GourmetCurationManager.getInstance().getSaleTime();
            int gourmetIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = saleTime.getDailyTime();

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();

            // date가 비어 있는 경우
            if (Util.isTextEmpty(date) == true)
            {
                if (datePlus >= 0)
                {
                    mOnCommunicateListener.selectPlace(gourmetIndex, dailyTime, datePlus, 1);
                } else
                {
                    throw new NullPointerException("datePlus < 0");
                }
            } else
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (dailyDayOfDays < 0)
                {
                    throw new NullPointerException("dailyDayOfDays < 0");
                }

                mOnCommunicateListener.selectPlace(gourmetIndex, dailyTime, dailyDayOfDays, nights);
            }

            DailyDeepLink.getInstance().clear();
            mIsDeepLink = true;

            return true;
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            DailyDeepLink.getInstance().clear();

            return false;
        }
    }

    private boolean moveDeepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.GOURMET_BANNER, url, null, GourmetCurationManager.getInstance().getSaleTime());
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
            mIsDeepLink = true;

            return true;
        } else
        {
            return false;
        }
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

        Intent intent = GourmetRegionListActivity.newInstance(baseActivity, provinceIndex, areaIndex, GourmetCurationManager.getInstance().getSaleTime());
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
        mIsDeepLink = true;
        return true;
    }

    private boolean moveDeepLinkGourmetList(List<Province> provinceList, List<Area> areaList)
    {
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();
        GourmetCurationManager.getInstance().setSortType(DailyDeepLink.getInstance().getSorting());

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
            selectedProvince = GourmetCurationManager.getInstance().getProvince();
        }

        GourmetCurationManager.getInstance().setProvince(selectedProvince);
        mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);
        DailyDeepLink.getInstance().clear();

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            try
            {
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                SaleTime saleTime = GourmetCurationManager.getInstance().getSaleTime();
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (dailyDayOfDays >= 0)
                {
                    SaleTime deepLinkSaleTime = saleTime.getClone(dailyDayOfDays);

                    // 리스트 요청하면 됨.
                    String dateText = deepLinkSaleTime.getDayOfDaysDateFormat("M.d(EEE)");
                    mPlaceMainLayout.setToolbarDateText(dateText);

                    // 특정 날짜 고메 리스트 요청
                    mOnCommunicateListener.selectDay(deepLinkSaleTime, true);
                } else
                {
                    return false;
                }
            } catch (Exception e)
            {
                return false;
            }
        } else if (datePlus >= 0)
        {
            SaleTime saleTime = GourmetCurationManager.getInstance().getSaleTime();

            try
            {
                SaleTime deepLinkSaleTime = saleTime.getClone(datePlus);

                // 리스트 요청하면 됨.
                String dateText = deepLinkSaleTime.getDayOfDaysDateFormat("M.d(EEE)");
                mPlaceMainLayout.setToolbarDateText(dateText);

                mOnCommunicateListener.selectDay(deepLinkSaleTime, true);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            return false;
        }

        return true;
    }

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, List<Province> provinceList, List<Area> areaList)
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
}
