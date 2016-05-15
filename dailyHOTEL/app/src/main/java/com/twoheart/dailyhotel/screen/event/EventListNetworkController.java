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

public class EventListNetworkController extends BaseNetworkController
{
    private OnNetworkControllerListener mListener;

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void processEventPage(String eventUrl);

        void onEventListResponse(List<Event> eventList);
    }

    public EventListNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);

        mListener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mListener.onErrorResponse(volleyError);
    }

    public void requestEventList()
    {
        DailyNetworkAPI.getInstance(mContext).requestEventList(mNetworkTag, mDailyEventListJsonResponseListener, this);
    }

    public void requestEventPageUrl(Event event)
    {
        String store;

        if (Constants.RELEASE_STORE == Constants.Stores.PLAY_STORE || Constants.RELEASE_STORE == Constants.Stores.N_STORE)
        {
            store = "google";
        } else
        {
            store = "skt";
        }

        DailyNetworkAPI.getInstance(mContext).requestEventPageUrl(mNetworkTag, event.index, store, mDailyEventPageJsonResponseListener, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailyEventListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");

                        mListener.onErrorPopupMessage(msgCode, message);
                    }

                    mListener.onEventListResponse(null);
                } else
                {
                    JSONArray eventJSONArray = response.getJSONArray("data");

                    if (eventJSONArray == null)
                    {
                        mListener.onEventListResponse(null);
                    } else
                    {
                        int length = eventJSONArray.length();

                        if (length == 0)
                        {
                            mListener.onEventListResponse(null);
                        } else
                        {
                            ArrayList<Event> eventList = new ArrayList<>(length);

                            for (int i = 0; i < length; i++)
                            {
                                eventList.add(new Event(eventJSONArray.getJSONObject(i)));
                            }

                            mListener.onEventListResponse(eventList);
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
                mListener.onEventListResponse(null);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyEventPageJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");
                        mListener.onErrorPopupMessage(msgCode, message);
                    }
                } else
                {
                    String eventUrl = response.getJSONObject("data").getString("url");
                    mListener.processEventPage(eventUrl);
                }
            } catch (Exception e)
            {
                mListener.onError(e);
            }
        }
    };
}