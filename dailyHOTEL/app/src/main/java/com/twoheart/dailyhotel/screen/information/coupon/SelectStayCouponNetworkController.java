package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;

import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
public class SelectStayCouponNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponList(List<Coupon> list);

        /**
         * 쿠폰 다운로드 결과
         *
         * @param userCouponCode 사용자 고유 쿠폰코드
         */
        void onDownloadCoupon(String userCouponCode);
    }

    /**
     * 결제화면의 소유자의 전체 쿠폰리스트
     */
    public void requestCouponList(int hotelIdx, int roomIdx, String checkIn, String checkOut)
    {
        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, hotelIdx, roomIdx, checkIn, checkOut, mCouponListCallback);
    }

    /**
     * 상세화면의 호텔 쿠폰리스트
     *
     * @param placeIndex
     * @param date
     * @param nights
     */
    public void requestCouponList(int placeIndex, String date, int nights)
    {
        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, placeIndex, date, nights, mCouponListCallback);
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

        DailyMobileAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.userCouponCode, mDownloadJsonResponseListener);
    }

    public SelectStayCouponNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
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
                        ArrayList<Coupon> list = CouponUtil.getCouponList(responseJSONObject);
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
            mOnNetworkControllerListener.onError(t);
        }
    };

    private retrofit2.Callback mDownloadJsonResponseListener = new retrofit2.Callback<JSONObject>()
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
                        String userCouponCode = call.request().url().queryParameter("userCouponCode");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(userCouponCode);
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
