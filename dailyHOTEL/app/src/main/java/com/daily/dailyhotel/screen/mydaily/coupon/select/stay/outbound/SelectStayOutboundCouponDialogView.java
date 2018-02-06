package com.daily.dailyhotel.screen.mydaily.coupon.select.stay.outbound;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding;

public class SelectStayOutboundCouponDialogView extends BaseDialogView<SelectStayOutboundCouponDialogInterface.OnEventListener, ActivitySelectCouponDialogDataBinding> implements SelectStayOutboundCouponDialogInterface.ViewInterface
{


    public SelectStayOutboundCouponDialogView(BaseActivity baseActivity, SelectStayOutboundCouponDialogInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySelectCouponDialogDataBinding viewDataBinding)
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

    }

    private void initToolbar(ActivitySelectCouponDialogDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }
}
