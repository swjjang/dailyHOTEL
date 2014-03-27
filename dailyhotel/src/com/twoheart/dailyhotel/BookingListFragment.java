package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BookingTabActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.adapter.BookingListAdapter;
import com.twoheart.dailyhotel.obj.Booking;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class BookingListFragment extends Fragment implements Constants,
		OnItemClickListener, OnClickListener, DailyHotelJsonResponseListener,
		DailyHotelStringResponseListener, ErrorListener {

	private static final String TAG = "BookingListFragment";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private ArrayList<Booking> mItems;
	private BookingListAdapter mAdapter;

	private RelativeLayout mEmptyLayout;
	private ListView mListView;
	private Button btnSignUp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_booking_list, null);
		mHostActivity = (MainActivity) getActivity();
		mQueue = VolleyHttpClient.getRequestQueue();

		mListView = (ListView) view.findViewById(R.id.listview_booking);
		mEmptyLayout = (RelativeLayout) view
				.findViewById(R.id.layout_booking_empty);
		btnSignUp = (Button) view.findViewById(R.id.btn_booking_empty_signup);
		btnSignUp.setOnClickListener(this);

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
		mHostActivity.setActionBar("예약확인");

		LoadingDialog.showLoading(mHostActivity);

		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
				BookingListFragment.this, BookingListFragment.this));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnSignUp.getId()) {
			Intent i = new Intent(mHostActivity, SignupActivity.class);

			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.hold);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView,
			int position, long id) {

		Intent i = new Intent(mHostActivity, BookingTabActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, mItems.get(position));
		startActivity(i);
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
						BookingListFragment.this, BookingListFragment.this));

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
						loginParams
								.put("accessToken",
										mHostActivity.sharedPreference
												.getString(
														KEY_PREFERENCE_USER_ACCESS_TOKEN,
														null));
					} else {
						loginParams.put("email", mHostActivity.sharedPreference
								.getString(KEY_PREFERENCE_USER_ID, null));
					}

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
							loginParams, BookingListFragment.this,
							BookingListFragment.this));

					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
				} else {

					LoadingDialog.hideLoading();

					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);

				}

			} else {
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);

				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			}

		} else if (url.contains(URL_WEBAPI_RESERVE_MINE)) {
			if (!response.trim().equals("none")) {
				mItems = new ArrayList<Booking>();

				try {
					JSONObject obj = new JSONObject(response);
					JSONArray rsvArr = obj.getJSONArray("rsv");

					for (int i = 0; i < rsvArr.length(); i++) {
						JSONObject rsvObj = rsvArr.getJSONObject(i);
						String sday = rsvObj.getString("sday");
						String hotel_idx = rsvObj.getString("hotel_idx");
						String hotel_name = rsvObj.getString("hotel_name");

						mItems.add(new Booking(sday, hotel_idx, hotel_name));
					}

					mAdapter = new BookingListAdapter(mHostActivity,
							R.layout.list_row_booking, mItems);
					mListView.setOnItemClickListener(this);

					mListView.setAdapter(mAdapter);

				} catch (Exception e) {
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
					btnSignUp.setVisibility(View.INVISIBLE);

					if (DEBUG)
						e.printStackTrace();

					Toast.makeText(mHostActivity,
							"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
							Toast.LENGTH_LONG).show();
				} finally {
					LoadingDialog.hideLoading();
				}
			} else {
				LoadingDialog.hideLoading();

				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnSignUp.setVisibility(View.INVISIBLE);
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

				}
			} catch (JSONException e) {
				if (DEBUG)
					e.printStackTrace();

				LoadingDialog.hideLoading();
				Toast.makeText(mHostActivity,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			} finally {
				LoadingDialog.hideLoading();
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnSignUp.setVisibility(View.INVISIBLE);

			}
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		mHostActivity.replaceFragment(new ErrorFragment());
		LoadingDialog.hideLoading();

	}

}
