package com.daily.dailyhotel.screen.mydaily.coupon.select.stay.outbound;

import android.content.DialogInterface;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.entity.Coupon;
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding;

import java.util.List;

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

    @Override
    public void setVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dialogLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showCouponListDialog(String title, List<Coupon> couponList, View.OnClickListener positiveListener//
        , View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener)
    {

    }
}
