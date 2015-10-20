/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * CreditFragment (적립금 화면)
 * <p/>
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어
 * 해당 화면을 띄워주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.EventListLayout;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EventListFragment extends BaseFragment implements Constants
{
    private EventListLayout mEventListLayout;
    private Event mSelectedEvent;
    private Customer mUser;
    private DailyHotelJsonResponseListener mDailyEventListJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                if (null == response)
                {
                    throw new NullPointerException("response is null.");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");
                        DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
                    }

                    mEventListLayout.setData(null);
                } else
                {
                    JSONArray eventJSONArray = response.getJSONArray("data");

                    if (eventJSONArray == null)
                    {
                        mEventListLayout.setData(null);
                    } else
                    {
                        int length = eventJSONArray.length();

                        if (length == 0)
                        {
                            mEventListLayout.setData(null);
                        } else
                        {
                            ArrayList<Event> eventList = new ArrayList<Event>(length);

                            for (int i = 0; i < length; i++)
                            {
                                eventList.add(new Event(eventJSONArray.getJSONObject(i)));
                            }

                            mEventListLayout.setData(eventList);
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
                mEventListLayout.setData(null);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
                ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
                ed.putString(KEY_PREFERENCE_USER_ID, null);
                ed.putString(KEY_PREFERENCE_USER_PWD, null);
                ed.putString(KEY_PREFERENCE_USER_TYPE, null);
                ed.commit();

                unLockUI();
            } catch (JSONException e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {

        @Override
        public void onResponse(String url, String response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                // session alive
                // 사용자 정보 요청.
                mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, baseActivity));
            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인
                if (baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
                {
                    HashMap<String, String> params = Util.getLoginParams(baseActivity.sharedPreference);

                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNIN).toString(), params, mUserLoginJsonResponseListener, baseActivity));
                } else
                {
                    unLockUI();

                    // 이벤트 리스트 얻어오기
                    // 이벤트 요청 화면으로 이동
                    mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_LIST).toString(), null, mDailyEventListJsonResponseListener, baseActivity));
                }
            } else
            {
                onError();
            }
        }
    };
    private DailyHotelJsonResponseListener mDailyEventPageJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            unLockUI();

            try
            {
                if (null == response)
                {
                    throw new NullPointerException("response is null.");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String message = response.getString("msg");
                        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null);
                    }
                } else
                {
                    String eventUrl = response.getJSONObject("data").getString("url");

                    Intent intent = new Intent(baseActivity, EventWebActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, eventUrl);
                    startActivity(intent);
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }
    };
    private EventListFragment.OnUserActionListener mOnUserActionListener = new EventListFragment.OnUserActionListener()
    {
        @Override
        public void onEventClick(Event event)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            mSelectedEvent = event;

            // 로그인 상태 체크.
            if (baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false) && mUser != null)
            {
                if (Util.isTextEmpty(mUser.getAccessToken()) == false //
                    && (Util.isTextEmpty(mUser.getEmail()) == true || Util.isTextEmpty(mUser.getName()) == true || Util.isTextEmpty(mUser.getPhone()) == true))
                {
                    Intent intent = new Intent(baseActivity, SignupActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, mUser);

                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } else
                {
                    requestEvent(mSelectedEvent, mUser.getUserIdx());
                    mSelectedEvent = null;
                }
            } else
            {
                // 로그인이 되어있지 않으면 회원 가입으로 이동
                Intent intent = new Intent(baseActivity, LoginActivity.class);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
        }
    };
    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                if (null == response)
                {
                    throw new NullPointerException("response is null.");
                }

                mUser.setEmail(response.getString("email"));
                mUser.setName(response.getString("name"));
                mUser.setPhone(response.getString("phone"));
                mUser.setAccessToken(response.getString("accessToken"));
                mUser.setUserIdx(response.getString("idx"));

                if (mSelectedEvent == null)
                {
                    String params = String.format("?user_idx=%s", mUser.getUserIdx());
                    mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_LIST).append(params).toString(), null, mDailyEventListJsonResponseListener, baseActivity));

                } else
                {
                    if (Util.isTextEmpty(mUser.getAccessToken()) == false //
                        && (Util.isTextEmpty(mUser.getEmail()) == true || Util.isTextEmpty(mUser.getName()) == true || Util.isTextEmpty(mUser.getPhone()) == true))
                    {
                        Intent intent = new Intent(baseActivity, SignupActivity.class);
                        intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, mUser);

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
                        baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    } else
                    {
                        requestEvent(mSelectedEvent, mUser.getUserIdx());
                        mSelectedEvent = null;
                    }
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mEventListLayout = new EventListLayout(getActivity());
        mEventListLayout.setOnUserActionListener(mOnUserActionListener);

        mUser = new Customer();

        return mEventListLayout.createView(inflater, container, savedInstanceState);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Action Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.CREDIT);

        super.onStart();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        // ActionBar Setting
        baseActivity.setActionBar(getString(R.string.actionbar_title_event_list_frag), false);

        lockUI();
        mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        unLockUI();
        releaseUiComponent();
        baseActivity.releaseUiComponent();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
            {
                if (resultCode == Activity.RESULT_OK)
                {

                } else
                {
                    mSelectedEvent = null;
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestEvent(Event event, String userIndex)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing())
        {
            return;
        }

        lockUI();

        String params;

        if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
        {
            params = String.format("?user_idx=%s&daily_event_idx=%d&store_type=%s", userIndex, event.index, "google");
        } else
        {
            params = String.format("?user_idx=%s&daily_event_idx=%d&store_type=%s", userIndex, event.index, "skt");
        }

        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_PAGE).append(params).toString(), null, mDailyEventPageJsonResponseListener, baseActivity));
    }

    public interface OnUserActionListener
    {
        public void onEventClick(Event event);
    }
}