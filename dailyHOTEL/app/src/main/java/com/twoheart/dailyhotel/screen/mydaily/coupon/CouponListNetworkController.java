package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.CouponUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
@Deprecated
public class CouponListNetworkController extends BaseNetworkController
{

    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponList(List<Coupon> list);

        /**
         * 쿠폰 다운로드 결과
         *
         * @param couponCode 사용자 쿠폰 고유코드
         */
        void onDownloadCoupon(String couponCode);
    }

    /**
     * 소유자의 전체 쿠폰리스트
     */
    public void requestCouponList()
    {
        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, mCouponListCallback);
    }

    public void requestDownloadCoupon(Coupon coupon)
    {
        if (coupon == null)
        {
            ExLog.e("coupon is null");
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.couponCode, mDownloadCouponCallback);
    }

    public CouponListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    private retrofit2.Callback mCouponListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    ArrayList<Coupon> list = new ArrayList<>();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        try
                        {
                            list = CouponUtil.getCouponList(responseJSONObject);
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }

                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onCouponList(list);
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

    private retrofit2.Callback mDownloadCouponCallback = new retrofit2.Callback<JSONObject>()
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
                        String couponCode = call.request().url().queryParameter("couponCode");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(couponCode);
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
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}
