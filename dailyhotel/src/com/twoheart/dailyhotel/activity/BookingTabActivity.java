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

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Hotel.HotelGrade;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;

public class BookingTabActivity extends BaseActivity
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<BaseFragment> mFragmentList;

	public BookingHotelDetail mHotelDetail;
	public Booking booking;
	private int mPosition = 0;

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
		setActionBar(booking.getHotel_name());

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
			String[] strings = { getString(R.string.drawer_menu_pin_title_resrvation), getString(R.string.frag_booking_tab_year), getString(R.string.frag_booking_tab_month), getString(R.string.frag_booking_tab_day), getString(R.string.frag_booking_tab_hour) };

			ArrayList<String> titleList = new ArrayList<String>();
			titleList.add(getString(R.string.frag_booking_tab_title));
			titleList.add(getString(R.string.frag_tab_info_title));
			titleList.add(getString(R.string.frag_tab_map_title));

			mFragmentViewPager = (FragmentViewPager) findViewById(R.id.fragmentViewPager);

			mFragmentList = new ArrayList<BaseFragment>();

			BaseFragment baseFragment01 = BookingTabBookingFragment.newInstance(mHotelDetail, booking, strings);
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
		String params = String.format("?reservationIdx=%d", booking.index);
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_HOTEL_ROOM_INFO).append(params).toString(), null, mHotelDetailJsonResponseListener, this));

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

			mPosition = position;

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

	private DailyHotelJsonResponseListener mHotelDetailJsonResponseListener = new DailyHotelJsonResponseListener()
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
							AlertDialog alertDlg = SimpleAlertDialog.build(BookingTabActivity.this, null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null).create();
							alertDlg.show();
							break;
						}
					}

					finish();
					return;
				}

				JSONObject jsonObject = response.getJSONObject("data");

				if (mHotelDetail.getHotel() == null)
				{
					mHotelDetail.setHotel(new Hotel());
				}

				Hotel hotelBasic = mHotelDetail.getHotel();

				hotelBasic.setName(jsonObject.getString("hotel_name"));

				try
				{
					hotelBasic.setCategory(jsonObject.getString("cat"));
				} catch (Exception e)
				{
					hotelBasic.setCategory(HotelGrade.etc.name());
				}

				hotelBasic.setAddress(jsonObject.getString("address"));
				mHotelDetail.setHotel(hotelBasic);

				JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("spec"));
				JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");
				mHotelDetail.setSpecification(jsonArray);

				double latitude = jsonObject.getDouble("lat");
				double longitude = jsonObject.getDouble("lng");

				mHotelDetail.setLatitude(latitude);
				mHotelDetail.setLongitude(longitude);

				int saleIdx = jsonObject.getInt("idx");
				mHotelDetail.setSaleIdx(saleIdx);
				mHotelDetail.roomName = jsonObject.getString("room_name");

				loadFragments();
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};
}
