package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 9. 20..
 */
public class RegisterCouponNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRegisterCoupon(String couponCode);
    }

    public void requestRegisterCoupon(String couponCode)
    {

        if (Util.isTextEmpty(couponCode))
        {
            ExLog.e("couponCode is empty");
            return;
        }

//        DailyNetworkAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.userCouponCode, mJsonResponseListener);
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

                if (msgCode == 100)
                {
                    Uri uri = Uri.parse(url);
                    String userCouponCode = uri.getQueryParameter("userCouponCode");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRegisterCoupon(userCouponCode);
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
