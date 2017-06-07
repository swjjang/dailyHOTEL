package com.daily.dailyhotel.screen.booking.detail.stayoutbound.refund;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundRefundDataBinding;

public class StayOutboundRefundView extends BaseDialogView<StayOutboundRefundView.OnEventListener, ActivityStayOutboundRefundDataBinding> implements StayOutboundRefundInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundRefundView(BaseActivity baseActivity, StayOutboundRefundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundRefundDataBinding viewDataBinding)
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
