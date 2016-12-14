package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StayDetailNetworkController extends PlaceDetailNetworkController
{
    public interface OnNetworkControllerListener extends PlaceDetailNetworkController.OnNetworkControllerListener
    {
        void onStaytDetailInformation(JSONObject dataJSONObject);

        void onHasCoupon(boolean hasCoupon);
    }

    public StayDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayDetailInformation(int placeIndex, String day, int nights)
    {
        DailyMobileAPI.getInstance(mContext).requestStayDetailInformation(mNetworkTag, placeIndex, //
            day, nights, mStayDetailInformationCallback);
    }

    public void requestHasCoupon(int placeIndex, String date, int nights)
    {
        DailyNetworkAPI.getInstance(mContext).requestHasCoupon(mNetworkTag, placeIndex, date, nights, mHasCouponJsonResponseListener);
    }

    private retrofit2.Callback mStayDetailInformationCallback = new retrofit2.Callback<JSONObject>()
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

                    JSONObject dataJSONObject = null;

                    if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
                    {
                        dataJSONObject = responseJSONObject.getJSONObject("data");
                    }

                    if (msgCode == 100 && dataJSONObject == null)
                    {
                        msgCode = 4;
                    }

                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    switch (msgCode)
                    {
                        case 100:
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStaytDetailInformation(dataJSONObject);
                            break;

                        case 5:
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStaytDetailInformation(dataJSONObject);

                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");
                                mOnNetworkControllerListener.onErrorPopupMessage(msgCode, msg);
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }

                        case 4:
                        default:
                        {
                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");
                                mOnNetworkControllerListener.onErrorToastMessage(msg);
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log(call.request().url().toString());
                    }

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

    private DailyHotelJsonResponseListener mHasCouponJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    boolean hasCoupon = dataJSONObject.getBoolean("existCoupons");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(hasCoupon);
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(false);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(false);
        }
    };
}
