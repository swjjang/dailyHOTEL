package com.twoheart.dailyhotel.screen.gourmet.region;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceRegionFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.screen.gourmet.search.GourmetSearchActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

public class GourmetRegionListActivity extends PlaceRegionListActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private static final int GOURMET_TAB_COUNT = 1;

    private ViewPager mViewPager;
    private PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;

    private GourmetRegionListPresenter mRegionListPresenter;
    private SaleTime mSaleTime;
    private Province mSelectedProvince;

    public static Intent newInstance(Context context, Province province, SaleTime saleTime)
    {
        Intent intent = new Intent(context, GourmetRegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);

        return intent;
    }

    @Override
    protected void initPrepare()
    {
        mRegionListPresenter = new GourmetRegionListPresenter(this, mOnResponsePresenterListener);
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
        tabLayout.setVisibility(ViewPager.GONE);
    }

    @Override
    protected void initViewPager(TabLayout tabLayout)
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(GOURMET_TAB_COUNT);

        GourmetRegionListFragment regionListFragment01 = new GourmetRegionListFragment();
        regionListFragment01.setInformation(Region.DOMESTIC, false ? null : mSelectedProvince);
        regionListFragment01.setOnUserActionListener(mOnUserActionListener);
        fragmentList.add(regionListFragment01);

        mFragmentPagerAdapter = new PlaceRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(GOURMET_TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        AnalyticsManager.getInstance(GourmetRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, null);
    }

    @Override
    protected void showSearch()
    {
        Intent intent = GourmetSearchActivity.newInstance(this, mSaleTime);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.GOURMET_LOCATION_LIST, null);
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

            AnalyticsManager.getInstance(GourmetRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED, label, null);
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

    private OnResponsePresenterListener mOnResponsePresenterListener = new OnResponsePresenterListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList)
        {
            ArrayList<PlaceRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(GourmetRegionListActivity.this, domesticList);

            unLockUI();
        }

        @Override
        public void onInternalError()
        {
            unLockUI();
            GourmetRegionListActivity.this.onErrorMessage();
        }

        @Override
        public void onInternalError(String message)
        {
            unLockUI();
            GourmetRegionListActivity.this.onErrorMessage(message);
        }
    };
}