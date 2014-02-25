package com.twoheart.dailyhotel;

import static com.twoheart.dailyhotel.AppConstants.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.booking.BookingListFragment;
import com.twoheart.dailyhotel.credit.CreditFragment;
import com.twoheart.dailyhotel.credit.NoLoginFragment;
import com.twoheart.dailyhotel.hotel.HotelListFragment;
import com.twoheart.dailyhotel.setting.SettingFragment;

public class DailyMenuFragment extends Fragment implements OnClickListener{
	
	private Context context;
	private View view;
	
	private LinearLayout linear_hotel;
	private LinearLayout linear_booking;
	private LinearLayout linear_credit;
	private LinearLayout linear_setting;
	
	private SharedPreferences prefs;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_menu_daily, null);
		
		context = view.getContext();
		
		prefs = view.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		loadResource();
		
		return view;
	}
	
	public void loadResource() {
		linear_hotel = (LinearLayout) view.findViewById(R.id.linear_hotel);
		linear_booking = (LinearLayout) view.findViewById(R.id.linear_booking);
		linear_credit = (LinearLayout) view.findViewById(R.id.linear_credit);
		linear_setting = (LinearLayout) view.findViewById(R.id.linear_setting);
		
		linear_hotel.setOnClickListener(this);
		linear_booking.setOnClickListener(this);
		linear_credit.setOnClickListener(this);
		linear_setting.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		Fragment newContent = null;
		SharedPreferences.Editor ed = prefs.edit();
		
		if(v.getId() == linear_hotel.getId()) {		// 오늘의 호텔
			
			ed.putString(PREFERENCE_SELECTED_MENU, "hotel");
			ed.commit();
			
			linear_hotel.setBackgroundResource(R.drawable.dh_menu_select);
			linear_booking.setBackgroundDrawable(null);
			linear_credit.setBackgroundDrawable(null);
			linear_setting.setBackgroundDrawable(null);
			
			newContent = new HotelListFragment();
			switchFragment(newContent);
			
		} else if(v.getId() == linear_booking.getId()) {		// 예약확인
			
			ed.putString(PREFERENCE_SELECTED_MENU, "booking");
			ed.commit();
			
			linear_hotel.setBackgroundDrawable(null);
			linear_booking.setBackgroundResource(R.drawable.dh_menu_select);
			linear_credit.setBackgroundDrawable(null);
			linear_setting.setBackgroundDrawable(null);
			
			
			newContent = new BookingListFragment();
			switchFragment(newContent);
			
		} else if(v.getId() == linear_credit.getId()) {		// 적립금
			
			ed.putString(PREFERENCE_SELECTED_MENU, "credit");
			ed.commit();
			
			linear_hotel.setBackgroundDrawable(null);
			linear_booking.setBackgroundDrawable(null);
			linear_credit.setBackgroundResource(R.drawable.dh_menu_select);
			linear_setting.setBackgroundDrawable(null);
			
			if(checkLogin())	//로그인상태
				newContent = new CreditFragment();
			else		// 로그아웃 상태
				newContent = new NoLoginFragment();
			switchFragment(newContent);
			
		} else if(v.getId() == linear_setting.getId()) {		// 설정
			
			linear_hotel.setBackgroundDrawable(null);
			linear_booking.setBackgroundDrawable(null);
			linear_credit.setBackgroundDrawable(null);
			linear_setting.setBackgroundResource(R.drawable.dh_menu_select);
			
			newContent = new SettingFragment();
			switchFragment(newContent);
		}
		
	}
	
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		MainActivity activity = (MainActivity) getActivity();
		activity.switchContent(fragment);
	}
	
	private boolean checkLogin() {
		prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}
	
	public void changeMenu() {
		linear_hotel.setBackgroundDrawable(null);
		linear_booking.setBackgroundResource(R.drawable.dh_menu_select);
		linear_credit.setBackgroundDrawable(null);
		linear_setting.setBackgroundDrawable(null);
	}
	
	
	
}
