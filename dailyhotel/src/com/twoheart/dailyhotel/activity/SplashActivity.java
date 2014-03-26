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
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.GCMIntentService;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.layout;
import com.twoheart.dailyhotel.fragment.CreditFragment;
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
	private static final String appPlayStoreUrl = "market://details?id=com.twoheart.dailyhotel";
	
	private RequestQueue mQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		if (!sharedPreference.getBoolean(KEY_PREFERENCE_GCM, false)) {
			GCMIntentService.unregisterToken(getApplicationContext());
			GCMIntentService.registerToken(getApplicationContext());
		}

		mQueue = VolleyHttpClient.getRequestQueue();
		
		VolleyHttpClient.cookie = null;
		VolleyHttpClient.cookieManager
				.removeAllCookie();
		
		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {
			
			String id = sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);
			
			Map<String, String> loginParams = new HashMap<String, String>();
			
			if (accessToken != null) {
				loginParams.put("accessToken", sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null));
			} else {
				loginParams.put("email", sharedPreference.getString(KEY_PREFERENCE_USER_ID, null));
			}
			
			loginParams.put("pw", pw);
			
			mQueue.add(new DailyHotelJsonRequest(Method.POST, 
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(),
					loginParams, SplashActivity.this, SplashActivity.this));
		}
		
		mQueue.add(new DailyHotelJsonRequest(Method.GET, 
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(),
				null, SplashActivity.this, SplashActivity.this));
		
		// sleep 2 second
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				finish();

			}
		}, 2000);
		
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		Log.d(TAG, url);
		
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
					// 사용자 정보 요청.
					mQueue.add(new DailyHotelJsonRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									 URL_WEBAPI_USER_INFO).toString(), null,
							this, this));
				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
			} 
			
		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {
//			ed.putString(PREFERENCE_NEW_EVENT, response.getString("new_event"));
			
			try {
				Log.d(TAG, response.getString("play_min"));
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
			}
				
			
			try {
				SharedPreferences.Editor editor = sharedPreference.edit();
				editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("play_max"));
				editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("play_min"));
				editor.commit();
				
				int minVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MIN_VERSION_NAME, null).replace(".", ""));
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
										public void onClick(DialogInterface dialog,
												int which) {
											Intent marketLaunch = new Intent(
													Intent.ACTION_VIEW);
											marketLaunch.setData(Uri
													.parse(appPlayStoreUrl));
											startActivity(marketLaunch);
											finish();
										}
									});
					AlertDialog alert = alertDialog.create();
					alert.show();
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
