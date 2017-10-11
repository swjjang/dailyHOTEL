package com.daily.dailyhotel.screen.booking.cancel;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityBookingCancelListDataBinding;

public class BookingCancelListView extends BaseDialogView<BookingCancelListView.OnEventListener, ActivityBookingCancelListDataBinding> implements BookingCancelListInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public BookingCancelListView(BaseActivity baseActivity, BookingCancelListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityBookingCancelListDataBinding viewDataBinding)
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
