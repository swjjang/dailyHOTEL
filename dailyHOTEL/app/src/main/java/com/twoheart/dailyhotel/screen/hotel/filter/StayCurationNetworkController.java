package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 7. 1..
 */
public class StayCurationNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int hotelSaleCount);
    }

    public StayCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayList(StayParams params)
    {
        DailyNetworkAPI.getInstance(mContext).requestStayList(mNetworkTag, params, mStayListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DailyHotelJsonResponseListener mStayListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(null, -1);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            int hotelSaleCount;

            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");

                } else
                {
                    hotelSaleCount = 0;
                }
            } catch (Exception e)
            {
                hotelSaleCount = 0;
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(url, hotelSaleCount);
        }
    };
}
