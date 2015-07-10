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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.HotelDaysListFragment;
import com.twoheart.dailyhotel.HotelListFragment;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.WaitTimerFragment;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.activity.SelectAreaActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;

public class HotelMainFragment extends BaseFragment
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<HotelListFragment> mFragmentList;

	private SaleTime mTodaySaleTime;
	private ArrayList<AreaItem> mAreaItemList;
	private Province mSelectedProvince;

	private boolean mMenuEnabled;
	private AlertDialog mAlertDialog;
	private boolean mDontReloadAtOnResume;

	private HOTEL_VIEW_TYPE mHotelViewType = HOTEL_VIEW_TYPE.LIST;

	public enum HOTEL_VIEW_TYPE
	{
		LIST, MAP, GONE, // 목록이 비어있는 경우.
	};

	public interface OnUserActionListener
	{
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime);

		public void selectDay(HotelListFragment fragment, boolean isListSelectionTop);

		public void toggleViewType();

		public void onClickActionBarArea();
	};

	public interface UserAnalyticsActionListener
	{
		public void selectHotel(String hotelName, long hotelIndex);

		public void selectRegion(Province province);
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
		hotelListFragment.setUserActionListener(mOnUserActionListener);
		mFragmentList.add(hotelListFragment);

		HotelListFragment hotelListFragment01 = new HotelListFragment();
		hotelListFragment01.setUserActionListener(mOnUserActionListener);
		mFragmentList.add(hotelListFragment01);

		HotelDaysListFragment hotelListFragment02 = new HotelDaysListFragment();
		hotelListFragment02.setUserActionListener(mOnUserActionListener);
		mFragmentList.add(hotelListFragment02);

		mFragmentViewPager.setData(mFragmentList);
		mFragmentViewPager.setAdapter(getChildFragmentManager());

		mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
		mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

		setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해 
		mMenuEnabled = true;

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
			switch (mHotelViewType)
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
		if (mMenuEnabled == enabled)
		{
			return;
		}

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mMenuEnabled = enabled;

		baseActivity.invalidateOptionsMenu();

		// 메뉴가 열리는 시점이다.
		HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

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
				int isInstalledGooglePlayServices = installGooglePlayService(getActivity());

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
				int isInstalledGooglePlayServices = installGooglePlayService(getActivity());

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

		releaseUiComponent();
		baseActivity.releaseUiComponent();

		switch (requestCode)
		{
			case CODE_REQUEST_ACTIVITY_HOTELTAB:
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
		if (province.name.equalsIgnoreCase(baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "")) == false)
		{
			SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, province.name);
			editor.commit();

			isSelectionTop = true;
		}

		if (mUserAnalyticsActionListener != null)
		{
			mUserAnalyticsActionListener.selectRegion(province);
		}

		refreshHotelList(province, isSelectionTop);
	}

	private void refreshHotelList(Province province, boolean isSelectionTop)
	{
		HotelListFragment hotelListFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();
		hotelListFragment.refreshHotelList(province, isSelectionTop);
	}

	private ArrayList<AreaItem> makeAreaItemList(ArrayList<Province> provinceList, ArrayList<Area> areaList)
	{
		ArrayList<AreaItem> arrayList = new ArrayList<AreaItem>(provinceList.size());

		for (Province province : provinceList)
		{
			AreaItem item = new AreaItem();

			item.setProvince(province);
			item.setAreaList(new ArrayList<Area>());

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

			arrayList.add(item);
		}

		return arrayList;
	}

	/**
	 * 
	 * @param activity
	 * @return -1 : 설치가 필요., 0 : 업데이트가 필요, 1 : 이상없음.
	 */
	private int installGooglePlayService(final Activity activity)
	{
		if (activity == null || (mAlertDialog != null && mAlertDialog.isShowing() == true))
		{
			return -1;
		}

		int state = -1;

		try
		{
			PackageManager packageManager = activity.getPackageManager();

			ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
			PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNATURES);

			if (packageInfo.versionCode < 7500000)
			{
				state = 0;
			} else
			{
				state = 1;
			}
		} catch (PackageManager.NameNotFoundException e)
		{
			state = -1;
		}

		if (state == 1)
		{
			return 1;
		} else
		{
			if (activity.isFinishing() == true)
			{
				return -1;
			}

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

			// set dialog message
			int messageId = state == -1 ? R.string.dialog_msg_install_googleplayservice : R.string.dialog_msg_update_googleplayservice;
			int positiveId = state == -1 ? R.string.dialog_btn_install : R.string.dialog_btn_update;

			mAlertDialog = alertDialogBuilder.setTitle(activity.getString(R.string.dialog_title_googleplayservice)).setMessage(activity.getString(messageId)).setCancelable(true).setPositiveButton(activity.getString(positiveId), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.dismiss();
					// Try the new HTTP method (I assume that is the official way now given that google uses it).
					try
					{
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						intent.setPackage("com.android.vending");
						activity.startActivity(intent);
					} catch (ActivityNotFoundException e)
					{
						// Ok that didn't work, try the market method.
						try
						{
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							intent.setPackage("com.android.vending");
							activity.startActivity(intent);
						} catch (ActivityNotFoundException f)
						{
							// Ok, weird. Maybe they don't have any market app. Just show the website.

							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							activity.startActivity(intent);
						}
					}
				}
			}).setNegativeButton(activity.getString(R.string.dialog_btn_text_no), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			}).create();

			mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					mAlertDialog = null;
				}
			});

			mAlertDialog.show();

			return -1;
		}
	}

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

				if (mTodaySaleTime.isSaleTime() == false)
				{
					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mTodaySaleTime));
					unLockUI();
				} else
				{
					// 지역 리스트를 가져온다
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SALE_HOTEL_ALL).toString(), null, mSaleHotelAllJsonResponseListener, baseActivity));
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mSaleHotelAllJsonResponseListener = new DailyHotelJsonResponseListener()
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

				JSONObject dataJSONObject = response.getJSONObject("data");

				JSONArray provinceArray = dataJSONObject.getJSONArray("province");
				ArrayList<Province> provinceList = makeProvinceList(provinceArray);

				JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
				ArrayList<Area> areaList = makeAreaList(areaJSONArray);

				// 마지막으로 선택한 지역을 가져온다.
				String regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
				Province selectedProvince = null;

				if (TextUtils.isEmpty(regionName) == true)
				{
					// 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
					regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "");

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

					if (selectedProvince == null)
					{
						for (Area area : areaList)
						{
							if (area.name.equals(regionName) == true)
							{
								selectedProvince = area;
								break;
							}
						}
					}
				}

				mAreaItemList = makeAreaItemList(provinceList, areaList);

				// 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
				if (selectedProvince == null)
				{
					selectedProvince = provinceList.get(0);
					regionName = selectedProvince.name;
				}

				boolean mIsProvinceSetting = baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_REGION_SETTING, false);
				SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
				editor.putBoolean(KEY_PREFERENCE_REGION_SETTING, true);
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

				editor.putString(KEY_PREFERENCE_REGION_SELECT, regionName);
				editor.commit();

				//탭에 들어갈 날짜를 만든다.
				SaleTime[] tabSaleTime = null;

				int fragmentSize = mFragmentList.size();

				tabSaleTime = new SaleTime[3];

				for (int i = 0; i < fragmentSize; i++)
				{
					HotelListFragment hotelListFragment = mFragmentList.get(i);

					SaleTime saleTime = mTodaySaleTime.getClone(i);
					tabSaleTime[i] = saleTime;

					if (hotelListFragment.getSaleTime() == null)
					{
						hotelListFragment.setSaleTime(saleTime);
					}
				}

				// 임시로 여기서 날짜를 넣는다.
				ArrayList<String> dayList = new ArrayList<String>();

				dayList.add(getString(R.string.label_format_tabday, tabSaleTime[0].getDailyDay(), tabSaleTime[0].getDailyDayOftheWeek()));
				dayList.add(getString(R.string.label_format_tabday, tabSaleTime[1].getDailyDay(), tabSaleTime[1].getDailyDayOftheWeek()));

				if (TextUtils.isEmpty(mTabIndicator.getSubText(2)) == true)
				{
					dayList.add(getString(R.string.label_format_tabday, tabSaleTime[2].getDailyDay(), tabSaleTime[2].getDailyDayOftheWeek()));
				} else
				{
					dayList.add(mTabIndicator.getSubText(2));
				}

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
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				mTabIndicator.setCurrentItem(position);

				// 현재 페이지 선택 상태를 Fragment에게 알려준다.
				HotelListFragment currentFragment = (HotelListFragment) mFragmentViewPager.getCurrentFragment();

				boolean isSelectionTop = false;
				Province province = null;

				for (HotelListFragment hotelListFragment : mFragmentList)
				{
					if (hotelListFragment == currentFragment)
					{
						province = hotelListFragment.getProvince();

						if (province == null || mSelectedProvince.index != province.index || mSelectedProvince.name.equalsIgnoreCase(province.name) == false)
						{
							isSelectionTop = true;
						}

						hotelListFragment.onPageSelected(true);
					} else
					{
						hotelListFragment.onPageUnSelected();
					}
				}

				refreshHotelList(mSelectedProvince, isSelectionTop);
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				// 릴리즈 버전에서 메모리 해지에 문제가 생기는 경우가 있어 앱을 재 시작 시킨다.
				if (DEBUG == false)
				{
					baseActivity.restartApp();
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

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{

		@Override
		public void selectHotel(HotelListViewItem hotelListViewItem, int hotelIndex, SaleTime saleTime)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();
			baseActivity.lockUiComponent();

			if (hotelListViewItem == null || hotelIndex < 0)
			{
				ExLog.d("hotelListViewItem == null || hotelIndex < 0");

				releaseUiComponent();
				baseActivity.releaseUiComponent();
				return;
			}

			switch (hotelListViewItem.getType())
			{
				case HotelListViewItem.TYPE_ENTRY:
				{
					//					Intent intent = new Intent(baseActivity, HotelTabActivity.class);
					Intent intent = new Intent(baseActivity, HotelDetailActivity.class);

					String region = baseActivity.sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");

					SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
					editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, hotelListViewItem.getItem().getName());
					editor.commit();

					intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, hotelListViewItem.getItem());
					intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
					intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, region);
					intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);

					startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);

					mUserAnalyticsActionListener.selectHotel(hotelListViewItem.getItem().getName(), hotelIndex);
					break;
				}

				case HotelListViewItem.TYPE_SECTION:
				default:
					releaseUiComponent();
					baseActivity.releaseUiComponent();
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
				String day = getString(R.string.label_format_tabday, saleTime.getDailyDay(), saleTime.getDailyDayOftheWeek());

				mTabIndicator.setSubTextEnable(2, true);
				mTabIndicator.setSubText(2, day);

				fragment.refreshHotelList(mSelectedProvince, isListSelectionTop);
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

	private UserAnalyticsActionListener mUserAnalyticsActionListener = new UserAnalyticsActionListener()
	{
		@Override
		public void selectHotel(String hotelName, long hotelIndex)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordEvent("click", "selectHotel", hotelName, hotelIndex);
		}

		@Override
		public void selectRegion(Province province)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			RenewalGaManager.getInstance(baseActivity.getApplicationContext()).recordEvent("click", "selectRegion", province.name, (long) province.index);
		}
	};
}
