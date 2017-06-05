package com.daily.dailyhotel.screen.booking.detail.stayoutbound;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundBookingDetailDataBinding;

public class StayOutboundBookingDetailView extends BaseDialogView<StayOutboundBookingDetailView.OnEventListener, ActivityStayOutboundBookingDetailDataBinding> implements StayOutboundBookingDetailInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundBookingDetailView(BaseActivity baseActivity, StayOutboundBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundBookingDetailDataBinding viewDataBinding)
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
