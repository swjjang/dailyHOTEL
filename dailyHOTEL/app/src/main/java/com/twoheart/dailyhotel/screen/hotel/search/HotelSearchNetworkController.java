package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HotelSearchNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseAutoComplete(String keyword, List<Keyword> list);
    }

    public HotelSearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    //    @Override
    //    public void onErrorResponse(VolleyError volleyError)
    //    {
    //        mOnNetworkControllerListener.onErrorResponse(volleyError);
    //    }

    public void requestAutoComplete(SaleTime saleTime, int lengthStay, String keyword)
    {
        if (saleTime == null || lengthStay == 0 || Util.isTextEmpty(keyword) == true)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestHotelSearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), lengthStay, keyword, mHotelSearchAutoCompleteListener, mHotelSearchAutoCompleteErrorListener);
    }

    private DailyHotelJsonArrayResponseListener mHotelSearchAutoCompleteListener = new DailyHotelJsonArrayResponseListener()
    {

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

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
                    keywordList.add(new Keyword(response.getJSONObject(i), PlaceSearchLayout.HOTEL_ICON));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(url.substring(startIndex + 1), keywordList);
        }


    };

    private Response.ErrorListener mHotelSearchAutoCompleteErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
