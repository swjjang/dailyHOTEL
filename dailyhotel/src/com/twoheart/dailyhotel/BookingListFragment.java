/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingListFragment (예약 확인 화면)
 * 
 * 예약된 목록들을 보여주는 화면이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.activity.BookingTabActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.PaymentWaitActivity;
import com.twoheart.dailyhotel.adapter.BookingListAdapter;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

/**
 * 예약한 호텔의 리스트들을 출력.
 * @author jangjunho
 *
 */
public class BookingListFragment extends BaseFragment implements Constants,
OnItemClickListener, OnClickListener, DailyHotelJsonResponseListener,
DailyHotelStringResponseListener {

	private static final String TAG = "BookingListFragment";

	private ArrayList<Booking> mItems;
	private BookingListAdapter mAdapter;

	private RelativeLayout mEmptyLayout;
	private ListView mListView;
	private Button btnLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

		mListView = (ListView) view.findViewById(R.id.listview_booking);
		mEmptyLayout = (RelativeLayout) view.findViewById(R.id.layout_booking_empty);
		btnLogin = (Button) view.findViewById(R.id.btn_booking_empty_login);

		btnLogin.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mHostActivity.setActionBar(R.string.actionbar_title_booking_list_frag);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
						BookingListFragment.this, mHostActivity));
		
		Log.v("BookingListFragment", "BookingListFragment");
		RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordScreen("bookingList", "/bookings/");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnLogin.getId()) {
			android.util.Log.e("BtnLogin","true");
			Intent i = new Intent(mHostActivity, LoginActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView,
			int position, long id) {
		Intent i = null;
		Booking item = mItems.get(position);
		RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectBookingConfirmation", item.getHotel_name(), null);
		if (item.getPayType() == CODE_PAY_TYPE_CARD_COMPLETE || item.getPayType() == CODE_PAY_TYPE_ACCOUNT_COMPLETE) { // 카드결제 완료 || 가상계좌 완료
			i = new Intent(mHostActivity, BookingTabActivity.class);
		} else if (item.getPayType() == CODE_PAY_TYPE_ACCOUNT_WAIT) { // 가상계좌 입금대기
			i = new Intent(mHostActivity, PaymentWaitActivity.class);
		} 
		i.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, item);
		startActivityForResult(i, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE_REQUEST_ACTIVITY_BOOKING_DETAIL) {
			switch (resultCode) {
				case CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT:
					SimpleAlertDialog.build(getActivity(), getString(R.string.dialog_notice2), data.getStringExtra("msg"), getString(R.string.dialog_btn_text_confirm), null).show();
					break;
			}
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();
			if (result.equals("alive")) { // session alive
				// 예약 목록 요청.
				mQueue.add(new DailyHotelStringRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_RESERVE_MINE).toString(), null,
								BookingListFragment.this, mHostActivity));

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

					if (accessToken != null) loginParams.put("accessToken",accessToken);
					else loginParams.put("email", id);

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
									loginParams, BookingListFragment.this,
									mHostActivity));

					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
				} else {
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);

					unLockUI();
				}

			} else {
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);

				onError();
				unLockUI();
			}

		} else if (url.contains(URL_WEBAPI_RESERVE_MINE)) {
			if (!response.trim().equals("none")) {
				mItems = new ArrayList<Booking>();

				try {
					JSONObject obj = new JSONObject(response);
					JSONArray rsvArr = obj.getJSONArray("rsv");

					for (int i = 0; i < rsvArr.length(); i++) {
						JSONObject rsvObj = rsvArr.getJSONObject(i);

						//kcpno (depre)
						String hotel_name = rsvObj.getString("hotel_name");
						//room_name (depre)
						String sday = rsvObj.getString("sday");
						//rsv_idx (dpre)
						String hotel_idx = rsvObj.getString("hotel_idx");
						String bedType = rsvObj.getString("bed_type");
						int payType = rsvObj.getInt("pay_type");
						String tid = rsvObj.getString("tid");

						mItems.add(new Booking(sday, hotel_idx, hotel_name, bedType, payType, tid));
					}

					mAdapter = new BookingListAdapter(mHostActivity,
							R.layout.list_row_booking, mItems);
					mListView.setOnItemClickListener(this);
					mListView.setAdapter(mAdapter);

					unLockUI();

					// flag가 가상계좌 입금 대기에서 날아온경우 
					SharedPreferences pref = getActivity().getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
					int flag = pref.getInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
					if (flag == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY) {
						mListView.performItemClick(null, 0, 0);
						Editor editor = pref.edit();
						editor.remove(KEY_PREFERENCE_ACCOUNT_READY_FLAG);
						editor.apply();
					}

				} catch (Exception e) {
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
					btnLogin.setVisibility(View.INVISIBLE);

					onError(e);
					unLockUI();
				}
			} else {
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				unLockUI();
			}
		}

	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			try {
				if (!response.getString("login").equals("true")) {
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = mHostActivity.sharedPreference
							.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

				} else {
					VolleyHttpClient.createCookie();
				}
			} catch (JSONException e) {
				onError(e);
			} finally {
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				unLockUI();
			}
		}
	}


}
