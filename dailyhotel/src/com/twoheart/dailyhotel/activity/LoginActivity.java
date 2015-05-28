/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.

 *
 * LoginActivity (로그인화면)
 * 
 * 사용자 계정 로그인을 담당하는 화면이다. 사용자로부터 아이디와 패스워드를
 * 입력받으며, 이를 로그인을 하는 웹서버 API를 이용한다. 
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LoginActivity extends BaseActivity implements Constants, OnClickListener, ErrorListener
{

	private EditText etId, etPwd;
	private SwitchCompat cbxAutoLogin;
	private TextView btnLogin;
	private TextView tvSignUp, tvForgotPwd;
	private LoginButton facebookLogin;

	private Map<String, String> loginParams;
	private Map<String, String> snsSignupParams;
	private Map<String, String> regPushParams;

	public Session fbSession;

	private MixpanelAPI mMixpanel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setActionBar(R.string.actionbar_title_login_activity);

		etId = (EditText) findViewById(R.id.et_login_id);
		etPwd = (EditText) findViewById(R.id.et_login_pwd);
		cbxAutoLogin = (SwitchCompat) findViewById(R.id.cb_login_auto);
		tvSignUp = (TextView) findViewById(R.id.tv_login_signup);
		tvForgotPwd = (TextView) findViewById(R.id.tv_login_forgot);
		btnLogin = (TextView) findViewById(R.id.btn_login);
		facebookLogin = (LoginButton) findViewById(R.id.authButton);

		GlobalFont.apply(facebookLogin);

		//		cbxAutoLogin.setSwitchMinWidth(Util.dpToPx(LoginActivity.this, 60));
		cbxAutoLogin.setChecked(true);
		cbxAutoLogin.setSwitchPadding(Util.dpToPx(LoginActivity.this, 15));

		tvSignUp.setOnClickListener(this);
		tvForgotPwd.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		facebookLogin.setOnClickListener(this);

		etPwd.setId(EditorInfo.IME_ACTION_DONE);
		etPwd.setOnEditorActionListener(new OnEditorActionListener()
		{

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				switch (actionId)
				{
					case EditorInfo.IME_ACTION_DONE:
						btnLogin.performClick();
						break;
				}
				return false;
			}
		});

		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();

		mMixpanel = MixpanelAPI.getInstance(this, "791b366dadafcd37803f6cd7d8358373");

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}

	private void makeMeRequest(final Session session)
	{

		Request request = Request.newMeRequest(session, new Request.GraphUserCallback()
		{

			@Override
			public void onCompleted(GraphUser user, Response response)
			{
				if (user != null)
				{
					TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

					String userEmail = null;

					try
					{
						if (user.getProperty("email") != null)
						{
							userEmail = user.getProperty("email").toString();
						}
					} catch (Exception e)
					{
						ExLog.e(e.toString());
					}

					String userId = user.getId();
					String encryptedId = Crypto.encrypt(userId).replace("\n", "");
					String userName = user.getName();
					String deviceId = telephonyManager.getDeviceId();

					snsSignupParams = new HashMap<String, String>();
					loginParams = new HashMap<String, String>();

					if (userEmail != null)
					{
						snsSignupParams.put("email", userEmail);
					}

					if (userId != null)
					{
						snsSignupParams.put("accessToken", userId);
						loginParams.put("accessToken", userId);
					}

					if (encryptedId != null)
					{
						snsSignupParams.put("pw", userId); // 회원가입
						// 시엔 서버
						// 사이드에서
						// 암호화
						loginParams.put("pw", encryptedId);
					}

					if (userName != null)
					{
						snsSignupParams.put("name", userName);
					}

					if (deviceId != null)
					{
						snsSignupParams.put("device", deviceId);
					}

					snsSignupParams.put("marketType", RELEASE_STORE.getName());

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, LoginActivity.this));

					fbSession.closeAndClearTokenInformation();
				}
			}
		});

		// 페이스북 연결 시작
		lockUI();
		request.executeAsync();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == tvForgotPwd.getId())
		{
			// 비밀번호 찾기
			Intent i = new Intent(this, ForgotPwdActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == tvSignUp.getId())
		{
			// 회원가입
			Intent i = new Intent(this, SignupActivity.class);
			startActivityForResult(i, CODE_REQEUST_ACTIVITY_SIGNUP);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == btnLogin.getId())
		{
			// 로그인
			if (isBlankFields() == false)
			{
				return;
			}

			String md5 = Crypto.encrypt(etPwd.getText().toString()).replace("\n", "");

			loginParams = new LinkedHashMap<String, String>();
			loginParams.put("email", etId.getText().toString());
			loginParams.put("pw", md5);
			ExLog.d("email : " + loginParams.get("email") + " pw : " + loginParams.get("pw"));
			lockUI();

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, this));

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestLogin", null, null);

		} else if (v.getId() == facebookLogin.getId())
		{
			//			fbSession = new Session.Builder(this).setTokenCachingStrategy(new SharedPreferencesTokenCachingStrategy(this)).setApplicationId(getString(R.string.app_id)).build();
			//			Session.OpenRequest openRequest = new Session.OpenRequest(this);
			//			openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
			//			openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
			//			openRequest.setPermissions(Arrays.asList("email", "basic_info"));
			//			openRequest.setCallback(statusCallback);
			//			fbSession.openForRead(openRequest); 

			fbSession = new Session.Builder(this).setApplicationId(getString(R.string.app_id)).build();
			Session.OpenRequest or = new Session.OpenRequest(this); // 안드로이드 sdk를 사용하기 위해선 내 컴퓨터의 hash key를 페이스북 개발 설정페이지에서 추가하여야함.
			//			or.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO); // 앱 호출이 아닌 웹뷰를 강제로 호출함.
			or.setPermissions(Arrays.asList("email", "basic_info"));
			or.setCallback(statusCallback);

			fbSession.openForRead(or);

			Session.setActiveSession(fbSession);

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestFacebookLogin", null, null);
		}
	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback()
	{

		@Override
		public void call(Session session, SessionState state, Exception exception)
		{
			if (state.isOpened())
			{
				makeMeRequest(session);
			} else if (state.isClosed())
			{
				session.closeAndClearTokenInformation();
			} else
			{
				unLockUI();
			}
			// 사용자 취소 시
			//			if (exception instanceof FacebookOperationCanceledException 
			//					|| exception instanceof FacebookAuthorizationException) {
			//				unLockUI();
			//			}

		}

	};

	public boolean isBlankFields()
	{
		if (etId.getText().toString().trim().length() == 0)
		{
			DailyToast.showToast(this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
			return false;
		}

		if (etPwd.getText().toString().trim().length() == 0)
		{
			DailyToast.showToast(this, R.string.toast_msg_please_input_passwd, Toast.LENGTH_SHORT);
			return false;
		}

		return true;
	}

	public void storeLoginInfo()
	{

		// 자동 로그인 체크시
		if (cbxAutoLogin.isChecked())
		{
			String id = loginParams.get("email");
			String pwd = loginParams.get("pw");
			String accessToken = loginParams.get("accessToken");

			SharedPreferences.Editor ed = sharedPreference.edit();
			ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);

			if (accessToken != null)
			{
				ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, accessToken);
				ed.putString(KEY_PREFERENCE_USER_ID, null);
			} else
			{
				ed.putString(KEY_PREFERENCE_USER_ID, id);
				ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			}

			ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
			ed.commit();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_REQEUST_ACTIVITY_SIGNUP)
		{
			if (resultCode == RESULT_OK)
			{
				setResult(RESULT_OK);
				finish();
			}
		} else
		{
			if (fbSession != null)
			{
				fbSession.onActivityResult(this, requestCode, resultCode, data);
			}
		}
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	private String getGcmId()
	{
		return sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");
	}

	private Boolean isGoogleServiceAvailable()
	{
		int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else
			{
				DailyToast.showToast(this, R.string.toast_msg_is_not_available_google_service, Toast.LENGTH_LONG);
				finish();
			}
			return false;
		} else
		{
			return true;
		}
	}

	private void regGcmId(final int idx)
	{
		if (isGoogleServiceAvailable() == false)
		{
			return;
		}

		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(LoginActivity.this);
				String regId = "";
				try
				{
					regId = instance.register(GCM_PROJECT_NUMBER);
				} catch (IOException e)
				{
					ExLog.e(e.toString());
				}

				return regId;
			}

			@Override
			protected void onPostExecute(String regId)
			{
				// 이 값을 서버에 등록하기.
				// gcm id가 없을 경우 스킵.
				if (regId == null || regId.isEmpty())
				{
					unLockUI();

					DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
					setResult(RESULT_OK);
					finish();
					return;
				}

				regPushParams = new HashMap<String, String>();

				regPushParams.put("user_idx", idx + "");
				regPushParams.put("notification_id", regId);
				regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

				ExLog.d("params for register push id : " + regPushParams.toString());

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_GCM_REGISTER).toString(), regPushParams, mGcmRegisterJsonResponseListener, LoginActivity.this));
			}
		}.execute();
	}

	@Override
	protected void onResume()
	{
		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("profileWithLogoff", "/todays-hotels/profile-with-logoff");
		super.onResume();
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

	private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				String msg = null;

				if (response == null)
				{
					throw new NullPointerException("response == null.");
				}

				if (response.getBoolean("login") == true)
				{
					VolleyHttpClient.createCookie();
					storeLoginInfo();

					if (sharedPreference.getBoolean("Facebook SignUp", false) == true)
					{
						mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, LoginActivity.this));
					} else
					{
						if (getGcmId().isEmpty() == true)
						{
							// 로그인에 성공하였으나 기기에 GCM을 등록하지 않은 유저의 경우 인덱스를 가져와 push_id를 업그레이드 하는 절차 시작.
							mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, LoginActivity.this));
						} else
						{
							unLockUI();

							// 로그인에 성공 하였고 GCM 코드 또한 이미 기기에 저장되어 있는 상태이면 종료. 
							DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
							setResult(RESULT_OK);
							finish();
						}
					}

					Editor editor = sharedPreference.edit();
					editor.putString("collapseKey", "");
					editor.apply();
				} else
				{
					if (loginParams.containsKey("accessToken"))
					{
						// SNS 로그인인데
						// 실패했을 경우 회원가입 시도
						cbxAutoLogin.setChecked(true); // 회원가입의 경우 기본으로 자동 로그인인
						// 정책 상.
						mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNUP).toString(), snsSignupParams, mUserSignupJsonResponseListener, LoginActivity.this));
					} else if (response.length() > 1)
					{
						// 로그인 실패
						// 실패 msg 출력

						unLockUI();
						msg = response.getString("msg");
						SimpleAlertDialog.build(LoginActivity.this, msg, getString(R.string.dialog_btn_text_confirm), null).show();
					}
				}
			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null.");
				}

				// GCM 아이디를 등록한다.
				if (sharedPreference.getBoolean("Facebook SignUp", false) == true)
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
					props.put("method", "facebook");
					mMixpanel.track("signup", props);

					Editor editor = sharedPreference.edit();
					editor.putBoolean("Facebook SignUp", false);
					editor.commit();

					ExLog.d("facebook signup is completed.");

					// Facebook은 한번에 처리하기 위해서.
					if (getGcmId().isEmpty() == false)
					{
						unLockUI();

						// 로그인에 성공 하였고 GCM 코드 또한 이미 기기에 저장되어 있는 상태이면 종료. 
						DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
						setResult(RESULT_OK);
						finish();
						return;
					}
				}

				regGcmId(response.getInt("idx"));
			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null.");
				}

				String result = response.getString("join");
				String msg = response.getString("msg");

				ExLog.d("user/join? " + response.toString());

				if ("true".equalsIgnoreCase(result) == true)
				{
					// 회원가입에 성공하면 이제 로그인 절차
					Editor ed = sharedPreference.edit();
					ed.putBoolean("Facebook SignUp", true);
					ed.commit();

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, LoginActivity.this));
				} else
				{
					unLockUI();

					loginParams.clear();
					DailyToast.showToast(LoginActivity.this, msg, Toast.LENGTH_LONG);
				}

			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}

		}
	};

	private DailyHotelJsonResponseListener mGcmRegisterJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null.");
				}

				ExLog.e("MSG : " + response.toString());

				if (response.getString("result").equals("true") == true)
				{
					Editor editor = sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
					editor.apply();
				}

				DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
				setResult(RESULT_OK);
				finish();
			} catch (JSONException e)
			{
				ExLog.e(e.toString());
			} finally
			{
				unLockUI();
			}
		}
	};

	//
	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
	//			// 서버와 연결 종료
	//			unLockUI();
	//
	//			JSONObject obj = response;
	//			try {
	//				String msg = null;
	//
	//				if (obj.getBoolean("login")) {
	//					VolleyHttpClient.createCookie();
	//					storeLoginInfo();
	//					
	//					if (sharedPreference.getBoolean("Facebook SignUp", false)) {
	//						lockUI();
	//						mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//								new StringBuilder(URL_DAILYHOTEL_SERVER)
	//						.append(URL_WEBAPI_USER_INFO).toString(), null, this, this));
	//					}
	//
	//					ExLog.e("LOGIN : " + obj.getBoolean("login")+"");
	//
	//					if (getGcmId().isEmpty()) {
	//						ExLog.e("STORED_GCM_IS_EMPTY = true");
	//						// 로그인에 성공하였으나 기기에 GCM을 등록하지 않은 유저의 경우 인덱스를 가져와 push_id를 업그레이드 하는 절차 시작.
	//						lockUI();
	//						mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//								new StringBuilder(URL_DAILYHOTEL_SERVER)
	//						.append(URL_WEBAPI_USER_INFO).toString(), null, this, this));
	//					} else {
	//						// 로그인에 성공 하였고 GCM 코드 또한 이미 기기에 저장되어 있는 상태이면 종료. 
	//						showToast(getString(R.string.toast_msg_logoined), Toast.LENGTH_SHORT, true);
	//						setResult(RESULT_OK);
	//						finish();
	//					}
	//					
	//					Editor editor = sharedPreference.edit();
	//					editor.putString("collapseKey", "");
	//					editor.apply();
	//				} else {
	//
	//					if (loginParams.containsKey("accessToken")) { // SNS 로그인인데
	//						// 실패했을 경우 회원가입 시도
	//						lockUI();
	//						cbxAutoLogin.setChecked(true); // 회원가입의 경우 기본으로 자동 로그인인
	//						// 정책 상.
	//						mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//								new StringBuilder(URL_DAILYHOTEL_SERVER)
	//						.append(URL_WEBAPI_USER_SIGNUP)
	//						.toString(), snsSignupParams, this,
	//						this));
	//
	//					}
	//
	//					// 로그인 실패
	//					// 실패 msg 출력
	//					else if (obj.length() > 1) {
	//						msg = obj.getString("msg");
	//						SimpleAlertDialog.build(this, msg, getString(R.string.dialog_btn_text_confirm), null).show();
	//					}
	//
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_WEBAPI_USER_SIGNUP)) {
	//			try {
	//				unLockUI();
	//
	//				JSONObject obj = response;
	//
	//				String result = obj.getString("join");
	//				String msg = obj.getString("msg");
	//
	//				ExLog.d("user/join? " + response.toString());
	//				if (result.equals("true")) { // 회원가입에 성공하면 이제 로그인 절차
	//					lockUI();
	//					Editor ed = sharedPreference.edit();
	//					ed.putBoolean("Facebook SignUp", true);
	//					ed.commit();
	//					
	//					mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
	//									URL_WEBAPI_USER_LOGIN).toString(),
	//									loginParams, LoginActivity.this, LoginActivity.this));
	//				} else {
	//					loginParams.clear();
	//					showToast(msg, Toast.LENGTH_LONG, true);
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
	//			ExLog.d("user_info!!!!");
	//			unLockUI();
	//			try {
	//				// GCM 아이디를 등록한다.
	//				if (sharedPreference.getBoolean("Facebook SignUp", false)) {
	//					int userIdx = response.getInt("idx");
	//					String userIdxStr = String.format("%07d", userIdx);
	//					
	//					SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
	//					Date date = new Date();
	//					String strDate = dateFormat.format(date);
	//					
	//					mMixpanel.getPeople().identify(userIdxStr);
	//					
	//					JSONObject props = new JSONObject();
	//					props.put("userId", userIdxStr);
	//					props.put("datetime", strDate);
	//					props.put("method", "facebook");
	//					mMixpanel.track("signup", props);
	//					
	//					Editor editor = sharedPreference.edit();
	//					editor.putBoolean("Facebook SignUp", false);
	//					editor.commit();
	//					
	//					ExLog.d("facebook signup is completed.");
	//					
	//					return;
	//				}
	//				
	//				if (isGoogleServiceAvailable()) {
	//					ExLog.d("call regGcmId");
	//					lockUI();
	//					mGcm = GoogleCloudMessaging.getInstance(this);
	//					regGcmId(response.getInt("idx"));
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_GCM_REGISTER)) {
	//			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
	//			try {
	//				unLockUI();
	//				ExLog.e("MSG : " + response.toString());
	//				if (response.getString("result").equals("true")) {
	//					ExLog.d(response.toString());
	//					Editor editor = sharedPreference.edit();
	//					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
	//					editor.apply();
	//
	//					ExLog.e("STORED_GCM_ID : " + sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "NOAP"));
	//
	//				}
	//
	//				showToast(getString(R.string.toast_msg_logoined), Toast.LENGTH_SHORT, true);
	//				setResult(RESULT_OK);
	//				finish();
	//			} catch (JSONException e) {
	//				ExLog.e(e.toString());
	//			}
	//		}
	//
	//	}
}
