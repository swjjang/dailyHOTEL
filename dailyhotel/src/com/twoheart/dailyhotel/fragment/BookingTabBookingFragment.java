/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingTabBookingFragment (예약한 호텔의 예약 탭)
 * 
 * 예약한 호텔 탭 중 예약 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.PaymentActivity;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class BookingTabBookingFragment extends BaseFragment implements Constants
{

	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";
	private static final String KEY_BUNDLE_ARGUMENTS_BOOKING = "booking";

	private TextView tvCustomerName, tvCustomerPhone, tvBedtype, tvHotelName,
			tvAddress;
	private TextView tvCheckIn, tvCheckOut;

	private Booking mBooking;
	private HotelDetail mHotelDetail;
	private static String[] mStrings;

	public static BookingTabBookingFragment newInstance(HotelDetail hotelDetail, Booking booking, String[] strings)
	{
		BookingTabBookingFragment newFragment = new BookingTabBookingFragment();

		//관련 정보는 BookingTabActivity에서 넘겨받음. 
		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING, booking);

		newFragment.setArguments(arguments);
		newFragment.setTitle(strings[0]);

		mStrings = strings;

		return newFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
		mBooking = (Booking) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = inflater.inflate(R.layout.fragment_booking_tab_booking, container, false);
		tvCustomerName = (TextView) view.findViewById(R.id.tv_booking_tab_user_name);
		tvCustomerPhone = (TextView) view.findViewById(R.id.tv_booking_tab_user_phone);
		tvHotelName = (TextView) view.findViewById(R.id.tv_booking_tab_hotel_name);
		tvAddress = (TextView) view.findViewById(R.id.tv_booking_tab_address);
		tvBedtype = (TextView) view.findViewById(R.id.tv_booking_tab_bedtype);
		tvCheckIn = (TextView) view.findViewById(R.id.tv_booking_tab_checkin);
		tvCheckOut = (TextView) view.findViewById(R.id.tv_booking_tab_checkout);

		tvHotelName.setText(mBooking.getHotel_name());
		tvAddress.setText(mHotelDetail.getHotel().getAddress());
		tvBedtype.setText(mBooking.getBedType());

		// Android Marquee bug...
		tvCustomerName.setSelected(true);
		tvCustomerPhone.setSelected(true);
		tvHotelName.setSelected(true);
		tvAddress.setSelected(true);
		tvBedtype.setSelected(true);
		tvCheckIn.setSelected(true);
		tvCheckOut.setSelected(true);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));

		return view;
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
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				if (response.getString("login").equals("true") == false)
				{
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

					showToast(getString(R.string.toast_msg_failed_to_login), Toast.LENGTH_SHORT, true);
				} else
				{
					VolleyHttpClient.createCookie();
				}
			} catch (JSONException e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
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

				String name = response.getString("name");
				String phone = response.getString("phone");
				tvCustomerName.setText(name);
				tvCustomerPhone.setText(phone);

				// SailIndex가 0인 경우에 서버에 이슈가 발생할수 있다.
				// 0인 경우 아마도 메모리에서 정보가 삭제되어 발생한듯 하다.
				if (mHotelDetail.getSaleIdx() == 0)
				{
					// 세션이 만료되어 재시작 요청.
					SimpleAlertDialog.build(baseActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_session_expired), getString(R.string.dialog_btn_text_confirm), null, new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Util.restartApp(getActivity());
						}
					}, null).setCancelable(false).show();
				} else
				{
					// 체크인 정보 요청.
					//				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_CHECKIN).append('/').append(mHotelDetail.getSaleIdx()).toString(), null, mReserveCheckInJsonResponseListener, mHostActivity));
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_CHECKINOUT).append('/').append(mHotelDetail.getSaleIdx()).toString(), null, mReserveCheckInJsonResponseListener, baseActivity));
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mReserveCheckInJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (getActivity() == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				long checkin = Long.valueOf(response.getString("checkin"));
				long checkout = Long.valueOf(response.getString("checkout"));

				SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH시", Locale.KOREA);
				format.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));

				// Check In
				Calendar calendarCheckin = DailyCalendar.getInstance();
				calendarCheckin.setTimeInMillis(checkin);

				String checkInday = format.format(calendarCheckin.getTime());

				tvCheckIn.setText(checkInday);

				// Check Out
				Calendar calendarCheckout = DailyCalendar.getInstance();
				calendarCheckout.setTimeInMillis(checkout);

				String checkOutday = format.format(calendarCheckout.getTime());

				tvCheckOut.setText(checkOutday);
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
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
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, baseActivity));

			} else if ("dead".equalsIgnoreCase(result) == true)
			{ // session dead
				// 재로그인
				if (baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
				{
					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("email", baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ID, null));
					loginParams.put("pw", baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null));

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, baseActivity));
				} else
				{
					startActivity(new Intent(baseActivity, LoginActivity.class));
				}
			} else
			{
				unLockUI();
			}
		}
	};

	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_USER_LOGIN)) {
	//			try {
	//				if (!response.getString("login").equals("true")) {
	//					// 로그인 실패
	//					// data 초기화
	//					SharedPreferences.Editor ed = mHostActivity.sharedPreference
	//							.edit();
	//					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
	//					ed.putString(KEY_PREFERENCE_USER_ID, null);
	//					ed.putString(KEY_PREFERENCE_USER_PWD, null);
	//					ed.commit();
	//					
	//					unLockUI();
	//					showToast(getString(R.string.toast_msg_failed_to_login), Toast.LENGTH_SHORT, true);
	//				} else
	//					VolleyHttpClient.createCookie();
	//				
	//			} catch (JSONException e) {
	//				onError(e);
	//				unLockUI();
	//			}
	//
	//		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
	//			try {
	//				JSONObject obj = response;
	//				String name = obj.getString("name");
	//				String phone = obj.getString("phone");
	//				tvCustomerName.setText(name);
	//				tvCustomerPhone.setText(phone);
	//				
	//				// 체크인 정보 요청.
	//				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
	//						URL_DAILYHOTEL_SERVER).append(
	//						URL_WEBAPI_RESERVE_CHECKIN).append(mHotelDetail.getSaleIdx()).toString(), null, this,
	//						mHostActivity));
	//				ExLog.e("madsd : " + mHotelDetail.getSaleIdx()+"");
	//			} catch (Exception e) {
	//				onError(e);
	//				unLockUI();
	//			}
	//
	//		} else if (url.contains(URL_WEBAPI_RESERVE_CHECKIN)) {
	//			try {
	//				ExLog.e("url!QWEW : " + url);
	//				JSONObject obj = response;
	//				String checkin = obj.getString("checkin");
	//				String checkout = obj.getString("checkout");
	//				
	//				String in[] = checkin.split("-");
	//				ExLog.e("chkin : " + checkin);
	//				ExLog.e("chkout : " + checkout);
	//				
	//				tvCheckIn.setText("20" + in[0] + mStrings[1] + in[1] + mStrings[2] + in[2] + mStrings[3] + in[3] + mStrings[4]);
	//				
	//				String out[] = checkout.split("-");
	//				tvCheckOut.setText("20" + out[0] + mStrings[1] + out[1] + mStrings[2] + out[2] + mStrings[3] + out[3] + mStrings[4]);
	//				
	//				unLockUI();
	//
	//			} catch (Exception e) {
	//				onError(e);
	//				unLockUI();
	//			}
	//		}
	//
	//	}
	//
	//	@Override
	//	public void onResponse(String url, String response) {
	//		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
	//			String result = response.trim();
	//			if (result.equals("alive")) { // session alive
	//				// 사용자 정보 요청.
	//				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
	//						URL_DAILYHOTEL_SERVER).append(
	//						URL_WEBAPI_USER_INFO).toString(), null, this,
	//						mHostActivity));
	//
	//			} else if (result.equals("dead")) { // session dead
	//
	//				// 재로그인
	//				if (mHostActivity.sharedPreference.getBoolean(
	//						KEY_PREFERENCE_AUTO_LOGIN, false)) {
	//					Map<String, String> loginParams = new HashMap<String, String>();
	//					loginParams.put("email", mHostActivity.sharedPreference
	//							.getString(KEY_PREFERENCE_USER_ID, null));
	//					loginParams.put("pw", mHostActivity.sharedPreference
	//							.getString(KEY_PREFERENCE_USER_PWD, null));
	//
	//					mQueue.add(new DailyHotelJsonRequest(Method.POST,
	//							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
	//									URL_WEBAPI_USER_LOGIN).toString(),
	//							loginParams, this, mHostActivity));
	//				} else {
	//					startActivity(new Intent(mHostActivity, LoginActivity.class));
	//				}
	//
	//			} else {
	//				unLockUI();
	//			}
	//
	//		}
	//	}
}
