package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.PlaceCurationOption;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetCurationManager;
import com.twoheart.dailyhotel.screen.hotel.region.HotelRegionListActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StayMainFragment extends PlaceMainFragment
{

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // 아마도 위치 정보
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
    protected void onRegionActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 지역 선택하고 돌아온 경우
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
            {
                Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
                refreshCurrentFragment(province);
            }
        }
    }

    @Override
    protected void onCurationActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCurationOption placeCurationOption = data.getParcelableExtra(GourmetCurationActivity.INTENT_EXTRA_DATA_CURATION_OPTIONS);

            if (placeCurationOption instanceof HotelCurationOption == false)
            {
                return;
            }

            StayCurationOption changeCurationOption = (StayCurationOption) placeCurationOption;
            StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

            stayCurationOption.setSortType(changeCurationOption.getSortType());
            stayCurationOption.setFiltersList(changeCurationOption.getFiltersList());

            stayCurationOption.person = changeCurationOption.person;
            stayCurationOption.flagBedTypeFilters = changeCurationOption.flagBedTypeFilters;
            stayCurationOption.flagAmenitiesFilters = changeCurationOption.flagAmenitiesFilters;

            if (changeCurationOption.getSortType() == SortType.DISTANCE)
            {
                searchMyLocation();
            } else
            {
                Category category = StayCurationManager.getInstance().getCategory();
                refreshCurrentFragmentByCuration(category, stayCurationOption);
            }
        }
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

    private String makeTabDateFormat(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        String dateFormat;
        String tabDateFormat;

        if (Util.getLCDWidth(getContext()) < 720)
        {
            dateFormat = "M.d";
            tabDateFormat = "%s - %s";
        } else
        {
            dateFormat = "M월d일";
            tabDateFormat = "%s-%s";
        }

        String checkInDay = checkInSaleTime.getDayOfDaysDateFormat(dateFormat);
        String checkOutDay = checkOutSaleTime.getDayOfDaysDateFormat(dateFormat);

        return String.format(tabDateFormat, checkInDay, checkOutDay);
    }

    private void refreshCurrentFragment(List<EventBanner> list)
    {
        StayListFragment currentFragment = (StayListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();
        currentFragment.refreshList(list);
    }

    private void refreshCurrentFragmentByCuration(Category category, StayCurationOption stayCurationOption)
    {
        StayListFragment currentFragment = (StayListFragment) mPlaceMainLayout.getCurrentPlaceListFragment();
        currentFragment.curationList(mViewType, category, stayCurationOption);
    }

    private void refreshCurrentFragment(Province province)
    {
        if (province == null)
        {
            return;
        }

        if (isFinishing() == true)
        {
            return;
        }

        StayCurationManager.getInstance().setProvince(province);

        mPlaceMainLayout.setToolbarRegionText(province.name);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        String savedRegion = DailyPreference.getInstance(mBaseActivity).getSelectedRegion(PlaceType.HOTEL);

        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(mBaseActivity).setSelectedOverseaRegion(PlaceType.HOTEL, province.isOverseas);
            DailyPreference.getInstance(mBaseActivity).setSelectedRegion(PlaceType.HOTEL, province.name);
        }

        refreshCurrentFragment(StayEventBannerManager.getInstance().getList());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean moveDeepLinkDetail(BaseActivity baseActivity)
    {
        try
        {
            // 신규 타입의 화면이동
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());

            SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();
            long dailyTime = checkInSaleTime.getDailyTime();
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();

            DailyDeepLink.getInstance().clear();

            if (Util.isTextEmpty(date) == true)
            {
                if (datePlus >= 0)
                {
                    mOnCommunicateListener.selectHotel(hotelIndex, dailyTime, datePlus, nights);
                } else
                {
                    return false;
                }
            } else
            {
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (nights <= 0 || dailyDayOfDays < 0)
                {
                    return false;
                }

                mOnCommunicateListener.selectHotel(hotelIndex, dailyTime, dailyDayOfDays, nights);
            }

            mIsDeepLink = true;
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            DailyDeepLink.getInstance().clear();
            return false;
        }

        return true;
    }

    private boolean moveDeepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.HOTEL_BANNER, url, null, checkInSaleTime);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
            mIsDeepLink = true;

            return true;
        } else
        {
            return false;
        }
    }

    private Province searchDeeLinkRegion(List<Province> provinceList, List<Area> areaList)
    {
        Province selectedProvince = null;

        try
        {
            int provinceIndex = Integer.parseInt(DailyDeepLink.getInstance().getProvinceIndex());
            int areaIndex = -1;

            try
            {
                areaIndex = Integer.parseInt(DailyDeepLink.getInstance().getAreaIndex());
            } catch (Exception e)
            {
            }

            boolean isOverseas = DailyDeepLink.getInstance().getIsOverseas();

            if (areaIndex == -1)
            {
                // 전체 지역으로 이동
                for (Province province : provinceList)
                {
                    if (province.isOverseas == isOverseas && province.index == provinceIndex)
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

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, boolean isOverseas, //
                                         List<Province> provinceList, List<Area> areaList)
    {
        Province selectedProvince = null;

        if (provinceIndex < 0 && areaIndex < 0)
        {
            return searchDeeLinkRegion(provinceList, areaList);
        }

        try
        {
            if (areaIndex == -1)
            {
                // 전체 지역으로 이동
                for (Province province : provinceList)
                {
                    if (province.isOverseas == isOverseas && province.index == provinceIndex)
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

        SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();
        int night = checkInSaleTime.getOffsetDailyDay();

        Intent intent = HotelRegionListActivity.newInstance(baseActivity, provinceIndex, areaIndex, checkInSaleTime, night);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
        mIsDeepLink = true;

        return true;
    }

    private boolean moveDeepLinkStayList(List<Province> provinceList, List<Area> areaList)
    {
        String categoryCode = DailyDeepLink.getInstance().getCategoryCode();
        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();

        StayCurationManager.getInstance().setSortType(DailyDeepLink.getInstance().getSorting());

        int night;

        try
        {
            night = Integer.parseInt(DailyDeepLink.getInstance().getNights());
        } catch (Exception e)
        {
            night = 1;
        }

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

        boolean isOverseas = DailyDeepLink.getInstance().getIsOverseas();

        // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
        Province selectedProvince = searchDeeLinkRegion(provinceIndex, areaIndex, isOverseas, provinceList, areaList);

        if (selectedProvince == null)
        {
            selectedProvince = StayCurationManager.getInstance().getProvince();
        }

        StayCurationManager.getInstance().setProvince(selectedProvince);

        mPlaceMainLayout.setToolbarRegionText(selectedProvince.name);

        // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
        if (Util.isTextEmpty(categoryCode) == false)
        {
            for (Category category : selectedProvince.getCategoryList())
            {
                if (category.code.equalsIgnoreCase(categoryCode) == true)
                {
                    StayCurationManager.getInstance().setCategory(category);
                    break;
                }
            }
        }

        DailyDeepLink.getInstance().clear();

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            try
            {
                SaleTime todaySaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(todaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (dailyDayOfDays >= 0)
                {

                    SaleTime checkInSaleTime = todaySaleTime.getClone(dailyDayOfDays);
                    SaleTime checkOutSaleTime = todaySaleTime.getClone(dailyDayOfDays + night);

                    StayListFragment hotelListFragment = (StayListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    hotelListFragment.setCheckInSaleTime(checkInSaleTime);
                    hotelListFragment.setCheckOutSaleTime(checkOutSaleTime);

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
            try
            {
                SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime().getClone(datePlus);
                SaleTime checkOutSaleTime = StayCurationManager.getInstance().getCheckInSaleTime().getClone(datePlus + night);

                HotelDaysListFragment hotelListFragment = (HotelDaysListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                hotelListFragment.setCheckInSaleTime(checkInSaleTime);
                hotelListFragment.setCheckOutSaleTime(checkOutSaleTime);

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlaceMainLayout.OnEventListener mOnEventListener = new PlaceMainLayout.OnEventListener()
    {
        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {

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

        }

        @Override
        public void onDateClick()
        {

        }

        @Override
        public void onRegionClick()
        {

        }

        @Override
        public void onViewTypeClick()
        {

        }

        @Override
        public void onFilterClick()
        {

        }

        @Override
        public void finish()
        {

        }
    };

    PlaceMainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceMainNetworkController.OnNetworkControllerListener()
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
                StayCurationManager.getInstance().setCheckInSaleTime(currentDateTime, dailyDateTime);
                StayCurationManager.getInstance().setCheckOutSaleTime( //
                    StayCurationManager.getInstance().getCheckInSaleTime().getClone(1));

                if (DailyDeepLink.getInstance().isValidateLink() == true //
                    && processDeepLinkByDateTime(mBaseActivity) == true)
                {

                } else
                {
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
            if (isFinishing() == true)
            {
                return;
            }

            Province selectedProvince = StayCurationManager.getInstance().getProvince();
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
                        break;
                    }
                }
            }

            StayCurationManager.getInstance().setProvince(selectedProvince);

            if (DailyDeepLink.getInstance().isValidateLink() == true//
                && processDeepLinkByReginList(mBaseActivity, provinceList, areaList) == true)
            {

            } else
            {
                String dateText = makeTabDateFormat(StayCurationManager.getInstance().getCheckInSaleTime(), //
                    StayCurationManager.getInstance().getCheckOutSaleTime());
                mPlaceMainLayout.setToolbarDateText(dateText);

                // stay list refresh
                refreshCurrentFragment(StayCurationManager.getInstance().getProvince());
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

        // onRegionList
        private boolean processDeepLinkByReginList(BaseActivity baseActivity, //
                                                   List<Province> provinceList, //
                                                   List<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isHotelRegionListView() == true)
            {
                unLockUI();

                return moveDeepLinkRegionList(baseActivity);

            } else if (DailyDeepLink.getInstance().isHotelListView() == true)
            {
                unLockUI();

                return moveDeepLinkStayList(provinceList, areaList);

            } else
            {
                DailyDeepLink.getInstance().clear();
            }

            return false;
        }

        // onDateTime
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
    };

}
