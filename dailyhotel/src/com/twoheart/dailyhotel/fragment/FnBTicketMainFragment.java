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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.SelectAreaActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;
import com.twoheart.dailyhotel.widget.FragmentViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FnBTicketMainFragment extends TicketMainFragment
{
	private TextView mHeaderTextView;

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_fnb_main, container, false);

		mHeaderTextView = (TextView) view.findViewById(R.id.headerTextView);
		mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
		mFragmentList = new ArrayList<TicketListFragment>();

		FnBTicketListFragment fnbListFragment = new FnBTicketListFragment();
		fnbListFragment.setUserActionListener(mOnUserActionListener);
		mFragmentList.add(fnbListFragment);

		mFragmentViewPager.setData(mFragmentList);
		mFragmentViewPager.setAdapter(getChildFragmentManager());

		return view;
	}

	@Override
	protected void showSlidingDrawer()
	{
		mHeaderTextView.setVisibility(View.VISIBLE);

		setMenuEnabled(true);
	}

	@Override
	protected void hideSlidingDrawer()
	{
		mHeaderTextView.setVisibility(View.INVISIBLE);

		setMenuEnabled(false);
	}

	public void onNavigationItemSelected(Province province)
	{
		if (province == null)
		{
			return;
		}

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mSelectedProvince = province;

		baseActivity.setActionBarAreaEnabled(true);
		baseActivity.setActionBarArea(province.name, mOnUserActionListener);

		boolean isSelectionTop = false;

		// 기존에 설정된 지역과 다른 지역을 선택하면 해당 지역을 저장한다.
		if (province.name.equalsIgnoreCase(baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "")) == false)
		{
			SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE, baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_FNB_REGION_SELECT, province.name);
			editor.commit();

			isSelectionTop = true;
		}

		if (mUserAnalyticsActionListener != null)
		{
			mUserAnalyticsActionListener.selectRegion(province);
		}

		refreshList(province, isSelectionTop);
	}

	@Override
	protected void requestTicketList(BaseActivity baseActivity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void requestProvinceList(BaseActivity baseActivity)
	{
		// 지역 리스트를 가져온다
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_REGION_PROVINCE_LIST).toString(), null, mProvinceListJsonResponseListener, baseActivity));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{

		@Override
		public void selectedTicket(TicketViewItem baseListViewItem, SaleTime checkSaleTime)
		{
			//			BaseActivity baseActivity = (BaseActivity) getActivity();
			//
			//			if (baseActivity == null)
			//			{
			//				return;
			//			}
			//
			//			if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
			//			{
			//				return;
			//			}
			//
			//			lockUiComponent();
			//			baseActivity.lockUiComponent();
			//
			//			if (baseListViewItem == null)
			//			{
			//				releaseUiComponent();
			//				baseActivity.releaseUiComponent();
			//				return;
			//			}
			//
			//			switch (baseListViewItem.type)
			//			{
			//				case HotelListViewItem.TYPE_ENTRY:
			//				{
			//					lockUI();
			//
			//					FnBTicketDto ticketDto = (FnBTicketDto) baseListViewItem.getTicketDto();
			//
			//					String region = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "");
			//					SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
			//					editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
			//					editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, ticketDto.name);
			//					editor.commit();
			//
			//					Intent intent = new Intent(baseActivity, FnBTicketDetailActivity.class);
			//					intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
			//					intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETIDX, ticketDto.index);
			//					intent.putExtra(NAME_INTENT_EXTRA_DATA_TITLE, ticketDto.name);
			//					intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, ticketDto.image);
			//
			//					startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);
			//
			//					mUserAnalyticsActionListener.selectedTicket(ticketDto.name, ticketDto.index, checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
			//					break;
			//				}
			//
			//				case HotelListViewItem.TYPE_SECTION:
			//				default:
			//					releaseUiComponent();
			//					baseActivity.releaseUiComponent();
			//					break;
			//			}
		}

		@Override
		public void selectedTicket(int index, long dailyTime, int dailyDayOfDays, int nights)
		{
			//			BaseActivity baseActivity = (BaseActivity) getActivity();
			//
			//			if (baseActivity == null || index < 0)
			//			{
			//				return;
			//			}
			//
			//			if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
			//			{
			//				return;
			//			}
			//
			//			lockUI();
			//
			//			Intent intent = new Intent(baseActivity, FnBTicketDetailActivity.class);
			//
			//			intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
			//			intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETIDX, index);
			//			intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
			//			intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
			//			intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
			//
			//			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);
		}

		@Override
		public void toggleViewType()
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUI();

			switch (mViewType)
			{
				case LIST:
					mViewType = VIEW_TYPE.MAP;
					break;

				case MAP:
					mViewType = VIEW_TYPE.LIST;
					break;
			}

			// 현재 페이지 선택 상태를 Fragment에게 알려준다.
			TicketListFragment currentFragment = (TicketListFragment) mFragmentViewPager.getCurrentFragment();

			for (TicketListFragment fnbListFragment : mFragmentList)
			{
				boolean isCurrentFragment = fnbListFragment == currentFragment;

				fnbListFragment.setViewType(mViewType, isCurrentFragment);
			}

			unLockUI();
		}

		@Override
		public void onClickActionBarArea()
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			Intent intent = new Intent(baseActivity, SelectAreaActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mSelectedProvince);
			intent.putParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREAITEMLIST, mAreaItemList);
			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SELECT_AREA);
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// NetworkActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mProvinceListJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null || baseActivity.isFinishing() == true)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					throw new NullPointerException("response == null");
				}

				JSONArray provinceArray = response.getJSONArray("data");
				ArrayList<Province> provinceList = makeProvinceList(provinceArray);

				// 마지막으로 선택한 지역을 가져온다.
				String regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "");
				Province selectedProvince = null;

				if (TextUtils.isEmpty(regionName) == true)
				{
					// 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
					regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE, "");

					// 해당 지역이 없는 경우 Province의 첫번째 지역으로 한다.
					if (TextUtils.isEmpty(regionName) == true)
					{
						selectedProvince = provinceList.get(0);
						regionName = selectedProvince.name;
					}
				}

				if (selectedProvince == null)
				{
					for (Province province : provinceList)
					{
						if (province.name.equals(regionName) == true)
						{
							selectedProvince = province;
							break;
						}
					}
				}

				mAreaItemList = makeAreaItemList(provinceList, null);

				// 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
				if (selectedProvince == null)
				{
					selectedProvince = provinceList.get(0);
					regionName = selectedProvince.name;
				}

				boolean mIsProvinceSetting = baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_FNB_REGION_SETTING, false);
				SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
				editor.putBoolean(KEY_PREFERENCE_FNB_REGION_SETTING, true);
				editor.commit();

				// 마지막으로 지역이 Area로 되어있으면 Province로 바꾸어 준다.
				if (mIsProvinceSetting == false && selectedProvince instanceof Area)
				{
					int provinceIndex = ((Area) selectedProvince).getProvinceIndex();

					for (Province province : provinceList)
					{
						if (province.getProvinceIndex() == provinceIndex)
						{
							selectedProvince = province;
							break;
						}
					}
				}

				editor.putString(KEY_PREFERENCE_FNB_REGION_SELECT, regionName);
				editor.commit();

				//탭에 들어갈 날짜를 만든다.
				SaleTime[] tabSaleTime = null;

				int fragmentSize = mFragmentList.size();

				tabSaleTime = new SaleTime[3];

				for (int i = 0; i < fragmentSize; i++)
				{
					TicketListFragment ticketListFragment = mFragmentList.get(i);

					SaleTime saleTime;

					if (i == 2)
					{
						saleTime = mTodaySaleTime.getClone(0);
						tabSaleTime[i] = saleTime;
					} else
					{
						saleTime = mTodaySaleTime.getClone(i);
						tabSaleTime[i] = saleTime;
					}

					if (ticketListFragment.getSaleTime() == null)
					{
						ticketListFragment.setSaleTime(saleTime);
					}
				}

				onNavigationItemSelected(selectedProvince);
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}

		private ArrayList<Area> makeAreaList(JSONArray jsonArray)
		{
			ArrayList<Area> areaList = new ArrayList<Area>();

			try
			{
				int length = jsonArray.length();

				for (int i = 0; i < length; i++)
				{
					JSONObject jsonObject = jsonArray.getJSONObject(i);

					try
					{
						Area area = new Area(jsonObject);

						areaList.add(area);
					} catch (JSONException e)
					{
						ExLog.d(e.toString());
					}
				}
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}

			return areaList;
		}

		private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
		{
			ArrayList<Province> provinceList = new ArrayList<Province>();

			try
			{
				int length = jsonArray.length();

				for (int i = 0; i < length; i++)
				{
					JSONObject jsonObject = jsonArray.getJSONObject(i);

					try
					{
						Province province = new Province(jsonObject);

						provinceList.add(province);
					} catch (JSONException e)
					{
						ExLog.d(e.toString());
					}
				}
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}

			return provinceList;
		}

	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////	

}
