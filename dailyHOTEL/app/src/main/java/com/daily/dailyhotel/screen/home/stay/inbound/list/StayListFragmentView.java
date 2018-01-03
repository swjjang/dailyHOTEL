package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.screen.home.stay.inbound.list.map.StayMapFragment;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentStayListDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseFragmentDialogView<StayListFragmentView.OnEventListener, FragmentStayListDataBinding> implements StayListFragmentInterface
{
    private StayListFragmentAdapter mStayListFragmentAdapter;

    private StayMapFragment mStayMapFragment;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSwipeRefreshing();

        void onMoreRefreshing();

        void onStayClick(android.support.v4.util.Pair[] pairs, Stay stay, int listCount);

        void onStayLongClick(int position, android.support.v4.util.Pair[] pairs, Stay stay);

        void onViewPagerClose();

        // Map Event
        void onMapReady();

        void onMarkerClick(Stay stay);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();

        void onRetryClick();

        void onResearchClick();

        void onCallClick();

        void onFilterClick();

        void onRegionClick();

        void onCalendarClick();

        void onWishClick(int position, Stay stay);
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {
        viewDataBinding.swipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getEventListener().onSwipeRefreshing();
            }
        });

        viewDataBinding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (getViewDataBinding().swipeRefreshLayout.isRefreshing() == true)
                {
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        getViewDataBinding().swipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        getViewDataBinding().swipeRefreshLayout.setEnabled(false);
                    }
                } else
                {
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    int itemCount = linearLayoutManager.getItemCount();

                    if (lastVisibleItemPosition > itemCount * 2 / 3)
                    {
                        getEventListener().onMoreRefreshing();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));
    }

    @Override
    public void setList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mStayListFragmentAdapter == null)
        {
            mStayListFragmentAdapter = new StayListFragmentAdapter(getContext(), null);
            mStayListFragmentAdapter.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
                    if (position < 0)
                    {
                        return;
                    }

                    ObjectItem objectItem = mStayListFragmentAdapter.getItem(position);

                    if (objectItem.mType == objectItem.TYPE_ENTRY)
                    {
                        if (view instanceof DailyStayCardView == true)
                        {
                            getEventListener().onStayClick(((DailyStayCardView) view).getOptionsCompat(), objectItem.getItem(), mStayListFragmentAdapter.getItemCount());
                        } else
                        {

                        }
                    }
                }
            }, new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    return false;
                }
            });

            mStayListFragmentAdapter.setOnWishClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            getViewDataBinding().recyclerView.setAdapter(mStayListFragmentAdapter);
        }

        mStayListFragmentAdapter.setDistanceEnabled(isSortByDistance);
        mStayListFragmentAdapter.setNightsEnabled(isNights);
        mStayListFragmentAdapter.setRewardEnabled(rewardEnabled);
        mStayListFragmentAdapter.setTrueVREnabled(supportTrueVR);
        mStayListFragmentAdapter.setAll(objectItemList);
        mStayListFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void addList(List<ObjectItem> objectItemList, boolean isSortByDistance, boolean isNights, boolean rewardEnabled, boolean supportTrueVR)
    {
        if (getViewDataBinding() == null || objectItemList == null || objectItemList.size() == 0)
        {
            return;
        }

        if (mStayListFragmentAdapter == null)
        {
            mStayListFragmentAdapter = new StayListFragmentAdapter(getContext(), null);

            getViewDataBinding().recyclerView.setAdapter(mStayListFragmentAdapter);
        }

        // 항상 마지막 아이템은 Loading, Footer View 이다.
        int itemCount = mStayListFragmentAdapter.getItemCount();
        if (itemCount > 0)
        {
            mStayListFragmentAdapter.remove(itemCount - 1);
        }

        mStayListFragmentAdapter.setDistanceEnabled(isSortByDistance);
        mStayListFragmentAdapter.setNightsEnabled(isNights);
        mStayListFragmentAdapter.setRewardEnabled(rewardEnabled);
        mStayListFragmentAdapter.setTrueVREnabled(supportTrueVR);
        mStayListFragmentAdapter.addAll(objectItemList);
        mStayListFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSwipeRefreshing(boolean refreshing)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setEmptyViewVisible(boolean visible, boolean applyFilter)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (applyFilter == true)
        {
            getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_not_exist_filters), getString(R.string.message_changing_filter_option));
            getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_hotel_list_changing_filter), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onFilterClick();
                }
            });

            getViewDataBinding().emptyView.setButton02(false, null, null);
            getViewDataBinding().emptyView.setBottomMessage(false);
        } else
        {
            getViewDataBinding().emptyView.setMessageTextView(getString(R.string.message_stay_empty_message01), getString(R.string.message_stay_empty_message02));
            getViewDataBinding().emptyView.setButton01(true, getString(R.string.label_stay_category_change_region), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onRegionClick();
                }
            });

            getViewDataBinding().emptyView.setButton02(true, getString(R.string.label_stay_category_change_date), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onCalendarClick();
                }
            });
            getViewDataBinding().emptyView.setBottomMessage(false);
        }
    }
}
