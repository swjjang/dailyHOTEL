package com.daily.dailyhotel.screen.mydaily.reward;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class RewardView extends BaseDialogView<RewardView.OnEventListener, ActivityCopyDataBinding> implements RewardInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public RewardView(BaseActivity baseActivity, RewardView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCopyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }
}
