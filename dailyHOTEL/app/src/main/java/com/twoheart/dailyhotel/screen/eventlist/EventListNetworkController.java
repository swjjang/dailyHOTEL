package com.twoheart.dailyhotel.screen.eventlist;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventListNetworkController extends BaseNetworkController
{
    private OnNetworkControllerListener mListener;

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRequestEvent(String userIndex);

        void onUpdateUserInformation(Customer user, int recommender, boolean isDailyUser);

        void processEventPage(String eventUrl);

        void onSignin();

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
        DailyNetworkAPI.getInstance().requestEventList(mNetworkTag, mDailyEventListJsonResponseListener, this);
    }

    public void requestUserInformationEx()
    {
        DailyNetworkAPI.getInstance().requestUserInformationEx(mNetworkTag, mUserInformationJsonResponseListener, EventListNetworkController.this);
    }

    public void requestEventPageUrl(Event event, String userIndex)
    {
        String store;

        if (Constants.RELEASE_STORE == Constants.Stores.PLAY_STORE || Constants.RELEASE_STORE == Constants.Stores.N_STORE)
        {
            store = "google";
        } else
        {
            store = "skt";
        }

        DailyNetworkAPI.getInstance().requestEventPageUrl(mNetworkTag, userIndex, event.index, store, mDailyEventPageJsonResponseListener, this);
    }

    private boolean isEmptyTextField(String... fieldText)
    {
        for (int i = 0; i < fieldText.length; i++)
        {
            if (Util.isTextEmpty(fieldText[i]) == true)
            {
                return true;
            }
        }

        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailyEventListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
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

                        mListener.onErrorMessage(msgCode, message);
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
                            ArrayList<Event> eventList = new ArrayList<Event>(length);

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
                        mListener.onErrorMessage(msgCode, message);
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

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                JSONObject jsonObject = response.getJSONObject("data");

                Customer user = new Customer();
                user.setEmail(jsonObject.getString("email"));
                user.setName(jsonObject.getString("name"));
                user.setPhone(jsonObject.getString("phone"));
                user.setUserIdx(jsonObject.getString("idx"));

                // 추천인
                int recommender = jsonObject.getInt("recommender_code");
                boolean isDailyUser = jsonObject.getBoolean("is_daily_user");

                if (isEmptyTextField(new String[]{user.getEmail(), user.getPhone(), user.getName()}) == false && Util.isValidatePhoneNumber(user.getPhone()) == true)
                {
                    mListener.onRequestEvent(user.getUserIdx());
                } else
                {
                    // 정보 업데이트 화면으로 이동.
                    mListener.onUpdateUserInformation(user, recommender, isDailyUser);
                }
            } catch (Exception e)
            {
                mListener.onError(e);
            }
        }
    };
}