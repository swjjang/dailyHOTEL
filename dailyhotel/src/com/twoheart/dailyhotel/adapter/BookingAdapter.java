package com.twoheart.dailyhotel.adapter;

import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.HotelTabInfoFragment;
import com.twoheart.dailyhotel.fragment.HotelTabMapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BookingAdapter extends FragmentPagerAdapter{
	
	
	public BookingAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {
		if (position == 0)	return BookingTabBookingFragment.newInstance();
		else if (position == 1)	return HotelTabInfoFragment.newInstance();
		else return HotelTabMapFragment.newInstance();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (position == 0) return "예약";
		else if (position == 1) return "정보";
		else return "지도";
	}
	
	@Override
	public int getCount() {
		return 3;
	}
}
