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
import com.twoheart.dailyhotel.screen.hotel.filter.HotelCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchLayout;
import com.twoheart.dailyhotel.screen.hotel.search.HotelSearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StaySearchFragment extends PlaceSearchFragment
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private StaySearchNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights;

    private Handler mAnalyticsHandler;

//    public static Intent newInstance(Context context, SaleTime saleTime, int nights)
//    {
//        Intent intent = new Intent(context, StaySearchFragment.class);
//        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
//        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
//
//        return intent;
//    }
//
//    @Override
//    protected void initIntent(Intent intent)
//    {
//        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
//        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);
//
//        mAnalyticsHandler = new AnalyticsHandler(this);
//    }

    @Override
    protected void initContents()
    {
        super.initContents();

        mNetworkController = new StaySearchNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);

        setDateText(mSaleTime, mNights);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mSaleTime == null)
        {
            mBaseActivity.finish();
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

                    setDateText(checkInSaleTime, checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay());

                    mPlaceSearchLayout.requestUpdateAutoCompleteLayout();
                }

                mPlaceSearchLayout.showSearchKeyboard();
                break;
            }
        }
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return null;
    }

    @Override
    protected PlaceSearchNetworkController getPlaceSearchNetworkController(Context context)
    {
        return null;
    }

    @Override
    protected void initIntent(Intent intent)
    {

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
        Intent intent = HotelSearchResultActivity.newInstance(mBaseActivity, mSaleTime, mNights, location);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(mBaseActivity).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH);
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

            //            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
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

            Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }

        @Override
        public void onDeleteRecentSearches()
        {
            mDailyRecentSearches.clear();
            DailyPreference.getInstance(mBaseActivity).setHotelRecentSearches("");

            mPlaceSearchLayout.updateRecentSearchesLayout(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
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

            Intent intent = HotelSearchResultActivity.newInstance(mBaseActivity, mSaleTime, mNights, text);
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
                Intent intent = HotelSearchResultActivity.newInstance(mBaseActivity, mSaleTime, mNights, keyword, HotelSearchResultActivity.SEARCHTYPE_RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = HotelSearchResultActivity.newInstance(mBaseActivity, mSaleTime, mNights, text, keyword, HotelSearchResultActivity.SEARCHTYPE_AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onShowCalendar(boolean isAnimation)
        {
            if (isAnimation == true)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
            }

            Intent intent = HotelCalendarActivity.newInstance(mBaseActivity, mSaleTime, mNights, AnalyticsManager.ValueType.SEARCH, true, isAnimation);
            startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
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
            String checkInDate = staySearchFragment.mSaleTime.getDayOfDaysDateFormat("yyMMdd");
            SaleTime checkOutSaleTime = staySearchFragment.mSaleTime.getClone(staySearchFragment.mSaleTime.getOffsetDailyDay() + staySearchFragment.mNights);
            String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyMMdd");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);

            return String.format("%s-%s-%s", checkInDate, checkOutDate, simpleDateFormat.format(calendar.getTime()));
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
