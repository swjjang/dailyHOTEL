package com.twoheart.dailyhotel.screen.search.gourmet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class GourmetSearchFragment extends PlaceSearchFragment
{
    GourmetBookingDay mGourmetBookingDay;
    Disposable mAnalyticsDisposable;

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
        return R.layout.fragment_gourmet_search;
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return new GourmetSearchLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceSearchNetworkController getPlaceSearchNetworkController(Context context)
    {
        return new GourmetSearchNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
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

        if (mGourmetBookingDay == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
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
    public void startCalendar(boolean isAnimation)
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

        Intent intent = GourmetCalendarActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, //
            AnalyticsManager.ValueType.SEARCH, true, isAnimation);

        if (intent == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
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
                onCalendarClick(true);
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

            mPlaceSearchLayout.updateRecentSearchesLayout(null);

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

            ((GourmetSearchNetworkController) mPlaceSearchNetworkController).requestAutoComplete(mGourmetBookingDay, keyword);
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
                onCalendarClick(true);
                return;
            }

            Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
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
                onCalendarClick(true);
                return;
            }

            if (keyword instanceof GourmetKeyword)
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, text, keyword, Constants.SearchType.AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mTodayDateTime, mGourmetBookingDay, keyword, Constants.SearchType.RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation)
        {
            startCalendar(isAnimation);
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
        public void finish()
        {
            mBaseActivity.finish();
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetSearchNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAutoComplete(String keyword, List<GourmetKeyword> list)
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

                        for (GourmetKeyword gourmetKeyword : list)
                        {
                            if (gourmetKeyword.availableTickets == 0 || gourmetKeyword.availableTickets < gourmetKeyword.minimumOrderQuantity || gourmetKeyword.isExpired == true)
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

            setDateChanged(mTodayDateTime, mGourmetBookingDay);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetSearchFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetSearchFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetSearchFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetSearchFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetSearchFragment.this.onErrorResponse(call, response);
        }
    };
}
