package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.activity.SelectAreaActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;
import com.twoheart.dailyhotel.view.widget.TabIndicator;
import com.twoheart.dailyhotel.view.widget.TabIndicator.OnTabSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HotelMainFragment extends BaseFragment
{
    private TabIndicator mTabIndicator;
    private FragmentViewPager mFragmentViewPager;
    private ArrayList<HotelListFragment> mFragmentList;

    private SaleTime mTodaySaleTime;
    private ArrayList<AreaItem> mAreaItemList;
    private Province mSelectedProvince;

    private boolean mMenuEnabled;
    private boolean mMapEnabled;
    private boolean mDontReloadAtOnResume;

    private HOTEL_VIEW_TYPE mHotelViewType = HOTEL_VIEW_TYPE.LIST;

    public enum HOTEL_VIEW_TYPE
    {
        LIST,
        MAP,
        GONE, // 목록이 비어있는 경우.
    }

    public interface OnUserActionListener
    {
        void selectHotel(HotelListViewItem hotelListViewItem, SaleTime checkSaleTime);

        void selectHotel(int hotelIndex, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, SaleTime checkOutSaleTime, boolean isListSelectionTop);

        void toggleViewType();

        void onClickActionBarArea();

        void setMapViewVisible(boolean isVisible);

        void refreshAll();
    }

    public interface UserAnalyticsActionListener
    {
        public void selectHotel(String hotelName, long hotelIndex, String checkInTime, int nights);

        public void selectRegion(Province province);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);

        ArrayList<String> titleList = new ArrayList<String>();
        titleList.add(getString(R.string.label_today));
        titleList.add(getString(R.string.label_tomorrow));
        titleList.add(getString(R.string.label_selecteday));

        mHotelViewType = HOTEL_VIEW_TYPE.LIST;

        mTabIndicator = (TabIndicator) view.findViewById(R.id.tabindicator);

        //		mTabIndicator.setData(titleList, dayList, true);
        mTabIndicator.setData(titleList, true);
        mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

        mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
        //		mFragmentViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mFragmentList = new ArrayList<HotelListFragment>();
        mTodaySaleTime = new SaleTime();

        HotelListFragment hotelListFragment = new HotelListFragment();
        hotelListFragment.setUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment);

        HotelListFragment hotelListFragment01 = new HotelListFragment();
        hotelListFragment01.setUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment01);

        HotelDaysListFragment hotelListFragment02 = new HotelDaysListFragment();
        hotelListFragment02.setUserActionListener(mOnUserActionListener);
        mFragmentList.add(hotelListFragment02);

        mFragmentViewPager.setData(mFragmentList);
        mFragmentViewPager.setAdapter(getChildFragmentManager());

        mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
        mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

        setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해

        initHide();

        return view;
    }

    private void initHide()
    {
        mTabIndicator.setVisibility(View.INVISIBLE);

        setMenuEnabled(false);
    }

    private void initShow()
    {
        mTabIndicator.setVisibility(View.VISIBLE);

        setMenuEnabled(true);
    }

    @Override
    public void onResume()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }

        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        MenuInflater inflater = baseActivity.getMenuInflater();

        menu.clear();

        if (mMenuEnabled == true)
        {
            switch (mHotelViewType)
            {
                case LIST:
                    inflater.inflate(R.menu.actionbar_icon_map, menu);
                    break;

                case MAP:
                    inflater.inflate(R.menu.actionbar_icon_list, menu);
                    break;
            }
        }
    }

    public void setMenuEnabled(boolean enabled)
    {
        if (mMenuEnabled == enabled || (enabled == true && mMapEnabled == false))
        {
            return;
        }

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mMenuEnabled = enabled;

        baseActivity.invalidateOptionsMenu();

        // 메뉴가 열리는 시점이다.
        HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

        if (currentFragment != null)
        {
            if (enabled == true)
            {
                currentFragment.setActionBarAnimationLock(false);
            } else
            {
                currentFragment.showActionBarAnimatoin(baseActivity);
                currentFragment.setActionBarAnimationLock(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return false;
        }

        switch (item.getItemId())
        {
            case R.id.action_list:
            {
                boolean isInstalledGooglePlayServices = Util.installGooglePlayService((BaseActivity) getActivity());

                if (isInstalledGooglePlayServices == true)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.toggleViewType();
                    }

                    baseActivity.invalidateOptionsMenu();
                }
                return true;
            }

            case R.id.action_map:
            {
                boolean isInstalledGooglePlayServices = Util.installGooglePlayService((BaseActivity) getActivity());

                if (isInstalledGooglePlayServices == true)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.toggleViewType();
                    }

                    baseActivity.invalidateOptionsMenu();
                }
                return true;
            }

            case R.id.action_sort:
            {
                HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.showSortDialogView();

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        unLockUI();
        releaseUiComponent();
        baseActivity.releaseUiComponent();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_HOTELTAB:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    ((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
                } else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    ((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
                }
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.onActivityResult(requestCode, resultCode, data);
                break;
            }

            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_SELECT_AREA:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK)
                {
                    if (data != null)
                    {
                        if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
                        {
                            Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

                            setNavigationItemSelected(province);

                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.refreshAll();
                            }
                        } else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
                        {
                            Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

                            setNavigationItemSelected(province);

                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.refreshAll();
                            }
                        }
                    }
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

        if (hotelListFragment != null)
        {
            hotelListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setNavigationItemSelected(Province province)
    {
        mSelectedProvince = province;
    }

    private void onNavigationItemSelected(Province province, boolean isSelectionTop)
    {
        if (province == null)
        {
            return;
        }

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mSelectedProvince = province;

        baseActivity.setActionBarAreaEnabled(true);
        baseActivity.setActionBarArea(province.name, mOnUserActionListener);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        if (province.name.equalsIgnoreCase(baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "")) == false)
        {
            SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
            editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
            editor.putString(KEY_PREFERENCE_REGION_SELECT, province.name);
            editor.commit();

            isSelectionTop = true;
        }

        if (mUserAnalyticsActionListener != null)
        {
            mUserAnalyticsActionListener.selectRegion(province);
        }

        refreshHotelList(province, isSelectionTop);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void refreshHotelList(Province province, boolean isSelectionTop)
    {
        HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

        if (isSelectionTop == true)
        {
            hotelListFragment.setSortType(HotelListFragment.SortType.DEFAULT);
        }

        hotelListFragment.refreshHotelList(province, isSelectionTop);
    }

    private ArrayList<AreaItem> makeAreaItemList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        ArrayList<AreaItem> arrayList = new ArrayList<AreaItem>(provinceList.size());

        for (Province province : provinceList)
        {
            AreaItem item = new AreaItem();

            item.setProvince(province);
            item.setAreaList(new ArrayList<Area>());

            for (Area area : areaList)
            {
                if (province.getProvinceIndex() == area.getProvinceIndex())
                {
                    ArrayList<Area> areaArrayList = item.getAreaList();

                    if (areaArrayList.size() == 0)
                    {
                        Area totalArea = new Area();

                        totalArea.index = -1;
                        totalArea.name = province.name + " 전체";
                        totalArea.setProvince(province);
                        totalArea.sequence = -1;
                        totalArea.tag = totalArea.name;
                        totalArea.setProvinceIndex(province.getProvinceIndex());

                        areaArrayList.add(totalArea);
                    }

                    area.setProvince(province);
                    areaArrayList.add(area);
                }
            }

            arrayList.add(item);
        }

        return arrayList;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(int position)
        {
            if (mFragmentViewPager == null)
            {
                return;
            }

            if (mFragmentViewPager.getCurrentItem() == position)
            {
                HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.onPageSelected(false);
            } else
            {
                mFragmentViewPager.setCurrentItem(position);
            }
        }
    };

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                mTabIndicator.setCurrentItem(position);

                // 현재 페이지 선택 상태를 Fragment에게 알려준다.
                HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

                for (HotelListFragment hotelListFragment : mFragmentList)
                {
                    if (hotelListFragment == currentFragment)
                    {
                        hotelListFragment.onPageSelected(true);
                    } else
                    {
                        hotelListFragment.onPageUnSelected();
                    }
                }

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.refreshAll();
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.PROVINCE, mSelectedProvince.name);
                params.put(Label.DATE_TAB, mTabIndicator.getMainText(position));

                AnalyticsManager.getInstance(baseActivity).recordEvent(mHotelViewType.name(), Action.CLICK, Label.DATE_TAB, params);
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                // 릴리즈 버전에서 메모리 해지에 문제가 생기는 경우가 있어 앱을 재 시작 시킨다.
                if (DEBUG == false)
                {
                    baseActivity.restartApp();
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            switch (state)
            {
                case ViewPager.SCROLL_STATE_IDLE:
                {
                    HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
                    currentFragment.setFloatingActionButtonVisible(true);
                    break;
                }

                case ViewPager.SCROLL_STATE_DRAGGING:
                {
                    HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
                    currentFragment.setFloatingActionButtonVisible(false);
                    break;
                }

                case ViewPager.SCROLL_STATE_SETTLING:
                {
                    break;
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }
    };

    private UserAnalyticsActionListener mUserAnalyticsActionListener = new UserAnalyticsActionListener()
    {
        @Override
        public void selectHotel(String hotelName, long hotelIndex, String checkInTime, int nights)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            String text = mTabIndicator.getMainText(mFragmentViewPager.getCurrentItem());

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.DATE_TAB, text);
            params.put(Label.HOTEL_INDEX, String.valueOf(hotelIndex));
            params.put(Label.CHECK_IN, checkInTime);
            params.put(Label.NIGHTS, String.valueOf(nights));

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mHotelViewType.name(), Action.CLICK, hotelName, params);
        }

        @Override
        public void selectRegion(Province province)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            String text = mTabIndicator.getMainText(mFragmentViewPager.getCurrentItem());

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.DATE_TAB, text);

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mHotelViewType.name(), Action.CLICK, province.name, params);
        }
    };

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void selectHotel(HotelListViewItem hotelListViewItem, SaleTime checkSaleTime)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
            {
                return;
            }

            if (hotelListViewItem == null)
            {
                unLockUI();
                return;
            }

            lockUI();

            switch (hotelListViewItem.getType())
            {
                case HotelListViewItem.TYPE_ENTRY:
                {
                    Hotel hotel = hotelListViewItem.getItem();

                    String region = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
                    SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
                    editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, hotel.getName());
                    editor.commit();

                    Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotel.getIdx());
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, hotel.nights);

                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotel.getName());
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, hotel.imageUrl);

                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);

                    mUserAnalyticsActionListener.selectHotel(hotelListViewItem.getItem().getName(), hotel.getIdx(), checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), hotel.nights);
                    break;
                }

                case HotelListViewItem.TYPE_SECTION:
                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void selectHotel(int hotelIndex, long dailyTime, int dailyDayOfDays, int nights)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || hotelIndex < 0)
            {
                return;
            }

            if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);
        }

        @Override
        public void selectDay(SaleTime checkInSaleTime, SaleTime checkOutSaleTime, boolean isListSelectionTop)
        {
            if (isLockUiComponent() == true || checkInSaleTime == null || checkOutSaleTime == null || isAdded() == false)
            {
                return;
            }

            lockUiComponent();

            String checkInDay = getString(R.string.label_format_tabmonth, //
                checkInSaleTime.getDayOfDaysHotelDateFormat("M"),//
                checkInSaleTime.getDayOfDaysHotelDateFormat("d"));
            String checkOutDay = getString(R.string.label_format_tabmonth, //
                checkOutSaleTime.getDayOfDaysHotelDateFormat("M"),//
                checkOutSaleTime.getDayOfDaysHotelDateFormat("d"));

            // 선택탭의 이름을 수정한다.
            mTabIndicator.setSubTextEnable(2, true);
            mTabIndicator.setSubText(2, checkInDay + "-" + checkOutDay);

            refreshHotelList(mSelectedProvince, isListSelectionTop);

            releaseUiComponent();
        }

        @Override
        public void toggleViewType()
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            switch (mHotelViewType)
            {
                case LIST:
                    mHotelViewType = HOTEL_VIEW_TYPE.MAP;
                    break;

                case MAP:
                    mHotelViewType = HOTEL_VIEW_TYPE.LIST;
                    break;
            }

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

            for (HotelListFragment hotelListFragment : mFragmentList)
            {
                boolean isCurrentFragment = hotelListFragment == currentFragment;

                hotelListFragment.setHotelViewType(mHotelViewType, isCurrentFragment);
            }

            unLockUI();
        }

        @Override
        public void onClickActionBarArea()
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Intent intent = new Intent(baseActivity, SelectAreaActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mSelectedProvince);
            intent.putParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREAITEMLIST, mAreaItemList);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SELECT_AREA);
        }

        @Override
        public void setMapViewVisible(boolean isVisible)
        {
            mMapEnabled = isVisible;
            setMenuEnabled(isVisible);
        }

        @Override
        public void refreshAll()
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            lockUI();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }
    };

    private DailyHotelJsonResponseListener mHotelRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    throw new NullPointerException("response == null");
                }

                JSONObject dataJSONObject = response.getJSONObject("data");

                JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                Province selectedProvince = null;
                String regionName = null;

                if (mSelectedProvince != null)
                {
                    selectedProvince = mSelectedProvince;
                    regionName = mSelectedProvince.name;
                } else
                {
                    // 마지막으로 선택한 지역을 가져온다.
                    regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

                    if (Util.isTextEmpty(regionName) == true)
                    {
                        // 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
                        regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "");

                        // 해당 지역이 없는 경우 Province의 첫번째 지역으로 한다.
                        if (Util.isTextEmpty(regionName) == true)
                        {
                            selectedProvince = provinceList.get(0);
                            regionName = selectedProvince.name;
                        }
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
                                    selectedProvince = area;
                                    break;
                                }
                            }
                        }
                    }
                }

                mAreaItemList = makeAreaItemList(provinceList, areaList);

                // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                if (selectedProvince == null)
                {
                    selectedProvince = provinceList.get(0);
                    regionName = selectedProvince.name;
                }

                // 처음 시작시에는 지역이 Area로 저장된 경우 Province로 변경하기 위한 저장값.
                boolean mIsProvinceSetting = baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_REGION_SETTING, false);
                SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
                editor.putBoolean(KEY_PREFERENCE_REGION_SETTING, true);
                editor.commit();

                // 마지막으로 지역이 Area로 되어있으면 Province로 바꾸어 준다.
                if (mIsProvinceSetting == false && selectedProvince instanceof Area)
                {
                    int provinceIndex = ((Area) selectedProvince).getProvinceIndex();

                    for (Province province : provinceList)
                    {
                        if (province.getProvinceIndex() == provinceIndex)
                        {
                            selectedProvince = province;
                            break;
                        }
                    }
                }

                //탭에 들어갈 날짜를 만든다.
                makeTabDate();

                boolean isSelectionTop = isSelectionTop();
                onNavigationItemSelected(selectedProvince, isSelectionTop);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private void makeTabDate()
        {
            //탭에 들어갈 날짜를 만든다.
            SaleTime[] tabSaleTime = null;

            int fragmentSize = mFragmentList.size();

            tabSaleTime = new SaleTime[3];

            for (int i = 0; i < fragmentSize; i++)
            {
                HotelListFragment hotelListFragment = mFragmentList.get(i);

                SaleTime saleTime;

                if (i == 2)
                {
                    saleTime = mTodaySaleTime.getClone(0);
                    tabSaleTime[i] = saleTime;
                } else
                {
                    saleTime = mTodaySaleTime.getClone(i);
                    tabSaleTime[i] = saleTime;
                }

                hotelListFragment.setSaleTime(saleTime);
            }

            // 임시로 여기서 날짜를 넣는다.
            ArrayList<String> dayList = new ArrayList<String>();

            dayList.add(getString(R.string.label_format_tabday, tabSaleTime[0].getDailyDay(), tabSaleTime[0].getDailyDayOftheWeek()));
            dayList.add(getString(R.string.label_format_tabday, tabSaleTime[1].getDailyDay(), tabSaleTime[1].getDailyDayOftheWeek()));

            if (Util.isTextEmpty(mTabIndicator.getSubText(2)) == true)
            {
                SaleTime checkInSaleTime = tabSaleTime[0].getClone(2);
                SaleTime checkOutSaleTime = tabSaleTime[0].getClone(3);

                String checkInDay = getString(R.string.label_format_tabmonth, //
                    checkInSaleTime.getDayOfDaysHotelDateFormat("M"),//
                    checkInSaleTime.getDayOfDaysHotelDateFormat("d"));
                String checkOutDay = getString(R.string.label_format_tabmonth, //
                    checkOutSaleTime.getDayOfDaysHotelDateFormat("M"),//
                    checkOutSaleTime.getDayOfDaysHotelDateFormat("d"));

                String checkInOutDate = checkInDay + "-" + checkOutDay;
                dayList.add(checkInOutDate);

                HotelDaysListFragment fragment = (HotelDaysListFragment) mFragmentList.get(2);
                fragment.initSelectedCheckInOutDate(checkInSaleTime, checkOutSaleTime);
            } else
            {
                dayList.add(mTabIndicator.getSubText(2));
            }

            int tabSize = mTabIndicator.size();

            for (int i = 0; i < tabSize; i++)
            {
                String day = dayList.get(i);

                if (Util.isTextEmpty(day) == true)
                {
                    mTabIndicator.setSubTextEnable(i, false);
                } else
                {
                    mTabIndicator.setSubTextEnable(i, true);
                    mTabIndicator.setSubText(i, day);
                }
            }
        }

        private boolean isSelectionTop()
        {
            boolean isSelectionTop = false;

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

            for (HotelListFragment hotelListFragment : mFragmentList)
            {
                if (hotelListFragment == currentFragment)
                {
                    Province province = hotelListFragment.getProvince();

                    if (province == null || mSelectedProvince.index != province.index || mSelectedProvince.name.equalsIgnoreCase(province.name) == false)
                    {
                        isSelectionTop = true;
                        break;
                    }
                }
            }

            return isSelectionTop;
        }

        private ArrayList<Area> makeAreaList(JSONArray jsonArray)
        {
            ArrayList<Area> areaList = new ArrayList<Area>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Area area = new Area(jsonObject);

                        areaList.add(area);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return areaList;
        }

        private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
        {
            ArrayList<Province> provinceList = new ArrayList<Province>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Province province = new Province(jsonObject);

                        provinceList.add(province);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return provinceList;
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                mTodaySaleTime.setCurrentTime(response.getLong("currentDateTime"));
                mTodaySaleTime.setDailyTime(response.getLong("dailyDateTime"));

                if (mTabIndicator.getVisibility() != View.VISIBLE)
                {
                    initShow();
                }

                if (baseActivity.sharedPreference.contains(KEY_PREFERENCE_BY_SHARE) == true)
                {
                    String param = baseActivity.sharedPreference.getString(KEY_PREFERENCE_BY_SHARE, null);
                    baseActivity.sharedPreference.edit().remove(KEY_PREFERENCE_BY_SHARE).apply();

                    if (param != null)
                    {
                        unLockUI();

                        try
                        {
                            String previousType = Util.getValueForLinkUrl(param, "hotelIndex");

                            if (Util.isTextEmpty(previousType) == false)
                            {
                                // 이전 타입의 화면 이동
                                int hotelIndex = Integer.parseInt(previousType);
                                long dailyTime = Long.parseLong(Util.getValueForLinkUrl(param, "dailyTime"));
                                int dailyDayOfDays = Integer.parseInt(Util.getValueForLinkUrl(param, "dailyDayOfDays"));
                                int nights = Integer.parseInt(Util.getValueForLinkUrl(param, "nights"));

                                if (nights <= 0 || dailyDayOfDays < 0)
                                {
                                    throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
                                }

                                if (mOnUserActionListener != null)
                                {
                                    mOnUserActionListener.selectHotel(hotelIndex, dailyTime, dailyDayOfDays, nights);
                                }
                            } else
                            {
                                // 신규 타입의 화면이동
                                int hotelIndex = Integer.parseInt(Util.getValueForLinkUrl(param, "idx"));
                                long dailyTime = mTodaySaleTime.getDailyTime();
                                int nights = Integer.parseInt(Util.getValueForLinkUrl(param, "nights"));

                                String date = Util.getValueForLinkUrl(param, "date");
                                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                                Date schemeDate = format.parse(date);
                                Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"));

                                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                                if (nights <= 0 || dailyDayOfDays < 0)
                                {
                                    throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
                                }

                                if (mOnUserActionListener != null)
                                {
                                    mOnUserActionListener.selectHotel(hotelIndex, dailyTime, dailyDayOfDays, nights);
                                }
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());

                            // 지역 리스트를 가져온다
                            DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mHotelRegionListJsonResponseListener, baseActivity);
                        }
                    }
                } else
                {
                    // 지역 리스트를 가져온다
                    DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mHotelRegionListJsonResponseListener, baseActivity);
                }

            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }
    };

}
