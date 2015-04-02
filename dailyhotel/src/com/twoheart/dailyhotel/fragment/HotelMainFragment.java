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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.HotelListFragment;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.WaitTimerFragment;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.adapter.RegionListAdapter;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
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
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.FragmentViewPager.OnPageSelectedListener;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;
import com.viewpagerindicator.TabPageIndicator;

public class HotelMainFragment extends BaseFragment
{
	protected HotelViewPager mViewPager;
	protected TabPageIndicator mIndicator;

	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<Fragment> mFragmentList;
	
	private SaleTime mSaleTime;
	
	public interface UserActionListener
	{
		public void openHotelDetail(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime);
	};
	
	public interface UserAnalyticsActionListener
	{
		public void openHotelDetail(String hotelName, long hotelIndex);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_hotel_main, container, false);

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add("오늘");
		titleList.add("내일");
		titleList.add("선택");

		ArrayList<String> dayList = new ArrayList<String>();
		dayList.add("1일(수)");
		dayList.add("2일(목)");
		dayList.add("");

		mTabIndicator = (TabIndicator) view.findViewById(R.id.tabindicator);
		mTabIndicator.setData(titleList, dayList, true);
		mTabIndicator.setTextColor(getResources().getColor(R.color.textView_textColor_main));
		mTabIndicator.setTextTypeface(Typeface.BOLD);
		mTabIndicator.setSubTextColor(getResources().getColor(R.color.textView_textColor_main));
		mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

		mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
		mFragmentViewPager.setOnPageSelectedListener(mOnPageSelectedListener);

		mFragmentList = new ArrayList<Fragment>();
		mSaleTime = new SaleTime();

		HotelListFragment hotelListFragment = new HotelListFragment();
		hotelListFragment.setUserActionListener(mUserActionListener);
		mFragmentList.add(hotelListFragment);

		HotelListFragment hotelListFragment01 = new HotelListFragment();
		mFragmentList.add(hotelListFragment01);

		HotelListFragment hotelListFragment02 = new HotelListFragment();
		mFragmentList.add(hotelListFragment02);
		
		mFragmentViewPager.setData(mFragmentList);
		mFragmentViewPager.setAdapter(getFragmentManager());

		mHostActivity.setActionBar(R.string.actionbar_title_hotel_list_frag);

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
	public void onPause()
	{
		
		super.onPause();
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
				
				mSaleTime.setCurrentTime(response);
				
				// 오픈, 클로즈 타임을 가져온다
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME).toString(), null, mAppSaleTimeJsonResponseListener, mHostActivity));

			} catch (Exception e)
			{
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

				mSaleTime.setOpenTime(open);
				mSaleTime.setCloseTime(close);

				if (mSaleTime.isSaleTime() == false)
				{
					((MainActivity) mHostActivity).replaceFragment(WaitTimerFragment.newInstance(mSaleTime));
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

				mRegionList = new ArrayList<String>();
				mRegionDetailList = new LinkedHashMap<String, List<String>>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++)
				{
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					StringBuilder nameWithWhiteSpace = new StringBuilder(name);

					// 네비게이션 리스트 방식을 사용할 경우 간격을 조절하기 위함. 
					if (event == false)
					{
						name = nameWithWhiteSpace.append("    ").append(obj.getString("name")).append("    ").toString();
					} else
					{
						name = obj.getString("name");
					}

					mRegionList.add(name);

					if (name.trim().equals(getString(R.string.frag_hotel_list_seoul)) == true)
					{
						seoulIdx = i;
					}

					// 세부지역 추가
					JSONArray arrDetail = obj.getJSONArray("child");
					List<String> nameDetailList = new ArrayList<String>(arrDetail.length());

					for (int j = 0; j < arrDetail.length(); j++)
					{
						String nameDetail = arrDetail.getString(j);
						nameDetailList.add(nameDetail);
					}

					mRegionDetailList.put(name.trim(), nameDetailList);
				}

				ExLog.e("mRegionList : " + mRegionList.toString());
				ExLog.e("mRegionDetailList : " + mRegionDetailList.toString());

				//기존의 지역리스트 표시방식 
				if (event == false)
				{
					mHostActivity.actionBar.setDisplayShowTitleEnabled(false);
					// 호텔 프래그먼트 일때 액션바에 네비게이션 리스트 설치.
					mHostActivity.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
					regionListAdapter = new RegionListAdapter(mHostActivity, mRegionList);
					regionListAdapter.setNotifyOnChange(true);

					mHostActivity.actionBar.setListNavigationCallbacks(regionListAdapter, HotelListFragment.this);
				}

				/**
				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
				 */
				int regionIdx = 0;
				boolean isRegion = false;
				boolean isBeforeRegion = false;
				if (mKakaoHotelRegion != null && !mKakaoHotelRegion.isEmpty())
				{
					for (int i = 0; i < mRegionList.size(); i++)
					{
						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion) == true)
						{
							regionIdx = i;
							isRegion = true;
							break;
						}
					}

					if (regionIdx == 0)
					{
						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
					}
				} else
				{
					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

					for (int i = 0; i < mRegionList.size(); i++)
					{
						if (mRegionList.get(i).trim().equals(regionStr) == true)
						{
							regionIdx = i;
							isRegion = true;
							break;
						}

						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "")) == true)
						{
							beforeIdx = i;
							isBeforeRegion = true;
						}
					}
					//					regionIdx = mHostActivity.sharedPreference
					//							.getInt(KEY_PREFERENCE_REGION_INDEX, 0);
				}

				//선택지역이 없는 경우 
				if (isRegion == false)
				{
					//					String country = getCountryName();
					//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
					//					else if (country.equals("일본")) 

					if (event == false)
					{
						regionIdx = beforeIdx;

						if (!isBeforeRegion)
						{
							String country = getCountryByLocale();
							if (country.equals(getString(R.string.act_list_region_korea)))
							{
								regionIdx = seoulIdx;
							}
						}

						SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
						editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx));
						editor.commit();
					}
					//새로운 지역리스트 방식의 경우 해외지역리스트 API도 호출함. 
					else
					{
						mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(), null, mSiteCountryLocationListJsonArrayResponseListener, mHostActivity));
					}
				}

				if (event == true)
				{
					fetchHotelList();
				} else
				{
					mHostActivity.actionBar.setSelectedNavigationItem(regionIdx);
				}
			} catch (Exception e)
			{
				onError(e);
			}

		}
	};
	
	private DailyHotelJsonArrayResponseListener mSiteCountryLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
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

				for (int i = 0; i < response.length(); i++)
				{
					JSONObject obj = response.getJSONObject(i);
					String name = obj.getString("name");

					mRegionList.add(name);
					if (name.equals(getString(R.string.frag_hotel_list_tokyo)) == true)
					{
						tokyoIdx = i;
					}

					// 세부지역 추가
					JSONArray arrDetail = obj.getJSONArray("child");
					List<String> nameDetailList = new ArrayList<String>(arrDetail.length());

					for (int j = 0; j < arrDetail.length(); j++)
					{
						String nameDetail = arrDetail.getString(j);
						nameDetailList.add(nameDetail);
					}

					mRegionDetailList.put(name.trim(), nameDetailList);
				}

				ExLog.e("mJaRegionList : " + mRegionList.toString());
				ExLog.e("mJaRegionDetailList : " + mRegionDetailList.toString());

				/**
				 * KaKao링크를 통한 접속 일경우 해당 호텔까지 접속함.
				 */
				int regionIdx = 0;
				boolean isRegion = false;
				boolean isBeforeRegion = false;

				if (mKakaoHotelRegion != null && mKakaoHotelRegion.isEmpty() == false)
				{
					for (int i = 0; i < mRegionList.size(); i++)
					{
						if (mRegionList.get(i).trim().equals(mKakaoHotelRegion) == true)
						{
							regionIdx = i;
							isRegion = true;
							break;
						}
					}

					if (regionIdx == 0)
					{
						SimpleAlertDialog.build(mHostActivity, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_kakao_link), getString(R.string.dialog_btn_text_confirm), null);
					}
				} else
				{
					String regionStr = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

					for (int i = 0; i < mRegionList.size(); i++)
					{
						if (mRegionList.get(i).trim().equals(regionStr) == true)
						{
							regionIdx = i;
							isRegion = true;
							break;
						}

						//현재 선택지역 이전에 선택했던 지역이 있는지 파악 
						if (mRegionList.get(i).trim().equals(mHostActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "")) == true)
						{
							beforeIdx = i;
							isBeforeRegion = true;
						}
					}
				}

				//선택지역이 없는 경우 
				if (isRegion == false)
				{
					//					String country = getCountryName();
					//					if (country.equals("대한민국"))	regionIdx = seoulIdx;
					//					else if (country.equals("일본")) 

					//이전에 선택했던 지역을 선택지역으로 설정함.
					regionIdx = beforeIdx;

					//이전에 선택했던 지역도 없는 경우
					//사용자의 나라를 얻어와서 대한민국이면 서울, 일본이면 도쿄를 선택지역으로 설정함. 
					if (isBeforeRegion == false)
					{
						String country = getCountryByLocale();

						if (country.equals(getString(R.string.act_list_region_korea)))
						{
							regionIdx = seoulIdx;
						} else if (country.equals(getString(R.string.act_list_region_japan)))
						{
							regionIdx = tokyoIdx;
						}
					}

					SharedPreferences.Editor editor = mHostActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT, mRegionList.get(regionIdx));
					editor.commit();
				}

				fetchHotelList();
			} catch (Exception e)
			{
				onError(e);
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

			mFragmentViewPager.setCurrentItem(position);
		}
	};

	private OnPageSelectedListener mOnPageSelectedListener = new OnPageSelectedListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			mTabIndicator.setCurrentItem(position);
		}
	};
	
	private UserActionListener mUserActionListener = new UserActionListener()
	{

		@Override
		public void openHotelDetail(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();
			
			if(hotelListViewItem == null || hotelIndex < 0)
			{
				ExLog.d("hotelListViewItem == null || hotelIndex < 0");
				
				releaseUiComponent();
				return;
			}
			
			switch(hotelListViewItem.getType())
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

					mUserAnalyticsActionListener.openHotelDetail(hotelListViewItem.getItem().getName(), hotelIndex);
					break;
				}
				
				case HotelListViewItem.TYPE_SECTION:
				default:
					releaseUiComponent();
					break;
			}
		}
	};
	
	
	private UserAnalyticsActionListener mUserAnalyticsActionListener = new UserAnalyticsActionListener()
	{
		@Override
		public void openHotelDetail(String hotelName, long hotelIndex)
		{
			RenewalGaManager.getInstance(mHostActivity.getApplicationContext()).recordEvent("click", "selectHotel", hotelName, hotelIndex);
		}
	};
}
