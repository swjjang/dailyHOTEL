package com.twoheart.dailyhotel.screen.search.stay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.util.Pair;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.search.StayOutboundSearchActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetSearchCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StaySearchCalendarActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

public class StaySearchFragment extends PlaceSearchFragment
{
    StayBookingDay mStayBookingDay;
    Disposable mAnalyticsDisposable;

    ArrayList<RecentlyPlace> mRecentlyStayList;
    ArrayList<String> mStayOutboundKeywordList;

    String mInputText;
    Object mCalenderObject;

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

        setStayOutboundKeywordList(mBaseActivity);
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
                            String searchTypeString = data.getStringExtra(StaySearchCalendarActivity.INTENT_EXTRA_DATA_SEARCH_TYPE);
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

                            mCalenderObject = null;
                            mInputText = null;
                        }
                    }
                }
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
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
    }

    @Override
    public void startSearchResultActivity()
    {
        if (mIsScrolling == true)
        {
            return;
        }

        String text = mPlaceSearchLayout.getSearchKeyword();

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
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
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
    public void startCalendar(boolean isAnimation, SearchType searchType)
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
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH //
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
        Intent intent = StayCampaignTagListActivity.newInstance(getActivity() //
            , index, title, mStayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT) //
            , mStayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));

        startActivityForResult(intent, REQUEST_CODE_STAY_CAMPAIGN_TAG_LIST);
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

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject(DailyDb.MAX_RECENT_PLACE_COUNT, ServiceType.HOTEL) //
            .observeOn(Schedulers.io()).flatMap(new Function<JSONObject, ObservableSource<ArrayList<RecentlyPlace>>>()
            {
                @Override
                public ObservableSource<ArrayList<RecentlyPlace>> apply(@NonNull JSONObject jsonObject) throws Exception
                {
                    if (jsonObject == null || jsonObject.has("keys") == false)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    return mRecentlyRemoteImpl.getInboundRecentlyList(jsonObject);
                }
            });

        addCompositeDisposable(Observable.zip(ibObservable //
            , mCampaignTagRemoteImpl.getCampaignTagList(getServiceType().name()) //
            , mRecentlyLocalImpl.getRecentlyIndexList(ServiceType.HOTEL)  //
            , new Function3<ArrayList<RecentlyPlace>, ArrayList<CampaignTag>, ArrayList<Integer>, List<Keyword>>()
            {
                @Override
                public List<Keyword> apply(@NonNull ArrayList<RecentlyPlace> stayList //
                    , @NonNull ArrayList<CampaignTag> tagList, @NonNull ArrayList<Integer> expectedList) throws Exception
                {
                    if (expectedList != null && expectedList.size() > 0)
                    {
                        Collections.sort(stayList, new Comparator<RecentlyPlace>()
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

                    mRecentlyStayList = stayList;
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
                    mPlaceSearchLayout.setRecyclerViewData(mRecentlyStayList, mCampaignTagList, mKeywordList);
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
                        StayKeyword stayKeyword = (StayKeyword) item;

                        if (stayKeyword != null && stayKeyword.index > 0 //
                            && stayKeyword.availableRooms == 0)
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

    private void setStayOutboundKeywordList(Context context)
    {
        if (context == null)
        {
            mStayOutboundKeywordList = null;
            return;
        }

        Observable.defer(new Callable<ObservableSource<ArrayList<String>>>()
        {
            @Override
            public ObservableSource<ArrayList<String>> call() throws Exception
            {
                String prefereceText = DailyRemoteConfigPreference.getInstance(context).getKeyRemoteConfigObSearchKeyword();
                if (DailyTextUtils.isTextEmpty(prefereceText) == true)
                {
                    return Observable.just(new ArrayList<>());
                }

                ArrayList<String> arrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(prefereceText);
                int length = jsonArray.length();
                for (int i = 0; i < length; i++)
                {
                    arrayList.add(jsonArray.getString(i));
                }

                return Observable.just(arrayList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<String>>()
        {
            @Override
            public void accept(ArrayList<String> stringList) throws Exception
            {
                mStayOutboundKeywordList = stringList;
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                ExLog.w(throwable.getMessage());

                mStayOutboundKeywordList = null;
            }
        });
    }

    public String getSearchKeyword()
    {
        if (mPlaceSearchLayout == null)
        {
            return null;
        }

        return mPlaceSearchLayout.getSearchKeyword();
    }

    public void startStayOutboundSearchActivity(String keyword)
    {
        if (DailyTextUtils.isTextEmpty(keyword) == false)
        {
            Intent intent = DailyInternalDeepLink.getStayOutboundSearchSuggestScreenLink(getActivity(), keyword);
            DailyDeepLink dailyDeepLink = DailyDeepLink.getNewInstance(intent.getData());

            startActivity(StayOutboundSearchActivity.newInstance(getContext(), dailyDeepLink == null ? null : dailyDeepLink.getDeepLink()));
            //            startActivity(DailyInternalDeepLink.getStayOutboundSearchSuggestScreenLink(getActivity(), keyword));
            return;
        }

        startActivity(StayOutboundSearchActivity.newInstance(getContext()));
    }

    public boolean showCheckStayOutboundSearchDialog()
    {
        if (mBaseActivity == null)
        {
            return false;
        }

        String keyword = getSearchKeyword();
        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            return false;
        }

        if (mStayOutboundKeywordList == null || mStayOutboundKeywordList.size() == 0)
        {
            return false;
        }

        boolean hasKeyword = mStayOutboundKeywordList.contains(keyword);
        if (hasKeyword == false)
        {
            return false;
        }

        String title = mBaseActivity.getResources().getString(R.string.dialog_notice2);
        String message = mBaseActivity.getResources().getString(R.string.dialog_message_check_stayoutbound_search);
        String positive = mBaseActivity.getResources().getString(R.string.dialog_btn_text_yes);
        String negative = mBaseActivity.getResources().getString(R.string.dialog_btn_text_no);

        mBaseActivity.showSimpleDialog(title, message, positive, negative, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // positive
                startStayOutboundSearchActivity(getSearchKeyword());

                try
                {
                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.SEARCH //
                        , AnalyticsManager.Action.OB_KEYWORDS_IN_DOMESTIC, AnalyticsManager.Label.YES, null);
                } catch (Exception e)
                {
                    ExLog.w(e.toString());
                }
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // negative
                try
                {
                    if (mOnEventListener == null)
                    {
                        return;
                    }

                    mOnEventListener.onSearch(getSearchKeyword(), true);

                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.SEARCH //
                        , AnalyticsManager.Action.OB_KEYWORDS_IN_DOMESTIC, AnalyticsManager.Label.NO, null);
                } catch (Exception e)
                {
                    ExLog.w(e.toString());
                }
            }
        });

        try
        {
            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.SEARCH //
                , AnalyticsManager.Action.OB_KEYWORDS_IN_DOMESTIC, AnalyticsManager.Label.POPED_UP, null);
        } catch (Exception e)
        {
            ExLog.w(e.toString());
        }

        return true;
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

            startStayOutboundSearchActivity(null);

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
            DailyPreference.getInstance(mBaseActivity).setHotelRecentSearches("");

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

            if (DailyTextUtils.isTextEmpty(keyword) == true)
            {
                return;
            }

            String checkInDate;
            int nights;

            try
            {
                checkInDate = mStayBookingDay.getCheckInDay("yyyy-MM-dd");
                nights = mStayBookingDay.getNights();
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return;
            }

            mSuggestRemoteImpl.getSuggestsByStayInbound(checkInDate, nights, keyword) //
                .subscribe(new Consumer<Pair<String, ArrayList<StayKeyword>>>()
                {
                    @Override
                    public void accept(@NonNull Pair<String, ArrayList<StayKeyword>> stringArrayListPair) throws Exception
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
        public void onSearch(String text, boolean isSkipCheck)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (DailyTextUtils.isTextEmpty(text) == true || mStayBookingDay == null)
            {
                return;
            }

            if (isSkipCheck == false && showCheckStayOutboundSearchDialog() == true)
            {
                return;
            }

            if (isDateChanged() == false)
            {
                mInputText = text;

                onCalendarClick(true, SearchType.SEARCHES);
                return;
            }

            Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
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
                SearchType searchType = keyword instanceof StayKeyword ? Constants.SearchType.AUTOCOMPLETE : Constants.SearchType.RECENTLY_KEYWORD;
                mInputText = text;
                mCalenderObject = keyword;

                onCalendarClick(true, searchType);
                return;
            }

            if (keyword instanceof StayKeyword)
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, text, keyword, Constants.SearchType.AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
            } else
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, keyword, Constants.SearchType.RECENTLY_KEYWORD);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH_RESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation, SearchType searchType)
        {
            if (searchType == null && showCheckStayOutboundSearchDialog() == true)
            {
                return;
            }

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
            if (campaignTag == null)
            {
                return;
            }

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

            StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
            analyticsParam.setAddressAreaName(place.addrSummary);

            if (place.prices != null)
            {
                analyticsParam.price = place.prices.normalPrice;

                if (place.prices.discountPrice > 0)
                {
                    analyticsParam.discountPrice = place.prices.discountPrice;
                }
            } else
            {
                analyticsParam.price = 0;
                analyticsParam.discountPrice = 0;
            }

            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = -1;
            analyticsParam.totalListCount = -1;
            analyticsParam.isDailyChoice = false;
            analyticsParam.gradeName = place.details.grade;

            //            AnalyticsParam analyticsParam = new AnalyticsParam();
            //            analyticsParam.setParam(getActivity(), place);
            //            analyticsParam.setProvince(null);
            //            analyticsParam.setTotalListCount(0);

            //            Intent intent = StayDetailActivity.newInstance(getActivity() //
            //                , mStayBookingDay, place.index, place.title, place.imageUrl //
            //                , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , place.index, place.title, place.imageUrl//
                , place.prices != null ? place.prices.discountPrice : StayDetailActivity.NONE_PRICE//
                , mStayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

            if (place.details.isTrueVr == true)
            {
                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(place.index), null);
            }

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK, String.format(Locale.KOREA, "%d_%d", -1, place.index), null);

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
