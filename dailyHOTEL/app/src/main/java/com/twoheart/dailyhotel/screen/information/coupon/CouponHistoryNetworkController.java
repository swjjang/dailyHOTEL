package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.CouponHistory;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.CouponUtil;

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
        void onCouponHistoryList(List<CouponHistory> list);

    }

    public void requestCouponHistoryList()
    {
        DailyNetworkAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, mCouponHistoryJsonResponseListener);
    }

    public CouponHistoryNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    DailyHotelJsonResponseListener mCouponHistoryJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                ArrayList<CouponHistory> list = new ArrayList<>();

                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    list = CouponUtil.getCouponHistoryList(response);

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
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}
