package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 9. 20..
 */
@Deprecated
public class RegisterCouponNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRegisterCoupon(boolean isSuccess, int msgCode, String message);
    }

    public void requestRegisterCoupon(String couponCode)
    {

        if (DailyTextUtils.isTextEmpty(couponCode))
        {
            mOnNetworkControllerListener.onErrorToastMessage(mContext.getString(R.string.toast_msg_register_coupon_empty_keyword));
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestRegisterKeywordCoupon(mNetworkTag, couponCode, mRegisterKeywordCouponCallback);
    }

    public RegisterCouponNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    private retrofit2.Callback mRegisterKeywordCouponCallback = new retrofit2.Callback<JSONObject>()
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
                    boolean isSuccess = msgCode == 100;
                    String message = responseJSONObject.getString("msg");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRegisterCoupon(isSuccess, msgCode, message);
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
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}
