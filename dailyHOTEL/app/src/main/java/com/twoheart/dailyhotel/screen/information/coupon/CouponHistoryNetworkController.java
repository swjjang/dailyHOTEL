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
public class CouponHistoryNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponHistoryList(List<Coupon> list);

    }

    public void requestCouponHistoryList() {
        DailyNetworkAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, mCouponHistoryJsonResponseListener, this);
    }

    private ArrayList<Coupon> makeCouponHistoryList(JSONArray jsonArray) throws JSONException
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

            Coupon coupon = makeCouponHistory(jsonObject);
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

    private Coupon makeCouponHistory(JSONObject jsonObject)
    {
        Coupon coupon = null;
        try
        {
            String code = jsonObject.getString("code"); // 쿠폰 별칭 코드
            Long validFrom = jsonObject.getLong("validFrom"); // 쿠폰 시작 시간
            Long validTo = jsonObject.getLong("validTo"); // 유효기간, 만료일, 쿠폰 만료시간
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


    public CouponHistoryNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    DailyHotelJsonResponseListener mCouponHistoryJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONObject data = response.getJSONObject("data");
                    if (data != null)
                    {
                        JSONArray couponList = response.getJSONArray("coupons");

                        list = makeCouponHistoryList(couponList);

                    }

                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onCouponHistoryList(list);
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
}
