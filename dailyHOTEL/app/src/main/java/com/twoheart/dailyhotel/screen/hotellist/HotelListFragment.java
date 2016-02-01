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
package com.twoheart.dailyhotel.screen.hotellist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecycleView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelListFragment extends BaseFragment implements Constants
{
    private static final int APPBARLAYOUT_DRAG_DISTANCE = 200;

    protected PinnedSectionRecycleView mHotelRecycleView;
    protected HotelListAdapter mHotelAdapter;
    protected SaleTime mSaleTime;
    protected Province mSelectedProvince;
    protected Category mSelectedCategory;

    private View mEmptyView;
    private ViewGroup mMapLayout;
    private HotelMapFragment mHotelMapFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Location mMyLocation;
    private List<EventBanner> mEventBannerList;

    private HotelMainFragment.HOTEL_VIEW_TYPE mHotelViewType;
    protected boolean mIsSelectionTop;
    protected boolean mIsSelectionTopBySort;
    protected HotelMainFragment.OnUserActionListener mOnUserActionListener;

    // Sort
    protected Constants.SortType mPrevSortType;
    protected Constants.SortType mSortType = Constants.SortType.DEFAULT;

    private int mDownDistance;
    private int mUpDistance;

    private boolean mIsAttach;

    protected List<Hotel> mHotelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);

        mHotelRecycleView = (PinnedSectionRecycleView) view.findViewById(R.id.recycleView);
        mHotelRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHotelRecycleView.setTag("HotelListFragment");

        mHotelAdapter = new HotelListAdapter(getContext(), new ArrayList<PlaceViewItem>(), getOnItemClickListener(), mOnEventBannerItemClickListener);
        mHotelRecycleView.setAdapter(mHotelAdapter);
        mHotelRecycleView.setOnScrollListener(mOnScrollListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.refreshAll(false);
                } else
                {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mEmptyView = view.findViewById(R.id.emptyView);

        mMapLayout = (ViewGroup) view.findViewById(R.id.hotelMapLayout);

        mHotelViewType = HotelMainFragment.HOTEL_VIEW_TYPE.LIST;

        setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.LIST);

        mHotelRecycleView.setShadowVisible(false);

        return view;
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.HOTEL_LIST);
        super.onStart();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mIsAttach = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mHotelViewType == HotelMainFragment.HOTEL_VIEW_TYPE.MAP)
        {
            if (mHotelMapFragment != null)
            {
                mHotelMapFragment.onActivityResult(requestCode, resultCode, data);
            }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mHotelViewType == HotelMainFragment.HOTEL_VIEW_TYPE.MAP)
        {
            if (mHotelMapFragment != null)
            {
                mHotelMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else
        {
            if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION)
            {
                searchMyLocation();
            }
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
        if (detailRegion != null && mHotelViewType == HotelMainFragment.HOTEL_VIEW_TYPE.MAP)
        {
            refreshHotelList(mSelectedProvince, true);
        }
    }

    public void onPageSelected(boolean isRequestHotelList)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.invalidateOptionsMenu();
    }

    public void onPageUnSelected()
    {
    }

    public void onRefreshComplete()
    {
        mSwipeRefreshLayout.setRefreshing(false);

        if (mHotelViewType == HotelMainFragment.HOTEL_VIEW_TYPE.MAP)
        {
            return;
        }

        Object objectTag = mSwipeRefreshLayout.getTag();

        if (objectTag == null)
        {
            mSwipeRefreshLayout.setTag(mSwipeRefreshLayout.getId());

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    mSwipeRefreshLayout.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });

            mSwipeRefreshLayout.startAnimation(animation);
        }
    }

    /**
     * 새로 고침을 하지 않고 기존의 있는 데이터를 보여준다.
     *
     * @param type
     * @param isCurrentPage
     */
    public void setHotelViewType(HotelMainFragment.HOTEL_VIEW_TYPE type, boolean isCurrentPage)
    {
        mHotelViewType = type;

        if (mEmptyView.getVisibility() == View.VISIBLE)
        {
            setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.GONE);
        } else
        {
            switch (mHotelViewType)
            {
                case LIST:
                    setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.LIST, isCurrentPage);
                    break;

                case MAP:
                    setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.MAP, isCurrentPage);

                    if (mHotelMapFragment != null)
                    {
                        mHotelMapFragment.setUserActionListener(mOnUserActionListener);

                        if (isCurrentPage == true)
                        {
                            if (HotelListFragment.this instanceof HotelDaysListFragment)
                            {
                                mHotelMapFragment.setHotelList(mHotelAdapter.getAll(), ((HotelDaysListFragment) HotelListFragment.this).getSelectedCheckInSaleTime(), false);
                            } else
                            {
                                mHotelMapFragment.setHotelList(mHotelAdapter.getAll(), mSaleTime, false);
                            }
                        }
                    }
                    break;

                case GONE:
                    break;
            }
        }
    }

    private void setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE type, boolean isCurrentPage)
    {
        switch (type)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mHotelMapFragment != null)
                {
                    getChildFragmentManager().beginTransaction().remove(mHotelMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mHotelMapFragment = null;
                }

                //				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
                //				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_map_mini);

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.showAppBarLayout();
                }

                if (isCurrentPage == true && mHotelMapFragment == null)
                {
                    mHotelMapFragment = new HotelMapFragment();
                    getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mHotelMapFragment).commitAllowingStateLoss();
                }

                //				mDailyFloatingActionButton.setVisibility(View.VISIBLE);
                //				mDailyFloatingActionButton.setImageResource(R.drawable.img_ic_list_mini);
                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                //				mDailyFloatingActionButton.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE type)
    {
        setVisibility(type, true);
    }

    public SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public View.OnClickListener getOnItemClickListener()
    {
        return mOnItemClickListener;
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    public void setSelectedCategory(Category category)
    {
        mSelectedCategory = category;
    }

    public void setOnUserActionListener(HotelMainFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void refreshHotelList(Province province, boolean isSelectionTop)
    {
        mSelectedProvince = province;

        if (mIsSelectionTopBySort == true)
        {
            mIsSelectionTop = true;
            mIsSelectionTopBySort = false;
        } else
        {
            mIsSelectionTop = isSelectionTop;
        }

        Map<String, String> params = new HashMap<>();
        params.put("type", "hotel");

        DailyNetworkAPI.getInstance().requestEventBannerList(mNetworkTag, params, mEventBannerListJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                fetchHotelList();
            }
        });
    }

    /**
     * 이벤트 리스트를 얻어오는 API가 생겨서 어쩔수 없이 상속구조로 바꿈
     */
    protected void fetchHotelList()
    {
        fetchHotelList(mSelectedProvince, mSaleTime, null);
    }

    /**
     * @param province
     * @param checkInSaleTime
     * @param checkOutSaleTime
     */
    protected void fetchHotelList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (province == null || checkInSaleTime == null)
        {
            Util.restartApp(baseActivity);
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

            params = String.format("?province_idx=%d&area_idx=%d&checkin_date=%s&length_stay=%d", area.getProvinceIndex(), area.index, checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"), stayDays);
        } else
        {
            params = String.format("?province_idx=%d&checkin_date=%s&length_stay=%d", province.getProvinceIndex(), checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"), stayDays);
        }

        if (DEBUG == true && this instanceof HotelDaysListFragment)
        {
            baseActivity.showSimpleDialog(null, mSaleTime.toString() + "\n" + params, getString(R.string.dialog_btn_text_confirm), null);
        }

        DailyNetworkAPI.getInstance().requestHotelList(mNetworkTag, params, mHotelListJsonResponseListener, baseActivity);
    }

    public Province getProvince()
    {
        return mSelectedProvince;
    }

    public void setProvince(Province province)
    {
        mSelectedProvince = province;
    }

    protected void showSortDialogView()
    {
        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true || mIsAttach == false)
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

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.selectSortType(mSortType);
                }

                switch (mSortType)
                {
                    case DEFAULT:
                        refreshHotelList(mSelectedProvince, true);

                        baseActivity.invalidateOptionsMenu();
                        break;

                    case DISTANCE:
                        searchMyLocation();
                        break;

                    case LOW_PRICE:
                    case HIGH_PRICE:
                        requestSortList(mSortType);

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
        // 기존 타입과 sortType이 다르면
        mIsSelectionTopBySort = mSortType != sortType;

        mSortType = sortType;
    }

    public void setLocation(Location location)
    {
        mMyLocation = location;
    }

    public SortType getSortType()
    {
        return mSortType;
    }

    private void searchMyLocation()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || isLockUiComponent() == true)
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
                unLockUI();

                mSortType = mPrevSortType;

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.selectSortType(mSortType);
                }

                if (Util.isOverAPI23() == true)
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null || baseActivity.isFinishing() == true)
                    {
                        return;
                    }

                    baseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps_android6)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mSortType = mPrevSortType;

                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.selectSortType(mSortType);
                            }
                        }
                    }, true);
                }
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

                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.selectSortType(mSortType);
                        }
                    }
                }, false);
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
                    requestSortList(mSortType);

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.setLocation(location);
                    }

                    baseActivity.invalidateOptionsMenu();
                }
            }
        });
    }

    private void requestSortList(SortType type)
    {
        if (SortType.DEFAULT == type)
        {
            ExLog.d("Not supported type");
            return;
        }

        List<PlaceViewItem> arrayList = mHotelAdapter.getAll();

        int size = arrayList.size();

        if (size == 0)
        {
            unLockUI();
            return;
        }

        for (int i = size - 1; i >= 0; i--)
        {
            PlaceViewItem hotelListViewItem = arrayList.get(i);

            if (hotelListViewItem.getType() != PlaceViewItem.TYPE_ENTRY)
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
                        Hotel hotel1 = placeViewItem1.<Hotel>getItem();
                        Hotel hotel2 = placeViewItem2.<Hotel>getItem();

                        float[] results1 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), hotel1.latitude, hotel1.longitude, results1);
                        hotel1.distance = results1[0];

                        float[] results2 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), hotel2.latitude, hotel2.longitude, results2);
                        hotel2.distance = results2[0];

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
                        Hotel hotel1 = placeViewItem1.<Hotel>getItem();
                        Hotel hotel2 = placeViewItem2.<Hotel>getItem();

                        return hotel1.averageDiscount - hotel2.averageDiscount;
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
                        Hotel hotel1 = placeViewItem1.<Hotel>getItem();
                        Hotel hotel2 = placeViewItem2.<Hotel>getItem();

                        return hotel2.averageDiscount - hotel1.averageDiscount;
                    }
                };

                Collections.sort(arrayList, comparator);
                break;
            }
        }

        if (mOnUserActionListener != null)
        {
            mOnUserActionListener.expandedAppBar(true, true);
        }

        mHotelAdapter.setSortType(mSortType);
        mHotelRecycleView.scrollToPosition(0);
        mHotelAdapter.notifyDataSetChanged();
        unLockUI();
    }

    private ArrayList<PlaceViewItem> makeSortHotelList(List<Hotel> hotelList, SortType type)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (hotelList == null || hotelList.size() == 0)
        {
            return hotelListViewItemList;
        }

        switch (type)
        {
            case DEFAULT:
                return makeSectionHotelList(hotelList);

            case DISTANCE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), hotel1.latitude, hotel1.longitude, results1);
                        hotel1.distance = results1[0];

                        float[] results2 = new float[3];
                        Location.distanceBetween(mMyLocation.getLatitude(), mMyLocation.getLongitude(), hotel2.latitude, hotel2.longitude, results2);
                        hotel2.distance = results2[0];

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }

            case LOW_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        return hotel1.averageDiscount - hotel2.averageDiscount;
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }

            case HIGH_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        return hotel2.averageDiscount - hotel1.averageDiscount;
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }
        }

        for (Hotel hotel : hotelList)
        {
            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel));
        }

        return hotelListViewItemList;
    }

    private ArrayList<PlaceViewItem> makeSectionHotelList(List<Hotel> hotelList)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (hotelList == null || hotelList.size() == 0)
        {
            return hotelListViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Hotel hotel : hotelList)
        {
            String region = hotel.getDetailRegion();

            if (Util.isTextEmpty(region) == true)
            {
                continue;
            }

            if (hotel.isDailyChoice == true)
            {
                if (hasDailyChoice == false)
                {
                    hasDailyChoice = true;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getString(R.string.label_dailychoice));
                    hotelListViewItemList.add(section);
                }
            } else
            {
                if (Util.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                {
                    previousRegion = region;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                    hotelListViewItemList.add(section);
                }
            }

            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel));
        }

        return hotelListViewItemList;
    }

    public void resetScrollDistance(boolean isUpDistance)
    {
        if (isUpDistance == true)
        {
            mDownDistance = 1;
            mUpDistance = 0;
        } else
        {
            mUpDistance = -1;
            mDownDistance = 0;
        }
    }

    private List<Hotel> filteringCategory(List<Hotel> list, Category category)
    {
        if (category == null || Category.ALL.code.equalsIgnoreCase(category.code) == true)
        {
            return list;
        }

        List<Hotel> filteredCategoryList = new ArrayList<>(50);

        for (Hotel hotel : list)
        {
            if (category.code.equalsIgnoreCase(hotel.categoryCode) == true)
            {
                filteredCategoryList.add(hotel);
            }
        }

        return filteredCategoryList;
    }

    public void requestFilteringCategory()
    {
        requestFilteringCategory(mHotelList, mSelectedCategory, mSortType);
    }

    private void requestFilteringCategory(List<Hotel> list, Category category, SortType sortType)
    {
        List<Hotel> hotelList = filteringCategory(list, category);

        ArrayList<PlaceViewItem> hotelListViewItemList = makeSortHotelList(filteringCategory(hotelList, mSelectedCategory), sortType);

        setVisibility(mHotelViewType);

        // 지역이 변경되면 다시 리스트를 받아오는데 어떻게 해야할지 의문.
        if (mHotelViewType == HotelMainFragment.HOTEL_VIEW_TYPE.MAP)
        {
            mHotelMapFragment.setUserActionListener(mOnUserActionListener);

            if (HotelListFragment.this instanceof HotelDaysListFragment)
            {
                mHotelMapFragment.setHotelList(hotelListViewItemList, ((HotelDaysListFragment) HotelListFragment.this).getSelectedCheckInSaleTime(), mIsSelectionTop);
            } else
            {
                mHotelMapFragment.setHotelList(hotelListViewItemList, mSaleTime, mIsSelectionTop);
            }
        }

        mHotelAdapter.clear();

        if (hotelListViewItemList.size() == 0)
        {
            mHotelAdapter.notifyDataSetChanged();

            setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.GONE);

            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.expandedAppBar(true, true);
                mOnUserActionListener.setMapViewVisible(false);
            }
        } else
        {
            if (sortType == SortType.DEFAULT)
            {
                if (mEventBannerList != null && mEventBannerList.size() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, mEventBannerList);
                    hotelListViewItemList.add(0, placeViewItem);
                }
            }

            mHotelAdapter.addAll(hotelListViewItemList, sortType);
            mHotelAdapter.notifyDataSetChanged();

            if (mIsSelectionTop == true)
            {
                mHotelRecycleView.scrollToPosition(0);
            }

            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.setMapViewVisible(true);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            super.onScrolled(recyclerView, dx, dy);

            if (dy < 0)
            {
                if (mDownDistance == 1)
                {
                    return;
                }

                mDownDistance += dy;

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (-mDownDistance >= Util.dpToPx(baseActivity, APPBARLAYOUT_DRAG_DISTANCE))
                {
                    if (mOnUserActionListener != null)
                    {
                        mUpDistance = 0;
                        mDownDistance = 1;
                        mOnUserActionListener.showAppBarLayout();
                    }
                }
            } else if (dy > 0)
            {
                if (mUpDistance == -1)
                {
                    return;
                }

                mUpDistance += dy;

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (mUpDistance >= Util.dpToPx(baseActivity, APPBARLAYOUT_DRAG_DISTANCE))
                {
                    if (mOnUserActionListener != null)
                    {
                        mDownDistance = 0;
                        mUpDistance = -1;
                        mOnUserActionListener.showAppBarLayout();
                        mOnUserActionListener.expandedAppBar(false, true);
                    }
                }
            }
        }
    };

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            int position = mHotelRecycleView.getChildAdapterPosition(view);

            if (position < 0)
            {
                refreshHotelList(mSelectedProvince, true);
                return;
            }

            if (mOnUserActionListener != null)
            {
                PlaceViewItem placeViewItem = mHotelAdapter.getItem(position);

                if (placeViewItem.getType() == PlaceViewItem.TYPE_ENTRY)
                {
                    mOnUserActionListener.selectHotel(placeViewItem, mSaleTime);
                }
            }
        }
    };

    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Integer index = (Integer) view.getTag(view.getId());

            if (index != null)
            {
                EventBanner eventBanner = mEventBannerList.get(index.intValue());

                mOnUserActionListener.selectEventBanner(eventBanner);

                AnalyticsManager.getInstance(baseActivity).recordEvent("event banner", "hotel", eventBanner.name, 0L);
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mEventBannerListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String baseUrl = dataJSONObject.getString("imgUrl");

                    JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");

                    if (mEventBannerList == null)
                    {
                        mEventBannerList = new ArrayList<>();
                    }

                    mEventBannerList.clear();

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            EventBanner eventBanner = new EventBanner(jsonArray.getJSONObject(i), baseUrl);
                            mEventBannerList.add(eventBanner);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            } finally
            {
                fetchHotelList();
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                int msgCode = response.getInt("msg_code");

                if (msgCode != 0)
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
                int nights = dataJSONObject.getInt("nights");
                JSONArray hotelJSONArray = dataJSONObject.getJSONArray("saleList");

                int length = hotelJSONArray.length();

                mHotelList.clear();

                if (length == 0)
                {
                    mHotelAdapter.clear();
                    mHotelAdapter.notifyDataSetChanged();

                    setVisibility(HotelMainFragment.HOTEL_VIEW_TYPE.GONE);

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.expandedAppBar(true, true);
                        mOnUserActionListener.setMapViewVisible(false);
                    }
                } else
                {
                    JSONObject jsonObject;

                    ArrayList<Hotel> hotelList = new ArrayList<Hotel>(length);

                    for (int i = 0; i < length; i++)
                    {
                        jsonObject = hotelJSONArray.getJSONObject(i);

                        Hotel newHotel = new Hotel();

                        if (newHotel.setHotel(jsonObject, imageUrl, nights) == true)
                        {
                            hotelList.add(newHotel); // 추가.
                        }
                    }

                    // 기본적으로 보관한다.
                    mHotelList.addAll(hotelList);

                    // section 및 HotelListViewItem 으로 바꾸어 주기.
                    // 거리순인데 위치 정보가 없는 경우.
                    if (mMyLocation == null && mSortType == SortType.DISTANCE)
                    {
                        mSortType = SortType.DEFAULT;

                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.selectSortType(mSortType);
                        }
                    }

                    requestFilteringCategory(hotelList, mSelectedCategory, mSortType);
                }

                // 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
                onRefreshComplete();
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
