package com.twoheart.dailyhotel.screen.search.stay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.search.StayOutboundSearchActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetSearchCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StaySearchCalendarActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class StaySearchFragment extends PlaceSearchFragment
{
    StayBookingDay mStayBookingDay;
    Disposable mAnalyticsDisposable;

    private List<Stay> mRecentlyStayList;

    @Override
    protected void initContents()
    {
        super.initContents();

        if (mStayBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mStayBookingDay);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mStayBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mStayBookingDay);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_CALENDAR:
            {
                setDateChanged(true);

                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY) == true)
                    {
                        StayBookingDay stayBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                        if (stayBookingDay == null)
                        {
                            return;
                        }

                        setStayBookingDay(stayBookingDay);
                        setDateText(stayBookingDay);

                        mPlaceSearchLayout.requestUpdateAutoCompleteLayout();

                        if (data.hasExtra(GourmetSearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_TYPE) == true)
                        {
                            SearchType searchType;
                            try
                            {
                                searchType = SearchType.valueOf(data.getStringExtra(StaySearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_TYPE));
                            } catch (Exception e)
                            {
                                searchType = null;
                            }

                            String inputText = data.getStringExtra(StaySearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_INPUT_TEXT);
                            Keyword keyword = data.getParcelableExtra(StaySearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_KEYWORD);

                            if (searchType == null)
                            {
                                // do nothing!
                            } else if (searchType == SearchType.SEARCHES)
                            {
                                startSearchResultActivity();
                            } else if (searchType == SearchType.LOCATION)
                            {
                                mOnEventListener.onSearchMyLocation();
                            } else if (searchType == SearchType.AUTOCOMPLETE)
                            {
                                mOnEventListener.onSearch(inputText, keyword);
                            } else if (searchType == SearchType.RECENT)
                            {
                                mOnEventListener.onSearch(inputText, keyword);
                            }
                        }
                    }
                }

                mShowSearchKeyboard = true;
                break;
            }
        }
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_stay_search;
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return new StaySearchLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceSearchNetworkController getPlaceSearchNetworkController(Context context)
    {
        return new StaySearchNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected String getRecentSearches()
    {
        return DailyPreference.getInstance(mBaseActivity).getHotelRecentSearches();
    }

    @Override
    protected void writeRecentSearches(String text)
    {
        DailyPreference.getInstance(mBaseActivity).setHotelRecentSearches(text);
    }

    @Override
    protected void onSearch(Location location)
    {
        if (mIsScrolling == true)
        {
            unLockUI();

            return;
        }

        if (mStayBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        lockUI();

        Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, location, AnalyticsManager.Screen.SEARCH_MAIN);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    public void startSearchResultActivity()
    {
        if (mIsScrolling == true)
        {
            return;
        }

        String text = mPlaceSearchLayout.getSearchKeyWord();

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            return;
        }

        if (mStayBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    public void setStayBookingDay(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        mStayBookingDay = stayBookingDay;
    }

    private void setDateText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null || mPlaceSearchLayout == null)
        {
            return;
        }

        try
        {
            mPlaceSearchLayout.setDataText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)")//
                , stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"), stayBookingDay.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void setDateChanged(TodayDateTime todayDateTime, StayBookingDay stayBookingDay)
    {
        if (isDateChanged() == true || todayDateTime == null || stayBookingDay == null)
        {
            return;
        }

        try
        {
            int night = stayBookingDay.getNights();
            if (night > 1)
            {
                setDateChanged(true);
                return;
            }

            String checkInDateString = stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT);

            long checkInTime = DailyCalendar.getInstance(checkInDateString, true).getTimeInMillis();
            long toDayTime = DailyCalendar.getInstance(todayDateTime.dailyDateTime, true).getTimeInMillis();

            int dayOfDays = (int) ((checkInTime - toDayTime) / DailyCalendar.DAY_MILLISECOND);
            // 체크인 날짜가 익일이상이면 달력 체크 안함 , 즉 현재 날짜 이전이면 달력 표시 해야 함
            setDateChanged(dayOfDays > 0);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void startCalendar(boolean isAnimation, Constants.SearchType searchType, String inputText, Keyword keyword)
    {
        if (mIsScrolling == true || isAdded() == false)
        {
            return;
        }

        if (mStayBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        if (isAnimation == true)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
        }

        Intent intent = StaySearchCalendarActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay //
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, isAnimation, searchType, inputText, keyword);

        if (intent == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    public ServiceType getServiceType()
    {
        return ServiceType.HOTEL;
    }

    @Override
    public void setTodayDateTime(TodayDateTime todayDateTime)
    {
        setDateChanged(todayDateTime, mStayBookingDay);

        addCompositeDisposable(Observable.zip(mRecentlyRemoteImpl.getStayInboundRecentlyList(mStayBookingDay, false) //
            , mCampaignTagRemoteImpl.getCampaignTagList(getServiceType().name()) //
            , new BiFunction<List<Stay>, ArrayList<CampaignTag>, List<Keyword>>()
            {
                @Override
                public List<Keyword> apply(@NonNull List<Stay> stayList, @NonNull ArrayList<CampaignTag> tagList) throws Exception
                {
                    mRecentlyStayList = stayList;
                    mCampaignTagList = tagList;

                    return mDailyRecentSearches.getList();
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Keyword>>()
        {
            @Override
            public void accept(@NonNull List<Keyword> keywordList) throws Exception
            {
                unLockUI();
                mPlaceSearchLayout.setRecyclerViewData(mRecentlyStayList, mCampaignTagList, keywordList);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StaySearchLayout.OnEventListener mOnEventListener = new StaySearchLayout.OnEventListener()
    {
        @Override
        public void onStayOutboundClick()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            startActivity(StayOutboundSearchActivity.newInstance(getContext()));

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.SEARCH, AnalyticsManager.Action.SEARCH_SCREEN,//
                AnalyticsManager.Label.OUTBOUND_CLICK, null);
        }

        @Override
        public void onResetKeyword()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            mPlaceSearchLayout.resetSearchKeyword();
        }

        @Override
        public void onSearchMyLocation()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                onCalendarClick(true, SearchType.LOCATION, null, null);
                return;
            }

            checkLocationProvider();
        }

        @Override
        public void onDeleteRecentSearches()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            mDailyRecentSearches.clear();
            DailyPreference.getInstance(mBaseActivity).setHotelRecentSearches("");

            //            mPlaceSearchLayout.updateRecentSearchesLayout(null);
            mPlaceSearchLayout.setKeywordListData(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.SEARCH_//
                , AnalyticsManager.Action.SEARCH_SCREEN, AnalyticsManager.Label.DELETE_ALL_KEYWORDS, null);
        }

        @Override
        public void onAutoCompleteKeyword(String keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (mStayBookingDay == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            ((StaySearchNetworkController) mPlaceSearchNetworkController).requestAutoComplete(mStayBookingDay, keyword);
        }

        @Override
        public void onSearch(String text)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (DailyTextUtils.isTextEmpty(text) == true || mStayBookingDay == null)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                onCalendarClick(true, SearchType.SEARCHES, text, null);
                return;
            }

            Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
        }

        @Override
        public void onSearch(String text, Keyword keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (keyword == null || mStayBookingDay == null)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                SearchType searchType = keyword instanceof StayKeyword ? Constants.SearchType.AUTOCOMPLETE : Constants.SearchType.RECENT;
                onCalendarClick(true, searchType, text, keyword);
                return;
            }

            if (keyword instanceof StayKeyword)
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, text, keyword, Constants.SearchType.AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, keyword, Constants.SearchType.RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation, Constants.SearchType searchType, String inputText, Keyword keyword)
        {
            startCalendar(isAnimation, searchType, inputText, keyword);
        }

        @Override
        public void onSearchEnabled(boolean enabled)
        {
            if (mOnSearchFragmentListener == null)
            {
                return;
            }

            mOnSearchFragmentListener.onSearchEnabled(enabled);
        }

        @Override
        public void onSearchCampaignTag(CampaignTag campaignTag)
        {
            Intent intent = StayCampaignTagListActivity.newInstance(getActivity() //
                , campaignTag.index, campaignTag.campaignTag, mStayBookingDay);

            startActivityForResult(intent, REQUEST_CODE_STAY_CAMPAIGN_TAG_LIST);
        }

        @Override
        public void onSearchRecentlyPlace(Place place)
        {
            if (place == null)
            {
                return;
            }

            Stay stay = (Stay) place;


            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), stay);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(0);

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , mStayBookingDay, stay.index, stay.name, stay.imageUrl //
                , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

            if (stay.truevr == true)
            {
                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
            }

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK, Integer.toString(stay.index), null);

        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StaySearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StaySearchNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAutoComplete(String keyword, List<StayKeyword> list)
        {
            if (mAnalyticsDisposable != null)
            {
                mAnalyticsDisposable.dispose();
            }

            mAnalyticsDisposable = null;

            if (isFinishing() == true)
            {
                return;
            }

            mPlaceSearchLayout.updateAutoCompleteLayout(keyword, list);

            if (keyword != null)
            {
                mAnalyticsDisposable = Observable.just(keyword).delaySubscription(2, TimeUnit.SECONDS).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(@NonNull String keyword) throws Exception
                    {
                        int soldOutCount = 0;

                        for (StayKeyword stayKeyword : list)
                        {
                            if (stayKeyword.index > 0 && stayKeyword.availableRooms == 0)
                            {
                                soldOutCount++;
                            }
                        }

                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.AUTO_SEARCH_LIST//
                            , keyword, Integer.toString(list.size()), soldOutCount, null);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception
                    {
                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.AUTO_SEARCH_LIST//
                            , keyword, "0", 0, null);
                    }
                });
            }
        }

        @Override
        public void onDateTime(TodayDateTime todayDateTime)
        {
            unLockUI();

            mTodayDateTime = todayDateTime;

            setDateChanged(mTodayDateTime, mStayBookingDay);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StaySearchFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StaySearchFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StaySearchFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StaySearchFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StaySearchFragment.this.onErrorResponse(call, response);
        }
    };
}
