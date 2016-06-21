package com.twoheart.dailyhotel.place.networkcontroller;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONObject;

import java.util.List;

public abstract class PlaceMainNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onDateTime(long currentDateTime, long dailyDateTime);

        void onEventBanner(List<EventBanner> eventBannerList);

        void onRegionList();
    }

    public abstract void requestEventBanner();

    public abstract void requestRegionList();

    public PlaceMainNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestDateTime()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, mDateTimeJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            long currentDateTime;
            long dailyDateTime;

            try
            {
                currentDateTime = response.getLong("currentDateTime");
                dailyDateTime = response.getLong("dailyDateTime");

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onDateTime(currentDateTime, dailyDateTime);
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

    //    private DailyHotelJsonResponseListener mEventBannerListJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onErrorResponse(VolleyError volleyError)
    //        {
    //            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventBanner(null);
    //        }
    //
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            List<EventBanner> eventBannerList = PlaceEventBannerManager.makeEventBannerList(response);
    //
    //            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventBanner(eventBannerList);
    //        }
    //    };
}
