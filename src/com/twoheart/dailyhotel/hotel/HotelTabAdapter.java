package com.twoheart.dailyhotel.hotel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HotelTabAdapter extends FragmentPagerAdapter{
	public HotelTabAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {
		if (position == 0)	return HotelTabBookingFragment.newInstance();
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
