package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
public class CouponListNetworkController extends BaseNetworkController
{

    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponList(List<Coupon> list);

        /**
         * 쿠폰 다운로드 결과
         *
         * @param isSuccess 성공 여부
         */
        void onDownloadCoupon(boolean isSuccess, String userCouponCode);
    }

    /**
     * 소유자의 전체 쿠폰리스트
     */
    public void requestCouponList()
    {
        DailyNetworkAPI.getInstance(mContext).requestCouponList(mNetworkTag, mCouponListJsonResponseListener);
    }

    public void requestDownloadCoupon(Coupon coupon)
    {

        if (coupon == null)
        {
            ExLog.e("coupon is null");
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.userCouponCode, mDownloadJsonResponseListener);
    }

    public CouponListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    DailyHotelJsonResponseListener mCouponListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                ArrayList<Coupon> list = new ArrayList<>();
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    boolean hasData = response.has("data");

                    if (hasData == true)
                    {
                        JSONObject data = response.getJSONObject("data");
                        if (data != null)
                        {
                            list = Coupon.makeCouponList(data);
                        }

                    } else
                    {
                        ExLog.d("response has not data");
                    }

                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onCouponList(list);
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

    DailyHotelJsonResponseListener mDownloadJsonResponseListener = new DailyHotelJsonResponseListener()
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

                Uri uri = Uri.parse(url);
                String userCouponCode = uri.getQueryParameter("userCouponCode");

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(isSuccess, userCouponCode);

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
