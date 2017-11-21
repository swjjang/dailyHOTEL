package com.daily.dailyhotel.screen.home.stay.inbound.region;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayRegionListDataBinding;

public class StayRegionListView extends BaseDialogView<StayRegionListView.OnEventListener, ActivityStayRegionListDataBinding> implements StayRegionListInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayRegionListView(BaseActivity baseActivity, StayRegionListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayRegionListDataBinding viewDataBinding)
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

    private void initToolbar(ActivityStayRegionListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
