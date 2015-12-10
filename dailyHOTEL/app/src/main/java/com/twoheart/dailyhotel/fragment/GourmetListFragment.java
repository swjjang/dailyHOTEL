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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.GourmetListAdapter;
import com.twoheart.dailyhotel.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment.VIEW_TYPE;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.GourmetViewItem;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.DailyHotelHeaderTransformer;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

public class GourmetListFragment extends PlaceListFragment
{
    protected SaleTime mSaleTime;

    private PlaceListAdapter mPlaceListAdapter;

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
    protected PlaceViewItem getPlaceViewItem(int position)
    {
        if (mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getItem(position);
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

        if (DEBUG == true)
        {
            baseActivity.showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
        }

        DailyNetworkAPI.getInstance().requestGourmetList(mNetworkTag, params, mGourmetListJsonResponseListener, baseActivity);
    }

    @Override
    protected ArrayList<PlaceViewItem> getPlaceViewItemList()
    {
        if (mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getData();
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

                JSONArray jsonArray = response.getJSONArray("data");

                int length = jsonArray.length();

                if (length == 0)
                {
                    if (mPlaceListAdapter != null)
                    {
                        mPlaceListAdapter.clear();
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
                        jsonObject = jsonArray.getJSONObject(i);

                        Gourmet newGourmet = new Gourmet();

                        if (newGourmet.setData(jsonObject) == true)
                        {
                            gourmetList.add(newGourmet); // 추가.
                        }
                    }

                    ArrayList<PlaceViewItem> placeViewItemList = makeSectionList(gourmetList);

                    if (mPlaceListAdapter == null)
                    {
                        mPlaceListAdapter = new GourmetListAdapter(baseActivity, R.layout.list_row_gourmet, new ArrayList<PlaceViewItem>());
                        mListView.setAdapter(mPlaceListAdapter);
                        mListView.setOnItemClickListener(GourmetListFragment.this);
                    }

                    setVisibility(mViewType);

                    if (mViewType == VIEW_TYPE.MAP)
                    {
                        setPlaceMapData(placeViewItemList);
                    }

                    mPlaceListAdapter.clear();
                    mPlaceListAdapter.addAll(placeViewItemList);
                    mPlaceListAdapter.notifyDataSetChanged();

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
