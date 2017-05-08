package com.twoheart.dailyhotel.screen.home.category.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.base.widget.DailyViewPager;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionListActivity extends BaseActivity
{
    private DailyCategoryType mDailyCategoryType;
    private StayBookingDay mStayBookingDay;

    private DailyViewPager mViewPager;
    private HomeCategoryRegionFragmentPagerAdapter mFragmentPagerAdapter; // 임시

    private HomeCategoryRegionListNetworkController mNetworkController;

    public static Intent newInstance(Context context //
        , DailyCategoryType categoryType, StayBookingDay stayBookingDay)
    {
        Intent intent = new Intent(context, HomeCategoryRegionListActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE, (Parcelable) categoryType);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        mNetworkController = new HomeCategoryRegionListNetworkController( //
            HomeCategoryRegionListActivity.this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(R.layout.activity_region_list);

        initIntent(getIntent());

        // 지역로딩시에 백버튼 누르면 종료되도록 수정
        setLockUICancelable(true);
        initLayout();
    }

    private void initLayout()
    {
        initToolbar();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        initTabLayout(tabLayout);
        initViewPager(tabLayout);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        initToolbar(toolbar);
    }

    private void initIntent(Intent intent)
    {
        mDailyCategoryType = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE);
        mStayBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
    }

    private void initTabLayout(TabLayout tabLayout)
    {
        if (tabLayout == null)
        {
            return;
        }

        tabLayout.setVisibility(View.GONE);
    }

    private void initToolbar(View toolbar)
    {
        String categoryName = getResources().getString(mDailyCategoryType.getNameResId());


        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar( //
            getResources().getString(R.string.label_select_area_daily_category_format, categoryName) //
            , R.drawable.navibar_ic_x, v ->
            {
                String label = HomeCategoryRegionListActivity.this.getResources().getString(mDailyCategoryType.getNameResId());

                HomeCategoryRegionListActivity.this.finish();

                AnalyticsManager.getInstance(HomeCategoryRegionListActivity.this).recordEvent( //
                    AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.LOCATION_LIST_CLOSE, label, null);
            }, false);

        dailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_search, -1);
        dailyToolbarLayout.setToolbarMenuClickListener(v -> showSearch());
    }

    private void initViewPager(TabLayout tabLayout)
    {
        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);

        ArrayList<HomeCategoryRegionListFragment> fragmentList = new ArrayList<>();
        HomeCategoryRegionListFragment regionListFragment = new HomeCategoryRegionListFragment();
        regionListFragment.setDailyCategoryType(mDailyCategoryType);
        regionListFragment.setOnFragmentListener(mOnFragmentListener);
        fragmentList.add(regionListFragment);

        mFragmentPagerAdapter = new HomeCategoryRegionFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

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
            params.put(AnalyticsManager.KeyType.CATEGORY, getResources().getString(mDailyCategoryType.getCodeResId()));

            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC, null, params);
        } catch (Exception e)
        {

        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        String label = HomeCategoryRegionListActivity.this.getResources().getString(mDailyCategoryType.getNameResId());

        AnalyticsManager.getInstance(HomeCategoryRegionListActivity.this).recordEvent( //
            AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.LOCATION_LIST_CLOSE, label, null);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_SEARCH:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                updateTermsOfLocationLayout();

                searchMyLocation();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                updateTermsOfLocationLayout();

                if (resultCode == Activity.RESULT_OK)
                {
                    searchMyLocation();
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }
        }
    }

    protected void searchMyLocation()
    {
        lockUI();

        DailyLocationFactory.getInstance(HomeCategoryRegionListActivity.this) //
            .startLocationMeasure(this, null, new DailyLocationFactory.LocationListenerEx()
            {
                @Override
                public void onRequirePermission()
                {
                    unLockUI();

                    Intent intent = PermissionManagerActivity.newInstance( //
                        HomeCategoryRegionListActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
                }

                @Override
                public void onFailed()
                {
                    unLockUI();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderEnabled(String provider)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderDisabled(String provider)
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    DailyLocationFactory.getInstance(HomeCategoryRegionListActivity.this).stopLocationMeasure();

                    HomeCategoryRegionListActivity.this.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , v ->
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        }, null, false);
                }

                @Override
                public void onLocationChanged(Location location)
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    DailyLocationFactory.getInstance(HomeCategoryRegionListActivity.this).stopLocationMeasure();

                    if (location == null)
                    {
                        DailyToast.showToast(HomeCategoryRegionListActivity.this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    } else
                    {
                        // Location
                        Intent intent = new Intent();
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_LOCATION, location);

                        try
                        {
                            HomeCategoryRegionListFragment homeCategoryRegionListFragment = getCurrentFragment();
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_RESULT, homeCategoryRegionListFragment.getResultName());
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                        setResult(RESULT_ARROUND_SEARCH_LIST, intent);
                        finish();
                    }
                }
            });
    }

    private void showSearch()
    {
        // 우선 고메가 없음으로 Stay로 고정
        Intent intent = SearchActivity.newInstance(this, PlaceType.HOTEL, mStayBookingDay);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH);

        String label = "";
        switch (mDailyCategoryType)
        {
            case STAY_HOTEL:
                label = AnalyticsManager.Label.HOTEL_LOCATION_LIST;
                break;
            case STAY_BOUTIQUE:
                label = AnalyticsManager.Label.BOUTIQUE_LOCATION_LIST;
                break;
            case STAY_PENSION:
                label = AnalyticsManager.Label.PENSION_LOCATION_LIST;
                break;
            case STAY_RESORT:
                label = AnalyticsManager.Label.RESORT_LOCATION_LIST;
                break;
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SEARCH//
            , AnalyticsManager.Action.SEARCH_BUTTON_CLICK, label, null);
    }

    private void requestRegionList()
    {
        if (mNetworkController == null)
        {
            Util.restartApp(this);
            return;
        }

        mNetworkController.requestRegionList();
    }

    private void updateTermsOfLocationLayout()
    {
        for (HomeCategoryRegionListFragment fragment : mFragmentPagerAdapter.getFragmentList())
        {
            if (fragment.isAdded() == true)
            {
                fragment.updateTermsOfLocationView();
            }
        }
    }

    private HomeCategoryRegionListFragment getCurrentFragment()
    {
        if (mFragmentPagerAdapter == null)
        {
            return null;
        }

        return (HomeCategoryRegionListFragment) mFragmentPagerAdapter.getItem(0);
    }

    private HomeCategoryRegionListFragment.OnFragmentListener mOnFragmentListener = new HomeCategoryRegionListFragment.OnFragmentListener()
    {
        private void recordEvent(Province province)
        {
            //            String label;
            //
            //            if (province instanceof Area)
            //            {
            //                Area area = (Area) province;
            //
            //                if (area.index == -1)
            //                {
            //                    label = String.format(Locale.KOREA, "%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
            //                        , area.getProvince().name);
            //                } else
            //                {
            //                    label = String.format(Locale.KOREA, "%s-%s-%s", area.getProvince().isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
            //                        , area.getProvince().name, area.name);
            //                }
            //            } else
            //            {
            //                label = String.format(Locale.KOREA, "%s-%s", province.isOverseas ? getString(R.string.label_global) : getString(R.string.label_domestic)//
            //                    , province.name);
            //            }
            //
            //            AnalyticsManager.getInstance(HomeCategoryRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            //                , AnalyticsManager.Action.GOURMET_LOCATIONS_CLICKED, label, null);
        }

        @Override
        public void onActivityCreated(HomeCategoryRegionListFragment homeCategoryRegionListFragment)
        {
            HomeCategoryRegionListFragment currentHomeCategoryRegionListFragment //
                = (HomeCategoryRegionListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());

            if (currentHomeCategoryRegionListFragment == homeCategoryRegionListFragment)
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
                //                DailyPreference.getInstance(HomeCategoryRegionListActivity.this) //
                //                    .setDailyRegion(mDailyCategoryType, Util.getDailyRegionJSONObject(province));
                //
                //                Intent intent = StayCategoryListActivity.newInstance( //
                //                    HomeCategoryRegionListActivity.this, mDailyCategoryType, null);
                //                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY);

                intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE, (Parcelable) mDailyCategoryType);
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

            Intent intent = PermissionManagerActivity.newInstance( //
                HomeCategoryRegionListActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);

            String label = "";
            switch (mDailyCategoryType)
            {
                case STAY_HOTEL:
                    label = AnalyticsManager.Label.HOTEL_LOCATION_LIST;
                    break;
                case STAY_BOUTIQUE:
                    label = AnalyticsManager.Label.BOUTIQUE_LOCATION_LIST;
                    break;
                case STAY_PENSION:
                    label = AnalyticsManager.Label.PENSION_LOCATION_LIST;
                    break;
                case STAY_RESORT:
                    label = AnalyticsManager.Label.RESORT_LOCATION_LIST;
                    break;
            }

            AnalyticsManager.getInstance(HomeCategoryRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.STAY_NEARBY_SEARCH, label, null);
        }
    };

    private HomeCategoryRegionListNetworkController.OnNetworkControllerListener //
        mOnNetworkControllerListener = new HomeCategoryRegionListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRegionListResponse(List<RegionViewItem> regionViewList, List<RegionViewItem> subwayViewList)
        {
            ArrayList<HomeCategoryRegionListFragment> arrayList = mFragmentPagerAdapter.getFragmentList();

            arrayList.get(0).setRegionViewList(HomeCategoryRegionListActivity.this, regionViewList //
                , DailyPreference.getInstance(HomeCategoryRegionListActivity.this).isAgreeTermsOfLocation());

            unLockUI();
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            HomeCategoryRegionListActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            HomeCategoryRegionListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            HomeCategoryRegionListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            HomeCategoryRegionListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            HomeCategoryRegionListActivity.this.onErrorResponse(call, response);
        }
    };
}
