package com.twoheart.dailyhotel.screen.hotel.region;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.twoheart.dailyhotel.place.networkcontroller.PlaceRegionListNetworkController;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyViewPager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HotelRegionListActivity extends PlaceRegionListActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private static final int HOTEL_TAB_COUNT = 2;

    private DailyViewPager mViewPager;
    private PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;

    private HotelRegionListNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights;
    private Province mSelectedProvince;
    private TabLayout mTabLayout;
    private View mToolbarUnderline;
    private int mAttachFragmentCount;

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
        mNetworkController = new HotelRegionListNetworkController(this, mOnNetworkControllerListener);

        mAttachFragmentCount = 0;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSelectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);

        if (++mAttachFragmentCount == HOTEL_TAB_COUNT)
        {
            mAttachFragmentCount = 0;
            lockUI();

            requestRegionList();
        }
    }

    @Override
    protected void initTabLayout(TabLayout tabLayout)
    {
        if (tabLayout == null)
        {
            return;
        }

        mTabLayout = tabLayout;

        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_domestic));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_global));
        tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mToolbarUnderline = findViewById(R.id.toolbarUnderline);

        hideTabLayout();

        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    @Override
    protected void initToolbar(View toolbar)
    {
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_area), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

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
        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(HOTEL_TAB_COUNT);
        HotelRegionListFragment regionListFragment01 = new HotelRegionListFragment();

        boolean isOverseas = false;

        if (mSelectedProvince != null)
        {
            if (mSelectedProvince instanceof Area)
            {
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
            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
        } else
        {
            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
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
        if (mNetworkController == null)
        {
            Util.restartApp(this);
            return;
        }

        mNetworkController.requestRegionList();
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

        private String convertLabelFormatAnalytics(Province province)
        {
            String label;

            if (province instanceof Area)
            {
                Area area = (Area) province;

                if (area.index == -1)
                {
                    label = String.format("%s-%s-None", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name);
                } else
                {
                    label = String.format("%s-%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name, area.name);
                }
            } else
            {
                label = String.format("%s-%s-None", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                    , province.name);
            }

            return label;
        }

        private String getRegionAnalytics(Province previousProvince, Province selectedProvince, SaleTime checkInTime, SaleTime checkOutTime)
        {
            String previousLabel = convertLabelFormatAnalytics(previousProvince);
            String selectedLabel = convertLabelFormatAnalytics(selectedProvince);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분");

            String checkInDate = checkInTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
            String checkOutDate = checkOutTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

            return previousLabel + "-" + selectedLabel + "-" + checkInDate + "-" + checkOutDate + "-" + simpleDateFormat.format(new Date());
        }

        @Override
        public void onRegionClick(final Province province)
        {
            if (province == null)
            {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            } else
            {
                if (mSelectedProvince.isOverseas != province.isOverseas//
                    || mSelectedProvince.getProvinceIndex() != province.getProvinceIndex())
                {
                    String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

                    SaleTime checkOutTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
                    String checkOutDate = checkOutTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

                    String message = checkInDate + "-" + checkOutDate + "\n" + getString(R.string.message_region_search_date);

                    final String analyticsLabel = getRegionAnalytics(mSelectedProvince, province, mSaleTime, checkOutTime);

                    showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CHANGED, analyticsLabel, null);

                            Intent intent = new Intent();
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                            setResult(RESULT_OK, intent);

                            recordEvent(province);
                            finish();
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            AnalyticsManager.getInstance(HotelRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CONFIRMED, analyticsLabel, null);

                            // 날짜 선택 화면으로 이동한다.
                            Intent intent = new Intent();
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                            setResult(RESULT_FIRST_USER, intent);

                            recordEvent(province);
                            finish();
                        }
                    }, true);
                } else
                {
                    Intent intent = new Intent();
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                    setResult(RESULT_OK, intent);

                    recordEvent(province);
                    finish();
                }
            }
        }
    };

    private void removeGlobalRegion()
    {
        mTabLayout.setVisibility(View.GONE);
        mTabLayout.removeTabAt(1);
        mToolbarUnderline.setVisibility(View.GONE);
        mViewPager.setCurrentItem(0);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.setPagingEnabled(false);

        mFragmentPagerAdapter.removeItem(1);
        mFragmentPagerAdapter.notifyDataSetChanged();
    }

    private void showTabLayout()
    {
        mTabLayout.setVisibility(View.VISIBLE);
        mToolbarUnderline.setVisibility(View.VISIBLE);
    }

    private void hideTabLayout()
    {
        mTabLayout.setVisibility(View.INVISIBLE);
        mToolbarUnderline.setVisibility(View.INVISIBLE);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        private void recordAnalytics(int position)
        {
            if (position == 0)
            {
                AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
            } else
            {
                AnalyticsManager.getInstance(HotelRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
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

    private PlaceRegionListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceRegionListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList, List<RegionViewItem> globalList)
        {
            ArrayList<PlaceRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(HotelRegionListActivity.this, domesticList);

            if (globalList == null || globalList.size() == 0)
            {
                removeGlobalRegion();
            } else
            {
                showTabLayout();

                arrayList.get(1).setRegionViewList(HotelRegionListActivity.this, globalList);
            }

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
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            HotelRegionListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            HotelRegionListActivity.this.onErrorToastMessage(message);
        }
    };
}