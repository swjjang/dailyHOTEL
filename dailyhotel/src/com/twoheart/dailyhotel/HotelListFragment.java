package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.adapter.RegionListAdapter;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class HotelListFragment extends BaseFragment implements Constants,
		OnItemClickListener, OnNavigationListener,
		DailyHotelJsonArrayResponseListener, DailyHotelJsonResponseListener,
		DailyHotelStringResponseListener, OnRefreshListener {

	private final static String TAG = "HotelListFragment";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;
	
	private ListView mHotelListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private HotelListAdapter mHotelListAdapter;
	private List<Hotel> mHotelList;
	private List<String> mRegionList;
	private SaleTime mDailyHotelSaleTime;
	private Button btnListViewHeader;
	private ImageView ivNewEvent;

	private boolean mRefreshHotelList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_list, container,
				false);
		mQueue = VolleyHttpClient.getRequestQueue();
		mDailyHotelSaleTime = new SaleTime();
		mRefreshHotelList = true;
		mHotelListView = (ListView) view
				.findViewById(R.id.listview_hotel_list);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mHostActivity = (MainActivity) getActivity();
		mHostActivity.setActionBar("오늘의 호텔");
		
		View listViewHeader = inflater
				.inflate(R.layout.header_hotel_list, null);
		mHotelListView.addHeaderView(listViewHeader);

		ivNewEvent = (ImageView) view.findViewById(R.id.iv_new_event);
		btnListViewHeader = (Button) view.findViewById(R.id.btn_footer);
		btnListViewHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mHostActivity, EventWebActivity.class);
				mHostActivity.startActivity(i);
				mHostActivity.overridePendingTransition(R.anim.slide_in_bottom,
						R.anim.hold);
			}
		});
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(R.color.dh_theme_color, android.R.color.transparent, 
				R.color.dh_theme_color, android.R.color.transparent);
		
		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());

	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (mRefreshHotelList) {
			LoadingDialog.showLoading(mHostActivity);

			// 현재 서버 시간을 가져온다
			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME)
					.toString(), null, HotelListFragment.this,
					mHostActivity));
		}
	}

	@Override
	public void onPause() {
		
		mRefreshHotelList = true;
		// mPosition =
		// mPullToRefreshListView.getRefreshableView().getFirstVisiblePosition();
		// mScrollY = mPullToRefreshListView.getChildAt(0).getTop();

		super.onPause();

	}

	// 호텔 클릭시
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView,
			int position, long id) {
		int selectedPosition = position - 1;

		Hotel selectedHotel = mHotelList.get(selectedPosition);

		Intent i = new Intent(mHostActivity, HotelTabActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, selectedHotel);
		i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mDailyHotelSaleTime);

		startActivityForResult(i, CODE_REQUEST_ACTIVITY_HOTELTAB);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_REQUEST_ACTIVITY_HOTELTAB) {
			mRefreshHotelList = false;
			
			if (resultCode == Activity.RESULT_OK) {
				mHostActivity
						.replaceFragment(mHostActivity
								.getFragment(mHostActivity.INDEX_BOOKING_LIST_FRAGMENT));
				
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		LoadingDialog.showLoading(mHostActivity);
		fetchHotelList(position);

		return true;
	}

	private void fetchHotelList(int position) {
		mHostActivity.drawerLayout.closeDrawer(mHostActivity.drawerList);

		String selectedRegion = mRegionList.get(position);

		if (position != mHostActivity.sharedPreference.getInt(
				KEY_PREFERENCE_REGION_INDEX, 0)) {
			SharedPreferences.Editor editor = mHostActivity.sharedPreference
					.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT, selectedRegion);
			editor.putInt(KEY_PREFERENCE_REGION_INDEX, position);
			editor.commit();

		}

		selectedRegion = selectedRegion.replace(" ", "%20");
		selectedRegion = selectedRegion.replace("|", "%7C");

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_WEBAPI_HOTEL).append(selectedRegion)
				.append("/near/0/0/0/1000/")
				.append(mDailyHotelSaleTime.getCurrentYear()).append("/")
				.append(mDailyHotelSaleTime.getCurrentMonth()).append("/")
				.append(mDailyHotelSaleTime.getCurrentDay()).toString();

		Log.d(TAG, url);

		// 호텔 리스트를 가져온다
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null,
				HotelListFragment.this, mHostActivity));

	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_APP_SALE_TIME)) {

			try {
				String open = response.getString("open");
				String close = response.getString("close");
				
				mDailyHotelSaleTime.setOpenTime(open);
//				mDailyHotelSaleTime.setOpenTime("09:00:00");
				mDailyHotelSaleTime.setCloseTime(close);

				if (!mDailyHotelSaleTime.isSaleTime()) {
					mHostActivity.replaceFragment(WaitTimerFragment.newInstance(
							mDailyHotelSaleTime));
					mListener.onLoadComplete(this, true);
				} else {
					// 지역 리스트를 가져온다
					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_SITE_LOCATION_LIST).toString(),
							null, HotelListFragment.this,
							mHostActivity));
				}

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}

		} else if (url.contains(URL_WEBAPI_HOTEL)) {

			try {
				mHotelList = new ArrayList<Hotel>();
				JSONArray hotelArr = response.getJSONArray("hotels");

				for (int i = 0; i < hotelArr.length(); i++) {
					JSONObject obj = hotelArr.getJSONObject(i);

					Hotel newHotel = new Hotel();

					String name = obj.getString("name");
					String price = obj.getString("price");
					String discount = obj.getString("discount");
					String address = obj.getString("addr_summary");
					String category = obj.getString("cat");
					int idx = obj.getInt("idx");
					int available = obj.getInt("avail_room_count");
					int seq = obj.getInt("seq");

					JSONArray arr = obj.getJSONArray("img");
					String image = "default";
					if (arr.length() != 0) {
						JSONObject arrObj = arr.getJSONObject(0);
						image = arrObj.getString("path");
					}

					newHotel.setName(name);
					newHotel.setPrice(price);
					newHotel.setDiscount(discount);
					newHotel.setAddress(address);
					newHotel.setCategory(category);
					newHotel.setIdx(idx);
					newHotel.setAvailableRoom(available);
					newHotel.setSequence(seq);
					newHotel.setImage(image);

					if (seq >= 0) { // 숨김호텔이 아니라면 추가. (음수일 경우 숨김호텔.)

						if (available <= 0) // SOLD OUT 된 항목은 밑으로.
							available *= 100;

						mHotelList.add(newHotel); // 추가.

						// seq 값에 따른 리스트 정렬
						Comparator<Hotel> comparator = new Comparator<Hotel>() {
							public int compare(Hotel o1, Hotel o2) {
								// 숫자정렬
								return Integer.parseInt(o1.getSequence() + "")
										- Integer.parseInt(o2.getSequence() + "");
							}
						};

						Collections.sort(mHotelList, comparator);

					}

				}

				mHotelListAdapter = new HotelListAdapter(mHostActivity,
						R.layout.list_row_hotel, mHotelList);
				mHotelListView.setAdapter(mHotelListAdapter);
				mHotelListView.setOnItemClickListener(this);
				mSwipeRefreshLayout.setRefreshing(false);
				
				mRefreshHotelList = true;
				
				// 새로운 이벤트 확인을 위해 버전 API 호출
				mQueue.add(new DailyHotelJsonRequest(Method.GET, 
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(),
						null, this, mHostActivity));
				
				mListener.onLoadComplete(this, true);

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}
		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {
			try {
				if (response.getString("new_event").equals("1")) {
					
					if (ivNewEvent != null)
						ivNewEvent.setVisibility(View.VISIBLE);
					
				}
			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}
		}

	}

	@Override
	public void onResponse(String url, JSONArray response) {
		if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
			try {
				mRegionList = new ArrayList<String>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = obj.getString("name");
					mRegionList.add(name);
				}

				mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
				mHostActivity.actionBar
						.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

				RegionListAdapter regionListAdapter = new RegionListAdapter(
						mHostActivity, mRegionList);

				mHostActivity.actionBar.setListNavigationCallbacks(
						regionListAdapter, this);
				mHostActivity.actionBar
						.setSelectedNavigationItem(mHostActivity.sharedPreference
								.getInt(KEY_PREFERENCE_REGION_INDEX, 0));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				mListener.onLoadComplete(this, false);
			}
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_APP_TIME)) {
			Log.d(TAG, response);
			mDailyHotelSaleTime.setCurrentTime(response);

			// 오픈, 클로즈 타임을 가져온다
			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME)
					.toString(), null, HotelListFragment.this,
					mHostActivity));

		}
	}

	@Override
	public void onRefresh() {
		fetchHotelList(mHostActivity.actionBar
				.getSelectedNavigationIndex());
	}

}
