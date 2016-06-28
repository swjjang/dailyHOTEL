/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * HotelListFragment (호텔 목록 화면)
 * <p/>
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
package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StayListLayout extends BaseLayout implements View.OnClickListener
{
    protected PinnedSectionRecyclerView mStayRecyclerView;
    protected StayListAdapter mStayListAdapter;

    private View mEmptyView;
    private ViewGroup mMapLayout;
    private StayMapFragment mStayMapFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Constants.ViewType mViewType;
    protected boolean mScrollListTop;
    protected HotelMainFragment.OnCommunicateListener mOnCommunicateListener;

    protected List<Stay> mStayList = new ArrayList<>();

    public interface OnEventListener extends OnBaseEventListener
    {
        void onStayClick(PlaceViewItem placeViewItem, SaleTime checkInSaleTime);

        void onEventBannerClick(EventBanner eventBanner);

        void onRefreshAll(boolean isShowProgress);
    }

    public StayListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        mStayRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);
        mStayRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        EdgeEffectColor.setEdgeGlowColor(mStayRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mStayListAdapter = new StayListAdapter(mContext, new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
        mStayRecyclerView.setAdapter(mStayListAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if (mOnCommunicateListener == null)
                {
                    return;
                }

                mOnCommunicateListener.refreshAll(false);
            }
        });

        mEmptyView = view.findViewById(R.id.emptyLayout);

        mMapLayout = (ViewGroup) view.findViewById(R.id.mapLayout);

        mViewType = Constants.ViewType.LIST;

        mStayRecyclerView.setShadowVisible(false);
    }

    public boolean canScrollUp()
    {
        if (mSwipeRefreshLayout != null)
        {
            return mSwipeRefreshLayout.canChildScrollUp();
        }

        return true;
    }

    public void onPageSelected(String tabText)
    {

    }

    public void onPageUnSelected()
    {
    }

    public void onRefreshComplete()
    {
        //        mOnCommunicateListener.refreshCompleted();
        //
        //        mSwipeRefreshLayout.setRefreshing(false);
        //
        //        if (mViewType == ViewType.MAP)
        //        {
        //            mSwipeRefreshLayout.setTag(mSwipeRefreshLayout.getId());
        //            mOnCommunicateListener.showFloatingActionButton();
        //        } else
        //        {
        //            Object objectTag = mSwipeRefreshLayout.getTag();
        //
        //            if (objectTag == null)
        //            {
        //                mSwipeRefreshLayout.setTag(mSwipeRefreshLayout.getId());
        //
        //                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        //                animation.setDuration(300);
        //                animation.setAnimationListener(new Animation.AnimationListener()
        //                {
        //                    @Override
        //                    public void onAnimationStart(Animation animation)
        //                    {
        //                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        //                    }
        //
        //                    @Override
        //                    public void onAnimationEnd(Animation animation)
        //                    {
        //                        mSwipeRefreshLayout.setAnimation(null);
        //                        mOnCommunicateListener.showFloatingActionButton();
        //                    }
        //
        //                    @Override
        //                    public void onAnimationRepeat(Animation animation)
        //                    {
        //
        //                    }
        //                });
        //
        //                mSwipeRefreshLayout.startAnimation(animation);
        //            } else
        //            {
        //                mOnCommunicateListener.showFloatingActionButton();
        //            }
        //        }
    }

    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mViewType = Constants.ViewType.LIST;

                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mStayMapFragment != null)
                {
                    fragmentManager.beginTransaction().remove(mStayMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mStayMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mViewType = Constants.ViewType.MAP;

                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mStayMapFragment == null)
                {
                    mStayMapFragment = new StayMapFragment();
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mStayMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY);
                break;
        }
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mStayListAdapter.getAll());
    }

    private boolean hasSalesPlace(List<PlaceViewItem> placeViewItemList)
    {
        boolean hasSalesPlace = false;

        if (placeViewItemList != null)
        {
            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                    && placeViewItem.<Gourmet>getItem().isSoldOut == false)
                {
                    hasSalesPlace = true;
                    break;
                }
            }
        }

        return hasSalesPlace;
    }

    public void clear()
    {
        mStayListAdapter.clear();
    }

    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                        ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        if (mStayListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        mStayListAdapter.clear();

        if (list == null || list.size() == 0)
        {
            mStayListAdapter.notifyDataSetChanged();

            setVisibility(fragmentManager, Constants.ViewType.GONE, true);

        } else
        {
            setVisibility(fragmentManager, viewType, true);

            if (viewType == Constants.ViewType.MAP)
            {
                mStayMapFragment.setOnCommunicateListener(mOnCommunicateListener);
                mStayMapFragment.setHotelViewItemList(list, StayCurationManager.getInstance().getCheckInSaleTime(), mScrollListTop);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP);
            } else
            {
                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST);

                Map<String, String> params = new HashMap<>();
                Province province = StayCurationManager.getInstance().getProvince();

                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST, params);
            }

            if (sortType == Constants.SortType.DEFAULT)
            {
                if (StayEventBannerManager.getInstance().getCount() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
                        StayEventBannerManager.getInstance().getList());
                    list.add(0, placeViewItem);
                }
            }

            mStayListAdapter.addAll(list, sortType);
            mStayListAdapter.notifyDataSetChanged();

            if (mScrollListTop == true)
            {
                mScrollListTop = false;
                mStayRecyclerView.scrollToPosition(0);
            }
        }
    }

    //    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //    //
    //    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v)
    {

    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

            int position = mStayRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onStayClick(null, checkInSaleTime);
                return;
            }

            PlaceViewItem placeViewItem = mStayListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onStayClick(placeViewItem, checkInSaleTime);
            }
        }
    };

    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Integer index = (Integer) view.getTag(view.getId());
            if (index != null)
            {
                EventBanner eventBanner = StayEventBannerManager.getInstance().getEventBanner(index);

                ((OnEventListener) mOnEventListener).onEventBannerClick(eventBanner);
            }
        }
    };
}
