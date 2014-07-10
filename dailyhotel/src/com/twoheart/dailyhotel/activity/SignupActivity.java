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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SignupActivity extends BaseActivity implements OnClickListener,
		DailyHotelJsonResponseListener {

	private static final String TAG = "SignupActivity";

	private EditText etEmail, etName, etPhone, etPwd, etRecommender;
	private TextView tvTerm, tvPrivacy;
	private Button btnSignUp;
	
	private Map<String, String> signupParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_signup_activity);
		setContentView(R.layout.activity_signup);
		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		etPwd = (EditText) findViewById(R.id.et_signup_pwd);
		etEmail = (EditText) findViewById(R.id.et_signup_email);
		etRecommender = (EditText) findViewById(R.id.et_signup_recommender);
		etName = (EditText) findViewById(R.id.et_signup_name);
		etPhone = (EditText) findViewById(R.id.et_signup_phone);
		tvTerm = (TextView) findViewById(R.id.tv_signup_agreement);
		tvPrivacy = (TextView) findViewById(R.id.tv_signup_personal_info);
		btnSignUp = (Button) findViewById(R.id.btn_signup);

		tvTerm.setOnClickListener(this);
		tvPrivacy.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);

		getPhoneNumber();

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());
	}



	public void getPhoneNumber() {
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNum = telManager.getLine1Number();
		etPhone.setText(phoneNum);
	}

	public boolean checkInput() {
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

	public boolean isValidEmail(String inputStr) {
		Pattern p = Pattern
				.compile("^[_a-zA-Z0-9-]+(.[_a-zA-Z0-9-]+)*@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	public boolean isValidPhone(String inputStr) {
		Pattern p = Pattern
				.compile("^(01[0|1|6|7|8|9])(\\d{4}|\\d{3})(\\d{4})$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	public boolean isVaildrecommend(String inputStr) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnSignUp.getId()) { // 회원가입

			// 필수 입력 check
			if (!checkInput()) {
				showToast(getString(R.string.toast_msg_please_input_required_infos), Toast.LENGTH_SHORT, true);
				return;
			}

			// email check
			if (!isValidEmail(etEmail.getText().toString())) {
				showToast(getString(R.string.toast_msg_wrong_email_address), Toast.LENGTH_SHORT, true);
				return;
			}

			if (etPwd.length() < 4) {
				showToast(getString(R.string.toast_msg_please_input_password_more_than_4chars), Toast.LENGTH_SHORT, true);
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
			
			String recommender = etRecommender.getText().toString().trim();
			if (!recommender.equals(""))				// 추천인 코드를 입력했을 경우 추천인 파라미터 추가
				signupParams.put("recommender", recommender);
			
			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_SIGNUP).toString(), signupParams,
					this, this));
			
		} else if (v.getId() == tvTerm.getId()) { // 이용약관

			Intent i = new Intent(this, TermActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == tvPrivacy.getId()) { // 개인정보 취급

			Intent i = new Intent(this, PrivacyActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		}
	}

	public void storeLoginInfo() {

		String id = etEmail.getText().toString();
		String pwd = Crypto.encrypt(etPwd.getText().toString()).replace("\n",
				"");

		SharedPreferences.Editor ed = sharedPreference.edit();
		ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);
		ed.putString(KEY_PREFERENCE_USER_ID, id);
		ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
		ed.commit();

		setResult(RESULT_OK);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_SIGNUP)) {
			try {
				JSONObject obj = response;

				String result = obj.getString("join");
				String msg = null;

				if (obj.length() > 1)
					msg = obj.getString("msg");

				if (result.equals("true")) {
					
					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("email", signupParams.get("email"));
					loginParams.put("pw", Crypto.encrypt(signupParams.get("pw")).replace(
							"\n", ""));
					
					mQueue.add(new DailyHotelJsonRequest(
							Method.POST, new StringBuilder(
									URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN)
									.toString(), loginParams,
							this, this));
					
				} else {
					unLockUI();
					showToast(msg, Toast.LENGTH_LONG, true);
				}

			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			
			try {
				if (response.getBoolean("login")) {
					VolleyHttpClient.createCookie();
					unLockUI();
					showToast(getString(R.string.toast_msg_success_to_signup), Toast.LENGTH_LONG, false);
					
					storeLoginInfo();
					finish();
				}
			} catch (JSONException e) {
				onError(e);
			} 
		}
	}
}
