/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * SignupActivity (회원가입화면)
 * 
 * 새로운 사용자 가입하는 화면이다. 새로운 사용자로부터 이메일, 이름, 패스워드,
 * 추천인 코드를 입력받는다. 회원가입하는 웹서버 API를 이용한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

public class SignupActivity extends BaseActivity implements OnClickListener
{

	private EditText etEmail, etName, etPhone, etPwd, etRecommender;
	private TextView tvTerm, tvPrivacy;
	private TextView btnSignUp;

	private Map<String, String> signupParams;

	private MixpanelAPI mMixpanel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signup);
		setActionBar(R.string.actionbar_title_signup_activity);

		etPwd = (EditText) findViewById(R.id.et_signup_pwd);
		etEmail = (EditText) findViewById(R.id.et_signup_email);
		etRecommender = (EditText) findViewById(R.id.et_signup_recommender);
		etName = (EditText) findViewById(R.id.et_signup_name);
		etPhone = (EditText) findViewById(R.id.et_signup_phone);
		tvTerm = (TextView) findViewById(R.id.tv_signup_agreement);
		tvPrivacy = (TextView) findViewById(R.id.tv_signup_personal_info);
		btnSignUp = (TextView) findViewById(R.id.btn_signup);

		tvTerm.setOnClickListener(this);
		tvPrivacy.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);

		getPhoneNumber();

		mMixpanel = MixpanelAPI.getInstance(this, "791b366dadafcd37803f6cd7d8358373");
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	/**
	 * 기기의 번호를 받아옴
	 */
	public void getPhoneNumber()
	{
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNum = telManager.getLine1Number();

		if (TextUtils.isEmpty(phoneNum) == false)
		{
			etPhone.setText(phoneNum);
			etEmail.requestFocus();
		}

	}

	public boolean checkInput()
	{
		if (etEmail.getText().toString().equals(""))
			return false;
		else if (etName.getText().toString().equals(""))
			return false;
		else if (etPhone.getText().toString().equals(""))
			return false;
		else if (etPwd.getText().toString().equals(""))
			return false;
		else
			return true;
	}

	public boolean isValidEmail(String inputStr)
	{
		Pattern p = Pattern.compile("^[_a-zA-Z0-9-]+(.[_a-zA-Z0-9-]+)*@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	public boolean isValidPhone(String inputStr)
	{
		Pattern p = Pattern.compile("^(01[0|1|6|7|8|9])(\\d{4}|\\d{3})(\\d{4})$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	public boolean isVaildrecommend(String inputStr)
	{
		Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnSignUp.getId())
		{ // 회원가입

			// 필수 입력 check
			if (!checkInput())
			{
				DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
				return;
			}

			// email check
			if (!isValidEmail(etEmail.getText().toString()))
			{
				DailyToast.showToast(SignupActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
				return;
			}

			if (etPwd.length() < 4)
			{
				DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_password_more_than_4chars, Toast.LENGTH_SHORT);
				return;
			}
			lockUI();

			signupParams = new HashMap<String, String>();
			signupParams.put("email", etEmail.getText().toString());
			signupParams.put("pw", etPwd.getText().toString());
			signupParams.put("name", etName.getText().toString());
			signupParams.put("phone", etPhone.getText().toString());

			TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			signupParams.put("device", tManager.getDeviceId());
			signupParams.put("marketType", RELEASE_STORE.getName());

			String recommender = etRecommender.getText().toString().trim();
			if (!recommender.equals(""))
				signupParams.put("recommender", recommender);

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNUP).toString(), signupParams, mUserSignupJsonResponseListener, this));

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestSignup", null, null);

		} else if (v.getId() == tvTerm.getId())
		{ // 이용약관

			Intent i = new Intent(this, TermActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == tvPrivacy.getId())
		{ // 개인정보 취급

			Intent i = new Intent(this, PrivacyActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		}
	}

	public void storeLoginInfo()
	{

		String id = etEmail.getText().toString();
		String pwd = Crypto.encrypt(etPwd.getText().toString()).replace("\n", "");

		SharedPreferences.Editor ed = sharedPreference.edit();
		ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);
		ed.putString(KEY_PREFERENCE_USER_ID, id);
		ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
		ed.commit();

		setResult(RESULT_OK);
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy()
	{
		mMixpanel.flush();
		super.onDestroy();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				String result = response.getString("join");
				String msg = null;

				if (response.length() > 1)
				{
					msg = response.getString("msg");
				}

				if (result.equals("true") == true)
				{
					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("email", signupParams.get("email"));
					loginParams.put("pw", Crypto.encrypt(signupParams.get("pw")).replace("\n", ""));

					ExLog.d("email : " + loginParams.get("email") + " pw : " + loginParams.get("pw"));

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, SignupActivity.this));
				} else
				{
					unLockUI();
					DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_LONG);
				}

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
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				if (response.getBoolean("login") == true)
				{
					VolleyHttpClient.createCookie();
					unLockUI();
					//					showToast(getString(R.string.toast_msg_success_to_signup), Toast.LENGTH_LONG, false);

					storeLoginInfo();

					lockUI();
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, SignupActivity.this));

					//					finish();
				}
			} catch (Exception e)
			{
				onError(e);
			}

		}
	};

	private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{

			unLockUI();

			try
			{
				int userIdx = response.getInt("idx");
				String userIdxStr = String.format("%07d", userIdx);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
				Date date = new Date();
				String strDate = dateFormat.format(date);

				mMixpanel.getPeople().identify(userIdxStr);

				JSONObject props = new JSONObject();
				props.put("userId", userIdxStr);
				props.put("datetime", strDate);
				props.put("method", "email");
				mMixpanel.track("signup", props);

				DailyToast.showToast(SignupActivity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);
				finish();
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_USER_SIGNUP)) {
	//			try {
	//				JSONObject obj = response;
	//
	//				String result = obj.getString("join");
	//				String msg = null;
	//
	//				ExLog.d(response.toString());
	//				if (obj.length() > 1) msg = obj.getString("msg");
	//
	//				if (result.equals("true")) {
	//					ExLog.d("result? " + result);
	//					Map<String, String> loginParams = new HashMap<String, String>();
	//					loginParams.put("email", signupParams.get("email"));
	//					loginParams.put("pw", Crypto.encrypt(signupParams.get("pw")).replace("\n", ""));
	//					ExLog.d("email : " + loginParams.get("email") + " pw : " + loginParams.get("pw"));
	//					mQueue.add(new DailyHotelJsonRequest(
	//							Method.POST, new StringBuilder(
	//									URL_DAILYHOTEL_SERVER).append(
	//									URL_WEBAPI_USER_LOGIN)
	//									.toString(), loginParams,
	//							this, this));
	//				} else {
	//					unLockUI();
	//					showToast(msg, Toast.LENGTH_LONG, true);
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_WEBAPI_USER_LOGIN)) {
	//			
	//			try {
	//				ExLog.d(response.toString());
	//				if (response.getBoolean("login")) {
	//					VolleyHttpClient.createCookie();
	//					unLockUI();
	////					showToast(getString(R.string.toast_msg_success_to_signup), Toast.LENGTH_LONG, false);
	//					
	//					storeLoginInfo();
	//					
	//					lockUI();
	//					mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//							new StringBuilder(URL_DAILYHOTEL_SERVER)
	//					.append(URL_WEBAPI_USER_INFO).toString(), null, this, this));
	//					
	////					finish();
	//				} 
	//
	//			} catch (JSONException e) {
	//				onError(e);
	//			} 
	//		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
	//
	//			try {
	//				unLockUI();
	//				int userIdx = response.getInt("idx");
	//				String userIdxStr = String.format("%07d", userIdx);
	//				
	//				SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
	//				Date date = new Date();
	//				String strDate = dateFormat.format(date);
	//				
	//				mMixpanel.getPeople().identify(userIdxStr);
	//				
	//				JSONObject props = new JSONObject();
	//				props.put("userId", userIdxStr);
	//				props.put("datetime", strDate);
	//				props.put("method", "email");
	//				mMixpanel.track("signup", props);
	//				
	//				showToast(getString(R.string.toast_msg_success_to_signup), Toast.LENGTH_LONG, false);
	//				finish();
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//	}
}
