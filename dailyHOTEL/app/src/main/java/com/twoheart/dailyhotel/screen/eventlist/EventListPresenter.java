package com.twoheart.dailyhotel.screen.eventlist;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventListPresenter implements Response.ErrorListener
{
    private BaseActivity mBaseActivity;
    private EventListActivity.OnResponsePresenterListener mListener;

    public EventListPresenter(BaseActivity baseActivity, EventListActivity.OnResponsePresenterListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mListener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mListener.onErrorResponse(volleyError);
    }

    public void requestEventList()
    {
        DailyNetworkAPI.getInstance().requestEventList(mBaseActivity.getNetworkTag(), mDailyEventListJsonResponseListener, this);
    }

    public void requestUserAlive()
    {
        DailyNetworkAPI.getInstance().requestUserAlive(mBaseActivity.getNetworkTag(), mUserAliveStringResponseListener, this);
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

        DailyNetworkAPI.getInstance().requestEventPageUrl(mBaseActivity.getNetworkTag(), userIndex, event.index, store, mDailyEventPageJsonResponseListener, this);
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
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");
                        mListener.onInternalError(message);
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

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();
                        requestUserAlive();
                        return;
                    }
                }

                mListener.onSignin();
            } catch (Exception e)
            {
                mListener.onInternalError();
            }
        }
    };

    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                // session alive
                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance().requestUserInformationEx(mBaseActivity.getNetworkTag(), mUserInformationJsonResponseListener, EventListPresenter.this);
            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인
                if (DailyPreference.getInstance(mBaseActivity).isAutoLogin() == true)
                {
                    HashMap<String, String> params = Util.getLoginParams(mBaseActivity);
                    DailyNetworkAPI.getInstance().requestUserSignin(mBaseActivity.getNetworkTag(), params, mUserLoginJsonResponseListener, EventListPresenter.this);
                } else
                {
                    mListener.onSignin();
                }
            } else
            {
                mListener.onInternalError();
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
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");
                        mListener.onInternalError(message);
                    }
                } else
                {
                    String eventUrl = response.getJSONObject("data").getString("url");
                    mListener.processEventPage(eventUrl);
                }
            } catch (Exception e)
            {
                mListener.onInternalError();
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
                mListener.onInternalError();
            }
        }
    };
}