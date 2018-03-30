package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailyRecyclerStickyItemDecoration;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceListLayout extends BaseLayout
{
    public static final int LOAD_MORE_POSITION_GAP = Constants.PAGENATION_LIST_SIZE / 3;

    protected boolean mIsLoading;

    protected View mEmptyView;
    protected View mFilterEmptyView;
    protected View mBottomOptionLayout;

    protected ViewGroup mMapLayout;

    protected PlaceListMapFragment mPlaceListMapFragment;

    protected LinearLayoutManager mLayoutManager;

    protected PlaceListAdapter mListAdapter;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected RecyclerView mRecyclerView;

    protected enum ScreenType
    {
        NONE,
        EMPTY,
        FILTER_EMPTY,
        LIST,
        MAP
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void onPlaceClick(int position, View view, PlaceViewItem placeViewItem);

        void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem);

        //        void onEventBannerClick(EventBanner eventBanner);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onRefreshAll(boolean isShowProgress);

        void onLoadMoreList();

        void onFilterClick();

        void onUpdateViewTypeEnabled(boolean isEnabled);

        void onUpdateFilterEnabled(boolean isEnabled);

        void onBottomOptionVisible(boolean visible);

        void onShowActivityEmptyView(boolean isShow);

        void onRecordAnalytics(Constants.ViewType viewType);

        void onShowCallDialog();

        void onRegionClick();

        void onCalendarClick();

        void onWishClick(int position, PlaceViewItem placeViewItem);
    }

    protected abstract PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList);

    public abstract void notifyWishChanged(int position, boolean wish);

    public abstract void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, Constants.EmptyStatus emptyStatus, boolean isCurrentPage);

    //    protected abstract EventBanner getEventBanner(int index);
    //
    //    protected abstract PlaceViewItem getEventBannerViewItem();

    protected abstract void onInformationClick(View view, PlaceViewItem placeViewItem);

    protected abstract void initEmptyView(View view);

    public PlaceListLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mListAdapter = getPlaceListAdapter(mContext, new ArrayList<>());

        if (DailyPreference.getInstance(mContext).getTrueVRSupport() > 0)
        {
            mListAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(mContext) == true)
        {
            mListAdapter.setOnLongClickListener(mOnItemLongClickListener);
        }

        mListAdapter.setOnWishClickListener(mOnWishClickListener);

        if (mListAdapter instanceof DailyRecyclerStickyItemDecoration.StickyHeaderInterface)
        {
            DailyRecyclerStickyItemDecoration itemDecoration = new DailyRecyclerStickyItemDecoration(mRecyclerView, (DailyRecyclerStickyItemDecoration.StickyHeaderInterface) mListAdapter);
            mRecyclerView.addItemDecoration(itemDecoration);
        }

        mRecyclerView.setAdapter(mListAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });

        mRecyclerView.addOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

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
        initEmptyView(mEmptyView);

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

        mMapLayout = view.findViewById(R.id.mapLayout);
        //        setBannerVisibility(false);
    }

    public void clearList()
    {
        if (mListAdapter != null)
        {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
        }

        setScrollListTop();
    }

    public List<PlaceViewItem> getList()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getAll();
    }

    public PlaceViewItem getItem(int position)
    {
        if (mRecyclerView == null || mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getItem(position);
    }

    public int getItemCount()
    {
        if (mListAdapter == null)
        {
            return 0;
        }

        return mListAdapter.getItemCount();
    }

    public PlaceListMapFragment getListMapFragment()
    {
        return mPlaceListMapFragment;
    }

    //    public void setBannerVisibility(Boolean visibility)
    //    {
    //        mBannerVisibility = visibility;
    //    }
    //
    //    public boolean isBannerVisibility()
    //    {
    //        return mBannerVisibility;
    //    }

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
        if (mRecyclerView != null)
        {
            mRecyclerView.scrollToPosition(0);
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

    public boolean isRefreshing()
    {
        if (mSwipeRefreshLayout == null)
        {
            return false;
        }

        return mSwipeRefreshLayout.isRefreshing();
    }

    public void addResultList(FragmentManager fragmentManager, Constants.ViewType viewType//
        , ArrayList<PlaceViewItem> list, Constants.SortType sortType, PlaceBookingDay placeBookingDay, boolean rewardEnabled)
    {
        mIsLoading = false;

        if (mListAdapter == null || placeBookingDay == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (viewType == Constants.ViewType.LIST)
        {
            DailyRecyclerStickyItemDecoration itemDecoration = getItemDecoration(mRecyclerView);

            if (itemDecoration != null)
            {
                itemDecoration.setStickyEnabled(hasSectionList(list));
            }

            setVisibility(fragmentManager, viewType, Constants.EmptyStatus.NOT_EMPTY, true);

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
                if (DailyTextUtils.isTextEmpty(districtName) == false)
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

                mListAdapter.setPlaceBookingDay(placeBookingDay);
                mListAdapter.setRewardEnabled(rewardEnabled);
                mListAdapter.setSortType(sortType);
                mListAdapter.addAll(list);

                if (list.size() < Constants.PAGENATION_LIST_SIZE)
                {
                    mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                } else
                {
                    mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
                }
            } else
            {
                // 요청 온 데이터가 empty 일때 기존 리스트가 있으면 라스트 footer 재 생성
                if (oldListSize > 0)
                {
                    mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                }
            }

            int size = getItemCount();
            if (size == 0)
            {
                mListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, viewType, Constants.EmptyStatus.EMPTY, true);
            } else
            {
                // 배너의 경우 리스트 타입이면서, 기존 데이터가 0일때 즉 첫 페이지일때, sortType은 default type 이면서 배너가 있을때만 최상단에 위치한다.
                if (oldListSize == 0)
                {
                    ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);

                    //                    if (sortType == Constants.SortType.DEFAULT && isBannerVisibility() == true)
                    //                    {
                    //                        PlaceViewItem placeViewItem = getEventBannerViewItem();
                    //                        if (placeViewItem != null)
                    //                        {
                    //                            mPlaceListAdapter.add(0, placeViewItem);
                    //                        }
                    //                    }
                }

                mListAdapter.setSortType(sortType);
                mListAdapter.notifyDataSetChanged();
            }
        } else
        {

        }
    }

    private DailyRecyclerStickyItemDecoration getItemDecoration(RecyclerView recyclerView)
    {
        if (recyclerView == null)
        {
            return null;
        }

        int itemDecorationCount = recyclerView.getItemDecorationCount();

        if (itemDecorationCount > 0)
        {
            for (int i = 0; i < itemDecorationCount; i++)
            {
                RecyclerView.ItemDecoration itemDecoration = recyclerView.getItemDecorationAt(i);

                if (itemDecoration instanceof DailyRecyclerStickyItemDecoration)
                {
                    return (DailyRecyclerStickyItemDecoration) itemDecoration;
                }
            }
        }

        return null;
    }

    private boolean hasSectionList(List<PlaceViewItem> objectItemList)
    {
        return objectItemList != null && objectItemList.size() > 0 && objectItemList.get(0).mType == PlaceViewItem.TYPE_SECTION;
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mListAdapter.getAll());
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

    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, List<PlaceViewItem> list//
        , Constants.SortType sortType, PlaceBookingDay placeBookingDay, boolean rewardEnabled)
    {
        mIsLoading = false;

        if (mListAdapter == null)
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
                mListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, viewType, Constants.EmptyStatus.EMPTY, true);

            } else
            {
                setVisibility(fragmentManager, viewType, Constants.EmptyStatus.NOT_EMPTY, true);

                mPlaceListMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(View view, PlaceViewItem placeViewItem)
                    {
                        PlaceListLayout.this.onInformationClick(view, placeViewItem);
                    }
                });

                mPlaceListMapFragment.setPlaceViewItemList(placeBookingDay, list, true, rewardEnabled);

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

    public void setEmptyScreenVisible(boolean visible)
    {
        if (mEmptyView == null || mBottomOptionLayout == null)
        {
            return;
        }

        mEmptyView.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    public void setFilterEmptyScreenVisible(boolean visible)
    {
        if (mFilterEmptyView == null)
        {
            return;
        }

        mFilterEmptyView.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    public void setMapScreenVisible(boolean visible)
    {
        if (mMapLayout == null)
        {
            return;
        }

        mMapLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    public void setListScreenVisible(boolean visible)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        mSwipeRefreshLayout.setVisibility(visible == true ? View.VISIBLE : View.INVISIBLE);
    }

    public void setScreenVisible(ScreenType screenType)
    {
        if (screenType == null)
        {
            return;
        }

        switch (screenType)
        {
            case NONE:
                setEmptyScreenVisible(false);
                setFilterEmptyScreenVisible(false);
                setListScreenVisible(false);
                setMapScreenVisible(false);
                break;

            case EMPTY:
                setEmptyScreenVisible(true);
                setFilterEmptyScreenVisible(false);
                setListScreenVisible(false);
                setMapScreenVisible(false);
                break;

            case FILTER_EMPTY:
                setEmptyScreenVisible(false);
                setFilterEmptyScreenVisible(true);
                setListScreenVisible(false);
                setMapScreenVisible(false);
                break;

            case LIST:
                setEmptyScreenVisible(false);
                setFilterEmptyScreenVisible(false);
                setListScreenVisible(true);
                setMapScreenVisible(false);
                break;

            case MAP:
                setEmptyScreenVisible(false);
                setFilterEmptyScreenVisible(false);
                setListScreenVisible(false);
                setMapScreenVisible(true);
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////         Listener         ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(-1, null, null);
                return;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(position, view, placeViewItem);
            }
        }
    };

    protected View.OnLongClickListener mOnItemLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onPlaceLongClick(-1, null, null);
                return true;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceLongClick(position, view, placeViewItem);
            }

            return true;
        }
    };

    protected View.OnClickListener mOnWishClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (mRecyclerView == null)
            {
                return;
            }

            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onWishClick(position, placeViewItem);
            }
        }
    };
}
