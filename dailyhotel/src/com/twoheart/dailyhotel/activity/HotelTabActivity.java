package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelTabBookingFragment;
import com.twoheart.dailyhotel.fragment.HotelTabInfoFragment;
import com.twoheart.dailyhotel.fragment.HotelTabMapFragment;
import com.twoheart.dailyhotel.obj.Hotel;
import com.twoheart.dailyhotel.obj.HotelDetail;
import com.twoheart.dailyhotel.obj.SaleTime;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.vo.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class HotelTabActivity extends BaseActivity implements OnClickListener,
		DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "HotelTabActivity";

	public HotelDetail hotelDetail;
	private SaleTime mSaleTime;
	private RequestQueue mQueue;

	protected List<Fragment> mFragments = new LinkedList<Fragment>();
	protected List<String> mTitles = new LinkedList<String>();

	private FragmentPagerAdapter mAdapter;
	private HotelViewPager mViewPager;
	private TabPageIndicator mIndicator;
	private Button btnSoldOut;
	private Button btnBooking;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_tab);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			hotelDetail = new HotelDetail();
			hotelDetail.setHotel((Hotel) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_HOTEL));
			mSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
		}

		mQueue = VolleyHttpClient.getRequestQueue();

		mViewPager = (HotelViewPager) findViewById(R.id.pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		btnSoldOut = (Button) findViewById(R.id.tv_hotel_tab_soldout);
		btnBooking = (Button) findViewById(R.id.btn_hotel_tab_booking);
		btnBooking.setOnClickListener(this);
		
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public Fragment getItem(int position) {
				return mFragments.get(position);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return mTitles.get(position);
			}

			@Override
			public int getCount() {
				return mFragments.size();
			}
		};

		mViewPager.setOffscreenPageLimit(mAdapter.getCount());
		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager);

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
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_HOTEL_DETAIL)) {

			try {
				JSONObject obj = response;
				JSONArray bookingArr = obj.getJSONArray("detail");
				JSONObject detailObj = bookingArr.getJSONObject(0);

				DecimalFormat comma = new DecimalFormat("###,##0");
				String strDiscount = comma.format(Integer.parseInt(detailObj
						.getString("discount")));
				String strPrice = comma.format(Integer.parseInt(detailObj
						.getString("price")));

				Hotel hotelBasic = hotelDetail.getHotel();

				hotelBasic.setAddress(detailObj.getString("address"));
				hotelBasic.setName(detailObj.getString("hotel_name"));
				hotelBasic.setDiscount(strDiscount);
				hotelBasic.setPrice(strPrice);
				hotelBasic.setCat(detailObj.getString("cat"));

				JSONArray imgArr = detailObj.getJSONArray("img");
				List<String> imageList = new ArrayList<String>();

				for (int i = 0; i < imgArr.length(); i++) {
					if (i == 0)
						continue;
					JSONObject imgObj = imgArr.getJSONObject(i);
					imageList.add(imgObj.getString("path"));
				}

				hotelDetail.setImageUrl(imageList);

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
				
				LoadingDialog.hideLoading();
				loadFragments();

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				Toast.makeText(this, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		Toast.makeText(this, "네트워크 상태를 확인해주세요", Toast.LENGTH_LONG).show();
		LoadingDialog.hideLoading();

	}

	private void loadFragments() {
		
		// TODO: BaseFragment 만들어서 통합적으로 관리할 것.
		mFragments.add(new HotelTabBookingFragment());
		mFragments.add(new HotelTabInfoFragment());
		mFragments.add(new HotelTabMapFragment());

		mTitles.add("예약");
		mTitles.add("정보");
		mTitles.add("지도");
		
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();

	}

}
