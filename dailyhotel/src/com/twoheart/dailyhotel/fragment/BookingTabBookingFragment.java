package com.twoheart.dailyhotel.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class BookingTabBookingFragment extends BaseFragment implements Constants,
		DailyHotelJsonResponseListener,
		DailyHotelStringResponseListener {

	private static final String TAG = "BookingTabBookingFragment";
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";
	private static final String KEY_BUNDLE_ARGUMENTS_BOOKING = "booking";

	private TextView tvCustomerName, tvCustomerPhone, tvBedtype, tvHotelName, tvAddress;
	private TextView tvCheckIn, tvCheckOut;
	
	private Booking mBooking;
	private HotelDetail mHotelDetail;
	private static String[] mStrings;

	public static BookingTabBookingFragment newInstance(HotelDetail hotelDetail, Booking booking, String[] strings) {
		BookingTabBookingFragment newFragment = new BookingTabBookingFragment();
		
		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING, booking);
		
		newFragment.setArguments(arguments);
		newFragment.setTitle(strings[0]);
		
		mStrings = strings;
		
		return newFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
		mBooking = (Booking) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

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
		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null, this, mHostActivity));

		return view;
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
					
					unLockUI();
					showToast(getString(R.string.toast_msg_failed_to_login), Toast.LENGTH_SHORT, true);
				} else
					VolleyHttpClient.createCookie();
				
			} catch (JSONException e) {
				onError(e);
				unLockUI();
			}

		} else if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				JSONObject obj = response;
				String name = obj.getString("name");
				String phone = obj.getString("phone");
				tvCustomerName.setText(name);
				tvCustomerPhone.setText(phone);
				
				// 체크인 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
						URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_RESERVE_CHECKIN).append(mHotelDetail.getSaleIdx()).toString(), null, this,
						mHostActivity));
				android.util.Log.e("madsd", mHotelDetail.getSaleIdx()+"");
			} catch (Exception e) {
				onError(e);
				unLockUI();
			}

		} else if (url.contains(URL_WEBAPI_RESERVE_CHECKIN)) {
			try {
				android.util.Log.e("url!QWEW",url);
				JSONObject obj = response;
				String checkin = obj.getString("checkin");
				String checkout = obj.getString("checkout");
				
				String in[] = checkin.split("-");
				android.util.Log.e("chkin",checkin);
				android.util.Log.e("chkout",checkout);
				
				tvCheckIn.setText("20" + in[0] + mStrings[1] + in[1] + mStrings[2] + in[2] + mStrings[3] + in[3] + mStrings[4]);
				
				String out[] = checkout.split("-");
				tvCheckOut.setText("20" + out[0] + mStrings[1] + out[1] + mStrings[2] + out[2] + mStrings[3] + out[3] + mStrings[4]);
				
				unLockUI();

			} catch (Exception e) {
				onError(e);
				unLockUI();
			}
		}

	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();
			if (result.equals("alive")) { // session alive
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
						URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_INFO).toString(), null, this,
						mHostActivity));

			} else if (result.equals("dead")) { // session dead

				// 재로그인
				if (mHostActivity.sharedPreference.getBoolean(
						KEY_PREFERENCE_AUTO_LOGIN, false)) {
					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("email", mHostActivity.sharedPreference
							.getString(KEY_PREFERENCE_USER_ID, null));
					loginParams.put("pw", mHostActivity.sharedPreference
							.getString(KEY_PREFERENCE_USER_PWD, null));

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
							loginParams, this, mHostActivity));
				} else {
					startActivity(new Intent(mHostActivity, LoginActivity.class));
				}

			} else {
				unLockUI();
			}

		}
	}

}
