package com.twoheart.dailyhotel.screen.hotel.region;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceRegionFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceRegionListFragment;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceRegionListNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StayRegionListActivity extends PlaceRegionListActivity
{
    public static final String INTENT_EXTRA_DATA_PROVINCE_INDEX = "provinceIndex";
    public static final String INTENT_EXTRA_DATA_AREA_INDEX = "areaIndex";

    private static final int STAY_TAB_COUNT = 1;

    ViewPager mViewPager;
    PlaceRegionFragmentPagerAdapter mFragmentPagerAdapter;

    private StayRegionListNetworkController mNetworkController;
    StayBookingDay mStayBookingDay;
    Province mSelectedProvince;
    private String mCategoryCode;

    public static Intent newInstance(Context context, Province province, StayBookingDay stayBookingDay, String categoryCode)
    {
        Intent intent = new Intent(context, StayRegionListActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, categoryCode);

        return intent;
    }

    public static Intent newInstance(Context context, int provinceIndex, int areaIndex, StayBookingDay stayBookingDay, String categoryCode)
    {
        Intent intent = new Intent(context, StayRegionListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PROVINCE_INDEX, provinceIndex);
        intent.putExtra(INTENT_EXTRA_DATA_AREA_INDEX, areaIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, categoryCode);

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
        mStayBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mCategoryCode = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);
    }

    @Override
    protected void initToolbar(View toolbar)
    {
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_selectarea_stay_area), R.drawable.navibar_ic_x, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, false);

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
    protected void initViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        ArrayList<PlaceRegionListFragment> fragmentList = new ArrayList<>(STAY_TAB_COUNT);

        StayRegionListFragment regionListFragment01 = new StayRegionListFragment();
        regionListFragment01.setInformation(Region.DOMESTIC, mSelectedProvince, mCategoryCode);
        regionListFragment01.setOnPlaceRegionListFragmentListener(mOnPlaceRegionListFragment);
        fragmentList.add(regionListFragment01);

        mFragmentPagerAdapter = new PlaceRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(STAY_TAB_COUNT);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.clearOnPageChangeListeners();

        try
        {
            Map<String, String> params = new HashMap<>();

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, mCategoryCode);

            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null, params);
        } catch (Exception e)
        {

        }
    }

    @Override
    protected void showSearch()
    {
        Intent intent = SearchActivity.newInstance(this, PlaceType.HOTEL, mStayBookingDay);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, AnalyticsManager.Label.STAY_LOCATION_LIST, null);
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
        void recordEvent(Province province)
        {
            String label;

            if (province instanceof Area)
            {
                Area area = (Area) province;

                if (area.index == -1)
                {
                    label = String.format(Locale.KOREA, "%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name);
                } else
                {
                    label = String.format(Locale.KOREA, "%s-%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name, area.name);
                }
            } else
            {
                label = String.format(Locale.KOREA, "%s-%s", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                    , province.name);
            }

            AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
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
                    label = String.format(Locale.KOREA, "%s-%s-None", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name);
                } else
                {
                    label = String.format(Locale.KOREA, "%s-%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                        , area.getProvince().name, area.name);
                }
            } else
            {
                label = String.format(Locale.KOREA, "%s-%s-None", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
                    , province.name);
            }

            return label;
        }

        private String getRegionAnalytics(Province previousProvince, Province selectedProvince, StayBookingDay stayBookingDay)
        {
            try
            {
                String previousLabel = convertLabelFormatAnalytics(previousProvince);
                String selectedLabel = convertLabelFormatAnalytics(selectedProvince);

                String checkInDate = stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)");
                String checkOutDate = stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)");

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
                    String message = mStayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)") + "-" + mStayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);

                    final String analyticsLabel = getRegionAnalytics(mSelectedProvince, province, mStayBookingDay);

                    showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (analyticsLabel != null)
                            {
                                AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
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
                                AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                                    , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CONFIRMED, analyticsLabel, null);
                            }

                            AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                                , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.CHANGE_LOCATION, null);

                            // 날짜 선택 화면으로 이동한다.
                            Intent intent = new Intent();
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, mStayBookingDay);
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

            AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.HOTEL_LOCATIONS_CLICKED, getString(R.string.label_view_myaround_hotel), null);
        }
    };

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
        if (mViewPager == null || mFragmentPagerAdapter == null)
        {
            return null;
        }

        int position = mViewPager.getCurrentItem();

        return (PlaceRegionListFragment) mFragmentPagerAdapter.getItem(position);
    }

    private PlaceRegionListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceRegionListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> domesticList)
        {
            ArrayList<PlaceRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(StayRegionListActivity.this, domesticList);

            unLockUI();
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayRegionListActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayRegionListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayRegionListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayRegionListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayRegionListActivity.this.onErrorResponse(call, response);
        }
    };
}