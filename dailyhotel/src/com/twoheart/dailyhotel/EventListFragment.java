/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditFragment (적립금 화면)
 * 
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어 
 * 해당 화면을 띄워주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.ui.EventListLayout;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.DailyToast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class EventListFragment extends BaseFragment implements Constants
{
	private EventListLayout mEventListLayout;
	private Event mSelectedEvent;
	private int mUserIndex;

	public interface OnUserActionListener
	{
		public void onEventClick(Event event);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mEventListLayout = new EventListLayout(getActivity());
		mEventListLayout.setOnUserActionListener(mOnUserActionListener);

		return mEventListLayout.createView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart()
	{
		AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.CREDIT);

		super.onStart();
	}

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
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));
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

	private void requestEvent(Event event, int userIndex)
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
			params = String.format("?user_idx=%d&daily_event_idx=%d&store_type=%s", userIndex, event.index, "google");
		} else
		{
			params = String.format("?user_idx=%d&daily_event_idx=%d&store_type=%s", userIndex, event.index, "skt");
		}

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_PAGE).append(params).toString(), null, mDailyEventPageJsonResponseListener, baseActivity));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// User Action Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

			mSelectedEvent = event;

			// 로그인 상태 체크.
			if (baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
			{
				requestEvent(mSelectedEvent, mUserIndex);
				mSelectedEvent = null;
			} else
			{
				// 로그인이 되어있지 않으면 회원 가입으로 이동
				Intent intent = new Intent(baseActivity, LoginActivity.class);
				startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
				baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
			}
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

				if (mSelectedEvent == null)
				{
					// 이벤트 요청 화면으로 이동
					mUserIndex = response.getInt("idx");

					String params = String.format("?user_idx=%d", mUserIndex);
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_LIST).append(params).toString(), null, mDailyEventListJsonResponseListener, baseActivity));

				} else
				{
					requestEvent(mSelectedEvent, mUserIndex);
					mSelectedEvent = null;
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

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
				if (!response.getBoolean("login"))
				{
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

					unLockUI();
				} else
				{
					VolleyHttpClient.createCookie();

				}
			} catch (JSONException e)
			{
				onError(e);
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

			if (false == TextUtils.isEmpty(response))
			{
				result = response.trim();
			}

			if (true == "alive".equalsIgnoreCase(result))
			{
				// session alive
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, baseActivity));
			} else if (true == "dead".equalsIgnoreCase(result))
			{
				// session dead
				// 재로그인
				if (baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
				{
					String id = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
					String accessToken = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					String pw = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);

					Map<String, String> loginParams = new HashMap<String, String>();

					if (null != accessToken)
					{
						loginParams.put("accessToken", accessToken);
					} else
					{
						loginParams.put("email", id);
					}

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, baseActivity));
				} else
				{
					unLockUI();

					// 이벤트 리스트 얻어오기
					// 이벤트 요청 화면으로 이동
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_LIST).toString(), null, mDailyEventListJsonResponseListener, baseActivity));
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
}