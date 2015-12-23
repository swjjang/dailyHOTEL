package com.twoheart.dailyhotel.screen.region;

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

import java.util.ArrayList;

public class RegionListActivity extends BaseActivity
{
    private static final int HOTEL_TAB_COUNT = 2;
    private static final int GOURMET_TAB_COUNT = 1;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RegionFragmentPagerAdapter mFragmentPagerAdapter;

    private PlaceMainFragment.TYPE mType;
    private Province mSelectedProvince;

    public interface OnUserActionListener
    {
        void onRegionClick(Province province);
    }

    public enum Region
    {
        DOMESTIC,
        GLOBAL
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

        // 호텔 인지 고메인지
        mType = PlaceMainFragment.TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        mSelectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

        // 국내로 시작하는지 헤외로 시작하는지
        // 고메인 경우에는 해외 지역이 없기 때문에 기존과 동일하게?

        initLayout(mType, mSelectedProvince);
    }

    private void initLayout(PlaceMainFragment.TYPE type, Province province)
    {
        setContentView(R.layout.activity_region_list);

        initToolbar();

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        switch (type)
        {
            case HOTEL:
            {
                int selectTab = 0;

                if (province.isOverseas == true)
                {
                    mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_domestic));
                    mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_global));
                } else
                {
                    mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_domestic));
                    mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_global));
                }

                ArrayList<RegionListFragment> fragmentList = new ArrayList<>(HOTEL_TAB_COUNT);

                RegionListFragment regionListFragment01 = new RegionListFragment();
                regionListFragment01.setInformation(mType, Region.DOMESTIC, province.isOverseas ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                RegionListFragment regionListFragment02 = new RegionListFragment();
                regionListFragment02.setInformation(mType, Region.GLOBAL, province.isOverseas ? province : null);
                regionListFragment02.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment02);

                mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(HOTEL_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                break;
            }

            case FNB:
            {
                mTabLayout.setVisibility(ViewPager.GONE);

                ArrayList<RegionListFragment> fragmentList = new ArrayList<>(GOURMET_TAB_COUNT);

                RegionListFragment regionListFragment01 = new RegionListFragment();
                regionListFragment01.setInformation(mType, Region.DOMESTIC, province.isOverseas ? null : province);
                regionListFragment01.setOnUserActionListener(mOnUserActionListener);
                fragmentList.add(regionListFragment01);

                mFragmentPagerAdapter = new RegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

                mViewPager.setOffscreenPageLimit(GOURMET_TAB_COUNT);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

                regionListFragment01.onPageSelected();
                break;
            }
        }
    }

    private void initToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolbar(mToolbar, getString(R.string.label_selectarea_area));
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
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
            }

            finish();
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            RegionListFragment fragment = (RegionListFragment) mFragmentPagerAdapter.getItem(tab.getPosition());
            fragment.onPageSelected();
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
}