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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
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
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

/**
 * 예약한 호텔의 리스트들을 출력.
 * 
 * @author jangjunho
 *
 */
public class BookingListFragment extends BaseFragment implements Constants, OnItemClickListener, OnClickListener
{

	private ArrayList<Booking> mItems;
	private BookingListAdapter mAdapter;

	private RelativeLayout mEmptyLayout;
	private PinnedSectionListView mListView;
	private TextView btnLogin;
	private long mCurrentTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

		mListView = (PinnedSectionListView) view.findViewById(R.id.listview_booking);
		mListView.setShadowVisible(false);

		mEmptyLayout = (RelativeLayout) view.findViewById(R.id.layout_booking_empty);
		btnLogin = (TextView) view.findViewById(R.id.btn_booking_empty_login);

		btnLogin.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		baseActivity.setActionBar(getString(R.string.actionbar_title_booking_list_frag), false);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));

		RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordScreen("bookingList", "/bookings/");
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnLogin.getId())
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			Intent i = new Intent(baseActivity, LoginActivity.class);
			startActivity(i);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (isLockUiComponent() == true)
		{
			return;
		}

		lockUiComponent();

		Intent intent = null;
		Booking item = mItems.get(position);

		if (item.type == Booking.TYPE_SECTION)
		{
			releaseUiComponent();

			return;
		}

		RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordEvent("click", "selectBookingConfirmation", item.getHotel_name(), null);

		if (item.getPayType() == CODE_PAY_TYPE_CARD_COMPLETE || item.getPayType() == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
		{ // 카드결제 완료 || 가상계좌 완료
			intent = new Intent(baseActivity, BookingTabActivity.class);
		} else if (item.getPayType() == CODE_PAY_TYPE_ACCOUNT_WAIT)
		{ // 가상계좌 입금대기
			intent = new Intent(baseActivity, PaymentWaitActivity.class);
		}

		if (intent != null)
		{
			intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, item);
			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);
		} else
		{
			releaseUiComponent();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		if (requestCode == CODE_REQUEST_ACTIVITY_BOOKING_DETAIL)
		{
			switch (resultCode)
			{
				case CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT:
					SimpleAlertDialog.build(getActivity(), getString(R.string.dialog_notice2), data.getStringExtra("msg"), getString(R.string.dialog_btn_text_confirm), null).show();
					break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

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
					SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();
				} else
				{
					VolleyHttpClient.createCookie();
				}
			} catch (JSONException e)
			{
				onError(e);
			} finally
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				unLockUI();
			}
		}

	};

	private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			String result = null;

			if (TextUtils.isEmpty(response) == false)
			{
				result = response.trim();
			}

			if ("alive".equalsIgnoreCase(result) == true)
			{ // session alive
				Map<String, String> params = new HashMap<String, String>();
				params.put("timeZone", "Asia/Seoul");

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));

			} else if ("dead".equalsIgnoreCase(result) == true)
			{ // session dead
				// 재로그인
				if (true == baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
				{

					String id = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
					String accessToken = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					String pw = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);

					Map<String, String> loginParams = new HashMap<String, String>();

					if (null != accessToken)
					{
						loginParams.put("accessToken", accessToken);
					} else
					{
						loginParams.put("email", id);
					}

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, baseActivity));

					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
				} else
				{
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);

					unLockUI();
				}

			} else
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);

				onError();
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				mCurrentTime = response.getLong("currentDateTime");

				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_MINE_ALL).toString(), null, mReserveMineJsonResponseListener, baseActivity));
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mReserveMineJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			int msg_code = -1;

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
				return;
			}

			try
			{
				JSONArray jsonArray = response.getJSONArray("data");
				int length = jsonArray.length();

				if (length == 0)
				{
					//예약한 호텔이 없는 경우 
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
					btnLogin.setVisibility(View.INVISIBLE);
				} else
				{
					// 입금대기, 결제완료, 이용완료
					ArrayList<Booking> waitBookingList = new ArrayList<Booking>();
					ArrayList<Booking> paymentBookingList = new ArrayList<Booking>();
					ArrayList<Booking> usedBookingList = new ArrayList<Booking>();

					for (int i = 0; i < length; i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						Booking booking = new Booking(jsonObject);

						switch (booking.getPayType())
						{
							case CODE_PAY_TYPE_CARD_COMPLETE:
							case CODE_PAY_TYPE_ACCOUNT_COMPLETE:
								boolean isUsed = booking.checkoutTime < mCurrentTime;

								booking.isUsed = isUsed;

								if (isUsed)
								{
									usedBookingList.add(booking);
								} else
								{
									paymentBookingList.add(booking);
								}
								break;

							case CODE_PAY_TYPE_ACCOUNT_WAIT:
								waitBookingList.add(booking);
								break;
						}
					}

					if (mItems == null)
					{
						mItems = new ArrayList<Booking>();
					}

					mItems.clear();

					// 입금 대기가 있는 경우.
					if (waitBookingList.size() > 0)
					{
						Booking sectionWait = new Booking(getString(R.string.frag_booking_wait_account));
						mItems.add(sectionWait);
						mItems.addAll(waitBookingList);
					}

					// 결제 완료가 있는 경우.
					if (paymentBookingList.size() > 0)
					{
						Booking sectionPay = new Booking(getString(R.string.frag_booking_complete_payment));
						mItems.add(sectionPay);
						mItems.addAll(paymentBookingList);
					}

					// 이용 완료가 있는 경우.
					if (usedBookingList.size() > 0)
					{
						Booking sectionUsed = new Booking(getString(R.string.frag_booking_use));
						mItems.add(sectionUsed);
						mItems.addAll(usedBookingList);
					}

					mAdapter = new BookingListAdapter(baseActivity, R.layout.list_row_booking, mItems);
					mListView.setOnItemClickListener(BookingListFragment.this);
					mListView.setAdapter(mAdapter);

					mListView.setVisibility(View.VISIBLE);
					mEmptyLayout.setVisibility(View.GONE);

					// flag가 가상계좌 입금 대기에서 날아온경우 
					SharedPreferences pref = getActivity().getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
					int flag = pref.getInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
					if (flag == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
					{
						mListView.performItemClick(null, 0, 0);
						Editor editor = pref.edit();
						editor.remove(KEY_PREFERENCE_ACCOUNT_READY_FLAG);
						editor.apply();
					}
				}

			} catch (Exception e)
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};
}

//@Override
//public void onResponse(String url, JSONObject response) {
//	if (url.contains(URL_WEBAPI_USER_LOGIN)) {
//		try {
//			if (!response.getString("login").equals("true")) {
//				// 로그인 실패
//				// data 초기화
//				SharedPreferences.Editor ed = mHostActivity.sharedPreference
//						.edit();
//				ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
//				ed.putString(KEY_PREFERENCE_USER_ID, null);
//				ed.putString(KEY_PREFERENCE_USER_PWD, null);
//				ed.commit();
//
//			} else {
//				VolleyHttpClient.createCookie();
//			}
//		} catch (JSONException e) {
//			onError(e);
//		} finally {
//			mListView.setVisibility(View.GONE);
//			mEmptyLayout.setVisibility(View.VISIBLE);
//			btnLogin.setVisibility(View.INVISIBLE);
//
//			unLockUI();
//		}
//	}
//}

//@Override
//public void onResponse(String url, String response) {
//	if (url.contains(URL_WEBAPI_USER_ALIVE)) {
//		String result = response.trim();
//		if (result.equals("alive")) { // session alive
//			// 예약 목록 요청.
//			mQueue.add(new DailyHotelStringRequest(Method.GET,
//					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
//							URL_WEBAPI_RESERVE_MINE).toString(), null,
//							BookingListFragment.this, mHostActivity));
//
//		} else if (result.equals("dead")) { // session dead
//			// 재로그인
//			if (mHostActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {
//				
//				String id = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
//				String accessToken = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
//				String pw = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);
//
//				Map<String, String> loginParams = new HashMap<String, String>();
//
//				if (accessToken != null) {
//					loginParams.put("accessToken",accessToken);
//				} else {
//					loginParams.put("email", id);
//				}
//
//				loginParams.put("pw", pw);
//
//				mQueue.add(new DailyHotelJsonRequest(Method.POST,
//						new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(),
//						loginParams, mUserLoginJsonResponseListener, mHostActivity));
//
//				mListView.setVisibility(View.GONE);
//				mEmptyLayout.setVisibility(View.VISIBLE);
//			} else {
//				mListView.setVisibility(View.GONE);
//				mEmptyLayout.setVisibility(View.VISIBLE);
//
//				unLockUI();
//			}
//
//		} else {
//			mListView.setVisibility(View.GONE);
//			mEmptyLayout.setVisibility(View.VISIBLE);
//
//			onError();
//			unLockUI();
//		}
//
//	} else if (url.contains(URL_WEBAPI_RESERVE_MINE)) {//예약한 호텔 리스트 
//		if (!response.trim().equals("none")) {
//			mItems = new ArrayList<Booking>();
//
//			try {
//				JSONObject obj = new JSONObject(response);
//				JSONArray rsvArr = obj.getJSONArray("rsv");
//
//				for (int i = 0; i < rsvArr.length(); i++) {
//					JSONObject rsvObj = rsvArr.getJSONObject(i);
//
//					//kcpno (depre)
//					String hotel_name = rsvObj.getString("hotel_name");
//					//room_name (depre)
//					String sday = rsvObj.getString("sday");
//					//rsv_idx (dpre)
//					String hotel_idx = rsvObj.getString("hotel_idx");
//					String bedType = rsvObj.getString("bed_type");
//					int payType = rsvObj.getInt("pay_type");
//					String tid = rsvObj.getString("tid");
//
//					mItems.add(new Booking(sday, hotel_idx, hotel_name, bedType, payType, tid));
//				}
//
//				mAdapter = new BookingListAdapter(mHostActivity,
//						R.layout.list_row_booking, mItems);
//				mListView.setOnItemClickListener(this);
//				mListView.setAdapter(mAdapter);
//
//				unLockUI();
//
//				// flag가 가상계좌 입금 대기에서 날아온경우 
//				SharedPreferences pref = getActivity().getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
//				int flag = pref.getInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
//				if (flag == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY) {
//					mListView.performItemClick(null, 0, 0);
//					Editor editor = pref.edit();
//					editor.remove(KEY_PREFERENCE_ACCOUNT_READY_FLAG);
//					editor.apply();
//				}
//
//			} catch (Exception e) {
//				mListView.setVisibility(View.GONE);
//				mEmptyLayout.setVisibility(View.VISIBLE);
//				btnLogin.setVisibility(View.INVISIBLE);
//
//				onError(e);
//				unLockUI();
//			}
//		} else {//예약한 호텔이 없는 경우 
//			mListView.setVisibility(View.GONE);
//			mEmptyLayout.setVisibility(View.VISIBLE);
//			btnLogin.setVisibility(View.INVISIBLE);
//
//			unLockUI();
//		}
//	}
//
//}
