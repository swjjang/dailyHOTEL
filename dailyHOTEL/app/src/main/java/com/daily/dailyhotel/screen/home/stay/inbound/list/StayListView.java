package com.daily.dailyhotel.screen.home.stay.inbound.list;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayListDataBinding;

public class StayListView extends BaseDialogView<StayListView.OnEventListener, ActivityStayListDataBinding> implements StayListInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayListView(BaseActivity baseActivity, StayListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayListDataBinding viewDataBinding)
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

    private void initToolbar(ActivityStayListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
