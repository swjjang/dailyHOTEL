package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 9. 20..
 */
public class RegisterCouponNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRegisterCoupon(String couponCode, boolean isSuccess, String message);
    }

    public void requestRegisterCoupon(String couponCode)
    {

        if (Util.isTextEmpty(couponCode))
        {
            mOnNetworkControllerListener.onErrorToastMessage(mContext.getString(R.string.toast_msg_register_coupon_empty_keyword));
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestRegistKeywordCoupon(mNetworkTag, couponCode, mJsonResponseListener);
    }

    public RegisterCouponNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    DailyHotelJsonResponseListener mJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                boolean isSuccess = msgCode == 100 ? true : false;
                String message = response.getString("msg");

                Uri uri = Uri.parse(url);
                String userCouponCode = uri.getQueryParameter("keyword");

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onRegisterCoupon(userCouponCode, isSuccess, message);
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
