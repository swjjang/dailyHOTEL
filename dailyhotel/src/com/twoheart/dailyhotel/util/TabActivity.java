 package com.twoheart.dailyhotel.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public abstract class TabActivity extends BaseActivity {
	
	public HotelDetail hotelDetail;

	protected List<BaseFragment> mFragments;

	protected FragmentPagerAdapter mAdapter;
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragments = new LinkedList<BaseFragment>();
		
	}
	
	protected abstract void onPostSetCookie();
	
	@Override
	protected void onResume() {
		super.onResume();
		onPostSetCookie();
		
		if (mAdapter == null) {
			mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
	
				@Override
				public Fragment getItem(int position) {
					return mFragments.get(position);
				}
	
				@Override
				public CharSequence getPageTitle(int position) {
					return mFragments.get(position).getTitle();
				}
	
				@Override
				public int getCount() {
					return mFragments.size();
				}
			};
	
			mViewPager.setOffscreenPageLimit(mAdapter.getCount() + 2);
			mViewPager.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		mIndicator.setViewPager(mViewPager);
	}

	protected void loadFragments() {
		mFragments.add(TabInfoFragment.newInstance(hotelDetail));
		mFragments.add(TabMapFragment.newInstance(hotelDetail));
		
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
}
