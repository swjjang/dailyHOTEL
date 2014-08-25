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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.Switch;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LoginActivity extends BaseActivity implements Constants,
OnClickListener, DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "LoginActivity";

	private EditText etId, etPwd;
	private Switch cbxAutoLogin;
	private Button btnLogin;
	private TextView tvSignUp, tvForgotPwd;
	private LoginButton facebookLogin;

	private Map<String, String> loginParams;
	private Map<String, String> snsSignupParams;
	private Map<String, String> regPushParams;

	public Session fbSession;

	private GoogleCloudMessaging mGcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setActionBar(R.string.actionbar_title_login_activity);
		setContentView(R.layout.activity_login);

		etId = (EditText) findViewById(R.id.et_login_id);
		etPwd = (EditText) findViewById(R.id.et_login_pwd);
		cbxAutoLogin = (Switch) findViewById(R.id.cb_login_auto);
		tvSignUp = (TextView) findViewById(R.id.tv_login_signup);
		tvForgotPwd = (TextView) findViewById(R.id.tv_login_forgot);
		btnLogin = (Button) findViewById(R.id.btn_login);
		facebookLogin = (LoginButton) findViewById(R.id.authButton);

		tvSignUp.setOnClickListener(this);
		tvForgotPwd.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		facebookLogin.setOnClickListener(this);

		etPwd.setId(EditorInfo.IME_ACTION_DONE);
		etPwd.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_DONE:
					btnLogin.performClick();
					break;
				}
				return false;
			}
		});

		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();

	}

	private void makeMeRequest(final Session session) {

		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {

				if (user != null) {
					TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext()
							.getSystemService(Context.TELEPHONY_SERVICE);

					String userEmail = null;

					try {
						if (user.getProperty("email") != null) userEmail = user.getProperty("email").toString();
					} catch (Exception e) {
						if (DEBUG)
							e.printStackTrace();
					}

					String userId = user.getId();
					String encryptedId = Crypto.encrypt(userId)
							.replace("\n", "");
					String userName = user.getName();
					String deviceId = telephonyManager.getDeviceId();

					snsSignupParams = new HashMap<String, String>();
					loginParams = new HashMap<String, String>();

					if (userEmail != null) snsSignupParams.put("email", userEmail);

					if (userId != null) {
						snsSignupParams.put("accessToken", userId);
						loginParams.put("accessToken", userId);
					}

					if (encryptedId != null) {
						snsSignupParams.put("pw", userId); // 회원가입
						// 시엔 서버
						// 사이드에서
						// 암호화
						loginParams.put("pw", encryptedId);
					}

					if (userName != null) snsSignupParams.put("name", userName);

					if (deviceId != null) snsSignupParams.put("device", deviceId);
					unLockUI(); // 페이스북 연결 종료 

					lockUI(); // 서버와 연결 시작 

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER)
					.append(URL_WEBAPI_USER_LOGIN)
					.toString(), loginParams,
					LoginActivity.this, LoginActivity.this));

					fbSession.closeAndClearTokenInformation();
				}
			}

		});

		// 페이스북 연결 시작
		lockUI();
		request.executeAsync();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == tvForgotPwd.getId()) { // 비밀번호 찾기
			Intent i = new Intent(this, ForgotPwdActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == tvSignUp.getId()) { // 회원가입
			Intent i = new Intent(this, SignupActivity.class);
			startActivityForResult(i, CODE_REQEUST_ACTIVITY_SIGNUP);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == btnLogin.getId()) { // 로그인
			if (!isBlankFields()) return;

			String md5 = Crypto.encrypt(etPwd.getText().toString()).replace("\n", "");

			loginParams = new LinkedHashMap<String, String>();
			loginParams.put("email", etId.getText().toString());
			loginParams.put("pw", md5);
			lockUI();

			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_LOGIN).toString(), loginParams,
							this, this));

		} else if (v.getId() == facebookLogin.getId()) {
			fbSession = new Session.Builder(this).setApplicationId(getString(R.string.app_id)).build();
			Session.OpenRequest or = new Session.OpenRequest(this); // 안드로이드 sdk를 사용하기 위해선 내 컴퓨터의 hash key를 페이스북 개발 설정페이지에서 추가하여야함.
			//			or.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO); // 앱 호출이 아닌 웹뷰를 강제로 호출함.
			or.setPermissions(Arrays.asList("email", "basic_info"));
			or.setCallback(statusCallback);

			fbSession.openForRead(or);

			Session.setActiveSession(fbSession);
		}
	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) makeMeRequest(session);
			else if (state.isClosed()) session.closeAndClearTokenInformation();

			// 사용자 취소 시
			//			if (exception instanceof FacebookOperationCanceledException 
			//					|| exception instanceof FacebookAuthorizationException) {
			//				unLockUI();
			//			}

		}

	};

	public boolean isBlankFields() {
		if (etId.getText().toString().trim().length() == 0) {
			showToast(getString(R.string.toast_msg_please_input_id), Toast.LENGTH_SHORT, true);
			return false;
		}

		if (etPwd.getText().toString().trim().length() == 0) {
			showToast(getString(R.string.toast_msg_please_input_passwd), Toast.LENGTH_SHORT, true);
			return false;
		}

		return true;
	}

	public void storeLoginInfo() {

		// 자동 로그인 체크시
		if (cbxAutoLogin.isChecked()) {

			String id = loginParams.get("email");
			String pwd = loginParams.get("pw");
			String accessToken = loginParams.get("accessToken");

			SharedPreferences.Editor ed = sharedPreference.edit();
			ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);

			if (accessToken != null) {
				ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, accessToken);
				ed.putString(KEY_PREFERENCE_USER_ID, null);
			} else {
				ed.putString(KEY_PREFERENCE_USER_ID, id);
				ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			}

			ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
			ed.commit();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_REQEUST_ACTIVITY_SIGNUP) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		} else {
			fbSession.onActivityResult(this, requestCode, resultCode, data);
		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			// 서버와 연결 종료
			unLockUI();

			JSONObject obj = response;
			try {
				String msg = null;

				if (obj.getBoolean("login")) {
					VolleyHttpClient.createCookie();
					storeLoginInfo();
					
					android.util.Log.e("LOGIN",obj.getBoolean("login")+"");
					
					if (getGcmId().isEmpty()) {
						android.util.Log.e("STORED_GCM_IS_EMPTY","true");
						// 로그인에 성공하였으나 기기에 GCM을 등록하지 않은 유저의 경우 인덱스를 가져와 push_id를 업그레이드 하는 절차 시작.
						lockUI();
						mQueue.add(new DailyHotelJsonRequest(Method.POST,
								new StringBuilder(URL_DAILYHOTEL_SERVER)
						.append(URL_WEBAPI_USER_INFO).toString(), null, this, this));
					} else {
						// 로그인에 성공 하였고 GCM 코드 또한 이미 기기에 저장되어 있는 상태이면 종료. 
						showToast(getString(R.string.toast_msg_logoined), Toast.LENGTH_SHORT, true);
						setResult(RESULT_OK);
						finish();
					}

				} else {

					if (loginParams.containsKey("accessToken")) { // SNS 로그인인데
						// 실패했을 경우 회원가입 시도
						lockUI();
						cbxAutoLogin.setChecked(true); // 회원가입의 경우 기본으로 자동 로그인인
						// 정책 상.
						mQueue.add(new DailyHotelJsonRequest(Method.POST,
								new StringBuilder(URL_DAILYHOTEL_SERVER)
						.append(URL_WEBAPI_USER_SIGNUP)
						.toString(), snsSignupParams, this,
						this));

					}

					// 로그인 실패
					// 실패 msg 출력
					else if (obj.length() > 1) {
						msg = obj.getString("msg");
						SimpleAlertDialog.build(this, msg, "확인", null).show();
					}

				}

			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_USER_SIGNUP)) {
			try {
				unLockUI();

				JSONObject obj = response;

				String result = obj.getString("join");
				String msg = obj.getString("msg");

				if (result.equals("true")) { // 회원가입에 성공하면 이제 로그인 절차
					lockUI();
					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
									loginParams, LoginActivity.this, LoginActivity.this));
				} else {
					loginParams.clear();
					showToast(msg, Toast.LENGTH_LONG, true);
				}

			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
			
			try {
				// GCM 아이디를 등록한다.
				if (isGoogleServiceAvailable()) {
					mGcm = GoogleCloudMessaging.getInstance(this);
					regGcmId(response.getInt("idx"));
				}

			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_GCM_REGISTER)) {
			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
			try {
				unLockUI();
				android.util.Log.e("MSG?",response.toString());
				if (response.getString("result").equals("true")) {
					Editor editor = sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
					editor.apply();

					android.util.Log.e("STORED_GCM_ID", sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "NOAP"));

				}
				
				showToast(getString(R.string.toast_msg_logoined), Toast.LENGTH_SHORT, true);
				setResult(RESULT_OK);
				finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private String getGcmId() {
		return sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");
	}

	private Boolean isGoogleServiceAvailable() {
		int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resCode)) {
				GooglePlayServicesUtil.getErrorDialog(resCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				showToast(getString(R.string.toast_msg_is_not_available_google_service), Toast.LENGTH_LONG, false);
				finish();
			}
			return false;
		} else {
			return true;
		}
	}

	private void regGcmId(final int idx) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(LoginActivity.this);
				String regId = "";
				try {
					regId = instance.register(GCM_PROJECT_NUMBER);
				} catch (IOException e) {e.printStackTrace();}

				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {
				// 이 값을 서버에 등록하기.
				regPushParams = new HashMap<String, String>();

				regPushParams.put("user_idx", idx+"");
				regPushParams.put("notification_id", regId);
				regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);
				
				android.util.Log.e("params for register push id",regPushParams.toString());
				
				mQueue.add(new DailyHotelJsonRequest(Method.POST,
						new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_GCM_REGISTER)
				.toString(), regPushParams, LoginActivity.this,
				LoginActivity.this));
			}
		}.execute();		
	}
}
