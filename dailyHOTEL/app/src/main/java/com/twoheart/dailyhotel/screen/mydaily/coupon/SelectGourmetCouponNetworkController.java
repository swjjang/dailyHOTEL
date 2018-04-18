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


public class SelectGourmetCouponNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponList(List<Coupon> list);

        /**
         * 쿠폰 다운로드 결과
         *
         * @param couponCode 사용자 고유 쿠폰코드
         */
        void onDownloadCoupon(String couponCode);
    }

    /**
     * 결제화면의 소유자의 전체 쿠폰리스트
     */
    //    public void requestCouponList(int ticketIdx, int ticketCount)
    //    {
    //        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, ticketIdx, ticketCount, mCouponListCallback);
    //    }

    /**
     * 상세화면의 쿠폰리스트
     *
     * @param placeIndex
     * @param date
     */
    public void requestCouponList(int placeIndex, String date)
    {
        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, placeIndex, date, mCouponListCallback);
    }

    /**
     * 쿠폰 다운로드 시도
     *
     * @param coupon
     */
    public void requestDownloadCoupon(Coupon coupon)
    {
        if (coupon == null)
        {
            ExLog.e("coupon is null");
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.couponCode, mDownloadCouponCallback);
    }

    public SelectGourmetCouponNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
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

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        ArrayList<Coupon> list = new ArrayList<>();

                        try
                        {
                            list = CouponUtil.getCouponList(responseJSONObject);
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCouponList(list);
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

    private retrofit2.Callback mDownloadCouponCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJONObject = response.body();

                    int msgCode = responseJONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        String couponCode = call.request().url().queryParameter("couponCode");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(couponCode);
                    } else
                    {
                        String message = responseJONObject.getString("msg");
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
