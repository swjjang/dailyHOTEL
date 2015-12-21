package com.twoheart.dailyhotel.screen.hotellist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.fragment.BaseFragment;
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
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelListViewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HotelMainFragment extends BaseFragment
{
    private  static final int TAB_COUNT = 3;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private HotelFragmentPagerAdapter mFragmentPagerAdapter;

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
        void selectHotel(String hotelName, long hotelIndex, String checkInTime, int nights);

        void selectRegion(Province province);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);

        initToolbar(view);

        mHotelViewType = HOTEL_VIEW_TYPE.LIST;

        mTodaySaleTime = new SaleTime();

        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_today), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_tomorrow));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_selecteday));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mFragmentPagerAdapter = new HotelFragmentPagerAdapter(getChildFragmentManager(), TAB_COUNT, mOnUserActionListener);

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해

        initHide();

        return view;
    }

    private void initToolbar(View view)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        mAppBarLayout = (AppBarLayout)view.findViewById(R.id.appBarLayout);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View spinnerView = inflater.inflate(R.layout.view_actionbar_spinner, null, true);
        TextView textView = (TextView) spinnerView.findViewById(R.id.titleTextView);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(R.string.label_dailyhotel);
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    lockUiComponent();

                    mOnUserActionListener.onClickActionBarArea();
                }
            }
        });

        mToolbar.addView(spinnerView);
        mToolbar.setTag(mToolbar.getId(), textView);

        baseActivity.setSupportActionBar(mToolbar);
    }

    private void setToolbar(String text, boolean isEnabled)
    {
        View view = mToolbar.getChildAt(0);

        TextView textView = (TextView) view.findViewById(R.id.titleTextView);
        textView.setText(text);
    }

    private void initHide()
    {
        setMenuEnabled(false);
    }

    private void initShow()
    {
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

                    MenuItem menuItem = menu.getItem(0);

                    HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

                    switch (currentFragment.getSortType())
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
                HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
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
//                    ((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
                } else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
//                    ((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
                }
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
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
        HotelListFragment hotelListFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

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

        setToolbar(province.name, true);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        String savedRegion = DailyPreference.getInstance(baseActivity).getSelectedRegion();
        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(baseActivity).setPreviouslySelectedRegion(savedRegion);
            DailyPreference.getInstance(baseActivity).setSelectedRegion(province.name);

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
        HotelListFragment hotelListFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

        if (isSelectionTop == true)
        {
            hotelListFragment.setSortType(HotelListFragment.SortType.DEFAULT);

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.invalidateOptionsMenu();
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

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if(mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            mAppBarLayout.setExpanded(true);

            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.refreshAll();
            }

            // Google Analytics
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.PROVINCE, mSelectedProvince.name);
            params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));

            AnalyticsManager.getInstance(getActivity()).recordEvent(mHotelViewType.name(), Action.CLICK, Label.DATE_TAB, params);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageUnSelected();
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
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

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.DATE_TAB, Integer.toString(mTabLayout.getSelectedTabPosition()));
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

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.DATE_TAB, Integer.toString(mTabLayout.getSelectedTabPosition()));

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

                    String region = DailyPreference.getInstance(baseActivity).getSelectedRegion();
                    DailyPreference.getInstance(baseActivity).setGASelectedRegion(region);
                    DailyPreference.getInstance(baseActivity).setGAHotelName(hotel.getName());

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
            mTabLayout.getTabAt(2).setText(checkInDay + "-" + checkOutDay);

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
            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
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
                    regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion();

                    if (Util.isTextEmpty(regionName) == true)
                    {
                        // 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
                        regionName = DailyPreference.getInstance(baseActivity).getPreviouslySelectedRegion();

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
                boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).IsSettingRegion();
                DailyPreference.getInstance(baseActivity).setSettingRegion(true);

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

            tabSaleTime = new SaleTime[TAB_COUNT];

            for (int i = 0; i < TAB_COUNT; i++)
            {
                HotelListFragment hotelListFragment = (HotelListFragment)mFragmentPagerAdapter.getItem(i);

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

            dayList.add(String.format("%s(%s일)", getString(R.string.label_today), tabSaleTime[0].getDailyDay()));
            dayList.add(String.format("%s(%s일)", getString(R.string.label_tomorrow), tabSaleTime[1].getDailyDay()));

            String text = (String) mTabLayout.getTabAt(2).getTag();

            if (Util.isTextEmpty(text) == true)
            {
                SaleTime checkInSaleTime = tabSaleTime[0].getClone(2);
                SaleTime checkOutSaleTime = tabSaleTime[0].getClone(3);

                String checkInDay = checkInSaleTime.getDayOfDaysHotelDateFormat("d");
                String checkOutDay = checkOutSaleTime.getDayOfDaysHotelDateFormat("d");

                HotelDaysListFragment fragment = (HotelDaysListFragment) mFragmentPagerAdapter.getItem(2);
                fragment.initSelectedCheckInOutDate(checkInSaleTime, checkOutSaleTime);

                String checkInOutDate = String.format("%s(%s-%s일)" , getString(R.string.label_day), checkInDay , checkOutDay);

                mTabLayout.getTabAt(2).setTag(checkInOutDate);
                dayList.add(checkInOutDate);
            } else
            {
                dayList.add(getString(R.string.label_selecteday));
            }

            for (int i = 0; i < TAB_COUNT; i++)
            {
                String day = dayList.get(i);
                mTabLayout.getTabAt(i).setText(day);
            }
        }

        private boolean isSelectionTop()
        {
            boolean isSelectionTop = false;

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
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

                if (mTabLayout.getVisibility() != View.VISIBLE)
                {
                    initShow();
                }

                String deepLink = DailyPreference.getInstance(baseActivity).getDeepLink();

                if (Util.isTextEmpty(deepLink)== false)
                {
                    DailyPreference.getInstance(baseActivity).removeDeepLink();

                    unLockUI();

                    try
                    {
                        String previousType = Util.getValueForLinkUrl(deepLink, "hotelIndex");

                        if (Util.isTextEmpty(previousType) == false)
                        {
                            // 이전 타입의 화면 이동
                            int hotelIndex = Integer.parseInt(previousType);
                            long dailyTime = Long.parseLong(Util.getValueForLinkUrl(deepLink, "dailyTime"));
                            int dailyDayOfDays = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "dailyDayOfDays"));
                            int nights = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "nights"));

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
                            int hotelIndex = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "idx"));
                            long dailyTime = mTodaySaleTime.getDailyTime();
                            int nights = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "nights"));

                            String date = Util.getValueForLinkUrl(deepLink, "date");
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
