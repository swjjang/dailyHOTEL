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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.util.ui.OnLoadCompleteListener;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public abstract class TabActivity extends BaseActivity implements
		DailyHotelJsonResponseListener, ErrorListener, OnLoadCompleteListener {

	private static final String TAG = "TabActivity";

	public HotelDetail hotelDetail;
	public Booking booking;
	protected SaleTime mSaleTime;
	protected RequestQueue mQueue;

	protected List<Fragment> mFragments = new LinkedList<Fragment>();
	protected List<String> mTitles = new LinkedList<String>();

	protected FragmentPagerAdapter mAdapter;
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_tab);
		
		hotelDetail = new HotelDetail();
		booking = new Booking();
		mSaleTime = new SaleTime();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			hotelDetail.setHotel((Hotel) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_HOTEL));
			mSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
			booking = (Booking) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
		}

		mQueue = VolleyHttpClient.getRequestQueue();

	}
	
	protected abstract void onPostSetCookie();
	
	@Override
	protected void onResume() {
		super.onResume();
		onPostSetCookie();
		
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

		mViewPager.setOffscreenPageLimit(mAdapter.getCount() + 2);
		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager);
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		try {
			JSONObject obj = response;
			JSONArray bookingArr = obj.getJSONArray("detail");
			JSONObject detailObj = bookingArr.getJSONObject(0);

			DecimalFormat comma = new DecimalFormat("###,##0");
			String strDiscount = comma.format(Integer.parseInt(detailObj
					.getString("discount")));
			String strPrice = comma.format(Integer.parseInt(detailObj
					.getString("price")));
			
			if (hotelDetail.getHotel() == null)
				 hotelDetail.setHotel(new Hotel());
			
			Hotel hotelBasic = hotelDetail.getHotel();

			hotelBasic.setAddress(detailObj.getString("address"));
			hotelBasic.setName(detailObj.getString("hotel_name"));
			hotelBasic.setDiscount(strDiscount);
			hotelBasic.setPrice(strPrice);
			hotelBasic.setCategory(detailObj.getString("cat"));
			hotelBasic.setBedType(detailObj.getString("bed_type"));
			
			hotelDetail.setHotel(hotelBasic);

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
			
			int saleIdx = detailObj.getInt("idx");
			hotelDetail.setSaleIdx(saleIdx);
			
			mFragments.clear();
			loadFragments();
			
			LoadingDialog.hideLoading();

		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();

			Toast.makeText(this, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
		}

	}

	protected void loadFragments() {
		mTitles.add("예약");
		mTitles.add("정보");
		mTitles.add("지도");

		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
	
	@Override
	public void onLoadComplete(Fragment fragment, boolean isSucceed) {
		if (!isSucceed) {
			Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
					Toast.LENGTH_SHORT).show();
		}
		
		if (fragment != null)
			if (fragment.getView() != null)
				GlobalFont.apply((ViewGroup) fragment.getView().getRootView());
		
		LoadingDialog.hideLoading();
			
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();
		finish();
	}
	
}
