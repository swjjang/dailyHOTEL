/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * SplashActivity (로딩화면)
 * 
 * 어플리케이션 처음 시작 시 나타나는 화면이며, 이는 MainActivity에 의해서
 * 호출된다. SplashActivity는 어플리케이션 처음 실행 시 가장 먼저 나타나는
 * 화면이나 어플리케이션의 주 화면은 아니므로 MainActivity가 처음 실행됐을 시
 * 호출된다. SplashActivity는 어플리케이션이 최신 버전인지 확인하며, 자동
 * 로그인이 필요한 경우 수행하는 일을 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.CreditFragment;
import com.twoheart.dailyhotel.GCMIntentService;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.layout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SplashActivity extends BaseActivity implements Constants,
		DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "SplashActivity";
	private RequestQueue mQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarHide();
		setContentView(R.layout.activity_splash);

		mQueue = VolleyHttpClient.getRequestQueue();

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (!sharedPreference.getBoolean(KEY_PREFERENCE_GCM, false)) {
			GCMIntentService.unregisterToken(getApplicationContext());
			GCMIntentService.registerToken(getApplicationContext());
		}

		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {

			String id = sharedPreference
					.getString(KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(
					KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD,
					null);

			Map<String, String> loginParams = new HashMap<String, String>();

			if (accessToken != null) {
				loginParams.put("accessToken", accessToken);
			} else {
				loginParams.put("email", id);
			}

			loginParams.put("pw", pw);

			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_LOGIN).toString(), loginParams,
					this, this));
		}

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION)
				.toString(), null, this, this));

	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_LOGIN)) {

			try {
				if (!response.getBoolean("login")) {
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

				} else {
					VolleyHttpClient.createCookie();
					
				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
			}

		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {
			// ed.putString(PREFERENCE_NEW_EVENT,
			// response.getString("new_event"));

			try {
				Log.d(TAG, response.getString("play_min"));
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
			}

			try {
				SharedPreferences.Editor editor = sharedPreference.edit();
				editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
						response.getString("play_max"));
				editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
						response.getString("play_min"));
				
//				editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
//						response.getString("tstore_max"));
//				editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
//						response.getString("tstore_min"));
				
//				editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
//						response.getString("nstore_max"));
//				editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
//						response.getString("nstore_min"));
				
				editor.commit();

				int minVersion = Integer
						.parseInt(sharedPreference.getString(
								KEY_PREFERENCE_MIN_VERSION_NAME, null).replace(
								".", ""));
				int currentVersion = Integer.parseInt(this.getPackageManager()
						.getPackageInfo(this.getPackageName(), 0).versionName
						.replace(".", ""));

				if (minVersion > currentVersion) { // 강제 업데이트
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							SplashActivity.this);
					alertDialog
							.setTitle("공지")
							.setMessage("dailyHOTEL의 새로운 버전이 출시되었습니다. 업데이트해주세요")
							.setCancelable(false)
							.setPositiveButton("업데이트",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent marketLaunch = new Intent(
													Intent.ACTION_VIEW);
											marketLaunch.setData(Uri
													.parse(URL_STORE_GOOGLE_DAILYHOTEL));
//											marketLaunch.setData(Uri
//													.parse(URL_STORE_T_DAILYHOTEL));
											startActivity(marketLaunch);
											finish();
										}
									});
					AlertDialog alert = alertDialog.create();
					alert.show();
				} else {
					// sleep 2 second
					Handler h = new Handler();
					h.postDelayed(new Runnable() {
						public void run() {
							setResult(RESULT_OK);
							finish();

						}
					}, 2000);
				}

			} catch (UnsupportedOperationException e) {
				Toast.makeText(this, "구글 플레이 서비스를 이용할 수 있는 기기이어야 합니다.",
						Toast.LENGTH_LONG).show();
				finish();
			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();
		Toast.makeText(getApplicationContext(), "네트워크 상태를 확인해주세요",
				Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onBackPressed() {
		return;
	}
	
}
