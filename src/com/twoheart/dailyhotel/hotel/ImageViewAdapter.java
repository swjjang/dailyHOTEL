package com.twoheart.dailyhotel.hotel;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImageViewAdapter extends FragmentPagerAdapter{
	private ArrayList<String> urlList;
	
	public ImageViewAdapter(FragmentManager fm, ArrayList<String> urlList) {
		super(fm);
		this.urlList = urlList;
	}
	
	@Override
	public Fragment getItem(int position) {
		return ImageViewFragment.newInstance(urlList.get(position));
	}
	
	@Override
	public int getCount() {
		return urlList.size();
	}
	
}
