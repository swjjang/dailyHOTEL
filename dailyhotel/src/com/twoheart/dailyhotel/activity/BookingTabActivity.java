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

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;
import com.twoheart.dailyhotel.view.widget.TabIndicator;
import com.twoheart.dailyhotel.view.widget.TabIndicator.OnTabSelectedListener;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

public class BookingTabActivity extends BaseActivity
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<BaseFragment> mFragmentList;

	public BookingHotelDetail mHotelDetail;
	public Booking booking;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHotelDetail = new BookingHotelDetail();
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
		setActionBar(booking.getHotelName());

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add(getString(R.string.frag_booking_tab_title));
		titleList.add(getString(R.string.frag_tab_info_title));
		titleList.add(getString(R.string.frag_tab_map_title));

		mTabIndicator = (TabIndicator) findViewById(R.id.tabindicator);
		mTabIndicator.setData(titleList, false);
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
	}

	private void loadFragments()
	{

		if (mFragmentViewPager == null)
		{
			ArrayList<String> titleList = new ArrayList<String>();
			titleList.add(getString(R.string.frag_booking_tab_title));
			titleList.add(getString(R.string.frag_tab_info_title));
			titleList.add(getString(R.string.frag_tab_map_title));

			mFragmentViewPager = (FragmentViewPager) findViewById(R.id.fragmentViewPager);

			mFragmentList = new ArrayList<BaseFragment>();

			BaseFragment baseFragment01 = BookingTabBookingFragment.newInstance(mHotelDetail, booking, getString(R.string.drawer_menu_pin_title_resrvation));
			mFragmentList.add(baseFragment01);

			BaseFragment baseFragment02 = TabInfoFragment.newInstance(mHotelDetail, titleList.get(1));
			mFragmentList.add(baseFragment02);

			BaseFragment baseFragment03 = TabMapFragment.newInstance(mHotelDetail, titleList.get(2));
			mFragmentList.add(baseFragment03);

			mFragmentViewPager.setData(mFragmentList);
			mFragmentViewPager.setAdapter(getSupportFragmentManager());

			mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
			mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);
		}
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(BookingTabActivity.this).recordScreen(Screen.BOOKING_DETAIL);
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		lockUI();

		// 호텔 정보를 가져온다.
		String params = String.format("?reservationIdx=%d", booking.reservationIndex);
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_DETAIL).append(params).toString(), null, mReservationBookingDetailJsonResponseListener, this));

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

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			mTabIndicator.setCurrentItem(position);

			AnalyticsManager.getInstance(BookingTabActivity.this).recordEvent(Screen.BOOKING_DETAIL, Action.CLICK, mTabIndicator.getMainText(position), (long) position);
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

	private DailyHotelJsonResponseListener mReservationBookingDetailJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					// 에러가 나오는 경우 처리는 추후 통합해서 관리해야 한다.
					switch (msg_code)
					{
						case 100:
						{
							String msg = response.getString("msg");
							DailyToast.showToast(BookingTabActivity.this, msg, Toast.LENGTH_SHORT);
							break;
						}

						case 200:
						{
							if (isFinishing() == true)
							{
								return;
							}

							String msg = response.getString("msg");
							showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
							break;
						}
					}

					finish();
					return;
				}

				JSONObject jsonObject = response.getJSONObject("data");

				boolean result = mHotelDetail.setData(jsonObject);

				if (result == true)
				{
					loadFragments();
				} else
				{
					throw new NullPointerException("result == false");
				}
			} catch (Exception e)
			{
				onError(e);
				finish();
			} finally
			{
				unLockUI();
			}
		}
	};
}
