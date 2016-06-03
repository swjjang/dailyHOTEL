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

    public void requestCouponHistoryList()
    {
        DailyNetworkAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, mCouponHistoryJsonResponseListener, this);
    }

    private ArrayList<Coupon> makeCouponHistoryList(JSONArray jsonArray, String serverDate) throws JSONException
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

            Coupon coupon = makeCouponHistory(jsonObject, serverDate);
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

    private Coupon makeCouponHistory(JSONObject jsonObject, String serverDate)
    {
        Coupon coupon = null;

        String code = null; // 쿠폰 별칭 코드
        String validFrom = null; // 쿠폰 시작 시간
        String validTo = null; // 유효기간, 만료일, 쿠폰 만료시간
        String title = null;
        String warning = null; // 유의사항
        int amount = 0; // 쿠폰가격
        int amountMinimum = 0; // 최소 결제 금액
        String isDownloaded = null; // 다운로드 여부 Y or N
        String availableItem = null; // 사용가능처
        String description = null; // ??? - CouponHistory
        boolean isInvalidDate = false; // 유효기간 만료 여부
        boolean isRedeemed = false; // 사용 여부
        String redeemedAt = null; // 사용한 날짜 (ISO-8601)

        try
        {
            if (jsonObject.has(Coupon.CODE))
            {
                code = jsonObject.getString(Coupon.CODE); // 쿠폰 별칭 코드
            }

            if (jsonObject.has(Coupon.VALID_FROM))
            {
                validFrom = jsonObject.getString(Coupon.VALID_FROM); // 쿠폰 시작 시간
            }

            if (jsonObject.has(Coupon.VALID_TO))
            {
                validTo = jsonObject.getString(Coupon.VALID_TO); // 유효기간, 만료일, 쿠폰 만료시간
            }

            if (jsonObject.has(Coupon.TITLE))
            {
                title = jsonObject.getString(Coupon.TITLE);
            }

            if (jsonObject.has(Coupon.WARNING))
            {
                warning = jsonObject.getString(Coupon.WARNING); // 유의사항
            }

            if (jsonObject.has(Coupon.AMOUNT))
            {
                amount = jsonObject.getInt(Coupon.AMOUNT); // 쿠폰가격
            }

            if (jsonObject.has(Coupon.AMOUNT_MINIMUM))
            {
                amountMinimum = jsonObject.getInt(Coupon.AMOUNT_MINIMUM); // 최소 결제 금액
            }

            if (jsonObject.has(Coupon.IS_DOWNLOADED))
            {
                isDownloaded = jsonObject.getString(Coupon.IS_DOWNLOADED); // 다운로드 여부 Y or N
            }

            if (jsonObject.has(Coupon.AVAILABLE_ITEM))
            {
                availableItem = jsonObject.getString(Coupon.AVAILABLE_ITEM); // 사용가능처
            }

            if (jsonObject.has(Coupon.DESCRIPTION))
            {
                description = jsonObject.getString(Coupon.DESCRIPTION); // ?? - CouponHistory
            }

            if (jsonObject.has(Coupon.IS_INVALID_DATE))
            {
                isInvalidDate = jsonObject.getBoolean(Coupon.IS_INVALID_DATE); // 유효기간 만료 여부
            }

            if (jsonObject.has(Coupon.IS_REDEEMED))
            {
                isRedeemed = jsonObject.getBoolean(Coupon.IS_REDEEMED); // 사용 여부
            }

            if (jsonObject.has(Coupon.REDEEMED_AT))
            {
                redeemedAt = jsonObject.getString(Coupon.REDEEMED_AT); // 사용한 날짜 (ISO-8601)
            }

            coupon = new Coupon(code, amount, title, validFrom, validTo, amountMinimum, //
                isDownloaded, availableItem, warning, serverDate, description, isInvalidDate, //
                isRedeemed, redeemedAt);

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
                    boolean hasData = response.has("data");

                    if (hasData == true)
                    {
                        JSONObject data = response.getJSONObject("data");
                        if (data != null)
                        {
                            String serverDate = "";

                            if (data.has(Coupon.SERVER_DATE) == true)
                            {
                                serverDate = data.getString(Coupon.SERVER_DATE);
                            }

                            JSONArray couponList = data.getJSONArray("coupons");

                            list = makeCouponHistoryList(couponList, serverDate);
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
