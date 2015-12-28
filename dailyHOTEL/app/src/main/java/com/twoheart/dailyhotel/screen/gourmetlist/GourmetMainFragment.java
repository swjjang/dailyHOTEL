package com.twoheart.dailyhotel.screen.gourmetlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.region.RegionListActivity;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetMainFragment extends PlaceMainFragment
{
    private static final int TAB_COUNT = 3;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private GourmetFragmentPagerAdapter mFragmentPagerAdapter;
    private Province mSelectedProvince;

    public interface OnUserActionListener
    {
        void selectPlace(PlaceViewItem baseListViewItem, SaleTime checkSaleTime);

        void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights);

        void selectDay(SaleTime checkInSaleTime, boolean isListSelectionTop);

        void toggleViewType();

        void showSortDialogView();

        void onClickActionBarArea();

        void setMapViewVisible(boolean isVisible);

        void refreshAll(boolean isShowProgress);
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

        setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해

        return view;
    }

    private void initToolbar(View view)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        baseActivity.initToolbarRegion(mToolbar, getString(R.string.label_dailygourmet), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnGourmetUserActionListener.onClickActionBarArea();
            }
        });
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

                    GourmetListFragment gourmetListFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

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
                    mOnGourmetUserActionListener.toggleViewType();

                    baseActivity.invalidateOptionsMenu();
                }
                return true;
            }

            case R.id.action_map:
            {
                boolean isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

                if (isInstalledGooglePlayServices == true)
                {
                    mOnGourmetUserActionListener.toggleViewType();

                    baseActivity.invalidateOptionsMenu();
                }
                return true;
            }

            case R.id.action_sort:
            {
                mOnGourmetUserActionListener.showSortDialogView();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
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

        baseActivity.setToolbarRegionText(mToolbar, province.name);

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
    public void activityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                GourmetListFragment currentFragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
                currentFragment.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            mAppBarLayout.setExpanded(true);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            GourmetListFragment fragment = (GourmetListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected(true);

            mOnGourmetUserActionListener.refreshAll(true);

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

                    startActivityForResult(intent, CODE_REQUEST_FRAGMENT_PLACE_MAIN);

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

            String checkInDay = checkInSaleTime.getDayOfDaysHotelDateFormat("d");

            // 선택탭의 이름을 수정한다.
            mTabLayout.getTabAt(2).setText(String.format("%s(%s일)", getString(R.string.label_day), checkInDay));

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
                    regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(TYPE.FNB);

                    if (Util.isTextEmpty(regionName) == true)
                    {
                        selectedProvince = provinceList.get(0);
                        regionName = selectedProvince.name;
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

                // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                if (selectedProvince == null)
                {
                    selectedProvince = provinceList.get(0);
                    regionName = selectedProvince.name;
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

            dayList.add(String.format("%s(%s일)", getString(R.string.label_today), tabSaleTime[0].getDailyDay()));
            dayList.add(String.format("%s(%s일)", getString(R.string.label_tomorrow), tabSaleTime[1].getDailyDay()));

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
