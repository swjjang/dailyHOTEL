package com.twoheart.dailyhotel.screen.event;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventListNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void processEventPage(String eventUrl);

        void onEventListResponse(List<Event> eventList);
    }

    public EventListNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestEventList()
    {
        DailyNetworkAPI.getInstance(mContext).requestEventList(mNetworkTag, mDailyEventListJsonResponseListener);
    }

    public void requestEventPageUrl(Event event)
    {
        String store;

        if (Constants.RELEASE_STORE == Constants.Stores.PLAY_STORE)
        {
            store = "google";
        } else
        {
            store = "skt";
        }

        DailyNetworkAPI.getInstance(mContext).requestEventPageUrl(mNetworkTag, event.index, store, mDailyEventPageJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailyEventListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                } else
                {
                    JSONArray eventJSONArray = response.getJSONArray("data");

                    if (eventJSONArray == null)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                    } else
                    {
                        int length = eventJSONArray.length();

                        if (length == 0)
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                        } else
                        {
                            ArrayList<Event> eventList = new ArrayList<>(length);

                            for (int i = 0; i < length; i++)
                            {
                                eventList.add(new Event(eventJSONArray.getJSONObject(i)));
                            }

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(eventList);
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mDailyEventPageJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } else
                {
                    String eventUrl = response.getJSONObject("data").getString("url");
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).processEventPage(eventUrl);
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