package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.obj.Hotel;
import com.twoheart.dailyhotel.obj.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.vo.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.vo.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.vo.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class HotelListFragment extends Fragment implements Constants,
		OnItemClickListener, OnNavigationListener,
		DailyHotelJsonArrayResponseListener, DailyHotelJsonResponseListener,
		DailyHotelResponseListener, ErrorListener {

	private final static String TAG = "HotelListFragment";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;
	
	private PullToRefreshListView mPullToRefreshListView;
	private HotelListAdapter mHotelListAdapter;
	private List<Hotel> mHotelList;
	private List<String> mRegionList;
	private SaleTime mDailyHotelSaleTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);
		mQueue = VolleyHttpClient.getRequestQueue();
		mDailyHotelSaleTime = new SaleTime();
		mHostActivity = (MainActivity) getActivity();
		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.listview_hotel_list);

		ListView hotelListView = mPullToRefreshListView.getRefreshableView();
		View listViewHeader = inflater.inflate(R.layout.header_hotel_list, null);
		hotelListView.addHeaderView(listViewHeader);

		ImageButton btnListViewHeader = (ImageButton) view
				.findViewById(R.id.btn_footer);
		btnListViewHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mHostActivity, EventWebActivity.class);
				mHostActivity.startActivity(i);
				mHostActivity.overridePendingTransition(R.anim.slide_in_bottom,
						R.anim.hold);
			}
		});

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		LoadingDialog.showLoading(mHostActivity);

		// 현재 서버 시간을 가져온다
		mQueue.add(new DailyHotelRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME).toString(),
				null, HotelListFragment.this, HotelListFragment.this));
	}

	

	// 호텔 클릭시
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView,
			int position, long id) {

		int selectedPosition = position - 2;
		Hotel selectedHotel = mHotelList.get(selectedPosition);
		
		Intent i = new Intent(mHostActivity, HotelTabActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, selectedHotel);
		i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mDailyHotelSaleTime);

		startActivityForResult(i, CODE_REQUEST_ACTIVITY_MAIN);
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_REQUEST_ACTIVITY_MAIN) {
			if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS) {
				mHostActivity.replaceFragment(mHostActivity.getFragment(mHostActivity.INDEX_BOOKING_LIST_FRAGMENT));
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
		
		SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
		editor.putString(KEY_PREFERENCE_REGION_SELECT, selectedRegion);
		editor.putInt(KEY_PREFERENCE_REGION_INDEX, position);
		editor.commit();
		
		selectedRegion = selectedRegion.replace(" ", "%20");
		selectedRegion = selectedRegion.replace("|", "%7C");
		
		String url = new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL)
				.append(selectedRegion).append("/near/0/0/0/1000/")
				.append(mDailyHotelSaleTime.getCurrentYear()).append("/")
				.append(mDailyHotelSaleTime.getCurrentMonth()).append("/")
				.append(mDailyHotelSaleTime.getCurrentDay()).toString();
		
		Log.d(TAG, url);

		// 호텔 리스트를 가져온다
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null,
				HotelListFragment.this, HotelListFragment.this));
		
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		mHostActivity.addFragment(new ErrorFragment());
		LoadingDialog.hideLoading();
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_APP_SALE_TIME)) {

			try {
				String open = response.getString("open");
				String close = response.getString("close");

				mDailyHotelSaleTime.setOpenTime(open);
				mDailyHotelSaleTime.setCloseTime(close);

				if (!mDailyHotelSaleTime.isSaleTime()) {
					mHostActivity.replaceFragment(new WaitTimerFragment(mDailyHotelSaleTime));
					LoadingDialog.hideLoading();
				} else {
					// 지역 리스트를 가져온다
					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_SITE_LOCATION_LIST).toString(),
							null, HotelListFragment.this,
							HotelListFragment.this));
				}
				
			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				LoadingDialog.hideLoading();
				mHostActivity.addFragment(new ErrorFragment());
				
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
					newHotel.setCat(category);
					newHotel.setIdx(idx);
					newHotel.setAvali_cnt(available);
					newHotel.setSeq(seq);
					newHotel.setImg(image);
					
					if (seq >= 0) {	// 숨김호텔이 아니라면 추가. (음수일 경우 숨김호텔.)
						
						if (available <= 0)	// SOLD OUT 된 항목은 밑으로.
							available *= 100;
						
						mHotelList.add(newHotel);	// 추가. 
						
						// seq 값에 따른 리스트 정렬
						Comparator<Hotel> comparator = new Comparator<Hotel>() {
							public int compare(Hotel o1, Hotel o2) {
								// 숫자정렬
								return Integer.parseInt(o1.getSeq() + "")
										- Integer.parseInt(o2.getSeq() + "");
							}
						};
						
						Collections.sort(mHotelList, comparator);
						
					}
					
				}

				mHotelListAdapter = new HotelListAdapter(mHostActivity,
						R.layout.list_row_hotel, mHotelList);
				mPullToRefreshListView.setAdapter(mHotelListAdapter);
				mPullToRefreshListView.setOnItemClickListener(this);
				mPullToRefreshListView
						.setOnRefreshListener(new OnRefreshListener<ListView>() {

							// listview 끌어서 새로고침
							@Override
							public void onRefresh(
									PullToRefreshBase<ListView> refreshView) {
								String label = DateUtils.formatDateTime(
										mHostActivity, System.currentTimeMillis(),
										DateUtils.FORMAT_SHOW_TIME
												| DateUtils.FORMAT_SHOW_DATE
												| DateUtils.FORMAT_ABBREV_ALL);

								// Update the LastUpdatedLabel
								refreshView.getLoadingLayoutProxy()
										.setLastUpdatedLabel(label);

								fetchHotelList(
										mHostActivity.actionBar.getSelectedNavigationIndex());

							}
						});

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				Toast.makeText(mHostActivity,
						"네트워크 상태를 확인해주세요", Toast.LENGTH_LONG).show();
			} finally {
				LoadingDialog.hideLoading();
				mPullToRefreshListView.onRefreshComplete();
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

				mHostActivity.setActionBar("");
				mHostActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

				ArrayAdapter<String> regionListAdapter = new ArrayAdapter<String>(
						mHostActivity, android.R.layout.simple_list_item_1,
						android.R.id.text1, mRegionList);

				mHostActivity.actionBar.setListNavigationCallbacks(regionListAdapter, this);
				mHostActivity.actionBar.setSelectedNavigationItem(mHostActivity
						.sharedPreference.getInt(KEY_PREFERENCE_REGION_INDEX, 0));

			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();

				LoadingDialog.hideLoading();
				mHostActivity.addFragment(new ErrorFragment());
			}
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_APP_TIME)) {
			Log.d(TAG, response.toString());
			mDailyHotelSaleTime.setCurrentTime(response
					.toString());
			
			// 오픈, 클로즈 타임을 가져온다
			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME)
					.toString(), null, HotelListFragment.this,
					HotelListFragment.this));
			
		} 
	}
	
	// private void customizeFastScrollerOfListView(ListView listView) {
		//
		// try {
		//
		// Class<?> clazz = Class.forName("android.widget.FastScroller");
		// Constructor<?> constructor = clazz.getConstructor(
		// AbsListView.class);
		// Object newFastScroller = constructor.newInstance(listView);
		//
		// Field padding = clazz.getDeclaredField("mPreviewPadding");
		// padding.setAccessible(true);
		//
		// int oldPadding = padding.getInt(newFastScroller);
		// Log.d(TAG, Integer.toString(oldPadding));
		//
		// padding.set(newFastScroller, 24);
		// int newPadding = padding.getInt(newFastScroller);
		// Log.d(TAG, Integer.toString(newPadding));
		//
		// padding.setAccessible(false);
		//
		//
		// Field orgFastScroller =
		// AbsListView.class.getDeclaredField("mFastScroller");
		// orgFastScroller.setAccessible(true);
		// orgFastScroller.set(listView, newFastScroller);
		//
		// orgFastScroller.setAccessible(false);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
	
}
