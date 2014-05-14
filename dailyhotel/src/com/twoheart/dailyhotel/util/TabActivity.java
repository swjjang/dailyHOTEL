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

import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public abstract class TabActivity extends BaseActivity implements
		DailyHotelJsonResponseListener {

	private static final String TAG = "TabActivity";

	public HotelDetail hotelDetail;
	public Booking booking;
	protected SaleTime mSaleTime;
	protected RequestQueue mQueue;

	protected List<BaseFragment> mFragments;

	protected FragmentPagerAdapter mAdapter;
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFragments = new LinkedList<BaseFragment>();
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
		
		if (mAdapter == null) {
			mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
	
				@Override
				public Fragment getItem(int position) {
					return mFragments.get(position);
				}
	
				@Override
				public CharSequence getPageTitle(int position) {
					return mFragments.get(position).getTitle();
				}
	
				@Override
				public int getCount() {
					return mFragments.size();
				}
			};
	
			mViewPager.setOffscreenPageLimit(mAdapter.getCount() + 2);
			mViewPager.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
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
			
			unLockUI();

		} catch (Exception e) {
			onError(e);
		}

	}

	protected void loadFragments() {
		mFragments.add(TabInfoFragment.newInstance(hotelDetail));
		mFragments.add(TabMapFragment.newInstance(hotelDetail));
		
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
		
		GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
	}
}
