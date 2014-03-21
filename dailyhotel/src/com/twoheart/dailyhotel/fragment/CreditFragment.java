package com.twoheart.dailyhotel.fragment;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.obj.Credit;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.KakaoLink;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;


public class CreditFragment extends Fragment implements Constants,
		OnClickListener, ErrorListener, DailyHotelJsonResponseListener,
		DailyHotelResponseListener {

	private static final String TAG = "CreditFragment";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private Button btnInvite;
	private TextView tvBonus, tvRecommenderCode;
	private TextView tvCredit;
	private String mRecommendCode;
	private ArrayList<Credit> mCreditList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_credit, null);
		mHostActivity = (MainActivity) getActivity();
		mQueue = VolleyHttpClient.getRequestQueue();

		// ActionBar Setting
		mHostActivity.setActionBar("적립금");

		btnInvite = (Button) view.findViewById(R.id.btn_credit_invite_frd);
		tvCredit = (TextView) view.findViewById(R.id.tv_credit_history);
		tvRecommenderCode = (TextView) view
				.findViewById(R.id.tv_credit_recommender_code);
		tvBonus = (TextView) view.findViewById(R.id.tv_credit_money);
		btnInvite.setOnClickListener(this);
		tvCredit.setOnClickListener(this);

		LoadingDialog.showLoading(mHostActivity);

		mQueue.add(new DailyHotelRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
				CreditFragment.this, CreditFragment.this));

		return view;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == btnInvite.getId()) {
			try {
//				sendUrlLink(v);
				sendAppData(v);
			} catch (Exception e) {
				Log.d(TAG, "kakao link error " + e.toString());
			}

		} else if (v.getId() == tvCredit.getId()) {
			mHostActivity.addFragment(new CreditListFragment(mCreditList));

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

		kakaoLink
				.openKakaoAppLink(
						mHostActivity,
						"http://link.kakao.com/?test-android-app",
						"좋은 어플 추천해 드려요~\n"
								+ "오늘 남은 객실만 최대 70% 할인하는 데일리호텔이에요.\n"
								+ "추천인코드 : " + mRecommendCode
								+ "을 입력하면 5,000원 바로 할인!",
								mHostActivity.getPackageName(),
								mHostActivity
								.getPackageManager()
								.getPackageInfo(
										mHostActivity.getPackageName(), 0).versionName,
						"dailyHOTEL 초대 메시지", "UTF-8", metaInfoArray);
	}

	private void alert(String message) {
		new AlertDialog.Builder(mHostActivity)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.app_name).setMessage(message)
				.setPositiveButton(android.R.string.ok, null).create().show();
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			try {
				if (!response.getBoolean("login")) {
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = mHostActivity.sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();
					
					mHostActivity.replaceFragment(new NoLoginFragment());

				} else {
					// credit 요청
					mQueue.add(new DailyHotelRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_RESERVE_SAVED_MONEY).toString(), null,
							CreditFragment.this, CreditFragment.this));
					
				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();
				
				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
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
						CreditFragment.this, CreditFragment.this));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
				
				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
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

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
				
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			} finally {
				LoadingDialog.hideLoading();
			}
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		mHostActivity.addFragment(new ErrorFragment());
		LoadingDialog.hideLoading();
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();
			if (result.equals("alive")) { // session alive
				// credit 요청
				mQueue.add(new DailyHotelRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_RESERVE_SAVED_MONEY).toString(), null,
						CreditFragment.this, CreditFragment.this));

			} else if (result.equals("dead")) { // session dead
				
				// 재로그인
				if (mHostActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {
					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("email", mHostActivity.sharedPreference
							.getString(KEY_PREFERENCE_USER_ID, null));
					loginParams.put("pw", mHostActivity.sharedPreference.getString(
							KEY_PREFERENCE_USER_PWD, null));
	
					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(), loginParams,
							CreditFragment.this, CreditFragment.this));
				} else {
					mHostActivity.replaceFragment(new NoLoginFragment());
					LoadingDialog.hideLoading();
					
					startActivity(new Intent(mHostActivity, LoginActivity.class));
				}

			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
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
						CreditFragment.this, CreditFragment.this));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
				
				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}