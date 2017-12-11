package com.daily.dailyhotel.screen.home.stay.inbound.filter;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayFilterDataBinding;

public class StayFilterView extends BaseDialogView<StayFilterView.OnEventListener, ActivityStayFilterDataBinding> implements StayFilterInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayFilterView(BaseActivity baseActivity, StayFilterView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayFilterDataBinding viewDataBinding)
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

    private void initToolbar(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
