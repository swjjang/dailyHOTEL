package com.daily.dailyhotel.screen.mydaily.reward.history;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.databinding.ActivityRewardHistoryDataBinding;

import java.util.List;

public class RewardHistoryView extends BaseDialogView<RewardHistoryView.OnEventListener, ActivityRewardHistoryDataBinding> implements RewardHistoryInterface
{
    private RewardHistoryAdapter mRewardHistoryAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public RewardHistoryView(BaseActivity baseActivity, RewardHistoryView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityRewardHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    private void initToolbar(ActivityRewardHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void setRewardHistoryData(List<ObjectItem> list)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRewardHistoryAdapter == null)
        {
            mRewardHistoryAdapter = new RewardHistoryAdapter(getContext());
        }

        mRewardHistoryAdapter.setAll(list);
        getViewDataBinding().recyclerView.setAdapter(mRewardHistoryAdapter);
    }
}
