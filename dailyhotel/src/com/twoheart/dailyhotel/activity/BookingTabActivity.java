package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class BookingTabActivity extends TabActivity {

	private final static String TAG = "BookingTabActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("예약확인");
		setContentView(R.layout.activity_booking_tab);

		mViewPager = (HotelViewPager) findViewById(R.id.booking_pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.booking_indicator);

		setTabPage();
		String[] date = booking.getSday().split("-");

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_WEBAPI_HOTEL_DETAIL).append(booking.getHotel_idx())
				.append("/").append(date[0]).append("/").append(date[1])
				.append("/").append(date[2]).toString();

		Log.d(TAG, url);

		LoadingDialog.showLoading(this);
		// 호텔 정보를 가져온다.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, this, this));

	}

	@Override
	protected void loadFragments() {
		
		// TODO: BaseFragment 만들어서 통합적으로 관리할 것.
		mFragments.add(new BookingTabBookingFragment());
		mFragments.add(new TabInfoFragment());
		mFragments.add(new TabMapFragment());

		mTitles.add("예약");
		mTitles.add("정보");
		mTitles.add("지도");

		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();

	}
}
