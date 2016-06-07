package com.twoheart.dailyhotel.screen.gourmet.search;

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
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GourmetSearchActivity extends PlaceSearchActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";

    private GourmetSearchNetworkController mNetworkController;
    private SaleTime mSaleTime;

    private Handler mAnalyticsHandler;

    public static Intent newInstance(Context context, SaleTime saleTime)
    {
        Intent intent = new Intent(context, GourmetSearchActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);

        mAnalyticsHandler = new AnalyticsHandler(this);
    }

    @Override
    protected void initContents()
    {
        super.initContents();

        mNetworkController = new GourmetSearchNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setDateText(mSaleTime);

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
                    SaleTime saleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);

                    setDateText(saleTime);

                    mPlaceSearchLayout.requestUpdateAutoCompleteLayout();
                }
                break;
            }
        }
    }

    @Override
    protected String getRecentSearches()
    {
        return DailyPreference.getInstance(this).getGourmetRecentSearches();
    }

    @Override
    protected void writeRecentSearches(String text)
    {
        DailyPreference.getInstance(this).setGourmetRecentSearches(text);
    }

    @Override
    protected PlaceSearchLayout getLayout()
    {
        return new GourmetSearchLayout(this, mOnEventListener);
    }

    @Override
    protected void onSearch(Location location)
    {
        Intent intent = GourmetSearchResultActivity.newInstance(GourmetSearchActivity.this, mSaleTime, location);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH);
    }

    private void setDateText(SaleTime saleTime)
    {
        if (saleTime == null || mPlaceSearchLayout == null)
        {
            return;
        }

        mSaleTime = saleTime;

        mPlaceSearchLayout.setDataText(saleTime.getDailyDateFormat("yyyy.MM.dd(EEE)"));
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

            //            AnalyticsManager.getInstance(GourmetSearchActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
            //                , AnalyticsManager.Action.GOURMET_KEYWORD_RESET_CLICKED, AnalyticsManager.Label.SEARCH_KEYWORD_RESET, null);
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
            DailyPreference.getInstance(GourmetSearchActivity.this).setGourmetRecentSearches("");

            mPlaceSearchLayout.updateRecentSearchesLayout(null);

            AnalyticsManager.getInstance(GourmetSearchActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_KEYWORD_HISTORY_DELETED, AnalyticsManager.Label.DELETE_ALL_KEYWORDS, null);
        }

        @Override
        public void onAutoCompleteKeyword(String keyword)
        {
            mNetworkController.requestAutoComplete(mSaleTime, keyword);
        }

        @Override
        public void onSearch(String text)
        {
            if (Util.isTextEmpty(text) == true)
            {
                return;
            }

            Intent intent = GourmetSearchResultActivity.newInstance(GourmetSearchActivity.this, mSaleTime, text);
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
                Intent intent = GourmetSearchResultActivity.newInstance(GourmetSearchActivity.this, mSaleTime, keyword, GourmetSearchResultActivity.SEARCHTYPE_RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = GourmetSearchResultActivity.newInstance(GourmetSearchActivity.this, mSaleTime, text, keyword, GourmetSearchResultActivity.SEARCHTYPE_AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onShowCalendar(boolean isAnimation)
        {
            if (isAnimation == true)
            {
                AnalyticsManager.getInstance(GourmetSearchActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
            }

            Intent intent = GourmetCalendarActivity.newInstance(GourmetSearchActivity.this, mSaleTime, AnalyticsManager.ValueType.SEARCH, true, isAnimation);
            startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void finish()
        {
            GourmetSearchActivity.this.finish();

            AnalyticsManager.getInstance(GourmetSearchActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.KEYWORD_BACK_BUTTON_CLICKED, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetSearchNetworkController.OnNetworkControllerListener()
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
            GourmetSearchActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            GourmetSearchActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            GourmetSearchActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            GourmetSearchActivity.this.onErrorToastMessage(message);
        }
    };

    private static class AnalyticsHandler extends Handler
    {
        private final WeakReference<GourmetSearchActivity> mWeakReference;

        public AnalyticsHandler(GourmetSearchActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        private String getSearchDate(GourmetSearchActivity gourmetSearchActivity)
        {
            String saleDate = gourmetSearchActivity.mSaleTime.getDayOfDaysDateFormat("yyMMdd");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");

            return String.format("%s-%s", saleDate, simpleDateFormat.format(calendar.getTime()));
        }

        @Override
        public void handleMessage(Message msg)
        {
            GourmetSearchActivity gourmetSearchActivity = mWeakReference.get();

            if (gourmetSearchActivity == null)
            {
                return;
            }

            String label = String.format("%s-%s", msg.obj, getSearchDate(gourmetSearchActivity));
            AnalyticsManager.getInstance(gourmetSearchActivity).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_AUTOCOMPLETED_KEYWORD_NOTMATCHED, label, null);
        }
    }
}
