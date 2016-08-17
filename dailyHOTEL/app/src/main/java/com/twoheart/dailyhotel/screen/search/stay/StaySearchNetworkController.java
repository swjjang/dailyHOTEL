package com.twoheart.dailyhotel.screen.search.stay;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class StaySearchNetworkController extends PlaceSearchNetworkController
{
    public StaySearchNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAutoComplete(SaleTime saleTime, int stays, String keyword)
    {
        if (saleTime == null || stays == 0 || Util.isTextEmpty(keyword) == true)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestHotelSearchAutoCompleteList(mNetworkTag//
            , saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), stays, keyword, mHotelSearchAutoCompleteListener);
    }

    private DailyHotelJsonResponseListener mHotelSearchAutoCompleteListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int startIndex = url.lastIndexOf('=');

            List<Keyword> keywordList = null;

            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONArray jsonArray = response.getJSONArray("data");

                    int length = jsonArray.length();

                    keywordList = new ArrayList<>(length);

                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            keywordList.add(new Keyword(jsonArray.getJSONObject(i), PlaceSearchLayout.HOTEL_ICON));
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(URLDecoder.decode(url.substring(startIndex + 1)), keywordList);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAutoComplete(null, null);
        }
    };
}
