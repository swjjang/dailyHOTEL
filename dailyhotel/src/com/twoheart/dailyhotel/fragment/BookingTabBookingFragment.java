package com.twoheart.dailyhotel.fragment;

import static com.twoheart.dailyhotel.util.AppConstants.CHECKIN;
import static com.twoheart.dailyhotel.util.AppConstants.DETAIL;
import static com.twoheart.dailyhotel.util.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.USERINFO;
import static com.twoheart.dailyhotel.util.AppConstants.USER_ALIVE;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.network.Parameter;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class BookingTabBookingFragment extends Fragment{

	private static final String TAG = "BookingTabBookingFragment";
	
	private View view;
	
	private String booking_idx;
	
	
	private TextView tv_user_name, tv_hotel_name, tv_address;
	private TextView tv_checkin, tv_checkout;
	private SharedPreferences prefs;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	public static BookingTabBookingFragment newInstance() {
		BookingTabBookingFragment fragment = new BookingTabBookingFragment();
		return fragment;
	}
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Booking View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_booking_tab_booking, null);
		loadResource();
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(view.getContext());
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");

		new GeneralHttpTask(sessionListener, view.getContext()).execute(REST_URL + USER_ALIVE);
		
		return view;
	}
	
	public void loadResource() {
		tv_user_name = (TextView) view.findViewById(R.id.tv_booking_tab_user_name);
		tv_hotel_name = (TextView) view.findViewById(R.id.tv_booking_tab_hotel_name);
		tv_address= (TextView) view.findViewById(R.id.tv_booking_tab_address);
		tv_checkin = (TextView) view.findViewById(R.id.tv_booking_tab_checkin);
		tv_checkout = (TextView) view.findViewById(R.id.tv_booking_tab_checkout);
	}
	
	public void parseLoginJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			if ( obj.getString("login").equals("true") ) {
				new GeneralHttpTask(userInfoListener, view.getContext()).execute(REST_URL + USERINFO);
				
			} else {
				// 로그인 실패
				Toast.makeText(view.getContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean(PREFERENCE_AUTO_LOGIN, false);
				ed.putBoolean(PREFERENCE_IS_LOGIN, false);
				ed.commit();
				
				Toast.makeText(view.getContext(), "다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseCheckinJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			String checkin = obj.getString("checkin");
			String checkout = obj.getString("checkout");
			
			String in[] = checkin.split("-");
			tv_checkin.setText("20" + in[0] + "년 " + in[1] + "월 " + in[2] + "일 " + in[3] + "시");
			String out[] = checkout.split("-");
			tv_checkout.setText("20" + out[0] + "년 " + out[1] + "월 " + out[2] + "일 " + out[3] + "시");
			
		} catch (Exception e) {
			Log.d("parseCheckinJson", "TagDataParser" + "->" + e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void parseJson(String str) {
		
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray bookingArr = obj.getJSONArray("detail");
			JSONObject detailObj =  bookingArr.getJSONObject(0);
			
			tv_hotel_name.setText(detailObj.getString("hotel_name"));
			tv_address.setText(detailObj.getString("address"));
			
			booking_idx = Integer.toString(detailObj.getInt("idx"));
			
//			String cat = detailObj.getString("cat");
//			ImageView grade = (ImageView) view.findViewById(R.id.iv_booking_tab_grade);
//			
//			if(cat.equals("biz")) {
//				grade.setImageResource(R.drawable.dh_grademark_biz);
//			} else if(cat.equals("boutique")) {
//				grade.setImageResource(R.drawable.dh_grademark_boutique);
//			} else if(cat.equals("residence")) {
//				grade.setImageResource(R.drawable.dh_grademark_residence);
//			} else if(cat.equals("special")) {
//				grade.setImageResource(R.drawable.dh_grademark_special);
//			}
			
			new GeneralHttpTask(checkinListener, view.getContext()).execute(REST_URL + CHECKIN + booking_idx);
						
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	//session check
	protected OnCompleteListener sessionListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "sessionListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			result = result.trim();
			if(result.equals("alive")) {		// session alive
				// 예약 list  요청
				new GeneralHttpTask(userInfoListener, view.getContext()).execute(REST_URL + USERINFO);
				
			} else if(result.equals("dead")){		// session dead
				// 재로그인
				
				// parameter setting
				ArrayList<Parameter> paramList = new ArrayList<Parameter>();
				paramList.add(new Parameter("email", prefs.getString(PREFERENCE_USER_ID, "")));
				paramList.add(new Parameter("pw", prefs.getString(PREFERENCE_USER_PWD, "")));
				
				// 로그인 요청
				new GeneralHttpTask(loginListener, paramList, view.getContext()).execute(REST_URL + LOGIN);
				
			} else {
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	
	protected OnCompleteListener loginListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "loginListener onTAskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseLoginJson(result);
		}
	};
	
	protected OnCompleteListener userInfoListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "userInfoListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			try {
				JSONObject obj =  new JSONObject(result);
				String name = obj.getString("name");
				tv_user_name.setText(name + " 님" );
				
				prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
				String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
				String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
				String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
				String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);

				new GeneralHttpTask(bookingListener, view.getContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
				
			} catch (Exception e) {
				Log.d("userInfoListener", e.toString());
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected OnCompleteListener bookingListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "bookingListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
	
	protected OnCompleteListener checkinListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("TAG", "bookingListener onTaskFailed");
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseCheckinJson(result);
		}
	};
	
}
