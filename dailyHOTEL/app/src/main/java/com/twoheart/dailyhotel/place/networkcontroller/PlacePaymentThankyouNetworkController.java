package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class PlacePaymentThankyouNetworkController extends BaseNetworkController
{
    public PlacePaymentThankyouNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserTracking(int hotelPaymentCompletedCount, int gourmetPaymentCompletedCount);
    }

    public void requestUserTracking()
    {
        DailyMobileAPI.getInstance(mContext).requestUserTracking(mNetworkTag, mUserTrackingCallback);
    }

    private retrofit2.Callback mUserTrackingCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                    JSONObject tracking = dataJSONObject.getJSONObject("tracking");

                    // 서버시간은 사용안함
                    //                String serverDate = data.getString("serverDate");

                    int gourmetPaymentCompletedCount = tracking.getInt("countOfGourmetPaymentCompleted");

                    int hotelPaymentCompletedCount = tracking.getInt("countOfHotelPaymentCompleted");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserTracking(//
                        hotelPaymentCompletedCount, gourmetPaymentCompletedCount);
                } catch (Exception e)
                {
                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };
}
