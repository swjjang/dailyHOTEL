package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
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

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSwipeRefreshing();

        void onMoreRefreshing();
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {
        viewDataBinding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        setSwipeRefreshing(true);
                    } else
                    {
                        setSwipeRefreshing(false);
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
}
