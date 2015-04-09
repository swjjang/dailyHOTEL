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
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.FragmentViewPager.OnPageSelectedListener;
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

	public interface UserActionListener
	{
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex);

		public void selectDay(HotelListFragment fragment);
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

		//		ArrayList<String> dayList = new ArrayList<String>();
		//		dayList.add("1일(수)");
		//		dayList.add("2일(목)");
		//		dayList.add("");

		mTabIndicator = (TabIndicator) view.findViewById(R.id.tabindicator);
		//		mTabIndicator.setData(titleList, dayList, true);
		mTabIndicator.setData(titleList, true);
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

		mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
		mFragmentViewPager.setOnPageSelectedListener(mOnPageSelectedListener);

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

		return view;
	}

	@Override
	public void onResume()
	{
		lockUI();

		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME).toString(), null, mAppTimeStringResponseListener, mHostActivity));

		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		if (requestCode == CODE_REQUEST_ACTIVITY_HOTELTAB)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
			} else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
			{
				((MainActivity) mHostActivity).selectMenuDrawer(((MainActivity) mHostActivity).menuBookingListFragment);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(int position)
	{
		onNavigationItemSelected(position);
	}

	public boolean onNavigationItemSelected(int position)
	{
		String region = mRegionList.get(position);

		mHostActivity.setActionBarListEnabled(true);
		mHostActivity.setActionBarListData(region, mRegionList, this);

		// 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
		if (region.equalsIgnoreCase(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "")) == false)
		{
			SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, region);
			editor.commit();
		}

		refreshHotelList(true);

		ExLog.d("before region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "") + " select region : " + mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));

		mUserAnalyticsActionListener.selectRegion(position);
		return true;
	}

	private void refreshHotelList(boolean isSelectedNavigationItem)
	{
		HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

		hotelListFragment.refreshHotelList(isSelectedNavigationItem);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// NetworkActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelStringResponseListener mAppTimeStringResponseListener = new DailyHotelStringResponseListener()
	{
		@Override
		public void onResponse(String url, String response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				mTodaySaleTime.setCurrentTime(response);

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

				if (mFragmentList != null)
				{
					int fragmentSize = mFragmentList.size();

					for (int i = 0; i < fragmentSize; i++)
					{
						HotelListFragment hotelListFragment = mFragmentList.get(i);
						hotelListFragment.setSaleTime(mTodaySaleTime.getClone(i));
						hotelListFragment.setRegionList(detailRegionList);
					}
				}

				// 임시로 여기서 날짜를 넣는다.
				ArrayList<String> dayList = new ArrayList<String>();

				SaleTime nextSaleTime = mTodaySaleTime.getClone(1);

				dayList.add(getString(R.string.label_format_tabday, mTodaySaleTime.getCurrentDayEx(), mTodaySaleTime.getCurrentDayOftheWeek()));
				dayList.add(getString(R.string.label_format_tabday, nextSaleTime.getCurrentDayEx(), nextSaleTime.getCurrentDayOftheWeek()));
				dayList.add(mTabIndicator.getSubText(2));

				int tabSize = mTabIndicator.size();

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
				}

				// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
				mHostActivity.setActionBarListEnabled(true);
				mHostActivity.setActionBarListData(mRegionList.get(currentRegionIndex), mRegionList, HotelMainFragment.this);

				onNavigationItemSelected(currentRegionIndex);
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

	private OnPageSelectedListener mOnPageSelectedListener = new OnPageSelectedListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			mTabIndicator.setCurrentItem(position);

			// 현재 페이지 선택 상태를 Fragment에게 알려준다.
			HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

			for (HotelListFragment hotelListFragment : mFragmentList)
			{
				if (hotelListFragment == currentFragment)
				{
					hotelListFragment.onPageSelected(true);
				} else
				{
					hotelListFragment.onPageUnSelected();
				}
			}

			refreshHotelList(false);
		}
	};

	private UserActionListener mUserActionListener = new UserActionListener()
	{

		@Override
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex)
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
				case HotelListViewItem.TYPE_ENTRY: {
					Intent i = new Intent(mHostActivity, HotelTabActivity.class);

					String region = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
					editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, hotelListViewItem.getItem().getName());
					editor.commit();

					i.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, hotelListViewItem.getItem());
					i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mTodaySaleTime);
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
		public void selectDay(HotelListFragment fragment)
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
				String day = getString(R.string.label_format_tabday, saleTime.getCurrentDayEx(), saleTime.getCurrentDayOftheWeek());

				mTabIndicator.setSubTextEnable(2, true);
				mTabIndicator.setSubText(2, day);

				fragment.refreshHotelList(false);
			}

			releaseUiComponent();
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
