package com.twoheart.dailyhotel.screen.home.category.filter;

import android.content.Context;

import com.twoheart.dailyhotel.model.StayCategoryParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 7. 1..
 */

@Deprecated
public class StayCategoryCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int hotelSaleCount);
    }

    public StayCategoryCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayCategoryList(StayCategoryParams params, String abTestType)
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
                int hotelSaleCount;

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        hotelSaleCount = dataJSONObject.getInt("hotelSalesCount");
                    } else
                    {
                        hotelSaleCount = 0;
                    }
                } catch (Exception e)
                {
                    hotelSaleCount = 0;
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(call.request().url().toString(), hotelSaleCount);
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
