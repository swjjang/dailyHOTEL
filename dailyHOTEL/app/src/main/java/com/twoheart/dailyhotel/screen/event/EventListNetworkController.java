package com.twoheart.dailyhotel.screen.event;

import android.content.Context;

import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
        DailyMobileAPI.getInstance(mContext).requestEventList(mNetworkTag, mDailyEventListCallback);
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

        DailyMobileAPI.getInstance(mContext).requestEventPageUrl(mNetworkTag, event.index, store, mDailyEventPageJsonResponseListener);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mDailyEventListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode != 0)
                    {
                        if (responseJSONObject.has("msg") == true)
                        {
                            String message = responseJSONObject.getString("msg");

                            mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                    } else
                    {
                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");

                        if (dataJSONArray == null)
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                        } else
                        {
                            int length = dataJSONArray.length();

                            if (length == 0)
                            {
                                ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventListResponse(null);
                            } else
                            {
                                ArrayList<Event> eventList = new ArrayList<>(length);

                                for (int i = 0; i < length; i++)
                                {
                                    eventList.add(new Event(dataJSONArray.getJSONObject(i)));
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

    private retrofit2.Callback mDailyEventPageJsonResponseListener = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode != 0)
                    {
                        if (responseJSONObject.has("msg") == true)
                        {
                            String message = responseJSONObject.getString("msg");
                            mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                        }
                    } else
                    {
                        String eventUrl = responseJSONObject.getJSONObject("data").getString("url");
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).processEventPage(eventUrl);
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