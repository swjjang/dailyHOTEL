/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelListFragment (호텔 목록 화면)
 * <p>
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.GourmetListAdapter;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.GourmetViewItem;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

public class GourmetListFragment extends PlaceListFragment
{
    protected SaleTime mSaleTime;

    private GourmetListAdapter mGourmetListAdapter;

    // Sort
    protected SortType mPrevSortType;
    protected SortType mSortType = SortType.DEFAULT;
    private Location mMyLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_gourmet_list, container, false);

        mListView = (PinnedSectionListView) view.findViewById(R.id.listview_hotel_list);
        mListView.setTag("GourmetListFragment");

        if (Util.isOverAPI12() == true)
        {
            mListView.addHeaderView(inflater.inflate(R.layout.list_header_empty, null, true));
            mListView.setOnScrollListener(mOnScrollListener);
        } else
        {
            mListView.setPadding(0, Util.dpToPx(baseActivity, 119), 0, 0);
        }

        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        mEmptyView = view.findViewById(R.id.emptyView);

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);
        mMapLayout.setPadding(0, Util.dpToPx(baseActivity, 119) + 2, 0, 0);

        mViewType = VIEW_TYPE.LIST;

        setVisibility(mViewType);

        ActionBarPullToRefresh.from(baseActivity).options(Options.create().scrollDistance(.3f).headerTransformer(new DailyHotelHeaderTransformer()).build()).allChildrenArePullable().listener(this).useViewDelegate(AbsListView.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);

        mListView.setShadowVisible(false);

        ActionbarViewHolder actionbarViewHolder = new ActionbarViewHolder();
        actionbarViewHolder.mAnchorView = baseActivity.findViewById(R.id.anchorAnimation);
        actionbarViewHolder.mActionbarLayout = baseActivity.findViewById(R.id.actionBarLayout);
        actionbarViewHolder.mTabindicatorView = baseActivity.findViewById(R.id.tabindicator);

        setActionbarViewHolder(actionbarViewHolder);

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

        position -= mListView.getHeaderViewsCount();

        if (position < 0)
        {
            return;
        }

        if (mOnUserActionListener != null)
        {
            GourmetViewItem gourmetViewItem = (GourmetViewItem) getPlaceViewItem(position);

            if (gourmetViewItem.type == PlaceViewItem.TYPE_SECTION)
            {
                return;
            }

            mOnUserActionListener.selectPlace(gourmetViewItem, getSelectedSaleTime());
        }
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.GOURMET_LIST);
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mPlaceMapFragment != null)
        {
            mPlaceMapFragment.onActivityResult(requestCode, resultCode, data);
        } else
        {
            switch (requestCode)
            {
                case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
                    searchMyLocation();
                    break;
            }
        }
    }

    @Override
    protected PlaceViewItem getPlaceViewItem(int position)
    {
        if (mGourmetListAdapter == null)
        {
            return null;
        }

        return mGourmetListAdapter.getItem(position);
    }

    @Override
    public void onPageUnSelected()
    {
        super.onPageUnSelected();

        mSortType = SortType.DEFAULT;
    }

    protected SaleTime getSelectedSaleTime()
    {
        return mSaleTime;
    }

    @Override
    protected void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        if (checkInSaleTime == null)
        {
            return;
        }

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        lockUI();

        int stayDays = 0;

        if (checkOutSaleTime == null)
        {
            // 오늘, 내일인 경우
            stayDays = 1;
        } else
        {
            // 연박인 경우
            stayDays = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
        }

        if (stayDays <= 0)
        {
            unLockUI();
            return;
        }

        String params = null;

        if (province instanceof Area)
        {
            Area area = (Area) province;

            params = String.format("?province_idx=%d&area_idx=%d&sday=%s", area.getProvinceIndex(), area.index, checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
        } else
        {
            params = String.format("?province_idx=%d&sday=%s", province.getProvinceIndex(), checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
        }

        //        if (DEBUG == true)
        //        {
        //            baseActivity.showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
        //        }

        DailyNetworkAPI.getInstance().requestGourmetList(mNetworkTag, params, mGourmetListJsonResponseListener, baseActivity);
    }

    @Override
    protected ArrayList<PlaceViewItem> getPlaceViewItemList()
    {
        if (mGourmetListAdapter == null)
        {
            return null;
        }

        return mGourmetListAdapter.getData();
    }

    @Override
    protected PlaceMapFragment createPlaceMapFragment()
    {
        return new GourmetMapFragment();
    }

    @Override
    protected boolean hasSalesPlace()
    {
        boolean hasPlace = false;

        ArrayList<PlaceViewItem> arrayList = getPlaceViewItemList();

        if (arrayList != null)
        {
            for (PlaceViewItem placeViewItem : arrayList)
            {
                if (placeViewItem.getPlace() != null && placeViewItem.getPlace().isSoldOut == false)
                {
                    hasPlace = true;
                    break;
                }
            }
        }

        return hasPlace;
    }

    /**
     * 새로 고침을 하지 않고 기존의 있는 데이터를 보여준다.
     *
     * @param type
     * @param isCurrentPage
     */
    public void setViewType(VIEW_TYPE type, boolean isCurrentPage)
    {
        mViewType = type;

        if (mEmptyView.getVisibility() == View.VISIBLE)
        {
            setVisibility(VIEW_TYPE.GONE);
        } else
        {
            switch (type)
            {
                case LIST:
                    setVisibility(VIEW_TYPE.LIST, isCurrentPage);
                    break;

                case MAP:
                    setVisibility(VIEW_TYPE.MAP, isCurrentPage);

                    if (mPlaceMapFragment != null)
                    {
                        mPlaceMapFragment.setUserActionListener(mOnUserActionListener);

                        if (isCurrentPage == true)
                        {
                            ArrayList<PlaceViewItem> arrayList = getPlaceViewItemList();

                            if (arrayList != null)
                            {
                                mPlaceMapFragment.setPlaceViewItemList(arrayList, getSelectedSaleTime(), false);
                            }
                        }
                    }
                    break;

                case GONE:
                    break;
            }
        }
    }

    protected void setPlaceMapData(ArrayList<PlaceViewItem> placeViewItemList)
    {
        if (mViewType == VIEW_TYPE.MAP && mPlaceMapFragment != null)
        {
            mPlaceMapFragment.setUserActionListener(mOnUserActionListener);
            mPlaceMapFragment.setPlaceViewItemList(placeViewItemList, getSelectedSaleTime(), mIsSelectionTop);
        }
    }

    public void refreshList(Province province, boolean isSelectionTop)
    {
        setProvince(province);
        mIsSelectionTop = isSelectionTop;

        fetchHotelList(province, getSelectedSaleTime(), null);
    }

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    protected void showSortDialogView()
    {
        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (isLockUiComponent() == true)
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_sortdialog_layout, null, false);

        final Dialog dialog = new Dialog(baseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 버튼
        final TextView[] sortByView = new TextView[4];

        sortByView[0] = (TextView) dialogView.findViewById(R.id.sortByAreaView);
        sortByView[1] = (TextView) dialogView.findViewById(R.id.sortByDistanceView);
        sortByView[2] = (TextView) dialogView.findViewById(R.id.sortByLowPriceView);
        sortByView[3] = (TextView) dialogView.findViewById(R.id.sortByHighPriceView);

        sortByView[0].setTag(SortType.DEFAULT);
        sortByView[1].setTag(SortType.DISTANCE);
        sortByView[2].setTag(SortType.LOW_PRICE);
        sortByView[3].setTag(SortType.HIGH_PRICE);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == false)
                {
                    return;
                }

                dialog.cancel();

                mPrevSortType = mSortType;
                mSortType = (SortType) v.getTag();

                switch (mSortType)
                {
                    case DEFAULT:
                        refreshList(getProvince(), true);

                        baseActivity.invalidateOptionsMenu();
                        break;

                    case DISTANCE:
                        searchMyLocation();
                        break;

                    case LOW_PRICE:
                    case HIGH_PRICE:
                        requestSortHotelList(mSortType);

                        baseActivity.invalidateOptionsMenu();
                        break;
                }
            }
        };

        int ordinal = mSortType.ordinal();
        sortByView[ordinal].setSelected(true);
        sortByView[ordinal].setTypeface(sortByView[ordinal].getTypeface(), Typeface.BOLD);

        for (TextView textView : sortByView)
        {
            textView.setOnClickListener(onClickListener);
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        });
        dialog.setCanceledOnTouchOutside(true);

        try
        {
            dialog.setContentView(dialogView);
            dialog.show();
        } catch (Exception e)

        {
            ExLog.d(e.toString());
        }
    }

    public void setSortType(SortType sortType)
    {
        mSortType = sortType;
    }

    public SortType getSortType()
    {
        return mSortType;
    }

    private void searchMyLocation()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        lockUI();

        LocationFactory.getInstance(baseActivity).startLocationMeasure(baseActivity, null, new LocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                if (Util.isOverAPI23() == true)
                {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                }

                unLockUI();
            }

            @Override
            public void onFailed()
            {
                mSortType = mPrevSortType;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                unLockUI();

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                LocationFactory.getInstance(baseActivity).stopLocationMeasure();

                baseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                    , getString(R.string.dialog_msg_used_gps)//
                    , getString(R.string.dialog_btn_text_dosetting)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mSortType = mPrevSortType;
                    }
                }, true);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    unLockUI();
                    return;
                }

                mMyLocation = location;

                LocationFactory.getInstance(baseActivity).stopLocationMeasure();

                if (SortType.DISTANCE == mSortType)
                {
                    requestSortHotelList(mSortType);

                    baseActivity.invalidateOptionsMenu();
                }
            }
        });
    }

    private void requestSortHotelList(PlaceListFragment.SortType type)
    {
        if (SortType.DEFAULT == type)
        {
            ExLog.d("Not supported type");
            return;
        }

        ArrayList<PlaceViewItem> arrayList = mGourmetListAdapter.getData();

        int size = arrayList.size();

        for (int i = size - 1; i >= 0; i--)
        {
            PlaceViewItem placeViewItem = arrayList.get(i);

            if (placeViewItem.type == PlaceViewItem.TYPE_SECTION)
            {
                arrayList.remove(i);
            }
        }

        switch (type)
        {
            case DISTANCE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
                {
                    public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
                    {
                        Place place1 = placeViewItem1.getPlace();
                        Place place2 = placeViewItem2.getPlace();

                        float[] results1 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), place1.latitude, place1.longitude, results1);
                        ((Gourmet) place1).distance = results1[0];

                        float[] results2 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), place2.latitude, place2.longitude, results2);
                        ((Gourmet) place2).distance = results2[0];

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(arrayList, comparator);
                break;
            }

            case LOW_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
                {
                    public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
                    {
                        Place place1 = placeViewItem1.getPlace();
                        Place place2 = placeViewItem2.getPlace();

                        return place1.discountPrice - place2.discountPrice;
                    }
                };

                Collections.sort(arrayList, comparator);
                break;
            }

            case HIGH_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
                {
                    public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
                    {
                        Place place1 = placeViewItem1.getPlace();
                        Place place2 = placeViewItem2.getPlace();

                        return place2.discountPrice - place1.discountPrice;
                    }
                };

                Collections.sort(arrayList, comparator);
                break;
            }
        }

        mGourmetListAdapter.setSortType(mSortType);
        mGourmetListAdapter.notifyDataSetChanged();
        unLockUI();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        private ArrayList<PlaceViewItem> makeSectionList(ArrayList<Gourmet> fnbList)
        {
            ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<PlaceViewItem>();

            if (fnbList == null || fnbList.size() == 0)
            {
                return placeViewItemList;
            }

            String area = null;
            boolean hasDailyChoice = false;

            for (Gourmet fnb : fnbList)
            {
                String region = fnb.districtName;

                if (Util.isTextEmpty(region) == true)
                {
                    continue;
                }

                if (fnb.isDailyChoice == true)
                {
                    if (hasDailyChoice == false)
                    {
                        hasDailyChoice = true;

                        GourmetViewItem section = new GourmetViewItem(getString(R.string.label_dailychoice));
                        placeViewItemList.add(section);
                    }
                } else
                {
                    if (Util.isTextEmpty(area) == true || region.equalsIgnoreCase(area) == false)
                    {
                        area = region;

                        GourmetViewItem section = new GourmetViewItem(region);
                        placeViewItemList.add(section);
                    }
                }

                placeViewItemList.add(new GourmetViewItem(fnb));
            }

            return placeViewItemList;
        }

        private ArrayList<PlaceViewItem> makeSortHotelList(ArrayList<Gourmet> gourmetList, SortType type)
        {
            ArrayList<PlaceViewItem> gourmetViewItemList = new ArrayList<>();

            if (gourmetList == null || gourmetList.size() == 0)
            {
                return gourmetViewItemList;
            }

            switch (type)
            {
                case DEFAULT:
                    return makeSectionList(gourmetList);

                case DISTANCE:
                {
                    // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                    Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                    {
                        public int compare(Gourmet gourmet1, Gourmet gourmet2)
                        {
                            float[] results1 = new float[3];
                            Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), gourmet1.latitude, gourmet1.longitude, results1);
                            gourmet1.distance = results1[0];

                            float[] results2 = new float[3];
                            Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), gourmet2.latitude, gourmet2.longitude, results2);
                            gourmet2.distance = results2[0];

                            return Float.compare(results1[0], results2[0]);
                        }
                    };

                    Collections.sort(gourmetList, comparator);
                    break;
                }

                case LOW_PRICE:
                {
                    // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                    Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                    {
                        public int compare(Gourmet gourmet1, Gourmet gourmet2)
                        {
                            return gourmet1.discountPrice - gourmet2.discountPrice;
                        }
                    };

                    Collections.sort(gourmetList, comparator);
                    break;
                }

                case HIGH_PRICE:
                {
                    // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                    Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                    {
                        public int compare(Gourmet gourmet1, Gourmet gourmet2)
                        {
                            return gourmet2.discountPrice - gourmet1.discountPrice;
                        }
                    };

                    Collections.sort(gourmetList, comparator);
                    break;
                }
            }

            for (Gourmet gourmet : gourmetList)
            {
                gourmetViewItemList.add(new GourmetViewItem(gourmet));
            }

            return gourmetViewItemList;
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
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");
                        DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
                    }

                    throw new NullPointerException("response == null");
                }

                JSONObject dataJSONObject = response.getJSONObject("data");

                String imageUrl = dataJSONObject.getString("imgUrl");
                JSONArray gourmetJSONArray = dataJSONObject.getJSONArray("saleList");

                int length = gourmetJSONArray.length();

                if (length == 0)
                {
                    if (mGourmetListAdapter != null)
                    {
                        mGourmetListAdapter.clear();
                    }

                    setVisibility(VIEW_TYPE.GONE);

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.setMapViewVisible(false);
                    }
                } else
                {
                    JSONObject jsonObject;

                    ArrayList<Gourmet> gourmetList = new ArrayList<Gourmet>(length);

                    for (int i = 0; i < length; i++)
                    {
                        jsonObject = gourmetJSONArray.getJSONObject(i);

                        Gourmet newGourmet = new Gourmet();

                        if (newGourmet.setData(jsonObject, imageUrl) == true)
                        {
                            gourmetList.add(newGourmet); // 추가.
                        }
                    }

                    ArrayList<PlaceViewItem> placeViewItemList = makeSortHotelList(gourmetList, mSortType);

                    if (mGourmetListAdapter == null)
                    {
                        mGourmetListAdapter = new GourmetListAdapter(baseActivity, R.layout.list_row_gourmet, new ArrayList<PlaceViewItem>());
                        mListView.setAdapter(mGourmetListAdapter);
                        mListView.setOnItemClickListener(GourmetListFragment.this);
                    }

                    setVisibility(mViewType);

                    if (mViewType == VIEW_TYPE.MAP)
                    {
                        setPlaceMapData(placeViewItemList);
                    }

                    mGourmetListAdapter.clear();
                    mGourmetListAdapter.addAll(placeViewItemList, mSortType);
                    mGourmetListAdapter.notifyDataSetChanged();

                    if (mIsSelectionTop == true)
                    {
                        mListView.setSelection(0);
                    }

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.setMapViewVisible(true);
                    }
                }

                // Notify PullToRefreshLayout that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();

                // 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
                onRefreshComplete();

                setActionBarAnimationLock(false);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

}
