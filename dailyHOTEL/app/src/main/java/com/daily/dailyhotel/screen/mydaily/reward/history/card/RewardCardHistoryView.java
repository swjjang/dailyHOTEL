package com.daily.dailyhotel.screen.mydaily.reward.history.card;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.RewardCardHistory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityRewardCardDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class RewardCardHistoryView extends BaseDialogView<RewardCardHistoryView.OnEventListener, ActivityRewardCardDataBinding> implements RewardCardHistoryInterface
{
    private RewardCardHistoryAdapter mRewardCardHistoryAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHomeClick();
    }

    public RewardCardHistoryView(BaseActivity baseActivity, RewardCardHistoryView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityRewardCardDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.homeImageView.setOnClickListener(v -> getEventListener().onHomeClick());
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

    @Override
    public void setRewardCardHistoryList(List<RewardCardHistory> rewardCardHistoryList)
    {
        if (getViewDataBinding() == null || rewardCardHistoryList == null || rewardCardHistoryList.size() == 0)
        {
            return;
        }

        if (mRewardCardHistoryAdapter == null)
        {
            mRewardCardHistoryAdapter = new RewardCardHistoryAdapter(getContext());

            getViewDataBinding().recyclerView.setAdapter(mRewardCardHistoryAdapter);
        }

        mRewardCardHistoryAdapter.setAll(rewardCardHistoryList);
        mRewardCardHistoryAdapter.notifyDataSetChanged();
    }

    private void initToolbar(ActivityRewardCardDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

}
