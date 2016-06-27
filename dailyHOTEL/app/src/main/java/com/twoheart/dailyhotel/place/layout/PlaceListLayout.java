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
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceListLayout extends BaseLayout
{
    protected PinnedSectionRecyclerView mPlaceRecyclerView;
    protected PlaceListAdapter mPlacetListAdapter;

    protected View mEmptyView;
    protected ViewGroup mMapLayout;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected boolean mScrollListTop;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onPlaceClick(PlaceViewItem placeViewItem);

        void onEventBannerClick(EventBanner eventBanner);

        void onRefreshAll(boolean isShowProgress);
    }

    protected abstract PlaceListAdapter getPlacetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList);

    protected abstract void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage);

    protected abstract void setList(FragmentManager fragmentManager, Constants.ViewType viewType, ArrayList<PlaceViewItem> list, Constants.SortType sortType);

    protected abstract boolean hasSalesPlace();

    protected abstract boolean isShowInformationAtMapView(Constants.ViewType viewType);

    public PlaceListLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        mPlaceRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);
        mPlaceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mPlaceRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mPlacetListAdapter = getPlacetListAdapter(mContext, new ArrayList<PlaceViewItem>());
        mPlaceRecyclerView.setAdapter(mPlacetListAdapter);

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

        // edgeglow을 보이게 하기 위해서
        mPlaceRecyclerView.addOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    View firstView = recyclerView.findChildViewUnder(recyclerView.getLeft() + 1, recyclerView.getTop() + 1);
                    int firstVisibleItem = recyclerView.getChildAdapterPosition(firstView);

                    if (firstVisibleItem == 0)
                    {
                        mSwipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                }


                if (dy < 0)
                {

                } else if (dy > 0)
                {

                }
            }
        });

        mEmptyView = view.findViewById(R.id.emptyLayout);
        mMapLayout = (ViewGroup) view.findViewById(R.id.mapLayout);
        mPlaceRecyclerView.setShadowVisible(false);
    }

    public boolean canScrollUp()
    {
        if (mSwipeRefreshLayout != null)
        {
            return mSwipeRefreshLayout.canChildScrollUp();
        }

        return true;
    }

    public void setScrollListTop(boolean scrollListTop)
    {
        mScrollListTop = scrollListTop;
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
                && placeViewItem.<Gourmet>getItem().isSoldOut == false)
            {
                hasPlace = true;
                break;
            }
        }

        return hasPlace;
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
