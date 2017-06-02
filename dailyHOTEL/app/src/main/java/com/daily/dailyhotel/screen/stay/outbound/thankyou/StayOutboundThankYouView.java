package com.daily.dailyhotel.screen.stay.outbound.thankyou;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentThankYouDataBinding;

public class StayOutboundThankYouView extends BaseDialogView<StayOutboundThankYouView.OnEventListener, ActivityStayOutboundPaymentThankYouDataBinding> implements StayOutboundThankYouInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundThankYouView(BaseActivity baseActivity, StayOutboundThankYouView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPaymentThankYouDataBinding viewDataBinding)
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
