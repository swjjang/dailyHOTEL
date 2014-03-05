package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.SIGNUP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.network.Parameter;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class SignupActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = "SignupActivity";
	private final static int SETTING_FRAGMENT = 1;
	private final static int NOLOGIN_FRAGMENT = 2;
	private final static int BOOKING_LIST_FRAGMENT = 3;
	private final static int HOTEL_TAB_FRAGMENT = 4;
	
	private EditText et_email, et_name, et_phone, et_pwd, et_recommender;
	private TextView tv_agreement, tv_personal_info;
	private Button btn_signup;
	
	private SharedPreferences prefs;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Sign up");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signup);
		setTitle(Html.fromHtml("<font color='#050505'>회원가입</font>"));
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e(TAG, "Exception loading drawable"); 
		}
		
		loadResource();
		getPhoneNumber();
		
	}
	
	public void loadResource() {
		et_pwd = (EditText)findViewById(R.id.et_signup_pwd); 
		et_email = (EditText)findViewById(R.id.et_signup_email);
		et_recommender = (EditText)findViewById(R.id.et_signup_recommender);
		et_name = (EditText) findViewById(R.id.et_signup_name);
		et_phone = (EditText) findViewById(R.id.et_signup_phone);
		tv_agreement = (TextView) findViewById(R.id.tv_signup_agreement);
		tv_personal_info = (TextView) findViewById(R.id.tv_signup_personal_info);
		btn_signup = (Button)findViewById(R.id.btn_signup);
		
		tv_agreement.setOnClickListener(this);
		tv_personal_info.setOnClickListener(this);
		btn_signup.setOnClickListener(this);
	}
	
	public void getPhoneNumber() {
		TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		String phoneNum = telManager.getLine1Number();
		et_phone.setText(phoneNum);
	}
	
	public boolean checkInput() {
		if(et_email.getText().toString().equals("")) return false;
		else if(et_name.getText().toString().equals("")) return false;
		else if(et_phone.getText().toString().equals("")) return false;
		else if (et_pwd.getText().toString().equals("")) return false;
		else return true;
	}
	
	public boolean isValidEmail(String inputStr) {
		Pattern p = Pattern.compile("^[_a-zA-Z0-9-]+(.[_a-zA-Z0-9-]+)*@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	 }
	
	public boolean isValidPhone(String inputStr) {
		Pattern p = Pattern.compile("^(01[0|1|6|7|8|9])(\\d{4}|\\d{3})(\\d{4})$");
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
		if(v.getId() == btn_signup.getId()) {	// 회원가입
			
			// 필수 입력 check
			if(!checkInput()) {
				Toast.makeText(getApplicationContext(), "필수 입력사항은 모두 입력해주세요", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// email check
			if(!isValidEmail(et_email.getText().toString())) {
				Toast.makeText(this, "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(et_pwd.length() < 4)
			{
				Toast.makeText(this, "비밀번호를 4자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// Jason Park | Start
			// make parameter
			ArrayList<Parameter> paramList = new ArrayList<Parameter>();
			paramList.add(new Parameter("email", et_email.getText().toString()));
			paramList.add(new Parameter("pw", et_pwd.getText().toString()));
			paramList.add(new Parameter("name", et_name.getText().toString()));
			paramList.add(new Parameter("phone",et_phone.getText().toString()));
			TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			paramList.add(new Parameter("device",tManager.getDeviceId()));
			
			String recommender = et_recommender.getText().toString();
			// 추천인 CODE가 있는 확인
			LoadingDialog.showLoading(this);
			
			// 추천인 코드 입력 여부에 따른 요청
			if(recommender.trim().equals("")) {	
				new GeneralHttpTask(singupListener,paramList, getApplicationContext()).execute(REST_URL + SIGNUP);
			}
			else
			{
//				new GeneralHttpTask(recommenderListener, getApplicationContext()).execute(REST_URL + RECOMMEND + "/" + recommender);
				paramList.add(new Parameter("recommender", recommender));
				new GeneralHttpTask(singupListener,paramList, getApplicationContext()).execute(REST_URL + SIGNUP);
			}
			// Jason Park | End
		} else if(v.getId() == tv_agreement.getId()) {	// 이용약관
			
			Intent i = new Intent(this, AgreementActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		} else if(v.getId() == tv_personal_info.getId()) {	// 개인정보 취급
			
			Intent i = new Intent(this, PersonalInfoActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		}
	}
	
	
	public void parseRecommendJson(String str) {
		str = str.trim();
		
		if(str.equals("-1")) {
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "존재하지 않는 추천인 CODE 입니다", Toast.LENGTH_SHORT).show();
		} else {		// 정상적인 추천인 CODE
			ArrayList<Parameter> paramList = new ArrayList<Parameter>();
			paramList.add(new Parameter("email", et_email.getText().toString()));
			paramList.add(new Parameter("pw", et_pwd.getText().toString()));
			paramList.add(new Parameter("name", et_name.getText().toString()));
			paramList.add(new Parameter("phone",et_phone.getText().toString()));
			TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			paramList.add(new Parameter("device",tManager.getDeviceId()));
			paramList.add(new Parameter("recommender",str));
			new GeneralHttpTask(singupListener,paramList, getApplicationContext()).execute(REST_URL + SIGNUP);
		}
		
	}
	
	public void parseSignupJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			
			String result =  obj.getString("join");
			String msg = null;
			
			if(obj.length() > 1)
				msg = obj.getString("msg");
			
			if(result.equals("true")) {
				Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
				
				// 자동 로그인
				ArrayList<Parameter> params = new ArrayList<Parameter>(); 
				params.add(new Parameter("email", et_email.getText().toString()));
				params.add(new Parameter("pw", Crypto.encrypt(et_pwd.getText().toString()).replace("\n", "")));
				new GeneralHttpTask(loginListener, params, getApplicationContext()).execute(REST_URL + LOGIN);
				
			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
			
		} catch(Exception e) {
			Log.d(TAG, e.getMessage());
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseLoginJson(String str) {
		try{
			JSONObject obj = new JSONObject(str);
			String msg = null;
			
			if ( obj.getString("login").equals("true") ) {
				
				Log.d(TAG, "로그인 성공");
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
				commitLogin();
				
			} else {
				// 로그인 실패
				// 실패 msg 출력
				if(obj.length() > 1)
					msg = obj.getString("msg");
				LoadingDialog.hideLoading();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
			
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void commitLogin() {
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean(PREFERENCE_AUTO_LOGIN, true);
		ed.putString(PREFERENCE_USER_ID, et_email.getText().toString());
		ed.putString(PREFERENCE_USER_PWD, Crypto.encrypt(et_pwd.getText().toString()).replace("\n", ""));
		ed.putBoolean(PREFERENCE_IS_LOGIN, true);
		ed.commit();
		
		setResult(RESULT_OK);
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected OnCompleteListener recommenderListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFail");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseRecommendJson(result);
		}
	};
	
	protected OnCompleteListener singupListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFail");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseSignupJson(result);
		}
	};
	
	protected OnCompleteListener loginListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFail");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseLoginJson(result);
		}
	}; 
}
