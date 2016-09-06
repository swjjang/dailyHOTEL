package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

public class PlacePaymentThankyouNetworkController extends BaseNetworkController
{
    public PlacePaymentThankyouNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserTracking(int hotelPaymentCompletedCount, int hotelUsedCount, int gourmetPaymentCompletedCount, int gourmetUsedCount);
    }

    public void requestUserTracking()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserTracking(mNetworkTag, mJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                JSONObject data = response.getJSONObject("data");
                JSONObject tracking = data.getJSONObject("tracking");

                // 서버시간은 사용안함
                //                String serverDate = data.getString("serverDate");

                int gourmetPaymentCompletedCount = tracking.getInt("countOfGourmetPaymentCompleted");
                int gourmetUsedCount = tracking.getInt("countOfGourmetUsed");

                int hotelPaymentCompletedCount = tracking.getInt("countOfHotelPaymentCompleted");
                int hotelUsedCount = tracking.getInt("countOfHotelUsed");

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserTracking(//
                    hotelPaymentCompletedCount, hotelUsedCount, gourmetPaymentCompletedCount, gourmetUsedCount);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

}
