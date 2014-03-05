package com.twoheart.dailyhotel.fragment;


import static com.twoheart.dailyhotel.util.AppConstants.LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_AUTO_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_ID;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_USER_PWD;
import static com.twoheart.dailyhotel.util.AppConstants.RESERVE;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.USER_ALIVE;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BookingTabActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.adapter.BookingListAdapter;
import com.twoheart.dailyhotel.obj.Booking;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.network.Parameter;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.util.ui.NoActionBarException;

public class BookingListFragment extends Fragment implements OnItemClickListener, OnClickListener{
	
	private static final String TAG = "BookingListFragment";
	
	private final static int BOOKING_LIST_FRAGMENT = 3;
	
	private View view;
	private SharedPreferences prefs;
	
	private ArrayList<Booking> items;
	private ListView listView;
	private BookingListAdapter adapter;
	
	private Button btn_signup;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_booking_list, null);
		prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		
		// ActionBar Setting
		MainActivity activity = (MainActivity)view.getContext();
		try {
			activity.changeTitle("예약확인");
		} catch (NoActionBarException e) {
			e.printStackTrace();
		}
		activity.hideMenuItem();
		activity.addMenuItem("dummy");
		
		// sliding setting
//		activity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		
		if(checkLogin()) {		// 로그인 상태
			LoadingDialog.showLoading(view.getContext());
			new GeneralHttpTask(sessionListener, view.getContext()).execute(REST_URL + USER_ALIVE);
			
		} else {		//		로그아웃 상태
			listView = (ListView) view.findViewById(R.id.listview_booking);
			listView.setVisibility(View.GONE);
			
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_booking_empty);
			layout.setVisibility(View.VISIBLE);
			btn_signup = (Button) view.findViewById(R.id.btn_booking_empty_signup);
			btn_signup.setOnClickListener(this);
		}
		
		return view;
	}
	
	private boolean checkLogin() {
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}
	
	public void parseLoginJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			if ( obj.getString("login").equals("true") ) {
				new GeneralHttpTask(reservListener, view.getContext()).execute(REST_URL + RESERVE);
				
			} else {
				// 로그인 실패
				Toast.makeText(view.getContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean(PREFERENCE_AUTO_LOGIN, false);
				ed.putBoolean(PREFERENCE_IS_LOGIN, false);
				ed.commit();
				
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.d(TAG, "parseLoginJson " +  e.toString());
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseReservJson(String str) {
		
		str.trim();
		if(str.indexOf("none") >= 0) {			// 예약이 없는경우
			listView = (ListView) view.findViewById(R.id.listview_booking);
			listView.setVisibility(View.GONE);
			
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_booking_empty);
			layout.setVisibility(View.VISIBLE);
			
			btn_signup = (Button) view.findViewById(R.id.btn_booking_empty_signup);
			btn_signup.setVisibility(View.INVISIBLE);
			
		} else {		// 예약이 있는 경우
			items = new ArrayList<Booking>();
			
			try {
				JSONObject obj = new JSONObject(str);
				JSONArray rsvArr = obj.getJSONArray("rsv");
				
				for(int i =0; i<rsvArr.length(); i++) {
					JSONObject rsvObj = rsvArr.getJSONObject(i);
					String sday = rsvObj.getString("sday");
					String hotel_idx = rsvObj.getString("hotel_idx");
					String hotel_name = rsvObj.getString("hotel_name");
					
					items.add(new Booking(sday, hotel_idx, hotel_name));
				}
				
				setListView();
				
			} catch (Exception e) {
				Log.d("parseReservJson", e.toString());
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void setListView() {
		listView = (ListView) view.findViewById(R.id.listview_booking);
		adapter = new BookingListAdapter(view.getContext(), R.layout.list_row_booking, items);
		listView.setOnItemClickListener(this);
		
		// footer 추가
		LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
		View footer = inflater.inflate(R.layout.footer_booking_list, null, false);
		listView.addFooterView(footer);
		Button btn_review = (Button) view.findViewById(R.id.btn_footer_booking_review);
		btn_review.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent marketLaunch = new Intent(Intent.ACTION_VIEW); 
		    	marketLaunch.setData(Uri.parse("market://details?id=com.twoheart.dailyhotel")); 
		    	startActivity(marketLaunch);
			}
		});
		
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btn_signup.getId()) {
			Intent i = new Intent(view.getContext(), SignupActivity.class);
			MainActivity activity = (MainActivity) view.getContext();
			startActivityForResult(i, BOOKING_LIST_FRAGMENT);
			activity.overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
		
		String sday = items.get(position).getSday();
		String[] array;
		array = sday.split("-");
		
		SharedPreferences.Editor ed = prefs.edit();
		ed.putString(PREFERENCE_HOTEL_IDX, items.get(position).getHotel_idx());
		ed.putString(PREFERENCE_HOTEL_YEAR, array[0]);
		ed.putString(PREFERENCE_HOTEL_MONTH, array[1]);
		ed.putString(PREFERENCE_HOTEL_DAY, array[2]);
		ed.commit();
		
		Intent i = new Intent(view.getContext(), BookingTabActivity.class);
		startActivity(i);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == BOOKING_LIST_FRAGMENT) {
			if(resultCode == getActivity().RESULT_OK) {
				MainActivity activity = (MainActivity) view.getContext();
				activity.switchContent(new BookingListFragment());
			}
		}
	}
	
	//session check
	protected OnCompleteListener sessionListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "sessionListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			result = result.trim();
			if(result.equals("alive")) {		// session alive
				// 예약 list  요청
				new GeneralHttpTask(reservListener, view.getContext()).execute(REST_URL + RESERVE);
				
			} else if(result.equals("dead")){		// session dead
				// 재로그인
				
				// parameter setting
				ArrayList<Parameter> paramList = new ArrayList<Parameter>();
				paramList.add(new Parameter("email", prefs.getString(PREFERENCE_USER_ID, "")));
				paramList.add(new Parameter("pw", prefs.getString(PREFERENCE_USER_PWD, "")));
				
				// 로그인 요청
				new GeneralHttpTask(loginListener, paramList, view.getContext()).execute(REST_URL + LOGIN);
				
			} else {
				LoadingDialog.hideLoading();
				Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	protected OnCompleteListener loginListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "loginListener onTAskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseLoginJson(result);
		}
	};
	
	
	protected OnCompleteListener reservListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d("reservListener", "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(view.getContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
			MainActivity activity = (MainActivity) view.getContext();
			activity.switchContent(new ErrorFragment());
		}
		
		@Override
		public void onTaskComplete(String result) {
			LoadingDialog.hideLoading();
			parseReservJson(result);
		}
	};
	
}
