package com.twoheart.dailyhotel;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.twoheart.dailyhotel.activity.AboutActivity;
import com.twoheart.dailyhotel.activity.FAQActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.NoticeActivity;
import com.twoheart.dailyhotel.activity.VersionActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class SettingFragment extends BaseFragment implements Constants,
		DailyHotelStringResponseListener, DailyHotelJsonResponseListener,
		OnClickListener {

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private TextView tvNotice, tvHelp, tvMail, tvLogin, tvEmail, tvCall,
			tvAbout, tvVersion;
	private LinearLayout llVersion;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting, container, false);

		// ActionBar Setting
		mHostActivity = (MainActivity) getActivity();
		mQueue = VolleyHttpClient.getRequestQueue();

		tvNotice = (TextView) view.findViewById(R.id.tv_setting_notice);
		tvVersion = (TextView) view.findViewById(R.id.tv_setting_version);
		llVersion = (LinearLayout) view.findViewById(R.id.ll_setting_version);
		tvHelp = (TextView) view.findViewById(R.id.tv_setting_help);
		tvMail = (TextView) view.findViewById(R.id.tv_setting_mail);
		tvLogin = (TextView) view.findViewById(R.id.tv_setting_login);
		tvEmail = (TextView) view.findViewById(R.id.tv_setting_email);
		tvCall = (TextView) view.findViewById(R.id.tv_setting_call);
		tvAbout = (TextView) view.findViewById(R.id.tv_setting_introduction);

		tvNotice.setOnClickListener(this);
		llVersion.setOnClickListener(this);
		tvHelp.setOnClickListener(this);
		tvMail.setOnClickListener(this);
		tvLogin.setOnClickListener(this);
		tvEmail.setOnClickListener(this);
		tvCall.setOnClickListener(this);
		tvAbout.setOnClickListener(this);

		try {
			String currentVersion = mHostActivity.getPackageManager()
					.getPackageInfo(mHostActivity.getPackageName(), 0).versionName;

			tvVersion.setText(currentVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mHostActivity.setActionBar("설정");
		
		LoadingDialog.showLoading(mHostActivity);
		
		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null, this, mHostActivity));
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == tvNotice.getId()) {
			Intent i = new Intent(mHostActivity, NoticeActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);

		} else if (v.getId() == llVersion.getId()) {

			Intent i = new Intent(mHostActivity, VersionActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);

		} else if (v.getId() == tvHelp.getId()) {

			Intent i = new Intent(mHostActivity, FAQActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);

		} else if (v.getId() == tvMail.getId()) {

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "help@dailyhotel.co.kr" });
			intent.putExtra(Intent.EXTRA_SUBJECT, "데일리호텔에 문의합니다");
			intent.putExtra(Intent.EXTRA_TEXT, "데일리호텔의 안드로이드 어플리케이션에 관한 문의입니다.");
			startActivity(Intent.createChooser(intent, "이메일 어플리케이션 선택"));

		} else if (v.getId() == tvLogin.getId()) {

			if (tvLogin.getText().equals("로그아웃")) { // 로그인 되어 있는 상태
				AlertDialog.Builder alert_confirm = new AlertDialog.Builder(
						mHostActivity);
				alert_confirm
						.setMessage("로그아웃 하시겠습니까?")
						.setCancelable(false)
						.setPositiveButton("로그아웃",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										mQueue.add(new DailyHotelJsonRequest(
												Method.GET,
												new StringBuilder(
														URL_DAILYHOTEL_SERVER)
														.append(URL_WEBAPI_USER_LOGOUT)
														.toString(), null,
												SettingFragment.this,
												mHostActivity));
										VolleyHttpClient.destroyCookie();

										SharedPreferences.Editor ed = mHostActivity.sharedPreference
												.edit();
										ed.putBoolean(
												KEY_PREFERENCE_AUTO_LOGIN,
												false);
										ed.putString(KEY_PREFERENCE_USER_ID,
												null);
										ed.putString(KEY_PREFERENCE_USER_PWD,
												null);
										ed.commit();

										invalidateLoginButton(false, "");

										Toast.makeText(mHostActivity,
												"로그아웃되었습니다", Toast.LENGTH_SHORT)
												.show();

										if (Session.getActiveSession() != null)
											if (Session.getActiveSession()
													.isOpened()) {
												Session.getActiveSession()
														.closeAndClearTokenInformation();
												Session.setActiveSession(null);
											}

									}
								})
						.setNegativeButton("취소",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								});
				AlertDialog alert = alert_confirm.create();
				alert.show();

			} else { // 로그아웃 상태
				Intent i = new Intent(mHostActivity, LoginActivity.class);
				startActivityForResult(i, CODE_REQUEST_ACTIVITY_LOGIN);
				mHostActivity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.hold);
			}

		} else if (v.getId() == tvCall.getId()) {
			Intent i = new Intent(Intent.ACTION_DIAL,
					Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
			startActivity(i);
		} else if (v.getId() == tvAbout.getId()) {
			Intent i = new Intent(mHostActivity, AboutActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN) {
			if (resultCode == Activity.RESULT_OK) {
				mHostActivity.selectMenuDrawer(mHostActivity.menuHotelListFragment);
			}
		}
	}
	
	private void invalidateLoginButton(boolean login, String email) {
		tvEmail.setText(email);
		
		if (login) {
			tvLogin.setText("로그아웃");
		} else {
			tvLogin.setText("로그인");
		}
		
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();

			if (result.equals("alive")) { // session alive
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_INFO).toString(), null, this,
						mHostActivity));

			} else {
				invalidateLoginButton(false, "");
				mListener.onLoadComplete(this, true);
			}
		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				JSONObject obj = response;
				String userEmail = obj.getString("email");
				
				if ((userEmail != null) && !(userEmail.equals("")) && !(userEmail.equals("null")))
					invalidateLoginButton(true, userEmail);
				else
					invalidateLoginButton(true, "");
				
				mListener.onLoadComplete(this, true);

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				invalidateLoginButton(true, "");
			}

		}

	}
}
