package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StayAutoRefundNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onBankList(List<Bank> bankList);

        void onRefundResult(int msgCode, String message, boolean readyForRefund);
    }

    public StayAutoRefundNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestBankList()
    {
        DailyNetworkAPI.getInstance(mContext).requestBankList(mNetworkTag, mBankListJsonResponseListener);
    }

    public void requestRefund(int hotelIdx, String dateCheckIn, String transactionType//
        , int hotelReservationIdx, String reasonCancel)
    {
        DailyNetworkAPI.getInstance(mContext).requestRefund(mNetworkTag, hotelIdx, dateCheckIn, transactionType//
            , hotelReservationIdx, reasonCancel, null, null, null, mRefundJsonResponseListener);
    }

    public void requestRefund(int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode)
    {
        DailyNetworkAPI.getInstance(mContext).requestRefund(mNetworkTag, hotelIdx, dateCheckIn, transactionType//
            , hotelReservationIdx, reasonCancel, accountHolder, bankAccount, bankCode, mRefundJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mBankListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                List<Bank> bankList = new ArrayList<>(length);

                for (int i = 0; i < length; i++)
                {
                    bankList.add(new Bank(jsonArray.getJSONObject(i)));
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onBankList(bankList);
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

    private DailyHotelJsonResponseListener mRefundJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                String message = null;
                boolean readyForRefund = false;

                switch (msgCode)
                {
                    case 1014:
                        message = response.getString("msg");
                        break;

                    default:
                        if (response.has("data") == true && response.isNull("data") == false)
                        {
                            JSONObject dataJSONObject = response.getJSONObject("data");
                            message = dataJSONObject.getString("messageFromPg");

                            readyForRefund = dataJSONObject.getBoolean("readyForRefund");
                        }

                        if (Util.isTextEmpty(message) == true)
                        {
                            message = response.getString("msg");
                        }
                        break;
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onRefundResult(msgCode, message, readyForRefund);
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
