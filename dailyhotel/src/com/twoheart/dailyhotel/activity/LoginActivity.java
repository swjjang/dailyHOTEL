package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.SIGNUP;
import static com.twoheart.dailyhotel.util.AppConstants.LOGIN_FACEBOOK;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.Parameter;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class LoginActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = "LoginActivity";
	
	private final static int SETTING_FRAGMENT = 1;
	private final static int NOLOGIN_FRAGMENT = 2;
	private final static int BOOKING_FRAGMENT = 3;
	private final static int HOTEL_TAB_FRAGMENT = 4;
	private final static int LOGIN_ACTIVITY = 5;
	
	private EditText et_id, et_pwd;
	private CheckBox cb_auto;
	private Button btn_login;
	private TextView tv_signup, tv_forgot;
	private LoginButton facebookLogin;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		loadResource();
		
		setTitle(Html.fromHtml("<font color='#050505'>로그인</font>"));
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
		
	}
	
	public void loadResource() {
		et_id = (EditText) findViewById(R.id.et_login_id);
		et_pwd = (EditText) findViewById(R.id.et_login_pwd);
		cb_auto = (CheckBox) findViewById(R.id.cb_login_auto);
		tv_signup = (TextView) findViewById(R.id.tv_login_signup);
		tv_forgot = (TextView) findViewById(R.id.tv_login_forgot);
		btn_login = (Button)	 findViewById(R.id.btn_login);
		facebookLogin = (LoginButton) findViewById(R.id.authButton);
		
		tv_signup.setOnClickListener(this);
		tv_forgot.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		facebookLogin.setOnClickListener(this);
		
		et_pwd.setId(EditorInfo.IME_ACTION_DONE);
		et_pwd.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId) {
                case EditorInfo.IME_ACTION_DONE:
                	btn_login.performClick();
                    break;
                }
				return false;
			}
		});
		
	}
	
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//	    @Override
//	    public void call(Session session, SessionState state, Exception exception) {
//	        onSessionStateChange(session, state, exception);
//	    }
//	};
//	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Facebook Login");
	        makeMeRequest(session);
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Facebook Logout");
	    }
	}
	
	private void makeMeRequest(final Session session) {
		
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
						
						TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
						String phoneNumber = telephonyManager.getLine1Number();
						
						ArrayList<Parameter> paramList = new ArrayList<Parameter>();
						paramList.add(new Parameter("email", null));
						paramList.add(new Parameter("pw", null));
						paramList.add(new Parameter("name", user.getName()));
						paramList.add(new Parameter("phone", phoneNumber));
						paramList.add(new Parameter("device", telephonyManager.getDeviceId()));
						paramList.add(new Parameter("accessToken", user.getId()));
						
						new GeneralHttpTask(snsListener, paramList, getApplicationContext()).execute(REST_URL + LOGIN_FACEBOOK);
					}
				}
				if (response.getError() != null) {
					
					
				}
			}
		});
		
		request.executeAsync();
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == tv_forgot.getId()) {	// 비밀번호 찾기
			Intent i = new Intent(this, ForgotPwdActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		} else if( v.getId() == tv_signup.getId()) {  // 회원가입
			Intent i = new Intent(this, SignupActivity.class);
			startActivityForResult(i, LOGIN_ACTIVITY);
			overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			
		} else if( v.getId() == btn_login.getId()) {	//로그인
			if(!checkBlank())
				return;
			
			String md5 = md5 = Crypto.encrypt(et_pwd.getText().toString()).replace("\n", "");
			
			ArrayList<Parameter> params = new ArrayList<Parameter>(); 
			params.add(new Parameter("email", et_id.getText().toString()));
			params.add(new Parameter("pw", md5));
			
			LoadingDialog.showLoading(this);
			new GeneralHttpTask(loginListener, params, getApplicationContext()).execute(REST_URL + LOGIN);
		} else if ( v.getId() == facebookLogin.getId()) {
			if (Session.getActiveSession() == null) {
				Session.openActiveSession(this, true,
						new Session.StatusCallback() {

							// callback when session changes state
							@Override
							public void call(Session session,
									SessionState state, Exception exception) {
								onSessionStateChange(session, state, exception);
							}
						});
			} else {
				Session.getActiveSession().closeAndClearTokenInformation();
				Session.setActiveSession(null);
			}
			
		}
	}
	
	public boolean checkBlank() {
		if (et_id.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (et_pwd.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public void parseJson(String str) {
		JSONObject obj = null;
		
		try{
			obj = new JSONObject(str);
			String msg = null;
			
			if ( obj.getString("login").equals("true") ) {
				
				Log.d(TAG, "로그인 성공");
				Toast.makeText(getApplicationContext(), "로그인되었습니다.", Toast.LENGTH_SHORT).show();
				LoadingDialog.hideLoading();
				commitLogin();
				
			} else {
				// 로그인 실패
				// 실패 msg 출력
				if(obj.length() > 1)  {
					msg = obj.getString("msg");
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    		dialog.dismiss();     //닫기
					    }
					});
					alert.setMessage(msg);
					alert.show();
				}
				LoadingDialog.hideLoading();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, obj.toString());
			
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	public void commitLogin() {

		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		
		// 자동 로그인 체크시
		if(cb_auto.isChecked()) {
			SharedPreferences.Editor ed = prefs.edit();
			ed.putBoolean(PREFERENCE_AUTO_LOGIN, true);
			ed.putString(PREFERENCE_USER_ID, et_id.getText().toString());
			ed.putString(PREFERENCE_USER_PWD, Crypto.encrypt(et_pwd.getText().toString()).replace("\n", ""));
			ed.commit();
		} 
		
		// 로그인 상태 저장
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean(PREFERENCE_IS_LOGIN, true);
		ed.commit();
		
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		
		if(requestCode == LOGIN_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		}
		
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
	
	protected OnCompleteListener loginListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "loginListener onTaskFailed");
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			LoadingDialog.hideLoading();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
	
	protected OnCompleteListener snsListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "loginListener onTaskFailed");
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			LoadingDialog.hideLoading();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
	
	
}
