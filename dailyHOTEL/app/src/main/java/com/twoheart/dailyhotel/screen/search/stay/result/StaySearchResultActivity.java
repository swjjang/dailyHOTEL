package com.twoheart.dailyhotel.screen.search.stay.result;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.research.ResearchStayActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayMapCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
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
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class StaySearchResultActivity extends PlaceSearchResultActivity
{
    int mReceiveDataFlag; // 0 연동 전 , 1 데이터 리시브 상태, 2 로그 발송 상태

    String mInputText;
    String mAddress;

    StaySearchCuration mStaySearchCuration;

    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    private PlaceSearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay//
        , String inputText, StaySuggest staySuggest, SortType sortType, String callByScreen)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(staySuggest));

        if (sortType != null)
        {
            intent.putExtra(INTENT_EXTRA_DATA_SORT_TYPE, sortType.name());
        }

        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay//
        , StaySuggest staySuggest, double radius, boolean isDeepLink)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(staySuggest));
        intent.putExtra(INTENT_EXTRA_DATA_RADIUS, radius);
        intent.putExtra(INTENT_EXTRA_DATA_IS_DEEPLINK, isDeepLink);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lockUI();

        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(this);
        mNetworkController = new PlaceSearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        switch (mStaySearchCuration.getSuggest().categoryKey)
        {
            case StaySuggest.CATEGORY_LOCATION:
                try
                {
                    if (mStaySearchCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStaySearchCuration.getLocation() == null)
                    {
                        unLockUI();

                        searchMyLocation();
                    } else
                    {
                        // 기본적으로 시작시에 전체 카테고리를 넣는다.
                        mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                        mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
                        mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);

                        if (DailyTextUtils.isTextEmpty(mStaySearchCuration.getSuggest().displayName) == true)
                        {
                            mNetworkController.requestAddress(mStaySearchCuration.getLocation());
                        } else
                        {
                            mOnNetworkControllerListener.onResponseAddress(mStaySearchCuration.getSuggest().displayName);
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
                break;

            default:
                // 기본적으로 시작시에 전체 카테고리를 넣는다.
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
                mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
                break;
        }
    }

    @Override
    protected PlaceSearchResultLayout getPlaceSearchResultLayout(Context context)
    {
        return new StaySearchResultLayout(context, mCallByScreen, mOnEventListener);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            try
            {
                String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                StayBookingDay stayBookingDay = new StayBookingDay();
                stayBookingDay.setCheckInDay(checkInDateTime);
                stayBookingDay.setCheckOutDay(checkOutDateTime);

                mStaySearchCuration.setStayBookingDay(stayBookingDay);
                ((StaySearchResultLayout) mPlaceSearchResultLayout).setCalendarText(stayBookingDay);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            // 날짜가 바뀌면 전체탭으로 이동하고 다시 재로딩.
            mStaySearchCuration.getCurationOption().clear();
            mStaySearchCuration.setCategory(Category.ALL);

            mPlaceSearchResultLayout.setOptionFilterSelected(false);
            mPlaceSearchResultLayout.clearCategoryTab();
            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
            mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

            if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
            {
                mStaySearchCuration.getCurationOption().setSortType(SortType.DISTANCE);
                mStaySearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);

                mPlaceSearchResultLayout.setSelectionSpinner(mStaySearchCuration.getRadius());
            }

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

                if (mStaySearchCuration.getLocation() != null)
                {
                    lockUI();

                    onLocationChanged(mStaySearchCuration.getLocation());
                } else
                {
                    searchMyLocation();
                }
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
    protected void onResearchActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            try
            {
                StayBookingDay stayBookingDay = new StayBookingDay();
                stayBookingDay.setCheckInDay(data.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME));
                stayBookingDay.setCheckOutDay(data.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME));

                StaySuggestParcel staySuggestParcel = data.getParcelableExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST);
                mInputText = data.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_KEYWORD);

                if (staySuggestParcel == null)
                {
                    return;
                }

                StaySuggest staySuggest = staySuggestParcel.getSuggest();

                if (staySuggest == null)
                {
                    return;
                }

                if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(staySuggest.categoryKey) == true)
                {
                    mStaySearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
                } else
                {
                    mStaySearchCuration.getCurationOption().setDefaultSortType(SortType.DEFAULT);
                }

                mStaySearchCuration.setSuggest(staySuggest);
                mStaySearchCuration.setStayBookingDay(stayBookingDay);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        switch (resultCode)
        {
            case Activity.RESULT_OK:
            {
                // 검색이 바뀌면 전체탭으로 이동하고 다시 재로딩.
                mStaySearchCuration.getCurationOption().clear();
                mStaySearchCuration.setCategory(Category.ALL);

                if (mViewType == ViewType.MAP)
                {
                    mViewType = ViewType.LIST;
                    mPlaceSearchResultLayout.setOptionViewTypeView(mViewType);
                }

                mPlaceSearchResultLayout.setOptionFilterSelected(false);
                mPlaceSearchResultLayout.clearCategoryTab();
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

                if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
                {
                    mStaySearchCuration.getCurationOption().setSortType(SortType.DISTANCE);
                    mStaySearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);

                    if (mStaySearchCuration.getSuggest().latitude != 0.0d && mStaySearchCuration.getSuggest().longitude != 0.0d)
                    {
                        Location location = new Location("provider");
                        location.setLatitude(mStaySearchCuration.getSuggest().latitude);
                        location.setLongitude(mStaySearchCuration.getSuggest().longitude);

                        mStaySearchCuration.setLocation(location);

                        onLocationChanged(mStaySearchCuration.getLocation());
                    } else
                    {
                        searchMyLocation();
                    }
                } else
                {
                    mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
                }

                initLayout();
                break;
            }
        }
    }

    @Override
    protected void onLocationFailed()
    {
        if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
        {
            showEmptyLayout();
        } else
        {
            StayCurationOption stayCurationOption = (StayCurationOption) mStaySearchCuration.getCurationOption();

            stayCurationOption.setSortType(SortType.DEFAULT);
            mPlaceSearchResultLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        if (mStaySearchCuration == null)
        {
            return;
        }

        switch (mStaySearchCuration.getSuggest().categoryKey)
        {
            case StaySuggest.CATEGORY_LOCATION:
                showEmptyLayout();
                break;

            default:
                StayCurationOption stayCurationOption = (StayCurationOption) mStaySearchCuration.getCurationOption();

                stayCurationOption.setSortType(SortType.DEFAULT);
                mPlaceSearchResultLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

                refreshCurrentFragment(true);
                break;
        }
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            showEmptyLayout();
        } else
        {
            if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
            {
                mNetworkController.requestAddress(location);
            }

            mStaySearchCuration.setLocation(location);

            if (mPlaceSearchResultLayout.getCategoryTabCount() > 0)
            {
                refreshCurrentFragment(true);
            } else
            {
                mPlaceSearchResultLayout.clearCategoryTab();

                // 기본적으로 시작시에 전체 카테고리를 넣는다.
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
                mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
            }


            //            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            //            if (mStaySearchCuration.getCurationOption().getSortType() == SortType.DISTANCE)
            //            {
            //                refreshCurrentFragment(true);
            //            }
        }
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mStaySearchCuration = new StaySearchCuration();

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

        mStaySearchCuration.setStayBookingDay(stayBookingDay);

        StaySuggestParcel staySuggestParcel = intent.getParcelableExtra(INTENT_EXTRA_DATA_SUGGEST);

        if (staySuggestParcel == null)
        {
            finish();
            return;
        }

        if (intent.hasExtra(INTENT_EXTRA_DATA_SORT_TYPE) == true)
        {
            try
            {
                SortType sortType = SortType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SORT_TYPE));
                mStaySearchCuration.getCurationOption().setSortType(sortType);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        mStaySearchCuration.setSuggest(staySuggestParcel.getSuggest());
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);

        switch (mStaySearchCuration.getSuggest().categoryKey)
        {
            case StaySuggest.CATEGORY_STAY:
            case StaySuggest.CATEGORY_DIRECT:
                break;

            case StaySuggest.CATEGORY_LOCATION:
            {
                if (mStaySearchCuration.getSuggest().latitude != 0.0d && mStaySearchCuration.getSuggest().longitude != 0.0d)
                {
                    Location location = new Location("provider");
                    location.setLatitude(mStaySearchCuration.getSuggest().latitude);
                    location.setLongitude(mStaySearchCuration.getSuggest().longitude);

                    mStaySearchCuration.setLocation(location);
                }

                mStaySearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
                mStaySearchCuration.getCurationOption().setSortType(SortType.DISTANCE);

                if (intent.hasExtra(INTENT_EXTRA_DATA_RADIUS) == true)
                {
                    double radius = intent.getDoubleExtra(INTENT_EXTRA_DATA_RADIUS, DEFAULT_SEARCH_RADIUS);

                    mStaySearchCuration.setRadius(radius);
                } else
                {
                    mStaySearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);
                }
                break;
            }
        }

        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN);

        if (intent.hasExtra(INTENT_EXTRA_DATA_IS_DEEPLINK) == true)
        {
            mIsDeepLink = intent.getBooleanExtra(INTENT_EXTRA_DATA_IS_DEEPLINK, false);
        }
    }

    @Override
    protected void initLayout()
    {
        if (mStaySearchCuration == null)
        {
            finish();
            return;
        }

        if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true)
        {
            mPlaceSearchResultLayout.setToolbarTitleImageResourc(R.drawable.search_ic_01_date);
        } else
        {
            mPlaceSearchResultLayout.setToolbarTitleImageResourc(R.drawable.search_ic_01_search);
        }

        StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();

        if (stayBookingDay == null)
        {
            return;
        }

        try
        {
            switch (mStaySearchCuration.getSuggest().categoryKey)
            {
                case StaySuggest.CATEGORY_LOCATION:
                    mPlaceSearchResultLayout.setToolbarTitle(getString(R.string.label_search_nearby_empty_address));

                    mPlaceSearchResultLayout.setSpinnerVisible(true);
                    break;

                default:
                    mPlaceSearchResultLayout.setToolbarTitle(mStaySearchCuration.getSuggest().displayName);

                    mPlaceSearchResultLayout.setSpinnerVisible(false);
                    break;
            }

            mPlaceSearchResultLayout.setSelectionSpinner(mStaySearchCuration.getRadius());

            ((StaySearchResultLayout) mPlaceSearchResultLayout).setCalendarText(stayBookingDay);
            mPlaceSearchResultLayout.setOptionFilterSelected(mStaySearchCuration.getCurationOption().isDefaultFilter() == false);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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

    @Override
    protected void finish(int resultCode)
    {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(mStaySearchCuration.getSuggest()));
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, mStaySearchCuration.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, mStaySearchCuration.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));

        setResult(resultCode, intent);

        if (resultCode == RESULT_CANCELED || resultCode == CODE_RESULT_ACTIVITY_GO_SEARCH)
        {
            requestAnalyticsByCanceled();
        }

        finish();
    }

    void showEmptyLayout()
    {
        if (mPlaceSearchResultLayout == null)
        {
            return;
        }

        mReceiveDataFlag = 2;

        unLockUI();

        mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        mPlaceSearchResultLayout.setScreenVisible(ScreenType.EMPTY);
        mPlaceSearchResultLayout.setSpinnerVisible(StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true);

        if (mPlaceSearchResultLayout.hasCampaignTag() == false)
        {
            addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(ServiceType.HOTEL.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CampaignTag>>()
            {
                @Override
                public void accept(ArrayList<CampaignTag> campaignTagList) throws Exception
                {
                    if (campaignTagList == null || campaignTagList.size() == 0)
                    {
                        mPlaceSearchResultLayout.setCampaignTagVisible(false);
                        return;
                    }

                    mPlaceSearchResultLayout.setCampaignTagVisible(true);
                    mPlaceSearchResultLayout.setCampaignTagList(campaignTagList);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    ExLog.e(throwable.toString());

                    mPlaceSearchResultLayout.setCampaignTagVisible(false);
                }
            }));
        }
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
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

            if (DailyHotel.isLogin() == false)
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
                switch (DailyUserPreference.getInstance(this).getType())
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            }

            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);
            params.put(AnalyticsManager.KeyType.FILTER, mStaySearchCuration.getCurationOption().toAdjustString());
            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(this).isBenefitAlarm() ? "on" : "off");

            params.put(AnalyticsManager.KeyType.VIEW_TYPE //
                , AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == true //
                    ? AnalyticsManager.ValueType.MAP : AnalyticsManager.ValueType.LIST);

            StayRegion region = mStaySearchCuration.getRegion();

            if (region != null)
            {
                params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.PROVINCE, region.getAreaGroupName());

                com.daily.dailyhotel.entity.Area area = region.getArea();
                params.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
            }

            if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this) //
                    .recordScreen(StaySearchResultActivity.this, AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY, null, params);
            } else
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(StaySearchResultActivity.this, screen + "_stay", null, params);
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
            params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AROUND);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , isEmpty ? "AroundSearchNotFound_LocationList_stay" : "AroundSearchClicked_LocationList_stay"//
                , mStaySearchCuration.getSuggest().displayName, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void requestAnalyticsByCanceled()
    {
        if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true && StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
        {
            AnalyticsManager.getInstance(this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_BACK_BUTTON_CLICK //
                , AnalyticsManager.Label.NEAR_BY, null);
        }
    }

    @Override
    protected void changeViewType()
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

                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._HOTEL_MAP, null);
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
            placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NONE, isCurrentFragment);

            ((StaySearchResultListFragment) placeListFragment).setIsDeepLink(mIsDeepLink);
        }

        refreshCurrentFragment(false);

        unLockUI();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    StaySearchResultLayout.OnEventListener mOnEventListener = new StaySearchResultLayout.OnEventListener()
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

            final int DAYS_OF_MAX_COUNT = 60;

            try
            {
                Calendar calendar = DailyCalendar.getInstance();
                calendar.setTime(DailyCalendar.convertDate(mTodayDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT));

                String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

                calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);

                String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

                StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();

                Intent intent = StayCalendarActivity.newInstance(StaySearchResultActivity.this//
                    , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                    , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                    , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                    , AnalyticsManager.ValueType.SEARCH_RESULT, true//
                    , 0, true);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void onViewTypeClick()
        {
            mPlaceSearchResultLayout.showBottomLayout();

            changeViewType();
        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = StaySearchResultCurationActivity.newInstance(StaySearchResultActivity.this,//
                mViewType, mStaySearchCuration, mIsFixedLocation);
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

        @Override
        public void onItemSelectedSpinner(double radius)
        {
            if (mStaySearchCuration == null)
            {
                return;
            }

            mStaySearchCuration.setRadius(radius);

            if (mReceiveDataFlag < 2)
            {
                // 초기 로딩이 끝나지 않았음으로 리턴
                return;
            }

            try
            {
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

                String label = StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true //
                    ? mAddress : mStaySearchCuration.getSuggest().displayName;
                AnalyticsManager.getInstance(StaySearchResultActivity.this) //
                    .recordEvent(AnalyticsManager.Category.NAVIGATION, action, label, null);

                AnalyticsManager.getInstance(StaySearchResultActivity.this) //
                    .recordEvent(AnalyticsManager.Category.SEARCH_, "stay_around_result_range_change", mStaySearchCuration.getSuggest().displayName, null);
            } catch (Exception e)
            {
                if (Constants.DEBUG == true)
                {
                    ExLog.d(e.getMessage());
                }
            }

            if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true//
                && mStaySearchCuration.getCurationOption().getSortType() == SortType.DISTANCE//
                && mStaySearchCuration.getLocation() == null)
            {
                searchMyLocation();
            } else
            {
                lockUI();

                mStaySearchCuration.setCategory(Category.ALL);

                mPlaceSearchResultLayout.clearCategoryTab();
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

                // 기본적으로 시작시에 전체 카테고리를 넣는다.
                mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnStayListFragmentListener);
            }
        }

        @Override
        public void onPageScroll()
        {
            // 목록을 맵으로 보고 있는 경우 페이지 스크롤 중에 하단 옵션 레이아웃의 위치가 초기화 되는 이슈 수정
            if (mViewType == ViewType.MAP)
            {
                try
                {
                    PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();

                    if (placeListFragment.getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                    {
                        placeListFragment.getPlaceListLayout().getListMapFragment().clickMap();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

                    changeViewType();
                }
            }
        }

        @Override
        public void onPageSelected(int changedPosition, int prevPosition)
        {
            // 목록을 맵으로 보고 있는 경우 페이지 스크롤 중에 하단 옵션 레이아웃의 위치가 초기화 되는 이슈 수정
            if (mViewType == ViewType.MAP)
            {
                try
                {
                    PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getPlaceListFragment().get(prevPosition);

                    if (placeListFragment.getPlaceListLayout().getListMapFragment() != null//
                        && placeListFragment.getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                    {
                        placeListFragment.getPlaceListLayout().getListMapFragment().clickMap();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

                    changeViewType();
                }
            }
        }

        @Override
        public void onResearchClick()
        {
            // 홈에서 온 경우에는 달력을 띄운다.
            if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true)
            {
                onDateClick();
            } else
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                startActivityForResult(ResearchStayActivity.newInstance(StaySearchResultActivity.this, mTodayDateTime.openDateTime, mTodayDateTime.closeDateTime//
                    , mTodayDateTime.currentDateTime, mTodayDateTime.dailyDateTime//
                    , mStaySearchCuration.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                    , mStaySearchCuration.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                    , mStaySearchCuration.getSuggest()), CODE_REQUEST_ACTIVITY_STAY_RESEARCH);


                switch (mStaySearchCuration.getSuggest().menuType)
                {
                    case StaySuggest.MENU_TYPE_LOCATION:
                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                            , "stay_around_result_research", mStaySearchCuration.getSuggest().displayName, null);
                        break;

                    default:
                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                            , "stay_research", null, null);
                        break;
                }
            }
        }

        @Override
        public void onSearchStayOutboundClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            finish(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen", "stay_ob", null);
        }

        @Override
        public void onSearchGourmetClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            finish(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen", "stay_gourmet", null);
        }

        @Override
        public void onSearchPopularTag(CampaignTag campaignTag)
        {
            if (mStaySearchCuration == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivity(StayCampaignTagListActivity.newInstance(StaySearchResultActivity.this //
                , campaignTag.index, campaignTag.campaignTag//
                , mStaySearchCuration.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT) //
                , mStaySearchCuration.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)));

            finish();

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen_location_stay", Integer.toString(campaignTag.index), null);
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

            if (DailyTextUtils.isTextEmpty(address) == true)
            {
                address = getString(R.string.label_search_nearby_empty_address);
            }

            mAddress = address;
            mPlaceSearchResultLayout.setToolbarTitle(address);

            if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == false)
            {
                return;
            }

            synchronized (StaySearchResultActivity.this)
            {
                if (mReceiveDataFlag >= 2)
                {
                    return;
                }

                if (mReceiveDataFlag == 0)
                {
                    mReceiveDataFlag = 1;
                    return;
                }

                ArrayList<PlaceListFragment> placeListFragmentList = mPlaceSearchResultLayout.getPlaceListFragment();
                if (placeListFragmentList != null && placeListFragmentList.size() > 0)
                {
                    StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();
                    Map<String, String> params = new HashMap<>();

                    try
                    {
                        params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                        params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                        params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
                        params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);

                        StayRegion region = mStaySearchCuration.getRegion();

                        if (region != null)
                        {
                            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                            params.put(AnalyticsManager.KeyType.PROVINCE, region.getAreaGroupName());

                            com.daily.dailyhotel.entity.Area area = region.getArea();
                            params.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
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
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onStayClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Stay stay = placeViewItem.getItem();

            StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
            analyticsParam.setAddressAreaName(stay.addressSummary);
            analyticsParam.discountPrice = stay.discountPrice;
            analyticsParam.price = stay.price;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setRegion(null);
            analyticsParam.entryPosition = stay.entryPosition;
            analyticsParam.totalListCount = listCount;
            analyticsParam.isDailyChoice = stay.isDailyChoice;
            analyticsParam.gradeName = stay.getGrade().getName(StaySearchResultActivity.this);

            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();

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

                ActivityOptionsCompat optionsCompat;
                Intent intent;

                if (view instanceof DailyStayCardView == true)
                {
                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StaySearchResultActivity.this, ((DailyStayCardView) view).getOptionsCompat());

                    intent = StayDetailActivity.newInstance(StaySearchResultActivity.this //
                        , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                        , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                        , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                        , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);
                } else if (view instanceof DailyStayMapCardView == true)
                {
                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(StaySearchResultActivity.this, ((DailyStayMapCardView) view).getOptionsCompat());

                    intent = StayDetailActivity.newInstance(StaySearchResultActivity.this //
                        , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                        , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                        , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                        , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP, analyticsParam);
                } else
                {
                    unLockUI();
                    return;
                }

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
            } else
            {
                Intent intent = StayDetailActivity.newInstance(StaySearchResultActivity.this //
                    , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                    , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                    , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                    , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            // 할인 쿠폰이 보이는 경우
            if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
            }

            if (stay.reviewCount > 0)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
            }

            if (stay.discountRate > 0)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.DISCOUNT_STAY, Integer.toString(stay.index), null);
            }

            if (stay.truevr == true)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
            }

            if (DailyRemoteConfigPreference.getInstance(StaySearchResultActivity.this).isKeyRemoteConfigRewardStickerEnabled()//
                && stay.provideRewardSticker == true)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.REWARD//
                    , AnalyticsManager.Action.THUMBNAIL_CLICK, Integer.toString(stay.index), null);
            }

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", stay.entryPosition, stay.index), null);

            if (stay.availableRooms == 0 && DailyTextUtils.isTextEmpty(mStaySearchCuration.getSuggest().categoryKey) == false)
            {
                switch (mStaySearchCuration.getSuggest().categoryKey)
                {
                    case StaySuggest.CATEGORY_LOCATION:
                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                            , AnalyticsManager.Action.NEARBY, Integer.toString(stay.index), null);
                        break;

                    case StaySuggest.CATEGORY_STAY:
                    case StaySuggest.CATEGORY_REGION:
                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                            , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(stay.index), null);
                        break;

                    case StaySuggest.CATEGORY_DIRECT:
                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                            , AnalyticsManager.Action.KEYWORD, Integer.toString(stay.index), null);
                        break;
                }

                //                switch (mSearchType)
                //                {
                //                    case AUTOCOMPLETE:
                //                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                //                            , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(stay.index), null);
                //                        break;
                //
                //                    case LOCATION:
                //                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                //                            , AnalyticsManager.Action.NEARBY, Integer.toString(stay.index), null);
                //                        break;
                //
                //                    case RECENTLY_KEYWORD:
                //                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                //                            , AnalyticsManager.Action.RECENT, Integer.toString(stay.index), null);
                //                        break;
                //
                //                    case SEARCHES:
                //                        AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_STAY_ITEM_CLICK//
                //                            , AnalyticsManager.Action.KEYWORD, Integer.toString(stay.index), null);
                //                        break;
                //                }
            }
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
        public void onRegionClick()
        {

        }

        @Override
        public void onCalendarClick()
        {
            mOnEventListener.onDateClick();
        }

        @Override
        public void onCategoryList(List<Category> categoryList)
        {
            if (categoryList != null && categoryList.size() > 0)
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.VISIBLE);

                ((StaySearchResultLayout) mPlaceSearchResultLayout).addCategoryTabLayout(categoryList, mOnStayListFragmentListener);
            } else if (StaySuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStaySearchCuration.getSuggest().categoryKey) == true)
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
            } else
            {
                showEmptyLayout();
            }
        }

        @Override
        public void onResearchClick()
        {
            mOnEventListener.onResearchClick();
        }

        @Override
        public void onRadiusClick()
        {
            mPlaceSearchResultLayout.showSpinner();
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
                currentPlaceListFragment.setVisibility(mViewType, Constants.EmptyStatus.NOT_EMPTY, true);
                currentPlaceListFragment.setPlaceCuration(mStaySearchCuration);
                currentPlaceListFragment.refreshList(true);
            } else
            {
                placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NOT_EMPTY, false);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            switch (newState)
            {
                case RecyclerView.SCROLL_STATE_IDLE:
                {
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
                                    mPlaceSearchResultLayout.showBottomLayout();
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
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            mPlaceSearchResultLayout.showBottomLayout();
        }

        @Override
        public void onBottomOptionVisible(boolean visible)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            mPlaceSearchResultLayout.setBottomOptionVisible(visible);
        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = StaySearchResultCurationActivity.newInstance(StaySearchResultActivity.this, //
                mViewType, mStaySearchCuration, mIsFixedLocation);
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
                showEmptyLayout();

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

                mPlaceSearchResultLayout.setScreenVisible(ScreenType.LIST);

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT);
            }

            StayBookingDay stayBookingDay = mStaySearchCuration.getStayBookingDay();
            Map<String, String> params = new HashMap<>();
            try
            {
                params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));

                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.STAY);
                params.put(AnalyticsManager.KeyType.CATEGORY, mStaySearchCuration.getCategory().code);

                StayRegion region = mStaySearchCuration.getRegion();

                if (region != null)
                {
                    params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.PROVINCE, region.getAreaGroupName());

                    com.daily.dailyhotel.entity.Area area = region.getArea();
                    params.put(AnalyticsManager.KeyType.DISTRICT, area == null || area.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                }

                params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(mSearchCount > mSearchMaxCount ? mSearchMaxCount : mSearchCount));
            } catch (Exception e)
            {

            }

            switch (mStaySearchCuration.getSuggest().categoryKey)
            {
                case StaySuggest.CATEGORY_LOCATION:
                    synchronized (StaySearchResultActivity.this)
                    {
                        if (mReceiveDataFlag == 0)
                        {
                            mReceiveDataFlag = 1;
                        } else if (mReceiveDataFlag == 1)
                        {
                            recordEventSearchResultByLocation(mAddress, isShow, params);
                            mReceiveDataFlag = 2;
                        }
                    }
                    break;
                //
                //                case StaySuggest.CATEGORY_STAY:
                //                case StaySuggest.CATEGORY_REGION:
                //                    recordEventSearchResultByAutoSearch(mStaySearchCuration.getSuggest().displayName, mInputText, isShow, params);
                //                    break;
                //
                //                case StaySuggest.CATEGORY_DIRECT:
                //
                //                    break;
                //
                //                default:
                //                    recordEventSearchResultByKeyword(mStaySearchCuration.getSuggest().displayName, isShow, params);
                //                    break;
            }

            switch (mStaySearchCuration.getSuggest().menuType)
            {
                case StaySuggest.MENU_TYPE_RECENTLY_SEARCH:
                    recordEventSearchResultByRecentKeyword(mStaySearchCuration.getSuggest().displayName, isShow, ServiceType.HOTEL, params);
                    break;

                case StaySuggest.MENU_TYPE_DIRECT:
                    recordEventSearchResultByKeyword(mStaySearchCuration.getSuggest().displayName, isShow, ServiceType.HOTEL, params);
                    break;

                case StaySuggest.MENU_TYPE_SUGGEST:
                    recordEventSearchResultByAutoSearch(mStaySearchCuration.getSuggest().displayName, mInputText, isShow, ServiceType.HOTEL, params);
                    break;
            }
        }

        @Override
        public void onRecordAnalytics(ViewType viewType)
        {
        }

        @Override
        public void onSearchCountUpdate(int searchCount, int searchMaxCount)
        {
            mSearchCount = searchCount;
            mSearchMaxCount = searchMaxCount;
        }
    };
}
