package com.daily.dailyhotel.screen.booking.detail.gourmet;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityGourmetBookingDetailDataBinding;

public class GourmetBookingDetailView extends BaseDialogView<GourmetBookingDetailView.OnEventListener, ActivityGourmetBookingDetailDataBinding> implements GourmetBookingDetailInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public GourmetBookingDetailView(BaseActivity baseActivity, GourmetBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetBookingDetailDataBinding viewDataBinding)
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

    private void initToolbar(ActivityGourmetBookingDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
