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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class SignupActivity extends BaseActivity implements OnClickListener,
		DailyHotelJsonResponseListener, DailyHotelStringResponseListener,
		ErrorListener {

	private static final String TAG = "SignupActivity";

	private RequestQueue mQueue;

	private EditText etEmail, etName, etPhone, etPwd, etRecommender;
	private TextView tvTerm, tvPrivacy;
	private Button btnSignUp;
	
	private Map<String, String> signupParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("회원가입");
		setContentView(R.layout.activity_signup);
		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		mQueue = VolleyHttpClient.getRequestQueue();

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
				Toast.makeText(getApplicationContext(), "필수 입력사항은 모두 입력해주세요",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// email check
			if (!isValidEmail(etEmail.getText().toString())) {
				Toast.makeText(this, "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (etPwd.length() < 4) {
				Toast.makeText(this, "비밀번호를 4자 이상 입력해주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			LoadingDialog.showLoading(this);
			
			signupParams = new HashMap<String, String>();
			signupParams.put("email", etEmail.getText().toString());
			signupParams.put("pw", etPwd.getText().toString());
			signupParams.put("name", etName.getText().toString());
			signupParams.put("phone", etPhone.getText().toString());
			TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			signupParams.put("device", tManager.getDeviceId());

			String recommender = etRecommender.getText().toString();
			
			// 추천인 코드 입력 여부에 따른 요청
			if (!recommender.trim().equals("")) {
				
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
						URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER)
						.append(recommender).toString(), null, this, this));

			} else {
				mQueue.add(new DailyHotelJsonRequest(Method.POST,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_SIGNUP).toString(), signupParams,
						this, this));
			}

		} else if (v.getId() == tvTerm.getId()) { // 이용약관

			Intent i = new Intent(this, TermActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

		} else if (v.getId() == tvPrivacy.getId()) { // 개인정보 취급

			Intent i = new Intent(this, PrivacyActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

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
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
				Toast.LENGTH_SHORT).show();
		LoadingDialog.hideLoading();

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
					
					Toast.makeText(this, msg,
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
				
				LoadingDialog.hideLoading();
				Toast.makeText(this,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			}
		} else if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			
			try {
				if (response.getBoolean("login")) {
					LoadingDialog.hideLoading();
					
					Toast.makeText(this, "회원가입이 완료되었습니다.",
							Toast.LENGTH_SHORT).show();
					
					setResult(RESULT_OK);
					storeLoginInfo();
				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
			} 
			
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER
				+ etRecommender.getText().toString().trim())) {
			String str = response.trim();

			if (str.equals("-1")) {
				LoadingDialog.hideLoading();
				Toast.makeText(this, "존재하지 않는 추천인 CODE 입니다",
						Toast.LENGTH_SHORT).show();
			} else { // 정상적인 추천인 CODE
				Map<String, String> joinParams = new HashMap<String, String>();
				joinParams.put("email", etEmail.getText().toString());
				joinParams.put("pw", etPwd.getText().toString());
				joinParams.put("name", etName.getText().toString());
				joinParams.put("phone", etPhone.getText().toString());
				TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				joinParams.put("device", tManager.getDeviceId());
				String recommender = etRecommender.getText().toString();
				joinParams.put("recommender", recommender);
				
				mQueue.add(new DailyHotelJsonRequest(Method.POST,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_SIGNUP).toString(), joinParams,
						this, this));

			}
		}

	}
}
