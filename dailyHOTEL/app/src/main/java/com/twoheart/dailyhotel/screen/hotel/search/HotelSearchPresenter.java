package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.place.base.BasePresenter;
import com.twoheart.dailyhotel.place.base.OnBasePresenterListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HotelSearchPresenter extends BasePresenter
{
    protected interface OnPresenterListener extends OnBasePresenterListener
    {
        void onResponseAutoComplete(String keyword, List<Keyword> list);
    }

    public HotelSearchPresenter(Context context, String networkTag, OnBasePresenterListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnPresenterListener.onErrorResponse(volleyError);
    }

    public void requestAutoComplete(SaleTime saleTime, int lengthStay, String keyword)
    {
        DailyNetworkAPI.getInstance().requestHotelSearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), lengthStay, keyword, mHotelSearchAutoCompleteListener, mHotelSearchAutoCompleteErrorListener);
    }

    private DailyHotelJsonArrayResponseListener mHotelSearchAutoCompleteListener = new DailyHotelJsonArrayResponseListener()
    {
        @Override
        public void onResponse(String url, JSONArray response)
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

            ((OnPresenterListener) mOnPresenterListener).onResponseAutoComplete(url.substring(startIndex + 1), keywordList);
        }
    };

    private Response.ErrorListener mHotelSearchAutoCompleteErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnPresenterListener) mOnPresenterListener).onResponseAutoComplete(null, null);
        }
    };
}
