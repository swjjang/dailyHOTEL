package com.twoheart.dailyhotel.screen.gourmet.region;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceRegionFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceRegionListNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetRegionListActivity extends PlaceRegionListActivity
{
    public static final String INTENT_EXTRA_DATA_PROVINCE_INDEX = "provinceIndex";
    public static final String INTENT_EXTRA_DATA_AREA_INDEX = "areaIndex";
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";

    private static final int GOURMET_TAB_COUNT = 1;

    ViewPager mViewPager;
    PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;
    private GourmetRegionListNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private Province mSelectedProvince;

    public static Intent newInstance(Context context, Province province, SaleTime saleTime)
    {
        Intent intent = new Intent(context, GourmetRegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);

        return intent;
    }

    public static Intent newInstance(Context context, int provinceIndex, int areaIndex, SaleTime saleTime)
    {
        Intent intent = new Intent(context, GourmetRegionListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PROVINCE_INDEX, provinceIndex);
        intent.putExtra(INTENT_EXTRA_DATA_AREA_INDEX, areaIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);

        return intent;
    }

    @Override
    protected void initPrepare()
    {
        mNetworkController = new GourmetRegionListNetworkController(this, mOnNetworkControllerListener);
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSelectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
    }

    @Override
    protected void initTabLayout(TabLayout tabLayout)
    {
        if (tabLayout == null)
        {
            return;
        }

        tabLayout.setVisibility(ViewPager.GONE);
    }

    @Override
    protected void initToolbar(View toolbar)
    {
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_gourmet_area), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        dailyToolbarLayout.setBackImageView(R.drawable.navibar_ic_x);
        dailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_search, -1);
        dailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSearch();
            }
        });
    }

    @Override
    protected void initViewPager(TabLayout tabLayout)
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(GOURMET_TAB_COUNT);

        GourmetRegionListFragment regionListFragment01 = new GourmetRegionListFragment();
        regionListFragment01.setInformation(Region.DOMESTIC, mSelectedProvince);
        regionListFragment01.setOnPlaceRegionListFragmentListener(mOnPlaceListFragmentListener);
        fragmentList.add(regionListFragment01);

        mFragmentPagerAdapter = new PlaceRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(GOURMET_TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        AnalyticsManager.getInstance(GourmetRegionListActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
    }

    @Override
    protected void showSearch()
    {
        Intent intent = SearchActivity.newInstance(this, PlaceType.FNB, mSaleTime, 1);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.GOURMET_LOCATION_LIST, null);
    }

    @Override
    protected void requestRegionList()
    {
        if (mNetworkController == null)
        {
            Util.restartApp(this);
            return;
        }

        mNetworkController.requestRegionList();
    }

    @Override
    protected void updateTermsOfLocationLayout()
    {
        for (PlaceRegionListFragment fragment : mFragmentPagerAdapter.getFragmentList())
        {
            if (fragment.isAdded() == true)
            {
                fragment.updateTermsOfLocationView();
            }
        }
    }

    @Override
    protected PlaceRegionListFragment getCurrentFragment()
    {
        if (mFragmentPagerAdapter == null)
        {
            return null;
        }

        return (PlaceRegionListFragment) mFragmentPagerAdapter.getItem(0);
    }

    private PlaceRegionListFragment.OnPlaceRegionListFragment mOnPlaceListFragmentListener = new PlaceRegionListFragment.OnPlaceRegionListFragment()
    {
        private void recordEvent(Province province)
        {
            String label;

            if (province instanceof Area)
            {
                Area area = (Area) province;

                if (area.index == -1)
                {
                    label = String.format("%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name);
                } else
                {
                    label = String.format("%s-%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name, area.name);
                }
            } else
            {
                label = String.format("%s-%s", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                    , province.name);
            }

            AnalyticsManager.getInstance(GourmetRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED, label, null);
        }

        @Override
        public void onActivityCreated(PlaceRegionListFragment placeRegionListFragment)
        {
            PlaceRegionListFragment currPlaceRegionListFragment = (PlaceRegionListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (currPlaceRegionListFragment == placeRegionListFragment)
            {
                lockUI();
                requestRegionList();
            }
        }

        @Override
        public void onRegionClick(Province province)
        {
            Intent intent = new Intent();

            if (province == null)
            {
                setResult(RESULT_CANCELED, intent);
            } else
            {
                intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                setResult(RESULT_OK, intent);

                recordEvent(province);
            }

            finish();
        }

        @Override
        public void onAroundSearchClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(GourmetRegionListActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);

            AnalyticsManager.getInstance(GourmetRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED, getString(R.string.label_view_myaround_gourmet), null);
        }
    };

    private PlaceRegionListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceRegionListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList)
        {
            ArrayList<PlaceRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(GourmetRegionListActivity.this, domesticList);

            unLockUI();
        }

        @Override
        public void onError(Throwable e)
        {
            unLockUI();
            GourmetRegionListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            GourmetRegionListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            GourmetRegionListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            unLockUI();
            GourmetRegionListActivity.this.onErrorResponse(call, response);
        }
    };
}