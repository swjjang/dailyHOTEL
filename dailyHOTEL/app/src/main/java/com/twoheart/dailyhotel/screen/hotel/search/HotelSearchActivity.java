package com.twoheart.dailyhotel.screen.hotel.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchActivity;
import com.twoheart.dailyhotel.util.DailyPreference;

public class HotelSearchActivity extends PlaceSearchActivity
{
    private static final int REQUEST_ACTIVITY_SEARCHRESULT = 100;

    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

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
    protected String getAroundPlaceString()
    {
        return getString(R.string.label_view_myaround_hotel);
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
    protected void deleteAllRecentSearches()
    {
        DailyPreference.getInstance(this).setHotelRecentSearches("");
    }

    @Override
    protected void requestAutoComplete(String text, PlaceSearchActivity.OnAutoCompleteResultListener listener)
    {

    }

    @Override
    protected void showSearchResult(String text)
    {
        Intent intent = HotelSearchResultActivity.newInstance(this, mSaleTime, mNights, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    protected void showSearchResult(Keyword keyword)
    {
        Intent intent = HotelSearchResultActivity.newInstance(this, mSaleTime, mNights, keyword);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_ACTIVITY_SEARCHRESULT:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }
        }
    }
}
