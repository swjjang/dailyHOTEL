/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditFragment (적립금 화면)
 * 
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어 
 * 해당 화면을 띄워주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
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
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

/**
 * 적립금 확인 페이지.
 * @author jangjunho
 *
 */
public class CreditFragment extends BaseFragment implements Constants,
		OnClickListener, DailyHotelJsonResponseListener,
		DailyHotelStringResponseListener {

	private static final String TAG = "CreditFragment";

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
		
		tvCredit.setPaintFlags(tvCredit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // underlining
		
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
		mHostActivity.setActionBar(R.string.actionbar_title_credit_frag);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
				this, mHostActivity));
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == btnInvite.getId()) {
			try {
				/**
				 * TODO : KAKAO LINK API 교체
				 */
				String msg = getString(R.string.kakaolink_msg_prefix) + mRecommendCode + getString(R.string.kakaolink_msg_suffix);
				KakaoLinkManager.newInstance(getActivity()).sendInviteMsgKakaoLink(msg);
			} catch (Exception e) {
				Log.d(TAG, "kakao link error " + e.toString());
			}

		} else if (v.getId() == tvCredit.getId()) {
			((MainActivity) mHostActivity).addFragment(CreditListFragment.newInstance(mCreditList));

		} else if (v.getId() == btnLogin.getId()) {
			Intent i = new Intent(mHostActivity, LoginActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

		} else if (v.getId() == btnSignup.getId()) {
			Intent i = new Intent(mHostActivity, SignupActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}

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

					unLockUI();
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
				onError(e);
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
				onError(e);
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
				unLockUI();

			} catch (Exception e) {
				onError(e);
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
					unLockUI();
					loadLoginProcess(false);
				}

			} else {
				onError();
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
				onError(e);
			}
		}
	}

}