/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelTabBookingFragment (호텔 예약 탭)
 * 
 * 호텔 탭 중 예약 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.HotelDaysListFragment;
import com.twoheart.dailyhotel.HotelListFragment;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.WaitTimerFragment;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.RegionPopupListView;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;

public class HotelMainFragment extends BaseFragment implements RegionPopupListView.UserActionListener
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<HotelListFragment> mFragmentList;

	private SaleTime mTodaySaleTime;
	private ArrayList<String> mRegionList;

	//	private String mSelectedDetailRegion;

	private HOTEL_VIEW_TYPE mHotelViewType = HOTEL_VIEW_TYPE.LIST;

	public enum HOTEL_VIEW_TYPE
	{
		LIST, MAP, GONE, // 목록이 비어있는 경우.
	};

	public interface UserActionListener
	{
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime);

		public void selectDay(HotelListFragment fragment, boolean isListSelectionTop);

		public void toggleViewType();

		//		public void toggleViewType(String detailRegion);

		public void showProgress();

		public void hideProgress();
	};

	public interface UserAnalyticsActionListener
	{
		public void selectHotel(String hotelName, long hotelIndex);

		public void selectRegion(int position);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add(getString(R.string.label_today));
		titleList.add(getString(R.string.label_tomorrow));
		titleList.add(getString(R.string.label_selecteday));

		mHotelViewType = HOTEL_VIEW_TYPE.LIST;

		mTabIndicator = (TabIndicator) view.findViewById(R.id.tabindicator);
		//		mTabIndicator.setData(titleList, dayList, true);
		mTabIndicator.setData(titleList, true);
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

		mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
		//		mFragmentViewPager.setOnPageChangeListener(mOnPageChangeListener);

		mFragmentList = new ArrayList<HotelListFragment>();
		mTodaySaleTime = new SaleTime();

		HotelListFragment hotelListFragment = new HotelListFragment();
		hotelListFragment.setUserActionListener(mUserActionListener);
		mFragmentList.add(hotelListFragment);

		HotelListFragment hotelListFragment01 = new HotelListFragment();
		hotelListFragment01.setUserActionListener(mUserActionListener);
		mFragmentList.add(hotelListFragment01);

		HotelDaysListFragment hotelListFragment02 = new HotelDaysListFragment();
		hotelListFragment02.setUserActionListener(mUserActionListener);
		mFragmentList.add(hotelListFragment02);

		mFragmentViewPager.setData(mFragmentList);
		mFragmentViewPager.setAdapter(getChildFragmentManager());

		mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
		mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

		//		setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해 

		return view;
	}

	@Override
	public void onResume()
	{
		lockUI();

		super.onResume();

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_TIME).toString(), null, mAppTimeJsonResponseListener, mHostActivity));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		switch (requestCode)
		{
			case CODE_REQUEST_ACTIVITY_HOTELTAB:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
				} else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
				{
					((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
				}
				break;
			}

			case CODE_REQUEST_ACTIVITY_SELECT_REGIONMAP:
			{
				HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
				currentFragment.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(int position)
	{
		onNavigationItemSelected(position);
	}

	public void onNavigationItemSelected(int position)
	{
		String region = mRegionList.get(position);

		mHostActivity.setActionBarListEnabled(true);
		mHostActivity.setActionBarListData(region, mRegionList, this);

		boolean isSelectionTop = false;

		// 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
		if (region.equalsIgnoreCase(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "")) == false)
		{
			SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, region);
			editor.commit();

			isSelectionTop = true;
		}

		if (mUserAnalyticsActionListener != null)
		{
			mUserAnalyticsActionListener.selectRegion(position);
		}

		// 맵상태에서 서울 지역으로 이동할 경우. 새로 로딩 되어야 한다.
		//		if (mHotelViewType == HOTEL_VIEW_TYPE.MAP && "서울".equalsIgnoreCase(region) == true && isSelectionTop == true)
		//		{
		//			SelectDetailRegionDialog dialog = new SelectDetailRegionDialog(mHostActivity, android.R.style.Theme_Translucent_NoTitleBar);
		//			dialog.setOnSelectedRegionListener(new OnSelectedDetailRegionListener()
		//			{
		//				@Override
		//				public void onClick(String detailRegion)
		//				{
		//					mSelectedDetailRegion = detailRegion;
		//					
		//					HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
		//					hotelListFragment.processSelectedDetailRegion(detailRegion);
		//				}
		//
		//				@Override
		//				public void onCancel()
		//				{
		//					mSelectedDetailRegion = null;
		//					
		//					HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
		//					hotelListFragment.processSelectedDetailRegion(null);
		//				}
		//			});
		//
		//			dialog.show();
		//			return;
		//		}

		refreshHotelList(isSelectionTop);

		ExLog.d("before region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "") + " select region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
		return;
	}

	private void refreshHotelList(boolean isSelectionTop)
	{
		HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

		//		hotelListFragment.setSelectedDetailRegion(mSelectedDetailRegion);
		hotelListFragment.refreshHotelList(isSelectionTop);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// NetworkActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mAppTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (getActivity() == null)
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

				mTodaySaleTime.setCurrentTime(time);

				// SaleTime 시간 테스트 하기.
				//				mTodaySaleTime.setCurrentTime(time + 3600 * 14 * 1000);// + 60 * 25 * 1000);

				// 오픈, 클로즈 타임을 가져온다
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME).toString(), null, mAppSaleTimeJsonResponseListener, mHostActivity));

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
			if (getActivity() == null)
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

				mTodaySaleTime.setOpenTime(open);
				mTodaySaleTime.setCloseTime(close);

				if (mTodaySaleTime.isSaleTime() == false)
				{
					((MainActivity) mHostActivity).replaceFragment(WaitTimerFragment.newInstance(mTodaySaleTime));
					unLockUI();
				} else
				{
					// 지역 리스트를 가져온다
					mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_LOCATION_LIST).toString(), null, mSiteLocationListJsonArrayResponseListener, mHostActivity));
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonArrayResponseListener mSiteLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
	{
		@Override
		public void onResponse(String url, JSONArray response)
		{
			if (getActivity() == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				if (mRegionList == null)
				{
					mRegionList = new ArrayList<String>();
				}

				mRegionList.clear();

				LinkedHashMap<String, List<String>> detailRegionList = new LinkedHashMap<String, List<String>>();

				int length = response.length();
				int seoulIndex = -1;

				for (int i = 0; i < length; i++)
				{
					JSONObject jsonObject = response.getJSONObject(i);

					String name = jsonObject.getString("name").trim();

					if (TextUtils.isEmpty(name) == true)
					{
						continue;
					}

					mRegionList.add(name);

					if (getString(R.string.frag_hotel_list_seoul).equalsIgnoreCase(name) == true)
					{
						seoulIndex = mRegionList.size() - 1;
					}

					// 세부지역 추가
					JSONArray childJSONArray = jsonObject.getJSONArray("child");

					int childLength = childJSONArray.length();
					List<String> nameDetailList = new ArrayList<String>(childLength);

					for (int j = 0; j < childLength; j++)
					{
						nameDetailList.add(childJSONArray.getString(j));
					}

					detailRegionList.put(name, nameDetailList);
				}

				ExLog.e("mRegionList : " + mRegionList.toString());
				ExLog.e("mRegionDetailList : " + detailRegionList.toString());

				int currentRegionIndex = -1;
				int beforeRegionIndex = -1;

				String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
				int size = mRegionList.size();

				for (int i = 0; i < size; i++)
				{
					String regison = mRegionList.get(i);

					if (regison.equalsIgnoreCase(regionStr) == true)
					{
						currentRegionIndex = i;
						break;
					}

					if (regison.equalsIgnoreCase(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "")) == true)
					{
						beforeRegionIndex = i;
					}
				}

				//현재 선택 지역이 없는 경우 기본을 [서울]로 한다.
				if (currentRegionIndex == -1)
				{
					// 이전에 선택한 지역이 없는 경우.
					if (beforeRegionIndex == -1)
					{
						currentRegionIndex = seoulIndex;
					} else
					{
						currentRegionIndex = beforeRegionIndex;
					}

					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(currentRegionIndex));
					editor.commit();
				}

				//탭에 들어갈 날짜를 만든다.
				//				mTodaySaleTime.setLogicalTime();

				SaleTime[] tabSaleTime = null;

				int fragmentSize = mFragmentList.size();

				tabSaleTime = new SaleTime[3];

				for (int i = 0; i < fragmentSize; i++)
				{
					HotelListFragment hotelListFragment = mFragmentList.get(i);

					SaleTime saleTime = mTodaySaleTime.getClone(i);
					tabSaleTime[i] = saleTime;

					hotelListFragment.setSaleTime(saleTime);
					hotelListFragment.setRegionList(detailRegionList);

					ExLog.d("saleTime : " + saleTime);
				}

				// 임시로 여기서 날짜를 넣는다.
				ArrayList<String> dayList = new ArrayList<String>();

				ExLog.d("tabSaleTime[0].getLogicalDay() : " + tabSaleTime[0].getLogicalDay());
				dayList.add(getString(R.string.label_format_tabday, tabSaleTime[0].getLogicalDay(), tabSaleTime[0].getLogicalDayOftheWeek()));

				ExLog.d("tabSaleTime[0].getLogicalDay() : " + tabSaleTime[1].getLogicalDay());
				dayList.add(getString(R.string.label_format_tabday, tabSaleTime[1].getLogicalDay(), tabSaleTime[1].getLogicalDayOftheWeek()));

				ExLog.d("mTabIndicator.getSubText(2) : " + mTabIndicator.getSubText(2));

				if (TextUtils.isEmpty(mTabIndicator.getSubText(2)) == true)
				{
					dayList.add(getString(R.string.label_format_tabday, tabSaleTime[2].getLogicalDay(), tabSaleTime[2].getLogicalDayOftheWeek()));
				} else
				{
					dayList.add(mTabIndicator.getSubText(2));
				}

				int tabSize = mTabIndicator.size();

				ExLog.d("tabSize : " + tabSize);

				for (int i = 0; i < tabSize; i++)
				{
					String day = dayList.get(i);

					if (TextUtils.isEmpty(day) == true)
					{
						mTabIndicator.setSubTextEnable(i, false);
					} else
					{
						mTabIndicator.setSubTextEnable(i, true);
						mTabIndicator.setSubText(i, day);
					}

					ExLog.d("day : " + day);
				}

				// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
				mHostActivity.setActionBarListEnabled(true);
				mHostActivity.setActionBarListData(mRegionList.get(currentRegionIndex), mRegionList, HotelMainFragment.this);

				ExLog.d("mRegionList.get(currentRegionIndex) : " + mRegionList.get(currentRegionIndex));

				onNavigationItemSelected(currentRegionIndex);

				ExLog.d("onNavigationItemSelected(currentRegionIndex);");
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener()
	{
		@Override
		public void onTabSelected(int position)
		{
			if (mFragmentViewPager == null)
			{
				return;
			}

			if (mFragmentViewPager.getCurrentItem() == position)
			{
				HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
				currentFragment.onPageSelected(false);
			} else
			{
				mFragmentViewPager.setCurrentItem(position);
			}
		}
	};

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			try
			{
				mTabIndicator.setCurrentItem(position);

				// 현재 페이지 선택 상태를 Fragment에게 알려준다.
				HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

				boolean isSelectionTop = false;

				for (HotelListFragment hotelListFragment : mFragmentList)
				{
					if (hotelListFragment == currentFragment)
					{
						String listRegion = hotelListFragment.getRegion();
						String selectedRegion = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

						isSelectionTop = selectedRegion.equalsIgnoreCase(listRegion) == false;
						hotelListFragment.onPageSelected(true);
					} else
					{
						hotelListFragment.onPageUnSelected();
					}
				}

				refreshHotelList(isSelectionTop);
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				// 릴리즈 버전에서 메모리 해지에 문제가 생기는 경우가 있어 앱을 재 시작 시킨다.
				if (DEBUG == false)
				{
					Util.restartApp(mHostActivity.getApplicationContext());
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
			switch (state)
			{
				case ViewPager.SCROLL_STATE_IDLE:
				{
					HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
					currentFragment.setFloatingActionButtonVisible(true);
					break;
				}

				case ViewPager.SCROLL_STATE_DRAGGING:
				{
					HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
					currentFragment.setFloatingActionButtonVisible(false);
					break;
				}

				case ViewPager.SCROLL_STATE_SETTLING:
				{
					break;
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}
	};

	private UserActionListener mUserActionListener = new UserActionListener()
	{

		@Override
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			if (hotelListViewItem == null || hotelIndex < 0)
			{
				ExLog.d("hotelListViewItem == null || hotelIndex < 0");

				releaseUiComponent();
				return;
			}

			switch (hotelListViewItem.getType())
			{
				case HotelListViewItem.TYPE_ENTRY:
				{
					Intent i = new Intent(mHostActivity, HotelTabActivity.class);

					String region = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
					editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, hotelListViewItem.getItem().getName());
					editor.commit();

					i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, hotelListViewItem.getItem());

					i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);

					i.putExtra(NAME_INTENT_EXTRA_DATA_REGION, region);
					i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);

					startActivityForResult(i, CODE_REQUEST_ACTIVITY_HOTELTAB);

					mUserAnalyticsActionListener.selectHotel(hotelListViewItem.getItem().getName(), hotelIndex);
					break;
				}

				case HotelListViewItem.TYPE_SECTION:
				default:
					releaseUiComponent();
					break;
			}
		}

		@Override
		public void selectDay(HotelListFragment fragment, boolean isListSelectionTop)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			if (fragment != null)
			{
				// 선택탭의 이름을 수정한다.
				SaleTime saleTime = fragment.getSaleTime();
				String day = getString(R.string.label_format_tabday, saleTime.getLogicalDay(), saleTime.getLogicalDayOftheWeek());

				mTabIndicator.setSubTextEnable(2, true);
				mTabIndicator.setSubText(2, day);

				fragment.refreshHotelList(isListSelectionTop);
			}

			releaseUiComponent();
		}

		@Override
		public void toggleViewType()
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUI();

			switch (mHotelViewType)
			{
				case LIST:
					mHotelViewType = HOTEL_VIEW_TYPE.MAP;
					break;

				case MAP:
					mHotelViewType = HOTEL_VIEW_TYPE.LIST;
					break;
			}

			// 현재 페이지 선택 상태를 Fragment에게 알려준다.
			HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

			for (HotelListFragment hotelListFragment : mFragmentList)
			{
				boolean isCurrentFragment = hotelListFragment == currentFragment;

				hotelListFragment.setHotelViewType(mHotelViewType, isCurrentFragment);
			}

			unLockUI();
		}

		//		@Override
		//		public void toggleViewType(String detailRegion)
		//		{
		//			mSelectedDetailRegion = detailRegion;
		//			
		//			toggleViewType();
		//		}

		@Override
		public void showProgress()
		{
			lockUI();
		}

		@Override
		public void hideProgress()
		{
			unLockUI();
		}
	};

	private UserAnalyticsActionListener mUserAnalyticsActionListener = new UserAnalyticsActionListener()
	{
		@Override
		public void selectHotel(String hotelName, long hotelIndex)
		{
			RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectHotel", hotelName, hotelIndex);
		}

		@Override
		public void selectRegion(int position)
		{
			RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectRegion", mRegionList.get(position), (long) (position + 1));
		}
	};
}
