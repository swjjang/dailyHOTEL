package com.twoheart.dailyhotel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.KakaoLink;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class CreditFragment extends BaseFragment implements Constants,
		OnClickListener, DailyHotelJsonResponseListener,
		DailyHotelStringResponseListener {

	private static final String TAG = "CreditFragment";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private RelativeLayout rlCreditNotLoggedIn;
	private LinearLayout llCreditLoggedIn, btnInvite;
	private Button btnLogin, btnSignup;
	private TextView tvBonus, tvRecommenderCode;
	private TextView tvCredit;
	private String mRecommendCode;
	private ArrayList<Credit> mCreditList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_credit, container, false);
		mHostActivity = (MainActivity) getActivity();
		mQueue = VolleyHttpClient.getRequestQueue();

		rlCreditNotLoggedIn = (RelativeLayout) view
				.findViewById(R.id.rl_credit_not_logged_in);
		llCreditLoggedIn = (LinearLayout) view
				.findViewById(R.id.ll_credit_logged_in);

		btnInvite = (LinearLayout) view
				.findViewById(R.id.btn_credit_invite_frd);
		tvCredit = (TextView) view.findViewById(R.id.tv_credit_history);
		tvRecommenderCode = (TextView) view
				.findViewById(R.id.tv_credit_recommender_code);
		tvBonus = (TextView) view.findViewById(R.id.tv_credit_money);
		btnLogin = (Button) view.findViewById(R.id.btn_no_login_login);
		btnSignup = (Button) view.findViewById(R.id.btn_no_login_signup);
		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);
		btnInvite.setOnClickListener(this);
		tvCredit.setOnClickListener(this);

		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public void onResume() {
		super.onResume();
		// ActionBar Setting
		mHostActivity.setActionBar("적립금");

		LoadingDialog.showLoading(mHostActivity);

		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
				this, mHostActivity));
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == btnInvite.getId()) {
			try {
				// sendUrlLink(v);
				sendAppData(v);
			} catch (Exception e) {
				Log.d(TAG, "kakao link error " + e.toString());
			}

		} else if (v.getId() == tvCredit.getId()) {
			mHostActivity.addFragment(CreditListFragment.newInstance(mCreditList));

		} else if (v.getId() == btnLogin.getId()) {
			Intent i = new Intent(mHostActivity, LoginActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);

		} else if (v.getId() == btnSignup.getId()) {
			Intent i = new Intent(mHostActivity, SignupActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);
		}

	}

	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application context for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(mHostActivity);

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			alert("카카오톡이 설치되어 있지 않습니다.");
			return;
		}

		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 */
		kakaoLink.openKakaoLink(
				mHostActivity,
				"http://dailyhotel.kr",
				"좋은 어플 추천해 드려요~\n" + "오늘 남은 객실만 최대 70% 할인하는 데일리호텔이에요."
						+ "추천인코드 : " + mRecommendCode + "을 입력하면 5,000원 바로 할인!",
				mHostActivity.getPackageName(),
				mHostActivity.getPackageManager().getPackageInfo(
						mHostActivity.getPackageName(), 0).versionName,
				"데일리호텔", "UTF-8");
	}

	/**
	 * Send App data
	 */
	public void sendAppData(View v) throws NameNotFoundException {
		ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

		// If application is support Android platform.
		Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("devicetype", "phone");
		// Play Store
		metaInfoAndroid.put("installurl", "http://kakaolink.dailyhotel.co.kr");
		// T Store
		// metaInfoAndroid.put("installurl", "http://tsto.re/0000412421");
		metaInfoAndroid.put("executeurl", "kakaoLinkTest://starActivity");

		// If application is support ios platform.
		Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
		metaInfoIOS.put("os", "ios");
		metaInfoIOS.put("devicetype", "phone");
		metaInfoIOS.put("installurl", "http://kakaolink.dailyhotel.co.kr");
		metaInfoIOS.put("executeurl", "kakaoLinkTest://starActivity");

		// add to array
		metaInfoArray.add(metaInfoAndroid);
		metaInfoArray.add(metaInfoIOS);

		// Recommended: Use application context for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(mHostActivity
				.getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent()) {
			alert("카카오톡이 설치되어 있지 않습니다.");
			return;
		}

		// String myId = prefs.getString(PREFERENCE_USER_ID, null);

		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 * @param metaInfoArray
		 */

		kakaoLink.openKakaoAppLink(
				mHostActivity,
				"http://link.kakao.com/?test-android-app",
				"좋은 어플 추천해 드려요~\n" + "오늘 남은 객실만 최대 70% 할인하는 데일리호텔이에요.\n"
						+ "추천인코드 : " + mRecommendCode + "을 입력하면 5,000원 바로 할인!",
				mHostActivity.getPackageName(),
				mHostActivity.getPackageManager().getPackageInfo(
						mHostActivity.getPackageName(), 0).versionName,
				"dailyHOTEL 초대 메시지", "UTF-8", metaInfoArray);
	}

	private void alert(String message) {
		new AlertDialog.Builder(mHostActivity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.app_name).setMessage(message)
				.setPositiveButton(android.R.string.ok, null).create().show();
	}

	private void loadLoginProcess(boolean loginSuccess) {
		if (loginSuccess) {
			rlCreditNotLoggedIn.setVisibility(View.GONE);
			llCreditLoggedIn.setVisibility(View.VISIBLE);

		} else {
			rlCreditNotLoggedIn.setVisibility(View.VISIBLE);
			llCreditLoggedIn.setVisibility(View.GONE);

		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			try {
				if (!response.getBoolean("login")) {
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = mHostActivity.sharedPreference
							.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

					mListener.onLoadComplete(this, true);
					loadLoginProcess(false);

				} else {
					VolleyHttpClient.createCookie();
					
					// credit 요청
					mQueue.add(new DailyHotelStringRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_RESERVE_SAVED_MONEY).toString(),
							null, this, mHostActivity));

				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}

		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				JSONObject obj = response;
				mRecommendCode = obj.getString("rndnum");
				tvRecommenderCode.setText(obj.getString("rndnum"));

				// 적립금 목록 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_BONUS_ALL).toString(), null,
						this, mHostActivity));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}

		} else if (url.contains(URL_WEBAPI_USER_BONUS_ALL)) {
			try {
				mCreditList = new ArrayList<Credit>();

				JSONObject obj = response;
				JSONArray arr = obj.getJSONArray("history");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject historyObj = arr.getJSONObject(i);
					String content = historyObj.getString("content");
					String expires = historyObj.getString("expires");
					String bonus = historyObj.getString("bonus");

					mCreditList.add(new Credit(content, bonus, expires));
				}

				loadLoginProcess(true);
				mListener.onLoadComplete(this, true);

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}
		}

	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();
			if (result.equals("alive")) { // session alive
				// credit 요청
				mQueue.add(new DailyHotelStringRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_RESERVE_SAVED_MONEY).toString(),
						null, this, mHostActivity));

			} else if (result.equals("dead")) { // session dead

				// 재로그인
				if (mHostActivity.sharedPreference.getBoolean(
						KEY_PREFERENCE_AUTO_LOGIN, false)) {
					String id = mHostActivity.sharedPreference.getString(
							KEY_PREFERENCE_USER_ID, null);
					String accessToken = mHostActivity.sharedPreference
							.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					String pw = mHostActivity.sharedPreference.getString(
							KEY_PREFERENCE_USER_PWD, null);

					Map<String, String> loginParams = new HashMap<String, String>();

					if (accessToken != null) {
						loginParams.put("accessToken", accessToken);
					} else {
						loginParams.put("email", id);
					}

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
							loginParams, this,
							mHostActivity));
				} else {
					mListener.onLoadComplete(this, true);
					loadLoginProcess(false);
				}

			} else {
				mListener.onLoadComplete(this, false);
			}

		} else if (url.contains(URL_WEBAPI_RESERVE_SAVED_MONEY)) {
			try {

				DecimalFormat comma = new DecimalFormat("###,##0");
				String str = comma.format(Integer.parseInt(response.trim()));
				tvBonus.setText(new StringBuilder(str).append("원"));

				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_INFO).toString(), null,
						this, mHostActivity));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}
		}
	}

}