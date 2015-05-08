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

import com.android.volley.Request.Method;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

/**
 * 신용카드 등록하기.
 * 
 * @author sheldon
 *
 */
public class CreditCardFragment extends BaseFragment implements Constants
{
	private ViewGroup notLoginLayout, logingLayout;
	private TextView loginButton, signupButton;
	private TextView addCreditCardButton;
	private ArrayList<CreditCard> mCreditList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_creditcard, container, false);

		notLoginLayout = (ViewGroup) view.findViewById(R.id.notLoginLayout);
		logingLayout = (ViewGroup) view.findViewById(R.id.loginLayout);

		loginButton = (TextView) view.findViewById(R.id.loginButton);
		signupButton = (TextView) view.findViewById(R.id.singupButton);
		addCreditCardButton = (TextView) view.findViewById(R.id.addCreditCardButton);

		loginButton.setOnClickListener(mLoginClickListener);
		signupButton.setOnClickListener(mSignupClickListener);
		addCreditCardButton.setOnClickListener(mAddCreditCardClickListener);

		return view;
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
		baseActivity.setActionBar(getString(R.string.actionbar_title_creditcard_frag), false);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));

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
			notLoginLayout.setVisibility(View.GONE);
			logingLayout.setVisibility(View.VISIBLE);
			RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordScreen("creditWithLogon", "/credit-with-logon/");
		} else
		{
			notLoginLayout.setVisibility(View.VISIBLE);
			logingLayout.setVisibility(View.GONE);
			RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordScreen("creditWithLogoff", "/credit-with-logoff/");
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UI Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private View.OnClickListener mLoginClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}
			
			Intent intent = new Intent(baseActivity, LoginActivity.class);
			startActivity(intent);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	};
	
	private View.OnClickListener mSignupClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}
			
			Intent intent = new Intent(baseActivity, SignupActivity.class);
			startActivity(intent);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	};
	
	private View.OnClickListener mAddCreditCardClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}
			
//			Intent intent = new Intent(baseActivity, AddCreditCardActivity.class);
//			startActivity(intent);
//			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	};
	
	

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Network Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////



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
					// credit card 요청
//					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_SAVED_MONEY).toString(), null, mReserveSavedMoneyStringResponseListener, baseActivity));

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
				VolleyHttpClient.createCookie();
				
				// session alive
				// credit card 요청
				// 목록 요청.
				
				loadLoginProcess(true);
				unLockUI();
				
//				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_SAVED_MONEY).toString(), null, mReserveSavedMoneyStringResponseListener, baseActivity));

			} else if (true == "dead".equalsIgnoreCase(result))
			{ 
				// session dead
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

	private class CreditCard
	{
		private final String mName;
		private final String mNumber;
		
		public CreditCard(String name, String number)
		{
			mName = name;
			mNumber = number;
		}
	}
}