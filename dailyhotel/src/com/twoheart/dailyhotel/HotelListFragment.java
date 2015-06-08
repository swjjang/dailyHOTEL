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
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.adapter.HotelListAdapter;
import com.twoheart.dailyhotel.fragment.HotelListMapFragment;
import com.twoheart.dailyhotel.fragment.HotelMainFragment;
import com.twoheart.dailyhotel.fragment.HotelMainFragment.HOTEL_VIEW_TYPE;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.DailyFloatingActionButton;
import com.twoheart.dailyhotel.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

public class HotelListFragment extends BaseFragment implements Constants, OnItemClickListener, OnRefreshListener
{
	private PinnedSectionListView mHotelListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private HotelListAdapter mHotelListAdapter;

	protected SaleTime mSaleTime;
	private Map<String, List<String>> mDetailRegionList;

	//	private boolean event;
	protected boolean mIsSelectionTop;
	private View mEmptyView;
	//	private View mFooterView; // FooterView

	private FrameLayout mMapLayout;
	private HotelListMapFragment mHotelListMapFragment;
	private HOTEL_VIEW_TYPE mHotelViewType;
	private String mSelectedRegion;
	//	private String mSelectedDetailRegion;

	private DailyFloatingActionButton mDailyFloatingActionButton;

	private HotelListViewItem mSelectedHotelListViewItem;
	private int mSelectedHotelIndex;

	protected HotelMainFragment.UserActionListener mUserActionListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);

		mHotelListView = (PinnedSectionListView) view.findViewById(R.id.listview_hotel_list);

		// 이벤트를 마지막에 넣는다.
		// FooterView
		//		mFooterView = inflater.inflate(R.layout.list_row_hotel_event, null, true);
		//		mHotelListView.addFooterView(mFooterView);

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		mEmptyView = view.findViewById(R.id.emptyView);
		mMapLayout = (FrameLayout) view.findViewById(R.id.hotelMapLayout);

		//		mHotelListMapFragment = (HotelListMapFragment) getChildFragmentManager().findFragmentById(R.id.hotelMapFragment);

		mDailyFloatingActionButton = (DailyFloatingActionButton) view.findViewById(R.id.floatingActionButton);
		mDailyFloatingActionButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});

		mDailyFloatingActionButton.setVisibility(View.GONE);

		mHotelViewType = HOTEL_VIEW_TYPE.LIST;

		setVisibility(HOTEL_VIEW_TYPE.LIST);

		// pinkred_font
		//		GlobalFont.apply(container);

		// 추후 왼쪽 탭로 빠질것이다.
		//		btnListViewHeader.setOnClickListener(new OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View v)
		//			{
		//				Intent i = new Intent(mHostActivity, EventWebActivity.class);
		//				mHostActivity.startActivity(i);
		//				mHostActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);
		//			}
		//		});

		// Now find the PullToRefreshLayout and set it up
		ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this)
		// Here we'll set a custom ViewDelegate
		.useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

		mHotelListView.setShadowVisible(false);

		// ver_dual API의 new_event값이 0이면 false, 1이면 true
		// false인 경우 기존의 호텔리스트 방식으로 
		// true인 경우 새로 만든 호텔리스트 화면방식으로 전환됨.
		//		event = mHostActivity.sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mSelectedHotelListViewItem = mHotelListAdapter.getItem(position);

		int count = 0;
		for (int i = 0; i < position; i++)
		{
			if (mHotelListAdapter.getItem(i).getType() == HotelListViewItem.TYPE_SECTION)
			{
				count++;
			}
		}

		mSelectedHotelIndex = position - count;

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_TIME).toString(), null, mAppTimeJsonResponseListener, baseActivity));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mHotelListMapFragment != null)
		{
			mHotelListMapFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 토글이 아닌 경우에만 진행하는 프로세스.
	 * 
	 * @param detailRegion
	 */
	public void processSelectedDetailRegion(String detailRegion)
	{
		// 현재 맵화면을 보고 있으면 맵화면을 유지 시켜중어야 한다.
		if (detailRegion != null && mHotelViewType == HOTEL_VIEW_TYPE.MAP)
		{
			refreshHotelList(true);
		}
	}

	public void onPageSelected(boolean isRequestHotelList)
	{
	}

	public void onPageUnSelected()
	{
	}

	public void onRefreshComplete()
	{
		//		mDailyFloatingActionButton.attachToListView(mHotelListView);
	}

	/**
	 * 새로 고침을 하지 않고 기존의 있는 데이터를 보여준다.
	 * 
	 * @param type
	 * @param isCurrentPage
	 */
	public void setHotelViewType(HOTEL_VIEW_TYPE type, boolean isCurrentPage)
	{
		mHotelViewType = type;

		if (mEmptyView.getVisibility() == View.VISIBLE)
		{
			setVisibility(HOTEL_VIEW_TYPE.GONE);
		} else
		{
			switch (mHotelViewType)
			{
				case LIST:
					setVisibility(HOTEL_VIEW_TYPE.LIST, isCurrentPage);
					break;

				case MAP:
					setVisibility(HOTEL_VIEW_TYPE.MAP, isCurrentPage);

					if (mHotelListMapFragment != null)
					{
						mHotelListMapFragment.setUserActionListener(mUserActionListener);

						if (isCurrentPage == true && mHotelListAdapter != null)
						{
							mHotelListMapFragment.setHotelList(mHotelListAdapter.getData(), mSaleTime, false);
						}
					}
					break;

				case GONE:
					break;
			}
		}
	}

	private void setVisibility(HOTEL_VIEW_TYPE type, boolean isCurrentPage)
	{
		switch (type)
		{
			case LIST:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.GONE);

				if (mHotelListMapFragment != null)
				{
					getChildFragmentManager().beginTransaction().remove(mHotelListMapFragment).commitAllowingStateLoss();
					mMapLayout.removeAllViews();
					mHotelListMapFragment = null;
				}

				//				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
				//				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_map_mini);

				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				break;

			case MAP:
				mEmptyView.setVisibility(View.GONE);
				mMapLayout.setVisibility(View.VISIBLE);

				if (isCurrentPage == true)
				{
					if (mHotelListMapFragment == null)
					{
						mHotelListMapFragment = new HotelListMapFragment();
						getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mHotelListMapFragment).commitAllowingStateLoss();
					}
				}

				//				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
				//				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_list_mini);
				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;

			case GONE:
				mEmptyView.setVisibility(View.VISIBLE);
				mMapLayout.setVisibility(View.GONE);

				mDailyFloatingActionButton.setVisibility(View.GONE);
				mPullToRefreshLayout.setVisibility(View.INVISIBLE);
				break;
		}
	}

	private void setVisibility(HOTEL_VIEW_TYPE type)
	{
		setVisibility(type, true);
	}

	public void setSaleTime(SaleTime saleTime)
	{
		mSaleTime = saleTime;
	}

	public SaleTime getSaleTime()
	{
		return mSaleTime;
	}

	public void setRegionList(Map<String, List<String>> regionDetailList)
	{
		mDetailRegionList = regionDetailList;
	}

	public void setUserActionListener(HotelMainFragment.UserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void setFloatingActionButtonVisible(boolean visible)
	{
		if (mDailyFloatingActionButton == null)
		{
			return;
		}

		// 일단 눈에 안보이도록 함.
		mDailyFloatingActionButton.hide(false, true);
		//
		//		if (visible == true)
		//		{
		//			if (mHotelListAdapter != null && mHotelListAdapter.getCount() != 0)
		//			{
		//				mDailyFloatingActionButton.show(false, true);
		//			}
		//		} else
		//		{
		//			mDailyFloatingActionButton.hide(false, true);
		//		}
	}

	public void refreshHotelList(boolean isSelectionTop)
	{
		mIsSelectionTop = isSelectionTop;

		fetchHotelList(mSaleTime);
	}

	/**
	 * 호텔리스트를 보여준다.
	 * 
	 * @param position
	 */
	private void fetchHotelList(SaleTime saleTime)
	{
		if (saleTime == null)
		{
			ExLog.e("saleTime == null");
			return;
		}

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		lockUI();

		mSelectedRegion = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
		ExLog.e("selectedRegion : " + mSelectedRegion + " fetchHotelList");

		mSelectedRegion = mSelectedRegion.replace(" ", "%20");
		mSelectedRegion = mSelectedRegion.replace("|", "%7C");

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SALE).append('/').append(mSelectedRegion).append("/").append(saleTime.getRequestHotelDateFormat("yyMMdd")).toString();

		// 호텔 리스트를 가져온다. 
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, mHotelJsonResponseListener, baseActivity));

		RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordScreen("hotelList", "/todays-hotels/" + mSelectedRegion);
	}

	public String getRegion()
	{
		return mSelectedRegion;
	}

	@Override
	public void onRefreshStarted(View view)
	{
		refreshHotelList(true);
	}

	// 현재 위치를 바탕으로 국가이름을 얻어옴
	// 선택한 지역이 없는 경우 현재 위치를 바탕으로 국가를 얻어오기 위해 사용(현재는 사용 안함)
	// 현재는 선택한 지역이 없는 경우 현재 위치를 바탕으로 얻는 대신
	// 일종의 캐시 개념으로 그 전에 선택했던 지역을 불러오는 방식을 사용함.
	//	private String getCountryName()
	//	{
	//		mLocationManager = (LocationManager) mHostActivity.getSystemService(Context.LOCATION_SERVICE);
	//		Location location = getLastKnownLocation();
	//		String countryName = "";
	//
	//		if (location == null)
	//		{
	//			countryName = getCountryByLocale();
	//
	//		} else
	//		{
	//			try
	//			{
	//				Geocoder mGeocoder = new Geocoder(mHostActivity, Locale.KOREAN);
	//				List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
	//				if (addresses.size() > 0)
	//				{
	//					countryName = addresses.get(0).getCountryName();
	//
	//					addresses.clear();
	//					addresses = null;
	//				}
	//			} catch (IOException e)
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//		return countryName;
	//	}

	// 사용자의 언어를 바탕으로 국가이름을 얻어옴.
	//	private String getCountryByLocale()
	//	{
	//		Locale locale = this.getResources().getConfiguration().locale;
	//		String code = locale.getLanguage();
	//		String country = "";
	//
	//		if (code.equals("ko"))
	//		{
	//			country = getString(R.string.act_list_region_korea);
	//		} else if (code.equals("ja"))
	//		{
	//			country = getString(R.string.act_list_region_japan);
	//		} else
	//		{
	//			country = getString(R.string.act_list_region_korea);
	//		}
	//		return country;
	//	}

	//	private Location getLastKnownLocation()
	//	{
	//		mLocationManager = (LocationManager) mHostActivity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	//		List<String> providers = mLocationManager.getProviders(true);
	//		Location bestLocation = null;
	//		for (String provider : providers)
	//		{
	//			Location l = mLocationManager.getLastKnownLocation(provider);
	//			if (l == null)
	//			{
	//				continue;
	//			}
	//			if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
	//			{
	//				// Found best last known location: %s", l);
	//				bestLocation = l;
	//			}
	//		}
	//		return bestLocation;
	//	}

	//	//이벤트 공지를 위한 dialog를 띄움.
	//	private Dialog getEventPopUpDialog()
	//	{
	//		final Dialog dialog = new Dialog(((MainActivity) mHostActivity));
	//
	//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	//		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	//		dialog.setCanceledOnTouchOutside(false);
	//
	//		View view = LayoutInflater.from(((MainActivity) mHostActivity)).inflate(R.layout.fragment_pop_up_event, null);
	//		ImageView btnClose = (ImageView) view.findViewById(R.id.btn_confirm_payment_close);
	//		WebView popUpWebView = (WebView) view.findViewById(R.id.pop_up_web);
	//
	//		popUpWebView.getSettings().setBuiltInZoomControls(true);
	//		//		popUpWebView.getSettings().setBlockNetworkLoads(false);
	//		popUpWebView.loadUrl("http://www.google.com");
	//		popUpWebView.setWebViewClient(new WebViewClientClass());
	//
	//		btnClose.setOnClickListener(new OnClickListener()
	//		{
	//			@Override
	//			public void onClick(View v)
	//			{
	//				dialog.dismiss();
	//			}
	//		});
	//
	//		dialog.setContentView(view);
	//		GlobalFont.apply((ViewGroup) view);
	//
	//		return dialog;
	//	}

	//	private class WebViewClientClass extends WebViewClient
	//	{
	//		@Override
	//		public boolean shouldOverrideUrlLoading(WebView view, String url)
	//		{
	//			view.loadUrl(url);
	//			return true;
	//		}
	//	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Hotel List Listener
	 */
	private DailyHotelJsonResponseListener mHotelJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		/**
		 * 
		 * @param region
		 *            : in
		 * @param hotelList
		 *            : in & out
		 * @param hotelListViewList
		 *            : out
		 */
		private void makeRegionHotelList(final String region, ArrayList<Hotel> hotelList, ArrayList<HotelListViewItem> hotelListViewList)
		{
			// insert section
			HotelListViewItem section = new HotelListViewItem(region);
			hotelListViewList.add(section);

			int insertHotelSize = 0;
			int size = hotelList.size();
			Hotel hotel;

			for (int i = size - 1; i >= 0; i--)
			{
				hotel = hotelList.get(i);

				if (region.equalsIgnoreCase(hotel.getDetailRegion()) == true)
				{
					hotelListViewList.add(new HotelListViewItem(hotel));
					hotelList.remove(i);

					insertHotelSize++;
				}
			}

			// 해당 지역에 호텔 정보가 없으면 section정보를 제거한다.
			if (insertHotelSize == 0)
			{
				hotelListViewList.remove(section);
			}
		}

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (getActivity() == null)
				{
					return;
				}

				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				JSONArray hotelJSONArray = response.getJSONArray("hotels");

				int length = hotelJSONArray.length();

				if (length == 0)
				{
					if (mHotelListAdapter != null)
					{
						mHotelListAdapter.clear();
					}

					setVisibility(HOTEL_VIEW_TYPE.GONE);
				} else
				{
					JSONObject jsonObject;

					ArrayList<Hotel> hotelList = new ArrayList<Hotel>(length);

					for (int i = 0; i < length; i++)
					{
						jsonObject = hotelJSONArray.getJSONObject(i);

						int seq = jsonObject.getInt("seq");

						if (seq >= 0)
						{
							// 숨김호텔이 아니라면 추가. (음수일 경우 숨김호텔.)
							Hotel newHotel = new Hotel();

							if (newHotel.setHotel(jsonObject) == true)
							{
								hotelList.add(newHotel); // 추가.
							}
						}
					}

					// seq 값에 따른 역순으로 정렬
					Comparator<Hotel> comparator = new Comparator<Hotel>()
					{
						public int compare(Hotel o1, Hotel o2)
						{
							// 숫자정렬
							return o2.getSequence() - o1.getSequence();
						}
					};

					Collections.sort(hotelList, comparator);

					List<String> selectedDetailRegionList = mDetailRegionList.get(baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));

					int hotelListSize = selectedDetailRegionList.size() + hotelList.size();
					ArrayList<HotelListViewItem> hotelListViewList = new ArrayList<HotelListViewItem>(hotelListSize);

					if (hotelList.size() > 0)
					{
						// 지역별로 호텔 리스트를 넣어준다.
						for (String detailRegion : selectedDetailRegionList)
						{
							if (TextUtils.isEmpty(detailRegion) == true)
							{
								continue;
							}

							makeRegionHotelList(detailRegion, hotelList, hotelListViewList);
						}

						// 위의 makeRegionHotelList를 거치면 hotelList 사이즈가 변경된다.
						// 호텔 지역 정보가 없는 기타 호텔들...
						if (hotelList.size() > 0)
						{
							HotelListViewItem section = new HotelListViewItem(getString(R.string.frag_hotel_list_others));
							hotelListViewList.add(section);

							for (Hotel hotel : hotelList)
							{
								hotelListViewList.add(new HotelListViewItem(hotel));
							}
						}
					}

					if (mHotelListAdapter == null)
					{
						mHotelListAdapter = new HotelListAdapter(baseActivity, R.layout.list_row_hotel, new ArrayList<HotelListViewItem>());
						mHotelListView.setAdapter(mHotelListAdapter);
						mHotelListView.setOnItemClickListener(HotelListFragment.this);
					}

					setVisibility(mHotelViewType);

					// 지역이 변경되면 다시 리스트를 받아오는데 어떻게 해야할지 의문.
					if (mHotelViewType == HOTEL_VIEW_TYPE.MAP)
					{
						mHotelListMapFragment.setUserActionListener(mUserActionListener);
						mHotelListMapFragment.setHotelList(hotelListViewList, mSaleTime, mIsSelectionTop);
					}

					mHotelListAdapter.clear();
					mHotelListAdapter.addAll(hotelListViewList);
					mHotelListAdapter.notifyDataSetChanged();

					if (mIsSelectionTop == true)
					{
						mHotelListView.setSelection(0);
						// mDailyFloatingActionButton
						//						mDailyFloatingActionButton.detachToListView(mHotelListView);
					}
				}

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();

				// 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
				onRefreshComplete();
			} catch (JSONException e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mAppTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				long time = response.getLong("time");

				mSaleTime.setCurrentTime(time);

				// 오픈, 클로즈 타임을 가져온다
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME).toString(), null, mAppSaleTimeJsonResponseListener, baseActivity));

			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mAppSaleTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				String open = response.getString("open");
				String close = response.getString("close");

				mSaleTime.setOpenTime(open);
				mSaleTime.setCloseTime(close);

				if (mSaleTime.isSaleTime() == false)
				{
					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mSaleTime));
					unLockUI();
				} else
				{
					if (mUserActionListener != null)
					{
						mUserActionListener.selectHotel(mSelectedHotelListViewItem, mSelectedHotelIndex, mSaleTime);
					}
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	//	private DailyHotelJsonArrayResponseListener mSiteLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
	//	{
	//
	//		@Override
	//		public void onResponse(String url, JSONArray response)
	//		{
	//			try
	//			{
	//				if (response == null)
	//				{
	//					throw new NullPointerException("response == null");
	//				}
	//
	//				mRegionList = new ArrayList<String>();
	//				mRegionDetailList = new LinkedHashMap<String, List<String>>();
	//
	//				JSONArray arr = response;
	//				for (int i = 0; i < arr.length(); i++)
	//				{
	//					JSONObject obj = arr.getJSONObject(i);
	//					String name = new String();
	//					StringBuilder nameWithWhiteSpace = new StringBuilder(name);
	//
	//					// 네비게이션 리스트 방식을 사용할 경우 간격을 조절하기 위함. 
	//					if (event == false)
	//					{
	//						name = nameWithWhiteSpace.append("    ").append(obj.getString("name")).append("    ").toString();
	//					} else
	//					{
	//						name = obj.getString("name");
	//					}
	//
	//					mRegionList.add(name);
	//
	//					if (name.trim().equals(getString(R.string.frag_hotel_list_seoul)) == true)
	//					{
	//						seoulIdx = i;
	//					}
	//
	//					// 세부지역 추가
	//					JSONArray arrDetail = obj.getJSONArray("child");
	//					List<String> nameDetailList = new ArrayList<String>(arrDetail.length());
	//
	//					for (int j = 0; j < arrDetail.length(); j++)
	//					{
	//						String nameDetail = arrDetail.getString(j);
	//						nameDetailList.add(nameDetail);
	//					}
	//
	//					mRegionDetailList.put(name.trim(), nameDetailList);
	//				}
	//
	//				ExLog.e("mRegionList : " + mRegionList.toString());
	//				ExLog.e("mRegionDetailList : " + mRegionDetailList.toString());
	//
	//				//기존의 지역리스트 표시방식 
	//				if (event == false)
	//				{
	//					mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
	//					// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
	//					mHostActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	//					regionListAdapter = new RegionListAdapter(mHostActivity, mRegionList);
	//					regionListAdapter.setNotifyOnChange(true);
	//
	//					mHostActivity.actionBar.setListNavigationCallbacks(regionListAdapter, HotelListFragment.this);
	//				}
	//
	//				/**
	//				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
	//				 */
	//				int regionIdx = 0;
	//				boolean isRegion = false;
	//				boolean isBeforeRegion = false;
	//				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty())
	//				{
	//					for (int i = 0; i < mRegionList.size(); i++)
	//					{
	//						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion) == true)
	//						{
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//					}
	//
	//					if (regionIdx == 0)
	//					{
	//						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
	//					}
	//				} else
	//				{
	//					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
	//
	//					for (int i = 0; i < mRegionList.size(); i++)
	//					{
	//						if (mRegionList.get(i).trim().equals(regionStr) == true)
	//						{
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//
	//						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "")) == true)
	//						{
	//							beforeIdx = i;
	//							isBeforeRegion = true;
	//						}
	//					}
	//					//					regionIdx = mHostActivity.sharedPreference
	//					//							.getInt(KEY_PREFERENCE_REGION_INDEX, 0);
	//				}
	//
	//				//선택지역이 없는 경우 
	//				if (isRegion == false)
	//				{
	//					//					String country = getCountryName();
	//					//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
	//					//					else if (country.equals("일본")) 
	//
	//					if (event == false)
	//					{
	//						regionIdx = beforeIdx;
	//
	//						if (!isBeforeRegion)
	//						{
	//							String country = getCountryByLocale();
	//							if (country.equals(getString(R.string.act_list_region_korea)))
	//							{
	//								regionIdx = seoulIdx;
	//							}
	//						}
	//
	//						SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
	//						editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx));
	//						editor.commit();
	//					}
	//					//새로운 지역리스트 방식의 경우 해외지역리스트 API도 호출함. 
	//					else
	//					{
	//						mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(), null, mSiteCountryLocationListJsonArrayResponseListener, mHostActivity));
	//					}
	//				}
	//
	//				if (event == true)
	//				{
	//					fetchHotelList();
	//				} else
	//				{
	//					mHostActivity.actionBar.setSelectedNavigationItem(regionIdx);
	//				}
	//			} catch (Exception e)
	//			{
	//				onError(e);
	//			}
	//
	//		}
	//	};
	//
	//	private DailyHotelJsonArrayResponseListener mSiteCountryLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
	//	{
	//
	//		@Override
	//		public void onResponse(String url, JSONArray response)
	//		{
	//
	//			try
	//			{
	//				if (response == null)
	//				{
	//					throw new NullPointerException("response == null");
	//				}
	//
	//				for (int i = 0; i < response.length(); i++)
	//				{
	//					JSONObject obj = response.getJSONObject(i);
	//					String name = obj.getString("name");
	//
	//					mRegionList.add(name);
	//					if (name.equals(getString(R.string.frag_hotel_list_tokyo)) == true)
	//					{
	//						tokyoIdx = i;
	//					}
	//
	//					// 세부지역 추가
	//					JSONArray arrDetail = obj.getJSONArray("child");
	//					List<String> nameDetailList = new ArrayList<String>(arrDetail.length());
	//
	//					for (int j = 0; j < arrDetail.length(); j++)
	//					{
	//						String nameDetail = arrDetail.getString(j);
	//						nameDetailList.add(nameDetail);
	//					}
	//
	//					mRegionDetailList.put(name.trim(), nameDetailList);
	//				}
	//
	//				ExLog.e("mJaRegionList : " + mRegionList.toString());
	//				ExLog.e("mJaRegionDetailList : " + mRegionDetailList.toString());
	//
	//				/**
	//				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
	//				 */
	//				int regionIdx = 0;
	//				boolean isRegion = false;
	//				boolean isBeforeRegion = false;
	//
	//				if (mKakaoHotelRegion != null && mKakaoHotelRegion.isEmpty() == false)
	//				{
	//					for (int i = 0; i < mRegionList.size(); i++)
	//					{
	//						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion) == true)
	//						{
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//					}
	//
	//					if (regionIdx == 0)
	//					{
	//						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
	//					}
	//				} else
	//				{
	//					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
	//
	//					for (int i = 0; i < mRegionList.size(); i++)
	//					{
	//						if (mRegionList.get(i).trim().equals(regionStr) == true)
	//						{
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//
	//						//현재 선택지역 이전에 선택했던 지역이 있는지 파악 
	//						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "")) == true)
	//						{
	//							beforeIdx = i;
	//							isBeforeRegion = true;
	//						}
	//					}
	//				}
	//
	//				//선택지역이 없는 경우 
	//				if (isRegion == false)
	//				{
	//					//					String country = getCountryName();
	//					//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
	//					//					else if (country.equals("일본")) 
	//
	//					//이전에 선택했던 지역을 선택지역으로 설정함.
	//					regionIdx = beforeIdx;
	//
	//					//이전에 선택했던 지역도 없는 경우
	//					//사용자의 나라를 얻어와서 대한민국이면 서울, 일본이면 도쿄를 선택지역으로 설정함. 
	//					if (isBeforeRegion == false)
	//					{
	//						String country = getCountryByLocale();
	//
	//						if (country.equals(getString(R.string.act_list_region_korea)))
	//						{
	//							regionIdx = seoulIdx;
	//						} else if (country.equals(getString(R.string.act_list_region_japan)))
	//						{
	//							regionIdx = tokyoIdx;
	//						}
	//					}
	//
	//					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
	//					editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx));
	//					editor.commit();
	//				}
	//
	//				fetchHotelList();
	//			} catch (Exception e)
	//			{
	//				onError(e);
	//			}
	//		}
	//	};
	//
	//	private DailyHotelJsonResponseListener mAppVersionJsonResponseListener = new DailyHotelJsonResponseListener()
	//	{
	//
	//		@Override
	//		public void onResponse(String url, JSONObject response)
	//		{
	//
	//			try
	//			{
	//				if (response == null)
	//				{
	//					throw new NullPointerException("response == null");
	//				}
	//
	//				//				if (response.getString("new_event").equals("1") && (ivNewEvent != null))  ivNewEvent.setVisibility(View.VISIBLE);
	//
	//				if (mKakaoHotelIdx != -1)
	//				{
	//					int count = mHotelListAdapter.getCount();
	//
	//					for (int i = 0; i < count; i++)
	//					{
	//						HotelListViewItem item = mHotelListAdapter.getItem(i);
	//
	//						if (item.getType() == HotelListViewItem.TYPE_SECTION)
	//						{
	//							continue;
	//						} else
	//						{
	//							if (item.getItem().getIdx() == mKakaoHotelIdx)
	//							{
	//								mHotelListView.performItemClick(null, i + 1, -1);
	//								break;
	//							}
	//						}
	//					}
	//
	//					mKakaoHotelRegion = null;
	//					mKakaoHotelIdx = -1;
	//				}
	//			} catch (Exception e)
	//			{
	//				onError(e);
	//			}
	//		}
	//	};

	//	private DailyHotelJsonResponseListener mAppSaleTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	//	{
	//
	//		@Override
	//		public void onResponse(String url, JSONObject response)
	//		{
	//			try
	//			{
	//				if (response == null)
	//				{
	//					throw new NullPointerException("response == null");
	//				}
	//
	//				String open = response.getString("open");
	//				String close = response.getString("close");
	//
	//				mDailyHotelSaleTime.setOpenTime(open);
	//				mDailyHotelSaleTime.setCloseTime(close);
	//
	//				if (mDailyHotelSaleTime.isSaleTime() == false)
	//				{
	//					((MainActivity) mHostActivity).replaceFragment(WaitTimerFragment.newInstance(mDailyHotelSaleTime));
	//					unLockUI();
	//				} else
	//				{
	//					// 지역 리스트를 가져온다
	//					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_LOCATION_LIST).toString(), null, mSiteLocationListJsonArrayResponseListener, mHostActivity));
	//				}
	//			} catch (Exception e)
	//			{
	//				onError(e);
	//			}
	//		}
	//	};
	//
	//	private DailyHotelStringResponseListener mAppTimeStringResponseListener = new DailyHotelStringResponseListener()
	//	{
	//
	//		@Override
	//		public void onResponse(String url, String response)
	//		{
	//
	//			if (response != null)
	//			{
	//				mDailyHotelSaleTime.setCurrentTime(response);
	//			}
	//
	//			// 오픈, 클로즈 타임을 가져온다
	//			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME).toString(), null, mAppSaleTimeJsonResponseListener, mHostActivity));
	//		}
	//	};

	//
	//	@Override
	//	public void onResponse(String url, JSONObject response) {
	//		if (url.contains(URL_WEBAPI_APP_SALE_TIME)) {
	//
	//			try {
	//				String open = response.getString("open");
	//				String close = response.getString("close");
	//
	//				mDailyHotelSaleTime.setOpenTime(open);
	//				mDailyHotelSaleTime.setCloseTime(close);
	//
	//				if (!mDailyHotelSaleTime.isSaleTime()) {
	//					((MainActivity) mHostActivity).replaceFragment(WaitTimerFragment.newInstance(
	//							mDailyHotelSaleTime));
	//					unLockUI();
	//				} else {
	//					// 지역 리스트를 가져온다
	//					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
	//							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
	//									URL_WEBAPI_SITE_LOCATION_LIST).toString(),
	//									null, HotelListFragment.this,
	//									mHostActivity));
	//				}
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//
	//		} else if (url.contains(URL_WEBAPI_HOTEL)) {
	//
	//			try {
	//				mHotelList = new ArrayList<Hotel>();
	//				JSONArray hotelArr = response.getJSONArray("hotels");
	//
	//				for (int i = 0; i < hotelArr.length(); i++) {
	//					JSONObject obj = hotelArr.getJSONObject(i);
	//
	//					Hotel newHotel = new Hotel();
	//
	//					String name = obj.getString("name");
	//					String price = obj.getString("price");
	//					String discount = obj.getString("discount");
	//					String address = obj.getString("addr_summary");
	//					String category = obj.getString("cat");
	//					int idx = obj.getInt("idx");
	//					int available = obj.getInt("avail_room_count");
	//					int seq = obj.getInt("seq");
	//					String detailRegion = obj.getString("site2_name");
	//					JSONArray arr = obj.getJSONArray("img");
	//					String image = "default";
	//					if (arr.length() != 0) {
	//						JSONObject arrObj = arr.getJSONObject(0);
	//						image = arrObj.getString("path");
	//					}
	//
	//					newHotel.setName(name);
	//					newHotel.setPrice(price);
	//					newHotel.setDiscount(discount);
	//					newHotel.setAddress(address);
	//					newHotel.setCategory(category);
	//					newHotel.setIdx(idx);
	//					newHotel.setAvailableRoom(available);
	//					newHotel.setSequence(seq);
	//					newHotel.setImage(image);
	//					newHotel.setDetailRegion(detailRegion);
	//
	//					if (seq >= 0) { // 숨김호텔이 아니라면 추가. (음수일 경우 숨김호텔.)
	//						// SOLD OUT 된 항목은 밑으로.
	//						if (available <= 0) available *= 100;
	//
	//						mHotelList.add(newHotel); // 추가.
	//
	//						// seq 값에 따른 리스트 정렬
	//						Comparator<Hotel> comparator = new Comparator<Hotel>() {
	//							public int compare(Hotel o1, Hotel o2) {
	//								// 숫자정렬
	//								return Integer.parseInt(o1.getSequence() + "")
	//										- Integer.parseInt(o2.getSequence() + "");
	//							}
	//						};
	//
	//						Collections.sort(mHotelList, comparator);
	//					}
	//
	//				}
	//
	//				mHotelListViewList = new ArrayList<HotelListViewItem>();
	//				List<String> selectedRegionDetail = mRegionDetailList.get(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
	//
	//				for (int i = 0; i < selectedRegionDetail.size(); i++) {
	//					String region = selectedRegionDetail.get(i);
	//					HotelListViewItem section = new HotelListViewItem(region);
	//					mHotelListViewList.add(section);
	//
	//					int count = 0;
	//					for (int j = 0; j < mHotelList.size(); j++) {
	//						Hotel hotel = mHotelList.get(j);
	//						if (hotel.getDetailRegion().equals(region)) {
	//							mHotelListViewList.add(new HotelListViewItem(hotel));
	//							count++;
	//						}
	//					}
	//
	//					if (count == 0) mHotelListViewList.remove(section);
	//				}
	//
	//				int count = 0;
	//				HotelListViewItem others = new HotelListViewItem(getString(R.string.frag_hotel_list_others));
	//
	//				mHotelListViewList.add(others);
	//				for (int i = 0; i < mHotelList.size(); i++) {
	//					Hotel hotel = mHotelList.get(i);
	//					if (hotel.getDetailRegion().equals("null")) {
	//						mHotelListViewList.add(new HotelListViewItem(hotel));
	//						count++;
	//					}
	//				}
	//
	//				if (count == 0) mHotelListViewList.remove(others);
	//
	//				mHotelListAdapter = new HotelListAdapter(mHostActivity,
	//						R.layout.list_row_hotel, mHotelListViewList);
	//				mHotelListView.setAdapter(mHotelListAdapter);
	//				mHotelListView.setOnItemClickListener(this);
	//
	//				mRefreshHotelList = true;
	//
	//				// 새로운 이벤트 확인을 위해 버전 API 호출
	//				mQueue.add(new DailyHotelJsonRequest(Method.GET, 
	//						new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_VERSION).toString(), null, 
	//						mAppVersionJsonResponseListener, mHostActivity));
	//
	//				unLockUI();
	//
	//				// Notify PullToRefreshLayout that the refresh has finished
	//				mPullToRefreshLayout.setRefreshComplete();
	//				
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		} else if (url.contains(URL_WEBAPI_APP_VERSION)) {
	//			try {
	////				if (response.getString("new_event").equals("1") && (ivNewEvent != null))  ivNewEvent.setVisibility(View.VISIBLE);
	//				
	//				if (mKakaoHotelIdx != -1) {
	//					for (int i=0; i<mHotelListAdapter.getCount(); i++) {
	//						HotelListViewItem item = mHotelListAdapter.getItem(i);
	//						if (item.getType() == HotelListViewItem.TYPE_SECTION) {
	//							continue;
	//						} else {
	//							if(item.getItem().getIdx() == mKakaoHotelIdx) {
	//								mHotelListView.performItemClick(null, i+1, -1);
	//								break;
	//							}	
	//						}
	//					}
	//
	//					mKakaoHotelRegion = null;
	//					mKakaoHotelIdx = -1;
	//				}
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//
	//	}
	//
	//	@Override
	//	public void onResponse(String url, JSONArray response) {
	//		//해외 지역리스트 
	//		if (url.contains(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST)) {
	//			try {
	//				JSONArray arr = response;
	//				for (int i = 0; i < arr.length(); i++) {
	//					JSONObject obj = arr.getJSONObject(i);
	//					String name = new String();
	//					name = obj.getString("name");
	//					mRegionList.add(name);
	//					if (name.equals(getString(R.string.frag_hotel_list_tokyo))) tokyoIdx = i;
	//
	//					// 세부지역 추가
	//					List<String> nameDetailList = new ArrayList<String>();
	//					JSONArray arrDetail = obj.getJSONArray("child");
	//					for (int j=0; j<arrDetail.length(); j++) {
	//						String nameDetail = arrDetail.getString(j);
	//						nameDetailList.add(nameDetail);
	//
	//					}
	//					mRegionDetailList.put(name.trim(), nameDetailList);
	//				}
	//				
	//				android.util.Log.e("mJaRegionList", mRegionList.toString());
	//				android.util.Log.e("mJaRegionDetailList", mRegionDetailList.toString());
	//		        
	//				/**
	//				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
	//				 */
	//				int regionIdx = 0;
	//				boolean isRegion = false;
	//				boolean isBeforeRegion = false;
	//				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty()) {
	//					for (int i=0;i<mRegionList.size();i++) {
	//						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion)) {
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//					}
	//					if (regionIdx == 0) {
	//						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
	//					}
	//				} else {
	//					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
	//					
	//					for (int i=0;i<mRegionList.size();i++) {
	//						if (mRegionList.get(i).trim().equals(regionStr)) {
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//						//현재 선택지역 이전에 선택했던 지역이 있는지 파악 
	//						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, ""))) {
	//							beforeIdx = i;
	//							isBeforeRegion = true;
	//						}
	//					}
	//				}
	//				
	//				//선택지역이 없는 경우 
	//				if (isRegion == false) {
	////					String country = getCountryName();
	////					if (country.equals("대한민국"))	regionIdx = seoulIdx;
	////					else if (country.equals("일본")) 
	//					
	//					//이전에 선택했던 지역을 선택지역으로 설정함.
	//					regionIdx = beforeIdx;
	//					
	//					//이전에 선택했던 지역도 없는 경우
	//					//사용자의 나라를 얻어와서 대한민국이면 서울, 일본이면 도쿄를 선택지역으로 설정함. 
	//					if (!isBeforeRegion) {
	//						String country = getCountryByLocale();
	//						if (country.equals(getString(R.string.act_list_region_korea))) regionIdx = seoulIdx;
	//						else if (country.equals(getString(R.string.act_list_region_japan))) regionIdx = tokyoIdx;
	//					}
	//					
	//					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
	//					editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx)); 
	//					editor.commit();
	//					
	//				}
	//				
	//				fetchHotelList();
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//		else if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
	//			try {
	//				mRegionList = new ArrayList<String>();
	//				mRegionDetailList = new LinkedHashMap<String, List<String>>();
	//
	//				JSONArray arr = response;
	//				for (int i = 0; i < arr.length(); i++) {
	//					JSONObject obj = arr.getJSONObject(i);
	//					String name = new String();
	//					StringBuilder nameWithWhiteSpace = new StringBuilder(name);
	//					
	//					// 네비게이션 리스트 방식을 사용할 경우 간격을 조절하기 위함. 
	//					if (!event)	name = nameWithWhiteSpace.append("    ").append(obj.getString("name")).append("    ").toString();
	//					else name = obj.getString("name");
	//					
	//					mRegionList.add(name);
	//					
	//					if (name.trim().equals(getString(R.string.frag_hotel_list_seoul))) seoulIdx = i;
	//
	//					// 세부지역 추가
	//					List<String> nameDetailList = new ArrayList<String>();
	//					JSONArray arrDetail = obj.getJSONArray("child");
	//					for (int j=0; j<arrDetail.length(); j++) {
	//						String nameDetail = arrDetail.getString(j);
	//						nameDetailList.add(nameDetail);
	//
	//					}
	//					mRegionDetailList.put(name.trim(), nameDetailList);
	//				}
	//				
	//				android.util.Log.e("mRegionList", mRegionList.toString());
	//				android.util.Log.e("mRegionDetailList", mRegionDetailList.toString());
	//
	//				//기존의 지역리스트 표시방식 
	//				if (!event) {
	//					mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
	//					// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
	//					mHostActivity.actionBar
	//					.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	//					regionListAdapter = new RegionListAdapter(
	//							mHostActivity, mRegionList);
	//					regionListAdapter.setNotifyOnChange(true);
	//
	//					mHostActivity.actionBar.setListNavigationCallbacks(
	//							regionListAdapter, this);
	//				}
	//				
	//				/**
	//				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
	//				 */
	//				int regionIdx = 0;
	//				boolean isRegion = false;
	//				boolean isBeforeRegion = false;
	//				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty()) {
	//					for (int i=0;i<mRegionList.size();i++) {
	//						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion)) {
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//					}
	//					if (regionIdx == 0) {
	//
	//						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
	//
	//					}
	//				} else {
	//					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
	//					
	//					for (int i=0;i<mRegionList.size();i++) {
	//						if (mRegionList.get(i).trim().equals(regionStr)) {
	//							regionIdx = i;
	//							isRegion = true;
	//							break;
	//						}
	//						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, ""))) {
	//							beforeIdx = i;
	//							isBeforeRegion = true;
	//						}
	//					}
	////					regionIdx = mHostActivity.sharedPreference
	////							.getInt(KEY_PREFERENCE_REGION_INDEX, 0);
	//				}
	//				//선택지역이 없는 경우 
	//				if (isRegion == false) {
	////					String country = getCountryName();
	////					if (country.equals("대한민국"))	regionIdx = seoulIdx;
	////					else if (country.equals("일본")) 
	//					
	//					if (!event) {
	//						regionIdx = beforeIdx;
	//						
	//						if (!isBeforeRegion) {
	//							String country = getCountryByLocale();
	//							if (country.equals(getString(R.string.act_list_region_korea))) regionIdx = seoulIdx;
	//						}
	//						
	//						SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
	//						editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx)); 
	//						editor.commit();	
	//					}
	//					//새로운 지역리스트 방식의 경우 해외지역리스트 API도 호출함. 
	//					else {
	//						mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
	//								new StringBuilder(URL_DAILYHOTEL_SERVER).append(
	//										URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(),
	//										null, HotelListFragment.this,
	//										mHostActivity));
	//					}
	//					
	//				} else {
	//					fetchHotelList();
	//				}
	//				
	//				if (!event)	mHostActivity.actionBar.setSelectedNavigationItem(regionIdx);
	//				
	////				fetchHotelList();
	//
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//	}

	//	
	//	@Override
	//	public void onResponse(String url, String response) {
	//		if (url.contains(URL_WEBAPI_APP_TIME)) {
	//			mDailyHotelSaleTime.setCurrentTime(response);
	//
	//			// 오픈, 클로즈 타임을 가져온다
	//			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
	//					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME)
	//					.toString(), null, HotelListFragment.this,
	//					mHostActivity));
	//
	//		}
	//	}

}
