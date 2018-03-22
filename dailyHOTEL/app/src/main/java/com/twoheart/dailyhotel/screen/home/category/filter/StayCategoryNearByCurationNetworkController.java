package com.twoheart.dailyhotel.screen.home.category.filter;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.StayCategoryParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

@Deprecated
public class StayCategoryNearByCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int totalCount, int maxCount);
    }

    public StayCategoryNearByCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayCategoryNearByList(StayCategoryParams params, String abTestType)
    {
        if (params == null)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestStayCategoryList(mNetworkTag //
            , params.getCategoryCode(), params.toParamsMap() //
            , params.getBedTypeList(), params.getLuxuryList(), abTestType, mStayCategoryListCallback);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private retrofit2.Callback mStayCategoryListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                int totalCount = 0;
                int maxCount = 0;

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        totalCount = dataJSONObject.getInt("hotelSalesCount");
                        maxCount = dataJSONObject.getInt("searchMaxCount");
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(call.request().url().toString(), totalCount, maxCount);
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}
