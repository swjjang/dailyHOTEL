package com.twoheart.dailyhotel.screen.home;

import android.content.Context;

import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 1. 18..
 */

public class HomeNetworkController extends BaseNetworkController
{
    public HomeNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCommonDateTime(long currentDateTime, long dailyDateTime);
    }

    public void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonCallback);
    }

    private retrofit2.Callback mDateTimeJsonCallback = new retrofit2.Callback<JSONObject>()
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

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, dailyDateTime);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onError(new RuntimeException(message));
                    }
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
