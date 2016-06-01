package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
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
        void onDownloadCoupon(boolean isSuccess);
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

        DailyNetworkAPI.getInstance(mContext).requestDownloadCoupon(mNetworkTag, coupon.code, mDownloadJsonResponseListener);
    }

    private ArrayList<Coupon> makeCouponList(JSONArray jsonArray) throws JSONException
    {
        if (jsonArray == null)
        {
            return null;
        }

        ArrayList<Coupon> list = new ArrayList<>();

        int length = jsonArray.length();

        for (int i = 0; i < length; i++)
        {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Coupon coupon = makeCoupon(jsonObject);
            if (coupon == null)
            {
                ExLog.w("coupon is not create, index : " + i);
            } else
            {
                list.add(coupon);
            }
        }

        return list;
    }

    private Coupon makeCoupon(JSONObject jsonObject)
    {
        Coupon coupon = null;
        try
        {
            String code = jsonObject.getString("code"); // 쿠폰 별칭 코드
            String validFrom = jsonObject.getString("validFrom"); // 쿠폰 시작 시간
            String validTo = jsonObject.getString("validTo"); // 유효기간, 만료일, 쿠폰 만료시간
            String title = jsonObject.getString("title");
            String warning = jsonObject.getString("warning"); // 유의사항
            int amount = jsonObject.getInt("amount"); // 쿠폰가격
            int amountMinimum = jsonObject.getInt("amountMinimum"); // 최소 결제 금액
            String isDownloaded = jsonObject.getString("isDownloaded"); // 다운로드 여부 Y or N

            coupon = new Coupon(code, amount, title, validFrom, validTo, amountMinimum, isDownloaded, "사용가능처", warning);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return coupon;
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
                            JSONArray couponList = data.getJSONArray("coupons");

                            list = makeCouponList(couponList);
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

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onDownloadCoupon(isSuccess);

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
