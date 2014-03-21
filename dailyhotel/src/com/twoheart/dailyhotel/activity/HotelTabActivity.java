package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class HotelTabActivity extends TabActivity implements OnClickListener,
		DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "HotelTabActivity";
	private Button btnSoldOut;
	private Button btnBooking;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_tab);

		mViewPager = (HotelViewPager) findViewById(R.id.pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		btnSoldOut = (Button) findViewById(R.id.tv_hotel_tab_soldout);
		btnBooking = (Button) findViewById(R.id.btn_hotel_tab_booking);
		btnBooking.setOnClickListener(this);
		
		setTabPage();
		setActionBar(hotelDetail.getHotel().getName());

		// 호텔 sold out시
		if (hotelDetail.getHotel().getAvali_cnt() == 0) {
			btnBooking.setVisibility(View.GONE);
			btnSoldOut.setVisibility(View.VISIBLE);
		}

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_WEBAPI_HOTEL_DETAIL)
				.append(hotelDetail.getHotel().getIdx()).append("/")
				.append(mSaleTime.getCurrentYear()).append("/")
				.append(mSaleTime.getCurrentMonth()).append("/")
				.append(mSaleTime.getCurrentDay()).toString();

		Log.d(TAG, url);
		
		LoadingDialog.showLoading(this);
		// 호텔 정보를 가져온다.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, this, this));

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnBooking.getId()) { // TODO: 로그인/로그아웃 상태를
												// HotelPaymentActivity에서 관리하도록
												// 이전할 것.
			Intent i = new Intent(this, HotelPaymentActivity.class);
			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, hotelDetail);
			startActivityForResult(i, CODE_REQUEST_ACTIVITY_HOTELTAB);
			overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_REQUEST_ACTIVITY_HOTELTAB) {
			if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS) {
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void loadFragments() {
		
		// TODO: BaseFragment 만들어서 통합적으로 관리할 것.
		mFragments.add(new HotelTabBookingFragment());
		mFragments.add(new TabInfoFragment());
		mFragments.add(new TabMapFragment());

		mTitles.add("예약");
		mTitles.add("정보");
		mTitles.add("지도");
		
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();

	}

}
