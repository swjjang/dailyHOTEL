package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;

import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class StayBookingDetailTabBookingNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onReviewInformation(Review review);
    }

    public StayBookingDetailTabBookingNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestReviewInformation(int reserveIdx)
    {
        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, reserveIdx, mStayReviewInformationCallback);
    }

    private retrofit2.Callback mStayReviewInformationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        Review review = new Review(dataJSONObject);

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(review);

                        //                } else if (msgCode == 701) {
                        //                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation();
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
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
