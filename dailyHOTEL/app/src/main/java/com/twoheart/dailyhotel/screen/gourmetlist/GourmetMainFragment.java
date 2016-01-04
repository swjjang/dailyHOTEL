package com.twoheart.dailyhotel.screen.gourmetlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.gourmetdetail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hoteldetail.HotelDetailActivity;
import com.twoheart.dailyhotel.screen.regionlist.RegionListActivity;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class GourmetMainFragment extends PlaceMainFragment
{
    private static final int TAB_COUNT = 3;

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private GourmetFragmentPagerAdapter mFragmentPagerAdapter;
    private Province mSelectedProvince;
    private DailyToolbarLayout mDailyToolbarLayout;

    public interface OnUserActionListener
    {
        void selectPlace(PlaceViewItem baseListViewItem, SaleTime checkSaleTime);

        void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, boolean isListSelectionTop);

        void selectEventBanner(EventBanner eventBanner);

        void toggleViewType();

        void showSortDialogView();

        void onClickActionBarArea();

        void setMapViewVisible(boolean isVisible);

        void refreshAll(boolean isShowProgress);

        void expandedAppBar(boolean expanded);
    }

    private interface OnUserAnalyticsActionListener
    {
        void selectPlace(String name, long index, String checkInTime);

        void selectRegion(Province province);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_main, container, false);

        initToolbar(view);

        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_today), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_tomorrow));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_selecteday));
        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mFragmentPagerAdapter = new GourmetFragmentPagerAdapter(getChildFragmentManager(), TAB_COUNT, mOnGourmetUserActionListener);

        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        return view;
    }

    private void initToolbar(View view)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        mDailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        mDailyToolbarLayout.initToolbarRegion(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnGourmetUserActionListener.onClickActionBarArea();
            }
        });

        mDailyToolbarLayout.initToolbarRegionMenu(mToolbarOptionsItemSelected);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        if (mMenuEnabled == true)
        {
            switch (mViewType)
            {
                case LIST:
                {

                    GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

                    switch (currentFragment.getSortType())
                    {
                        case DEFAULT:
                            mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_map : -1, R.drawable.navibar_ic_sorting_01);
                            break;

                        case DISTANCE:
                            mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_map : -1, R.drawable.navibar_ic_sorting_02);
                            break;

                        case LOW_PRICE:
                            mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_map : -1, R.drawable.navibar_ic_sorting_03);
                            break;

                        case HIGH_PRICE:
                            mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_map : -1, R.drawable.navibar_ic_sorting_04);
                            break;
                    }
                    break;
                }

                case MAP:
                    mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_list : -1, -1);
                    break;
            }
        } else
        {
            mDailyToolbarLayout.setToolbarRegionMenu(-1, -1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

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

        mDailyToolbarLayout.setToolbarRegionText(province.name);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        String savedRegion = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.FNB);

        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(baseActivity).setSelectedOverseaRegion(TYPE.FNB, province.isOverseas);
            DailyPreference.getInstance(baseActivity).setSelectedRegion(TYPE.FNB, province.name);

            isSelectionTop = true;
        }

        if (mOnUserAnalyticsActionListener != null)
        {
            mOnUserAnalyticsActionListener.selectRegion(province);
        }

        refreshList(province, isSelectionTop);
    }

    @Override
    public void requestRegionList(BaseActivity baseActivity)
    {
        // 지역 리스트를 가져온다
        DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
    }

    @Override
    public void refreshList(Province province, boolean isSelectionTop)
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

        if (isSelectionTop == true)
        {
            gourmetListFragment.setSortType(GourmetListFragment.SortType.DEFAULT);

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.invalidateOptionsMenu();
        }

        gourmetListFragment.refreshList(province, isSelectionTop);
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

        switch (requestCode)
        {
            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.onActivityResult(requestCode, resultCode, data);

                mAppBarLayout.setExpanded(true, false);
                break;
            }

            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_REGIONLIST:
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

                            refreshAll();
                        } else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
                        {
                            Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

                            setNavigationItemSelected(province);

                            refreshAll();
                        }
                    }
                }

                mAppBarLayout.setExpanded(true, false);
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void refreshAll()
    {
        mOnGourmetUserActionListener.refreshAll(true);
    }

    @Override
    public void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights)
    {
        mOnGourmetUserActionListener.selectPlace(index, dailyTime, dailyDayOfDays, nights);
    }

    @Override
    public void makeTabLayout()
    {
        SaleTime[] tabSaleTime = null;

        tabSaleTime = new SaleTime[TAB_COUNT];

        for (int i = 0; i < TAB_COUNT; i++)
        {
            GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(i);

            SaleTime saleTime = mTodaySaleTime.getClone(i);
            tabSaleTime[i] = saleTime;

            gourmetListFragment.setSaleTime(saleTime);
        }

        // 임시로 여기서 날짜를 넣는다.
        ArrayList<String> dayList = new ArrayList<String>();

        dayList.add(getString(R.string.label_format_tabday, getString(R.string.label_today), tabSaleTime[0].getDailyDay()));
        dayList.add(getString(R.string.label_format_tabday, getString(R.string.label_tomorrow), tabSaleTime[1].getDailyDay()));

        String text = (String) mTabLayout.getTabAt(2).getTag();

        if (Util.isTextEmpty(text) == true)
        {
            mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            dayList.add(getString(R.string.label_selecteday));
        } else
        {
            dayList.add(mTabLayout.getTabAt(2).getText().toString());
        }

        for (int i = 0; i < TAB_COUNT; i++)
        {
            String day = dayList.get(i);
            mTabLayout.getTabAt(i).setText(day);
        }

        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mToolbarOptionsItemSelected = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            Integer tag = (Integer) v.getTag();

            if (tag == null)
            {
                return;
            }

            switch (tag)
            {
                case R.drawable.navibar_ic_list:
                {
                    boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

                    if (isInstalledGooglePlayServices == true)
                    {
                        mOnGourmetUserActionListener.toggleViewType();

                        baseActivity.invalidateOptionsMenu();
                    }
                    break;
                }

                case R.drawable.navibar_ic_map:
                {
                    boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

                    if (isInstalledGooglePlayServices == true)
                    {
                        mOnGourmetUserActionListener.toggleViewType();

                        baseActivity.invalidateOptionsMenu();
                    }
                    break;
                }

                case R.drawable.navibar_ic_sorting_01:
                case R.drawable.navibar_ic_sorting_02:
                case R.drawable.navibar_ic_sorting_03:
                case R.drawable.navibar_ic_sorting_04:
                {
                    GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    currentFragment.showSortDialogView();
                    break;
                }
            }
        }
    };


    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            mTabLayout.setOnTabSelectedListener(null);

            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            mAppBarLayout.setExpanded(true);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            mOnGourmetUserActionListener.refreshAll(true);

            mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.PROVINCE, mSelectedProvince.name);
            params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));

            AnalyticsManager.getInstance(getActivity()).recordEvent(mViewType.name(), Action.CLICK, Label.DATE_TAB, params);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageUnSelected();
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            mAppBarLayout.setExpanded(true);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.PROVINCE, mSelectedProvince.name);
            params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));

            AnalyticsManager.getInstance(getActivity()).recordEvent(mViewType.name(), Action.CLICK, Label.DATE_TAB, params);
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

            switch (baseListViewItem.getType())
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Gourmet gourmet = baseListViewItem.<Gourmet>getItem();

                    String region = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.FNB);


                    DailyPreference.getInstance(baseActivity).setGASelectedPlaceRegion(region);
                    DailyPreference.getInstance(baseActivity).setGASelectedPlaceName(gourmet.name);

                    Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);

                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);

                    mOnUserAnalyticsActionListener.selectPlace(gourmet.name, gourmet.index, checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
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

            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
        }

        @Override
        public void selectDay(SaleTime checkInSaleTime, boolean isListSelectionTop)
        {
            if (isLockUiComponent() == true || checkInSaleTime == null || isAdded() == false)
            {
                return;
            }

            lockUiComponent();

            String checkInDay = checkInSaleTime.getDayOfDaysHotelDateFormat("M월d일");

            // 선택탭의 이름을 수정한다.
            mTabLayout.getTabAt(2).setText(checkInDay);
            FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

            refreshList(mSelectedProvince, isListSelectionTop);

            releaseUiComponent();
        }

        @Override
        public void selectEventBanner(EventBanner eventBanner)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || isLockUiComponent() == true)
            {
                return;
            }

            if (eventBanner.isDeepLink() == true)
            {
                try
                {
                    long dailyTime = mTodaySaleTime.getDailyTime();

                    Calendar calendar = DailyCalendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
                    calendar.setTimeInMillis(eventBanner.checkInTime);

                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    Date schemeDate = format.parse(format.format(calendar.getTime()));
                    Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"));

                    int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                    if (eventBanner.isHotel() == true)
                    {
                        Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, eventBanner.index);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, eventBanner.nights);

                        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
                    } else
                    {
                        selectPlace(eventBanner.index, dailyTime, dailyDayOfDays, eventBanner.nights);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            } else
            {
                Intent intent = EventWebActivity.newInstance(baseActivity, eventBanner.webLink);
                startActivity(intent);
            }
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
            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

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

            for (GourmetListFragment placeListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                boolean isCurrentFragment = placeListFragment == currentFragment;

                placeListFragment.setViewType(mViewType, isCurrentFragment);
            }

            unLockUI();
        }

        @Override
        public void showSortDialogView()
        {
            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.showSortDialogView();
        }

        @Override
        public void onClickActionBarArea()
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = RegionListActivity.newInstance(baseActivity, TYPE.FNB, mSelectedProvince);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);
        }

        @Override
        public void setMapViewVisible(boolean isVisible)
        {
            mMapEnabled = isVisible;
            setMenuEnabled(isVisible);
        }

        @Override
        public void refreshAll(boolean isShowProgress)
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

            lockUI(isShowProgress);
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }

        @Override
        public void expandedAppBar(boolean expanded)
        {
            mAppBarLayout.setExpanded(expanded);
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
                int msg_code = response.getInt("msgCode");

                if (msg_code == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                    ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                    Province selectedProvince = null;

                    if (mSelectedProvince != null)
                    {
                        selectedProvince = mSelectedProvince;
                    } else
                    {
                        // 마지막으로 선택한 지역을 가져온다.
                        String regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.FNB);

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
                    }

                    // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                    if (selectedProvince == null)
                    {
                        selectedProvince = provinceList.get(0);
                    }

                    boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).isSettingRegion(TYPE.FNB);
                    DailyPreference.getInstance(baseActivity).setSettingRegion(TYPE.FNB, true);

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

                    boolean isSelectionTop = isSelectionTop();
                    onNavigationItemSelected(selectedProvince, isSelectionTop);
                } else
                {
                    String message = response.getString("msg");

                    onInternalError(message);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private boolean isSelectionTop()
        {
            boolean isSelectionTop = false;

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            for (GourmetListFragment gourmetListFragment : mFragmentPagerAdapter.getFragmentList())
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

        private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
        {
            ArrayList<Area> areaList = new ArrayList<Area>();

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
                        Province province = new Province(jsonObject, null);

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
