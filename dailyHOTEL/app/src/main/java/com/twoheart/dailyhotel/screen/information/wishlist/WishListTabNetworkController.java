package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class WishListTabNetworkController extends BaseNetworkController
{
    public WishListTabNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCommonDateTime(long currentDateTime, long dailyDateTime);

        void onWishListCount(int stayCount, int gourmetCount);
    }

    public void requestCommonDateTime()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonResponseListener);
    }

    public void requestWishListCount()
    {
        //        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mWishListCountJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
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

                    long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                    long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                    ((WishListTabNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, dailyDateTime);
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onError(new RuntimeException(message));
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

    private DailyHotelJsonResponseListener mWishListCountJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    //                    JSONObject dataJSONObject = response.getJSONObject("data");
                    //
                    //                    long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                    //                    long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                    ((WishListTabNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishListCount(0, 0);
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onError(new RuntimeException(message));
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
}
