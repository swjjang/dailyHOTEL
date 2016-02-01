package com.twoheart.dailyhotel.screen.gourmetlist;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.fragment.PlaceMapFragment;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
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

public class GourmetListFragment extends BaseFragment implements Constants
{
    private static final int APPBARLAYOUT_DRAG_DISTANCE = 200;

    protected PinnedSectionRecycleView mGourmetRecycleView;
    protected GourmetListAdapter mGourmetAdapter;
    protected SaleTime mSaleTime;
    private Province mSelectedProvince;

    private View mEmptyView;
    private FrameLayout mMapLayout;
    private PlaceMapFragment mPlaceMapFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Location mMyLocation;
    private List<EventBanner> mEventBannerList;

    private VIEW_TYPE mViewType;
    protected boolean mIsSelectionTop;
    protected boolean mIsSelectionTopBySort;
    protected GourmetMainFragment.OnUserActionListener mOnUserActionListener;

    // Sort
    protected Constants.SortType mPrevSortType;
    protected Constants.SortType mSortType = Constants.SortType.DEFAULT;

    private int mDownDistance;
    private int mUpDistance;

    private boolean mIsAttach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_list, container, false);

        mGourmetRecycleView = (PinnedSectionRecycleView) view.findViewById(R.id.recycleView);
        mGourmetRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGourmetRecycleView.setTag("GourmetListFragment");

        mGourmetAdapter = new GourmetListAdapter(getContext(), new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
        mGourmetRecycleView.setAdapter(mGourmetAdapter);
        mGourmetRecycleView.setOnScrollListener(mOnScrollListener);

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

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);

        mViewType = VIEW_TYPE.LIST;

        setVisibility(mViewType);

        mGourmetRecycleView.setShadowVisible(false);

        return view;
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.GOURMET_LIST);
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
        if (mViewType == VIEW_TYPE.MAP)
        {
            if (mPlaceMapFragment != null)
            {
                mPlaceMapFragment.onActivityResult(requestCode, resultCode, data);
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
        if (mViewType == VIEW_TYPE.MAP)
        {
            if (mPlaceMapFragment != null)
            {
                mPlaceMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else
        {
            if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION)
            {
                searchMyLocation();
            }
        }
    }

    public PlaceViewItem getPlaceViewItem(int position)
    {
        return mGourmetAdapter.getItem(position);
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

        if (mViewType == VIEW_TYPE.MAP)
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

    protected SaleTime getSelectedSaleTime()
    {
        return mSaleTime;
    }

    public void fetchList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
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

            params = String.format("?province_idx=%d&area_idx=%d&sday=%s", area.getProvinceIndex(), area.index, checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"));
        } else
        {
            params = String.format("?province_idx=%d&sday=%s", province.getProvinceIndex(), checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"));
        }

        if (DEBUG == true && this instanceof GourmetDaysListFragment)
        {
            baseActivity.showSimpleDialog(null, mSaleTime.toString() + "\n" + params, getString(R.string.dialog_btn_text_confirm), null);
        }

        DailyNetworkAPI.getInstance().requestGourmetList(mNetworkTag, params, mGourmetListJsonResponseListener, baseActivity);
    }

    public List<PlaceViewItem> getPlaceViewItemList()
    {
        return mGourmetAdapter.getAll();
    }

    public PlaceMapFragment createPlaceMapFragment()
    {
        return new GourmetMapFragment();
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
                        mPlaceMapFragment.setOnUserActionListener(mOnUserActionListener);

                        if (isCurrentPage == true)
                        {
                            List<PlaceViewItem> arrayList = getPlaceViewItemList();

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

    protected void setVisibility(VIEW_TYPE type, boolean isCurrentPage)
    {
        switch (type)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                // 맵과 리스트에서 당일상품 탭 안보이도록 수정

                if (mPlaceMapFragment != null)
                {
                    getChildFragmentManager().beginTransaction().remove(mPlaceMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mPlaceMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if(mOnUserActionListener != null)
                {
                    mOnUserActionListener.showAppBarLayout();
                }

                // 맵과 리스트에서 당일상품 탭 안보이도록 수정
                if (isCurrentPage == true && mPlaceMapFragment == null)
                {
                    mPlaceMapFragment = createPlaceMapFragment();
                    getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mPlaceMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    protected void setPlaceMapData(ArrayList<PlaceViewItem> placeViewItemList)
    {
        if (mViewType == VIEW_TYPE.MAP && mPlaceMapFragment != null)
        {
            mPlaceMapFragment.setOnUserActionListener(mOnUserActionListener);
            mPlaceMapFragment.setPlaceViewItemList(placeViewItemList, getSelectedSaleTime(), mIsSelectionTop);
        }
    }

    public void refreshList(Province province, boolean isSelectionTop)
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
        params.put("type", "gourmet");

        DailyNetworkAPI.getInstance().requestEventBannerList(mNetworkTag, params, mEventBannerListJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                fetchList();
            }
        });
    }

    public void fetchList()
    {
        fetchList(mSelectedProvince, getSelectedSaleTime(), null);
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
                        refreshList(getProvince(), true);

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

    protected void setVisibility(VIEW_TYPE type)
    {
        setVisibility(type, true);
    }

    public void setOnUserActionListener(GourmetMainFragment.OnUserActionListener userActionLister)
    {
        mOnUserActionListener = userActionLister;
    }

    public Province getProvince()
    {
        return mSelectedProvince;
    }

    public void setProvince(Province province)
    {
        mSelectedProvince = province;
    }

    public void setSortType(SortType sortType)
    {
        mIsSelectionTopBySort = mSortType != sortType;
        mSortType = sortType;
    }

    public SortType getSortType()
    {
        return mSortType;
    }

    public boolean hasSalesPlace()
    {
        boolean hasPlace = false;

        List<PlaceViewItem> arrayList = getPlaceViewItemList();

        if (arrayList != null)
        {
            for (PlaceViewItem placeViewItem : arrayList)
            {
                if (placeViewItem.getType() == PlaceViewItem.TYPE_ENTRY//
                    && placeViewItem.<Gourmet>getItem().isSoldOut == false)
                {
                    hasPlace = true;
                    break;
                }
            }
        }

        return hasPlace;
    }

    private void searchMyLocation()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || this.isDetached())
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
                    requestSortList(mSortType);

                    baseActivity.invalidateOptionsMenu();
                }
            }
        });
    }

    private void requestSortList(GourmetListFragment.SortType type)
    {
        if (SortType.DEFAULT == type)
        {
            ExLog.d("Not supported type");
            return;
        }

        List<PlaceViewItem> arrayList = mGourmetAdapter.getAll();

        int size = arrayList.size();

        if (size == 0)
        {
            unLockUI();
            return;
        }

        for (int i = size - 1; i >= 0; i--)
        {
            PlaceViewItem placeViewItem = arrayList.get(i);

            if (placeViewItem.getType() != PlaceViewItem.TYPE_ENTRY)
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
                        Place place1 = placeViewItem1.<Gourmet>getItem();
                        Place place2 = placeViewItem2.<Gourmet>getItem();

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
                        Place place1 = placeViewItem1.<Gourmet>getItem();
                        Place place2 = placeViewItem2.<Gourmet>getItem();

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
                        Place place1 = placeViewItem1.<Gourmet>getItem();
                        Place place2 = placeViewItem2.<Gourmet>getItem();

                        return place2.discountPrice - place1.discountPrice;
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

        mGourmetAdapter.setSortType(mSortType);
        mGourmetRecycleView.scrollToPosition(0);
        mGourmetAdapter.notifyDataSetChanged();
        unLockUI();
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

            int position = mGourmetRecycleView.getChildAdapterPosition(view);

            if (position < 0)
            {
                refreshList(mSelectedProvince, true);
                return;
            }

            if (mOnUserActionListener != null)
            {
                PlaceViewItem gourmetViewItem = mGourmetAdapter.getItem(position);

                if (gourmetViewItem.getType() == PlaceViewItem.TYPE_ENTRY)
                {
                    mOnUserActionListener.selectPlace(gourmetViewItem, getSelectedSaleTime());
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

                AnalyticsManager.getInstance(baseActivity).recordEvent("event banner", "gourmet", eventBanner.name, 0L);
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
                fetchList();
            }
        }
    };

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        private ArrayList<PlaceViewItem> makeSectionList(ArrayList<Gourmet> gourmetList)
        {
            ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<PlaceViewItem>();

            if (gourmetList == null || gourmetList.size() == 0)
            {
                return placeViewItemList;
            }

            String area = null;
            boolean hasDailyChoice = false;

            for (Gourmet gourmet : gourmetList)
            {
                String region = gourmet.districtName;

                if (Util.isTextEmpty(region) == true)
                {
                    continue;
                }

                if (gourmet.isDailyChoice == true)
                {
                    if (hasDailyChoice == false)
                    {
                        hasDailyChoice = true;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getString(R.string.label_dailychoice));
                        placeViewItemList.add(section);
                    }
                } else
                {
                    if (Util.isTextEmpty(area) == true || region.equalsIgnoreCase(area) == false)
                    {
                        area = region;

                        PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                        placeViewItemList.add(section);
                    }
                }

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
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
                gourmetViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
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
                    mGourmetAdapter.clear();
                    mGourmetAdapter.notifyDataSetChanged();

                    setVisibility(VIEW_TYPE.GONE);

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.expandedAppBar(true, true);
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

                    setVisibility(mViewType);

                    if (mViewType == VIEW_TYPE.MAP)
                    {
                        setPlaceMapData(placeViewItemList);
                    }

                    if (mSortType == SortType.DEFAULT)
                    {
                        if (mEventBannerList != null && mEventBannerList.size() > 0)
                        {
                            PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, mEventBannerList);
                            placeViewItemList.add(0, placeViewItem);
                        }
                    }

                    mGourmetAdapter.clear();
                    mGourmetAdapter.addAll(placeViewItemList, mSortType);
                    mGourmetAdapter.notifyDataSetChanged();

                    if (mIsSelectionTop == true)
                    {
                        mGourmetRecycleView.scrollToPosition(0);
                    }

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.setMapViewVisible(true);
                    }
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
