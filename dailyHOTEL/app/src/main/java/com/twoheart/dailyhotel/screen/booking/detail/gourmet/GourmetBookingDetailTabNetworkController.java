package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class GourmetBookingDetailTabNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onReviewInformation(Review review);
    }

    public GourmetBookingDetailTabNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestReviewInformation(int reserveIdx)
    {
//        DailyNetworkAPI.getInstance(mContext).requestReviewInformation(mNetworkTag, Constants.PlaceType.FNB, reserveIdx, mJsonResponseListener);
    }

    DailyHotelJsonResponseListener mJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");
                    Review review = new Review();
                    review.setData(jsonObject);

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(review);

//                } else if (msgCode == 701) {
//                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation();
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
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
