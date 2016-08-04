package com.twoheart.dailyhotel.screen.hotel.region;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.RelativeLayout;

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
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyViewPager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StayRegionListActivity extends PlaceRegionListActivity
{
    public static final String INTENT_EXTRA_DATA_PROVINCE_INDEX = "provinceIndex";
    public static final String INTENT_EXTRA_DATA_AREA_INDEX = "areaIndex";
    public static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    public static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private static final int STAY_TAB_COUNT = 2;

    private DailyViewPager mViewPager;
    private PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;

    private StayRegionListNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights;
    private Province mSelectedProvince;
    private TabLayout mTabLayout;

    public static Intent newInstance(Context context, Province province, SaleTime saleTime, int nights)
    {
        Intent intent = new Intent(context, StayRegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);

        return intent;
    }

    public static Intent newInstance(Context context, int provinceIndex, int areaIndex, SaleTime saleTime, int nights)
    {
        Intent intent = new Intent(context, StayRegionListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PROVINCE_INDEX, provinceIndex);
        intent.putExtra(INTENT_EXTRA_DATA_AREA_INDEX, areaIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);

        return intent;
    }

    @Override
    protected void initPrepare()
    {
        mNetworkController = new StayRegionListNetworkController(this, mOnNetworkControllerListener);
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
        if (tabLayout == null)
        {
            return;
        }

        mTabLayout = tabLayout;

        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_domestic));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_global));
        tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - Util.dpToPx(this, 1);

        mTabLayout.setLayoutParams(layoutParams);

        hideTabLayout();

        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    @Override
    protected void initToolbar(View toolbar)
    {
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_stay_area), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        dailyToolbarLayout.setBackImageView(R.drawable.navibar_ic_x);
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

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(STAY_TAB_COUNT);
        StayRegionListFragment regionListFragment01 = new StayRegionListFragment();

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
        regionListFragment01.setOnPlaceRegionListFragmentListener(mOnPlaceRegionListFragment);
        fragmentList.add(regionListFragment01);

        StayRegionListFragment regionListFragment02 = new StayRegionListFragment();
        regionListFragment02.setInformation(Region.GLOBAL, isOverseas ? mSelectedProvince : null);
        regionListFragment02.setOnPlaceRegionListFragmentListener(mOnPlaceRegionListFragment);
        fragmentList.add(regionListFragment02);

        mFragmentPagerAdapter = new PlaceRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(STAY_TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setCurrentItem(isOverseas ? 1 : 0);

        if (isOverseas == true)
        {
            AnalyticsManager.getInstance(StayRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
        } else
        {
            AnalyticsManager.getInstance(StayRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
        }
    }

    @Override
    protected void showSearch()
    {
        Intent intent = SearchActivity.newInstance(this, PlaceType.HOTEL, mSaleTime, mNights);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.SEARCH_BUTTON_CLICKED, AnalyticsManager.Label.HOTEL_LOCATION_LIST, null);
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

    private PlaceRegionListFragment.OnPlaceRegionListFragment mOnPlaceRegionListFragment = new PlaceRegionListFragment.OnPlaceRegionListFragment()
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

            AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
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
            try
            {
                String previousLabel = convertLabelFormatAnalytics(previousProvince);
                String selectedLabel = convertLabelFormatAnalytics(selectedProvince);

                //                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분", Locale.KOREA);

                String checkInDate = checkInTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
                String checkOutDate = checkOutTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

                //                return previousLabel + "-" + selectedLabel + "-" + checkInDate + "-" + checkOutDate + "-" + simpleDateFormat.format(new Date());
                return previousLabel + "-" + selectedLabel + "-" + checkInDate + "-" + checkOutDate + "-" + DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분");
            } catch (Exception e)
            {
                return null;
            }
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
        public void onRegionClick(final Province province)
        {
            if (province == null)
            {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            } else
            {
                if (mSelectedProvince != null && (mSelectedProvince.isOverseas != province.isOverseas//
                    || mSelectedProvince.getProvinceIndex() != province.getProvinceIndex()))
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
                            if (analyticsLabel != null)
                            {
                                AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                    , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CHANGED, analyticsLabel, null);
                            }

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
                            if (analyticsLabel != null)
                            {
                                AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                    , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CONFIRMED, analyticsLabel, null);
                            }

                            AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.CHANGE_LOCATION, null);

                            // 날짜 선택 화면으로 이동한다.
                            Intent intent = new Intent();
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                            intent.putExtra(INTENT_EXTRA_DATA_SALETIME, mSaleTime);
                            intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, mNights);
                            setResult(RESULT_CHANGED_DATE, intent);

                            recordEvent(province);
                            finish();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            unLockUI();
                        }
                    }, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {

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

        @Override
        public void onAroundSearchClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(StayRegionListActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }
    };

    private void removeGlobalRegion()
    {
        mTabLayout.setVisibility(View.GONE);
        mTabLayout.removeTabAt(1);
        mViewPager.setCurrentItem(0);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.setPagingEnabled(false);

        mFragmentPagerAdapter.removeItem(1);
        mFragmentPagerAdapter.notifyDataSetChanged();
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

    private void showTabLayout()
    {
        mTabLayout.setVisibility(View.VISIBLE);
    }

    private void hideTabLayout()
    {
        mTabLayout.setVisibility(View.INVISIBLE);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        private void recordAnalytics(int position)
        {
            if (position == 0)
            {
                AnalyticsManager.getInstance(StayRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC);
            } else
            {
                AnalyticsManager.getInstance(StayRegionListActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL);
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

            arrayList.get(0).setRegionViewList(StayRegionListActivity.this, domesticList);

            if (globalList == null || globalList.size() == 0)
            {
                removeGlobalRegion();
            } else
            {
                showTabLayout();

                arrayList.get(1).setRegionViewList(StayRegionListActivity.this, globalList);
            }

            unLockUI();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            StayRegionListActivity.this.onError(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            StayRegionListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            StayRegionListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            StayRegionListActivity.this.onErrorToastMessage(message);
        }
    };
}