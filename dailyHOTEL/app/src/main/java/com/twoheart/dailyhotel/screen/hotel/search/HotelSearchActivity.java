package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.place.activity.PlaceSearchActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HotelSearchActivity extends PlaceSearchActivity
{
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
    protected String getSearchHintText()
    {
        return getString(R.string.label_search_hotel_hint);
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
    protected void requestAutoComplete(String text, final PlaceSearchActivity.OnAutoCompleteResultListener listener)
    {
        DailyNetworkAPI.getInstance().requestHotelSearchAutoCompleteList(mNetworkTag//
            , mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), text.trim(), new DailyHotelJsonArrayResponseListener()
        {
            @Override
            public void onResponse(String url, JSONArray response)
            {
                if (listener != null)
                {
                    int startIndex = url.lastIndexOf('=');

                    int length = response.length();

                    List<Keyword> keywordList = new ArrayList<>(length);

                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            keywordList.add(new Keyword(response.getJSONObject(i)));
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }

                    listener.onAutoCompleteResultListener(url.substring(startIndex + 1), keywordList);
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                if (listener != null)
                {
                    listener.onAutoCompleteResultListener(null, null);
                }
            }
        });
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
}
