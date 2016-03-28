package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchActivity;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;

public class HotelSearchActivity extends PlaceSearchActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private HotelSearchPresenter mHotelSearchPresenter;
    private SaleTime mSaleTime;
    private int mNights;

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

        mHotelSearchPresenter = new HotelSearchPresenter(this, mNetworkTag, mOnPresenterListener);
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
            mHotelSearchPresenter.requestAutoComplete(mSaleTime, mNights, keyword);
        }

        @Override
        public void onSearchResult(String text)
        {
            Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
        }

        @Override
        public void onSearchResult(String text, Keyword keyword)
        {
            Intent intent = HotelSearchResultActivity.newInstance(HotelSearchActivity.this, mSaleTime, mNights, keyword);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);

            if (keyword.price < 0)
            {
                // 최근 검색어로 검색
                AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_SEARCH_CLICKED, keyword.name, null);
            } else
            {
                // 자동 완성으로 검색
                String label = String.format("%s-%s", text, keyword.name);
                AnalyticsManager.getInstance(HotelSearchActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_SEARCH_CLICKED, label, null);
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
    // OnPresenterListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HotelSearchPresenter.OnPresenterListener mOnPresenterListener = new HotelSearchPresenter.OnPresenterListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            HotelSearchActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            HotelSearchActivity.this.onError(e);
        }

        @Override
        public void onErrorMessage(String message)
        {
            HotelSearchActivity.this.onErrorMessage(message);
        }

        @Override
        public void onResponseAutoComplete(String keyword, List<Keyword> list)
        {
            mPlaceSearchLayout.updateAutoCompleteLayout(keyword, list);
        }
    };
}
