package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

public class GourmetSearchResultCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onGourmetCount(String url, int totalCount, int maxCount);
    }

    public GourmetSearchResultCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestGourmetSearchList(GourmetParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestGourmetSearchList(mNetworkTag, params.toParamsString(), mGourmetListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetCount(null, -1, -1);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            int totalCount = 0;
            int maxCount = 0;

            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    totalCount = dataJSONObject.getInt("searchCount");
                    maxCount = dataJSONObject.getInt("searchMaxCount");
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetCount(url, totalCount, maxCount);
        }
    };
}
