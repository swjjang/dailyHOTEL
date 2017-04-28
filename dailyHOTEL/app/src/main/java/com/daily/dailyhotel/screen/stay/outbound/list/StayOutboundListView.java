package com.daily.dailyhotel.screen.stay.outbound.list;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchResultDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundListView extends BaseView<StayOutboundListView.OnEventListener, ActivityStayOutboundSearchResultDataBinding> implements StayOutboundListViewInterface
{
    private DailyToolbarLayout mDailyToolbarLayout;
    private StayOutboundListAdapter mStayOutboundListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundListView(BaseActivity baseActivity, StayOutboundListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }

    @Override
    public void setStayOutboundList(List<ListItem> listItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (listItemList.size() == 0)
        {
            getViewDataBinding().emptyLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().resultLayout.setVisibility(View.GONE);
            return;
        }

        if (mStayOutboundListAdapter == null)
        {
            mStayOutboundListAdapter = new StayOutboundListAdapter(getContext(), null);
            getViewDataBinding().recyclerView.setAdapter(mStayOutboundListAdapter);
        }

        getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        getViewDataBinding().resultLayout.setVisibility(View.VISIBLE);

        mStayOutboundListAdapter.setAll(listItemList);
        mStayOutboundListAdapter.notifyDataSetChanged();
    }

    @Override
    public void addStayOutboundList(List<ListItem> listItemList)
    {

    }

    private void initToolbar(ActivityStayOutboundSearchResultDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        mDailyToolbarLayout.initToolbar(null//
            , v -> getEventListener().finish());
    }
}
