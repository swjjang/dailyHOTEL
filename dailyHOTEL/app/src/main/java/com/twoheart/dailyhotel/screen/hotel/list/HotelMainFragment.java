package com.twoheart.dailyhotel.screen.hotel.list;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.HotelCurationActivity;
import com.twoheart.dailyhotel.screen.hotel.region.HotelRegionListActivity;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.widget.DailyFloatingActionButtonBehavior;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HotelMainFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener
{
    private static final int TAB_COUNT = 3;
    private int TOOLBAR_HEIGHT;

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private TabLayout mCategoryTabLayout;
    private View mUnderLine;
    private ViewPager mViewPager;
    private HotelFragmentPagerAdapter mFragmentPagerAdapter;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mFloatingActionView;
    private DailyFloatingActionButtonBehavior mDailyFloatingActionButtonBehavior;

    private SaleTime mTodaySaleTime;
    private boolean mDontReloadAtOnResume;
    private HotelCurationOption mCurationOption;
    private List<EventBanner> mEventBannerList;

    private ViewType mViewType = ViewType.LIST;

    public interface OnCommunicateListener
    {
        void selectHotel(PlaceViewItem hotelListViewItem, SaleTime checkSaleTime);

        void selectHotel(int hotelIndex, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, SaleTime checkOutSaleTime, boolean isListSelectionTop);

        void selectEventBanner(EventBanner eventBanner);

        void toggleViewType();

        void setScrollListTop(boolean scrollListTop);

        void refreshAll(boolean isShowProgress);

        void refreshCompleted();

        void showFloatingActionButton();

        void hideFloatingActionButton();

        void expandedAppBar(boolean expanded, boolean animate);

        HotelCurationOption getCurationOption();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);

        initToolbar(view);

        mViewType = ViewType.LIST;

        mTodaySaleTime = new SaleTime();

        mCurationOption = new HotelCurationOption();

        initDateTabLayout(view);

        initFloatingActionButton(view);

        setHasOptionsMenu(true);

        return view;
    }

    private void initToolbar(View view)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        TOOLBAR_HEIGHT = (int) baseActivity.getResources().getDimension(R.dimen.toolbar_height_has_tab);

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        View toolbar = view.findViewById(R.id.toolbar);

        mAppBarLayout.addOnOffsetChangedListener(this);

        mDailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        mDailyToolbarLayout.initToolbarRegion(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

                Intent intent = HotelRegionListActivity.newInstance(getContext(), getProvince(), currentFragment.getCheckInSaleTime(), currentFragment.getNights());
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);
            }
        });

        mDailyToolbarLayout.initToolbarRegionMenu(mToolbarOptionsItemSelected);
        onPrepareOptionsMenu(mViewType);
    }

    private void initDateTabLayout(View view)
    {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_today), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_tomorrow));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_selecteday));
        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

        mCategoryTabLayout = (TabLayout) view.findViewById(R.id.categoryTabLayout);
        mUnderLine = view.findViewById(R.id.underLine);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mFragmentPagerAdapter = new HotelFragmentPagerAdapter(getChildFragmentManager(), TAB_COUNT, mOnCommunicateListener);

        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                mOnCommunicateListener.hideFloatingActionButton();
            }

            @Override
            public void onPageSelected(int position)
            {
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    mOnCommunicateListener.showFloatingActionButton();
                }
            }
        });
    }

    private void initFloatingActionButton(View view)
    {
        mFloatingActionView = view.findViewById(R.id.floatingActionView);
        mFloatingActionView.setVisibility(View.GONE);
        mFloatingActionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    return;
                }

                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = HotelCurationActivity.newInstance(baseActivity, getProvince().isOverseas, mViewType, mCurationOption);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CURATION);
                baseActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

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

                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        if (mDailyFloatingActionButtonBehavior == null)
        {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFloatingActionView.getLayoutParams();
            mDailyFloatingActionButtonBehavior = (DailyFloatingActionButtonBehavior) layoutParams.getBehavior();
        }

        mDailyFloatingActionButtonBehavior.setScale(mFloatingActionView, ((float) verticalOffset) / TOOLBAR_HEIGHT);
    }

    @Override
    public void onResume()
    {
        mOnCommunicateListener.expandedAppBar(true, false);
        mDailyToolbarLayout.resizeToolbarRegionText();

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            lockUI();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }

        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        if (mAppBarLayout != null)
        {
            mAppBarLayout.removeOnOffsetChangedListener(this);
        }

        super.onDestroy();
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
            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    clearCurationOption();
                    updateFilteredFloatingActionButton();
                    mOnCommunicateListener.setScrollListTop(true);

                    if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
                    {
                        Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

                        setProvince(province);

                        mOnCommunicateListener.refreshAll(true);
                    }
                }

                mOnCommunicateListener.expandedAppBar(true, false);
                break;
            }

            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.onActivityResult(requestCode, resultCode, data);

                mOnCommunicateListener.expandedAppBar(true, false);
                break;
            }

            case CODE_REQUEST_ACTIVITY_CURATION:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    HotelCurationOption curationOption = data.getParcelableExtra(HotelCurationActivity.INTENT_EXTRA_DATA_CURATION_OPTIONS);

                    if (curationOption != null)
                    {
                        mOnCommunicateListener.setScrollListTop(true);

                        mCurationOption.setSortType(curationOption.getSortType());
                        mCurationOption.person = curationOption.person;
                        mCurationOption.flagBedTypeFilters = curationOption.flagBedTypeFilters;
                        mCurationOption.flagAmenitiesFilters = curationOption.flagAmenitiesFilters;

                        if (curationOption.getSortType() == SortType.DISTANCE)
                        {
                            searchMyLocation();
                        } else
                        {
                            curationCurrentFragment();
                        }
                    }
                }
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                mDontReloadAtOnResume = true;

                if (mViewType == ViewType.MAP)
                {
                    HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    currentFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH:
            {
                mDontReloadAtOnResume = true;
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mViewType == ViewType.LIST)
        {
            if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION)
            {
                searchMyLocation();
            }
        } else if (mViewType == ViewType.MAP)
        {
            HotelListFragment hotelListFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (hotelListFragment != null)
            {
                hotelListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void onPrepareOptionsMenu(ViewType viewType)
    {
        switch (viewType)
        {
            case LIST:
                mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_map, R.drawable.navibar_ic_search_black);
                break;

            case MAP:
                mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_list, R.drawable.navibar_ic_search_black);
                break;

            default:
                mDailyToolbarLayout.setToolbarMenu(-1, -1);
                break;
        }
    }

    private void makeDateTabLayout()
    {
        //탭에 들어갈 날짜를 만든다.
        SaleTime[] tabSaleTime = new SaleTime[TAB_COUNT];
        SaleTime saleTime;
        HotelListFragment hotelListFragment;

        for (int i = 0; i < TAB_COUNT; i++)
        {
            hotelListFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(i);

            saleTime = mTodaySaleTime.getClone(i);
            tabSaleTime[i] = saleTime;

            if (i == 2)
            {
                String text = (String) mTabLayout.getTabAt(2).getTag();

                if (Util.isTextEmpty(text) == false)
                {
                    continue;
                }
            }

            hotelListFragment.setCheckInSaleTime(saleTime);
            hotelListFragment.setCheckOutSaleTime(saleTime.getClone(saleTime.getOffsetDailyDay() + 1));
        }

        // 임시로 여기서 날짜를 넣는다.
        ArrayList<String> dayList = new ArrayList<>();

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

                // 선택탭의 이름을 수정한다.
                days = String.format(tabDateFormat, checkInDay, checkOutDay);

                FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

                mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            }

            HotelDaysListFragment fragment = (HotelDaysListFragment) mFragmentPagerAdapter.getItem(2);
            fragment.setCheckInSaleTime(checkInSaleTime);
            fragment.setCheckOutSaleTime(checkOutSaleTime);

            dayList.add(days);
        } else
        {
            dayList.add(mTabLayout.getTabAt(2).getText().toString());
        }

        for (int i = 0; i < TAB_COUNT; i++)
        {
            mTabLayout.getTabAt(i).setText(dayList.get(i));
        }

        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
    }

    private void recycleCategoryTabLayout(int size)
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
                for (int i = tabCount - 1; i > size - 1; i--)
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

    private void setCategoryTabLayoutVisibility(int visibility)
    {
        mCategoryTabLayout.setVisibility(visibility);
        mUnderLine.setVisibility(visibility);
    }

    private void makeCategoryTabLayout(List<Category> list)
    {
        if (list == null || list.size() <= 2)
        {
            setCategory(Category.ALL);
            setCategoryTabLayoutVisibility(View.GONE);
            return;
        }

        final int size = list.size();

        setCategoryTabLayoutVisibility(View.VISIBLE);
        recycleCategoryTabLayout(size);

        Category selectedCategory = mCurationOption.getCategory();

        // 선택된 카테고리가 없는 경우 2번째가 항상 선택된다
        if (selectedCategory == null)
        {
            setCategory(Category.ALL);
        } else
        {
            // 기존 카테고리에 존재하는지
            boolean isExist = false;
            for (Category category : list)
            {
                if (category.code.equalsIgnoreCase(selectedCategory.code) == true)
                {
                    isExist = true;
                    setCategory(category);
                    break;
                }
            }

            if (isExist == false)
            {
                setCategory(Category.ALL);
            }
        }

        TabLayout.Tab selectedTab = null;

        final float TAB_COUNT = 4.5f;
        final int TAB_WIDTH = (int) (Util.getLCDWidth(getContext()) / TAB_COUNT);

        Category category;
        TabLayout.Tab tab;
        View tabView;
        ViewGroup.LayoutParams layoutParams;

        for (int i = 0; i < size; i++)
        {
            category = list.get(i);

            tab = mCategoryTabLayout.getTabAt(i);
            tab.setText(category.name);
            tab.setTag(category);

            tabView = ((ViewGroup) mCategoryTabLayout.getChildAt(0)).getChildAt(i);
            layoutParams = tabView.getLayoutParams();
            layoutParams.width = TAB_WIDTH;
            tabView.setLayoutParams(layoutParams);

            if (selectedCategory.code.equalsIgnoreCase(category.code) == true)
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

    private void setCategory(Category category)
    {
        if (category == null)
        {
            category = Category.ALL;
        }

        mCurationOption.setCategory(category);
    }

    private void setSortType(SortType sortType)
    {
        if (sortType == null)
        {
            sortType = SortType.DEFAULT;
        }

        mCurationOption.setSortType(sortType);
    }

    private void setProvince(Province province)
    {
        if (province == null)
        {
            return;
        }

        mCurationOption.setProvince(province);
    }

    private Province getProvince()
    {
        if (mCurationOption == null)
        {
            return null;
        }

        return mCurationOption.getProvince();
    }

    private void clearCurationOption()
    {
        if (mCurationOption == null)
        {
            return;
        }

        mCurationOption.clear();
    }

    private void curationCurrentFragment()
    {
        updateFilteredFloatingActionButton();

        HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        currentFragment.curationList(mViewType, mCurationOption);
    }

    private void refreshCurrentFragment(List<EventBanner> list)
    {
        HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        currentFragment.refreshList(list);
    }

    private void updateFilteredFloatingActionButton()
    {
        if (mCurationOption.isDefaultFilter() == true)
        {
            mFloatingActionView.setSelected(false);
        } else
        {
            mFloatingActionView.setSelected(true);
        }
    }

    private void refreshEventBanner()
    {
        DailyNetworkAPI.getInstance().requestEventBannerList(mNetworkTag, "hotel", mEventBannerListJsonResponseListener, new Response.ErrorListener()
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
        String savedRegion = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.HOTEL);

        if (province.name.equalsIgnoreCase(savedRegion) == false)
        {
            DailyPreference.getInstance(baseActivity).setSelectedOverseaRegion(PlaceType.HOTEL, province.isOverseas);
            DailyPreference.getInstance(baseActivity).setSelectedRegion(PlaceType.HOTEL, province.name);
        }

        refreshCurrentFragment(mEventBannerList);
    }

    private void searchMyLocation()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        DailyLocationFactory.getInstance(baseActivity).startLocationMeasure(baseActivity, null, new DailyLocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                if (Util.isOverAPI23() == true)
                {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                }

                unLockUI();
            }

            @Override
            public void onFailed()
            {
                unLockUI();

                //                recordAnalyticsSortTypeEvent(getContext(), mCurationOption.getSortType());

                if (Util.isOverAPI23() == true)
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null || baseActivity.isFinishing() == true)
                    {
                        return;
                    }

                    baseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps_android6)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                            }
                        }, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mCurationOption.setSortType(SortType.DEFAULT);
                                curationCurrentFragment();
                            }
                        }, true);
                } else
                {
                    mCurationOption.setSortType(SortType.DEFAULT);
                    curationCurrentFragment();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                unLockUI();

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                DailyLocationFactory.getInstance(baseActivity).stopLocationMeasure();

                baseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                    , getString(R.string.dialog_msg_used_gps)//
                    , getString(R.string.dialog_btn_text_dosetting)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mCurationOption.setSortType(SortType.DEFAULT);
                            curationCurrentFragment();

                            //                        recordAnalyticsSortTypeEvent(getContext(), mSortType);
                        }
                    }, false);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    unLockUI();
                    return;
                }

                DailyLocationFactory.getInstance(baseActivity).stopLocationMeasure();

                if (location == null)
                {
                    mCurationOption.setSortType(SortType.DEFAULT);
                    curationCurrentFragment();
                } else
                {
                    mCurationOption.setLocation(location);

                    if (mCurationOption.getSortType() == SortType.DISTANCE)
                    {
                        curationCurrentFragment();
                    }
                }

                unLockUI();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void deepLinkDetail(BaseActivity baseActivity)
    {
        try
        {
            // 신규 타입의 화면이동
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = mTodaySaleTime.getDailyTime();
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            String date = DailyDeepLink.getInstance().getDate();
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
            Date schemeDate = format.parse(date);
            Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

            int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

            if (nights <= 0 || dailyDayOfDays < 0)
            {
                throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
            }

            mOnCommunicateListener.selectHotel(hotelIndex, dailyTime, dailyDayOfDays, nights);

            DailyDeepLink.getInstance().clear();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            DailyDeepLink.getInstance().clear();

            //탭에 들어갈 날짜를 만든다.
            makeDateTabLayout();

            // 지역 리스트를 가져온다
            DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
        }
    }

    private void deepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.HOTEL_BANNER, url, mTodaySaleTime);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
        } else
        {
            //탭에 들어갈 날짜를 만든다.
            makeDateTabLayout();

            // 지역 리스트를 가져온다
            DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
        }
    }

    private Province searchDeeLinkRegion(ArrayList<Province> provinceList, ArrayList<Area> areaList)
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

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, boolean isOverseas, ArrayList<Province> provinceList, ArrayList<Area> areaList)
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

    private void deepLinkRegionList(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        Province selectedProvince = searchDeeLinkRegion(provinceList, areaList);

        if (selectedProvince == null)
        {
            selectedProvince = getProvince();
        }

        setProvince(selectedProvince);

        mDailyToolbarLayout.setToolbarRegionText(selectedProvince.name);
        mDailyToolbarLayout.setToolbarMenuVisibility(true);

        Intent intent = HotelRegionListActivity.newInstance(baseActivity, selectedProvince, mTodaySaleTime, 1);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
    }

    private void deepLinkHotelList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        String categoryCode = DailyDeepLink.getInstance().getCategoryCode();
        String date = DailyDeepLink.getInstance().getDate();
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
            selectedProvince = getProvince();
        }

        setProvince(selectedProvince);

        mDailyToolbarLayout.setToolbarRegionText(selectedProvince.name);
        mDailyToolbarLayout.setToolbarMenuVisibility(true);

        // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
        if (Util.isTextEmpty(categoryCode) == false)
        {
            for (Category category : selectedProvince.getCategoryList())
            {
                if (category.code.equalsIgnoreCase(categoryCode) == true)
                {
                    setCategory(category);
                    break;
                }
            }
        }

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            try
            {
                mTabLayout.setOnTabSelectedListener(null);
                mTabLayout.setScrollPosition(2, 0f, true);
                mViewPager.setCurrentItem(2);
                mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
                DailyDeepLink.getInstance().clear();

                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (dailyDayOfDays >= 0)
                {
                    SaleTime checkInSaleTime = mTodaySaleTime.getClone(dailyDayOfDays);
                    SaleTime checkOutSaleTime = mTodaySaleTime.getClone(dailyDayOfDays + night);

                    HotelDaysListFragment hotelListFragment = (HotelDaysListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    hotelListFragment.setCheckInSaleTime(checkInSaleTime);
                    hotelListFragment.setCheckOutSaleTime(checkOutSaleTime);
                    mOnCommunicateListener.selectDay(checkInSaleTime, checkOutSaleTime, true);
                }
            } catch (Exception e)
            {
                mTabLayout.setOnTabSelectedListener(null);
                mTabLayout.setScrollPosition(0, 0f, true);
                mViewPager.setCurrentItem(0);
                mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

                DailyDeepLink.getInstance().clear();
                refreshCurrentFragment(selectedProvince);
            }
        } else
        {
            DailyDeepLink.getInstance().clear();
            refreshCurrentFragment(selectedProvince);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
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
                        mOnCommunicateListener.toggleViewType();

                        onPrepareOptionsMenu(mViewType);
                    }
                    break;
                }

                case R.drawable.navibar_ic_map:
                {
                    boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

                    if (isInstalledGooglePlayServices == true)
                    {
                        mOnCommunicateListener.toggleViewType();

                        onPrepareOptionsMenu(mViewType);
                    }
                    break;
                }

                case R.drawable.navibar_ic_search_black:
                {
                    HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

                    Intent intent = HotelSearchActivity.newInstance(baseActivity//
                        , currentFragment.getCheckInSaleTime(), currentFragment.getNights());
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

                    switch (mViewType)
                    {
                        case LIST:
                            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , Action.HOTEL_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_LIST, null);
                            break;

                        case MAP:
                            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , Action.HOTEL_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_MAP, null);
                            break;
                    }
                    break;
                }
            }

            mOnCommunicateListener.expandedAppBar(true, true);
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            lockUI();

            mTabLayout.setOnTabSelectedListener(null);

            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            mOnCommunicateListener.expandedAppBar(true, true);

            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected();

            //            mOnCommunicateListener.refreshAll(true);
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mSimpleDateTimeJsonResponseListener, baseActivity);

            mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

            //            // Google Analytics
            //            if (mSelectedProvince != null)
            //            {
            //                HashMap<String, String> params = new HashMap<String, String>();
            //                params.put(Label.PROVINCE, mSelectedProvince.name);
            //                params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));
            //
            //                AnalyticsManager.getInstance(getActivity()).recordEvent(mViewType.name(), Action.CLICK, Label.DATE_TAB, params);
            //            }
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

            mOnCommunicateListener.expandedAppBar(true, true);

            HotelListFragment fragment = (HotelListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected();

            //            // Google Analytics
            //            if (mSelectedProvince != null)
            //            {
            //                HashMap<String, String> params = new HashMap<String, String>();
            //                params.put(Label.PROVINCE, mSelectedProvince.name);
            //                params.put(Label.DATE_TAB, Integer.toString(tab.getPosition()));
            //
            //                AnalyticsManager.getInstance(getActivity()).recordEvent(mViewType.name(), Action.CLICK, Label.DATE_TAB, params);
            //            }
        }
    };

    private TabLayout.OnTabSelectedListener mOnCategoryTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            mCategoryTabLayout.setOnTabSelectedListener(null);

            Category category = (Category) tab.getTag();
            setCategory(category);

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.HOTEL_CATEGORY_CLICKED, category.name, null);

            curationCurrentFragment();

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

    private OnCommunicateListener mOnCommunicateListener = new OnCommunicateListener()
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

            lockUI();

            if (placeViewItem == null)
            {
                unLockUI();
                return;
            }

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Hotel hotel = placeViewItem.getItem();

                    String region = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.HOTEL);
                    DailyPreference.getInstance(baseActivity).setGASelectedRegion(region);
                    DailyPreference.getInstance(baseActivity).setGAHotelName(hotel.name);

                    Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotel.index);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, hotel.nights);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotel.name);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, hotel.imageUrl);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, hotel.categoryCode);

                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);

                    String label = String.format("%s-%s", hotel.categoryCode, hotel.name);
                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , Action.HOTEL_ITEM_CLICKED, label, null);
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

            // 선택탭의 이름을 수정한다.
            mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            mTabLayout.getTabAt(2).setText(String.format(tabDateFormat, checkInDay, checkOutDay));

            FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.setScrollListTop(true);

            refreshCurrentFragment(mEventBannerList);
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

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.HOTEL_EVENT_BANNER_CLICKED, eventBanner.name, null);

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
                    Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

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

                        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            } else
            {
                Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.HOTEL_BANNER, eventBanner.webLink, mTodaySaleTime);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
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

            switch (mViewType)
            {
                case LIST:
                    mViewType = ViewType.MAP;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP, null);
                    break;

                case MAP:
                    mViewType = ViewType.LIST;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST, null);
                    break;
            }

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            HotelListFragment currentFragment = (HotelListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                boolean isCurrentFragment = hotelListFragment == currentFragment;

                hotelListFragment.setVisibility(mViewType, isCurrentFragment);
            }

            currentFragment.curationList(mViewType, mCurationOption);

            unLockUI();
        }

        @Override
        public void setScrollListTop(boolean scrollListTop)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            for (HotelListFragment hotelListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                hotelListFragment.setScrollListTop(scrollListTop);
            }
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
        public void refreshCompleted()
        {
            mFloatingActionView.setTag("completed");
        }

        @Override
        public void showFloatingActionButton()
        {
            if (mDailyFloatingActionButtonBehavior != null)
            {
                mDailyFloatingActionButtonBehavior.show(mFloatingActionView);
            }
        }

        @Override
        public void hideFloatingActionButton()
        {
            if (mDailyFloatingActionButtonBehavior != null)
            {
                mDailyFloatingActionButtonBehavior.hide(mFloatingActionView);
            }
        }

        @Override
        public void expandedAppBar(boolean expanded, boolean animate)
        {
            mAppBarLayout.setExpanded(expanded, animate);
        }

        @Override
        public HotelCurationOption getCurationOption()
        {
            return mCurationOption;
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

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

                if (DailyDeepLink.getInstance().isValidateLink() == true //
                    && processDeepLink(baseActivity) == true)
                {

                } else
                {
                    //탭에 들어갈 날짜를 만든다.
                    makeDateTabLayout();

                    // 지역 리스트를 가져온다
                    DailyNetworkAPI.getInstance().requestHotelRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        private boolean processDeepLink(BaseActivity baseActivity)
        {
            if (DailyDeepLink.getInstance().isHotelDetailView() == true)
            {
                unLockUI();
                deepLinkDetail(baseActivity);
                return true;
            } else if (DailyDeepLink.getInstance().isHotelEventBannerWebView() == true)
            {
                unLockUI();
                deepLinkEventBannerWeb(baseActivity);
                return true;
            } else
            {
                // 더이상 진입은 없다.
                if (DailyDeepLink.getInstance().isHotelListView() == false//
                    && DailyDeepLink.getInstance().isHotelRegionListView() == false)
                {
                    DailyDeepLink.getInstance().clear();
                }
            }

            return false;
        }
    };

    private DailyHotelJsonResponseListener mSimpleDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
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

                //탭에 들어갈 날짜를 만든다.
                makeDateTabLayout();

                refreshCurrentFragment(mEventBannerList);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

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
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                    ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                    Province selectedProvince = getProvince();

                    if (selectedProvince == null)
                    {
                        selectedProvince = searchLastRegion(baseActivity, provinceList, areaList);
                    }

                    // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                    if (selectedProvince == null)
                    {
                        selectedProvince = provinceList.get(0);
                    }

                    // 처음 시작시에는 지역이 Area로 저장된 경우 Province로 변경하기 위한 저장값.
                    boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).isSettingRegion(PlaceType.HOTEL);
                    DailyPreference.getInstance(baseActivity).setSettingRegion(PlaceType.HOTEL, true);

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

                    setProvince(selectedProvince);

                    if (DailyDeepLink.getInstance().isValidateLink() == true//
                        && processDeepLink(baseActivity, provinceList, areaList) == true)
                    {
                        makeCategoryTabLayout(getProvince().getCategoryList());
                    } else
                    {
                        makeCategoryTabLayout(selectedProvince.getCategoryList());
                        refreshEventBanner();
                    }
                } else
                {
                    String message = response.getString("msg");
                    onErrorMessage(msgCode, message);
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

        private Province searchLastRegion(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
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

        private boolean processDeepLink(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isHotelRegionListView() == true)
            {
                unLockUI();
                deepLinkRegionList(baseActivity, provinceList, areaList);
                return true;
            } else if (DailyDeepLink.getInstance().isHotelListView() == true)
            {
                unLockUI();
                deepLinkHotelList(provinceList, areaList);
                return true;
            } else
            {
                DailyDeepLink.getInstance().clear();
            }

            return false;
        }

        private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
        {
            ArrayList<Area> areaList = new ArrayList<>();

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
            ArrayList<Province> provinceList = new ArrayList<>();

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

    private DailyHotelJsonResponseListener mEventBannerListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String baseUrl = dataJSONObject.getString("imgUrl");

                    JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");

                    if (mEventBannerList == null)
                    {
                        mEventBannerList = new ArrayList<>();
                    }

                    mEventBannerList.clear();

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            EventBanner eventBanner = new EventBanner(jsonArray.getJSONObject(i), baseUrl);
                            mEventBannerList.add(eventBanner);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            } finally
            {
                refreshCurrentFragment(getProvince());
            }
        }
    };
}
