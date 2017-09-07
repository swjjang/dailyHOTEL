package com.twoheart.dailyhotel.screen.search.gourmet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.util.Pair;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.screen.home.campaigntag.gourmet.GourmetCampaignTagListActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetSearchCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class GourmetSearchFragment extends PlaceSearchFragment
{
    GourmetBookingDay mGourmetBookingDay;
    Disposable mAnalyticsDisposable;

    private ArrayList<RecentlyPlace> mRecentlyGourmetList;

    private String mInputText;
    private Object mCalenderObject;

    @Override
    protected void initContents()
    {
        super.initContents();

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mGourmetBookingDay);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mGourmetBookingDay);
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
                        GourmetBookingDay gourmetBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                        if (gourmetBookingDay == null)
                        {
                            return;
                        }

                        setGourmetBookingDay(gourmetBookingDay);
                        setDateText(gourmetBookingDay);

                        mPlaceSearchLayout.requestUpdateAutoCompleteLayout();

                        if (data.hasExtra(GourmetSearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_TYPE) == true)
                        {
                            String searchTypeString = data.getStringExtra(GourmetSearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_TYPE);
                            if (searchTypeString == null)
                            {
                                return;
                            }

                            SearchType searchType;

                            try
                            {
                                searchType = SearchType.valueOf(searchTypeString);
                            } catch (Exception e)
                            {
                                searchType = null;
                            }

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
                                mOnEventListener.onSearch(mInputText, (Keyword) mCalenderObject);
                            } else if (searchType == SearchType.RECENTLY_KEYWORD)
                            {
                                mOnEventListener.onSearch(mInputText, (Keyword) mCalenderObject);
                            } else if (searchType == SearchType.CAMPAIGN_TAG)
                            {
                                mOnEventListener.onSearchCampaignTag((CampaignTag) mCalenderObject);
                            } else if (searchType == SearchType.RECENTLY_PLACE)
                            {
                                mOnEventListener.onSearchRecentlyPlace((RecentlyPlace) mCalenderObject);
                            }
                        }

                        mCalenderObject = null;
                        mInputText = null;
                    }
                }
                break;
            }
        }
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_gourmet_search;
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return new GourmetSearchLayout(context, mOnEventListener);
    }

    @Override
    protected String getRecentSearches()
    {
        return DailyPreference.getInstance(mBaseActivity).getGourmetRecentSearches();
    }

    @Override
    protected void writeRecentSearches(String text)
    {
        DailyPreference.getInstance(mBaseActivity).setGourmetRecentSearches(text);
    }

    @Override
    protected void onSearch(Location location)
    {
        if (mIsScrolling == true)
        {
            unLockUI();

            return;
        }

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        lockUI();

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, location, AnalyticsManager.Screen.SEARCH_MAIN);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
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

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    public void setGourmetBookingDay(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return;
        }

        mGourmetBookingDay = gourmetBookingDay;
    }

    private void setDateText(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null || mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.setDataText(gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)"));
    }

    private void setDateChanged(TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay)
    {
        if (isDateChanged() == true || todayDateTime == null || gourmetBookingDay == null)
        {
            return;
        }

        try
        {
            String visitDay = gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT);

            long visitTime = DailyCalendar.getInstance(visitDay, true).getTimeInMillis();
            long toDayTime = DailyCalendar.getInstance(todayDateTime.dailyDateTime, true).getTimeInMillis();

            int dayOfDays = (int) ((visitTime - toDayTime) / DailyCalendar.DAY_MILLISECOND);
            // 체크인 날짜가 익일이상이면 달력 체크 안함 , 즉 현재 날짜 이전이면 달력 표시 해야 함
            setDateChanged(dayOfDays > 0);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void startCalendar(boolean isAnimation, SearchType searchType)
    {
        if (mIsScrolling == true || isAdded() == false)
        {
            return;
        }

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        if (isAnimation == true)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
        }

        Intent intent = GourmetSearchCalendarActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay //
            , GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH //
            , true, isAnimation, searchType == null ? null : searchType.name());

        if (intent == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    public void startCampaignTagList(int index, String title)
    {
        Intent intent = GourmetCampaignTagListActivity.newInstance(getActivity() //
            , index, title, mGourmetBookingDay);

        startActivityForResult(intent, REQUEST_CODE_GOURMET_CAMPAIGN_TAG_LIST);
    }

    @Override
    public ServiceType getServiceType()
    {
        return ServiceType.GOURMET;
    }

    @Override
    public void setTodayDateTime(TodayDateTime todayDateTime)
    {
        setDateChanged(todayDateTime, mGourmetBookingDay);

        addCompositeDisposable(Observable.zip(mRecentlyRemoteImpl.getInboundRecentlyList(RecentlyPlaceUtil.MAX_RECENT_PLACE_COUNT, false, ServiceType.GOURMET) //
            , mCampaignTagRemoteImpl.getCampaignTagList(getServiceType().name()) //
            , new BiFunction<ArrayList<RecentlyPlace>, ArrayList<CampaignTag>, List<Keyword>>()
            {
                @Override
                public List<Keyword> apply(@NonNull ArrayList<RecentlyPlace> gourmetList, @NonNull ArrayList<CampaignTag> tagList) throws Exception
                {
                    ArrayList<Integer> expectedList = RecentlyPlaceUtil.getDbRecentlyIndexList(mBaseActivity, ServiceType.GOURMET);
                    if (expectedList != null && expectedList.size() > 0)
                    {
                        Collections.sort(gourmetList, new Comparator<RecentlyPlace>()
                        {
                            @Override
                            public int compare(RecentlyPlace o1, RecentlyPlace o2)
                            {
                                Integer position1 = expectedList.indexOf(o1.index);
                                Integer position2 = expectedList.indexOf(o2.index);

                                return position1.compareTo(position2);
                            }
                        });
                    }

                    mRecentlyGourmetList = gourmetList;
                    mCampaignTagList = tagList;

                    return mDailyRecentSearches.getList();
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Keyword>>()
        {
            @Override
            public void accept(@NonNull List<Keyword> keywordList) throws Exception
            {
                mKeywordList = keywordList;

                unLockUI();
                mPlaceSearchLayout.setRecyclerViewData(mRecentlyGourmetList, mCampaignTagList, keywordList);
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

    @Override
    public void requestCampaignTagList()
    {
        lockUI();

        addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(getServiceType().name()) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CampaignTag>>()
            {
                @Override
                public void accept(@NonNull ArrayList<CampaignTag> campaignTags) throws Exception
                {
                    unLockUI();
                    mPlaceSearchLayout.setRecyclerViewData(mRecentlyGourmetList, mCampaignTagList, mKeywordList);
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

    @Override
    public void setSuggestsList(String keyword, ArrayList<? extends Keyword> list)
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

                    for (Keyword item : list)
                    {
                        GourmetKeyword gourmetKeyword = (GourmetKeyword) item;

                        if (gourmetKeyword != null && gourmetKeyword.index > 0 //
                            && (gourmetKeyword.availableTickets == 0//
                            || gourmetKeyword.availableTickets < gourmetKeyword.minimumOrderQuantity//
                            || gourmetKeyword.isExpired == true))
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchLayout.OnEventListener mOnEventListener = new PlaceSearchLayout.OnEventListener()
    {
        @Override
        public void onResetKeyword()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            hideSearchKeyboard();
            resetSearchKeyword();
            hideAutoCompleteLayout();
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
                onCalendarClick(true, SearchType.LOCATION);
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
            DailyPreference.getInstance(mBaseActivity).setGourmetRecentSearches("");

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

            if (mGourmetBookingDay == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            if (DailyTextUtils.isTextEmpty(keyword) == true)
            {
                return;
            }

            mSuggestRemoteImpl.getSuggestsByGourmet(mGourmetBookingDay.getVisitDay("yyyy-MM-dd"), keyword) //
                .subscribe(new Consumer<Pair<String, ArrayList<GourmetKeyword>>>()
                {
                    @Override
                    public void accept(@NonNull Pair<String, ArrayList<GourmetKeyword>> stringArrayListPair) throws Exception
                    {
                        setSuggestsList(stringArrayListPair.first, stringArrayListPair.second);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception
                    {
                        ExLog.w(throwable.toString());

                        setSuggestsList(null, null);
                    }
                });
        }

        @Override
        public void onSearch(String text)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (DailyTextUtils.isTextEmpty(text) == true || mGourmetBookingDay == null)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                mInputText = text;

                onCalendarClick(true, SearchType.SEARCHES);
                return;
            }

            Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
        }

        @Override
        public void onSearch(String text, Keyword keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (keyword == null || mGourmetBookingDay == null)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                mCalenderObject = keyword;
                mInputText = text;
                SearchType searchType = keyword instanceof GourmetKeyword ? SearchType.AUTOCOMPLETE : SearchType.RECENTLY_KEYWORD;

                onCalendarClick(true, searchType);
                return;
            }

            if (keyword instanceof GourmetKeyword)
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text, keyword, Constants.SearchType.AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
            } else
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, keyword, Constants.SearchType.RECENTLY_KEYWORD);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation, SearchType searchType)
        {
            startCalendar(isAnimation, searchType);
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
            if (isDateChanged() == false)
            {
                mCalenderObject = campaignTag;

                onCalendarClick(true, SearchType.CAMPAIGN_TAG);
                return;
            }

            startCampaignTagList(campaignTag.index, campaignTag.campaignTag);
        }

        @Override
        public void onSearchRecentlyPlace(RecentlyPlace place)
        {
            if (place == null)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                mCalenderObject = place;

                onCalendarClick(true, SearchType.RECENTLY_PLACE);
                return;
            }

            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), place);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(0);

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , mGourmetBookingDay, place.index, place.title //
                , place.imageUrl, place.details.category, place.isSoldOut, analyticsParam, false //
                , PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.GOURMET_ITEM_CLICK, Integer.toString(place.index), null);
        }

        @Override
        public void onChangeAutoCompleteScrollView(boolean isShow)
        {
            if (mOnSearchFragmentListener == null)
            {
                return;
            }

            mOnSearchFragmentListener.onChangeAutoCompleteScrollView(isShow);
        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();
        }
    };
}
