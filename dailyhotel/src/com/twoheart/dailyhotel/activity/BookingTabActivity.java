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
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.TabActivity;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class BookingTabActivity extends TabActivity implements DailyHotelJsonResponseListener {

	private final static String TAG = "BookingTabActivity";
	public Booking booking;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hotelDetail = new HotelDetail();
		booking = new Booking();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
		
		setActionBar(R.string.actionbar_title_booking_tab_activity);
		setContentView(R.layout.activity_booking_tab);

		mViewPager = (HotelViewPager) findViewById(R.id.booking_pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.booking_indicator);
		
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (position == 0) RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_booking", "/bookings/" + booking.getHotel_name() + "/booking");
				else if (position == 1) RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_info", "/bookings/" + booking.getHotel_name() + "/info");
				else if (position == 2) RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail_map", "/bookings/" + booking.getHotel_name() + "/map");		
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

	}
	
	@Override
	protected void onPostSetCookie() {
		String[] date = booking.getSday().split("-");
		for (int i=0;i<date.length;i++) android.util.Log.e("date",date[i].toString());
		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_WEBAPI_HOTEL_DETAIL).append(booking.getHotel_idx())
				.append("/").append(date[0]).append("/").append(date[1])
				.append("/").append(date[2]).toString();
		Log.d(TAG, url);
		
		lockUI();
		// 호텔 정보를 가져온다.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, this, this));
	}

	@Override
	protected void loadFragments() {
		String[] strings = {getString(R.string.drawer_menu_pin_title_resrvation), getString(R.string.frag_booking_tab_year), 
				getString(R.string.frag_booking_tab_month), getString(R.string.frag_booking_tab_day), getString(R.string.frag_booking_tab_hour)};
		mFragments.add(BookingTabBookingFragment.newInstance(hotelDetail, booking, strings));
		super.loadFragments();
	}
	
	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_HOTEL_DETAIL)) {
			try {
				JSONObject obj = response;
				JSONArray bookingArr = obj.getJSONArray("detail");
				JSONObject detailObj = bookingArr.getJSONObject(0);
				
				android.util.Log.e("TestDetail",response.toString());
				
				if (hotelDetail.getHotel() == null) hotelDetail.setHotel(new Hotel());
				
				Hotel hotelBasic = hotelDetail.getHotel();
				
				hotelBasic.setName(detailObj.getString("hotel_name"));
				hotelBasic.setCategory(detailObj.getString("cat"));
				hotelBasic.setAddress(detailObj.getString("address"));
				hotelDetail.setHotel(hotelBasic);

				JSONArray specArr = obj.getJSONArray("spec");
				Map<String, List<String>> contentList = new LinkedHashMap<String, List<String>>();
				for (int i = 0; i < specArr.length(); i++) {

					JSONObject specObj = specArr.getJSONObject(i);
					String key = specObj.getString("key");
					JSONArray valueArr = specObj.getJSONArray("value");

					List<String> valueList = new ArrayList<String>();

					for (int j = 0; j < valueArr.length(); j++) {
						JSONObject valueObj = valueArr.getJSONObject(j);
						String value = valueObj.getString("value");
						valueList.add(value);
					}

					contentList.put(key, valueList);

				}
				hotelDetail.setSpecification(contentList);

				double latitude = detailObj.getDouble("lat");
				double longitude = detailObj.getDouble("lng");

				hotelDetail.setLatitude(latitude);
				hotelDetail.setLongitude(longitude);
				
				int saleIdx = detailObj.getInt("idx");
				hotelDetail.setSaleIdx(saleIdx);
				
				mFragments.clear();
				loadFragments();
				
				unLockUI();

			} catch (Exception e) {
				onError(e);
			}
		}
	}
}
