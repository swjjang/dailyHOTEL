package com.daily.dailyhotel.screen.booking.receipt.stay;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayReceiptDataBinding;

public class StayReceiptView extends BaseDialogView<StayReceiptView.OnEventListener, ActivityStayReceiptDataBinding> implements StayReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayReceiptView(BaseActivity baseActivity, StayReceiptView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayReceiptDataBinding viewDataBinding)
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

    private void initToolbar(ActivityStayReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
