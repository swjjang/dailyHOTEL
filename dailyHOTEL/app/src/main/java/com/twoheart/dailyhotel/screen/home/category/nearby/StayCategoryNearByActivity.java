package com.twoheart.dailyhotel.screen.home.category.nearby;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayCategoryNearByCuration;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultCurationActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 5. 19..
 */

public class StayCategoryNearByActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    public static final String INTENT_EXTRA_DATA_LOCATION = "location";
    public static final String INTENT_EXTRA_DATA_CALL_BY_SCREEN = "callByScreen";

    protected static final double DEFAULT_SEARCH_RADIUS = 10d;

    protected ViewType mViewType = ViewType.LIST;

    protected boolean mIsFixedLocation;
    protected String mCallByScreen;

    protected int mSearchCount;
    protected int mSearchMaxCount;

    protected PlaceViewItem mPlaceViewItemByLongPress;
    protected int mListCountByLongPress;
    protected View mViewByLongPress;

    protected TodayDateTime mTodayDateTime;

    protected StayCategoryNearByLayout mStayCategoryNearByLayout;

    private DailyCategoryType mDailyCategoryType;

    int mReceiveDataFlag; // 0 연동 전 , 1 데이터 리시브 상태, 2 로그 발송 상태

    String mAddress;

    StayCategoryNearByCuration mStayCategoryNearByCuration;

    private StayCategoryNearByNetworkController mNetworkController;

    public static Intent newInstance(Context context //
        , TodayDateTime todayDateTime, StayBookingDay stayBookingDay //
        , Location location, DailyCategoryType dailyCategoryType, String callByScreen)
    {
        Intent intent = new Intent(context, StayCategoryNearByActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE, (Parcelable) dailyCategoryType);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mStayCategoryNearByLayout = new StayCategoryNearByLayout(this, mOnEventListener);

        initIntent(getIntent());

        setContentView(mStayCategoryNearByLayout.onCreateView(R.layout.activity_search_result));

        initLayout();

        lockUI();

        mNetworkController = new StayCategoryNearByNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        mStayCategoryNearByLayout.setViewTypeVisibility(true);

        mNetworkController.requestAddress(mStayCategoryNearByCuration.getLocation());

        mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        mStayCategoryNearByLayout.processListLayout();

        mStayCategoryNearByLayout.setCategoryTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mStayCategoryNearByLayout != null && mStayCategoryNearByLayout.getBlurVisibility() == true)
        {
            mStayCategoryNearByLayout.setBlurVisibility(this, false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void onBackPressed()
    {
        finish(RESULT_CANCELED);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void finish(int resultCode)
    {
        if (mStayCategoryNearByLayout != null && mStayCategoryNearByLayout.isEmptyLayout() == false)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, getKeyword());
            setResult(resultCode, intent);
        } else
        {
            setResult(resultCode);
        }

        if (resultCode == RESULT_CANCELED)
        {
            requestAnalyticsByCanceled();
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                onCalendarActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAYCURATION:
            {
                onCurationActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_GOURMETCURATION:
            {
                onCurationActivityResult(resultCode, data);
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                if (mViewType == ViewType.MAP)
                {
                    PlaceListFragment placeListFragment = mStayCategoryNearByLayout.getCurrentPlaceListFragment();
                    placeListFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (mViewType == ViewType.MAP)
                {
                    switch (resultCode)
                    {
                        case CODE_RESULT_ACTIVITY_GO_HOME:
                            finish(resultCode);
                            break;

                        default:
                            PlaceListFragment placeListFragment = mStayCategoryNearByLayout.getCurrentPlaceListFragment();
                            placeListFragment.onActivityResult(requestCode, resultCode, data);
                            break;
                    }
                } else
                {
                    switch (resultCode)
                    {
                        case Activity.RESULT_OK:
                            searchMyLocation();
                            break;

                        case CODE_RESULT_ACTIVITY_GO_HOME:
                            finish(resultCode);
                            break;

                        default:
                            onLocationFailed();
                            break;
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish(resultCode);
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    finish(resultCode);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            onPlaceDetailClickByLongPress(mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                }
                break;
        }
    }

    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            StayBookingDay stayBookingDay = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (stayBookingDay == null)
            {
                return;
            }

            mStayCategoryNearByCuration.setStayBookingDay(stayBookingDay);

            mStayCategoryNearByLayout.setCalendarText(stayBookingDay);

            // 날짜가 바뀌면 전체탭으로 이동하고 다시 재로딩.
            mStayCategoryNearByCuration.getCurationOption().clear();

            mStayCategoryNearByLayout.setOptionFilterSelected(false);
            mStayCategoryNearByLayout.clearCategoryTab();
            mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
            mStayCategoryNearByLayout.processListLayout();
            mStayCategoryNearByLayout.setCategoryTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
        }
    }

    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

            if ((placeCuration instanceof StayCuration) == false)
            {
                return;
            }

            StayCuration changedStayCuration = (StayCuration) placeCuration;
            StayCurationOption changedStayCurationOption = (StayCurationOption) changedStayCuration.getCurationOption();

            mStayCategoryNearByCuration.setCurationOption(changedStayCurationOption);
            mStayCategoryNearByLayout.setOptionFilterSelected(changedStayCurationOption.isDefaultFilter() == false);

            if (changedStayCurationOption.getSortType() == SortType.DISTANCE)
            {
                mStayCategoryNearByCuration.setLocation(changedStayCuration.getLocation());

                searchMyLocation();
            } else
            {
                refreshCurrentFragment(true);
            }
        } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
        {
            setResult(resultCode);
            finish();
        }
    }

    protected void onLocationFailed()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryNearByCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mStayCategoryNearByLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    protected void onLocationProviderDisabled()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStayCategoryNearByCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mStayCategoryNearByLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            mStayCategoryNearByCuration.getCurationOption().setSortType(SortType.DEFAULT);
            refreshCurrentFragment(true);
        } else
        {
            mStayCategoryNearByCuration.setLocation(location);

            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            if (mStayCategoryNearByCuration.getCurationOption().getSortType() == SortType.DISTANCE)
            {
                refreshCurrentFragment(true);
            }
        }
    }

    protected void initIntent(Intent intent)
    {
        StayBookingDay stayBookingDay;

        try
        {
            mTodayDateTime = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_TODAYDATETIME);
            stayBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        if (stayBookingDay == null)
        {
            finish();
            return;
        }

        try
        {
            mDailyCategoryType = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_DAILY_CATEGORY_TYPE);
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        if (mDailyCategoryType == null || DailyCategoryType.NONE == mDailyCategoryType)
        {
            finish();
            return;
        }

        Location location = null;
        double radius = DEFAULT_SEARCH_RADIUS;

        mStayCategoryNearByCuration = new StayCategoryNearByCuration();

        if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            location = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);

            if (intent.hasExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN) == true)
            {
                mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN);
            }

            mStayCategoryNearByCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
        } else
        {
            finish();
            return;
        }

        // 내주변 위치 검색으로 시작하는 경우에는 특정 반경과 거리순으로 시작해야한다.
        mStayCategoryNearByCuration.getCurationOption().setSortType(SortType.DISTANCE);
        mStayCategoryNearByCuration.setRadius(radius);

        mStayCategoryNearByCuration.setLocation(location);
        mStayCategoryNearByCuration.setStayBookingDay(stayBookingDay);
        mStayCategoryNearByCuration.setCategory( //
            new Category(getResources().getString(mDailyCategoryType.getNameResId()) //
                , getResources().getString(mDailyCategoryType.getCodeResId())));
    }

    protected void initLayout()
    {
        if (mStayCategoryNearByCuration == null)
        {
            finish();
            return;
        }

        StayBookingDay stayBookingDay = mStayCategoryNearByCuration.getStayBookingDay();

        if (stayBookingDay == null)
        {
            return;
        }

        try
        {
            mStayCategoryNearByLayout.setToolbarTitle("");

            mStayCategoryNearByLayout.setSpinnerVisible(true);

            mStayCategoryNearByLayout.setSelectionSpinner(mStayCategoryNearByCuration.getRadius());

            mStayCategoryNearByLayout.setCalendarText(stayBookingDay);

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    protected Keyword getKeyword()
    {
        return mStayCategoryNearByCuration.getKeyword();
    }

    protected PlaceCuration getPlaceCuration()
    {
        return mStayCategoryNearByCuration;
    }

    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mOnStayListFragmentListener == null)
        {
            return;
        }

        mOnStayListFragmentListener.onStayClick(view, placeViewItem, listCount);
    }

    void recordScreenSearchResult(String screen)
    {
        if (AnalyticsManager.Screen.SEARCH_RESULT.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.SEARCH_RESULT_EMPTY.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        try
        {
            StayBookingDay stayBookingDay = mStayCategoryNearByCuration.getStayBookingDay();
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, mStayCategoryNearByCuration.getCategory().code);

            Province province = mStayCategoryNearByCuration.getProvince();
            if (province instanceof Area)
            {
                Area area = (Area) province;
                params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else if (province != null)
            {
                params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordScreen(StayCategoryNearByActivity.this, screen, null, params);

            if (mCallByScreen == AnalyticsManager.Screen.HOME)
            {
                AnalyticsManager.getInstance(StayCategoryNearByActivity.this) //
                    .recordScreen(StayCategoryNearByActivity.this, AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY, null, params);
            }
        } catch (Exception e)
        {
        }
    }

    void recordEventSearchResultByLocation(String address, boolean isEmpty, Map<String, String> params)
    {
        if (DailyTextUtils.isTextEmpty(address))
        {
            return;
        }

        try
        {
            String action = null;

            if (AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(mCallByScreen) == true)
            {
                action = (isEmpty == true) ? AnalyticsManager.Action.AROUND_SEARCH_NOT_FOUND : AnalyticsManager.Action.AROUND_SEARCH_CLICKED;
                params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);
                params.put(AnalyticsManager.KeyType.SEARCH_WORD, address);
                params.put(AnalyticsManager.KeyType.SEARCH_RESULT, address);


            } else if (AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC.equalsIgnoreCase(mCallByScreen) == true)
            {
                action = (isEmpty == true) ? AnalyticsManager.Action.AROUND_SEARCH_NOT_FOUND_LOCATIONLIST : AnalyticsManager.Action.AROUND_SEARCH_CLICKED_LOCATIONLIST;
            } else if (AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_GLOBAL.equalsIgnoreCase(mCallByScreen) == true)
            {
                action = (isEmpty == true) ? AnalyticsManager.Action.AROUND_SEARCH_NOT_FOUND_LOCATIONLIST : AnalyticsManager.Action.AROUND_SEARCH_CLICKED_LOCATIONLIST;
            }

            if (DailyTextUtils.isTextEmpty(action) == false)
            {
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , action, address, params);
            }
        } catch (Exception e)
        {
        }
    }

    protected void refreshCurrentFragment(boolean isClearList)
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (isClearList == true)
        {
            for (PlaceListFragment placeListFragment : mStayCategoryNearByLayout.getPlaceListFragment())
            {
                // 메인의 클리어 리스트의 경우 타화면에 영향을 줌으로 전체 리스트 데이터를 클리어함
                placeListFragment.clearList();
                // 해당 리스트의 viewType이 gone일 수 있음, 해당 경우 메인의 viewType을 따름
                placeListFragment.setViewType(mViewType);
            }
        }

        PlaceListFragment currentListFragment = mStayCategoryNearByLayout.getCurrentPlaceListFragment();
        if (currentListFragment != null)
        {
            currentListFragment.setPlaceCuration(getPlaceCuration());
            currentListFragment.refreshList(true);
        }
    }

    protected void searchMyLocation()
    {
        if (isFinishing() || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        if (mIsFixedLocation == true)
        {
            PlaceCuration placeCuration = getPlaceCuration();

            if (placeCuration != null)
            {
                onLocationChanged(placeCuration.getLocation());
            }
        } else
        {
            lockUI();

            DailyLocationFactory.getInstance(this).startLocationMeasure(this, null, new DailyLocationFactory.LocationListenerEx()
            {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onRequirePermission()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    Intent intent = PermissionManagerActivity.newInstance(StayCategoryNearByActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
                }

                @Override
                public void onFailed()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    onLocationFailed();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras)
                {
                    unLockUI();

                }

                @Override
                public void onProviderEnabled(String provider)
                {
                    unLockUI();
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
                    DailyLocationFactory.getInstance(StayCategoryNearByActivity.this).stopLocationMeasure();

                    showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                onLocationProviderDisabled();
                            }
                        }, false);
                }

                @Override
                public void onLocationChanged(Location location)
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    DailyLocationFactory.getInstance(StayCategoryNearByActivity.this).stopLocationMeasure();

                    StayCategoryNearByActivity.this.onLocationChanged(location);
                }
            });
        }
    }

    protected void setScrollListTop()
    {
        if (isFinishing() == true)
        {
            return;
        }

        PlaceListFragment placeListFragment = mStayCategoryNearByLayout.getCurrentPlaceListFragment();
        if (placeListFragment != null)
        {
            placeListFragment.setScrollListTop();
        }
    }

    protected void requestAnalyticsByCanceled()
    {
        if (AnalyticsManager.Screen.HOME == mCallByScreen)
        {
            AnalyticsManager.getInstance(this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK //
                , AnalyticsManager.Label.NEAR_BY, null);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StayCategoryNearByLayout.OnEventListener mOnEventListener = new StayCategoryNearByLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            StayCategoryNearByActivity.this.finish();
        }

        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            Category category = (Category) tab.getTag();
            mStayCategoryNearByCuration.setCategory(category);

            mStayCategoryNearByLayout.setCurrentItem(tab.getPosition());
            mStayCategoryNearByLayout.showBottomLayout(false);

            refreshCurrentFragment(false);
        }

        @Override
        public void onCategoryTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onCategoryTabReselected(TabLayout.Tab tab)
        {
            setScrollListTop();
        }

        @Override
        public void onDateClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayBookingDay stayBookingDay = mStayCategoryNearByCuration.getStayBookingDay();

            Intent intent = StayCalendarActivity.newInstance(StayCategoryNearByActivity.this, //
                mTodayDateTime, stayBookingDay, AnalyticsManager.ValueType.SEARCH_RESULT, true, true);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void onViewTypeClick()
        {
            if (isFinishing() == true || isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            StayCategoryNearByListFragment currentFragment = (StayCategoryNearByListFragment) mStayCategoryNearByLayout.getCurrentPlaceListFragment();

            if (currentFragment == null)
            {
                unLockUI();
                return;
            }

            switch (mViewType)
            {
                case LIST:
                {
                    // 고메 쪽에서 보여지는 메세지로 Stay의 경우도 동일한 처리가 필요해보여서 추가함
                    if (currentFragment.hasSalesPlace() == false)
                    {
                        unLockUI();

                        DailyToast.showToast(StayCategoryNearByActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;

                    AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
                    break;
                }

                case MAP:
                {
                    mViewType = ViewType.LIST;
                    break;
                }
            }

            mStayCategoryNearByLayout.setOptionViewTypeView(mViewType);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            for (PlaceListFragment placeListFragment : mStayCategoryNearByLayout.getPlaceListFragment())
            {
                boolean isCurrentFragment = (placeListFragment == currentFragment);
                placeListFragment.setVisibility(mViewType, isCurrentFragment);
            }

            refreshCurrentFragment(false);

            unLockUI();
        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = StaySearchResultCurationActivity.newInstance(StayCategoryNearByActivity.this,//
                mViewType, SearchType.LOCATION, mStayCategoryNearByCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void finish(int resultCode)
        {
            StayCategoryNearByActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL_, null);
            } else
            {
                AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            StayCategoryNearByActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.SEARCH_AGAIN, null);
        }

        @Override
        public void onShowCallDialog()
        {
            showDailyCallDialog(null);

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
        }

        @Override
        public void onItemSelectedSpinner(double radius)
        {
            if (mStayCategoryNearByCuration == null)
            {
                return;
            }

            mStayCategoryNearByCuration.setRadius(radius);
            refreshCurrentFragment(true);

            String action;
            if (radius > 5)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_10; // 10km
            } else if (radius > 3)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_5; // 5km
            } else if (radius > 1)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_3; // 3km
            } else if (radius > 0.5)
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_1; // 1km
            } else
            {
                action = AnalyticsManager.Action.NEARBY_DISTANCE_05; // 0.5km
            }

            AnalyticsManager.getInstance(StayCategoryNearByActivity.this) //
                .recordEvent(AnalyticsManager.Category.NAVIGATION, action, mAddress, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StayCategoryNearByNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayCategoryNearByNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAddress(String address)
        {
            if (isFinishing() == true)
            {
                return;
            }

            synchronized (StayCategoryNearByActivity.this)
            {
                if (mReceiveDataFlag == 0)
                {
                    mReceiveDataFlag = 1;
                } else if (mReceiveDataFlag == 1)
                {
                    ArrayList<PlaceListFragment> placeListFragmentList = mStayCategoryNearByLayout.getPlaceListFragment();
                    if (placeListFragmentList != null || placeListFragmentList.size() > 0)
                    {
                        StayBookingDay stayBookingDay = mStayCategoryNearByCuration.getStayBookingDay();
                        Map<String, String> params = new HashMap<>();

                        try
                        {
                            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
                            params.put(AnalyticsManager.KeyType.CATEGORY, mStayCategoryNearByCuration.getCategory().code);

                            Province province = mStayCategoryNearByCuration.getProvince();
                            if (province instanceof Area)
                            {
                                Area area = (Area) province;
                                params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                                params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                                params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                            } else if (province != null)
                            {
                                params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                                params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                            }
                            params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(mSearchCount > mSearchMaxCount ? mSearchMaxCount : mSearchCount));
                        } catch (Exception e)
                        {

                        }

                        int placeCount = placeListFragmentList.get(0).getPlaceCount();
                        recordEventSearchResultByLocation(address, placeCount == 0, params);
                        mReceiveDataFlag = 2;
                    }
                }
            }

            mAddress = address;
            mStayCategoryNearByLayout.setToolbarTitle(address);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayCategoryNearByActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayCategoryNearByActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayCategoryNearByActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayCategoryNearByActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayCategoryNearByActivity.this.onErrorResponse(call, response);
        }
    };

    StayCategoryNearByListFragment.OnStayCategoryNearByListFragmentListener mOnStayListFragmentListener = new StayCategoryNearByListFragment.OnStayCategoryNearByListFragmentListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onStayClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Stay stay = placeViewItem.getItem();

            if (Util.isUsedMultiTransition() == true)
            {
                setExitSharedElementCallback(new SharedElementCallback()
                {
                    @Override
                    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                    {
                        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                        for (View view : sharedElements)
                        {
                            if (view instanceof SimpleDraweeView)
                            {
                                view.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                });

                Intent intent = StayDetailActivity.newInstance(StayCategoryNearByActivity.this, //
                    mStayCategoryNearByCuration.getStayBookingDay(), stay, listCount, true);

                View simpleDraweeView = view.findViewById(R.id.imageView);
                View gradeTextView = view.findViewById(R.id.gradeTextView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                Object mapTag = gradientBottomView.getTag();

                if (mapTag != null && "map".equals(mapTag) == true)
                {
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
                }

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StayCategoryNearByActivity.this,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
            } else
            {
                Intent intent = StayDetailActivity.newInstance(StayCategoryNearByActivity.this, //
                    mStayCategoryNearByCuration.getStayBookingDay(), stay, listCount, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            if (stay.truevr == true)
            {
                AnalyticsManager.getInstance(StayCategoryNearByActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
            }
        }

        @Override
        public void onStayLongClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mStayCategoryNearByLayout.setBlurVisibility(StayCategoryNearByActivity.this, true);

            Stay stay = placeViewItem.getItem();

            // 기존 데이터를 백업한다.
            mViewByLongPress = view;
            mPlaceViewItemByLongPress = placeViewItem;
            mListCountByLongPress = listCount;

            Intent intent = StayPreviewActivity.newInstance(StayCategoryNearByActivity.this, mStayCategoryNearByCuration.getStayBookingDay(), stay);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void onCategoryList(List<Category> categoryList)
        {
            if (categoryList != null && categoryList.size() > 0)
            {
                mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
                mStayCategoryNearByLayout.processListLayout();
                mStayCategoryNearByLayout.addCategoryTabLayout(categoryList, mOnStayListFragmentListener);
            } else
            {
                mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.GONE);
                mStayCategoryNearByLayout.showEmptyLayout();
            }
        }

        @Override
        public void onActivityCreated(PlaceListFragment placeListFragment)
        {
            if (mStayCategoryNearByLayout == null || placeListFragment == null)
            {
                return;
            }

            PlaceListFragment currentPlaceListFragment = mStayCategoryNearByLayout.getCurrentPlaceListFragment();
            if (currentPlaceListFragment == placeListFragment)
            {
                currentPlaceListFragment.setVisibility(mViewType, true);
                currentPlaceListFragment.setPlaceCuration(mStayCategoryNearByCuration);
                currentPlaceListFragment.refreshList(true);
            } else
            {
                placeListFragment.setVisibility(mViewType, false);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            mStayCategoryNearByLayout.calculationMenuBarLayoutTranslationY(dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            switch (newState)
            {
                case RecyclerView.SCROLL_STATE_IDLE:
                {
                    mStayCategoryNearByLayout.animationMenuBarLayout();

                    //                    ExLog.d("offset : " + recyclerView.computeVerticalScrollOffset() + ", " + recyclerView.computeVerticalScrollExtent() + ", " + recyclerView.computeVerticalScrollRange());

                    if (recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() >= recyclerView.computeVerticalScrollRange())
                    {
                        StayListAdapter stayListAdapter = (StayListAdapter) recyclerView.getAdapter();

                        if (stayListAdapter != null)
                        {
                            int count = stayListAdapter.getItemCount();

                            if (count == 0)
                            {
                            } else
                            {
                                PlaceViewItem placeViewItem = stayListAdapter.getItem(stayListAdapter.getItemCount() - 1);

                                if (placeViewItem != null && placeViewItem.mType == PlaceViewItem.TYPE_FOOTER_VIEW)
                                {
                                    mStayCategoryNearByLayout.showBottomLayout(false);
                                }
                            }
                        }
                    }
                    break;
                }

                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;

                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onShowMenuBar()
        {

        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = StaySearchResultCurationActivity.newInstance(StayCategoryNearByActivity.this, //
                mViewType, SearchType.LOCATION, mStayCategoryNearByCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);
        }

        @Override
        public void onUpdateFilterEnabled(boolean isShowFilterEnabled)
        {
            if (mStayCategoryNearByLayout == null)
            {
                return;
            }

            mStayCategoryNearByLayout.setOptionFilterEnabled(isShowFilterEnabled);
        }

        @Override
        public void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled)
        {
            if (mStayCategoryNearByLayout == null)
            {
                return;
            }

            mStayCategoryNearByLayout.setOptionViewTypeEnabled(isShowViewTypeEnabled);
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {
            if (mStayCategoryNearByLayout == null)
            {
                return;
            }

            if (isShow == true)
            {
                mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.GONE);
                mStayCategoryNearByLayout.showEmptyLayout();

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT_EMPTY);
            } else
            {
                if (mStayCategoryNearByLayout.getCategoryTabCount() <= 2)
                {
                    mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.GONE);
                } else
                {
                    mStayCategoryNearByLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
                }

                mStayCategoryNearByLayout.showListLayout();
            }

            StayBookingDay stayBookingDay = mStayCategoryNearByCuration.getStayBookingDay();
            Map<String, String> params = new HashMap<>();
            try
            {
                params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
                params.put(AnalyticsManager.KeyType.CATEGORY, mStayCategoryNearByCuration.getCategory().code);

                Province province = mStayCategoryNearByCuration.getProvince();
                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else if (province != null)
                {
                    params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }
                params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(mSearchCount > mSearchMaxCount ? mSearchMaxCount : mSearchCount));
            } catch (Exception e)
            {

            }

            synchronized (StayCategoryNearByActivity.this)
            {
                if (mReceiveDataFlag == 0)
                {
                    mReceiveDataFlag = 1;
                } else
                {
                    recordEventSearchResultByLocation(mAddress, isShow, params);
                    mReceiveDataFlag = 2;
                }
            }
        }

        @Override
        public void onRecordAnalytics(ViewType viewType)
        {
            try
            {
                if (viewType == ViewType.LIST)
                {
                    recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT);
                }
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }

        @Override
        public void onSearchCountUpdate(int searchCount, int searchMaxCount)
        {
            mSearchCount = searchCount;
            mSearchMaxCount = searchMaxCount;
        }
    };
}
