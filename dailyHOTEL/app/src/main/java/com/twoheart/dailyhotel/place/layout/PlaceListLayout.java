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
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;

public abstract class PlaceListLayout extends BaseLayout
{
    public static final int LOAD_MORE_POSITION_GAP = Constants.PAGENATION_LIST_SIZE / 3;

    protected PinnedSectionRecyclerView mPlaceRecyclerView;
    protected PlaceListAdapter mPlaceListAdapter;

    protected View mEmptyView;
    protected View mFilterEmptyView;
    protected ViewGroup mMapLayout;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mBottomOptionLayout;

    protected boolean mIsLoading;
    private boolean mBannerVisibility;

    protected LinearLayoutManager mLayoutManager;

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
    }

    protected abstract PlaceListAdapter getPlacetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList);

    protected abstract void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage);

    public abstract void setList(FragmentManager fragmentManager, Constants.ViewType viewType, ArrayList<PlaceViewItem> list, Constants.SortType sortType);

    protected abstract boolean hasSalesPlace();

    protected abstract boolean isShowInformationAtMapView(Constants.ViewType viewType);

    public abstract PlaceListMapFragment getListMapFragment();

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

        mPlaceListAdapter = getPlacetListAdapter(mContext, new ArrayList<PlaceViewItem>());
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

    public int getItemCount()
    {
        if (mPlaceListAdapter == null)
        {
            return 0;
        }

        return mPlaceListAdapter.getItemCount();
    }

    public void setBannerVisibility(Boolean visibility)
    {
        mBannerVisibility = visibility;
    }

    public boolean isBannerVisibility()
    {
        return mBannerVisibility;
    }

    public boolean canScrollUp()
    {
        if (mSwipeRefreshLayout != null)
        {
            return mSwipeRefreshLayout.canChildScrollUp();
        }

        return true;
    }

    public void setBottomOptionLayout(View view)
    {
        mBottomOptionLayout = view;
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
}
