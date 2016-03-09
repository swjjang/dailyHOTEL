package com.twoheart.dailyhotel.screen.regionlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.util.ArrayList;
import java.util.List;

public class RegionListActivity extends BaseActivity
{
    private static final int HOTEL_TAB_COUNT = 2;
    private static final int GOURMET_TAB_COUNT = 1;

    private ViewPager mViewPager;
    private RegionFragmentPagerAdapter mFragmentPagerAdapter;

    private Constants.PlaceType mPlaceType;
    private RegionListPresenter mRegionListPresenter;

    public interface OnUserActionListener
    {
        void onRegionClick(Province province);
    }

    public enum Region
    {
        DOMESTIC,
        GLOBAL
    }

    public interface OnResponsePresenterListener
    {
        void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList);

        void onInternalError();

        void onInternalError(String message);
    }

    public static Intent newInstance(Context context, Constants.PlaceType placeType, Province province)
    {
        Intent intent = new Intent(context, RegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mRegionListPresenter = new RegionListPresenter(this, mOnResponsePresenterListener);

        // 호텔 인지 고메인지
        mPlaceType = PlaceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        Province selectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

        if (mPlaceType == null)
        {
            Util.restartApp(this);
            return;
        }

        // 지역로딩시에 백버튼 누르면 종료되도록 수정
        setLockUICancelable(true);
        initLayout(mPlaceType, selectedProvince);
    }

    private void initLayout(Constants.PlaceType placeType, Province province)
    {
        if (mPlaceType == null)
        {
            return;
        }

        setContentView(R.layout.activity_region_list);

        initToolbar();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        switch (placeType)
        {
            case HOTEL:
            {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.label_domestic));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.label_global));

                ArrayList<RegionListFragment> fragmentList = new ArrayList<>(HOTEL_TAB_COUNT);

                RegionListFragment regionListFragment01 = new RegionListFragment();

                boolean isOverseas = false;

                if (province != null)
                {
                    if (province instanceof Area)
                    {
                        // 어디선가에서 Proince가 누락되는데 찾을수가 없음ㅜㅜd

                        Area area = ((Area) province);

                        if (area.getProvince() == null)
                        {
                            isOverseas = DailyPreference.getInstance(this).isSelectedOverseaRegion(Constants.PlaceType.HOTEL);
                        } else
                        {
                            isOverseas = area.getProvince().isOverseas;
                        }
                    } else
                    {
                        isOverseas = province.isOverseas;
                    }
                }

                regionListFragment01.setInformation(placeType, Region.DOMESTIC, isOverseas ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                RegionListFragment regionListFragment02 = new RegionListFragment();
                regionListFragment02.setInformation(placeType, Region.GLOBAL, isOverseas ? province : null);
                regionListFragment02.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment02);

                tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(HOTEL_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                mViewPager.setCurrentItem(isOverseas ? 1 : 0);

                if (isOverseas == false)
                {
                    AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
                }
                break;
            }

            case FNB:
            {
                tabLayout.setVisibility(ViewPager.GONE);

                ArrayList<RegionListFragment> fragmentList = new ArrayList<>(GOURMET_TAB_COUNT);

                RegionListFragment regionListFragment01 = new RegionListFragment();
                regionListFragment01.setInformation(placeType, Region.DOMESTIC, false ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(GOURMET_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
                break;
            }
        }

        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_area));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();

        switch (mPlaceType)
        {
            case HOTEL:
                mRegionListPresenter.requestHotelRegionList();
                break;

            case FNB:
                mRegionListPresenter.requestGourmetRegionList();
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
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

            switch (mPlaceType)
            {
                case HOTEL:
                    AnalyticsManager.getInstance(RegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.HOTEL_LOCATIONS_CLICKED, label, null);
                    break;

                case FNB:
                    AnalyticsManager.getInstance(RegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED, label, null);
                    break;
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
                if (province instanceof Area)
                {
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, province);
                } else
                {
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                }

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
            switch (mPlaceType)
            {
                case HOTEL:
                    if (position == 0)
                    {
                        AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null);
                    } else
                    {
                        AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL, null);
                    }
                    break;

                case FNB:
                    AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
                    break;
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

    private OnResponsePresenterListener mOnResponsePresenterListener = new OnResponsePresenterListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList)
        {
            ArrayList<RegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            switch (mPlaceType)
            {
                case HOTEL:
                {
                    arrayList.get(0).setRegionViewList(RegionListActivity.this, domesticList);
                    arrayList.get(1).setRegionViewList(RegionListActivity.this, globalList);
                    break;
                }

                case FNB:
                {
                    arrayList.get(0).setRegionViewList(RegionListActivity.this, domesticList);
                    break;
                }
            }

            unLockUI();
        }

        @Override
        public void onInternalError()
        {
            unLockUI();
            RegionListActivity.this.onInternalError();
        }

        @Override
        public void onInternalError(String message)
        {
            unLockUI();
            RegionListActivity.this.onInternalError(message);
        }
    };
}