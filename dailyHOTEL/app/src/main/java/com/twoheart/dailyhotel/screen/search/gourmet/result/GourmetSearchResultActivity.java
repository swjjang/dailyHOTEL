package com.twoheart.dailyhotel.screen.search.gourmet.result;

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
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.screen.home.campaigntag.gourmet.GourmetCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.research.ResearchGourmetActivity;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchResultNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListAdapter;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListFragment;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class GourmetSearchResultActivity extends PlaceSearchResultActivity
{
    int mReceiveDataFlag; // 0 연동 전 , 1 데이터 리시브 상태, 2 로그 발송 상태

    String mInputText;
    String mAddress;

    GourmetSearchCuration mGourmetSearchCuration;

    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    private PlaceSearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay//
        , String inputText, GourmetSuggestV2 gourmetSuggest, SortType sortType, String callByScreen)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(gourmetSuggest));

        if (sortType != null)
        {
            intent.putExtra(INTENT_EXTRA_DATA_SORT_TYPE, sortType.name());
        }

        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay//
        , GourmetSuggestV2 gourmetSuggest, double radius, boolean isDeepLink)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(gourmetSuggest));
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

        GourmetSuggestV2 suggest = mGourmetSearchCuration.getSuggest();

        if (suggest != null && suggest.isLocationSuggestItem() == true)
        {
            try
            {
                if (mGourmetSearchCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetSearchCuration.getLocation() == null)
                {
                    unLockUI();

                    searchMyLocation();
                } else
                {
                    // 기본적으로 시작시에 전체 카테고리를 넣는다.
                    mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                    mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
                    mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnGourmetListFragmentListener);

                    mNetworkController.requestAddress(mGourmetSearchCuration.getLocation());

                    String displayName = suggest.getDisplayNameBySearchHome(GourmetSearchResultActivity.this);

                    if (DailyTextUtils.isTextEmpty(displayName) == true)
                    {
                        mNetworkController.requestAddress(mGourmetSearchCuration.getLocation());
                    } else
                    {
                        mOnNetworkControllerListener.onResponseAddress(displayName);
                    }
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        } else
        {
            // 기본적으로 시작시에 전체 카테고리를 넣는다.
            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
            mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
            mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnGourmetListFragmentListener);
        }
    }

    @Override
    protected PlaceSearchResultLayout getPlaceSearchResultLayout(Context context)
    {
        return new GourmetSearchResultLayout(this, mCallByScreen, mOnEventListener);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            GourmetBookingDay gourmetBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (gourmetBookingDay == null)
            {
                return;
            }

            mGourmetSearchCuration.setGourmetBookingDay(gourmetBookingDay);

            ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(gourmetBookingDay);

            // 날짜가 바뀌면 전체탭으로 이동하고 다시 재로딩.
            mGourmetSearchCuration.getCurationOption().clear();

            mPlaceSearchResultLayout.setOptionFilterSelected(false);
            mPlaceSearchResultLayout.clearCategoryTab();
            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
            mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

            if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true)
            {
                mGourmetSearchCuration.getCurationOption().setSortType(SortType.DISTANCE);
                mGourmetSearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);

                mPlaceSearchResultLayout.setSelectionSpinner(mGourmetSearchCuration.getRadius());
            }

            mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, mOnGourmetListFragmentListener);
        }
    }

    @Override
    protected void onCurationActivityResult(int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

            if ((placeCuration instanceof GourmetCuration) == false)
            {
                return;
            }

            GourmetCuration changedGourmetCuration = (GourmetCuration) placeCuration;
            GourmetCurationOption changedGourmetCurationOption = (GourmetCurationOption) changedGourmetCuration.getCurationOption();

            mGourmetSearchCuration.setCurationOption(changedGourmetCurationOption);
            mPlaceSearchResultLayout.setOptionFilterSelected(changedGourmetCurationOption.isDefaultFilter() == false);

            if (changedGourmetCurationOption.getSortType() == SortType.DISTANCE)
            {
                mGourmetSearchCuration.setLocation(changedGourmetCuration.getLocation());

                if (mGourmetSearchCuration.getLocation() != null)
                {
                    lockUI();

                    onLocationChanged(mGourmetSearchCuration.getLocation());
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
                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
                gourmetBookingDay.setVisitDay(data.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME));

                GourmetSuggestParcel gourmetSuggestParcel = data.getParcelableExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST);
                mInputText = data.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_KEYWORD);

                if (gourmetSuggestParcel == null)
                {
                    return;
                }

                GourmetSuggest gourmetSuggest = gourmetSuggestParcel.getSuggest();

                if (gourmetSuggest == null)
                {
                    return;
                }

                if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(gourmetSuggest.categoryKey) == true)
                {
                    mGourmetSearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
                } else
                {
                    mGourmetSearchCuration.getCurationOption().setDefaultSortType(SortType.DEFAULT);
                }

                mGourmetSearchCuration.setSuggest(gourmetSuggest);
                mGourmetSearchCuration.setGourmetBookingDay(gourmetBookingDay);
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
                mGourmetSearchCuration.getCurationOption().clear();

                if (mViewType == ViewType.MAP)
                {
                    mViewType = ViewType.LIST;
                    mPlaceSearchResultLayout.setOptionViewTypeView(mViewType);
                }

                mPlaceSearchResultLayout.setOptionFilterSelected(false);
                mPlaceSearchResultLayout.clearCategoryTab();
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

                if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true)
                {
                    mGourmetSearchCuration.getCurationOption().setSortType(SortType.DISTANCE);
                    mGourmetSearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);

                    if (mGourmetSearchCuration.getSuggest().latitude != 0.0d && mGourmetSearchCuration.getSuggest().longitude != 0.0d)
                    {
                        Location location = new Location("provider");
                        location.setLatitude(mGourmetSearchCuration.getSuggest().latitude);
                        location.setLongitude(mGourmetSearchCuration.getSuggest().longitude);

                        mGourmetSearchCuration.setLocation(location);

                        onLocationChanged(mGourmetSearchCuration.getLocation());
                    } else
                    {
                        searchMyLocation();
                    }
                } else
                {
                    mPlaceSearchResultLayout.setCategoryAllTabLayout(getSupportFragmentManager(), mOnGourmetListFragmentListener);
                }

                initLayout();
                break;
            }
        }
    }

    @Override
    protected void onLocationFailed()
    {
        if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true)
        {
            showEmptyLayout();
        } else
        {
            GourmetCurationOption stayCurationOption = (GourmetCurationOption) mGourmetSearchCuration.getCurationOption();

            stayCurationOption.setSortType(SortType.DEFAULT);
            mPlaceSearchResultLayout.setOptionFilterSelected(stayCurationOption.isDefaultFilter() == false);

            refreshCurrentFragment(true);
        }


        //        mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        //        mPlaceSearchResultLayout.setScreenVisible(ScreenType.EMPTY);

        //        if (mSearchType == SearchType.LOCATION)
        //        {
        //            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        //            mPlaceSearchResultLayout.setScreenVisible(ScreenType.EMPTY);
        //        } else
        //        {
        //            GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetSearchCuration.getCurationOption();
        //
        //            gourmetCurationOption.setSortType(SortType.DEFAULT);
        //            mPlaceSearchResultLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);
        //
        //            refreshCurrentFragment(true);
        //        }
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        if (mGourmetSearchCuration == null)
        {
            return;
        }

        switch (mGourmetSearchCuration.getSuggest().categoryKey)
        {
            case GourmetSuggest.CATEGORY_LOCATION:
                showEmptyLayout();
                break;

            default:
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetSearchCuration.getCurationOption();

                gourmetCurationOption.setSortType(SortType.DEFAULT);
                mPlaceSearchResultLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);

                refreshCurrentFragment(true);
                break;
        }

        //        mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        //        mPlaceSearchResultLayout.setScreenVisible(ScreenType.EMPTY);

        //        if (mSearchType == SearchType.LOCATION)
        //        {
        //            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
        //            mPlaceSearchResultLayout.setScreenVisible(ScreenType.EMPTY);
        //        } else
        //        {
        //            GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetSearchCuration.getCurationOption();
        //
        //            gourmetCurationOption.setSortType(SortType.DEFAULT);
        //            mPlaceSearchResultLayout.setOptionFilterSelected(gourmetCurationOption.isDefaultFilter() == false);
        //
        //            refreshCurrentFragment(true);
        //        }
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            showEmptyLayout();
        } else
        {
            if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true)
            {
                mNetworkController.requestAddress(location);
            }

            mGourmetSearchCuration.setLocation(location);

            if (mPlaceSearchResultLayout.getCategoryTabCount() > 0)
            {
                refreshCurrentFragment(true);
            } else
            {
                mPlaceSearchResultLayout.clearCategoryTab();

                // 기본적으로 시작시에 전체 카테고리를 넣는다.
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
                mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, mOnGourmetListFragmentListener);
            }


            //            mNetworkController.requestAddress(location);
            //            mGourmetSearchCuration.setLocation(location);
            //
            //            mPlaceSearchResultLayout.clearCategoryTab();
            //
            //            // 기본적으로 시작시에 전체 카테고리를 넣는다.
            //            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
            //            mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);
            //            mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, mOnGourmetListFragmentListener);

            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            //            if (mGourmetSearchCuration.getCurationOption().getSortType() == SortType.DISTANCE)
            //            {
            //                refreshCurrentFragment(true);
            //            }
        }
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mGourmetSearchCuration = new GourmetSearchCuration();

        GourmetBookingDay gourmetBookingDay;

        try
        {
            mTodayDateTime = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_TODAYDATETIME);
            gourmetBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        if (gourmetBookingDay == null)
        {
            finish();
            return;
        }

        mGourmetSearchCuration.setGourmetBookingDay(gourmetBookingDay);

        GourmetSuggestParcel gourmetSuggestParcel = intent.getParcelableExtra(INTENT_EXTRA_DATA_SUGGEST);

        if (gourmetSuggestParcel == null)
        {
            finish();
            return;
        }

        if (intent.hasExtra(INTENT_EXTRA_DATA_SORT_TYPE) == true)
        {
            try
            {
                SortType sortType = SortType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SORT_TYPE));
                mGourmetSearchCuration.getCurationOption().setSortType(sortType);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        mGourmetSearchCuration.setSuggest(gourmetSuggestParcel.getSuggest());
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);

        switch (mGourmetSearchCuration.getSuggest().categoryKey)
        {
            case GourmetSuggest.CATEGORY_GOURMET:
            case GourmetSuggest.CATEGORY_DIRECT:
                break;

            case GourmetSuggest.CATEGORY_LOCATION:
            {
                if (mGourmetSearchCuration.getSuggest().latitude != 0.0d && mGourmetSearchCuration.getSuggest().longitude != 0.0d)
                {
                    Location location = new Location("provider");
                    location.setLatitude(mGourmetSearchCuration.getSuggest().latitude);
                    location.setLongitude(mGourmetSearchCuration.getSuggest().longitude);

                    mGourmetSearchCuration.setLocation(location);
                }

                mGourmetSearchCuration.getCurationOption().setDefaultSortType(SortType.DISTANCE);
                mGourmetSearchCuration.getCurationOption().setSortType(SortType.DISTANCE);

                if (intent.hasExtra(INTENT_EXTRA_DATA_RADIUS) == true)
                {
                    double radius = intent.getDoubleExtra(INTENT_EXTRA_DATA_RADIUS, DEFAULT_SEARCH_RADIUS);

                    mGourmetSearchCuration.setRadius(radius);
                } else
                {
                    mGourmetSearchCuration.setRadius(DEFAULT_SEARCH_RADIUS);
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
        if (mGourmetSearchCuration == null)
        {
            finish();
            return;
        }

        GourmetBookingDay gourmetBookingDay = mGourmetSearchCuration.getGourmetBookingDay();

        if (gourmetBookingDay == null)
        {
            return;
        }

        try
        {
            switch (mGourmetSearchCuration.getSuggest().categoryKey)
            {
                case GourmetSuggest.CATEGORY_LOCATION:
                    mPlaceSearchResultLayout.setToolbarTitle(getString(R.string.label_search_nearby_empty_address));

                    mPlaceSearchResultLayout.setSpinnerVisible(true);
                    break;

                default:
                    mPlaceSearchResultLayout.setToolbarTitle(mGourmetSearchCuration.getSuggest().displayName);

                    mPlaceSearchResultLayout.setSpinnerVisible(false);
                    break;
            }

            mPlaceSearchResultLayout.setSelectionSpinner(mGourmetSearchCuration.getRadius());

            ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(gourmetBookingDay);
            mPlaceSearchResultLayout.setOptionFilterSelected(mGourmetSearchCuration.getCurationOption().isDefaultFilter() == false);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mGourmetSearchCuration;
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null || mOnGourmetListFragmentListener == null)
        {
            return;
        }

        mOnGourmetListFragmentListener.onGourmetClick(view, placeViewItem, listCount);
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
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(mGourmetSearchCuration.getSuggest()));
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT));

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
        mPlaceSearchResultLayout.setSpinnerVisible(GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true);

        if (mPlaceSearchResultLayout.hasCampaignTag() == false)
        {
            addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(ServiceType.GOURMET.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CampaignTag>>()
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
            GourmetBookingDay gourmetBookingDay = mGourmetSearchCuration.getGourmetBookingDay();
            Map<String, String> params = new HashMap<>();

            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);

            Province province = mGourmetSearchCuration.getProvince();
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

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(GourmetSearchResultActivity.this, screen + "_gourmet", null, params);
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
                , isEmpty ? "AroundSearchNotFound_LocationList_gourmet" : "AroundSearchClicked_LocationList_gourmet"//
                , mGourmetSearchCuration.getSuggest().displayName, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void requestAnalyticsByCanceled()
    {
        // do nothing!
    }

    @Override
    protected void changeViewType()
    {
        if (isFinishing() == true || isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        GourmetSearchResultListFragment currentFragment = (GourmetSearchResultListFragment) mPlaceSearchResultLayout.getCurrentPlaceListFragment();

        if (currentFragment == null)
        {
            unLockUI();
            return;
        }

        switch (mViewType)
        {
            case LIST:
            {
                // 맵리스트 진입시에 솔드아웃은 맵에서 보여주지 않기 때문에 맵으로 진입시에 아무것도 볼수 없다.
                if (currentFragment.hasSalesPlace() == false)
                {
                    unLockUI();

                    DailyToast.showToast(GourmetSearchResultActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                    return;
                }

                mViewType = ViewType.MAP;

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_MAP, null);
                break;
            }

            case MAP:
            {
                mViewType = ViewType.LIST;

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.CHANGE_VIEW, AnalyticsManager.Label._GOURMET_LIST_, null);
                break;
            }
        }

        // 고메는 리스트를 한번에 받기 때문에 계속 요청할 필요는 없다.
        mPlaceSearchResultLayout.setOptionViewTypeView(mViewType);

        for (PlaceListFragment placeListFragment : mPlaceSearchResultLayout.getPlaceListFragment())
        {
            boolean isCurrentFragment = (placeListFragment == currentFragment);
            placeListFragment.setVisibility(mViewType, Constants.EmptyStatus.NONE, isCurrentFragment);

            ((GourmetSearchResultListFragment) placeListFragment).setIsDeepLink(mIsDeepLink);
        }

        refreshCurrentFragment(false);

        unLockUI();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    GourmetSearchResultLayout.OnEventListener mOnEventListener = new GourmetSearchResultLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            GourmetSearchResultActivity.this.finish();
        }

        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onCategoryTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onCategoryTabReselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onDateClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetBookingDay gourmetBookingDay = mGourmetSearchCuration.getGourmetBookingDay();

            Intent intent = GourmetCalendarActivity.newInstance(GourmetSearchResultActivity.this, //
                mTodayDateTime, gourmetBookingDay, GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
                , AnalyticsManager.ValueType.SEARCH_RESULT, true, true);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED,//
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

            Intent intent = GourmetSearchResultCurationActivity.newInstance(GourmetSearchResultActivity.this,//
                mViewType, mGourmetSearchCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMETCURATION);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void finish(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL_, null);
            } else
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.SEARCH_AGAIN, null);
        }

        @Override
        public void onShowCallDialog()
        {
            showDailyCallDialog(null);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
        }

        @Override
        public void onItemSelectedSpinner(double radius)
        {
            if (mGourmetSearchCuration == null)
            {
                return;
            }

            mGourmetSearchCuration.setRadius(radius);

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

                String label = GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true //
                    ? mAddress : mGourmetSearchCuration.getSuggest().displayName;
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this) //
                    .recordEvent(AnalyticsManager.Category.NAVIGATION, action, label, null);

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this) //
                    .recordEvent(AnalyticsManager.Category.SEARCH_, "gourmet_around_result_range_change", mGourmetSearchCuration.getSuggest().displayName, null);
            } catch (Exception e)
            {
                if (Constants.DEBUG == true)
                {
                    ExLog.d(e.getMessage());
                }
            }

            if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == true//
                && mGourmetSearchCuration.getCurationOption().getSortType() == SortType.DISTANCE//
                && mGourmetSearchCuration.getLocation() == null)
            {
                searchMyLocation();
            } else
            {
                lockUI();

                mPlaceSearchResultLayout.clearCategoryTab();
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.NONE);

                mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, mOnGourmetListFragmentListener);
            }
        }

        @Override
        public void onPageScroll()
        {

        }

        @Override
        public void onPageSelected(int changedPosition, int prevPosition)
        {

        }

        @Override
        public void onResearchClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(ResearchGourmetActivity.newInstance(GourmetSearchResultActivity.this, mTodayDateTime.openDateTime, mTodayDateTime.closeDateTime//
                , mTodayDateTime.currentDateTime, mTodayDateTime.dailyDateTime//
                , mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                , mGourmetSearchCuration.getSuggest()), CODE_REQUEST_ACTIVITY_GOURMET_RESEARCH);

            switch (mGourmetSearchCuration.getSuggest().menuType)
            {
                case GourmetSuggest.MENU_TYPE_LOCATION:
                    AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                        , "gourmet_around_result_research", mGourmetSearchCuration.getSuggest().displayName, null);
                    break;

                default:
                    AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                        , "gourmet_research", null, null);
                    break;
            }
        }

        @Override
        public void onSearchStayClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            finish(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen", "gourmet_stay", null);
        }

        @Override
        public void onSearchStayOutboundClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            finish(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen", "gourmet_ob", null);
        }

        @Override
        public void onSearchPopularTag(CampaignTag campaignTag)
        {
            if (mGourmetSearchCuration == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivity(GourmetCampaignTagListActivity.newInstance(GourmetSearchResultActivity.this //
                , campaignTag.index, campaignTag.campaignTag//
                , mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)));

            finish();

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
                , "no_result_switch_screen_location_gourmet", Integer.toString(campaignTag.index), null);
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

            if (GourmetSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mGourmetSearchCuration.getSuggest().categoryKey) == false)
            {
                return;
            }

            synchronized (GourmetSearchResultActivity.this)
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
                    GourmetBookingDay gourmetBookingDay = mGourmetSearchCuration.getGourmetBookingDay();
                    Map<String, String> params = new HashMap<>();

                    try
                    {
                        params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);

                        Province province = mGourmetSearchCuration.getProvince();
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

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetSearchResultActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetSearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetSearchResultActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetSearchResultActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetSearchResultActivity.this.onErrorResponse(call, response);
        }
    };

    GourmetListFragment.OnGourmetListFragmentListener mOnGourmetListFragmentListener = new GourmetSearchResultListFragment.OnGourmetSearchResultListFragmentListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGourmetClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Gourmet gourmet = placeViewItem.getItem();

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
            analyticsParam.price = gourmet.price;
            analyticsParam.discountPrice = gourmet.discountPrice;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = gourmet.entryPosition;
            analyticsParam.totalListCount = listCount;
            analyticsParam.isDailyChoice = gourmet.isDailyChoice;
            analyticsParam.setAddressAreaName(gourmet.addressSummary);

            // <-- 추후에 정리되면 메소드로 수정

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

                if (view instanceof DailyGourmetCardView == true)
                {
                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(GourmetSearchResultActivity.this, ((DailyGourmetCardView) view).getOptionsCompat());

                    intent = GourmetDetailActivity.newInstance(GourmetSearchResultActivity.this //
                        , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                        , mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                        , gourmet.category, gourmet.isSoldOut, false, false, true//
                        , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                        , analyticsParam);
                } else
                {
                    View simpleDraweeView = view.findViewById(R.id.imageView);
                    View nameTextView = view.findViewById(R.id.nameTextView);
                    View gradientTopView = view.findViewById(R.id.gradientTopView);
                    View gradientBottomView = view.findViewById(R.id.gradientView);

                    intent = GourmetDetailActivity.newInstance(GourmetSearchResultActivity.this //
                        , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                        , mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                        , gourmet.category, gourmet.isSoldOut, false, false, true//
                        , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP//
                        , analyticsParam);

                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(GourmetSearchResultActivity.this,//
                        android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                        android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                        android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
                }

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(GourmetSearchResultActivity.this //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , mGourmetSearchCuration.getGourmetBookingDay().getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , gourmet.category, gourmet.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.GOURMET_ITEM_CLICK, Integer.toString(gourmet.index), null);

            // 할인 쿠폰이 보이는 경우
            if (DailyTextUtils.isTextEmpty(gourmet.couponDiscountText) == false)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.COUPON_GOURMET, Integer.toString(gourmet.index), null);
            }

            if (gourmet.reviewCount > 0)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.TRUE_REVIEW_GOURMET, Integer.toString(gourmet.index), null);
            }

            if (gourmet.discountRate > 0)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.DISCOUNT_GOURMET, Integer.toString(gourmet.index), null);
            }

            if (gourmet.availableTicketNumbers == 0 || gourmet.availableTicketNumbers < gourmet.minimumOrderQuantity || gourmet.expired == true)
            {
                switch (mGourmetSearchCuration.getSuggest().categoryKey)
                {
                    case GourmetSuggest.CATEGORY_LOCATION:
                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                            , AnalyticsManager.Action.NEARBY, Integer.toString(gourmet.index), null);
                        break;

                    case GourmetSuggest.CATEGORY_GOURMET:
                    case GourmetSuggest.CATEGORY_REGION:
                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                            , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(gourmet.index), null);
                        break;

                    case GourmetSuggest.CATEGORY_DIRECT:
                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                            , AnalyticsManager.Action.KEYWORD, Integer.toString(gourmet.index), null);
                        break;
                }

                //                switch (mSearchType)
                //                {
                //                    case AUTOCOMPLETE:
                //                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                //                            , AnalyticsManager.Action.AUTO_SEARCH, Integer.toString(gourmet.index), null);
                //                        break;
                //
                //                    case LOCATION:
                //                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                //                            , AnalyticsManager.Action.NEARBY, Integer.toString(gourmet.index), null);
                //                        break;
                //
                //                    case RECENTLY_KEYWORD:
                //                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                //                            , AnalyticsManager.Action.RECENT, Integer.toString(gourmet.index), null);
                //                        break;
                //
                //                    case SEARCHES:
                //                        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SOLDOUT_GOURMET_ITEM_CLICK//
                //                            , AnalyticsManager.Action.KEYWORD, Integer.toString(gourmet.index), null);
                //                        break;
                //                }
            }
        }

        @Override
        public void onGourmetLongClick(View view, PlaceViewItem placeViewItem, int listCount)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mPlaceSearchResultLayout.setBlurVisibility(GourmetSearchResultActivity.this, true);

            Gourmet gourmet = placeViewItem.getItem();

            // 기존 데이터를 백업한다.
            mViewByLongPress = view;
            mPlaceViewItemByLongPress = placeViewItem;
            mListCountByLongPress = listCount;

            Intent intent = GourmetPreviewActivity.newInstance(GourmetSearchResultActivity.this, mGourmetSearchCuration.getGourmetBookingDay(), gourmet);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void onGourmetCategoryFilter(int page, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
        {
            if (page <= 1 && mGourmetSearchCuration.getCurationOption().isDefaultFilter() == true)
            {
                ((GourmetCurationOption) mGourmetSearchCuration.getCurationOption()).setCategoryCoderMap(categoryCodeMap);
                ((GourmetCurationOption) mGourmetSearchCuration.getCurationOption()).setCategorySequenceMap(categorySequenceMap);
            }
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
                currentPlaceListFragment.setPlaceCuration(mGourmetSearchCuration);
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
                        GourmetListAdapter gourmetListAdapter = (GourmetListAdapter) recyclerView.getAdapter();

                        if (gourmetListAdapter != null)
                        {
                            int count = gourmetListAdapter.getItemCount();

                            if (count == 0)
                            {
                            } else
                            {
                                PlaceViewItem placeViewItem = gourmetListAdapter.getItem(gourmetListAdapter.getItemCount() - 1);

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

            Intent intent = GourmetSearchResultCurationActivity.newInstance(GourmetSearchResultActivity.this,//
                mViewType, mGourmetSearchCuration, mIsFixedLocation);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMETCURATION);
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
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                mPlaceSearchResultLayout.setScreenVisible(ScreenType.LIST);

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT);
            }

            GourmetBookingDay gourmetBookingDay = mGourmetSearchCuration.getGourmetBookingDay();
            Map<String, String> params = new HashMap<>();
            try
            {
                params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));

                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);

                Province province = mGourmetSearchCuration.getProvince();
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
                } else
                {
                    params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                }

                params.put(AnalyticsManager.KeyType.SEARCH_COUNT, Integer.toString(mSearchCount > mSearchMaxCount ? mSearchMaxCount : mSearchCount));
            } catch (Exception e)
            {

            }

            switch (mGourmetSearchCuration.getSuggest().categoryKey)
            {
                case GourmetSuggest.CATEGORY_LOCATION:
                    synchronized (GourmetSearchResultActivity.this)
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

                //                case GourmetSuggest.CATEGORY_GOURMET:
                //                case GourmetSuggest.CATEGORY_REGION:
                //                    recordEventSearchResultByAutoSearch(mGourmetSearchCuration.getSuggest().displayName, mInputText, isShow, params);
                //                    break;
                //
                //                case GourmetSuggest.CATEGORY_DIRECT:
                //                    recordEventSearchResultByRecentKeyword(mGourmetSearchCuration.getSuggest().displayName, isShow, params);
                //                    break;
                //
                //                default:
                //                    recordEventSearchResultByKeyword(mGourmetSearchCuration.getSuggest().displayName, isShow, params);
                //                    break;
            }

            switch (mGourmetSearchCuration.getSuggest().menuType)
            {
                case GourmetSuggest.MENU_TYPE_RECENTLY_SEARCH:
                    recordEventSearchResultByRecentKeyword(mGourmetSearchCuration.getSuggest().displayName, isShow, ServiceType.GOURMET, params);
                    break;

                case GourmetSuggest.MENU_TYPE_DIRECT:
                    recordEventSearchResultByKeyword(mGourmetSearchCuration.getSuggest().displayName, isShow, ServiceType.GOURMET, params);
                    break;

                case GourmetSuggest.MENU_TYPE_SUGGEST:
                    recordEventSearchResultByAutoSearch(mGourmetSearchCuration.getSuggest().displayName, mInputText, isShow, ServiceType.GOURMET, params);
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