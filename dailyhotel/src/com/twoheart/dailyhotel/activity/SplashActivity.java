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

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ABTestPreference;
import com.twoheart.dailyhotel.util.ABTestPreference.OnABTestListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class SplashActivity
		extends BaseActivity implements Constants, ErrorListener
{
	private static final int PROGRESS_CIRCLE_COUNT = 3;

	private Dialog alertDlg;
	protected HashMap<String, String> regPushParams;

	private View mProgressView;
	private View[] mCircleViewList;
	private boolean mIsRequestLogin;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					moveToLoginStep();
					break;

				case 1:
					if (mProgressView.getVisibility() != View.VISIBLE)
					{
						mProgressView.setVisibility(View.VISIBLE);

						startSplashLoad();
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		mIsRequestLogin = false;

		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putBoolean(KEY_PREFERENCE_REGION_SETTING, false);
		editor.remove(KEY_PREFERENCE_GCM_ID);
		editor.commit();
		editor.apply();

		mProgressView = findViewById(R.id.progressLayout);
		mProgressView.setVisibility(View.INVISIBLE);

		mCircleViewList = new View[PROGRESS_CIRCLE_COUNT];

		for (int i = 0; i < PROGRESS_CIRCLE_COUNT; i++)
		{
			mCircleViewList[i] = findViewById(R.id.iv_splash_circle1 + i);
		}
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(this).recordScreen(Screen.SPLASH);
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// 비행기 모드
		boolean isAirplainMode = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false;
		boolean isNetworkAvailable = VolleyHttpClient.isAvailableNetwork();

		if (isAirplainMode && !isNetworkAvailable)
		{
			showDisabledNetworkPopup();
		} else if (!isAirplainMode && !isNetworkAvailable)
		{
			showDisabledNetworkPopup();
		} else
		{
			if (mIsRequestLogin == false)
			{
				mIsRequestLogin = true;

				mHandler.sendEmptyMessageDelayed(0, 1500);
			}

			if (mProgressView.getVisibility() != View.VISIBLE)
			{
				mHandler.removeMessages(1);
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		mHandler.removeMessages(1);
	}

	private void showDisabledNetworkPopup()
	{
		if (alertDlg != null)
		{
			if (alertDlg.isShowing() == true)
			{
				alertDlg.dismiss();
			}

			alertDlg = null;
		}

		if (isFinishing() == true)
		{
			return;
		}

		View.OnClickListener posListener = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				alertDlg.dismiss();

				if (VolleyHttpClient.isAvailableNetwork())
				{
					if (mProgressView.getVisibility() != View.VISIBLE)
					{
						mHandler.removeMessages(1);
						mHandler.sendEmptyMessageDelayed(1, 1000);
					}

					moveToLoginStep();
				} else
				{
					mHandler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							showDisabledNetworkPopup();
						}
					}, 100);
				}
			}
		};

		View.OnClickListener negaListener = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				alertDlg.dismiss();
			}
		};

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					alertDlg.dismiss();
					finish();
					return true;
				}
				return false;
			}
		};

		alertDlg = createSimpleDialog(0, getString(R.string.dialog_btn_text_waiting), 0, getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi), getString(R.string.dialog_btn_text_retry), getString(R.string.dialog_btn_text_setting), posListener, negaListener);
		alertDlg.setOnKeyListener(keyListener);
		alertDlg.show();
	}

	private void startSplashLoad()
	{
		for (int i = 0; i < PROGRESS_CIRCLE_COUNT; i++)
		{
			final int idx = i;
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mCircleViewList[idx].setVisibility(View.VISIBLE);
					mCircleViewList[idx].startAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_load));
				}
			}, 250 * (i + 1));
		}
	}

	private void moveToLoginStep()
	{
		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
		{

			String id = sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);

			Map<String, String> loginParams = new HashMap<String, String>();

			if (accessToken != null)
			{
				loginParams.put("accessToken", accessToken);
			} else
			{
				loginParams.put("email", id);
			}

			loginParams.put("pw", pw);

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, this));
		}

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(), null, mAppVersionJsonResponseListener, this));
	}

	private void showMainActivity()
	{
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onErrorResponse(VolleyError error)
	{
		super.onErrorResponse(error);
		finish();
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.fade_out);
	}

	private void requestConfigurationABTest()
	{
		// ABTest
		ABTestPreference.getInstance(getApplicationContext()).requestConfiguration(getApplicationContext(), mQueue, new OnABTestListener()
		{
			@Override
			public void onPostExecute()
			{
				showMainActivity();
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				String result = null;

				if (response != null)
				{
					result = response.getString("login");
				}

				if ("true".equalsIgnoreCase(result) == false)
				{
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

				} else
				{
					// 로그인 성공
					VolleyHttpClient.createCookie();
					// 로그인에 성공하였으나 GCM을 등록하지 않은 유저의 경우 인덱스를 가져와 push_id를 업그레이드 하는 절차 시작.
				}

			} catch (JSONException e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mAppVersionJsonResponseListener = new DailyHotelJsonResponseListener()
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

				ExLog.e(" / onResponse : url = " + url + " / response = " + response.toString());
				ExLog.e(" / onResponse : Stores = " + RELEASE_STORE);

				SharedPreferences.Editor editor = sharedPreference.edit();

				if (RELEASE_STORE == Stores.PLAY_STORE)
				{
					ExLog.d("RELEASE_PLAY_STORE : true");

					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("play_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("play_min"));
				} else if (RELEASE_STORE == Stores.T_STORE)
				{
					ExLog.d("RELEASE_T_STORE : true");

					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("tstore_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("tstore_min"));
				} else if (RELEASE_STORE == Stores.N_STORE)
				{
					ExLog.d("RELEASE_N_STORE : true");
					editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("nstore_max"));
					editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("nstore_min"));
				}

				editor.commit();

				int maxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0").replace(".", ""));
				int minVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MIN_VERSION_NAME, "1.0.0").replace(".", ""));
				int currentVersion = Integer.parseInt(getPackageManager().getPackageInfo(getPackageName(), 0).versionName.replace(".", ""));
				int skipMaxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_SKIP_MAX_VERSION, "1.0.0").replace(".", ""));

				ExLog.e("MIN / MAX / CUR / SKIP : " + minVersion + " / " + maxVersion + " / " + currentVersion + " / " + skipMaxVersion);

				if (minVersion > currentVersion)
				{ // 강제 업데이트

					if (isFinishing() == true)
					{
						return;
					}

					View.OnClickListener posListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
							marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

							if (marketLaunch.resolveActivity(getPackageManager()) == null)
							{
								marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
							}

							startActivity(marketLaunch);
							finish();
						}
					};

					OnCancelListener cancelListener = new OnCancelListener()
					{
						@Override
						public void onCancel(DialogInterface dialog)
						{
							setResult(RESULT_CANCELED);
							finish();
						}
					};

					showSimpleDialog(0, getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_please_update_new_version), getString(R.string.dialog_btn_text_update), posListener, cancelListener);

				} else if ((maxVersion > currentVersion) && (skipMaxVersion != maxVersion))
				{
					if (isFinishing() == true)
					{
						return;
					}

					View.OnClickListener posListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
							marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

							if (marketLaunch.resolveActivity(getPackageManager()) == null)
							{
								marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
							}

							startActivity(marketLaunch);
							finish();
						}
					};

					final OnCancelListener cancelListener = new OnCancelListener()
					{
						@Override
						public void onCancel(DialogInterface dialog)
						{
							SharedPreferences.Editor editor = sharedPreference.edit();
							editor.putString(KEY_PREFERENCE_SKIP_MAX_VERSION, sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0"));
							editor.commit();

							requestConfigurationABTest();
						}
					};

					View.OnClickListener negListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							cancelListener.onCancel(null);
						}
					};

					showSimpleDialog(0, getString(R.string.dialog_title_notice), 0, getString(R.string.dialog_msg_update_now), getString(R.string.dialog_btn_text_update), getString(R.string.dialog_btn_text_cancel), posListener, negListener, cancelListener, null, false);
				} else
				{
					requestConfigurationABTest();
				}

			} catch (Exception e)
			{
				onError(e);
			}
		}
	};
}
