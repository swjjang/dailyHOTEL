/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelListFragment (호텔 목록 화면)
 * 
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.AbcDefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.adapter.RegionListAdapter;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

public class HotelListFragment extends BaseFragment implements Constants,
OnItemClickListener, OnNavigationListener,
DailyHotelJsonArrayResponseListener, DailyHotelJsonResponseListener,
DailyHotelStringResponseListener, uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener {

	private final static String TAG = "HotelListFragment";

	private PinnedSectionListView mHotelListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private HotelListAdapter mHotelListAdapter;
	private List<HotelListViewItem> mHotelListViewList;
	private List<Hotel> mHotelList;
	private List<String> mRegionList;
	private Map<String, List<String>> mRegionDetailList;
	private SaleTime mDailyHotelSaleTime;
	private LinearLayout llListViewFooter;
	private ImageView ivNewEvent;
	private LinearLayout btnListViewHeader;

	private boolean mRefreshHotelList;

	private int mKakaoHotelIdx = -1;
	private String mKakaoHotelRegion;

	private String selectedRegion;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);
		
		/**
		 * TODO : 카카오링크로 들어온 경우 해당 앱으로 이동하도록
		 */
		
		Uri intentData = ((MainActivity)mHostActivity).intentData;
		if (intentData != null) {
			mKakaoHotelIdx = Integer.parseInt(intentData.getQueryParameter("hotelIdx"));
			mKakaoHotelRegion = intentData.getQueryParameter("region");
			android.util.Log.e("KaKaoHotelIdx", mKakaoHotelIdx + " / " + mKakaoHotelRegion);
		}

		mDailyHotelSaleTime = new SaleTime();
		mRefreshHotelList = true;

		mHotelListView = (PinnedSectionListView) view.findViewById(R.id.listview_hotel_list);
		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

		mHostActivity.setActionBar(R.string.actionbar_title_hotel_list_frag);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			View listViewFooter = inflater.inflate(R.layout.footer_hotel_list, null);

			llListViewFooter = (LinearLayout) listViewFooter.findViewById(R.id.ll_hotel_list_footer);
			llListViewFooter.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
					((MainActivity) mHostActivity).config.getNavigationBarHeight()));

			mHotelListView.addFooterView(listViewFooter);
		}

		View listViewHeader = inflater.inflate(R.layout.header_hotel_list, null);
		mHotelListView.addHeaderView(listViewHeader);

		ivNewEvent = (ImageView) view.findViewById(R.id.iv_new_event);
		btnListViewHeader = (LinearLayout) view.findViewById(R.id.btn_footer); // 수정요망 footer -> header
		GlobalFont.apply(btnListViewHeader);
		btnListViewHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mHostActivity, EventWebActivity.class);
				mHostActivity.startActivity(i);
				mHostActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
			}
		});

		// Now find the PullToRefreshLayout and set it up
		ActionBarPullToRefresh.from(mHostActivity)
		.options(Options.create()
				.scrollDistance(.3f)
				.headerTransformer(new AbcDefaultHeaderTransformer())
				.build())
				.allChildrenArePullable()
				.listener(this)
				// Here we'll set a custom ViewDelegate
				.useViewDelegate(AbsListView.class, new AbsListViewDelegate())
				.setup(mPullToRefreshLayout);

		//		ViewTreeObserver vto = mSwipeRefreshLayout.getViewTreeObserver();
		//		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		//			
		//			@Override
		//			public void onGlobalLayout() {
		//				final DisplayMetrics metrics = getResources().getDisplayMetrics();
		//				Float mDistanceToTriggerSync = Math.min(((View) mSwipeRefreshLayout.getParent()).getHeight() * 0.7f,
		//						150 * metrics.density);
		//				
		//				try {
		//					
		//					Field field = SwipeRefreshLayout.class.getDeclaredField("mDistanceToTriggerSync");
		//					field.setAccessible(true);
		//					field.setFloat(mSwipeRefreshLayout, mDistanceToTriggerSync);
		//					
		//				} catch (Exception e) {
		//					if (DEBUG)
		//						e.printStackTrace();
		//				}
		//				
		//				ViewTreeObserver obs = mSwipeRefreshLayout.getViewTreeObserver();
		//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		//					obs.removeOnGlobalLayoutListener(this);
		//					
		//				} else
		//					obs.removeGlobalOnLayoutListener(this);
		//				
		//			}
		//		});

		mHotelListView.setShadowVisible(false);

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
			lockUI();
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
		super.onPause();

	}

	// 호텔 클릭시
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView,
			int position, long id) {
		// 7.2 G2 버전에서 호텔리스트에서 이벤트 칸을 클릭할 경우 튕기는 현상을 막기 위함. why? 헤더뷰인데도 아이템 클릭 리스너가 들어감.
		if (position == 0)  return; 

		int selectedPosition = position - 1;
		HotelListViewItem selectedItem = mHotelListViewList.get(selectedPosition);

		if (selectedItem.getType() == HotelListViewItem.TYPE_ENTRY) {
			mHotelListAdapter.getImgCache().evictAll(); // 호텔 리스트아이템들의 image를 캐싱하는 lru cache 비우기.

			Intent i = new Intent(mHostActivity, HotelTabActivity.class);
			
			int idx = mHostActivity.actionBar.getSelectedNavigationIndex();
			selectedRegion = mRegionList.get(idx).trim();

			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, selectedItem.getItem());
			i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mDailyHotelSaleTime);
			i.putExtra(NAME_INTENT_EXTRA_DATA_REGION, selectedRegion);

			startActivityForResult(i, CODE_REQUEST_ACTIVITY_HOTELTAB);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_REQUEST_ACTIVITY_HOTELTAB) {
			mRefreshHotelList = false;
			if (resultCode == Activity.RESULT_OK) {
				((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
			} else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY) {
				((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
			} else {
				//				
				//				lockUI();
				//				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
				//						URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME)
				//						.toString(), null, HotelListFragment.this,
				//						mHostActivity));
			}

		} 

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		lockUI();
		fetchHotelList(position);

		return true;
	}

	/**
	 * 호텔리스트를 보여준다.
	 * @param position
	 */
	private void fetchHotelList(int position) {
		android.util.Log.e("FETCHHOTEL LIST",position +"");
		((MainActivity) mHostActivity).drawerLayout.closeDrawer(((MainActivity) mHostActivity).drawerList);

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
				mDailyHotelSaleTime.setCloseTime(close);

				if (!mDailyHotelSaleTime.isSaleTime()) {
					((MainActivity) mHostActivity).replaceFragment(WaitTimerFragment.newInstance(
							mDailyHotelSaleTime));
					unLockUI();
				} else {
					// 지역 리스트를 가져온다
					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_SITE_LOCATION_LIST).toString(),
									null, HotelListFragment.this,
									mHostActivity));
				}

			} catch (Exception e) {
				onError(e);
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
					String detailRegion = obj.getString("site2_name");
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
					newHotel.setDetailRegion(detailRegion);

					if (seq >= 0) { // 숨김호텔이 아니라면 추가. (음수일 경우 숨김호텔.)
						// SOLD OUT 된 항목은 밑으로.
						if (available <= 0) available *= 100;

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

				mHotelListViewList = new ArrayList<HotelListViewItem>();
				List<String> selectedRegionDetail = mRegionDetailList.get(mRegionList.get(mHostActivity.actionBar
						.getSelectedNavigationIndex()));

				for (int i = 0; i < selectedRegionDetail.size(); i++) {
					String region = selectedRegionDetail.get(i);
					HotelListViewItem section = new HotelListViewItem(region);
					mHotelListViewList.add(section);

					int count = 0;
					for (int j = 0; j < mHotelList.size(); j++) {
						Hotel hotel = mHotelList.get(j);
						if (hotel.getDetailRegion().equals(region)) {
							mHotelListViewList.add(new HotelListViewItem(hotel));
							count++;
						}
					}

					if (count == 0) mHotelListViewList.remove(section);
				}

				int count = 0;
				HotelListViewItem others = new HotelListViewItem("기타");

				mHotelListViewList.add(others);
				for (int i = 0; i < mHotelList.size(); i++) {
					Hotel hotel = mHotelList.get(i);
					if (hotel.getDetailRegion().equals("null")) {
						mHotelListViewList.add(new HotelListViewItem(hotel));
						count++;
					}
				}

				if (count == 0) mHotelListViewList.remove(others);

				mHotelListAdapter = new HotelListAdapter(mHostActivity,
						R.layout.list_row_hotel, mHotelListViewList);
				mHotelListView.setAdapter(mHotelListAdapter);
				mHotelListView.setOnItemClickListener(this);

				mRefreshHotelList = true;

				// 새로운 이벤트 확인을 위해 버전 API 호출
				mQueue.add(new DailyHotelJsonRequest(Method.GET, 
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(),
						null, this, mHostActivity));

				unLockUI();

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();
				
			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {
			try {
				if (response.getString("new_event").equals("1") && (ivNewEvent != null))  ivNewEvent.setVisibility(View.VISIBLE);
				if (mKakaoHotelIdx != -1) {
					for (int i=0; i<mHotelListAdapter.getCount(); i++) {
						HotelListViewItem item = mHotelListAdapter.getItem(i);
						if (item.getType() == HotelListViewItem.TYPE_SECTION) {
							continue;
						} else {
							if(item.getItem().getIdx() == mKakaoHotelIdx) {
								mHotelListView.performItemClick(null, i+1, -1);
								break;
							}	
						}
					}

					mKakaoHotelRegion = null;
					mKakaoHotelIdx = -1;
				}
			} catch (Exception e) {
				onError(e);
			}
		}

	}

	@Override
	public void onResponse(String url, JSONArray response) {
		if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
			try {
				mRegionList = new ArrayList<String>();
				mRegionDetailList = new LinkedHashMap<String, List<String>>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					StringBuilder nameWithWhiteSpace = new StringBuilder(name);
					name = nameWithWhiteSpace.append("    ").append(obj.getString("name")).
							append("     ").toString();
					mRegionList.add(name);

					// 세부지역 추가
					List<String> nameDetailList = new ArrayList<String>();
					JSONArray arrDetail = obj.getJSONArray("child");
					for (int j=0; j<arrDetail.length(); j++) {
						String nameDetail = arrDetail.getString(j);
						nameDetailList.add(nameDetail);

					}
					mRegionDetailList.put(name, nameDetailList);
				}
				
				android.util.Log.e("mRegionList", mRegionList.toString());
				android.util.Log.e("mRegionDetailList", mRegionDetailList.toString());

				mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
				// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
				mHostActivity.actionBar
				.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				RegionListAdapter regionListAdapter = new RegionListAdapter(
						mHostActivity, mRegionList);

				mHostActivity.actionBar.setListNavigationCallbacks(
						regionListAdapter, this);
				
				int regionIdx = 0;
				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty()) {
					for (int i=0;i<mRegionList.size();i++) {
						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion)) {
							regionIdx = i;
							break;
						}
					}
				} else {
					regionIdx = mHostActivity.sharedPreference
							.getInt(KEY_PREFERENCE_REGION_INDEX, 0);
				}
				
				mHostActivity.actionBar
				.setSelectedNavigationItem(regionIdx);
//				.setSelectedNavigationItem(1);
				
				// 호텔 리프레시
				//				fetchHotelList(mHostActivity.actionBar.getSelectedNavigationIndex());
				//				mHotelListView.setSelectionFromTop(prevIndex, prevTop);
			} catch (Exception e) {
				onError(e);
			}
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_APP_TIME)) {
			mDailyHotelSaleTime.setCurrentTime(response);

			// 오픈, 클로즈 타임을 가져온다
			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME)
					.toString(), null, HotelListFragment.this,
					mHostActivity));

		}
	}


	@Override
	public void onRefreshStarted(View view) {
		fetchHotelList(mHostActivity.actionBar.getSelectedNavigationIndex());
	}

}
