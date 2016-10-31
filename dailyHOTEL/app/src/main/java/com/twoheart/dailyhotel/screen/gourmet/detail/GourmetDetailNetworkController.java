package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.Map;

public class GourmetDetailNetworkController extends PlaceDetailNetworkController
{
    public interface OnNetworkControllerListener extends PlaceDetailNetworkController.OnNetworkControllerListener
    {
        void onGourmetDetailInformation(JSONObject dataJSONObject);

        void onHasCoupon(boolean hasCoupon);
    }

    public GourmetDetailNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetDetailInformation(String day, int index)
    {
        DailyNetworkAPI.getInstance(mContext).requestGourmetDetailInformation(mNetworkTag, //
            index, day, mGourmetDetailJsonResponseListener);
    }

    public void requestHasCoupon(int placeIndex, String date)
    {
//        DailyNetworkAPI.getInstance(mContext).requestHasCoupon(mNetworkTag, placeIndex, date, mHasCouponJsonResponseListener);

        ((OnNetworkControllerListener) mOnNetworkControllerListener).onHasCoupon(true);
    }

    private DailyHotelJsonResponseListener mGourmetDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                JSONObject dataJSONObject = null;

                if (response.has("data") == true && response.isNull("data") == false)
                {
                    dataJSONObject = response.getJSONObject("data");
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
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetDetailInformation(dataJSONObject);
                        break;

                    case 5:
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetDetailInformation(dataJSONObject);

                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");
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
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");
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
                    Crashlytics.log(url);
                }

                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
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
