/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * 
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;
import com.twoheart.dailyhotel.view.widget.TabIndicator;
import com.twoheart.dailyhotel.view.widget.TabIndicator.OnTabSelectedListener;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public abstract class PlaceBookingTabActivity extends BaseActivity
{
	protected TabIndicator mTabIndicator;
	protected FragmentViewPager mFragmentViewPager;

	protected PlaceBookingDetail mPlaceBookingDetail;
	protected Booking booking;

	protected abstract void loadFragments();

	protected abstract void requestPlaceBookingDetail();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		booking = new Booking();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
		}

		if (booking == null)
		{
			Util.restartApp(this);
			return;
		}

		setContentView(R.layout.activity_booking_tab);
		setActionBar(booking.placeName);

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add(getString(R.string.frag_booking_tab_title));
		titleList.add(getString(R.string.frag_tab_info_title));
		titleList.add(getString(R.string.frag_tab_map_title));

		mTabIndicator = (TabIndicator) findViewById(R.id.tabindicator);
		mTabIndicator.setData(titleList, false);
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(PlaceBookingTabActivity.this).recordScreen(Screen.BOOKING_DETAIL);
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		lockUI();

		// 호텔 정보를 가져온다.
		requestPlaceBookingDetail();

		super.onResume();
	}

	private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener()
	{
		@Override
		public void onTabSelected(int position)
		{
			if (mFragmentViewPager == null)
			{
				return;
			}

			if (mFragmentViewPager.getCurrentItem() != position)
			{
				mFragmentViewPager.setCurrentItem(position);
			}
		}
	};

	protected OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			mTabIndicator.setCurrentItem(position);

			AnalyticsManager.getInstance(PlaceBookingTabActivity.this).recordEvent(Screen.BOOKING_DETAIL, Action.CLICK, mTabIndicator.getMainText(position), (long) position);
		}

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
