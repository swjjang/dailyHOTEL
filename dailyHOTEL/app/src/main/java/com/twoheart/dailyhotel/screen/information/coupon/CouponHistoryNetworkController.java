package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;

import com.twoheart.dailyhotel.model.CouponHistory;
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
public class CouponHistoryNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCouponHistoryList(List<CouponHistory> list);

    }

    public void requestCouponHistoryList()
    {
        DailyMobileAPI.getInstance(mContext).requestCouponHistoryList(mNetworkTag, mCouponHistoryCallback);
    }

    public CouponHistoryNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    private retrofit2.Callback mCouponHistoryCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    ArrayList<CouponHistory> list = new ArrayList<>();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        try
                        {
                            list = CouponUtil.getCouponHistoryList(responseJSONObject);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onCouponHistoryList(list);
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
