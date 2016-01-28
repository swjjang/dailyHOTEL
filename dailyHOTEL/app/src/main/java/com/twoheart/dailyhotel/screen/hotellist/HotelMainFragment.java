package com.twoheart.dailyhotel.screen.hotellist;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Hotel;
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
import java.util.List;
import java.util.TimeZone;

public class HotelMainFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener
{
    private static final int TAB_COUNT = 3;
    private int TOOLBAR_HEIGHT;

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private TabLayout mCategoryTabLayout;
    private ViewPager mViewPager;
    private HotelFragmentPagerAdapter mFragmentPagerAdapter;
    private DailyToolbarLayout mDailyToolbarLayout;

    private SaleTime mTodaySaleTime;
    private Province mSelectedProvince;
    private Category mSelectedCategory;

    private boolean mMenuEnabled;
    private boolean mMapEnabled;
    private boolean mDontReloadAtOnResume;
    private boolean mIsHideAppBarlayout;

    private HOTEL_VIEW_TYPE mHotelViewType = HOTEL_VIEW_TYPE.LIST;

    public enum HOTEL_VIEW_TYPE
    {
        LIST,
        MAP,
        GONE, // 목록이 비어있는 경우.
    }

    public interface OnUserActionListener
    {
        void selectHotel(PlaceViewItem hotelListViewItem, SaleTime checkSaleTime);

        void selectHotel(int hotelIndex, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, SaleTime checkOutSaleTime, boolean isListSelectionTop);

        void selectEventBanner(EventBanner eventBanner);

        void toggleViewType();

        void selectSortType(SortType sortType);

        void setLocation(Location location);

        void onClickActionBarArea();

        void setMapViewVisible(boolean isVisible);

        void refreshAll(boolean isShowProgress);

        void expandedAppBar(boolean expanded, boolean animate);

        void showAppBarLayout();

        void hideAppBarLayout();

        void pinAppBarLayout();
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

        initTabLayout(view);

        setHasOptionsMenu(true);

        setMenuEnabled(false);

        return view;
    }

    private void initToolbar(View view)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        TOOLBAR_HEIGHT = (int) baseActivity.getResources().getDimension(R.dimen.toolbar_height_has_tab);

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        mAppBarLayout.addOnOffsetChangedListener(this);

        mDailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        mDailyToolbarLayout.initToolbarRegion(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnUserActionListener.onClickActionBarArea();
            }
        });

        mDailyToolbarLayout.initToolbarRegionMenu(mToolbarOptionsItemSelected);
    }

    private void initTabLayout(View view)
    {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_today), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_tomorrow));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_selecteday));
        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

        mCategoryTabLayout = (TabLayout) view.findViewById(R.id.categoryTabLayout);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mFragmentPagerAdapter = new HotelFragmentPagerAdapter(getChildFragmentManager(), TAB_COUNT, mOnUserActionListener);

        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        if (verticalOffset == -TOOLBAR_HEIGHT && mIsHideAppBarlayout == false)
        {
            mOnUserActionListener.hideAppBarLayout();
            mIsHideAppBarlayout = true;

            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.resetScrollDistance(false);

        } else if (verticalOffset == 0 && mIsHideAppBarlayout == false)
        {
            mOnUserActionListener.pinAppBarLayout();
            mIsHideAppBarlayout = true;

            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.resetScrollDistance(true);

        } else if (verticalOffset < 0 && verticalOffset > -TOOLBAR_HEIGHT)
        {
            mIsHideAppBarlayout = false;
        }
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
    public void onDestroy()
    {
        mAppBarLayout.removeOnOffsetChangedListener(this);

        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        if (mMenuEnabled == true)
        {
            switch (mHotelViewType)
            {
                case LIST:

                    HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

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

                case MAP:
                    mDailyToolbarLayout.setToolbarRegionMenu(mMapEnabled ? R.drawable.navibar_ic_list : -1, -1);
                    break;

                default:
                    mDailyToolbarLayout.setToolbarRegionMenu(-1, -1);
                    break;
            }
        } else
        {
            mDailyToolbarLayout.setToolbarRegionMenu(-1, -1);
        }
    }

    public void setMenuEnabled(boolean enabled)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || (enabled == true && mMapEnabled == false) || mMenuEnabled == enabled)
        {
            return;
        }

        mMenuEnabled = enabled;

        baseActivity.invalidateOptionsMenu();
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

                HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.onActivityResult(requestCode, resultCode, data);

                mOnUserActionListener.expandedAppBar(true, false);
                break;
            }

            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    mOnUserActionListener.selectSortType(SortType.DEFAULT);

                    if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
                    {
                        Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

                        setNavigationItemSelected(province);

                        mOnUserActionListener.refreshAll(true);
                    } else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
                    {
                        Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

                        setNavigationItemSelected(province);

                        mOnUserActionListener.refreshAll(true);
                    }
                }

                mOnUserActionListener.expandedAppBar(true, false);
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

    private void makeTabLayout()
    {
        //탭에 들어갈 날짜를 만든다.
        SaleTime[] tabSaleTime = null;

        tabSaleTime = new SaleTime[TAB_COUNT];

        for (int i = 0; i < TAB_COUNT; i++)
        {
            HotelListFragment hotelListFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(i);

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

        dayList.add(getString(R.string.label_format_tabday, getString(R.string.label_today), tabSaleTime[0].getDailyDay()));
        dayList.add(getString(R.string.label_format_tabday, getString(R.string.label_tomorrow), tabSaleTime[1].getDailyDay()));

        String text = (String) mTabLayout.getTabAt(2).getTag();

        if (Util.isTextEmpty(text) == true)
        {
            String days = getString(R.string.label_selecteday);

            SaleTime checkInSaleTime = tabSaleTime[0].getClone(2);
            SaleTime checkOutSaleTime = tabSaleTime[0].getClone(3);

            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (currentFragment instanceof HotelDaysListFragment)
            {
                String checkInDay = checkInSaleTime.getDayOfDaysHotelDateFormat("M월d일");
                String checkOutDay = checkOutSaleTime.getDayOfDaysHotelDateFormat("M월d일");

                // 선택탭의 이름을 수정한다.
                days = String.format("%s-%s", checkInDay, checkOutDay);
                FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

                mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            }

            HotelDaysListFragment fragment = (HotelDaysListFragment) mFragmentPagerAdapter.getItem(2);
            fragment.initSelectedCheckInOutDate(checkInSaleTime, checkOutSaleTime);

            dayList.add(days);
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

    private void fixCategoryTabLayout(int size)
    {
        int tabCount = mCategoryTabLayout.getTabCount();
        if (tabCount > 0)
        {
            if (size > tabCount)
            {
                int count = size - tabCount;
                for (int i = 0; i < count; i++)
                {
                    mCategoryTabLayout.addTab(mCategoryTabLayout.newTab());
                }
            } else if (size < tabCount)
            {
                for (int i = tabCount - 1; i <= size - 1; i--)
                {
                    mCategoryTabLayout.removeTabAt(i);
                }
            }
        } else
        {
            for (int i = 0; i < size; i++)
            {
                mCategoryTabLayout.addTab(mCategoryTabLayout.newTab());
            }
        }
    }


    private void makeCategoryTabLayout(List<Category> list)
    {
        if (list == null)
        {
            setSelectCategory(Category.ALL);
            mCategoryTabLayout.setVisibility(View.GONE);
            return;
        }

        int size = list.size();

        if (size < 2)
        {
            setSelectCategory(Category.ALL);
            mCategoryTabLayout.setVisibility(View.GONE);
            return;
        }

        fixCategoryTabLayout(size);

        // 선택된 카테고리가 없는 경우 2번째가 항상 선택된다
        if (mSelectedCategory == null)
        {
            setSelectCategory(list.get(1));
        }

        TabLayout.Tab selectedTab = null;

        final float TAB_COUNT = 4.5f;
        final int TAB_WIDTH = (int) (Util.getLCDWidth(getContext()) / TAB_COUNT);

        for (int i = 0; i < size; i++)
        {
            Category category = list.get(i);

            TabLayout.Tab tab = mCategoryTabLayout.getTabAt(i);
            tab.setText(category.name);
            tab.setTag(category);

            ViewGroup tabStrip = (ViewGroup) mCategoryTabLayout.getChildAt(0);
            View tabView = tabStrip.getChildAt(i);

            ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
            layoutParams.width = TAB_WIDTH;
            tabView.setLayoutParams(layoutParams);

            if (mSelectedCategory.code.equalsIgnoreCase(category.code) == true)
            {
                selectedTab = tab;
            }
        }

        if (selectedTab != null)
        {
            selectedTab.select();
        }

        mCategoryTabLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int selectedIndex = mCategoryTabLayout.getSelectedTabPosition();

                mCategoryTabLayout.setScrollPosition(selectedIndex, 0f, false);
            }
        });

        mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);

        FontManager.apply(mCategoryTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
    }

    private void setSelectCategory(Category category)
    {
        if (category == null)
        {
            category = Category.ALL;
        }

        mSelectedCategory = category;

        for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
        {
            hotelListFragment.setSelectedCategory(mSelectedCategory);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setNavigationItemSelected(Province province)
    {
        mSelectedProvince = province;

        for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
        {
            hotelListFragment.setProvince(mSelectedProvince);
        }
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

        setNavigationItemSelected(mSelectedProvince);

        mDailyToolbarLayout.setToolbarRegionText(province.name);
        mDailyToolbarLayout.setToolbarRegionMenuVisibility(true);

        // 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
        String savedRegion = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.HOTEL);
        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(baseActivity).setSelectedOverseaRegion(TYPE.HOTEL, province.isOverseas);
            DailyPreference.getInstance(baseActivity).setSelectedRegion(TYPE.HOTEL, province.name);

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
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.invalidateOptionsMenu();
        }

        hotelListFragment.refreshHotelList(province, isSelectionTop);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                    boolean isInstalledGooglePlayServices = Util.installGooglePlayService((BaseActivity) getActivity());

                    if (isInstalledGooglePlayServices == true)
                    {
                        mOnUserActionListener.toggleViewType();

                        baseActivity.invalidateOptionsMenu();
                    }
                    break;
                }

                case R.drawable.navibar_ic_map:
                {
                    boolean isInstalledGooglePlayServices = Util.installGooglePlayService((BaseActivity) getActivity());

                    if (isInstalledGooglePlayServices == true)
                    {
                        mOnUserActionListener.toggleViewType();

                        baseActivity.invalidateOptionsMenu();
                    }
                    break;
                }

                case R.drawable.navibar_ic_sorting_01:
                case R.drawable.navibar_ic_sorting_02:
                case R.drawable.navibar_ic_sorting_03:
                case R.drawable.navibar_ic_sorting_04:
                {
                    HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    currentFragment.showSortDialogView();
                    break;
                }
            }

            mOnUserActionListener.expandedAppBar(true, true);
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            lockUI();

            mTabLayout.setOnTabSelectedListener(null);

            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            mOnUserActionListener.expandedAppBar(true, true);

            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            mOnUserActionListener.refreshAll(true);

            mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

            // Google Analytics
            if (mSelectedProvince != null)
            {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.PROVINCE, mSelectedProvince.name);
                params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));

                AnalyticsManager.getInstance(getActivity()).recordEvent(mHotelViewType.name(), Action.CLICK, Label.DATE_TAB, params);
            }
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
            if (isLockUiComponent() == true)
            {
                return;
            }

            mOnUserActionListener.expandedAppBar(true, true);

            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            // Google Analytics
            if (mSelectedProvince != null)
            {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.PROVINCE, mSelectedProvince.name);
                params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));

                AnalyticsManager.getInstance(getActivity()).recordEvent(mHotelViewType.name(), Action.CLICK, Label.DATE_TAB, params);
            }
        }
    };

    private TabLayout.OnTabSelectedListener mOnCategoryTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            mCategoryTabLayout.setOnTabSelectedListener(null);

            Category category = (Category) tab.getTag();
            setSelectCategory(category);

            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.requestFilteringCategory();

            mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
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
        public void selectHotel(PlaceViewItem placeViewItem, SaleTime checkSaleTime)
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

            if (placeViewItem == null)
            {
                unLockUI();
                return;
            }

            lockUI();

            switch (placeViewItem.getType())
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Hotel hotel = placeViewItem.<Hotel>getItem();

                    String region = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.HOTEL);
                    DailyPreference.getInstance(baseActivity).setGASelectedRegion(region);
                    DailyPreference.getInstance(baseActivity).setGAHotelName(hotel.getName());

                    Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotel.getIdx());
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, hotel.nights);

                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotel.getName());
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, hotel.imageUrl);

                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);

                    mUserAnalyticsActionListener.selectHotel(hotel.getName(), hotel.getIdx(), checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), hotel.nights);
                    break;
                }

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

            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        }

        @Override
        public void selectDay(SaleTime checkInSaleTime, SaleTime checkOutSaleTime, boolean isListSelectionTop)
        {
            if (isLockUiComponent() == true || checkInSaleTime == null || checkOutSaleTime == null || isAdded() == false)
            {
                return;
            }

            lockUiComponent();

            String checkInDay = checkInSaleTime.getDayOfDaysHotelDateFormat("M월d일");
            String checkOutDay = checkOutSaleTime.getDayOfDaysHotelDateFormat("M월d일");

            // 선택탭의 이름을 수정한다.
            mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            mTabLayout.getTabAt(2).setText(String.format("%s-%s", checkInDay, checkOutDay));
            FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

            refreshHotelList(mSelectedProvince, isListSelectionTop);
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
                        selectHotel(eventBanner.index, dailyTime, dailyDayOfDays, eventBanner.nights);
                    } else
                    {
                        Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);

                        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, eventBanner.index);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, eventBanner.nights);

                        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
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
        public void selectSortType(SortType sortType)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                hotelListFragment.setSortType(sortType);
            }

            baseActivity.invalidateOptionsMenu();
        }

        @Override
        public void setLocation(Location location)
        {
            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                hotelListFragment.setLocation(location);
            }
        }

        @Override
        public void onClickActionBarArea()
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Intent intent = RegionListActivity.newInstance(baseActivity, TYPE.HOTEL, mSelectedProvince);
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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            lockUI(isShowProgress);
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }

        @Override
        public void expandedAppBar(boolean expanded, boolean animate)
        {
            mAppBarLayout.setExpanded(expanded, animate);
        }

        @Override
        public void showAppBarLayout()
        {
            if (mDailyToolbarLayout == null)
            {
                return;
            }

            Toolbar toolbar = mDailyToolbarLayout.getToolbar();

            if (toolbar == null)
            {
                return;
            }

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            if (params != null &&//
                params.getScrollFlags() != (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS))
            {
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                toolbar.setLayoutParams(params);
            }
        }

        @Override
        public void hideAppBarLayout()
        {
            if (mDailyToolbarLayout == null)
            {
                return;
            }

            Toolbar toolbar = mDailyToolbarLayout.getToolbar();

            if (toolbar == null)
            {
                return;
            }

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            if (params != null &&//
                params.getScrollFlags() != AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
            {
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
                toolbar.setLayoutParams(params);
            }
        }

        @Override
        public void pinAppBarLayout()
        {
            if (mDailyToolbarLayout == null)
            {
                return;
            }

            Toolbar toolbar = mDailyToolbarLayout.getToolbar();

            if (toolbar == null)
            {
                return;
            }

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            if (params != null &&//
                params.getScrollFlags() != 0)
            {
                params.setScrollFlags(0);
                toolbar.setLayoutParams(params);
            }
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
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
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
                        String regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.HOTEL);

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

                    // 처음 시작시에는 지역이 Area로 저장된 경우 Province로 변경하기 위한 저장값.
                    boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).isSettingRegion(TYPE.HOTEL);
                    DailyPreference.getInstance(baseActivity).setSettingRegion(TYPE.HOTEL, true);

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

                    {
                        List<Category> list = new ArrayList<>();
                        list.add(new Category("전체", "all"));
                        list.add(new Category("특급", "special"));
                        list.add(new Category("부띠끄", "boutique"));
                        list.add(new Category("펜션", "pension"));
                        list.add(new Category("디자인", "design"));
                        list.add(new Category("풀빌라", "fullvilla"));
                        list.add(new Category("모텔", "motel"));

                        makeCategoryTabLayout(list);
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
                mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
                unLockUI();
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

                String deepLink = DailyPreference.getInstance(baseActivity).getDeepLink();

                if (Util.isTextEmpty(deepLink) == false)
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

                        //탭에 들어갈 날짜를 만든다.
                        makeTabLayout();

                        // 지역 리스트를 가져온다
                        DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mHotelRegionListJsonResponseListener, baseActivity);
                    }
                } else
                {
                    //탭에 들어갈 날짜를 만든다.
                    makeTabLayout();

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
