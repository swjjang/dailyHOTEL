package com.daily.dailyhotel.screen.booking.detail.stay;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayBookingDetailDataBinding;

public class StayBookingDetailView extends BaseDialogView<StayBookingDetailView.OnEventListener, ActivityStayBookingDetailDataBinding> implements StayBookingDetailInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayBookingDetailView(BaseActivity baseActivity, StayBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayBookingDetailDataBinding viewDataBinding)
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

    private void initToolbar(ActivityStayBookingDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
