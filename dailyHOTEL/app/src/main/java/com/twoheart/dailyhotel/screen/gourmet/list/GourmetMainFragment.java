package com.twoheart.dailyhotel.screen.gourmet.list;

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
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.common.BaseFragment;
import com.twoheart.dailyhotel.screen.eventlist.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;
import com.twoheart.dailyhotel.screen.regionlist.RegionListActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.widget.DailyFloatingActionButtonBehavior;
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
import java.util.TimeZone;

public class GourmetMainFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener
{
    private static final int TAB_COUNT = 3;
    private int TOOLBAR_HEIGHT;

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private GourmetFragmentPagerAdapter mFragmentPagerAdapter;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mFloatingActionView;

    private SaleTime mTodaySaleTime;

    private boolean mDontReloadAtOnResume;
    private boolean mIsHideAppBarlayout;
    private int mCanScrollUpCount = 0;

    private GourmetCurationOption mCurationOption;

    private ViewType mViewType = ViewType.LIST;

    public interface OnCommunicateListener
    {
        void selectPlace(PlaceViewItem baseListViewItem, SaleTime checkSaleTime);

        void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, boolean isListSelectionTop);

        void selectEventBanner(EventBanner eventBanner);

        void toggleViewType();

        void setScrollListTop(boolean scrollListTop);

        void refreshAll(boolean isShowProgress);

        void refreshCompleted();

        void expandedAppBar(boolean expanded, boolean animate);

        void showAppBarLayout();

        void hideAppBarLayout();

        void pinAppBarLayout();

        void showFloatingActionButton();

        void hideFloatingActionButton(boolean isAnimation);

        GourmetCurationOption getCurationOption();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_main, container, false);

        initToolbar(view);

        mViewType = ViewType.LIST;

        mTodaySaleTime = new SaleTime();

        mCurationOption = new GourmetCurationOption();

        initDateTabLayout(view);

        initFloatingActionButton(view);

        setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해

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

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

                Intent intent = RegionListActivity.newInstance(getContext(), PlaceType.FNB, mCurationOption.getProvince(), currentFragment.getSaleTime(), 1);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);
            }
        });

        mDailyToolbarLayout.initToolbarRegionMenu(mToolbarOptionsItemSelected);
    }

    private void initDateTabLayout(View view)
    {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_today), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_tomorrow));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_selecteday));
        FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mFragmentPagerAdapter = new GourmetFragmentPagerAdapter(getChildFragmentManager(), TAB_COUNT, mOnCommunicateListener);

        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                mOnCommunicateListener.hideFloatingActionButton(true);
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
                mOnCommunicateListener.hideFloatingActionButton(false);

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

                Intent intent = GourmetCurationActivity.newInstance(baseActivity, mCurationOption.getProvince().isOverseas, mViewType, mCurationOption);
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
                    , Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, viewType, null);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        final int CANSCROLLUP_REPEAT_COUNT = 3;

        if (verticalOffset == -TOOLBAR_HEIGHT)
        {
            if (mIsHideAppBarlayout == true)
            {
                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                boolean canScrollUp = currentFragment.canScrollUp();

                if (canScrollUp == false)
                {
                    if (++mCanScrollUpCount > CANSCROLLUP_REPEAT_COUNT)
                    {
                        mCanScrollUpCount = 0;
                        mOnCommunicateListener.showAppBarLayout();
                        mOnCommunicateListener.expandedAppBar(true, true);
                        mIsHideAppBarlayout = false;
                    }
                } else
                {
                    mCanScrollUpCount = 0;
                }
            } else
            {
                mOnCommunicateListener.hideAppBarLayout();
                mIsHideAppBarlayout = true;
                mCanScrollUpCount = 0;

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.resetScrollDistance(false);
            }
        } else if (verticalOffset == 0)
        {
            if (mIsHideAppBarlayout == true)
            {
                mOnCommunicateListener.showFloatingActionButton();
            } else
            {
                mOnCommunicateListener.showAppBarLayout();
                mOnCommunicateListener.pinAppBarLayout();
                mIsHideAppBarlayout = true;
                mCanScrollUpCount = 0;

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.resetScrollDistance(true);
            }
        } else if (verticalOffset < 0 && verticalOffset > -TOOLBAR_HEIGHT)
        {
            mIsHideAppBarlayout = false;
            mCanScrollUpCount = 0;
        }
    }

    @Override
    public void onResume()
    {
        mOnCommunicateListener.showAppBarLayout();
        mOnCommunicateListener.pinAppBarLayout();
        mOnCommunicateListener.expandedAppBar(true, false);

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
                    } else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
                    {
                        Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

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

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.onActivityResult(requestCode, resultCode, data);

                mOnCommunicateListener.expandedAppBar(true, false);
                break;
            }

            case CODE_REQUEST_ACTIVITY_CURATION:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    GourmetCurationOption curationOption = data.getParcelableExtra(GourmetCurationActivity.INTENT_EXTRA_DATA_CURATION_OPTIONS);

                    if (curationOption != null)
                    {
                        mOnCommunicateListener.setScrollListTop(true);

                        mCurationOption.setSortType(curationOption.getSortType());
                        mCurationOption.setFilterMap(curationOption.getFilterMap());
                        mCurationOption.flagTimeFilter = curationOption.flagTimeFilter;
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

                mOnCommunicateListener.showFloatingActionButton();
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
                mDontReloadAtOnResume = true;

                if (mViewType == ViewType.MAP)
                {
                    GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                    currentFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
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
            GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (gourmetListFragment != null)
            {
                gourmetListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void makeDateTabLayout()
    {
        SaleTime[] tabSaleTime = new SaleTime[TAB_COUNT];
        SaleTime saleTime;
        GourmetListFragment gourmetListFragment;

        for (int i = 0; i < TAB_COUNT; i++)
        {
            gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(i);

            saleTime = mTodaySaleTime.getClone(i);
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
            String days = getString(R.string.label_selecteday);

            SaleTime checkInSaleTime = tabSaleTime[0].getClone(2);

            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (currentFragment instanceof GourmetDaysListFragment)
            {
                days = checkInSaleTime.getDayOfDaysDateFormat("M월d일");

                mTabLayout.getTabAt(2).setTag(getString(R.string.label_selecteday));
            }

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

    private void setProvince(Province province)
    {
        if (province == null)
        {
            return;
        }

        mCurationOption.setProvince(province);
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

        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        gourmetListFragment.curationList(mViewType, mCurationOption);
    }

    public void refreshCurrentFragment()
    {
        GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
        gourmetListFragment.refreshList();
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

        refreshCurrentFragment();
    }

    private void searchMyLocation()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        LocationFactory.getInstance(baseActivity).startLocationMeasure(baseActivity, null, new LocationFactory.LocationListenerEx()
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

                //                recordAnalyticsSortTypeEvent(getContext(), mSortType);

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
                LocationFactory.getInstance(baseActivity).stopLocationMeasure();

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

                LocationFactory.getInstance(baseActivity).stopLocationMeasure();

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
            int fnbIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = mTodaySaleTime.getDailyTime();
            int nights = 1;

            String date = DailyDeepLink.getInstance().getDate();
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
            Date schemeDate = format.parse(date);
            Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

            int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

            if (dailyDayOfDays < 0)
            {
                throw new NullPointerException("dailyDayOfDays < 0");
            }

            mOnCommunicateListener.selectPlace(fnbIndex, dailyTime, dailyDayOfDays, nights);

            DailyDeepLink.getInstance().clear();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            DailyDeepLink.getInstance().clear();

            //탭에 들어갈 날짜를 만든다.
            makeDateTabLayout();

            // 지역 리스트를 가져온다
            DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
        }
    }

    private void deepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.GOURMET_BANNER, url, mTodaySaleTime);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
        } else
        {
            //탭에 들어갈 날짜를 만든다.
            makeDateTabLayout();

            // 지역 리스트를 가져온다
            DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
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

    private Province searchDeeLinkRegion(int provinceIndex, int areaIndex, ArrayList<Province> provinceList, ArrayList<Area> areaList)
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

    private void deepLinkRegionList(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        Province selectedProvince = searchDeeLinkRegion(provinceList, areaList);

        if (selectedProvince == null)
        {
            selectedProvince = mCurationOption.getProvince();
        } else
        {
            setProvince(selectedProvince);
        }

        mDailyToolbarLayout.setToolbarRegionText(selectedProvince.name);
        mDailyToolbarLayout.setToolbarMenuVisibility(true);

        Intent intent = RegionListActivity.newInstance(baseActivity, PlaceType.FNB, selectedProvince, mTodaySaleTime, 1);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGIONLIST);

        DailyDeepLink.getInstance().clear();
    }

    private void deepLinkGourmetList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
    {
        String date = DailyDeepLink.getInstance().getDate();

        int provinceIndex = -1;
        int areaIndex = -1;

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
            selectedProvince = mCurationOption.getProvince();
        } else
        {
            setProvince(selectedProvince);
        }

        mDailyToolbarLayout.setToolbarRegionText(selectedProvince.name);
        mDailyToolbarLayout.setToolbarMenuVisibility(true);

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
                    SaleTime selectedSaleTime = mTodaySaleTime.getClone(dailyDayOfDays);
                    mOnCommunicateListener.selectDay(selectedSaleTime, true);
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
            }

            mOnCommunicateListener.expandedAppBar(true, true);
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

            mOnCommunicateListener.expandedAppBar(true, true);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected();

            mOnCommunicateListener.refreshAll(true);

            mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

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
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
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

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected();

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

    private OnCommunicateListener mOnCommunicateListener = new OnCommunicateListener()
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

                    String region = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.FNB);
                    DailyPreference.getInstance(baseActivity).setGASelectedPlaceRegion(region);
                    DailyPreference.getInstance(baseActivity).setGASelectedPlaceName(gourmet.name);

                    Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);

                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , Action.GOURMET_ITEM_CLICKED, gourmet.name, null);
                    break;
                }

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

            String checkInDay = checkInSaleTime.getDayOfDaysDateFormat("M월d일");

            // 선택탭의 이름을 수정한다.
            mTabLayout.getTabAt(2).setText(checkInDay);
            FontManager.apply(mTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());

            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment.setScrollListTop(true);

            refreshCurrentFragment();
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
                , Action.GOURMET_EVENT_BANNER_CLICKED, eventBanner.name, null);

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
                        Intent intent = new Intent(baseActivity, HotelDetailActivity.class);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, eventBanner.index);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, eventBanner.nights);

                        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
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
                Intent intent = EventWebActivity.newInstance(baseActivity, EventWebActivity.SourceType.GOURMET_BANNER, eventBanner.webLink, mTodaySaleTime);
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

            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            switch (mViewType)
            {
                case LIST:
                {
                    // 맵리스트 진입시에 솔드아웃은 맵에서 보여주지 않기 때문에 맵으로 진입시에 아무것도 볼수 없다.
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

                    mViewType = ViewType.MAP;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP, null);
                    break;
                }

                case MAP:
                    mViewType = ViewType.LIST;
                    AnalyticsManager.getInstance(getActivity()).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST, null);
                    break;
            }

            showAppBarLayout();
            pinAppBarLayout();

            for (GourmetListFragment placeListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                boolean isCurrentFragment = placeListFragment == currentFragment;

                placeListFragment.setVisibility(mViewType, isCurrentFragment);
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

            for (GourmetListFragment gourmetListFragment : mFragmentPagerAdapter.getFragmentList())
            {
                gourmetListFragment.setScrollListTop(scrollListTop);
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
        public void expandedAppBar(boolean expanded, boolean animate)
        {
            mAppBarLayout.setExpanded(expanded, animate);

            if (expanded == true)
            {
                showFloatingActionButton();
            } else
            {
                hideFloatingActionButton(true);
            }
        }

        @Override
        public void showAppBarLayout()
        {
            if (mDailyToolbarLayout == null)
            {
                return;
            }

            View toolbar = mDailyToolbarLayout.getToolbar();

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

            View toolbar = mDailyToolbarLayout.getToolbar();

            if (toolbar == null)
            {
                return;
            }

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            if (params != null && params.getScrollFlags() != AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
            {
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
                toolbar.setLayoutParams(params);
            }

            hideFloatingActionButton(true);
        }

        @Override
        public void pinAppBarLayout()
        {
            if (mDailyToolbarLayout == null)
            {
                return;
            }

            View toolbar = mDailyToolbarLayout.getToolbar();

            if (toolbar == null)
            {
                return;
            }

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            if (params != null && params.getScrollFlags() != 0)
            {
                params.setScrollFlags(0);
                toolbar.setLayoutParams(params);
            }

            showFloatingActionButton();
        }

        @Override
        public void showFloatingActionButton()
        {
            if (mFloatingActionView.getTag() == null)
            {
                return;
            }

            if (mFloatingActionView.getVisibility() != View.GONE)
            {
                return;
            }

            GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (currentFragment.isShowInformationAtMapView() == true)
            {
                return;
            }

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFloatingActionView.getLayoutParams();
            DailyFloatingActionButtonBehavior dailyFloatingActionButtonBehavior = (DailyFloatingActionButtonBehavior) layoutParams.getBehavior();

            dailyFloatingActionButtonBehavior.show(mFloatingActionView);
        }

        @Override
        public void hideFloatingActionButton(boolean isAnimation)
        {
            if (mFloatingActionView.getVisibility() == View.GONE)
            {
                return;
            }

            if (isAnimation == true)
            {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFloatingActionView.getLayoutParams();
                DailyFloatingActionButtonBehavior dailyFloatingActionButtonBehavior = (DailyFloatingActionButtonBehavior) layoutParams.getBehavior();

                dailyFloatingActionButtonBehavior.hide(mFloatingActionView);
            } else
            {
                mFloatingActionView.setVisibility(View.GONE);
            }
        }

        @Override
        public GourmetCurationOption getCurationOption()
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
                    DailyNetworkAPI.getInstance().requestGourmetRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        private boolean processDeepLink(BaseActivity baseActivity)
        {
            if (DailyDeepLink.getInstance().isGourmetDetailView() == true)
            {
                unLockUI();
                deepLinkDetail(baseActivity);
                return true;
            } else if (DailyDeepLink.getInstance().isGourmetEventBannerWebView() == true)
            {
                unLockUI();
                deepLinkEventBannerWeb(baseActivity);
                return true;
            } else
            {
                // 더이상 진입은 없다.
                if (DailyDeepLink.getInstance().isGourmetListView() == false//
                    && DailyDeepLink.getInstance().isGourmetRegionListView() == false)
                {
                    DailyDeepLink.getInstance().clear();
                }
            }

            return false;
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

                    Province selectedProvince = mCurationOption.getProvince();

                    if (selectedProvince == null)
                    {
                        selectedProvince = searchLastRegion(baseActivity, provinceList, areaList);
                    }

                    // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                    if (selectedProvince == null)
                    {
                        selectedProvince = provinceList.get(0);
                    }

                    boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).isSettingRegion(PlaceType.FNB);
                    DailyPreference.getInstance(baseActivity).setSettingRegion(PlaceType.FNB, true);

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

                    setProvince(selectedProvince);

                    if (DailyDeepLink.getInstance().isValidateLink() == true//
                        && processDeepLink(baseActivity, provinceList, areaList) == true)
                    {

                    } else
                    {
                        refreshCurrentFragment(selectedProvince);
                    }
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

        private Province searchLastRegion(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
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

        private boolean processDeepLink(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isGourmetRegionListView() == true)
            {
                unLockUI();
                deepLinkRegionList(baseActivity, provinceList, areaList);
                return true;
            } else if (DailyDeepLink.getInstance().isGourmetListView() == true)
            {
                unLockUI();
                deepLinkGourmetList(provinceList, areaList);
                return true;
            } else
            {
                DailyDeepLink.getInstance().clear();
            }

            return false;
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
