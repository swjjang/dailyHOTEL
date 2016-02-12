package com.twoheart.dailyhotel.screen.regionlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
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

    private PlaceMainFragment.TYPE mType;
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

    public static Intent newInstance(Context context, PlaceMainFragment.TYPE type, Province province)
    {
        Intent intent = new Intent(context, RegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, type.toString());
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
        mType = TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        Province selectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

        if (mType == null)
        {
            Util.restartApp(this);
            return;
        }

        // 국내로 시작하는지 헤외로 시작하는지
        // 고메인 경우에는 해외 지역이 없기 때문에 기존과 동일하게?

        initLayout(mType, selectedProvince);
    }

    private void initLayout(PlaceMainFragment.TYPE type, Province province)
    {
        if (mType == null)
        {
            return;
        }

        setContentView(R.layout.activity_region_list);

        initToolbar();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        switch (type)
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
                        // 어디선가에서 Proince가 누락되는데 찾을수가 없음ㅜㅜ
                        try
                        {
                            isOverseas = ((Area) province).getProvince().isOverseas;
                        }catch (NullPointerException e)
                        {
                            isOverseas = DailyPreference.getInstance(this).isSelectedOverseaRegion(TYPE.HOTEL);
                        }
                    } else
                    {
                        isOverseas = province.isOverseas;
                    }
                }

                regionListFragment01.setInformation(type, Region.DOMESTIC, isOverseas ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                RegionListFragment regionListFragment02 = new RegionListFragment();
                regionListFragment02.setInformation(type, Region.GLOBAL, isOverseas ? province : null);
                regionListFragment02.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment02);

                tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(HOTEL_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                mViewPager.setCurrentItem(isOverseas ? 1 : 0);
                break;
            }

            case FNB:
            {
                tabLayout.setVisibility(ViewPager.GONE);

                ArrayList<RegionListFragment> fragmentList = new ArrayList<>(GOURMET_TAB_COUNT);

                RegionListFragment regionListFragment01 = new RegionListFragment();
                regionListFragment01.setInformation(type, Region.DOMESTIC, false ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(GOURMET_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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

        switch (mType)
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

            if(province instanceof Area)
            {
                Area area = (Area)province;

                if(area.index == -1)
                {
                    label = String.format("%s_%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name);
                } else
                {
                    label = String.format("%s_%s_%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name, area.name);
                }
            } else
            {
                label = String.format("%s_%s", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                    , province.name);
            }

            AnalyticsManager.getInstance(RegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_LOCATIONS_CLICKED, label, 0L);
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
            switch(mType)
            {
                case HOTEL:
                    if(position == 0)
                    {
                        AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
                    } else
                    {
                        AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
                    }
                    break;

                case FNB:
                    AnalyticsManager.getInstance(RegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC);
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

            switch (mType)
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