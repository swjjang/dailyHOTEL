/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelTabBookingFragment (호텔 예약 탭)
 * <p>
 * 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.GourmetDetailActivity;
import com.twoheart.dailyhotel.activity.SelectAreaActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;
import com.twoheart.dailyhotel.view.widget.TabIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetMainFragment extends PlaceMainFragment
{
    private TabIndicator mTabIndicator;
    private ArrayList<GourmetListFragment> mFragmentList;
    private ArrayList<AreaItem> mAreaItemList;
    private Province mSelectedProvince;

    private FragmentViewPager mFragmentViewPager;

    private interface OnUserAnalyticsActionListener
    {
        void selectPlace(String name, long index, String checkInTime);

        void selectRegion(Province province);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_main, container, false);

        mTabIndicator = (TabIndicator) view.findViewById(R.id.tabindicator);

        ArrayList<String> titleList = new ArrayList<String>();
        titleList.add(getString(R.string.label_today));
        titleList.add(getString(R.string.label_tomorrow));
        titleList.add(getString(R.string.label_selecteday));

        mTabIndicator.setData(titleList, true);
        mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

        mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
        mFragmentList = new ArrayList<GourmetListFragment>();

        setOnUserActionListener(mOnGourmetUserActionListener);

        GourmetListFragment gourmetListFragment01 = new GourmetListFragment();
        gourmetListFragment01.setUserActionListener(mOnGourmetUserActionListener);
        mFragmentList.add(gourmetListFragment01);

        GourmetListFragment gourmetListFragment02 = new GourmetListFragment();
        gourmetListFragment02.setUserActionListener(mOnGourmetUserActionListener);
        mFragmentList.add(gourmetListFragment02);

        GourmetDaysListFragment gourmetListFragment03 = new GourmetDaysListFragment();
        gourmetListFragment03.setUserActionListener(mOnGourmetUserActionListener);
        mFragmentList.add(gourmetListFragment03);

        mFragmentViewPager.setData(mFragmentList);
        mFragmentViewPager.setAdapter(getChildFragmentManager());

        mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
        mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

        return view;
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
            switch (mViewType)
            {
                case LIST:
                    inflater.inflate(R.menu.actionbar_icon_map, menu);

                    MenuItem menuItem = menu.getItem(0);

                    GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();

                    switch (gourmetListFragment.getSortType())
                    {
                        case DEFAULT:
                            menuItem.setIcon(R.drawable.actionbar_ic_sorting_01);
                            break;

                        case DISTANCE:
                            menuItem.setIcon(R.drawable.actionbar_ic_sorting_02);
                            break;

                        case LOW_PRICE:
                            menuItem.setIcon(R.drawable.actionbar_ic_sorting_03);
                            break;

                        case HIGH_PRICE:
                            menuItem.setIcon(R.drawable.actionbar_ic_sorting_04);
                            break;
                    }
                    break;

                case MAP:
                    inflater.inflate(R.menu.actionbar_icon_list, menu);
                    break;

                default:
                    break;
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
                boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

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
                boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

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
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.showSortDialogView();
                }
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void showSlidingDrawer()
    {
        mTabIndicator.setVisibility(View.VISIBLE);

        setMenuEnabled(true);
    }

    @Override
    protected void hideSlidingDrawer()
    {
        mTabIndicator.setVisibility(View.INVISIBLE);

        setMenuEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();

        if (gourmetListFragment != null)
        {
            gourmetListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setNavigationItemSelected(Province province)
    {
        mSelectedProvince = province;
    }

    public void onNavigationItemSelected(Province province, boolean isSelectionTop)
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

        boolean isShowSpinner = mAreaItemList != null && mAreaItemList.size() > 1 ? true : false;
        baseActivity.setActionBarRegionEnable(isShowSpinner);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        if (province.name.equalsIgnoreCase(baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "")) == false)
        {
            SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
            editor.putString(KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE, baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, ""));
            editor.putString(KEY_PREFERENCE_FNB_REGION_SELECT, province.name);
            editor.commit();

            isSelectionTop = true;
        }

        if (mOnUserAnalyticsActionListener != null)
        {
            mOnUserAnalyticsActionListener.selectRegion(province);
        }

        refreshList(province, isSelectionTop);
    }

    @Override
    protected void requestRegionList(BaseActivity baseActivity)
    {
        // 지역 리스트를 가져온다
        DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
    }

    @Override
    protected void refreshList(Province province, boolean isSelectionTop)
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();

        if (isSelectionTop == true)
        {
            gourmetListFragment.setSortType(PlaceListFragment.SortType.DEFAULT);
        }

        gourmetListFragment.refreshList(province, isSelectionTop);
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }

    @Override
    protected void setActionBarAnimationLock(boolean isLock)
    {
        PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();

        if (currentFragment != null)
        {
            if (isLock == true)
            {
                currentFragment.setActionBarAnimationLock(false);
            } else
            {
                currentFragment.showActionBarAnimatoin();
                currentFragment.setActionBarAnimationLock(true);
            }
        }
    }

    @Override
    protected boolean isEnabledRegionMenu()
    {
        if (mAreaItemList != null && mAreaItemList.size() > 1)
        {
            return true;
        } else
        {
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private TabIndicator.OnTabSelectedListener mOnTabSelectedListener = new TabIndicator.OnTabSelectedListener()
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
                PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.onPageSelected(false);
            } else
            {
                mFragmentViewPager.setCurrentItem(position);
            }
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
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
                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();

                for (PlaceListFragment placeListFragment : mFragmentList)
                {
                    if (placeListFragment == currentFragment)
                    {
                        placeListFragment.onPageSelected(true);
                    } else
                    {
                        placeListFragment.onPageUnSelected();
                    }
                }

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.refreshAll();
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.PROVINCE, mSelectedProvince.name);
                params.put(Label.DATE_TAB, mTabIndicator.getMainText(position));

                AnalyticsManager.getInstance(baseActivity).recordEvent(mViewType.name(), Action.CLICK, Label.DATE_TAB, params);
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
                    //                    PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();
                    //                    currentFragment.setFloatingActionButtonVisible(true);
                    break;
                }

                case ViewPager.SCROLL_STATE_DRAGGING:
                {
                    //                    PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();
                    //                    currentFragment.setFloatingActionButtonVisible(false);
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

    private OnUserAnalyticsActionListener mOnUserAnalyticsActionListener = new OnUserAnalyticsActionListener()
    {
        @Override
        public void selectPlace(String name, long index, String checkInTime)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.FNB_INDEX, String.valueOf(index));
            params.put(Label.CHECK_IN, checkInTime);

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mViewType.name(), Action.CLICK, name, params);
        }

        @Override
        public void selectRegion(Province province)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mViewType.name(), Action.CLICK, province.name, params);
        }
    };

    private OnUserActionListener mOnGourmetUserActionListener = new OnUserActionListener()
    {
        @Override
        public void selectPlace(PlaceViewItem baseListViewItem, SaleTime checkSaleTime)
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

            lockUI();

            if (baseListViewItem == null)
            {
                unLockUI();
                return;
            }

            switch (baseListViewItem.type)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Gourmet fnb = (Gourmet) baseListViewItem.getPlace();

                    String region = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "");
                    SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_PLACE_REGION_SELECT_GA, region);
                    editor.putString(KEY_PREFERENCE_PLACE_NAME_GA, fnb.name);
                    editor.commit();

                    Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, fnb.index);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, fnb.name);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, fnb.imageUrl);

                    startActivityForResult(intent, CODE_REQUEST_FRAGMENT_PLACE_MAIN);

                    mOnUserAnalyticsActionListener.selectPlace(fnb.name, fnb.index, checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
                    break;
                }

                case PlaceViewItem.TYPE_SECTION:
                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || index < 0)
            {
                return;
            }

            if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);

            intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, index);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

            startActivityForResult(intent, CODE_REQUEST_FRAGMENT_PLACE_MAIN);
        }

        @Override
        public void selectDay(SaleTime checkInSaleTime, boolean isListSelectionTop)
        {
            if (isLockUiComponent() == true || checkInSaleTime == null || isAdded() == false)
            {
                return;
            }

            lockUiComponent();

            String checkInDay = getString(R.string.label_format_gourmet_tabmonth, //
                checkInSaleTime.getDayOfDaysHotelDateFormat("M"),//
                checkInSaleTime.getDayOfDaysHotelDateFormat("d"), checkInSaleTime.getDailyDayOftheWeek());

            // 선택탭의 이름을 수정한다.
            mTabIndicator.setSubTextEnable(2, true);
            mTabIndicator.setSubText(2, checkInDay);

            refreshList(mSelectedProvince, isListSelectionTop);

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

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();

            if (currentFragment.hasSalesPlace() == false)
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

            switch (mViewType)
            {
                case LIST:
                    mViewType = VIEW_TYPE.MAP;
                    break;

                case MAP:
                    mViewType = VIEW_TYPE.LIST;
                    break;

                default:
                    break;
            }

            for (PlaceListFragment placeListFragment : mFragmentList)
            {
                boolean isCurrentFragment = placeListFragment == currentFragment;

                placeListFragment.setViewType(mViewType, isCurrentFragment);
            }

            unLockUI();
        }

        @Override
        public void showSortDialogView()
        {
            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();
            currentFragment.showSortDialogView();
        }

        @Override
        public void onClickActionBarArea()
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            if (mAreaItemList == null || mAreaItemList.size() == 1)
            {
                return;
            }

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "");

                    if (Util.isTextEmpty(regionName) == true)
                    {
                        // 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
                        regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE, "");

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

                boolean mIsProvinceSetting = baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_FNB_REGION_SETTING, false);
                SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
                editor.putBoolean(KEY_PREFERENCE_FNB_REGION_SETTING, true);
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
            SaleTime[] tabSaleTime = null;

            int fragmentSize = mFragmentList.size();

            tabSaleTime = new SaleTime[3];

            for (int i = 0; i < fragmentSize; i++)
            {
                GourmetListFragment gourmetListFragment = mFragmentList.get(i);

                SaleTime saleTime = mTodaySaleTime.getClone(i);
                tabSaleTime[i] = saleTime;

                gourmetListFragment.setSaleTime(saleTime);
            }

            // 임시로 여기서 날짜를 넣는다.
            ArrayList<String> dayList = new ArrayList<String>();

            dayList.add(getString(R.string.label_format_tabday, tabSaleTime[0].getDailyDay(), tabSaleTime[0].getDailyDayOftheWeek()));
            dayList.add(getString(R.string.label_format_tabday, tabSaleTime[1].getDailyDay(), tabSaleTime[1].getDailyDayOftheWeek()));

            if (Util.isTextEmpty(mTabIndicator.getSubText(2)) == true)
            {
                String checkInDay = getString(R.string.label_format_gourmet_tabmonth, //
                    tabSaleTime[2].getDayOfDaysHotelDateFormat("M"),//
                    tabSaleTime[2].getDayOfDaysHotelDateFormat("d"), tabSaleTime[2].getDailyDayOftheWeek());

                dayList.add(checkInDay);
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
            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();

            for (GourmetListFragment gourmetListFragment : mFragmentList)
            {
                if (gourmetListFragment == currentFragment)
                {
                    Province province = gourmetListFragment.getProvince();

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
}
