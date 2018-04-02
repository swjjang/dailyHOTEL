package com.daily.dailyhotel.screen.copy.java;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class CopyView extends BaseDialogView<CopyInterface.OnEventListener, ActivityCopyDataBinding> implements CopyInterface.ViewInterface
{
    public CopyView(BaseActivity baseActivity, CopyInterface.OnEventListener listener)
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
