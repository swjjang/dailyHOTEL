package com.twoheart.dailyhotel.screen.review;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

public class ReviewNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onAddReviewInformation(String grade);

        void onAddReviewDetailInformation();
    }

    public ReviewNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAddReviewInformation(JSONObject jsonObject)
    {
        DailyNetworkAPI.getInstance(mContext).requestAddReviewInformation(mNetworkTag, jsonObject, mAddReviewJsonResponseListener);
    }

    public void requestAddReviewDetailInformation(JSONObject jsonObject)
    {
        DailyNetworkAPI.getInstance(mContext).requestAddReviewDetailInformation(mNetworkTag, jsonObject, mAddReviewDetailJsonResponseListener);
    }

    private DailyJsonResponseListener mAddReviewJsonResponseListener = new DailyJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddReviewInformation(params.getString("grade"));
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

    private DailyJsonResponseListener mAddReviewDetailJsonResponseListener = new DailyJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onAddReviewDetailInformation();
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
