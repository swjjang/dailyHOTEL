package com.daily.dailyhotel.screen.stay.outbound.payment;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class StayOutobundPaymentView extends BaseView<StayOutobundPaymentView.OnEventListener, ActivityCopyDataBinding> implements StayOutobundPaymentInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutobundPaymentView(BaseActivity baseActivity, StayOutobundPaymentView.OnEventListener listener)
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
