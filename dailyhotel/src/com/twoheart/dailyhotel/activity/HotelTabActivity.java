package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.TabActivity;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class HotelTabActivity extends TabActivity implements OnClickListener,
		DailyHotelJsonResponseListener, ErrorListener,
		DailyHotelStringResponseListener {

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

		setActionBar(hotelDetail.getHotel().getName());

		// 호텔 sold out시
		if (hotelDetail.getHotel().getAvailableRoom() == 0) {
			btnBooking.setVisibility(View.GONE);
			btnSoldOut.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onPostSetCookie() {
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
		if (v.getId() == btnBooking.getId()) {

			LoadingDialog.showLoading(this);

			mQueue.add(new DailyHotelStringRequest(Method.GET,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_ALIVE).toString(), null, this, this));

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT) {
			setResult(resultCode);

			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN) {
			if (resultCode == RESULT_OK)
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
						URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE)
						.toString(), null, this, this));
		}


		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void loadFragments() {

		// TODO: BaseFragment 만들어서 통합적으로 관리할 것.
		mFragments.add(new HotelTabBookingFragment());
		mFragments.add(new TabInfoFragment());
		mFragments.add(new TabMapFragment());

		super.loadFragments();

	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			LoadingDialog.hideLoading();
			
			String result = response.trim();
			if (result.equals("alive")) { // session alive
				Intent i = new Intent(this, BookingActivity.class);
				i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, hotelDetail);
				startActivityForResult(i, CODE_REQUEST_ACTIVITY_PAYMENT);
				overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

			} else if (result.equals("dead")) { // session dead
				loadLoginProcess();

			} else {
				Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private void loadLoginProcess() {
		Toast.makeText(this, " 로그인이 필요합니다", Toast.LENGTH_LONG).show();

		startActivityForResult(new Intent(this, LoginActivity.class),
				CODE_REQUEST_ACTIVITY_LOGIN);
		overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

	}

}
