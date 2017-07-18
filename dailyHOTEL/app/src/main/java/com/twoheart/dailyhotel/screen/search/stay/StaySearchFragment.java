package com.twoheart.dailyhotel.screen.search.stay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.screen.home.stay.outbound.search.StayOutboundSearchActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class StaySearchFragment extends PlaceSearchFragment
{
    StayBookingDay mStayBookingDay;

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
            DailyPreference.getInstance(mBaseActivity).setHotelRecentSearches("");

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
                onCalendarClick(true);
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
                onCalendarClick(true);
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
        public void onCalendarClick(boolean isAnimation)
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

            Intent intent = StayCalendarActivity.newInstance(mBaseActivity, mTodayDateTime, mStayBookingDay, //
                AnalyticsManager.ValueType.SEARCH, true, isAnimation);

            if (intent == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
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

    private StaySearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StaySearchNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAutoComplete(String keyword, List<StayKeyword> list)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mPlaceSearchLayout.updateAutoCompleteLayout(keyword, list);
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
