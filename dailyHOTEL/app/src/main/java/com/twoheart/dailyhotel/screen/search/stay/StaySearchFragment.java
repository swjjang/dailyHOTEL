package com.twoheart.dailyhotel.screen.search.stay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetCurationManager;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayCurationManager;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

public class StaySearchFragment extends PlaceSearchFragment
{
    private SaleTime mCheckInSaleTime;
    private SaleTime mCheckOutSaleTime;

    private Handler mAnalyticsHandler;

    @Override
    protected void initContents()
    {
        super.initContents();

        mAnalyticsHandler = new AnalyticsHandler(this);

        if (GourmetCurationManager.getInstance().getSaleTime() == null)
        {
            try
            {
                SaleTime saleTime = StayCurationManager.getInstance().getCheckInSaleTime().getClone(0);

                GourmetCurationManager.getInstance().setSaleTime(saleTime);
            } catch (Exception e)
            {
                Util.restartApp(mBaseActivity);
                return;
            }
        }

        setDateText(StayCurationManager.getInstance().getCheckInSaleTime(), StayCurationManager.getInstance().getCheckOutSaleTime());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mCheckInSaleTime == null || mCheckOutSaleTime == null)
        {
            setDateText(StayCurationManager.getInstance().getCheckInSaleTime(), StayCurationManager.getInstance().getCheckOutSaleTime());
        } else
        {
            setDateText(mCheckInSaleTime, mCheckOutSaleTime);
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
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                    SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                    setDateText(checkInSaleTime, checkOutSaleTime);

                    mPlaceSearchLayout.requestUpdateAutoCompleteLayout();
                }

                mShowSearchKeyboard = true;
                break;
            }
        }
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return new StaySearchLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceSearchNetworkController getPlaceSearchNetworkController(Context context)
    {
        return new StaySearchNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
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
    protected PlaceSearchLayout getLayout()
    {
        return new StaySearchLayout(mBaseActivity, mOnEventListener);
    }

    @Override
    protected void onSearch(Location location)
    {
        if (mIsScrolling == true)
        {
            return;
        }

        int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

        Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, location);
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

        if (Util.isTextEmpty(text) == true)
        {
            return;
        }

        int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

        Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(mBaseActivity).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH);
    }

    private void setDateText(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        if (checkInSaleTime == null || checkOutSaleTime == null || mPlaceSearchLayout == null)
        {
            return;
        }

        mCheckInSaleTime = checkInSaleTime;
        mCheckOutSaleTime = checkOutSaleTime;

        String checkInDate = checkInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();
        mPlaceSearchLayout.setDataText(String.format("%s - %s, %d박", checkInDate, checkOutDate, nights));
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

            //            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
            //                , AnalyticsManager.Action.HOTEL_KEYWORD_RESET_CLICKED, AnalyticsManager.Label.SEARCH_KEYWORD_RESET, null);
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

            Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_KEYWORD_HISTORY_DELETED, AnalyticsManager.Label.DELETE_ALL_KEYWORDS, null);
        }

        @Override
        public void onAutoCompleteKeyword(String keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

            ((StaySearchNetworkController) mPlaceSearchNetworkController).requestAutoComplete(mCheckInSaleTime, nights, keyword);
        }

        @Override
        public void onSearch(String text)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (Util.isTextEmpty(text) == true)
            {
                return;
            }

            int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

            Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
        }

        @Override
        public void onSearch(String text, Keyword keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (keyword == null)
            {
                return;
            }

            int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

            if (keyword.price < 0)
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, keyword, StaySearchResultActivity.SEARCHTYPE_RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = StaySearchResultActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, text, keyword, StaySearchResultActivity.SEARCHTYPE_AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (isAnimation == true)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
            }

            int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

            Intent intent = StayCalendarActivity.newInstance(mBaseActivity, mCheckInSaleTime, nights, AnalyticsManager.ValueType.SEARCH, true, isAnimation);
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.KEYWORD_BACK_BUTTON_CLICKED, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StaySearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StaySearchNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAutoComplete(String keyword, List<Keyword> list)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mAnalyticsHandler.removeMessages(0);

            if (list != null && list.size() == 0)
            {
                Message message = mAnalyticsHandler.obtainMessage(0, keyword);
                mAnalyticsHandler.sendMessageDelayed(message, 1000);
            }

            mPlaceSearchLayout.updateAutoCompleteLayout(keyword, list);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            mBaseActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            mBaseActivity.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            mBaseActivity.onErrorToastMessage(message);
        }
    };

    private static class AnalyticsHandler extends Handler
    {
        private final WeakReference<StaySearchFragment> mWeakReference;

        public AnalyticsHandler(StaySearchFragment activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        private String getSearchDate(StaySearchFragment staySearchFragment)
        {
            String checkInDate = staySearchFragment.mCheckInSaleTime.getDayOfDaysDateFormat("yyMMdd");
            String checkOutDate = staySearchFragment.mCheckOutSaleTime.getDayOfDaysDateFormat("yyMMdd");

            //            Calendar calendar = Calendar.getInstance();
            //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);
            //
            //            return String.format("%s-%s-%s", checkInDate, checkOutDate, simpleDateFormat.format(calendar.getTime()));
            return String.format("%s-%s-%s", checkInDate, checkOutDate, DailyCalendar.format(new Date(), "yyMMddHHmm"));
        }

        @Override
        public void handleMessage(Message msg)
        {
            StaySearchFragment staySearchFragment = mWeakReference.get();

            if (staySearchFragment == null)
            {
                return;
            }

            String label = String.format("%s-%s", msg.obj, getSearchDate(staySearchFragment));
            AnalyticsManager.getInstance(staySearchFragment.getContext()).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_NOTMATCHED, label, null);
        }
    }
}
