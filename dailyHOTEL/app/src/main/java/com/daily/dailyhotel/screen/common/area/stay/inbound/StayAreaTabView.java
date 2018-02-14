package com.daily.dailyhotel.screen.common.area.stay.inbound;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class StayAreaTabView extends BaseDialogView<StayAreaTabInterface.OnEventListener, ActivityCopyDataBinding> implements StayAreaTabInterface.ViewInterface
{
    public StayAreaTabView(BaseActivity baseActivity, StayAreaTabInterface.OnEventListener listener)
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

        initToolbar(viewDataBinding);
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

    private void initToolbar(ActivityCopyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
