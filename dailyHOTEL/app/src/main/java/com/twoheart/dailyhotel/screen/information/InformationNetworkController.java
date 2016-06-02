package com.twoheart.dailyhotel.screen.information;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class InformationNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onUserInformation(String type, String email, String name, String recommender, int bonus, int couponTotalCount);
    }

    public InformationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }


    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, mUserInformationJsonResponseListener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    /**
     * 쿠폰 갯수 적립금 등
     */
    DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String email = response.getString("email");
                String name = response.getString("name");
                String ownRecommender = response.getString("rndnum"); // 자신의 추천 번호
                String userType = response.getString("user_type");
                int bonus = response.getInt("bonus");
                int couponTotalCount = response.getInt("coupon_total_count");

                ((OnNetworkControllerListener) mOnNetworkControllerListener) //
                    .onUserInformation(userType, email, name, ownRecommender, bonus, couponTotalCount);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            InformationNetworkController.this.onErrorResponse(volleyError);
        }
    };

    /**
     * 혜택 알림
     */
    DailyHotelJsonResponseListener mBenefitMessageJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                boolean isSuccess = false;

                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    isSuccess = true;
                }

                //                ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(isSuccess);

            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            InformationNetworkController.this.onErrorResponse(volleyError);
        }
    };
}
