package com.twoheart.dailyhotel.screen.search.stay.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchResultNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StaySearchResultActivity extends PlaceSearchResultActivity
{
    int mReceiveDataFlag; // 0 연동 전 , 1 데이터 리시브 상태, 2 로그 발송 상태

    String mInputText;
    String mAddress;

    SearchType mSearchType;
    StaySearchCuration mStaySearchCuration;

    private PlaceSearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, String inputText, Keyword keyword, SearchType searchType)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);

        return intent;
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, LatLng latLng, double radius, boolean isDeepLink)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_LATLNG, latLng);
        intent.putExtra(INTENT_EXTRA_DATA_RADIUS, radius);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SearchType.LOCATION.name());
        intent.putExtra(INTENT_EXTRA_DATA_IS_DEEPLINK, isDeepLink);

        return intent;
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, Keyword keyword, SearchType searchType)
    {
        return newInstance(context, todayDateTime, stayBookingDay, null, keyword, searchType);
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, String text)
    {
        return newInstance(context, todayDateTime, stayBookingDay, null, new Keyword(0, text), SearchType.SEARCHES);
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, Location location, String callByScreen)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SearchType.LOCATION.name());
        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lockUI();

        mNetworkController = new PlaceSearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        if (mSearchType == SearchType.LOCATION)
        {
            mPlaceSearchResultLayout.setViewTypeVisibility(true);

            mNetworkController.requestAddress(mStaySearchCuration.getLocation());
        } else
        {
            mPlaceSearchResultLayout.setViewTypeVisibility(false);
        }

        // 기본적으로 시작시에 전체 카테고리를 넣는다.
        mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        mPlaceSearchResultLayout.processListLayout();
        mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
    }

    @Override
    protected PlaceSearchResultLayout getPlaceSearchResultLayout(Context context)
    {
        return new StaySearchResultLayout(context, mOnEventListener);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            StayBookingDay stayBookingDay = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (stayBookingDay == null)
            {
                return;
            }

            mStaySearchCuration.setStayBookingDay(stayBookingDay);

            ((StaySearchResultLayout) mPlaceSearchResultLayout).setCalendarText(stayBookingDay);

            // 날짜가 바뀌면 전체탭으로 이동하고 다시 재로딩.
            mStaySearchCuration.getCurationOption().clear();
            mStaySearchCuration.setCategory(Category.ALL);

            mPlaceSearchResultLayout.setOptionFilterSelected(false);
            mPlaceSearchResultLayout.clearCategoryTab();
            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
            mPlaceSearchResultLayout.processListLayout();
            mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
        }
    }

    @Override
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

            mStaySearchCuration.setCurationOption(changedStayCurationOption);
            mPlaceSearchResultLayout.setOptionFilterSelected(changedStayCurationOption.isDefaultFilter() == false);

            if (changedStayCurationOption.getSortType() == SortType.DISTANCE)
            {
                mStaySearchCuration.setLocation(changedStayCuration.getLocation());

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

    @Override
    protected void onLocationFailed()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStaySearchCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        StayCurationOption stayCurationOption = (StayCurationOption) mStaySearchCuration.getCurationOption();

        stayCurationOption.setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            mStaySearchCuration.getCurationOption().setSortType(SortType.DEFAULT);
            refreshCurrentFragment(true);
        } else
        {
            mStaySearchCuration.setLocation(location);

            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            if (mStaySearchCuration.getCurationOption().getSortType() == SortType.DISTANCE)
            {
                refreshCurrentFragment(true);
            }
        }
    }

    @Override
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

        Location location = null;
        Keyword keyword = null;
        double radius = DEFAULT_SEARCH_RADIUS;

        mStaySearchCuration = new StaySearchCuration();

        if (intent.hasExtra(INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            keyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            location = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);

            if (intent.hasExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN) == true)
            {
                mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN);
            }

            mStaySearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LATLNG) == true)
        {
            LatLng latLng = intent.getParcelableExtra(INTENT_EXTRA_DATA_LATLNG);

            if (intent.hasExtra(INTENT_EXTRA_DATA_RADIUS) == true)
            {
                radius = intent.getDoubleExtra(INTENT_EXTRA_DATA_RADIUS, DEFAULT_SEARCH_RADIUS);
            }

            mIsDeepLink = intent.getBooleanExtra(INTENT_EXTRA_DATA_IS_DEEPLINK, false);

            location = new Location((String) null);
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);

            // 고정 위치로 진입한 경우
            mIsFixedLocation = true;
            mStaySearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
        } else
        {
            finish();
            return;
        }

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);

        mStaySearchCuration.setKeyword(keyword);

        // 내주변 위치 검색으로 시작하는 경우에는 특정 반경과 거리순으로 시작해야한다.
        if (mSearchType == SearchType.LOCATION)
        {
            mStaySearchCuration.getCurationOption().setSortType(SortType.DISTANCE);
            mStaySearchCuration.setRadius(radius);
        }

        mStaySearchCuration.setLocation(location);
        mStaySearchCuration.setStayBookingDay(stayBookingDay);
    }

    @Override
    protected void initLayout()
    {
        if (mStaySearchCuration == null)
        {
            finish();
            return;
        }

        StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();

        if (stayBookingDay == null)
        {
            return;
        }

        try
        {
            if (mSearchType == SearchType.LOCATION)
            {
                mPlaceSearchResultLayout.setToolbarTitle("");
            } else
            {
                mPlaceSearchResultLayout.setToolbarTitle(mStaySearchCuration.getKeyword().name);
            }

            ((StaySearchResultLayout) mPlaceSearchResultLayout).setCalendarText(stayBookingDay);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected Keyword getKeyword()
    {
        return mStaySearchCuration.getKeyword();
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mStaySearchCuration;
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mOnStayListFragmentListener == null)
        {
            return;
        }

        mOnStayListFragmentListener.onStayClick(view, placeViewItem, listCount);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.HOTEL);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.HOTEL);
            params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);

            Province province = mStaySearchCuration.getProvince();
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

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(StaySearchResultActivity.this, screen, null, params);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchResultLayout.OnEventListener mOnEventListener = new PlaceSearchResultLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            StaySearchResultActivity.this.finish();
        }

        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            Category category = (Category) tab.getTag();
            mStaySearchCuration.setCategory(category);

            mPlaceSearchResultLayout.setCurrentItem(tab.getPosition());
            mPlaceSearchResultLayout.showBottomLayout(false);

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

            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();

            Intent intent = StayCalendarActivity.newInstance(StaySearchResultActivity.this, //
                mTodayDateTime, stayBookingDay, AnalyticsManager.ValueType.SEARCH_RESULT, true, true);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent( //
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

            StaySearchResultListFragment currentFragment = (StaySearchResultListFragment) mPlaceSearchResultLayout.getCurrentPlaceListFragment();

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

                        DailyToast.showToast(StaySearchResultActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;

                    AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label.HOTEL_MAP, null);
                    break;
                }

                case MAP:
                {
                    mViewType = ViewType.LIST;
                    break;
                }
            }

            mPlaceSearchResultLayout.setOptionViewTypeView(mViewType);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            for (PlaceListFragment placeListFragment : mPlaceSearchResultLayout.getPlaceListFragment())
            {
                boolean isCurrentFragment = (placeListFragment == currentFragment);
                placeListFragment.setVisibility(mViewType, isCurrentFragment);

                ((StaySearchResultListFragment) placeListFragment).setIsDeepLink(mIsDeepLink);
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

            Intent intent = StaySearchResultCurationActivity.newInstance(StaySearchResultActivity.this,//
                mViewType, mSearchType, mStaySearchCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void finish(int resultCode)
        {
            StaySearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL_, null);
            } else
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            StaySearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.SEARCH_AGAIN, null);
        }

        @Override
        public void onShowCallDialog()
        {
            showDailyCallDialog(null);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchResultNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceSearchResultNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAddress(String address)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (SearchType.LOCATION == mSearchType)
            {
                synchronized (StaySearchResultActivity.this)
                {
                    if (mReceiveDataFlag == 0)
                    {
                        mReceiveDataFlag = 1;
                    } else if (mReceiveDataFlag == 1)
                    {
                        ArrayList<PlaceListFragment> placeListFragmentList = mPlaceSearchResultLayout.getPlaceListFragment();
                        if (placeListFragmentList != null || placeListFragmentList.size() > 0)
                        {
                            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();
                            Map<String, String> params = new HashMap<>();

                            try
                            {
                                params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                                params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.HOTEL);
                                params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.HOTEL);
                                params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);

                                Province province = mStaySearchCuration.getProvince();
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
            }

            mAddress = address;
            mPlaceSearchResultLayout.setToolbarTitle(address);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StaySearchResultActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StaySearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StaySearchResultActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StaySearchResultActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StaySearchResultActivity.this.onErrorResponse(call, response);
        }
    };

    StaySearchResultListFragment.OnStaySearchResultListFragmentListener mOnStayListFragmentListener = new StaySearchResultListFragment.OnStaySearchResultListFragmentListener()
    {
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
                //                setExitSharedElementCallback(new SharedElementCallback()
                //                {
                //                    @Override
                //                    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                //                    {
                //                        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                //
                //                        for (View view : sharedElements)
                //                        {
                //                            if (view instanceof SimpleDraweeView)
                //                            {
                //                                view.setVisibility(View.VISIBLE);
                //                                break;
                //                            }
                //                        }
                //                    }
                //                });

                Intent intent = StayDetailActivity.newInstance(StaySearchResultActivity.this, //
                    mStaySearchCuration.getStayBookingDay(), stay, listCount, true);

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

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StaySearchResultActivity.this,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
            } else
            {
                Intent intent = StayDetailActivity.newInstance(StaySearchResultActivity.this, //
                    mStaySearchCuration.getStayBookingDay(), stay, listCount, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
        }

        @Override
        public void onStayLongClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mPlaceSearchResultLayout.setBlurVisibility(StaySearchResultActivity.this, true);

            Stay stay = placeViewItem.getItem();

            // 기존 데이터를 백업한다.
            mViewByLongPress = view;
            mPlaceViewItemByLongPress = placeViewItem;
            mListCountByLongPress = listCount;

            Intent intent = StayPreviewActivity.newInstance(StaySearchResultActivity.this, mStaySearchCuration.getStayBookingDay(), stay);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void onCategoryList(List<Category> categoryList)
        {
            if (categoryList != null && categoryList.size() > 0)
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
                mPlaceSearchResultLayout.processListLayout();
                ((StaySearchResultLayout) mPlaceSearchResultLayout).addCategoryTabLayout(categoryList, mOnStayListFragmentListener);
            } else
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                mPlaceSearchResultLayout.showEmptyLayout();
            }
        }

        @Override
        public void onActivityCreated(PlaceListFragment placeListFragment)
        {
            if (mPlaceSearchResultLayout == null || placeListFragment == null)
            {
                return;
            }

            PlaceListFragment currentPlaceListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
            if (currentPlaceListFragment == placeListFragment)
            {
                currentPlaceListFragment.setVisibility(mViewType, true);
                currentPlaceListFragment.setPlaceCuration(mStaySearchCuration);
                ((StaySearchResultListFragment) currentPlaceListFragment).setSearchType(mSearchType);
                currentPlaceListFragment.refreshList(true);
            } else
            {
                placeListFragment.setVisibility(mViewType, false);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            mPlaceSearchResultLayout.calculationMenuBarLayoutTranslationY(dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            switch (newState)
            {
                case RecyclerView.SCROLL_STATE_IDLE:
                {
                    mPlaceSearchResultLayout.animationMenuBarLayout();

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
                                    mPlaceSearchResultLayout.showBottomLayout(false);
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

            Intent intent = StaySearchResultCurationActivity.newInstance(StaySearchResultActivity.this, //
                mViewType, mSearchType, mStaySearchCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);
        }

        @Override
        public void onUpdateFilterEnabled(boolean isShowFilterEnabled)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            mPlaceSearchResultLayout.setOptionFilterEnabled(isShowFilterEnabled);
        }

        @Override
        public void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            mPlaceSearchResultLayout.setOptionViewTypeEnabled(isShowViewTypeEnabled);
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            if (isShow == true)
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                mPlaceSearchResultLayout.showEmptyLayout();

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT_EMPTY);
            } else
            {
                if (mPlaceSearchResultLayout.getCategoryTabCount() <= 2)
                {
                    mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                } else
                {
                    mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
                }

                mPlaceSearchResultLayout.showListLayout();
            }

            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();
            Map<String, String> params = new HashMap<>();
            try
            {
                params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.HOTEL);
                params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.HOTEL);
                params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);

                Province province = mStaySearchCuration.getProvince();
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

            Keyword keyword = mStaySearchCuration.getKeyword();

            if (mSearchType == SearchType.LOCATION)
            {
                synchronized (StaySearchResultActivity.this)
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
            } else if (mSearchType == SearchType.RECENT)
            {
                recordEventSearchResultByRecentKeyword(keyword, isShow, params);
            } else if (mSearchType == SearchType.AUTOCOMPLETE)
            {
                recordEventSearchResultByAutoSearch(keyword, mInputText, isShow, params);
            } else
            {
                recordEventSearchResultByKeyword(keyword, isShow, params);
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
