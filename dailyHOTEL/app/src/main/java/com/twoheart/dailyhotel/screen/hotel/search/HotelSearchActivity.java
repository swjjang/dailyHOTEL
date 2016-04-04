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
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;

public class HotelSearchActivity extends PlaceSearchActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private HotelSearchNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights;

    private Handler mAnalyticsHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_NOTMATCHED, (String) msg.obj, null);
        }
    };

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
    }

    @Override
    protected void initContents()
    {
        super.initContents();

        mNetworkController = new HotelSearchNetworkController(this, mNetworkTag, mOnNetworkControllerListener);
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

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH, null);
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

            Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, keyword);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);

            if (keyword.price < 0)
            {
                // 최근 검색어로 검색
                if (keyword.icon == PlaceSearchLayout.HOTEL_ICON)
                {
                    // 호텔인 경우
                    AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                        , AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_SEARCH_CLICKED, keyword.name, null);
                } else
                {
                    // 그외
                    AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                        , AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_SEARCH_CLICKED, keyword.name, null);
                }
            } else
            {
                // 자동 완성으로 검색
                if (keyword.price == 0)
                {
                    // 지역인 경우
                    String label = String.format("지역-%s-%s", text, keyword.name);
                    AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                        , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_CLICKED, label, null);
                } else
                {
                    // 호텔인 경우
                    String label = String.format("호텔-%s-%s", text, keyword.name);
                    AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                        , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_CLICKED, label, null);
                }
            }
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
        public void onErrorMessage(int msgCode, String message)
        {
            unLockUI();

            HotelSearchActivity.this.onErrorMessage(msgCode, message);
        }
    };
}
