package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchActivity;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.screen.hotel.filter.HotelCalendarActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HotelSearchActivity extends PlaceSearchActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private HotelSearchNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights;

    private Handler mAnalyticsHandler;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights)
    {
        Intent intent = new Intent(context, HotelSearchActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);

        mAnalyticsHandler = new AnalyticsHandler(this);
    }

    @Override
    protected void initContents()
    {
        super.initContents();

        mNetworkController = new HotelSearchNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setDateText(mSaleTime, mNights);

        mOnEventListener.onShowCalendar(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mSaleTime == null)
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_CALENDAR:
            {
                if (resultCode == RESULT_OK && data != null)
                {
                    SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                    SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                    setDateText(checkInSaleTime, checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay());

                    mPlaceSearchLayout.requestUpdateAutoCompleteLayout();
                }

                mPlaceSearchLayout.showSearchKeyboard();
                break;
            }
        }
    }

    @Override
    protected String getRecentSearches()
    {
        return DailyPreference.getInstance(this).getHotelRecentSearches();
    }

    @Override
    protected void writeRecentSearches(String text)
    {
        DailyPreference.getInstance(this).setHotelRecentSearches(text);
    }

    @Override
    protected PlaceSearchLayout getLayout()
    {
        return new HotelSearchLayout(this, mOnEventListener);
    }

    @Override
    protected void onSearch(Location location)
    {
        Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, location);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH);
    }

    private void setDateText(SaleTime saleTime, int nights)
    {
        if (saleTime == null || nights == 0 || mPlaceSearchLayout == null)
        {
            return;
        }

        mSaleTime = saleTime;
        mNights = nights;

        String checkInDate = saleTime.getDailyDateFormat("yyyy.MM.dd(EEE)");
        SaleTime checkOutSaleTime = saleTime.getClone(saleTime.getOffsetDailyDay() + nights);
        String checkOutDate = checkOutSaleTime.getDailyDateFormat("yyyy.MM.dd(EEE)");

        mPlaceSearchLayout.setDataText(checkInDate + " - " + checkOutDate);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchLayout.OnEventListener mOnEventListener = new PlaceSearchLayout.OnEventListener()
    {
        @Override
        public void onResetKeyword()
        {
            mPlaceSearchLayout.resetSearchKeyword();

            //            AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
            //                , AnalyticsManager.Action.HOTEL_KEYWORD_RESET_CLICKED, AnalyticsManager.Label.SEARCH_KEYWORD_RESET, null);
        }

        @Override
        public void onShowTermsOfLocationDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            showTermsOfLocationDialog();
        }

        @Override
        public void onSearchMyLocation()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            searchMyLocation();
        }

        @Override
        public void onDeleteRecentSearches()
        {
            mDailyRecentSearches.clear();
            DailyPreference.getInstance(HotelSearchActivity.this).setHotelRecentSearches("");

            mPlaceSearchLayout.updateRecentSearchesLayout(null);

            AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_KEYWORD_HISTORY_DELETED, AnalyticsManager.Label.DELETE_ALL_KEYWORDS, null);
        }

        @Override
        public void onAutoCompleteKeyword(String keyword)
        {
            mNetworkController.requestAutoComplete(mSaleTime, mNights, keyword);
        }

        @Override
        public void onSearch(String text)
        {
            if (Util.isTextEmpty(text) == true)
            {
                return;
            }

            Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
        }

        @Override
        public void onSearch(String text, Keyword keyword)
        {
            if (keyword == null)
            {
                return;
            }

            if (keyword.price < 0)
            {
                Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, keyword, HotelSearchResultActivity.SEARCHTYPE_RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, text, keyword, HotelSearchResultActivity.SEARCHTYPE_AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onShowCalendar(boolean isAnimation)
        {
            if (isAnimation == true)
            {
                AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
            }

            Intent intent = HotelCalendarActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, AnalyticsManager.ValueType.SEARCH, true, isAnimation);
            startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void finish()
        {
            HotelSearchActivity.this.finish();

            AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.KEYWORD_BACK_BUTTON_CLICKED, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HotelSearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new HotelSearchNetworkController.OnNetworkControllerListener()
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
            HotelSearchActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            HotelSearchActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            HotelSearchActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            HotelSearchActivity.this.onErrorToastMessage(message);
        }
    };

    private static class AnalyticsHandler extends Handler
    {
        private final WeakReference<HotelSearchActivity> mWeakReference;

        public AnalyticsHandler(HotelSearchActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        private String getSearchDate(HotelSearchActivity hotelSearchActivity)
        {
            String checkInDate = hotelSearchActivity.mSaleTime.getDayOfDaysDateFormat("yyMMdd");
            SaleTime checkOutSaleTime = hotelSearchActivity.mSaleTime.getClone(hotelSearchActivity.mSaleTime.getOffsetDailyDay() + hotelSearchActivity.mNights);
            String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyMMdd");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);

            return String.format("%s-%s-%s", checkInDate, checkOutDate, simpleDateFormat.format(calendar.getTime()));
        }

        @Override
        public void handleMessage(Message msg)
        {
            HotelSearchActivity hotelSearchActivity = mWeakReference.get();

            if (hotelSearchActivity == null)
            {
                return;
            }

            String label = String.format("%s-%s", msg.obj, getSearchDate(hotelSearchActivity));
            AnalyticsManager.getInstance(hotelSearchActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_NOTMATCHED, label, null);
        }
    }
}
