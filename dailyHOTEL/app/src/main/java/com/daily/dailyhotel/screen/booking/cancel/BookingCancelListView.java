package com.daily.dailyhotel.screen.booking.cancel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityBookingCancelListDataBinding;

public class BookingCancelListView extends BaseDialogView<BookingCancelListView.OnEventListener, ActivityBookingCancelListDataBinding> implements BookingCancelListInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefresh(boolean isShowProgress);
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

        getViewDataBinding().toolbarView.setTitleText(R.string.actionbar_title_booking_cancel_list_activity);
        getViewDataBinding().toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.bookingCancelSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.bookingCancelSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getEventListener().onRefresh(false);
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }
}
