package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.CouponUtil;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
         * @param userCouponCode 사용자 고유 쿠폰코드
         */
        void onDownloadCoupon(String userCouponCode);
    }

    /**
     * 결제화면의 소유자의 전체 쿠폰리스트
     */
    public void requestCouponList(int ticketIdx, int ticketCount)
    {
        DailyMobileAPI.getInstance(mContext).requestCouponList(mNetworkTag, ticketIdx, ticketCount, mCouponListCallback);
    }

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

        DailyNetworkAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.userCouponCode, mDownloadJsonResponseListener);
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

    private DailyHotelJsonResponseListener mDownloadJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    Uri uri = Uri.parse(url);
                    String userCouponCode = uri.getQueryParameter("userCouponCode");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(userCouponCode);
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
