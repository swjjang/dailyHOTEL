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
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.TicketViewItem;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class TicketMainFragment extends BaseFragment
{
	protected FragmentViewPager mFragmentViewPager;
	protected ArrayList<TicketListFragment> mFragmentList;

	protected SaleTime mTodaySaleTime;
	protected ArrayList<AreaItem> mAreaItemList;
	protected Province mSelectedProvince;

	private boolean mMenuEnabled;
	private boolean mDontReloadAtOnResume;
	protected OnUserActionListener mOnUserActionListener;
	protected UserAnalyticsActionListener mUserAnalyticsActionListener;

	protected VIEW_TYPE mViewType = VIEW_TYPE.LIST;

	public enum VIEW_TYPE
	{
		LIST, MAP, GONE, // 목록이 비어있는 경우.
	};

	public enum TICKET_TYPE
	{
		HOTEL, FNB,
	};

	public interface OnUserActionListener
	{
		public void selectedTicket(TicketViewItem baseListViewItem, SaleTime checkSaleTime);

		public void selectedTicket(int index, long dailyTime, int dailyDayOfDays, int nights);

		public void toggleViewType();

		public void onClickActionBarArea();
	};

	public interface UserAnalyticsActionListener
	{
		public void selectedTicket(String name, long index, String checkInTime);

		public void selectRegion(Province province);
	};

	protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	protected abstract void hideSlidingDrawer();

	protected abstract void showSlidingDrawer();

	protected abstract void onNavigationItemSelected(Province province);

	protected abstract void requestProvinceList(BaseActivity baseActivity);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mViewType = VIEW_TYPE.LIST;

		mFragmentList = new ArrayList<TicketListFragment>();
		mTodaySaleTime = new SaleTime();

		View view = createView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해 

		hideSlidingDrawer();

		return view;
	}

	@Override
	public void onResume()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (mDontReloadAtOnResume == true)
		{
			mDontReloadAtOnResume = false;
		} else
		{
			lockUI();

			Map<String, String> params = new HashMap<String, String>();
			params.put("timeZone", "Asia/Seoul");

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));
		}

		super.onResume();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		MenuInflater inflater = baseActivity.getMenuInflater();

		menu.clear();

		if (mMenuEnabled == true)
		{
			switch (mViewType)
			{
				case LIST:
					inflater.inflate(R.menu.actionbar_icon_map, menu);
					break;

				case MAP:
					inflater.inflate(R.menu.actionbar_icon_list, menu);
					break;
			}
		}
	}

	public void setMenuEnabled(boolean enabled)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (mAreaItemList != null && enabled == true)
		{
			boolean isShowSpinner = mAreaItemList != null && mAreaItemList.size() > 1 ? true : false;
			baseActivity.setActionBarRegionEnable(isShowSpinner);
		}

		if (mMenuEnabled == enabled || mTodaySaleTime.isSaleTime() == false)
		{
			return;
		}

		mMenuEnabled = enabled;

		baseActivity.invalidateOptionsMenu();

		// 메뉴가 열리는 시점이다.
		TicketListFragment currentFragment = (TicketListFragment) mFragmentViewPager.getCurrentFragment();

		if (currentFragment != null)
		{
			if (enabled == true)
			{
				currentFragment.setActionBarAnimationLock(false);
			} else
			{
				currentFragment.showActionBarAnimatoin(baseActivity);
				currentFragment.setActionBarAnimationLock(true);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return false;
		}

		switch (item.getItemId())
		{
			case R.id.action_list:
			{
				int isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

				if (isInstalledGooglePlayServices == 1)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.toggleViewType();
					}

					baseActivity.invalidateOptionsMenu();
				}
				return true;
			}

			case R.id.action_map:
			{
				int isInstalledGooglePlayServices = Util.installGooglePlayService(baseActivity);

				if (isInstalledGooglePlayServices == 1)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.toggleViewType();
					}

					baseActivity.invalidateOptionsMenu();
				}
				return true;
			}

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		unLockUI();

		switch (requestCode)
		{
			case CODE_REQUEST_FRAGMENT_TICKET_MAIN:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
				} else if (resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
				{
					((MainActivity) baseActivity).selectMenuDrawer(((MainActivity) baseActivity).menuBookingListFragment);
				}
				break;
			}

			case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
			{
				HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
				currentFragment.onActivityResult(requestCode, resultCode, data);
				break;
			}

				// 지역을 선택한 후에 되돌아 온경우.
			case CODE_REQUEST_ACTIVITY_SELECT_AREA:
			{
				mDontReloadAtOnResume = true;

				if (resultCode == Activity.RESULT_OK)
				{
					if (data != null)
					{
						if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
						{
							Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

							onNavigationItemSelected(province);
						} else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
						{
							Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

							onNavigationItemSelected(province);
						}
					}
				}
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void refreshList(Province province, boolean isSelectionTop)
	{
		FnBTicketListFragment fnbListFragment = (FnBTicketListFragment) mFragmentViewPager.getCurrentFragment();
		fnbListFragment.refreshList(province, isSelectionTop);
	}

	protected ArrayList<AreaItem> makeAreaItemList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
	{
		ArrayList<AreaItem> arrayList = new ArrayList<AreaItem>(provinceList.size());

		for (Province province : provinceList)
		{
			AreaItem item = new AreaItem();

			item.setProvince(province);
			item.setAreaList(new ArrayList<Area>());

			if (areaList != null)
			{
				for (Area area : areaList)
				{
					if (province.getProvinceIndex() == area.getProvinceIndex())
					{
						ArrayList<Area> areaArrayList = item.getAreaList();

						if (areaArrayList.size() == 0)
						{
							Area totalArea = new Area();

							totalArea.index = -1;
							totalArea.name = province.name + " 전체";
							totalArea.setProvince(province);
							totalArea.sequence = -1;
							totalArea.tag = totalArea.name;
							totalArea.setProvinceIndex(province.getProvinceIndex());

							areaArrayList.add(totalArea);
						}

						area.setProvince(province);
						areaArrayList.add(area);
					}
				}
			}

			arrayList.add(item);
		}

		return arrayList;
	}

	protected void setOnUserActionListener(OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// NetworkActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
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

				mTodaySaleTime.setCurrentTime(response.getLong("currentDateTime"));
				mTodaySaleTime.setOpenTime(response.getLong("openDateTime"));
				mTodaySaleTime.setCloseTime(response.getLong("closeDateTime"));
				mTodaySaleTime.setDailyTime(response.getLong("dailyDateTime"));

				if (mTodaySaleTime.isSaleTime() == true)
				{
					showSlidingDrawer();

					if (baseActivity.sharedPreference.contains(KEY_PREFERENCE_BY_SHARE) == true)
					{
						String param = baseActivity.sharedPreference.getString(KEY_PREFERENCE_BY_SHARE, null);
						baseActivity.sharedPreference.edit().remove(KEY_PREFERENCE_BY_SHARE).apply();

						if (param != null)
						{
							unLockUI();

							try
							{
								String[] params = param.split("\\&|\\=");

								int hotelIndex = 0;
								int fnbIndex = 0;
								long dailyTime = 0;
								int dailyDayOfDays = 0;
								int nights = 0;

								int length = params.length;

								for (int i = 0; i < length; i++)
								{
									if ("hotelIndex".equalsIgnoreCase(params[i]) == true)
									{
										hotelIndex = Integer.valueOf(params[++i]);
									} else if ("fnbIndex".equalsIgnoreCase(params[i]) == true)
									{
										fnbIndex = Integer.valueOf(params[++i]);
									} else if ("dailyTime".equalsIgnoreCase(params[i]) == true)
									{
										dailyTime = Long.valueOf(params[++i]);
									} else if ("dailyDayOfDays".equalsIgnoreCase(params[i]) == true)
									{
										dailyDayOfDays = Integer.valueOf(params[++i]);
									} else if ("nights".equalsIgnoreCase(params[i]) == true)
									{
										nights = Integer.valueOf(params[++i]);
									}
								}

								if (mOnUserActionListener != null)
								{
									if (hotelIndex != 0)
									{
										mOnUserActionListener.selectedTicket(hotelIndex, dailyTime, dailyDayOfDays, nights);
									} else if (fnbIndex != 0)
									{
										mOnUserActionListener.selectedTicket(fnbIndex, dailyTime, dailyDayOfDays, nights);
									}
								}
							} catch (Exception e)
							{
								ExLog.d(e.toString());

								// 지역 리스트를 가져온다
								requestProvinceList(baseActivity);
							}
						}
					} else
					{
						// 지역 리스트를 가져온다
						requestProvinceList(baseActivity);
					}
				} else
				{
					hideSlidingDrawer();

					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mTodaySaleTime, TicketMainFragment.TICKET_TYPE.FNB));
					unLockUI();
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};
}
