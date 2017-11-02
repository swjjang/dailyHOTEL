package com.daily.dailyhotel.screen.mydaily.coupon.history;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCouponHistoryDataBinding;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponHistoryView extends BaseDialogView<CouponHistoryView.OnEventListener, ActivityCouponHistoryDataBinding> implements CouponHistoryViewInterface
{
    private CouponHistoryListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHomeClick();
    }

    public CouponHistoryView(BaseActivity baseActivity, CouponHistoryView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCouponHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initListView(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityCouponHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.actionbar_title_coupon_history);
        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    private void initListView(ActivityCouponHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.listTopLine.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        viewDataBinding.couponHistoryRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.couponHistoryRecyclerView, getContext().getResources().getColor(R.color.default_over_scroll_edge));

        viewDataBinding.homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onHomeClick();
            }
        });
    }

    @Override
    public void setData(List<ObjectItem> list)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (list != null && list.size() != 0)
        {
            getViewDataBinding().listTopLine.setVisibility(View.VISIBLE);
            mListAdapter = new CouponHistoryListAdapter(getContext(), list);
            getViewDataBinding().emptyView.setVisibility(View.GONE);
        } else
        {
            mListAdapter = new CouponHistoryListAdapter(getContext(), new ArrayList<>());
            getViewDataBinding().emptyView.setVisibility(View.VISIBLE);

        }

        getViewDataBinding().couponHistoryRecyclerView.setAdapter(mListAdapter);
    }
}
