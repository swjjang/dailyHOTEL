package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.util.Map;

public class StaySearchResultCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onStayCount(String url, int totalCount, int maxCount);
    }

    public StaySearchResultCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStaySearchList(StayParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestStaySearchList(mNetworkTag, params.toParamsString(), mStayListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DailyHotelJsonResponseListener mStayListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(null, -1, -1);
        }

        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            int totalCount = 0;
            int maxCount = 0;

            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    totalCount = dataJSONObject.getInt("hotelSalesCount");
                    maxCount = dataJSONObject.getInt("searchMaxCount");
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onStayCount(url, totalCount, maxCount);
        }
    };
}
