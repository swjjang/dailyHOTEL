package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 7. 1..
 */
public class StayCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int hotelSaleCount);
    }

    public StayCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayList(StayParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestStayList(mNetworkTag, params.toParamsString(), mStayListCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private retrofit2.Callback mStayListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                int hotelSaleCount;

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");
                    } else
                    {
                        hotelSaleCount = 0;
                    }
                } catch (Exception e)
                {
                    hotelSaleCount = 0;
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(call.request().url().toString(), hotelSaleCount);
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(null, -1);
        }
    };
}
