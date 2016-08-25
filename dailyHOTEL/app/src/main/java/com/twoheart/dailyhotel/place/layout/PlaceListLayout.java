package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceListLayout extends BaseLayout
{
    public static final int LOAD_MORE_POSITION_GAP = Constants.PAGENATION_LIST_SIZE / 3;

    private boolean mBannerVisibility;
    protected boolean mIsLoading;

    protected View mEmptyView;
    protected View mFilterEmptyView;
    protected View mBottomOptionLayout;

    protected ViewGroup mMapLayout;

    protected PlaceListMapFragment mPlaceListMapFragment;

    protected LinearLayoutManager mLayoutManager;

    protected PlaceListAdapter mPlaceListAdapter;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected PinnedSectionRecyclerView mPlaceRecyclerView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onPlaceClick(PlaceViewItem placeViewItem);

        void onEventBannerClick(EventBanner eventBanner);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onRefreshAll(boolean isShowProgress);

        void onLoadMoreList();

        void onFilterClick();

        void onShowActivityEmptyView(boolean isShow);

        void onRecordAnalytics(Constants.ViewType viewType);
    }

    protected abstract PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList);

    public abstract void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage);

    protected abstract EventBanner getEventBanner(int index);

    protected abstract PlaceViewItem getEventBannerViewItem();

    protected abstract void onInformationClick(PlaceViewItem placeViewItem);

    public PlaceListLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        mPlaceRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mPlaceRecyclerView.setLayoutManager(mLayoutManager);
        EdgeEffectColor.setEdgeGlowColor(mPlaceRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mPlaceListAdapter = getPlaceListAdapter(mContext, new ArrayList<PlaceViewItem>());
        mPlaceRecyclerView.setAdapter(mPlaceListAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });

        mPlaceRecyclerView.addOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mPlaceRecyclerView.getLayoutManager();

                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        mSwipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                } else
                {
                    if (mIsLoading == false)
                    {
                        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                        int itemCount = mLayoutManager.getItemCount();

                        int loadMorePosition = itemCount > LOAD_MORE_POSITION_GAP //
                            ? lastVisibleItemPosition + LOAD_MORE_POSITION_GAP //
                            : lastVisibleItemPosition + (itemCount / 3);

                        if (itemCount > 0)
                        {
                            if ((itemCount - 1) <= (loadMorePosition))
                            {
                                mIsLoading = true;
                                ((OnEventListener) mOnEventListener).onLoadMoreList();
                            }
                        }
                    }
                }

                ((OnEventListener) mOnEventListener).onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                ((OnEventListener) mOnEventListener).onScrollStateChanged(recyclerView, newState);
            }
        });

        mEmptyView = view.findViewById(R.id.emptyLayout);
        mFilterEmptyView = view.findViewById(R.id.filterEmptyLayout);

        View buttonView = mFilterEmptyView.findViewById(R.id.buttonView);
        buttonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onFilterClick();
            }
        });

        mMapLayout = (ViewGroup) view.findViewById(R.id.mapLayout);
        mPlaceRecyclerView.setShadowVisible(false);
        setBannerVisibility(true);
    }

    public void clearList()
    {
        if (mPlaceListAdapter != null)
        {
            mPlaceListAdapter.clear();
            mPlaceListAdapter.notifyDataSetChanged();
        }

        setScrollListTop();
    }

    public List<PlaceViewItem> getList()
    {
        if (mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getAll();
    }

    public int getItemCount()
    {
        if (mPlaceListAdapter == null)
        {
            return 0;
        }

        return mPlaceListAdapter.getItemCount();
    }

    public PlaceListMapFragment getListMapFragment()
    {
        return mPlaceListMapFragment;
    }

    public void setBannerVisibility(Boolean visibility)
    {
        mBannerVisibility = visibility;
    }

    public boolean isBannerVisibility()
    {
        return mBannerVisibility;
    }

    public void setBottomOptionLayout(View view)
    {
        mBottomOptionLayout = view;
    }

    public boolean canScrollUp()
    {
        if (mSwipeRefreshLayout != null)
        {
            return mSwipeRefreshLayout.canChildScrollUp();
        }

        return true;
    }

    public void setScrollListTop()
    {
        if (mPlaceRecyclerView != null)
        {
            mPlaceRecyclerView.scrollToPosition(0);
        }
    }

    public void setSwipeRefreshing(boolean refreshing)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public void addResultList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                              ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        mIsLoading = false;

        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (viewType == Constants.ViewType.LIST)
        {
            setVisibility(fragmentManager, viewType, true);

            // 리스트의 경우 Pagination 상황 고려
            List<PlaceViewItem> oldList = getList();

            int oldListSize = oldList == null ? 0 : oldList.size();
            if (oldListSize > 0)
            {
                PlaceViewItem placeViewItem = oldList.get(oldListSize - 1);

                // 기존 리스트가 존재 할 때 마지막 아이템이 footer 일 경우 아이템 제거
                switch (placeViewItem.mType)
                {
                    case PlaceViewItem.TYPE_FOOTER_VIEW:
                    case PlaceViewItem.TYPE_LOADING_VIEW:
                        getList().remove(placeViewItem); // 실제 삭제
                        oldList.remove(placeViewItem); // 비교 리스트 삭제
                        break;
                }
            }

            String districtName = null;

            // 지역순일때 상위 섹션명을 가지고 가기위한 처리
            if (Constants.SortType.DEFAULT == sortType)
            {
                // 삭제 이벤트가 발생하였을수 있어서 재 검사
                int start = oldList == null ? 0 : oldList.size() - 1;
                int end = oldList == null ? 0 : oldList.size() - 5;
                end = end < 0 ? 0 : end;

                // 5번안에 검사 안끝나면 그냥 종료, 원래는 1번에 검사되어야 함
                for (int i = start; i >= end; i--)
                {
                    PlaceViewItem item = oldList.get(i);
                    if (item.mType == PlaceViewItem.TYPE_ENTRY)
                    {
                        Place place = item.getItem();
                        districtName = place.districtName;
                        break;
                    } else if (item.mType == PlaceViewItem.TYPE_SECTION)
                    {
                        districtName = item.getItem();
                        break;
                    }
                }
            }

            if (list != null && list.size() > 0)
            {
                if (Util.isTextEmpty(districtName) == false)
                {
                    PlaceViewItem firstItem = list.get(0);
                    if (firstItem.mType == PlaceViewItem.TYPE_SECTION)
                    {
                        String firstDistrictName = firstItem.getItem();
                        if (districtName.equalsIgnoreCase(firstDistrictName))
                        {
                            list.remove(0);
                        }
                    }
                }

                mPlaceListAdapter.setSortType(sortType);
                mPlaceListAdapter.addAll(list);

                if (list.size() < Constants.PAGENATION_LIST_SIZE)
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                } else
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
                }
            } else
            {
                // 요청 온 데이터가 empty 일때 기존 리스트가 있으면 라스트 footer 재 생성
                if (oldListSize > 0)
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                }
            }

            int size = getItemCount();
            if (size == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);
            } else
            {
                // 배너의 경우 리스트 타입이면서, 기존 데이터가 0일때 즉 첫 페이지일때, sortType은 default type 이면서 배너가 있을때만 최상단에 위치한다.
                if (oldListSize == 0)
                {
                    ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);

                    if (sortType == Constants.SortType.DEFAULT && isBannerVisibility() == true)
                    {
                        PlaceViewItem placeViewItem = getEventBannerViewItem();
                        if (placeViewItem != null)
                        {
                            mPlaceListAdapter.add(0, placeViewItem);
                        }
                    }
                }

                mPlaceListAdapter.setSortType(sortType);
                mPlaceListAdapter.notifyDataSetChanged();
            }
        } else
        {

        }
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mPlaceListAdapter.getAll());
    }

    protected boolean hasSalesPlace(List<PlaceViewItem> list)
    {
        if (list == null || list.size() == 0)
        {
            return false;
        }

        boolean hasPlace = false;

        for (PlaceViewItem placeViewItem : list)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                && placeViewItem.<Place>getItem().isSoldOut == false)
            {
                hasPlace = true;
                break;
            }
        }

        return hasPlace;
    }

    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        mIsLoading = false;

        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        // 지도의 경우 무조건 전체 데이터를 가져옴으로 clear 후 진행되야 함
        if (viewType == Constants.ViewType.MAP)
        {
            clearList();

            if (list == null || list.size() == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);

            } else
            {
                setVisibility(fragmentManager, viewType, true);

                mPlaceListMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        PlaceListLayout.this.onInformationClick(placeViewItem);
                    }
                });

                mPlaceListMapFragment.setPlaceViewItemList(list, true);

                ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);
            }
        } else
        {
        }
    }

    public int getMapItemSize()
    {
        return mPlaceListMapFragment != null ? mPlaceListMapFragment.getPlaceViewItemListSize() : 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////         Listener         ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mPlaceRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(null);
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
            }
        }
    };

    protected View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Integer index = (Integer) view.getTag(view.getId());
            if (index != null)
            {
                EventBanner eventBanner = getEventBanner(index);

                ((OnEventListener) mOnEventListener).onEventBannerClick(eventBanner);
            }
        }
    };
}
