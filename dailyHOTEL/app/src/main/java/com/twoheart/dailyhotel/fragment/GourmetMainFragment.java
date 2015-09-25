/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * HotelTabBookingFragment (호텔 예약 탭)
 * <p/>
 * 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.GourmetDetailActivity;
import com.twoheart.dailyhotel.activity.SelectAreaActivity;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GourmetMainFragment extends PlaceMainFragment
{
    private ArrayList<PlaceListFragment> mFragmentList;
    private ArrayList<AreaItem> mAreaItemList;
    private Province mSelectedProvince;

    private FragmentViewPager mFragmentViewPager;
    private View mHeaderSectionLayout;

    private interface OnUserAnalyticsActionListener
    {
        public void selectPlace(String name, long index, String checkInTime);

        public void selectRegion(Province province);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_main, container, false);

        mHeaderSectionLayout = view.findViewById(R.id.headerSectionBar);
        mHeaderSectionLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        mFragmentViewPager = (FragmentViewPager) view.findViewById(R.id.fragmentViewPager);
        mFragmentList = new ArrayList<PlaceListFragment>();

        setOnUserActionListener(mOnFnBUserActionListener);

        GourmetListFragment fnbListFragment = new GourmetListFragment();
        fnbListFragment.setUserActionListener(mOnFnBUserActionListener);
        mFragmentList.add(fnbListFragment);

        mFragmentViewPager.setData(mFragmentList);
        mFragmentViewPager.setAdapter(getChildFragmentManager());

        return view;
    }

    @Override
    protected void showSlidingDrawer()
    {
        setMenuEnabled(true);
    }

    @Override
    protected void hideSlidingDrawer()
    {
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

        boolean isShowSpinner = mAreaItemList != null && mAreaItemList.size() > 1 ? true : false;
        baseActivity.setActionBarRegionEnable(isShowSpinner);

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

        if (mOnUserAnalyticsActionListener != null)
        {
            mOnUserAnalyticsActionListener.selectRegion(province);
        }

        refreshList(province, isSelectionTop);
    }

    @Override
    protected void requestProvinceList(BaseActivity baseActivity)
    {
        // 지역 리스트를 가져온다
        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_REGION_PROVINCE_LIST).toString(), null, mProvinceListJsonResponseListener, baseActivity));
    }

    @Override
    protected void refreshList(Province province, boolean isSelectionTop)
    {
        GourmetListFragment fnbListFragment = (GourmetListFragment) mFragmentViewPager.getCurrentFragment();
        fnbListFragment.refreshList(province, isSelectionTop);
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();
                currentFragment.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }

    @Override
    protected void setActionBarAnimationLock(boolean isLock)
    {
        PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();

        if (currentFragment != null)
        {
            if (isLock == true)
            {
                currentFragment.setActionBarAnimationLock(false);
            } else
            {
                currentFragment.showActionBarAnimatoin();
                currentFragment.setActionBarAnimationLock(true);
            }
        }
    }

    @Override
    protected boolean isEnabledRegionMenu()
    {
        if (mAreaItemList != null && mAreaItemList.size() > 1)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    protected void showClosedDaily(SaleTime saleTime)
    {
        MainActivity baseActivity = (MainActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.replaceFragment(WaitTimerFragment.newInstance(saleTime, PlaceMainFragment.TYPE.FNB));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserAnalyticsActionListener mOnUserAnalyticsActionListener = new OnUserAnalyticsActionListener()
    {
        @Override
        public void selectPlace(String name, long index, String checkInTime)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.FNB_INDEX, String.valueOf(index));
            params.put(Label.CHECK_IN, checkInTime);

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mViewType.name(), Action.CLICK, name, params);
        }

        @Override
        public void selectRegion(Province province)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            HashMap<String, String> params = new HashMap<String, String>();

            AnalyticsManager.getInstance(baseActivity.getApplicationContext()).recordEvent(mViewType.name(), Action.CLICK, province.name, params);
        }
    };

    ;
    private OnUserActionListener mOnFnBUserActionListener = new OnUserActionListener()
    {
        @Override
        public void selectPlace(PlaceViewItem baseListViewItem, SaleTime checkSaleTime)
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

            lockUI();

            if (baseListViewItem == null)
            {
                unLockUI();
                return;
            }

            switch (baseListViewItem.type)
            {
                case PlaceViewItem.TYPE_ENTRY:
                {
                    Gourmet fnb = (Gourmet) baseListViewItem.getPlace();

                    String region = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT, "");
                    SharedPreferences.Editor editor = baseActivity.sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_PLACE_REGION_SELECT_GA, region);
                    editor.putString(KEY_PREFERENCE_PLACE_NAME_GA, fnb.name);
                    editor.commit();

                    Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkSaleTime);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, fnb.index);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, fnb.name);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, fnb.imageUrl);

                    startActivityForResult(intent, CODE_REQUEST_FRAGMENT_PLACE_MAIN);

                    mOnUserAnalyticsActionListener.selectPlace(fnb.name, fnb.index, checkSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
                    break;
                }

                case PlaceViewItem.TYPE_SECTION:
                default:
                    unLockUI();
                    break;
            }
        }

        @Override
        public void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || index < 0)
            {
                return;
            }

            if (isLockUiComponent() == true || baseActivity.isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            Intent intent = new Intent(baseActivity, GourmetDetailActivity.class);

            intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, index);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

            startActivityForResult(intent, CODE_REQUEST_FRAGMENT_PLACE_MAIN);
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

                default:
                    break;
            }

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            PlaceListFragment currentFragment = (PlaceListFragment) mFragmentViewPager.getCurrentFragment();

            for (PlaceListFragment fnbListFragment : mFragmentList)
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

            if (mAreaItemList == null || mAreaItemList.size() == 1)
            {
                return;
            }

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            Intent intent = new Intent(baseActivity, SelectAreaActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mSelectedProvince);
            intent.putParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREAITEMLIST, mAreaItemList);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SELECT_AREA);
        }

        @Override
        public void setHeaderSectionVisible(boolean isVisible)
        {
            if (mHeaderSectionLayout == null)
            {
                return;
            }

            if (isVisible == true)
            {
                if (mHeaderSectionLayout.getVisibility() != View.VISIBLE)
                {
                    mHeaderSectionLayout.setVisibility(View.VISIBLE);
                }
            } else
            {
                if (mHeaderSectionLayout.getVisibility() != View.INVISIBLE)
                {
                    mHeaderSectionLayout.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void setMapViewVisible(boolean isVisible)
        {
            setMenuEnabled(isVisible);
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

                if (Util.isTextEmpty(regionName) == true)
                {
                    // 마지막으로 선택한 지역이 없는 경이 이전 지역을 가져온다.
                    regionName = baseActivity.sharedPreference.getString(KEY_PREFERENCE_FNB_REGION_SELECT_BEFORE, "");

                    // 해당 지역이 없는 경우 Province의 첫번째 지역으로 한다.
                    if (Util.isTextEmpty(regionName) == true)
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
                    PlaceListFragment placeListFragment = mFragmentList.get(i);

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

                    if (placeListFragment.getSaleTime() == null)
                    {
                        placeListFragment.setSaleTime(saleTime);
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
}
