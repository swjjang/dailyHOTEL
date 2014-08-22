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
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SplashActivity extends BaseActivity implements Constants,
DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "SplashActivity";

	private static final int VALUE_WEB_API_RESPONSE_NEW_EVENT_NOTIFY = 1;
	private static final int VALUE_WEB_API_RESPONSE_NEW_EVENT_NONE = 0;
	private static final int DURING_SPLASH_ACTIVITY_SHOW = 1000;
	private boolean isDialogShown = false;

	private Dialog alertDlg;

	private GoogleCloudMessaging mGcm;

	protected HashMap<String, String> regPushParams;

	private ImageView ivCircle1;
	private ImageView ivCircle2;
	private ImageView ivCircle3;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Anroid 4.4 이상에서 Android StatusBar와 Android NavigationBar를
		// Translucent하게 해주는 API를 사용하도록 한다.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTheme(R.style.AppTheme_Translucent);
		}

		setActionBarHide();
		setContentView(R.layout.activity_splash);

		ivCircle1 = (ImageView)findViewById(R.id.iv_splash_circle1);
		ivCircle2 = (ImageView)findViewById(R.id.iv_splash_circle2);
		ivCircle3 = (ImageView)findViewById(R.id.iv_splash_circle3);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 비행기 모드
		boolean isAirplainMode = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true:false;
		boolean isNetworkAvailable = VolleyHttpClient.isAvailableNetwork();
		android.util.Log.e("STATUS",isAirplainMode + " / " + isNetworkAvailable);
		startSplashLoad();

		if(isAirplainMode && !isNetworkAvailable) {
			Builder builder = new AlertDialog.Builder(SplashActivity.this);

			builder.setTitle("잠시만요!");
			builder.setMessage(getString(R.string.dialog_msg_network_please_off_airplain));
			builder.setCancelable(false);
			builder.setPositiveButton("확인",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (VolleyHttpClient.isAvailableNetwork()) {
						moveToLoginStep();
					} else {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								alertDlg.show();									
							}
						}, 100);
					}
				}
			});
			builder.setNegativeButton("설정",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					dialog.dismiss();
				}
			});
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK){
						dialog.dismiss();
						finish();
						return true;
					}
					return false;
				}
			});

			alertDlg = builder.create();
			alertDlg.show();
		}

		else if (!isAirplainMode && !isNetworkAvailable) {

			if(alertDlg == null) {

				Builder builder = new AlertDialog.Builder(SplashActivity.this);

				builder.setTitle("잠시만요!");
				builder.setMessage(getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi));
				builder.setCancelable(false);
				builder.setPositiveButton("재시도",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (VolleyHttpClient.isAvailableNetwork()) {
							moveToLoginStep();
						} else {
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									alertDlg.show();									
								}
							}, 100);
						}
					}
				});
				builder.setNegativeButton("설정",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						dialog.dismiss();
					}
				});
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if(keyCode == KeyEvent.KEYCODE_BACK){
							dialog.dismiss();
							finish();
							return true;
						}
						return false;
					}
				});

				alertDlg = builder.create();
			}

			alertDlg.show();

		} else {
			moveToLoginStep();
		}

	}

	private void startSplashLoad() {
		final Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.splash_load);
		final Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.splash_load);
		final Animation fade3 = AnimationUtils.loadAnimation(this, R.anim.splash_load);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ivCircle1.setVisibility(View.VISIBLE);
				ivCircle1.startAnimation(fade1);
			}
		}, 250);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ivCircle2.setVisibility(View.VISIBLE);
				ivCircle2.startAnimation(fade2);
			}
		}, 500);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ivCircle3.setVisibility(View.VISIBLE);
				ivCircle3.startAnimation(fade3);
			}
		}, 750);		
	}

	private void moveToLoginStep() {
		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {

			String id = sharedPreference
					.getString(KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(
					KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD,
					null);

			Map<String, String> loginParams = new HashMap<String, String>();

			if (accessToken != null) loginParams.put("accessToken", accessToken);
			else loginParams.put("email", id);

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
					// 로그인 성공
					VolleyHttpClient.createCookie();
					// 로그인에 성공하였으나 GCM을 등록하지 않은 유저의 경우 인덱스를 가져와 push_id를 업그레이드 하는 절차 시작.
				}

			} catch (JSONException e) {
				onError(e);
			}

		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {

			try {
				
				android.util.Log.e("APP_VERSIONS", response.toString());
				
				SharedPreferences.Editor editor = sharedPreference.edit();

				if (RELEASE_STORE == Stores.PLAY_STORE) {
					android.util.Log.e("RELEASE_PLAY_STORE", "true");
					
					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
							response.getString("play_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
							response.getString("play_min"));
				} else if (RELEASE_STORE == Stores.T_STORE) {
					android.util.Log.e("RELEASE_T_STORE", "true");
					
					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
							response.getString("tstore_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
							response.getString("tstore_min"));
				} else if (RELEASE_STORE == Stores.N_STORE) {
					android.util.Log.e("RELEASE_N_STORE", "true");
					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME,
							response.getString("nstore_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME,
							response.getString("nstore_min"));
				} 

				editor.commit();
				
				int maxVersion = Integer.parseInt(sharedPreference.getString(
						KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0").replace(".",""));
				int minVersion = Integer.parseInt(sharedPreference.getString(
						KEY_PREFERENCE_MIN_VERSION_NAME, "1.0.0").replace(".",""));
				int currentVersion = Integer.parseInt(this.getPackageManager()
						.getPackageInfo(this.getPackageName(), 0).versionName.replace(".", ""));
				int skipMaxVersion = Integer.parseInt(sharedPreference
						.getString(KEY_PREFERENCE_SKIP_MAX_VERSION, "1.0.0").replace(".", ""));

				final int newEventFlag = Integer.parseInt(response.getString("new_event"));
				
				android.util.Log.e("MIN / MAX / CUR / SKIP", minVersion+" / "+maxVersion+" / "+currentVersion+" / "+skipMaxVersion);

				if (minVersion > currentVersion) { // 강제 업데이트
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
					alertDialog
					.setTitle(getString(R.string.dialog_title_notice))
					.setMessage(getString(R.string.dialog_msg_please_update_new_version)) 
					.setCancelable(false)
					.setPositiveButton("업데이트",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							Intent marketLaunch = new Intent(
									Intent.ACTION_VIEW);
							marketLaunch.setData(Uri.parse(Util
									.storeReleaseAddress()));
							startActivity(marketLaunch);
							finish();
						}
					});
					AlertDialog alert = alertDialog.create();
					alert.show();
				} else if ((maxVersion > currentVersion)
						&& (skipMaxVersion != maxVersion)) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							SplashActivity.this);
					alertDialog
					.setTitle(getString(R.string.dialog_title_notice))
					.setMessage(getString(R.string.dialog_msg_update_now))
					.setCancelable(true)
					.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							SharedPreferences.Editor editor = sharedPreference
									.edit();
							editor.putString(
									KEY_PREFERENCE_SKIP_MAX_VERSION,
									sharedPreference
									.getString(
											KEY_PREFERENCE_MAX_VERSION_NAME,
											"1.0.0"));

							editor.commit();
							showMainActivity(newEventFlag);
						}
					})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					})
					.setPositiveButton("업데이트",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							Intent marketLaunch = new Intent(
									Intent.ACTION_VIEW);
							marketLaunch.setData(Uri.parse(Util
									.storeReleaseAddress()));
							startActivity(marketLaunch);
						}
					});
					AlertDialog alert = alertDialog.create();
					alert.show();
				} else {
					showMainActivity(newEventFlag);
				}

			} catch (Exception e) {
				onError(e);

			}
		} 
	}

	private void showMainActivity(final int newEventFlag) {
		// sleep 2 second
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				if (newEventFlag == VALUE_WEB_API_RESPONSE_NEW_EVENT_NOTIFY)
					setResult(CODE_RESULT_ACTIVITY_SPLASH_NEW_EVENT);
				else if (newEventFlag == VALUE_WEB_API_RESPONSE_NEW_EVENT_NONE)
					setResult(RESULT_OK);
				finish();

			}
		}, DURING_SPLASH_ACTIVITY_SHOW);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.fade_out);
	}

}
