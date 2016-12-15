package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
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

import retrofit2.Call;
import retrofit2.Response;

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
        DailyMobileAPI.getInstance(mContext).requestRefund(mNetworkTag, hotelIdx, dateCheckIn, transactionType//
            , hotelReservationIdx, reasonCancel, null, null, null, mRefundCallback);
    }

    public void requestRefund(int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode)
    {
        DailyMobileAPI.getInstance(mContext).requestRefund(mNetworkTag, hotelIdx, dateCheckIn, transactionType//
            , hotelReservationIdx, reasonCancel, accountHolder, bankAccount, bankCode, mRefundCallback);
    }

    private DailyHotelJsonResponseListener mBankListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONArray jsonArray = response.getJSONArray("data");
                    int length = jsonArray.length();

                    List<Bank> bankList = new ArrayList<>(length);

                    for (int i = 0; i < length; i++)
                    {
                        bankList.add(new Bank(jsonArray.getJSONObject(i)));
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onBankList(bankList);
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

    private retrofit2.Callback mRefundCallback = new retrofit2.Callback<JSONObject>()
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
                    String message = null;
                    boolean readyForRefund = false;

                    // msgCode 1013: 환불 요청 중 실패한 것으로 messageFromPg를 사용자에게 노출함.
                    // msgCode 1014: 무료 취소 횟수를 초과한 것으로 msg 내용을 사용자에게 노출함.
                    // msgCode 1015: 환불 수동 스위치 ON일 경우
                    switch (msgCode)
                    {
                        case 1014:
                            message = responseJSONObject.getString("msg");
                            break;

                        case 1013:
                        case 1015:
                        default:
                            if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
                            {
                                JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                                message = dataJSONObject.getString("messageFromPg");

                                readyForRefund = dataJSONObject.getBoolean("readyForRefund");
                            }

                            if (Util.isTextEmpty(message) == true)
                            {
                                message = responseJSONObject.getString("msg");
                            }
                            break;
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onRefundResult(msgCode, message, readyForRefund);
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
