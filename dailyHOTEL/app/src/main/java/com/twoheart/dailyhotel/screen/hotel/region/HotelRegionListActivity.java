package com.twoheart.dailyhotel.screen.hotel.region;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceRegionFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.util.ArrayList;
import java.util.List;

public class HotelRegionListActivity extends PlaceRegionListActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private static final int HOTEL_TAB_COUNT = 2;

    private ViewPager mViewPager;
    private PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;

    private HotelRegionListPresenter mRegionListPresenter;
    private SaleTime mSaleTime;
    private int mNights;
    private Province mSelectedProvince;

    public static Intent newInstance(Context context, Province province, SaleTime saleTime, int nights)
    {
        Intent intent = new Intent(context, HotelRegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);

        return intent;
    }

    @Override
    protected void initPrepare()
    {
        mRegionListPresenter = new HotelRegionListPresenter(this, mOnResponsePresenterListener);
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSelectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);
    }

    @Override
    protected void initTabLayout(TabLayout tabLayout)
    {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_domestic));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_global));
        tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    @Override
    protected void initToolbar(View toolbar)
    {
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_area));
        dailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_search_black, -1);
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

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(HOTEL_TAB_COUNT);
        HotelRegionListFragment regionListFragment01 = new HotelRegionListFragment();

        boolean isOverseas = false;

        if (mSelectedProvince != null)
        {
            if (mSelectedProvince instanceof Area)
            {
                // 어디선가에서 Proince가 누락되는데 찾을수가 없음ㅜㅜd

                Area area = ((Area) mSelectedProvince);

                if (area.getProvince() == null)
                {
                    isOverseas = DailyPreference.getInstance(this).isSelectedOverseaRegion(PlaceType.HOTEL);
                } else
                {
                    isOverseas = area.getProvince().isOverseas;
                }
            } else
            {
                isOverseas = mSelectedProvince.isOverseas;
            }
        }

        regionListFragment01.setInformation(Region.DOMESTIC, isOverseas ? null : mSelectedProvince);
        regionListFragment01.setOnUserActionListener(mOnUserActionListener);
        fragmentList.add(regionListFragment01);

        HotelRegionListFragment regionListFragment02 = new HotelRegionListFragment();
        regionListFragment02.setInformation(Region.GLOBAL, isOverseas ? mSelectedProvince : null);
        regionListFragment02.setOnUserActionListener(mOnUserActionListener);
        fragmentList.add(regionListFragment02);

        mFragmentPagerAdapter = new PlaceRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(HOTEL_TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setCurrentItem(isOverseas ? 1 : 0);

        if (isOverseas == true)
        {
            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
        } else
        {
            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
        }
    }

    @Override
    protected void showSearch()
    {
        Intent intent = HotelSearchActivity.newInstance(this, mSaleTime, mNights);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_LOCATION_LIST, null);
    }

    @Override
    protected void requestRegionList()
    {
        mRegionListPresenter.requestRegionList();
    }

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
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

            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_LOCATIONS_CLICKED, label, null);
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
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        private void recordAnalytics(int position)
        {
            if (position == 0)
            {
                AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
            } else
            {
                AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            recordAnalytics(tab.getPosition());
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

    private HotelRegionListPresenter.OnResponsePresenterListener mOnResponsePresenterListener = new HotelRegionListPresenter.OnResponsePresenterListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList)
        {
            ArrayList<PlaceRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(HotelRegionListActivity.this, domesticList);
            arrayList.get(1).setRegionViewList(HotelRegionListActivity.this, globalList);

            unLockUI();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            HotelRegionListActivity.this.onError(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            HotelRegionListActivity.this.onError(e);
        }

        @Override
        public void onErrorMessage(int msgCode, String message)
        {
            unLockUI();
            HotelRegionListActivity.this.onErrorMessage(msgCode, message);
        }
    };
}