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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;

public class BookingTabActivity extends BaseActivity
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<BaseFragment> mFragmentList;

	public HotelDetail mHotelDetail;
	public Booking booking;
	private int mPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHotelDetail = new HotelDetail();
		booking = new Booking();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
		}

		setContentView(R.layout.activity_booking_tab);
		setActionBar(R.string.actionbar_title_booking_tab_activity);

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add(getString(R.string.frag_booking_tab_title));
		titleList.add(getString(R.string.frag_tab_info_title));
		titleList.add(getString(R.string.frag_tab_map_title));

		mTabIndicator = (TabIndicator) findViewById(R.id.tabindicator);
		mTabIndicator.setData(titleList, false);
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
	}

	private void onPostSetCookie()
	{
		String[] date = booking.getSday().split("-");

		ExLog.d("date", date);

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append('/').append(booking.getHotel_idx()).append("/").append(date[0]).append("/").append(date[1]).append("/").append(date[2]).toString();
		ExLog.d(url);

		lockUI();

		// 호텔 정보를 가져온다.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, mHotelDetailJsonResponseListener, this));
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
			mFragmentViewPager.setOnPageChangeListener(mOnPageChangeListener);

			mFragmentList = new ArrayList<BaseFragment>();

			BaseFragment baseFragment01 = BookingTabBookingFragment.newInstance(mHotelDetail, booking, strings);
			mFragmentList.add(baseFragment01);

			BaseFragment baseFragment02 = TabInfoFragment.newInstance(mHotelDetail, titleList.get(1));
			mFragmentList.add(baseFragment02);

			BaseFragment baseFragment03 = TabMapFragment.newInstance(mHotelDetail, titleList.get(2));
			mFragmentList.add(baseFragment03);

			mFragmentViewPager.setData(mFragmentList);
			mFragmentViewPager.setAdapter(getSupportFragmentManager());

			// pinkred_font
			//			GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
		}
	}

	@Override
	protected void onResume()
	{
		onPostSetCookie();

		if (mPosition == 0)
		{
			RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_booking", "/bookings/" + booking.getHotel_name() + "/booking");
		}
		if (mPosition == 1)
		{
			RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_info", "/bookings/" + booking.getHotel_name() + "/info");
		}
		if (mPosition == 2)
		{
			RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_map", "/bookings/" + booking.getHotel_name() + "/map");
		}

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

			if (position == 0)
			{
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_booking", "/bookings/" + booking.getHotel_name() + "/booking");
			} else if (position == 1)
			{
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_info", "/bookings/" + booking.getHotel_name() + "/info");
			} else if (position == 2)
			{
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_map", "/bookings/" + booking.getHotel_name() + "/map");
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			// TODO Auto-generated method stub
			
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

				JSONArray bookingArr = response.getJSONArray("detail");
				JSONObject detailObj = bookingArr.getJSONObject(0);

				if (mHotelDetail.getHotel() == null)
				{
					mHotelDetail.setHotel(new Hotel());
				}

				Hotel hotelBasic = mHotelDetail.getHotel();

				hotelBasic.setName(detailObj.getString("hotel_name"));
				hotelBasic.setCategory(detailObj.getString("cat"));
				hotelBasic.setAddress(detailObj.getString("address"));
				mHotelDetail.setHotel(hotelBasic);

				JSONArray specArr = response.getJSONArray("spec");
				int length = specArr.length();

				Map<String, List<String>> contentList = new LinkedHashMap<String, List<String>>(length);

				for (int i = 0; i < length; i++)
				{
					JSONObject specObj = specArr.getJSONObject(i);
					String key = specObj.getString("key");
					JSONArray valueArr = specObj.getJSONArray("value");

					int valueLength = valueArr.length();
					List<String> valueList = new ArrayList<String>(valueLength);

					for (int j = 0; j < valueLength; j++)
					{
						JSONObject valueObj = valueArr.getJSONObject(j);
						String value = valueObj.getString("value");
						valueList.add(value);
					}

					contentList.put(key, valueList);
				}

				mHotelDetail.setSpecification(contentList);

				double latitude = detailObj.getDouble("lat");
				double longitude = detailObj.getDouble("lng");

				mHotelDetail.setLatitude(latitude);
				mHotelDetail.setLongitude(longitude);

				int saleIdx = detailObj.getInt("idx");
				mHotelDetail.setSaleIdx(saleIdx);

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

	//
	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_HOTEL_DETAIL)) {
	//			try {
	//				JSONObject obj = response;
	//				JSONArray bookingArr = obj.getJSONArray("detail");
	//				JSONObject detailObj = bookingArr.getJSONObject(0);
	//				
	//				ExLog.e(response.toString());
	//				
	//				if (hotelDetail.getHotel() == null) hotelDetail.setHotel(new Hotel());
	//				
	//				Hotel hotelBasic = hotelDetail.getHotel();
	//				
	//				hotelBasic.setName(detailObj.getString("hotel_name"));
	//				hotelBasic.setCategory(detailObj.getString("cat"));
	//				hotelBasic.setAddress(detailObj.getString("address"));
	//				hotelDetail.setHotel(hotelBasic);
	//
	//				JSONArray specArr = obj.getJSONArray("spec");
	//				Map<String, List<String>> contentList = new LinkedHashMap<String, List<String>>();
	//				for (int i = 0; i < specArr.length(); i++) {
	//
	//					JSONObject specObj = specArr.getJSONObject(i);
	//					String key = specObj.getString("key");
	//					JSONArray valueArr = specObj.getJSONArray("value");
	//
	//					List<String> valueList = new ArrayList<String>();
	//
	//					for (int j = 0; j < valueArr.length(); j++) {
	//						JSONObject valueObj = valueArr.getJSONObject(j);
	//						String value = valueObj.getString("value");
	//						valueList.add(value);
	//					}
	//
	//					contentList.put(key, valueList);
	//
	//				}
	//				hotelDetail.setSpecification(contentList);
	//
	//				double latitude = detailObj.getDouble("lat");
	//				double longitude = detailObj.getDouble("lng");
	//
	//				hotelDetail.setLatitude(latitude);
	//				hotelDetail.setLongitude(longitude);
	//				
	//				int saleIdx = detailObj.getInt("idx");
	//				hotelDetail.setSaleIdx(saleIdx);
	//				
	//				mFragments.clear();
	//				loadFragments();
	//				
	//				unLockUI();
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//	}
}
