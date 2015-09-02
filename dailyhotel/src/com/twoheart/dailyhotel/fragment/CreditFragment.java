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
package com.twoheart.dailyhotel.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.CreditListActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 적립금 확인 페이지.
 * 
 * @author jangjunho
 *
 */
public class CreditFragment
		extends BaseFragment implements Constants, OnClickListener
{
	private RelativeLayout rlCreditNotLoggedIn;
	private LinearLayout llCreditLoggedIn, btnInvite;
	private TextView btnLogin, btnSignup;
	private TextView tvBonus, tvRecommenderCode;
	private TextView tvCredit;
	private String mRecommendCode;
	private ArrayList<Credit> mCreditList;
	private String mUserName;
	private String idx;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_credit, container, false);
		view.setPadding(0, Util.dpToPx(container.getContext(), 56) + 1, 0, 0);

		rlCreditNotLoggedIn = (RelativeLayout) view.findViewById(R.id.rl_credit_not_logged_in);
		llCreditLoggedIn = (LinearLayout) view.findViewById(R.id.ll_credit_logged_in);

		btnInvite = (LinearLayout) view.findViewById(R.id.btn_credit_invite_frd);
		tvCredit = (TextView) view.findViewById(R.id.tv_credit_history);
		tvRecommenderCode = (TextView) view.findViewById(R.id.tv_credit_recommender_code);
		tvBonus = (TextView) view.findViewById(R.id.tv_credit_money);
		btnLogin = (TextView) view.findViewById(R.id.btn_no_login_login);
		btnSignup = (TextView) view.findViewById(R.id.btn_no_login_signup);

		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);
		btnInvite.setOnClickListener(this);
		tvCredit.setOnClickListener(this);

		//영어버전에서만 텍스트 사이의 패딩 값을 주기위해 새로운 텍스트뷰를 보여줌
		TextView line1_4 = (TextView) view.findViewById(R.id.act_credit_line1_4);
		line1_4.setVisibility(View.GONE);

		tvCredit.setPaintFlags(tvCredit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // underlining

		return view;
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
		baseActivity.setActionBar(getString(R.string.actionbar_title_credit_frag), false);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));
	}

	@Override
	public void onClick(View v)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (v.getId() == btnInvite.getId())
		{
			try
			{
				String userIdxStr = idx;

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
				Date date = new Date();
				String strDate = dateFormat.format(date);

				String msg = getString(R.string.kakaolink_msg_invited_friend, mUserName, mUserName, mRecommendCode);
				KakaoLinkManager.newInstance(getActivity()).sendInviteMsgKakaoLink(msg);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("userId", userIdxStr);
				params.put("datetime", strDate);

				AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.INVITE_KAKAO_FRIEND, params);
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		} else if (v.getId() == tvCredit.getId())
		{
			Intent i = new Intent(baseActivity, CreditListActivity.class);
			i.putParcelableArrayListExtra(CreditListActivity.KEY_BUNDLE_ARGUMENTS_CREDITLIST, mCreditList);
			startActivity(i);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

			AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.VIEW_CREDIT_HISTORY, 0L);
		} else if (v.getId() == btnLogin.getId())
		{
			Intent i = new Intent(baseActivity, LoginActivity.class);
			startActivity(i);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

			AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.LOGIN, 0L);
		} else if (v.getId() == btnSignup.getId())
		{
			Intent i = new Intent(baseActivity, SignupActivity.class);
			startActivity(i);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

			AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.SIGNUP, 0L);
		}
	}

	private void loadLoginProcess(boolean loginSuccess)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (loginSuccess)
		{
			rlCreditNotLoggedIn.setVisibility(View.GONE);
			llCreditLoggedIn.setVisibility(View.VISIBLE);
		} else
		{
			rlCreditNotLoggedIn.setVisibility(View.VISIBLE);
			llCreditLoggedIn.setVisibility(View.GONE);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
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

				mRecommendCode = response.getString("rndnum");
				tvRecommenderCode.setText(response.getString("rndnum"));
				mUserName = response.getString("name");

				idx = response.getString("idx");

				// 적립금 목록 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_BONUS_ALL).toString(), null, mUserBonusAllResponseListener, baseActivity));

			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserBonusAllResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (getActivity() == null)
			{
				return;
			}

			//적립금 내역리스트 
			try
			{
				if (null == response)
				{
					throw new NullPointerException("response is null.");
				}

				if (null == mCreditList)
				{
					mCreditList = new ArrayList<Credit>();
				}

				mCreditList.clear();

				JSONArray jsonArray = response.getJSONArray("history");
				int length = jsonArray.length();

				for (int i = 0; i < length; i++)
				{
					JSONObject historyObj = jsonArray.getJSONObject(i);

					String content = historyObj.getString("content");
					String expires = historyObj.getString("expires");
					int bonus = historyObj.getInt("bonus");

					mCreditList.add(new Credit(content, bonus, expires));
				}

				loadLoginProcess(true);
				unLockUI();

			} catch (Exception e)
			{
				onError(e);
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
					loadLoginProcess(false);

				} else
				{
					VolleyHttpClient.createCookie();

					// credit 요청
					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SAVED_MONEY).toString(), null, mReserveSavedMoneyStringResponseListener, baseActivity));

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
			{ // session alive
				// credit 요청
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SAVED_MONEY).toString(), null, mReserveSavedMoneyStringResponseListener, baseActivity));

			} else if (true == "dead".equalsIgnoreCase(result))
			{ // session dead

				// 재로그인
				if (true == baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
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
					loadLoginProcess(false);
				}

			} else
			{
				onError();
			}
		}
	};

	private DailyHotelStringResponseListener mReserveSavedMoneyStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				String result = null;

				if (false == TextUtils.isEmpty(response))
				{
					result = response.trim();
				}

				DecimalFormat comma = new DecimalFormat("###,##0");
				String str = comma.format(Integer.parseInt(result));

				tvBonus.setText(new StringBuilder(str).append(Html.fromHtml(getString(R.string.currency))));

				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, baseActivity));

			} catch (Exception e)
			{
				onError(e);
			}

		}
	};
}