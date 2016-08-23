package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

/**
 * Created by android_sam on 2016. 8. 22..
 */
public class GourmetCurationNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener {
        void onGourmetCount(String url, int gourmetCount);
    }

    public GourmetCurationNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }


    public void requestGourmetList(GourmetParams params)
    {
        if (params == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestGourmetList(mNetworkTag, params.toParamsString(), mGourmetListJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetCount(null, -1);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            int totalCount = 0;

            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    totalCount = dataJSONObject.getInt("gourmetSalesCount");
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onGourmetCount(url, totalCount);
        }
    };
}
