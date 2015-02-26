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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.AbcDefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.adapter.RegionListAdapter;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
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
OnItemClickListener, 
DailyHotelJsonArrayResponseListener, DailyHotelJsonResponseListener,
DailyHotelStringResponseListener, uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener, OnNavigationListener {

	private final static String TAG = "HotelListFragment";

	private PinnedSectionListView mHotelListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private HotelListAdapter mHotelListAdapter;
	private List<HotelListViewItem> mHotelListViewList;
	private List<Hotel> mHotelList;
	private List<String> mRegionList;
	private Map<String, List<String>> mRegionDetailList;
	private List<String> mJaRegionList;
	private Map<String, List<String>> mJaRegionDetailList;
	private SaleTime mDailyHotelSaleTime;
	private LinearLayout llListViewFooter;
	private ImageView ivNewEvent;
	private LinearLayout btnListViewHeader;

	private boolean mRefreshHotelList;

	private int mKakaoHotelIdx = -1;
	private String mKakaoHotelRegion;

	private String selectedRegion;
	private RegionListAdapter regionListAdapter;
	
	private LocationManager mLocationManager;
	private int beforeIdx = 0;
	
	private boolean event;
	private int seoulIdx = 0;
	private int tokyoIdx = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);
		
		Uri intentData = ((MainActivity)mHostActivity).intentData;
		if (intentData != null) {
			String hotelIdx = intentData.getQueryParameter("hotelIdx");
			
			if (hotelIdx != null) {
				mKakaoHotelIdx = Integer.parseInt(intentData.getQueryParameter("hotelIdx"));
				mKakaoHotelRegion = intentData.getQueryParameter("region");
				android.util.Log.e("KaKaoHotelIdx", mKakaoHotelIdx + " / " + mKakaoHotelRegion);
			}
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

		mHotelListView.setShadowVisible(false);
		setHasOptionsMenu(true);
		
		//ver_dual API의 new_event값이 0이면 false, 1이면 true
		event = mHostActivity.sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);
		
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();

		if (mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "").equals("")) {
			Intent i = new Intent(mHostActivity, RegionListActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
		} else {
			if (mRefreshHotelList) {
				lockUI();
				// 현재 서버 시간을 가져온다
				// 사용자 시간은 변경가능성 있음. 서버시간을 바탕으로 판매시간 체크 
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
						URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME)
						.toString(), null, HotelListFragment.this,
						mHostActivity));
			}
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
		
//		mHostActivity.selectMenuDrawer(mHostActivity.menuHotelListFragment);
	
		int selectedPosition = position - 1;
		HotelListViewItem selectedItem = mHotelListViewList.get(selectedPosition);
		int count = 0;
		for (int i = 0; i < selectedPosition; i++) {
			if (mHotelListViewList.get(i).getType() == HotelListViewItem.TYPE_SECTION) count++;
		}
		int hotelIdx = position - count;

		if (selectedItem.getType() == HotelListViewItem.TYPE_ENTRY) {
			//리스트 뷰로 하면 보이는거 빼고 나머지 지워지는거 메모리에서 사라짐
			//이미지 캐시 만들어서 캐싱
			mHotelListAdapter.getImgCache().evictAll(); // 호텔 리스트아이템들의 image를 캐싱하는 lru cache 비우기.

			Intent i = new Intent(mHostActivity, HotelTabActivity.class);
			
//			int idx = mHostActivity.actionBar.getSelectedNavigationIndex();
			selectedRegion = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
			
			SharedPreferences.Editor editor = mHostActivity.sharedPreference
					.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, selectedRegion);
			editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, selectedItem.getItem().getName());
			editor.commit();

			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, selectedItem.getItem());
			i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mDailyHotelSaleTime);
			i.putExtra(NAME_INTENT_EXTRA_DATA_REGION, selectedRegion);
			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIdx);

			startActivityForResult(i, CODE_REQUEST_ACTIVITY_HOTELTAB);
			
			RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectHotel", selectedItem.getItem().getName(), (long) hotelIdx);
		} else if (selectedItem.getType() == HotelListViewItem.TYPE_SECTION) {
			return;
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
			}
		} 

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		String content = mRegionList.get(position).trim();
        
        if (!content.equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""))) {
			SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, content);
			editor.commit();
		}
        Log.d("RegionListActivity", "before region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "") + " select region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));

		lockUI();
		fetchHotelList();
		RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectRegion", mRegionList.get(position).trim(), (long) (position+1));
		
//		boolean showEventPopUp = ((MainActivity) mHostActivity).sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);
		
//		if (showEventPopUp) {
//			Dialog popUpDialog = getEventPopUpDialog();
//			popUpDialog.show();
//		}
		
		return true;
	}
	
	
	
	private Dialog getEventPopUpDialog() {
		final Dialog dialog = new Dialog(((MainActivity) mHostActivity));

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);

		View view = LayoutInflater.from(((MainActivity) mHostActivity)).inflate(R.layout.fragment_pop_up_event, null);
		ImageView btnClose = (ImageView) view.findViewById(R.id.btn_confirm_payment_close);
		WebView popUpWebView = (WebView) view.findViewById(R.id.pop_up_web);
		
		popUpWebView.getSettings().setJavaScriptEnabled(true);
		popUpWebView.getSettings().setBuiltInZoomControls(true);
//		popUpWebView.getSettings().setBlockNetworkLoads(false);
        popUpWebView.loadUrl("http://www.google.com");
        popUpWebView.setWebViewClient(new WebViewClientClass()); 

		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setContentView(view);
		GlobalFont.apply((ViewGroup) view);

		return dialog;
	}
	
	private class WebViewClientClass extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url);
            return true; 
        } 
    }


	/**
	 * 호텔리스트를 보여준다.
	 * @param position
	 */
	private void fetchHotelList() {
		((MainActivity) mHostActivity).drawerLayout.closeDrawer(((MainActivity) mHostActivity).drawerList);

		String selectedRegion = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
		android.util.Log.e("selectedRegion",selectedRegion +" fetchHotelList");
		
		if (event)	mHostActivity.setActionBar("  " + selectedRegion);
//		String selectedRegionTr = mRegionList.get(position).trim();
		
//		if (!selectedRegion.equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "서울"))) {
//			SharedPreferences.Editor editor = mHostActivity.sharedPreference
//					.edit();
//			editor.putString(KEY_PREFERENCE_REGION_SELECT, selectedRegion);
//			editor.putInt(KEY_PREFERENCE_REGION_INDEX, position);
//			editor.commit();
//		}

		selectedRegion = selectedRegion.replace(" ", "%20");
		selectedRegion = selectedRegion.replace("|", "%7C");

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
		.append(URL_WEBAPI_HOTEL).append(selectedRegion)
		.append("/near/0/0/0/1000/")
		.append(mDailyHotelSaleTime.getCurrentYear()).append("/")
		.append(mDailyHotelSaleTime.getCurrentMonth()).append("/")
		.append(mDailyHotelSaleTime.getCurrentDay()).toString();

		android.util.Log.e("Current Time is",
				mDailyHotelSaleTime.getCurrentYear().toString()+" "
				+ mDailyHotelSaleTime.getCurrentMonth().toString()+" "
				+ mDailyHotelSaleTime.getCurrentDay().toString()+" "
				+ mDailyHotelSaleTime.getCurrentHour().toString()+" "
				+ mDailyHotelSaleTime.getCurrentMin().toString()+" "
				+ mDailyHotelSaleTime.getCurrentSec().toString()+" ");
		Log.d(TAG, url);

		// 호텔 리스트를 가져온다. 
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null,
				HotelListFragment.this, mHostActivity));
		
		RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordScreen("hotelList", "/todays-hotels/" + selectedRegion);

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
				Log.d(TAG, "region? " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
				List<String> selectedRegionDetail = mRegionDetailList.get(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));

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
				HotelListViewItem others = new HotelListViewItem(getString(R.string.frag_hotel_list_others));

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
//				if (response.getString("new_event").equals("1") && (ivNewEvent != null))  ivNewEvent.setVisibility(View.VISIBLE);
				
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
		if (url.contains(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST)) {
			try {
				Log.d(TAG, "site/get ? " + response.toString());

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					name = obj.getString("name");
					mRegionList.add(name);
					if (name.equals(getString(R.string.frag_hotel_list_tokyo))) tokyoIdx = i;

					// 세부지역 추가
					List<String> nameDetailList = new ArrayList<String>();
					JSONArray arrDetail = obj.getJSONArray("child");
					for (int j=0; j<arrDetail.length(); j++) {
						String nameDetail = arrDetail.getString(j);
						nameDetailList.add(nameDetail);

					}
					mRegionDetailList.put(name.trim(), nameDetailList);
				}
				
				android.util.Log.e("mJaRegionList", mRegionList.toString());
				android.util.Log.e("mJaRegionDetailList", mRegionDetailList.toString());
		        
				/**
				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
				 */
				int regionIdx = 0;
				boolean isRegion = false;
				boolean isBeforeRegion = false;
				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty()) {
					for (int i=0;i<mRegionList.size();i++) {
						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion)) {
							regionIdx = i;
							isRegion = true;
							break;
						}
					}
					if (regionIdx == 0) {
						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
					}
				} else {
					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
					
					for (int i=0;i<mRegionList.size();i++) {
						if (mRegionList.get(i).trim().equals(regionStr)) {
							regionIdx = i;
							isRegion = true;
							break;
						}
						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, ""))) {
							beforeIdx = i;
							isBeforeRegion = true;
						}
					}
				}
				if (isRegion == false) {
//					String country = getCountryName();
//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
//					else if (country.equals("일본")) 
					
					regionIdx = beforeIdx;
					
					if (!isBeforeRegion) {
						String country = getCountryByLocale();
						if (country.equals(getString(R.string.act_list_region_korea))) regionIdx = seoulIdx;
						else if (country.equals(getString(R.string.act_list_region_japan))) regionIdx = tokyoIdx;
					}
					
					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx)); 
					editor.commit();
					
				}
				
				fetchHotelList();
			} catch (Exception e) {
				onError(e);
			}
		}
		else if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
			try {
				mRegionList = new ArrayList<String>();
				mRegionDetailList = new LinkedHashMap<String, List<String>>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					StringBuilder nameWithWhiteSpace = new StringBuilder(name);
					if (!event)	name = nameWithWhiteSpace.append("    ").append(obj.getString("name")).append("    ").toString();
					else name = obj.getString("name");
					mRegionList.add(name);
					if (name.trim().equals(getString(R.string.frag_hotel_list_seoul))) seoulIdx = i;

					// 세부지역 추가
					List<String> nameDetailList = new ArrayList<String>();
					JSONArray arrDetail = obj.getJSONArray("child");
					for (int j=0; j<arrDetail.length(); j++) {
						String nameDetail = arrDetail.getString(j);
						nameDetailList.add(nameDetail);

					}
					mRegionDetailList.put(name.trim(), nameDetailList);
				}
				
				android.util.Log.e("mRegionList", mRegionList.toString());
				android.util.Log.e("mRegionDetailList", mRegionDetailList.toString());

				if (!event) {
					mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
					// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
					mHostActivity.actionBar
					.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					regionListAdapter = new RegionListAdapter(
							mHostActivity, mRegionList);
					regionListAdapter.setNotifyOnChange(true);

					mHostActivity.actionBar.setListNavigationCallbacks(
							regionListAdapter, this);
				}
				
				/**
				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
				 */
				int regionIdx = 0;
				boolean isRegion = false;
				boolean isBeforeRegion = false;
				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty()) {
					for (int i=0;i<mRegionList.size();i++) {
						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion)) {
							regionIdx = i;
							isRegion = true;
							break;
						}
					}
					if (regionIdx == 0) {

						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);

					}
				} else {
					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
					
					for (int i=0;i<mRegionList.size();i++) {
						if (mRegionList.get(i).trim().equals(regionStr)) {
							regionIdx = i;
							isRegion = true;
							break;
						}
						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, ""))) {
							beforeIdx = i;
							isBeforeRegion = true;
						}
					}
//					regionIdx = mHostActivity.sharedPreference
//							.getInt(KEY_PREFERENCE_REGION_INDEX, 0);
				}
				if (isRegion == false) {
//					String country = getCountryName();
//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
//					else if (country.equals("일본")) 
					
					if (!event) {
						regionIdx = beforeIdx;
						
						if (!isBeforeRegion) {
							String country = getCountryByLocale();
							if (country.equals(getString(R.string.act_list_region_korea))) regionIdx = seoulIdx;
						}
						
						SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
						editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx)); 
						editor.commit();	
					}
					else {
						mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
								new StringBuilder(URL_DAILYHOTEL_SERVER).append(
										URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(),
										null, HotelListFragment.this,
										mHostActivity));
					}
					
				} else {
					fetchHotelList();
				}
				
//				if (regionIdx >= mRegionList.size()-1)	mHostActivity.actionBar.setSelectedNavigationItem(0);
				if (!event)	mHostActivity.actionBar.setSelectedNavigationItem(regionIdx);
				
//				fetchHotelList();
//				.setSelectedNavigationItem(1);
				
				// 호텔 리프레시
				//				fetchHotelList(mHostActivity.actionBar.getSelectedNavigationIndex());
				//				mHotelListView.setSelectionFromTop(prevIndex, prevTop);
			} catch (Exception e) {
				onError(e);
			}
		}
	}
	
	private String getCountryName() {
		mLocationManager = (LocationManager) mHostActivity.getSystemService(Context.LOCATION_SERVICE);
		Location location = getLastKnownLocation();
		String countryName = "";
		
//		Log.d(TAG, location.getLatitude() + " / " + location.getLongitude()); //35.6894875, 139.69170639999993 -> µµƒÏ
		if (location == null) {
			countryName = getCountryByLocale();
			
		} else {
			try {
				Geocoder mGeocoder = new Geocoder(mHostActivity, Locale.KOREAN);
	        	List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
	            if (addresses.size() > 0) {
	        		countryName = addresses.get(0).getCountryName();
	        		Log.d(TAG, "Country? " + countryName);
	        		
	                addresses.clear();
	                addresses = null; 
	            }
	        } catch ( IOException e ) {
	            e.printStackTrace();
	        }
		}
		return countryName;
	}
	
	private String getCountryByLocale() {
		Locale locale = this.getResources().getConfiguration().locale;
		String code = locale.getLanguage();
		String country = "";
		
		if (code.equals("ko")) {
			country = getString(R.string.act_list_region_korea);
		} else if (code.equals("ja")) {
			country = getString(R.string.act_list_region_japan);
		} else {	
			country = getString(R.string.act_list_region_korea);
		}
		return country;
	}
	
	private Location getLastKnownLocation() {
	    mLocationManager = (LocationManager)mHostActivity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    List<String> providers = mLocationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = mLocationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            // Found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    return bestLocation;
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
		fetchHotelList();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (event) {
			mHostActivity.getMenuInflater().inflate(R.menu.select_region_actions, menu);
		}
		
//		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_select_region:
			Intent i = new Intent(mHostActivity, RegionListActivity.class);
			startActivity(i);
			mHostActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
			return true;
		}
		return false;
	}

}
