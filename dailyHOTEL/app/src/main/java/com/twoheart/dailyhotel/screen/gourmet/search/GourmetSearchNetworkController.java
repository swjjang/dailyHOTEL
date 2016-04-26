package com.twoheart.dailyhotel.screen.gourmet.search;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class GourmetSearchNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseAutoComplete(String keyword, List<Keyword> list);
    }

    public GourmetSearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    public void requestAutoComplete(SaleTime saleTime, String keyword)
    {
        DailyNetworkAPI.getInstance().requestGourmetSearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), keyword, mGourmetSearchAutoCompleteListener, mGourmetearchAutoCompleteErrorListener);
    }

    private DailyHotelJsonArrayResponseListener mGourmetSearchAutoCompleteListener = new DailyHotelJsonArrayResponseListener()
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

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(url.substring(startIndex + 1), keywordList);
        }
    };

    private Response.ErrorListener mGourmetearchAutoCompleteErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
