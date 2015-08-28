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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends BaseActivity implements OnClickListener
{
	private static final int MAX_OF_RECOMMENDER = 45;

	private static final int MODE_SIGNUP = 1;
	private static final int MODE_USERINFO_UPDATE = 2;

	private EditText etEmail, etName, etPhone, etPwd, etRecommender;
	private TextView tvTerm, tvPrivacy;
	private TextView btnSignUp;
	private int mMode;
	private String mUserIdx;

	private Map<String, String> signupParams;
	private HashMap<String, String> regPushParams;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signup);

		Intent intent = getIntent();

		Customer user = null;

		if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER) == true)
		{
			mMode = MODE_USERINFO_UPDATE;

			user = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

			setActionBar(R.string.actionbar_title_userinfo_update_activity);

			if (user == null)
			{
				finish();
				return;
			}

			if (isFinishing() == true)
			{
				return;
			}

			showSimpleDialog(0, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_facebook_update), getString(R.string.dialog_btn_text_confirm), null, null, null);
		} else
		{
			mMode = MODE_SIGNUP;

			setActionBar(R.string.actionbar_title_signup_activity);
		}

		etPwd = (EditText) findViewById(R.id.et_signup_pwd);
		etEmail = (EditText) findViewById(R.id.et_signup_email);
		etRecommender = (EditText) findViewById(R.id.et_signup_recommender);
		etName = (EditText) findViewById(R.id.et_signup_name);

		// 회원 가입시 이름 필터 적용.
		StringFilter stringFilter = new StringFilter(SignupActivity.this);
		InputFilter[] allowAlphanumericHangul = new InputFilter[1];
		allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

		etName.setFilters(allowAlphanumericHangul);

		// 추천코드 최대 길이
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(MAX_OF_RECOMMENDER);
		etRecommender.setFilters(fArray);

		etPhone = (EditText) findViewById(R.id.et_signup_phone);

		tvTerm = (TextView) findViewById(R.id.tv_signup_agreement);
		tvPrivacy = (TextView) findViewById(R.id.tv_signup_personal_info);
		btnSignUp = (TextView) findViewById(R.id.btn_signup);

		if (user != null)
		{
			mUserIdx = user.getUserIdx();

			if (isEmptyTextField(user.getPhone()) == false)
			{
				etPhone.setText(user.getPhone());
				etPhone.setEnabled(false);
				etPhone.setFocusable(false);
			}

			if (isEmptyTextField(user.getEmail()) == false)
			{
				etEmail.setText(user.getEmail());
				etEmail.setEnabled(false);
				etEmail.setFocusable(false);
			}

			if (isEmptyTextField(user.getName()) == false)
			{
				etName.setText(user.getName());
				etName.setEnabled(false);
				etName.setFocusable(false);
			}

			etPwd.setVisibility(View.GONE);
			btnSignUp.setText(R.string.act_signup_btn_update);
		} else
		{
			getPhoneNumber();
		}

		tvTerm.setOnClickListener(this);
		tvPrivacy.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(SignupActivity.this).recordScreen(Screen.SIGNUP);
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

	public boolean checkInput(boolean checkPassword)
	{
		if (etEmail.getText().toString().trim().equals("") == true)
		{
			return false;
		} else if (etName.getText().toString().trim().equals("") == true)
		{
			return false;
		} else if (etPhone.getText().toString().trim().equals("") == true)
		{
			return false;
		} else if (checkPassword == true && etPwd.getText().toString().trim().equals("") == true)
		{
			return false;
		} else
		{
			return true;
		}
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
		{
			if (mMode == MODE_SIGNUP)
			{
				// 회원가입
				// 필수 입력 check
				if (checkInput(true) == false)
				{
					DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
					return;
				}

				// email check
				if (android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches() == false)
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
				if (recommender.equals("") == false)
				{
					signupParams.put("recommender", recommender);
				}

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNUP).toString(), signupParams, mUserSignupJsonResponseListener, this));

				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.SIGNUP, Action.CLICK, Label.SIGNUP, 0L);
			} else
			{
				// 회원 정보 업데이트
				// 필수 입력 check
				if (checkInput(false) == false)
				{
					DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
					return;
				}

				// email check
				if (etEmail.isEnabled() == true && android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches() == false)
				{
					DailyToast.showToast(SignupActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
					return;
				}

				lockUI();

				Map<String, String> updateParams = new HashMap<String, String>();
				updateParams.put("user_idx", mUserIdx);

				if (etEmail.isEnabled() == true)
				{
					updateParams.put("user_email", etEmail.getText().toString().trim());
				}

				if (etName.isEnabled() == true)
				{
					updateParams.put("user_name", etName.getText().toString().trim());
				}

				if (etPhone.isEnabled() == true)
				{
					updateParams.put("user_phone", etPhone.getText().toString().trim());
				}

				String recommender = etRecommender.getText().toString().trim();
				if (recommender.equals("") == false)
				{
					updateParams.put("recommendation_code", recommender);
				}

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_UPDATE_FB_USER).toString(), updateParams, mUserUpdateFacebookJsonResponseListener, this));
			}
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
		super.onDestroy();
	}

	private void signUpAndFinish()
	{
		unLockUI();

		DailyToast.showToast(SignupActivity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);
		finish();
	}

	private boolean isEmptyTextField(String fieldText)
	{
		return (TextUtils.isEmpty(fieldText) == true || fieldText.equals("null") == true || fieldText.trim().length() == 0);
	}

	private Boolean isGoogleServiceAvailable()
	{
		int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resCode != ConnectionResult.SUCCESS)
		{
			return false;
		} else
		{
			return true;
		}
	}

	private void regGcmId(final String idx)
	{
		if (isGoogleServiceAvailable() == false)
		{
			signUpAndFinish();
			return;
		}

		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(SignupActivity.this);
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
				// gcm id가 없을 경우 스킵.
				if (TextUtils.isEmpty(regId) == true)
				{
					signUpAndFinish();
					return;
				}

				// 이 값을 서버에 등록하기.
				regPushParams = new HashMap<String, String>();
				regPushParams.put("user_idx", idx);
				regPushParams.put("notification_id", regId);
				regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_GCM_REGISTER).toString(), regPushParams, mGcmRegisterJsonResponseListener, new ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError arg0)
					{
						signUpAndFinish();
					}
				}));
			}
		}.execute();
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
				unLockUI();
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
					storeLoginInfo();

					lockUI();
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, SignupActivity.this));
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
				String userIndex = String.valueOf(response.getInt("idx"));

				regGcmId(userIndex);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
				Date date = new Date();
				String strDate = dateFormat.format(date);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Label.CURRENT_TIME, strDate);
				params.put(Label.USER_INDEX, userIndex);
				params.put(Label.TYPE, "email");

				AnalyticsManager.getInstance(SignupActivity.this).recordEvent(Screen.SIGNUP, Action.NETWORK, Label.SIGNUP, params);
			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserUpdateFacebookJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (isFinishing() == true)
			{
				return;
			}

			try
			{
				unLockUI();

				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				JSONObject jsonObject = response.getJSONObject("data");

				boolean result = jsonObject.getBoolean("is_success");
				int msgCode = response.getInt("msg_code");

				if (result == true)
				{
					String msg = null;

					if (response.has("msg") == true)
					{
						msg = response.getString("msg");
					}

					switch (msgCode)
					{
						case 100:
						{
							if (msg != null)
							{
								DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_SHORT);
							}

							setResult(RESULT_OK);
							finish();
							break;
						}

						case 200:
						{
							if (msg != null)
							{
								if (isFinishing() == true)
								{
									return;
								}

								showSimpleDialog(0, null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
								{
									@Override
									public void onClick(View view)
									{
										setResult(RESULT_OK);
										finish();
									}
								}, null);
							} else
							{
								setResult(RESULT_OK);
								finish();
							}
							break;
						}

						default:
							setResult(RESULT_OK);
							finish();
							break;
					}

				} else
				{
					String msg = null;

					if (response.has("msg") == true)
					{
						msg = response.getString("msg");
					}

					switch (msgCode)
					{
						case 100:
						{
							if (msg != null)
							{
								DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_SHORT);
							}
							break;
						}

						case 200:
						{
							if (msg != null)
							{
								if (isFinishing() == true)
								{
									return;
								}

								showSimpleDialog(0, null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
							}
							break;
						}
					}
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mGcmRegisterJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			unLockUI();

			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
			try
			{
				String result = null;

				if (null != response)
				{
					result = response.getString("result");
				}

				if (true == "true".equalsIgnoreCase(result))
				{
					Editor editor = sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
					editor.apply();
				}
			} catch (Exception e)
			{
			} finally
			{
				signUpAndFinish();
			}
		}
	};
}
